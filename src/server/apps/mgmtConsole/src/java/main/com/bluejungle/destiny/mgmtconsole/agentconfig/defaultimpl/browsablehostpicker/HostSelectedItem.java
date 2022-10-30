/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker;

import java.util.HashMap;
import java.util.Map;

import com.bluejungle.destiny.services.management.types.AgentDTO;
import com.bluejungle.destiny.webui.browsabledatapicker.IEnhancedSelectedItem;

/**
 * An implementation of ISelectedItem for a Host
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/browsablehostpicker/HostSelectedItem.java#2 $
 */

public class HostSelectedItem implements IEnhancedSelectedItem {

    public static final String CURRENT_PROFILE_NAME_DISPLAYABLE_PROPERTY_IF = "currentProfileName";
    public static final String HOSTNAME_DISPLAYABLE_PROPERTY_ID = "hostname";
    
    private AgentDTO wrappedAgentDTO;
    private Map displayableProperties;

    /**
     * Create an instance of HostSelectedItem
     *  
     */
    public HostSelectedItem(AgentDTO wrappedAgentDTO) {
        if (wrappedAgentDTO == null) {
            throw new NullPointerException("wrappedDTO cannot be null.");
        }

        this.wrappedAgentDTO = wrappedAgentDTO;

        this.displayableProperties = new HashMap();
        this.displayableProperties.put(HOSTNAME_DISPLAYABLE_PROPERTY_ID, this.wrappedAgentDTO.getHost());
        this.displayableProperties.put(CURRENT_PROFILE_NAME_DISPLAYABLE_PROPERTY_IF, this.wrappedAgentDTO.getCommProfileName());
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getId()
     */
    public String getId() {
        return this.wrappedAgentDTO.getId().toString();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.wrappedAgentDTO.getHost();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IEnhancedSelectedItem#getProperties()
     */
    public Map getDisplayableProperties() {
        return this.displayableProperties;
    }

    /**
     * Retrieve the wrapped AgentDTO
     * 
     * @return the wrapped AgentDTO
     */
    AgentDTO getWrappedAgent() {
        return this.wrappedAgentDTO;
    }
}