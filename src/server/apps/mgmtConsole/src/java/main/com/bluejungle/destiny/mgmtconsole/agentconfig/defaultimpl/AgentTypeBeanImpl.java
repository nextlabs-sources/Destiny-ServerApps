/*
 * Created on Feb 9, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentTypeBean;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IPredefinedJournalingSettingsBean;
import com.bluejungle.destiny.services.management.types.AgentTypeDTO;

/**
 * Default implementation of IAgentTypeBean
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/AgentTypeBeanImpl.java#1 $
 */

public class AgentTypeBeanImpl implements IAgentTypeBean {

    private AgentTypeDTO agentTypeDTO;
    private IPredefinedJournalingSettingsBean predefinedJournalingSettings;

    /**
     * Create an instance of AgentTypeBeanImpl
     * @param nextAgentType
     */
    public AgentTypeBeanImpl(AgentTypeDTO nextAgentType) {
        if (nextAgentType == null) {
            throw new NullPointerException("nextAgentType cannot be null.");
        }
        
        this.agentTypeDTO = nextAgentType;
        this.predefinedJournalingSettings = new PredefinedJournalingSettingsBeanImpl(this.agentTypeDTO);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IAgentTypeBean#getAgentTypeId()
     */
    public String getAgentTypeId() {
        return this.agentTypeDTO.getId();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IAgentTypeBean#getAgentTypeTitle()
     */
    public String getAgentTypeTitle() {
        return this.agentTypeDTO.getTitle();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentTypeBean#getPredefinedJournalingSettings()
     */
    public IPredefinedJournalingSettingsBean getPredefinedJournalingSettings() {
        return this.predefinedJournalingSettings;
    }        
}
