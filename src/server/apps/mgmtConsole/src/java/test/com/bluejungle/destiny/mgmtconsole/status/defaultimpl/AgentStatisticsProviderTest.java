/*
 * Created on Apr 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import com.bluejungle.destiny.framework.types.CommitFault;
import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.framework.types.ServiceNotReadyFault;
import com.bluejungle.destiny.framework.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.framework.types.UnknownEntryFault;
import com.bluejungle.destiny.mgmtconsole.status.IAgentCountBean;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticSet;
import com.bluejungle.destiny.services.management.AgentServiceIF;
import com.bluejungle.destiny.services.management.types.AgentCount;
import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.services.management.types.AgentDTOQuerySpec;
import com.bluejungle.destiny.services.management.types.AgentQueryResultsDTO;
import com.bluejungle.destiny.services.management.types.AgentStatistics;
import com.bluejungle.destiny.services.management.types.AgentTypeDTOList;
import com.bluejungle.domain.agenttype.AgentTypeEnumType;

import javax.xml.rpc.ServiceException;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/AgentStatisticsProviderTest.java#5 $
 */

public class AgentStatisticsProviderTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AgentStatisticsProviderTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPullStatistic() {
        long agentsNotConnectedInLastDayCount = 2;
        long desktopAgentCount = 50;
        long fileServerAgentCount = 3457;
        long heartbeatsInLastDayCount = 104301092;
        long numAgentsWithOutOfDatePolicies = 4444;

        AgentStatistics testAgentStats = new AgentStatistics();
        testAgentStats.setAgentsNotConnectedInLastDayCount(agentsNotConnectedInLastDayCount);
        AgentCount[] agentCounts = { new AgentCount(AgentTypeEnumType.DESKTOP.getName(), "Desktop Enforcer", desktopAgentCount), new AgentCount(AgentTypeEnumType.FILE_SERVER.getName(), "File Server Enforcer", fileServerAgentCount) };
        testAgentStats.setAgentCount(agentCounts);
        testAgentStats.setHeartbeatsInLastDayCount(heartbeatsInLastDayCount);
        testAgentStats.setAgentsWithOutOfDatePolicies(numAgentsWithOutOfDatePolicies);

        TestAgentService testAgentService = new TestAgentService(testAgentStats);

        AgentStatisticsProvider providerToTest = new AgentStatisticsProviderExtention(testAgentService);
        IStatisticSet returnedStats = providerToTest.pullStatistic();

        IStatistic disconnectedAgentsStat = returnedStats.getStatistic(IAgentStatisticsProvider.AGENTS_NOT_CONNECTED_IN_LAST_DAY_COUNT_STAT_KEY);
        assertEquals("testPullStatistic - Ensure pulled agentsNotConnectedInLastDayCount is as expected", new Long(agentsNotConnectedInLastDayCount), disconnectedAgentsStat.getValue());
        IStatistic agentCountStat = returnedStats.getStatistic(IAgentStatisticsProvider.AGENT_COUNT_STAT_KEY);
        List<IAgentCountBean> retrievedAgentCounts = (List<IAgentCountBean>) agentCountStat.getValue();
        for (IAgentCountBean nextAgentCount : retrievedAgentCounts) {
            if (nextAgentCount.getAgentType().getAgentTypeId().equals(AgentTypeEnumType.DESKTOP)) {
                assertEquals("testPullStatistic - Ensure pulled desktopAgentCount is as expected", new Long(desktopAgentCount), nextAgentCount.getNumRegistered());
            } else if (nextAgentCount.getAgentType().getAgentTypeId().equals(AgentTypeEnumType.FILE_SERVER)) {
                assertEquals("testPullStatistic - Ensure pulled fileServerCount is as expected", new Long(fileServerAgentCount), nextAgentCount.getNumRegistered());
            }
        }
        IStatistic heartbeatsInLastDayCountStat = returnedStats.getStatistic(IAgentStatisticsProvider.HEARTBEATS_IN_LAST_DAY_COUNT_STAT_KEY);
        assertEquals("testPullStatistic - Ensure pulled heartbeatsInLastDayCount is as expected", new Long(heartbeatsInLastDayCount), heartbeatsInLastDayCountStat.getValue());
        IStatistic numAgentsWithOutOfDatePolicyCountStat = returnedStats.getStatistic(IAgentStatisticsProvider.AGENTS_WITH_OUT_OF_DATE_POLICIES_COUNT_STAT_KEY);
        assertEquals("testPullStatistic - Ensure pulled agentsWithOutOfDatePolicyCount is as expected", new Long(numAgentsWithOutOfDatePolicies), numAgentsWithOutOfDatePolicyCountStat.getValue());
    }

    private class AgentStatisticsProviderExtention extends AgentStatisticsProvider {

        private AgentServiceIF testAgentService;

        public AgentStatisticsProviderExtention(AgentServiceIF testAgentService) {
            super();
            this.testAgentService = testAgentService;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.status.defaultimpl.AgentStatisticsProvider#getAgentService()
         */
        protected AgentServiceIF getAgentService() throws ServiceException {
            return this.testAgentService;
        }
    }

    private class TestAgentService implements AgentServiceIF {

        private AgentStatistics agentStats;

        public TestAgentService(AgentStatistics agentStats) {
            super();
            this.agentStats = agentStats;
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#getAgentTypes()
         */
        public AgentTypeDTOList getAgentTypes() throws RemoteException, ServiceNotReadyFault {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#getAgentById(java.math.BigInteger)
         */
        public AgentDTO getAgentById(BigInteger id) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#getAgents(com.bluejungle.destiny.services.management.types.AgentDTOQuerySpec)
         */
        public AgentQueryResultsDTO getAgents(AgentDTOQuerySpec arg0) throws RemoteException, ServiceNotReadyFault {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#getAgentStatistics()
         */
        public AgentStatistics getAgentStatistics() throws RemoteException, ServiceNotReadyFault {
            return agentStats;
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#setCommProfile(java.math.BigInteger,
         *      java.lang.String)
         */
        public void setCommProfile(BigInteger id, String profileName) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault {
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#setAgentProfile(java.math.BigInteger,
         *      java.lang.String)
         */
        public void setAgentProfile(BigInteger id, String profileName) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault {
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#setCommProfileForAgents(com.bluejungle.destiny.services.common.types.IDList,
         *      java.math.BigInteger)
         */
        public void setCommProfileForAgents(IDList agentIDList, BigInteger profileId) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault {
        }

        /**
         * @see com.bluejungle.destiny.services.management.AgentServiceIF#unregisterAgent(java.math.BigInteger)
         */
        public void unregisterAgent(BigInteger arg0) throws RemoteException, ServiceNotReadyFault, CommitFault, UnknownEntryFault, UnauthorizedCallerFault {
            // TODO Auto-generated method stub

        }
    }
}
