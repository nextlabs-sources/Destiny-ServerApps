/*
 * Created on Apr 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

import java.util.Map;

/**
 * A bean which provides an interface to view/change Custom Journaling options
 * for a agent configuration profile
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/ICustomJournalingSettingsBean.java#1 $
 */

public interface ICustomJournalingSettingsBean {

    /**
     * Retrieve the name assigned to this set of custom journaling settings.
     * Used for persistence and identification purposes
     * 
     * @return the name assigned to this set of custom journaling settings
     */
    public String getName();

    /**
     * Retrieve the actions that are logged. The map value will be either true
     * or false. The purpose of the Map is to support the JSF expression
     * language syntax
     * 
     * @return that actions that will be logged
     */
    public Map<String, Boolean> getLoggedAction();
}