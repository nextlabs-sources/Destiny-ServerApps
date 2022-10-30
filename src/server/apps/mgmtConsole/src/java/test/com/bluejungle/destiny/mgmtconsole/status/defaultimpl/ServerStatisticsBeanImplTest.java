/*
 * Created on Apr 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluejungle.destiny.mgmtconsole.status.IAgentCountBean;
import com.bluejungle.destiny.mgmtconsole.status.IAgentTypeBean;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticSet;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.SimpleStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.SimpleStatisticSet;
import com.bluejungle.domain.agenttype.AgentTypeEnumType;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/ServerStatisticsBeanImplTest.java#1 $
 */

public class ServerStatisticsBeanImplTest extends BaseDestinyTestCase {

    private static final long TEST_NUM_FILE_SERVER_AGENTS = 1l;
    private static final long TEST_NUM_DESKTOP_SERVER_AGENTS = 5l;
    private static final long TEST_NUM_POLICIES = 15l;
    private static final long TEST_NUM_DISCONNECTED_AGENTS = 9l;
    private static final long TEST_NUM_AGENTS_WITH_OUT_OF_DATE_POLICIES = 7l;
    private static final long TEST_NUM_HEARTBEATS = 4l;
    private static final long TEST_NUM_TRACKING_LOGS = 22l;
    private static final long TEST_NUM_POLICY_LOGS = 10001l;
    private static final Calendar TEST_TIMESTAMP = Calendar.getInstance();

    /*
     * For convenience, use one stat set for all stats
     */
    private static final SimpleStatisticSet STAT_SET = new SimpleStatisticSet();
    static {
        IStatistic nextStatToAdd = new SimpleStatistic(new Long(TEST_NUM_DISCONNECTED_AGENTS), TEST_TIMESTAMP);
        STAT_SET.setStatistic(IAgentStatisticsProvider.AGENTS_NOT_CONNECTED_IN_LAST_DAY_COUNT_STAT_KEY, nextStatToAdd);

        List<IAgentCountBean> agentCounts = new ArrayList<IAgentCountBean>(2);
        IAgentTypeBean desktopAgentType = new AgentTypeBeanImpl(AgentTypeEnumType.DESKTOP.getName(), "Desktop");
        agentCounts.add(new AgentCountBeanImpl(desktopAgentType, new Long(TEST_NUM_DESKTOP_SERVER_AGENTS)));
        IAgentTypeBean fileServerAgentType = new AgentTypeBeanImpl(AgentTypeEnumType.FILE_SERVER.getName(), "File Server");
        agentCounts.add(new AgentCountBeanImpl(fileServerAgentType, new Long(TEST_NUM_FILE_SERVER_AGENTS)));

        Calendar earlierTimestamp = Calendar.getInstance();
        earlierTimestamp.add(Calendar.MONTH, -5);
        nextStatToAdd = new SimpleStatistic(new Long(TEST_NUM_FILE_SERVER_AGENTS), earlierTimestamp);
        nextStatToAdd = new SimpleStatistic(agentCounts, TEST_TIMESTAMP);
        STAT_SET.setStatistic(IAgentStatisticsProvider.AGENT_COUNT_STAT_KEY, nextStatToAdd);

        nextStatToAdd = new SimpleStatistic(new Long(TEST_NUM_AGENTS_WITH_OUT_OF_DATE_POLICIES), TEST_TIMESTAMP);
        STAT_SET.setStatistic(IAgentStatisticsProvider.AGENTS_WITH_OUT_OF_DATE_POLICIES_COUNT_STAT_KEY, nextStatToAdd);

        nextStatToAdd = new SimpleStatistic(new Long(TEST_NUM_HEARTBEATS), TEST_TIMESTAMP);
        STAT_SET.setStatistic(IAgentStatisticsProvider.HEARTBEATS_IN_LAST_DAY_COUNT_STAT_KEY, nextStatToAdd);

        nextStatToAdd = new SimpleStatistic(new Long(TEST_NUM_POLICIES), TEST_TIMESTAMP);
        STAT_SET.setStatistic(IPolicyStatisticsProvider.POLICY_COUNT_STAT_KEY, nextStatToAdd);

        nextStatToAdd = new SimpleStatistic(new Long(TEST_NUM_POLICY_LOGS), earlierTimestamp);
        STAT_SET.setStatistic(ILogCountStatisticsProvider.POLICY_ACTIVITY_LOG_COUNT_STAT_KEY, nextStatToAdd);

        nextStatToAdd = new SimpleStatistic(new Long(TEST_NUM_TRACKING_LOGS), TEST_TIMESTAMP);
        STAT_SET.setStatistic(ILogCountStatisticsProvider.TRACKING_ACTIVITY_LOG_COUNT_STAT_KEY, nextStatToAdd);
    }

    private ServerStatisticsBeanImpl beanToTest;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ServerStatisticsBeanImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        setupStatProviders();

        this.beanToTest = new ServerStatisticsBeanImpl();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for ServerStatisticsBeanImplTest.
     * 
     * @param arg0
     */
    public ServerStatisticsBeanImplTest(String arg0) {
        super(arg0);
    }

