/*
 * Created on Apr 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

/**
 * An extension to the IProfileBean interface for profiles which have not yet
 * been persisted
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/INewProfileBean.java#1 $
 */

public interface INewProfileBean extends IProfileBean {

    /**
     * Set the profile title
     * 
     * @param title
     *            the profile title
     */
    public void setProfileTitle(String title);
}