/*
 * Created on Apr 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticsProvider;

/**
 * IAgentStatisticsProvider is an extension of IStatisticsProvider which
 * contains functionality specifically for retrieving statistics related to
 * Agents
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/IAgentStatisticsProvider.java#1 $
 */
public interface IAgentStatisticsProvider extends IStatisticsProvider {

    /**
     * <code>AGENT_COUNT_STAT_KEY_PREFIX</code> is the key for the agent count
     * statistic that's used to retrieve the associated statistic value from the
     * IStatisticSet provided by an IAgentStatisticsProvider. The Agent Count
     * Statistic value is a List of IAgentCountBean instances
     */
    public static final String AGENT_COUNT_STAT_KEY = "AgentCount";

    /**
     * <code>HEARTBEATS_IN_LAST_DAY_COUNT_STAT_KEY</code> is the key for the
     * heartbeat throughput statistic that's used to retrieve the associated
     * statistic value from the IStatisticSet provided by an
     * IAgentStatisticsProvider
     */
    public static final String HEARTBEATS_IN_LAST_DAY_COUNT_STAT_KEY = "HeartbeatsInLastDayCount";

    /**
     * <code>AGENTS_NOT_CONNECTED_IN_LAST_DAY_COUNT_STAT_KEY</code> is the key
     * for the disconnected agent count statistic that's used to retrieve the
     * associated statistic value from the IStatisticSet provided by an
     * IAgentStatisticsProvider
     */
    public static final String AGENTS_NOT_CONNECTED_IN_LAST_DAY_COUNT_STAT_KEY = "AgentsNotConnectedInLastDayCount";

    /**
     * <code>AGENTS_WITH_OUT_OF_DATE_POLICIES_COUNT_STAT_KEY</code> is the key
     * for the out of date agent statistic that's used to retrieve the
     * associated statistic value from the IStatisticSet provided by an
     * IAgentStatisticsProvider
     */
    public static final String AGENTS_WITH_OUT_OF_DATE_POLICIES_COUNT_STAT_KEY = "AgentsWithOutOfDatePoliciesCount";
}