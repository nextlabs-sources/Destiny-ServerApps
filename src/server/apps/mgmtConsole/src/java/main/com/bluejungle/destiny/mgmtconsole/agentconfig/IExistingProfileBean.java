/*
 * Created on Apr 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

/**
 * An extension to the IProfileBean interface for profiles which are persistent
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/IExistingProfileBean.java#1 $
 */

public interface IExistingProfileBean extends IProfileBean {

    /**
     * Retrieve the id of the associated profile
     * 
     * @return the id of the associated profile
     */
    public long getProfileId();
}