    public void testGetNumAgents() {
        List<IAgentCountBean> numAgents = this.beanToTest.getNumAgents();
        for (IAgentCountBean nextAgentCount : numAgents) {
            if (nextAgentCount.getAgentType().getAgentTypeId().equals(AgentTypeEnumType.FILE_SERVER)) {
                assertEquals("testGetNumAgents - Ensure value retrieve is equals to that expected for file server agents", TEST_NUM_FILE_SERVER_AGENTS, nextAgentCount.getNumRegistered().longValue());
            } else if (nextAgentCount.getAgentType().getAgentTypeId().equals(AgentTypeEnumType.DESKTOP)) {
                assertEquals("testGetNumAgents - Ensure value retrieve is equals to that expected for desktop agents", TEST_NUM_DESKTOP_SERVER_AGENTS, nextAgentCount.getNumRegistered().longValue());
            }
        }
    }

    public void testGetTotalNumAgents() {
        assertEquals("testGetTotalNumAgents - Ensure value retrieve is equals to that expected", TEST_NUM_DESKTOP_SERVER_AGENTS + TEST_NUM_FILE_SERVER_AGENTS, this.beanToTest.getTotalNumAgents());
    }

    public void testGetNumPolicies() {
        assertEquals("testGetNumPolicies - Ensure value retrieve is equals to that expected", TEST_NUM_POLICIES, this.beanToTest.getNumPolicies());
    }

    public void testGetNumDisconnectedAgentsInLastDay() {
        assertEquals("testGetNumDisconnectedAgentInLastDay- Ensure value retrieve is equals to that expected", TEST_NUM_DISCONNECTED_AGENTS, this.beanToTest.getNumDisconnectedAgentsInLastDay());
    }

    public void testGetNumAgentsWithOutOfDatePolicies() {
        assertEquals("testGetNumAgentsWithOutOfDatePolicies - Ensure value retrieve is equals to that expected", TEST_NUM_AGENTS_WITH_OUT_OF_DATE_POLICIES, this.beanToTest.getNumAgentsWithOutOfDatePolicies());
    }

    public void testGetNumHeartbeatsInLastDay() {
        assertEquals("testGetNumHeartbeatsInLastDay - Ensure value retrieve is equals to that expected", TEST_NUM_HEARTBEATS, this.beanToTest.getNumHeartbeatsInLastDay());
    }

    public void testGetNumPolicyActivityLogEntriesInLastDay() {
        assertEquals("testGetNumPolicyLogEntriedInLastDay - Ensure value retrieve is equals to that expected", TEST_NUM_POLICY_LOGS, this.beanToTest.getNumPolicyActivityLogEntriesInLastDay());
    }

    public void testGetNumTrackingActivityLogEntriesInLastDay() {
        assertEquals("testGetNumTrackingLogEntriedInLastDay - Ensure value retrieve is equals to that expected", TEST_NUM_TRACKING_LOGS, this.beanToTest.getNumTrackingActivityLogEntriesInLastDay());
    }

    public void testGetTotalActivityLogEntriesInLastDay() {
        assertEquals("testGetTotalActivityLogEntriedInLastDay - Ensure value retrieve is equals to that expected", TEST_NUM_POLICY_LOGS + TEST_NUM_TRACKING_LOGS, this.beanToTest.getTotalActivityLogEntriesInLastDay());
    }

    public void testGetLastUpdatedTimestamp() {
        assertEquals("testGetLastUpdatedTimestamp - Ensure value retrieve is equals to that expected", TEST_TIMESTAMP, this.beanToTest.getLastUpdatedTimestamp());
    }

    private void setupStatProviders() {
        // Agent Stat Provider
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        ComponentInfo compInfo = new ComponentInfo(IAgentStatisticsProvider.class.getName(), AgentStatProviderImpl.class.getName(), LifestyleType.SINGLETON_TYPE);
        componentManager.registerComponent(compInfo, true);

        // Log count Provider
        compInfo = new ComponentInfo(ILogCountStatisticsProvider.class.getName(), LogCountStatProviderImpl.class.getName(), LifestyleType.SINGLETON_TYPE);
        componentManager.registerComponent(compInfo, true);

        // Policy Stat Provider
        compInfo = new ComponentInfo(IPolicyStatisticsProvider.class.getName(), PolicyStatisticsProviderImpl.class.getName(), LifestyleType.SINGLETON_TYPE);
        componentManager.registerComponent(compInfo, true);
    }

    public static class AgentStatProviderImpl implements IAgentStatisticsProvider {

        /**
         * @see com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticsProvider#getStatistics()
         */
        public IStatisticSet getStatistics() {
            return ServerStatisticsBeanImplTest.STAT_SET;
        }

    }

    public static class LogCountStatProviderImpl implements ILogCountStatisticsProvider {

        /**
         * @see com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticsProvider#getStatistics()
         */
        public IStatisticSet getStatistics() {
            return ServerStatisticsBeanImplTest.STAT_SET;
        }
    }

    public static class PolicyStatisticsProviderImpl implements IPolicyStatisticsProvider {

        /**
         * @see com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticsProvider#getStatistics()
         */
        public IStatisticSet getStatistics() {
            return ServerStatisticsBeanImplTest.STAT_SET;
        }
    }
}