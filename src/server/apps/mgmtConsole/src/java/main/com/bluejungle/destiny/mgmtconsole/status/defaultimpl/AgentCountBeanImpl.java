/*
 * Created on Feb 9, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.status.IAgentCountBean;
import com.bluejungle.destiny.mgmtconsole.status.IAgentTypeBean;

/**
 * Implementation of IAgentCountBeant
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/AgentCountBeanImpl.java#1 $
 */

public class AgentCountBeanImpl implements IAgentCountBean {

    private IAgentTypeBean agentType;
    private Long count;

    /**
     * Create an instance of AgentCountBeanImpl
     * 
     * @param agentType
     * @param count
     */
    public AgentCountBeanImpl(IAgentTypeBean agentType, Long count) {
        super();
        this.agentType = agentType;
        this.count = count;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IAgentCountBean#getAgentType()
     */
    public IAgentTypeBean getAgentType() {
        return this.agentType;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.status.IAgentCountBean#getNumRegistered()
     */
    public Long getNumRegistered() {
        return this.count;
    }

}
