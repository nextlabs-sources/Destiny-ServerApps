/*
 * Created on April 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic;

/**
 * An IStatisticProvider is reponsible for providing a set of IStatistic's
 * 
 * @author sgoldstein
 */
public interface IStatisticsProvider {

    /**
     * Retrieve the set of IStatistic instances that this provider provides
     * 
     * @return the set of IStatistic instances that this provider provides
     */
    public IStatisticSet getStatistics();
}

