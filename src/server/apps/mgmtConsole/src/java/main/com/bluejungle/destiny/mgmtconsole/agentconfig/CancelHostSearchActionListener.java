/*
 * Created on Apr 25, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

/**
 * CancelHostSearchActionListener is a JSF ActionListener responsible for
 * canceling the currently search filter in the angent config host list view
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentconfig/CancelHostSearchActionListener.java#1 $
 */

public class CancelHostSearchActionListener extends AgentConfigurationViewActionListenerBase {

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        IAgentConfigurationBean configurationBean = getAgentConfigurationViewBean();
        configurationBean.setHostSearchString(IAgentConfigurationBean.EMPTY_HOST_SEARCH_STRING);
    }
}
