/*
 * Created on April 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;

import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.IManagerEnabled;
import com.bluejungle.framework.comp.IStartable;
import com.bluejungle.framework.comp.PropertyKey;

/**
 * TimedPullStatisticProvider is an abstract implementation of
 * IStatisticProvider, which contains functionality for measuring a static at
 * reguarly timed intervals. The purpose, is to increase the response time of
 * the application and reduce load on the server. Concrete implementations can
 * extend this class and override the <code>pullStatistic()</code> to provide
 * the specific statistic measuring functionality. The actual timed retrieval of
 * the statistic will be using a call to the <code>start()</code> method
 * 
 * @author sgoldstein
 */
public abstract class TimedPullStatisticProvider implements IStatisticsProvider, IConfigurable, IInitializable, IStartable, IManagerEnabled, ILogEnabled {

    /**
     * <code>PULL_DELAY_PROPERTY_NAME</code> is the name of the configuration
     * property which specified, in milliseconds, the interval between statistic
     * measurements
     */
    public static final PropertyKey<Long> PULL_DELAY_PROPERTY_NAME = new PropertyKey<Long>("PullDelay");
    
    public static final PropertyKey<String> THREAD_NAME = new PropertyKey<String>("threadName");
    
    /**
     * <code>DEFAULT_PULL_DELAY</code> value of the pull delay property
     */
    public static final long DEFAULT_PULL_DELAY = 3 * 60 * 1000L;

    private long pullDelay;
    private Timer timer;
    private IStatisticSet currentStat;
    private IConfiguration config;
    private IComponentManager componentManager;
    private Log log;
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticsProvider#getStatistics()
     */
    public IStatisticSet getStatistics() {
        return this.currentStat;
    }

    /**
     * Called by the TimedPullStatisticProvider to execute the statistic
     * measurement. This method will be called at timed intervals, based on the
     * provided configuration
     * 
     * @return the mesaured statistic
     */
    public abstract IStatisticSet pullStatistic();

    /**
     * @see com.bluejungle.framework.comp.IInitializable#init()
     */
    public void init() {
        String threadName = null;
        long delay = DEFAULT_PULL_DELAY;
        IConfiguration config = getConfiguration();
        if (config != null) {
            threadName = config.get(THREAD_NAME);
            delay = config.get(PULL_DELAY_PROPERTY_NAME, DEFAULT_PULL_DELAY);
        }
        
        if (threadName == null) {
            threadName = this.getClass().getSimpleName() + "TimerThread";
        }
        
        timer = new Timer(threadName);
        pullDelay = delay;
    }

    /**
     * @see com.bluejungle.framework.comp.IConfigurable#getConfiguration()
     */
    public IConfiguration getConfiguration() {
        return this.config;
    }

    /**
     * @see com.bluejungle.framework.comp.IConfigurable#setConfiguration(com.bluejungle.framework.comp.IConfiguration)
     */
    public void setConfiguration(IConfiguration config) {
        this.config = config;
    }

    /**
     * Call to start the timed measurements of statistics. This method begins by
     * calling the pullStatistic() method to get an initial measurement.
     * Additional measurements are than taken at constant intervals specified
     * through a configuration property
     */
    public void start() {
        this.currentStat = pullStatistic();
        timer.schedule(new PullTask(), pullDelay, pullDelay);
    }

    /**
     * @see com.bluejungle.framework.comp.IStartable#stop()
     */
    public void stop() {
        if (getLog() != null && getLog().isDebugEnabled()) {;
            getLog().debug("Stopping Timer " + this);
        }
        timer.cancel();
    }
    
    /**
     * @see com.bluejungle.framework.comp.IManagerEnabled#getManager()
     */
    public IComponentManager getManager() {
        return this.componentManager;
    }
    /**
     * @see com.bluejungle.framework.comp.IManagerEnabled#setManager(com.bluejungle.framework.comp.IComponentManager)
     */
    public void setManager(IComponentManager manager) {
        this.componentManager = manager;
    }
    
    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#setLog(org.apache.commons.logging.Log)
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#getLog()
     */
    public Log getLog() {
        return this.log;
    }
    
    private class PullTask extends TimerTask {

        /**
         * @see java.util.TimerTask#run()
         */
        public void run() {
            try {
                IStatisticSet pulledStatistic = pullStatistic();
                TimedPullStatisticProvider.this.currentStat = pulledStatistic;
            } catch (RuntimeException ex) {
                if (getLog() != null && getLog().isErrorEnabled()) {
                    getLog().error(
                       "RuntimeException thrown while pulling statistics:", ex);
                }
            }
        }
    }
    
}

