/*
 * Created on Feb 20, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.agentconfig.AgentConfigurationViewActionListenerBase;
import com.bluejungle.destiny.mgmtconsole.agentconfig.IAgentConfigurationBean;
import com.bluejungle.domain.agenttype.AgentTypeEnumType;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;


/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/AgentTypeSelectionActionListener.java#1 $
 */

public class AgentTypeSelectionActionListener extends AgentConfigurationViewActionListenerBase {

    private static final String AGENT_TYPE_REQUEST_PARAMETER = "agentType";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        IAgentConfigurationBean agentConfigurationBean = getAgentConfigurationViewBean();
        String agentType = getRequestParameter(AGENT_TYPE_REQUEST_PARAMETER, AgentTypeEnumType.DESKTOP.getName());
        agentConfigurationBean.setAgentType(agentType);
    }

}
