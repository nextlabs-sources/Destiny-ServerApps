/*
 * Created on Apr 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.mgmtconsole.status.IAgentTypeBean;
import com.bluejungle.destiny.mgmtconsole.status.IAgentCountBean;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticSet;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.SimpleStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.SimpleStatisticSet;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.TimedPullStatisticProvider;
import com.bluejungle.destiny.services.management.AgentServiceStub;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.AgentCount;
import com.bluejungle.destiny.services.management.types.AgentStatistics;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;
import org.apache.axis2.AxisFault;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * AgentStatisticProvider is a concrete implementation of the
 * IAGentStatisticsProvider interface. It's reponsible for providing statistic
 * measurements related to Agents
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/AgentStatisticsProvider.java#2 $
 */
public class AgentStatisticsProvider extends TimedPullStatisticProvider implements IAgentStatisticsProvider {

    private static final Long ERROR_STATISTIC_VALUE = new Long(-1);
    private static final String AGENT_SERVICE_LOCATION_SERVLET_PATH = "/services/AgentService";
    private AgentServiceStub agentService;

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.statistic.TimedPullStatisticProvider#pullStatistic()
     */
    public IStatisticSet pullStatistic() {
        SimpleStatisticSet statisticSetToReturn = new SimpleStatisticSet();
                
        try {
            AgentServiceStub agentService = getAgentService();
            AgentStatistics agentStatistics = agentService.getAgentStatistics();

            Calendar timestamp = Calendar.getInstance();

            List<IAgentCountBean> agentCountBeans = new LinkedList<IAgentCountBean>();
            AgentCount[] agentCounts = agentStatistics.getAgentCount();
            for (int i = 0; i < agentCounts.length; i++) {
                AgentCount nextAgentCount = agentCounts[i];
                IAgentTypeBean nextAgentTypeBean = new AgentTypeBeanImpl(nextAgentCount.getAgentTypeId(), nextAgentCount.getDefaultAgentTitle());
                agentCountBeans.add(new AgentCountBeanImpl(nextAgentTypeBean, nextAgentCount.getCount()));
            }
            IStatistic agentsCountStat = new SimpleStatistic(agentCountBeans, timestamp);
            statisticSetToReturn.setStatistic(AGENT_COUNT_STAT_KEY, agentsCountStat);                

            long numHeartbeatsInLastDay = agentStatistics.getHeartbeatsInLastDayCount();
            IStatistic numHeartbeatCountInLastDataStat = new SimpleStatistic(new Long(numHeartbeatsInLastDay), timestamp);
            statisticSetToReturn.setStatistic(HEARTBEATS_IN_LAST_DAY_COUNT_STAT_KEY, numHeartbeatCountInLastDataStat);

            long agentsNotConnectedInLastDay = agentStatistics.getAgentsNotConnectedInLastDayCount();
            IStatistic agentsNotConnectedInLastDayStat = new SimpleStatistic(new Long(agentsNotConnectedInLastDay), timestamp);
            statisticSetToReturn.setStatistic(AGENTS_NOT_CONNECTED_IN_LAST_DAY_COUNT_STAT_KEY, agentsNotConnectedInLastDayStat);

            long agentsWithOutOfDatePolicies = agentStatistics.getAgentsWithOutOfDatePolicies();
            IStatistic agentsWithOutOfDataPoliciesStat = new SimpleStatistic(new Long(agentsWithOutOfDatePolicies), timestamp);
            statisticSetToReturn.setStatistic(AGENTS_WITH_OUT_OF_DATE_POLICIES_COUNT_STAT_KEY, agentsWithOutOfDataPoliciesStat);
        } catch (ServiceNotReadyFault exception) {
            getLog().warn("Failed to retrieve agent statistics.  Agent service not ready", exception);
            populateErrorStatistics(statisticSetToReturn);
        } catch (RemoteException exception) {
            getLog().warn("Failed to retrieve agent statistics", exception);
            populateErrorStatistics(statisticSetToReturn);
        }
        return statisticSetToReturn;
    }

    /**
     * Created a fabricated statistic set if the actual one cannot be retrieved
     * due to an error
     * 
     * @param statisticSetToPopulate
     *            the statistic set to populate with the fabricated data
     */
    private void populateErrorStatistics(SimpleStatisticSet statisticSetToPopulate) {
        IStatistic errorStat = new SimpleStatistic(ERROR_STATISTIC_VALUE, Calendar.getInstance());
        IStatistic agentCoutErrorStat = new SimpleStatistic(Collections.EMPTY_LIST, Calendar.getInstance());
        statisticSetToPopulate.setStatistic(AGENT_COUNT_STAT_KEY, agentCoutErrorStat);
        statisticSetToPopulate.setStatistic(HEARTBEATS_IN_LAST_DAY_COUNT_STAT_KEY, errorStat);
        statisticSetToPopulate.setStatistic(AGENTS_NOT_CONNECTED_IN_LAST_DAY_COUNT_STAT_KEY, errorStat);
        statisticSetToPopulate.setStatistic(AGENTS_WITH_OUT_OF_DATE_POLICIES_COUNT_STAT_KEY, errorStat);

    }

    /**
     * Retrieve the Agent Service interface.
     * 
     * Note - Protected for unit test purposes. Yes, not ideal, but it's also
     * better to test with a stub service rather than an actual service
     * 
     * @return the Agent Service interface
     * @throws ServiceException
     *             if the agent service interface could not be located
     */
    protected AgentServiceStub getAgentService() throws AxisFault {
        if (this.agentService == null) {
            IComponentManager compMgr = getManager();
            IConfiguration mainCompConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = (String) mainCompConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += AGENT_SERVICE_LOCATION_SERVLET_PATH;

            this.agentService = new AgentServiceStub(location);
        }

        return this.agentService;
    }
}
