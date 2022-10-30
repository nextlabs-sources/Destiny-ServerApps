/*
 * Created on Nov 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentstatus.defaultimpl;

import com.bluejungle.destiny.webui.framework.faces.UIEnumBase;

/**
 * This class is used to display the agent type on the UI.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentstatus/defaultimpl/AgentUIType.java#1 $
 */

public class AgentUIType extends UIEnumBase {

    /**
     * Name of the resource bundle
     */
    private static final String MGMT_CONSOLE_RESOURCE_BUNDLE_NAME = "MgmtConsoleMessages";

    /**
     * Supported agent types
     */
    public static final AgentUIType ENFORCER_AGENT = new AgentUIType("FILE_SERVER", "status_by_host_page_agent_enforcer_type", 1);
    public static final AgentUIType COMPLIANCE_AGENT = new AgentUIType("DESKTOP", "status_by_host_page_agent_compliance_type", 2);

    /**
     * Constructor
     * 
     * @param enunName
     * @param bundleKeyName
     * @param type
     */
    protected AgentUIType(String enunName, String bundleKeyName, int type) {
        super(enunName, bundleKeyName, type);
    }

    /**
     * Returns the localized display value
     * 
     * @param enumeration
     *            enum to localize
     * @return the localized display value
     */
    public static String getDisplayValue(UIEnumBase enumeration) {
        return getDisplayValue(enumeration, MGMT_CONSOLE_RESOURCE_BUNDLE_NAME);
    }

    /**
     * Retrieve an AgentUIType instance by name
     * 
     * @param type
     *            the name of the AgentUIType
     * @return the AgentUIType associated with the provided name
     * @throws IllegalArgumentException
     *             if no AgentUIType exists with the specified name
     */
    public static AgentUIType getAgentUIType(String type) {
        return getElement(type, AgentUIType.class);
    }
}
