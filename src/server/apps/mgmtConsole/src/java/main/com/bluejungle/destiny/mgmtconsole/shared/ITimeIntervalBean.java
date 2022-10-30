/*
 * Created on Apr 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.shared;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/shared/ITimeIntervalBean.java#1 $
 */

public interface ITimeIntervalBean {

    /**
     * Returns the time in the time unit specified in this TimeInterval
     * 
     * @return the time.
     */
    public int getTime();

    /**
     * Sets the time in the currently associated Time Unit
     * 
     * @param time
     *            The time to set.
     */
    public void setTime(int time);

    /**
     * Returns the time unit associated with this TimeInterval (one of "days",
     * "hours", "minutes", "seconds")
     * 
     * @return the time unit.
     */
    public String getTimeUnit();

    /**
     * Sets the time unit of the currently associated time (one of "days",
     * "hours", "minutes", "seconds"). Note that this does not change the value
     * of the time itself
     * 
     * @param time
     *            unit The time unit to set.
     */
    public void setTimeUnit(String timeUnit);
}