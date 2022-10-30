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
 * IPolicyCountStatisticsProvider is an extension of IStatisticsProvider which
 * contains functionality specifically for retrieving statistics related to
 * Policies
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/IPolicyStatisticsProvider.java#1 $
 */
public interface IPolicyStatisticsProvider extends IStatisticsProvider {

    /**
     * <code>POLICY_COUNT_STAT_KEY</code> is the key for the policy count
     * statistic that's used to retrieve the associated statistic value from the
     * IStatisticSet provided by an IPolicyStatisticsProvider
     */
    public static final String POLICY_COUNT_STAT_KEY = "PolicyCount";
}