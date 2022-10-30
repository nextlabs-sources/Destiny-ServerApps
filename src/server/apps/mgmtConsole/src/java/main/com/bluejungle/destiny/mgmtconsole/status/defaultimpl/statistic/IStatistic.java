/*
 * Created on April 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic;

import java.util.Calendar;

/**
 * IStatistic represents a single server data measurement
 * 
 * @author sgoldstein
 */
public interface IStatistic {

    /**
     * Retrieve the value of the statistic
     * 
     * @return the value of the statistic
     */
    public Object getValue();

    /**
     * Retrieve the last time the statistic was measured
     * 
     * @return the last time the statistic was measured
     */
    public Calendar getLastUpdatedTimestamp();
}

