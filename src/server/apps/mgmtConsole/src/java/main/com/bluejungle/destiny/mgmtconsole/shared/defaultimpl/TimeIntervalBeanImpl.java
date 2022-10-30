/*
 * Created on Apr 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.shared.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/shared/defaultimpl/TimeIntervalBeanImpl.java#1 $
 */

public class TimeIntervalBeanImpl implements ITimeIntervalBean {
    private int time;
    private String timeUnit;
    
    public TimeIntervalBeanImpl(int time, String timeUnit) {
        super();
        this.time = time;
        this.timeUnit = timeUnit;
    }
 
    /**
     * @see com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean#getTime()
     */
    public int getTime() {
        return this.time;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean#setTime(int)
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean#getTimeUnit()
     */
    public String getTimeUnit() {
        return this.timeUnit;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.shared.ITimeIntervalBean#setTimeUnit(java.lang.String)
     */
    public void setTimeUnit(String timeUnit) {
        if (timeUnit == null) {
            throw new NullPointerException("timeUnit cannot be null.");
        }
        
        this.timeUnit = timeUnit;
    }

}
