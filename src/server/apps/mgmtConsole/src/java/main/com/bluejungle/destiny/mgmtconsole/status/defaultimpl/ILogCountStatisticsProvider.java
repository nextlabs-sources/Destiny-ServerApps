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
 * ILogCountStatisticsProvider is an extension of IStatisticsProvider which
 * contains functionality specifically for retrieving statistics related to Logs
 * throughput
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/ILogCountStatisticsProvider.java#1 $
 */
public interface ILogCountStatisticsProvider extends IStatisticsProvider {

    /**
     * <code>TRACKING_ACTIVITY_LOG_COUNT_STAT_KEY</code> is the key for the
     * tracking activity log Count statistic that's used to retrieve the
     * associated statistic value from the IStatisticSet provided by an
     * ILogCountStatisticsProvider
     */
    public static final String TRACKING_ACTIVITY_LOG_COUNT_STAT_KEY = "TrackingActivityLogCountStatKey";

    /**
     * <code>POLICY_ACTIVITY_LOG_COUNT_STAT_KEY</code> is the key for the
     * policy activity log Count statistic that's used to retrieve the
     * associated statistic value from the IStatisticSet provided by an
     * ILogCountStatisticsProvider
     */
    public static final String POLICY_ACTIVITY_LOG_COUNT_STAT_KEY = "PolicyActivityLogCountStatKey";
}