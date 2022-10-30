/*
 * Created on Apr 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.status.IAgentCountBean;
import com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticSet;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;

import java.util.Calendar;
import java.util.List;

/**
 * ServerStatisticBeanImpl is a concrete implementation of
 * IServerStatisticsBean. It uses the Statistic API to retrieve statistic about
 * the Destiny servers
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/ServerStatisticsBeanImpl.java#2 $
 */
public class ServerStatisticsBeanImpl implements IServerStatisticsBean {

    /*
     * The statistic pull delay - 3 minutes
     */
    private static final Long PULL_STAT_DELAY = 3 * 60 * 1000L;

    private List<IAgentCountBean> agentCountBeans;
    private Long numPolicies;
    private Long numDisconnectedAgentsInLastDay;
    private Long numAgentsWithOutOfDatePolicies;
    private Long numHeartbeatsInLastDay;
    private Long numPolicyActivityLogEntriesInLastDay;
    private Long numTrackingActivityLogEntriesInLastDay;
    private Calendar lastUpdatedTimestamp;

    /**
     * Create an instance of ServerStatisticsBeanImpl
     */
    public ServerStatisticsBeanImpl() {
        super();

        this.lastUpdatedTimestamp = Calendar.getInstance();
        this.lastUpdatedTimestamp.setTimeInMillis(0); // Start with last update time
        // at 1970

        /*
         * Below, we instantiate specific statistics providers. In the future,
         * which providers are used could be specified in a config file
         * someplace, making the server statistics displayed completely
         * configurable
         * 
         * Also note that we collect stats in the constructor only. This is to
         * ensure that the timestamp is as accurate as possible. This mean that
         * this bean must be request scope
         */
        IAgentStatisticsProvider agentStatProvider = getAgentStatisticsProvider();
        collectAgentStatistics(agentStatProvider);

        ILogCountStatisticsProvider logCountStatProvider = getLogCountStatisticsProvider();
        collectLogCountStatistics(logCountStatProvider);
        
        IPolicyStatisticsProvider policyStatProvider = getPolicyStatisticsProvider();
        collectPolicyStatistics(policyStatProvider);
    }

    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean#getNumAgents()
     */
    public List<IAgentCountBean> getNumAgents() {
        return this.agentCountBeans;
    }


    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean#getTotalNumAgents()
     */
    public long getTotalNumAgents() {
        long count = 0;
        for (IAgentCountBean nextAgentCount: this.agentCountBeans) {
            count += nextAgentCount.getNumRegistered();
        }

        return count;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean#getNumPolicies()
     */
    public long getNumPolicies() {
        return this.numPolicies.longValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean#getNumDisconnectedAgentsInLastDay()
     */
    public long getNumDisconnectedAgentsInLastDay() {
        return this.numDisconnectedAgentsInLastDay.longValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean#getNumAgentsWithOutOfDatePolicies()
     */
    public long getNumAgentsWithOutOfDatePolicies() {
        return this.numAgentsWithOutOfDatePolicies.longValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean#getNumHeartbeatsInLastDay()
     */
    public long getNumHeartbeatsInLastDay() {
        return this.numHeartbeatsInLastDay.longValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean#getNumPolicyActivityLogEntriesInLastDay()
     */
    public long getNumPolicyActivityLogEntriesInLastDay() {
        return this.numPolicyActivityLogEntriesInLastDay.longValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean#getNumTrackingActivityLogEntriesInLastDay()
     */
    public long getNumTrackingActivityLogEntriesInLastDay() {
        return this.numTrackingActivityLogEntriesInLastDay.longValue();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean#getTotalActivityLogEntriesInLastDay()
     */
    public long getTotalActivityLogEntriesInLastDay() {
        return getNumTrackingActivityLogEntriesInLastDay() + getNumPolicyActivityLogEntriesInLastDay();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IServerStatisticsBean#getLastUpdatedTimestamp()
     */
    public Calendar getLastUpdatedTimestamp() {
        return this.lastUpdatedTimestamp;
    }

    /**
     * Retrieve the log count statistic provider
     * 
     * @return the log count statistic provider
     */
    private ILogCountStatisticsProvider getLogCountStatisticsProvider() {
        ILogCountStatisticsProvider statProviderToReturn = null;
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        String logCountStatProviderCompName = ILogCountStatisticsProvider.class.getName();
        if (componentManager.isComponentRegistered(logCountStatProviderCompName)) {
            statProviderToReturn = (ILogCountStatisticsProvider) componentManager.getComponent(logCountStatProviderCompName);
        } else {
            HashMapConfiguration logCountStatProviderConfig = new HashMapConfiguration();
            logCountStatProviderConfig.setProperty(LogCountStatisticsProvider.PULL_DELAY_PROPERTY_NAME, PULL_STAT_DELAY);
            logCountStatProviderConfig.setProperty(LogCountStatisticsProvider.THREAD_NAME, "LogCountStatisticsTimer");
            ComponentInfo<ILogCountStatisticsProvider> logCountStatProviderCompInfo = 
                new ComponentInfo<ILogCountStatisticsProvider>(
                    logCountStatProviderCompName, 
                    LogCountStatisticsProvider.class, 
                    ILogCountStatisticsProvider.class, 
                    LifestyleType.SINGLETON_TYPE, 
                    logCountStatProviderConfig);
            statProviderToReturn = componentManager.getComponent(logCountStatProviderCompInfo);
        }

        return statProviderToReturn;
    }

    /**
     * Retrieve the Agent Statistic Provider
     * 
     * @return the Agent Statistic Provider
     */
    private IAgentStatisticsProvider getAgentStatisticsProvider() {
        IAgentStatisticsProvider statProviderToReturn = null;
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();

        // Instantiate Agent Statistic Provider
        String agentStatProviderCompName = IAgentStatisticsProvider.class.getName();
        if (componentManager.isComponentRegistered(agentStatProviderCompName)) {
            statProviderToReturn = (IAgentStatisticsProvider) componentManager.getComponent(agentStatProviderCompName);
        } else {
            HashMapConfiguration agentStatProviderConfig = new HashMapConfiguration();
            agentStatProviderConfig.setProperty(AgentStatisticsProvider.PULL_DELAY_PROPERTY_NAME, PULL_STAT_DELAY);
            agentStatProviderConfig.setProperty(AgentStatisticsProvider.THREAD_NAME, "AgentStatisticsTimer");
            ComponentInfo<IAgentStatisticsProvider> agentStatProviderCompInfo = 
                new ComponentInfo<IAgentStatisticsProvider>(
                    agentStatProviderCompName, 
                    AgentStatisticsProvider.class, 
                    IAgentStatisticsProvider.class, 
                    LifestyleType.SINGLETON_TYPE, 
                    agentStatProviderConfig);
            statProviderToReturn = componentManager.getComponent(agentStatProviderCompInfo);
        }

        return statProviderToReturn;
    }

    /**
     * Retrieve the Policy Statistic Provider
     * 
     * @return the Policy Statistic Provider
     */
    private IPolicyStatisticsProvider getPolicyStatisticsProvider() {
        IPolicyStatisticsProvider statProviderToReturn = null;
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();

        // Instantiate Policy Statistic Provider
        String policyStatProviderCompName = IPolicyStatisticsProvider.class.getName();
        if (componentManager.isComponentRegistered(policyStatProviderCompName)) {
            statProviderToReturn = (IPolicyStatisticsProvider) componentManager.getComponent(policyStatProviderCompName);
        } else {
            HashMapConfiguration policyStatProviderConfig = new HashMapConfiguration();
            policyStatProviderConfig.setProperty(PolicyStatisticsProvider.PULL_DELAY_PROPERTY_NAME, PULL_STAT_DELAY);
            policyStatProviderConfig.setProperty(PolicyStatisticsProvider.THREAD_NAME, "PolicyStatisticsTimer");
            ComponentInfo<IPolicyStatisticsProvider> policyStatProviderCompInfo = 
                new ComponentInfo<IPolicyStatisticsProvider>(
                    policyStatProviderCompName, 
                    PolicyStatisticsProvider.class, 
                    IPolicyStatisticsProvider.class, 
                    LifestyleType.SINGLETON_TYPE, 
                    policyStatProviderConfig);
            statProviderToReturn = componentManager.getComponent(policyStatProviderCompInfo);
        }

        return statProviderToReturn;
    }
    
    /**
     * Collect the Agent Statistics
     * 
     * @param agentStatProvider
     *            the agent static provider instance
     */
    private void collectAgentStatistics(IAgentStatisticsProvider agentStatProvider) {
        IStatisticSet agentStatistics = agentStatProvider.getStatistics();

        IStatistic agentCounts = agentStatistics.getStatistic(AgentStatisticsProvider.AGENT_COUNT_STAT_KEY);
        this.agentCountBeans = (List<IAgentCountBean>) agentCounts.getValue();
        Calendar timestamp = agentCounts.getLastUpdatedTimestamp();
        if (timestamp.after(this.lastUpdatedTimestamp)) {
            this.lastUpdatedTimestamp = timestamp;
        }

        IStatistic numDisconnectdAgentsInLastDayStat = agentStatistics.getStatistic(AgentStatisticsProvider.AGENTS_NOT_CONNECTED_IN_LAST_DAY_COUNT_STAT_KEY);
        this.numDisconnectedAgentsInLastDay = (Long) numDisconnectdAgentsInLastDayStat.getValue();
        timestamp = numDisconnectdAgentsInLastDayStat.getLastUpdatedTimestamp();
        if (timestamp.after(this.lastUpdatedTimestamp)) {
            this.lastUpdatedTimestamp = timestamp;
        }

        IStatistic numHeartbeatsInLastDayStat = agentStatistics.getStatistic(AgentStatisticsProvider.HEARTBEATS_IN_LAST_DAY_COUNT_STAT_KEY);
        this.numHeartbeatsInLastDay = (Long) numHeartbeatsInLastDayStat.getValue();
        timestamp = numHeartbeatsInLastDayStat.getLastUpdatedTimestamp();
        if (timestamp.after(this.lastUpdatedTimestamp)) {
            this.lastUpdatedTimestamp = timestamp;
        }

        IStatistic numAgentsWithOutOfDatePoliciesStat = agentStatistics.getStatistic(AgentStatisticsProvider.AGENTS_WITH_OUT_OF_DATE_POLICIES_COUNT_STAT_KEY);
        this.numAgentsWithOutOfDatePolicies = (Long) numAgentsWithOutOfDatePoliciesStat.getValue();
        timestamp = numAgentsWithOutOfDatePoliciesStat.getLastUpdatedTimestamp();
        if (timestamp.after(this.lastUpdatedTimestamp)) {
            this.lastUpdatedTimestamp = timestamp;
        }
    }

    /**
     * Collect the log statistics
     * 
     * @param logCountStatProvider
     *            the log statistic provider
     */
    private void collectLogCountStatistics(ILogCountStatisticsProvider logCountStatProvider) {
        IStatisticSet logCountStatistics = logCountStatProvider.getStatistics();

        IStatistic numPolicyLogEntriesInLastDay = logCountStatistics.getStatistic(LogCountStatisticsProvider.POLICY_ACTIVITY_LOG_COUNT_STAT_KEY);
        this.numPolicyActivityLogEntriesInLastDay = (Long) numPolicyLogEntriesInLastDay.getValue();
        Calendar timestamp = numPolicyLogEntriesInLastDay.getLastUpdatedTimestamp();
        if (timestamp.after(this.lastUpdatedTimestamp)) {
            this.lastUpdatedTimestamp = timestamp;
        }

        IStatistic numTrackingLogEntriesInLastDayStat = logCountStatistics.getStatistic(LogCountStatisticsProvider.TRACKING_ACTIVITY_LOG_COUNT_STAT_KEY);
        this.numTrackingActivityLogEntriesInLastDay = (Long) numTrackingLogEntriesInLastDayStat.getValue();
        timestamp = numTrackingLogEntriesInLastDayStat.getLastUpdatedTimestamp();
        if (timestamp.after(this.lastUpdatedTimestamp)) {
            this.lastUpdatedTimestamp = timestamp;
        }
    }
    
    /**
     * Collect the policy statistics
     * 
     * @param policyStatProvider
     *            the policy statistic provider
     */
    private void collectPolicyStatistics(IPolicyStatisticsProvider policyStatProvider) {
        IStatisticSet policyStatistics = policyStatProvider.getStatistics();

        IStatistic policyCount = policyStatistics.getStatistic(IPolicyStatisticsProvider.POLICY_COUNT_STAT_KEY);
        this.numPolicies = (Long) policyCount.getValue();
        Calendar timestamp = policyCount.getLastUpdatedTimestamp();
        if (timestamp.after(this.lastUpdatedTimestamp)) {
            this.lastUpdatedTimestamp = timestamp;
        }
    }
}
