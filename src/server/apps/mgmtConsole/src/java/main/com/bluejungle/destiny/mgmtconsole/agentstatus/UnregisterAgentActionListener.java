/*
 * Created on Oct 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentstatus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import java.rmi.RemoteException;


/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentstatus/UnregisterAgentActionListener.java#1 $
 */

public class UnregisterAgentActionListener extends StatusByAgentActionListenerBase {
    private static final String UNREGISTER_AGENT_SUCCESS_MSG = "status_by_host_unregister_agent_success_message";
    private static final String UNREGISTER_AGENT_FAILED_ERROR_MSG = "status_by_host_unregister_agent_failed_message";    
    public static final String AGENT_TO_UNREGISTER_ID_PARAM_NAME = "agentId";
    private static final Log LOG = LogFactory.getLog(UnregisterAgentActionListener.class.getName());
    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        String agentToUnregisterId = getRequestParameter(AGENT_TO_UNREGISTER_ID_PARAM_NAME, null);

        if (agentToUnregisterId == null) {
            throw new NullPointerException("Agent to unregister ID parameter not found.");
        }
        
        IStatusByAgentBean statusByAgentBean = getStatusByAgentViewBean();
        
        
        try {
            statusByAgentBean.unregisterAgent(agentToUnregisterId);
            addSuccessMessage(UNREGISTER_AGENT_SUCCESS_MSG);
        } catch (StatusByAgentViewException | RemoteException exception) {
            addErrorMessage(UNREGISTER_AGENT_FAILED_ERROR_MSG);
            getLog().error("Failed to unregisterd agent with id, " + agentToUnregisterId + ".", exception);
        }
    }

    /**
     * Retrieve a Logger
     * 
     * @return a Logger
     */
    private Log getLog() {
        return LOG;
    }
}
