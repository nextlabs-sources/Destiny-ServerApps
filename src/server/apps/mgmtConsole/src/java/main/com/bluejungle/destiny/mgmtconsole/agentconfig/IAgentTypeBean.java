/*
 * Created on Feb 9, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

/**
 * IAgentTypeBean represents an Agent Type
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/IAgentTypeBean.java#1 $
 */
public interface IAgentTypeBean {
    /**
     * Retrieve the id of the agent type
     * 
     * @return the id of the agent type
     */
    public String getAgentTypeId();

    /**
     * Retrieve the title of the agent type
     * 
     * @return the title of the agent type
     */
    public String getAgentTypeTitle();
    
    /**
     * Retrieve the predefined journaling settings for the agent type
     * 
     * @return the predefined journaling settings for the agent type
     */
    public IPredefinedJournalingSettingsBean getPredefinedJournalingSettings();
}
