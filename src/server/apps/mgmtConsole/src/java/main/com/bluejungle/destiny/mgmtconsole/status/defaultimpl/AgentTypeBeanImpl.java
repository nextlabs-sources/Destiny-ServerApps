/*
 * Created on Feb 9, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.status.IAgentTypeBean;

/**
 * Default implementation of IAgentTypeBean
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/AgentTypeBeanImpl.java#1 $
 */

public class AgentTypeBeanImpl implements IAgentTypeBean {

    private String agentTypeId;
    private String agentTypeTitle;

    /**
     * Create an instance of AgentTypeBeanImpl
     * 
     * @param agentTypeId
     * @param agentTypeTitle
     */
    public AgentTypeBeanImpl(String agentTypeId, String agentTypeTitle) {
        super();
        this.agentTypeId = agentTypeId;
        this.agentTypeTitle = agentTypeTitle;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IAgentTypeBean#getAgentTypeId()
     */
    public String getAgentTypeId() {
        return this.agentTypeId;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IAgentTypeBean#getAgentTypeTitle()
     */
    public String getAgentTypeTitle() {
        return agentTypeTitle;
    }

}
