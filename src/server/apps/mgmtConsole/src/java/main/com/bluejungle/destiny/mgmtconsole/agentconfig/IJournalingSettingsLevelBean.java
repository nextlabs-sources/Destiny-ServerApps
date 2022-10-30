/*
 * Created on Feb 22, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

import java.util.List;

/**
 * Represents the predefined journaling settings
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/IJournalingSettingsLevelBean.java#1 $
 */

public interface IJournalingSettingsLevelBean {

    /**
     * Retrieve the title of this predefined journaling setting
     * 
     * @return the title of this predefined journaling setting
     */
    public String getLevelTitle();

    /**
     * Retrieve the actions associated with this journaling setting
     * 
     * @return the actions associated with this journaling setting
     */
    public List<IActionTypeBean> getLevelActions();
}
