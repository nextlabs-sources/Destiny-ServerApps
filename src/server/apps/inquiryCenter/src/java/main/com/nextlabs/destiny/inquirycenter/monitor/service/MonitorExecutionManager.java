/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.IStartable;
import com.bluejungle.framework.comp.LifestyleType;

/**
 * @author nnallagatla
 *
 */
public class MonitorExecutionManager implements ILogEnabled, IInitializable, IConfigurable, IStartable, IHasComponentInfo<MonitorExecutionManager> {
	
    private ScheduledExecutorService executorService;
    private IConfiguration configuration;
    
    public static final String MONITOR_EXECUTION_INTERVAL = "monitorExecutionInterval";
    private static final int DELAY = 5;
    private int delayInMinutes = DELAY;
    private MonitorExecutorEngine engine = null;
    private Log log;
    private static final Log LOG = LogFactory.getLog(MonitorExecutionManager.class);
    
    private static final ComponentInfo<MonitorExecutionManager> COMP_INFO = 
            new ComponentInfo<MonitorExecutionManager>(
                MonitorExecutionManager.class.getName(), 
                MonitorExecutionManager.class,
                LifestyleType.SINGLETON_TYPE);
    
	@Override
	public void start() {
		LOG.info("entering START");
		execute();
		LOG.info("exiting START");
	}

	private void execute(){
		
		LOG.info("Starting Monitor execution Engine with " + delayInMinutes + " minutes interval");
		executorService.schedule(new Runnable(){
			@Override
			public void run() {
				engine.runMonitors();
				execute();
			}
		}, delayInMinutes, TimeUnit.MINUTES);
	}
	
	@Override
	public void stop() {
		executorService.shutdownNow();
		if ( engine != null)
		{
			engine.shutdown();
		}
	}

	@Override
	public IConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void setConfiguration(IConfiguration arg0) {
		configuration = arg0;
	}

	@Override
	public void init() {
		LOG.info("entering INIT");
		
		try
		{
			if (configuration != null) {
				LOG.info(configuration.get(MONITOR_EXECUTION_INTERVAL));
				String interval = (String) configuration.get(MONITOR_EXECUTION_INTERVAL);
				delayInMinutes = Integer.parseInt(interval);
				
				if (delayInMinutes <= 0)
				{
					delayInMinutes = DELAY;
					LOG.warn(MONITOR_EXECUTION_INTERVAL + " should be greater than 0");
				}
			}
		}
		catch (Exception e)
		{
			LOG.error(MONITOR_EXECUTION_INTERVAL + " is configured with NaN");
		}
		engine = new MonitorExecutorEngine();
        executorService = Executors.newScheduledThreadPool(1);
		LOG.info("exiting INIT");
    }

	@Override
	public Log getLog() {
	    return log;
	}

	@Override
	public void setLog(Log arg0) {
		log = arg0;
	}

	@Override
	public ComponentInfo<MonitorExecutionManager> getComponentInfo() {
		return COMP_INFO;
	}

	public MonitorExecutionManager()
	{
		LOG.info("Instantiating MonitorExecutionManager");
	}
}
