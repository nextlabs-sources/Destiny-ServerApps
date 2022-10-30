/*
 * Created on Apr 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic;

import java.util.Iterator;

/**
 * IStatisticSet contains a set of IStatistic instances
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/statistic/IStatisticSet.java#2 $
 */
public interface IStatisticSet {

    /**
     * Retrieve an IStatistic instance by key
     * 
     * @param key
     *            the key of the IStatistic instance
     * @return the IStatistic instance associated with the specified key or null
     *         if the instance does not exists
     */
    public IStatistic getStatistic(String key);

    /**
     * Retrieve an iterator of all IStatistic instances within this Set
     * 
     * @return an iterator of all IStatistic instances within this Set
     */
    public Iterator iterator();
    
    // Possible future addition
    //public Set getStatisticKeySet();
}