/*
 * Created on Feb 22, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

import javax.faces.model.SelectItem;

import java.util.List;

/**
 * Represents the predefined jounraling settings for a given agent type
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/IPredefinedJournalingSettingsBean.java#1 $
 */

public interface IPredefinedJournalingSettingsBean {

    /**
     * Retrieve the list of predefined levels as select items
     * 
     * @return the list of predefined levels as select items
     */
    public List<SelectItem> getPredefinedJournalingSettingsLevelsAsSelectItems();

    /**
     * Retrieve the predefined journaling settings levels for the associate
     * agent type
     * 
     * @return the predefined journaling settings levels for the associate agent
     *         type
     */
    public List<IJournalingSettingsLevelBean> getPredefinedJournalingSettingsLevels();
}
