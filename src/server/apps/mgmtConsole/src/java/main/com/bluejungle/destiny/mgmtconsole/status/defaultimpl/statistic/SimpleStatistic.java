/*
 * Created on Apr 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic;

import java.util.Calendar;

/**
 * SimpleStatistic is a simple implementation of the IStatistic interface
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/statistic/SimpleStatistic.java#1 $
 */
public class SimpleStatistic implements IStatistic {

    private Object statisticValue = null;
    private Calendar timestamp = null;

    /**
     * Create a SimpleStatistic instance with a statistic value and assocatiated
     * timestamp
     * 
     * @param statisticValue
     *            the value of the statistic
     * @param timestamp
     *            the time at which the statistic was measured
     */
    public SimpleStatistic(Object statisticValue, Calendar timestamp) {
        super();

        if (statisticValue == null) {
            throw new NullPointerException("statisticValue cannot be null.");
        }

        if (timestamp == null) {
            throw new NullPointerException("timestamp cannot be null.");
        }

        this.statisticValue = statisticValue;
        this.timestamp = timestamp;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.statistic.IStatistic#getValue()
     */
    public Object getValue() {
        return this.statisticValue;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.statistic.IStatistic#getLastUpdatedTimestamp()
     */
    public Calendar getLastUpdatedTimestamp() {
        return this.timestamp;
    }

}