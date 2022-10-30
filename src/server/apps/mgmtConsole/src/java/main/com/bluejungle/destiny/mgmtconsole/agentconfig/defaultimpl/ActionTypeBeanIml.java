/*
 * Created on Feb 22, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.agentconfig.IActionTypeBean;

/**
 * Default implementation of IActionTypeBean
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/ActionTypeBeanIml.java#1 $
 */

public class ActionTypeBeanIml implements IActionTypeBean {

    private String actionId;
    private String actionTitle;

    /**
     * Create an instance of ActionTypeBeanIml
     * 
     * @param id
     * @param id2
     */
    public ActionTypeBeanIml(String id, String title) {
        if (id == null) {
            throw new NullPointerException("id cannot be null.");
        }

        if (title == null) {
            throw new NullPointerException("title cannot be null.");
        }

        this.actionId = id;
        this.actionTitle = title;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IActionTypeBean#getActionId()
     */
    public String getActionId() {
        return this.actionId;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IActionTypeBean#getActionTitle()
     */
    public String getActionTitle() {
        return this.actionTitle;
    }

}
