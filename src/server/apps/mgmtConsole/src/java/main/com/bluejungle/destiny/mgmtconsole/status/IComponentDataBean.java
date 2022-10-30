/*
 * Created on Apr 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status;

import java.util.Calendar;

/**
 * IComponentDataBean represents data about a particular Server Component
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/IComponentDataBean.java#1 $
 */
public interface IComponentDataBean {

    /**
     * Retrieve the component name
     * 
     * @return the component name
     */
    public String getComponentName();

    /**
     * Retrieve the component type
     * 
     * @return the component type
     */
    public String getComponentType();

    /**
     * Retrieve the component host
     * 
     * @return the component host
     */
    public String getComponentHostName();

    /**
     * Retrieve the component port
     * 
     * @return the component port
     */
    public int getComponentPort();

    /**
     * Retrieve the timestamp of the last heartbeat sent by the component
     * 
     * @return the timestamp of the last heartbeat sent by the component
     */
    public Calendar getComponentLastHeartbeatTime();

    /**
     * Retrieve the timestamp of the next expected heartbeat
     * 
     * @return the timestamp of the next expected heartbeat
     */
    public Calendar getComponentExpectedHeartbeatTime();

    /**
     * Determine if the component is active (running as expected)
     * 
     * @return true if the component is active; false otherwise
     */
    public boolean isActive();
}