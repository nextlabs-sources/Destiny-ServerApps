/*
 * Created on Feb 22, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.agentconfig.IActionTypeBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IJournalingSettingsLevelBean;

import java.util.List;

/**
 * Default implementation of {@link IJournalingSettingsLevelBean}
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/JournalingSettingsLevelBeanImpl.java#1 $
 */

public class JournalingSettingsLevelBeanImpl implements IJournalingSettingsLevelBean {

    private String levelTitle;
    private List<IActionTypeBean> levelActions;

    /**
     * Create an instance of JournalingSettingsLevelBeanImpl
     * 
     * @param key
     * @param value
     */
    public JournalingSettingsLevelBeanImpl(String level, List<IActionTypeBean> actions) {
        if (level == null) {
            throw new NullPointerException("level cannot be null.");
        }

        if (actions == null) {
            throw new NullPointerException("actions cannot be null.");
        }

        this.levelTitle = level;
        this.levelActions = actions;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IJournalingSettingsLevelBean#getLevelActions()
     */
    public List<IActionTypeBean> getLevelActions() {
        return this.levelActions;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IJournalingSettingsLevelBean#getLevelTitle()
     */
    public String getLevelTitle() {
        return this.levelTitle;
    }

}
