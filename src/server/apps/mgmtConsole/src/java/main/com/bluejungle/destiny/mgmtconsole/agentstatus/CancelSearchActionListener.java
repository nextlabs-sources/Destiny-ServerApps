/*
 * Created on Dec 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentstatus;

import javax.faces.event.ActionEvent;

/**
 * CancelFreeFormSearchActionListener is a Faces ActionListener which will update the
 * Status by Agent bean when a user cancels a search
 * 
 * @author sgoldstein
 */
public class CancelSearchActionListener extends StatusByAgentActionListenerBase {

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) {
        IStatusByAgentBean statusByAgentBean = getStatusByAgentViewBean();
        statusByAgentBean.cancelSearch();
    }
}

