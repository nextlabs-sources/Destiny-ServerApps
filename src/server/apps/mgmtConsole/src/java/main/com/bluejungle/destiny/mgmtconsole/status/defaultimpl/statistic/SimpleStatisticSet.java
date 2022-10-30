/*
 * Created on Apr 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * SimpleStatisticSet is a simple implementation of the IStatisticSet interface
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/statistic/SimpleStatisticSet.java#2 $
 */

public class SimpleStatisticSet implements IStatisticSet {

    private Map statistics = new HashMap();

    /**
     * Create an instance of SimpleStatisticSet
     *  
     */
    public SimpleStatisticSet() {
        super();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.statistic.IStatisticSet#getStatistic(java.lang.String)
     */
    public IStatistic getStatistic(String key) {
        if (key == null) {
            throw new NullPointerException("key cannot be null.");
        }

        return (IStatistic) statistics.get(key);
    }

    /**
     * Add a stastic to the set
     * 
     * @param key
     *            the key of the statistic to add
     * @param statisticToSet
     *            the statistic to add
     */
    public void setStatistic(String key, IStatistic statisticToSet) {
        if (key == null) {
            throw new NullPointerException("key cannot be null.");
        }

        if (statisticToSet == null) {
            throw new NullPointerException("statisticToSet cannot be null.");
        }

        statistics.put(key, statisticToSet);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticSet#iterator()
     */
    public Iterator iterator() {
        return this.statistics.values().iterator();
    }
}