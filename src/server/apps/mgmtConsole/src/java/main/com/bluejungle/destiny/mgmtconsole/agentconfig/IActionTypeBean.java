/*
 * Created on Feb 22, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

/**
 * A javabean which represents and action type
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/IActionTypeBean.java#1 $
 */

public interface IActionTypeBean {

    /**
     * Retrieve the id of this action type
     * 
     * @return the id of this action type
     */
    public String getActionId();

    /**
     * Retrieve the title of this action type
     * 
     * @return the title of this action type
     */
    public String getActionTitle();
}
