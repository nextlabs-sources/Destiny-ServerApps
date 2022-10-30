/*
 * Created on May 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utilized by the display layer to select a Role from the Role list menu
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/SelectRoleActionListener.java#1 $
 */

public class SelectRoleActionListener extends RolesViewActionListenerBase {

    public static final String SELECTED_ROLE_ID_PARAM_NAME = "selectedRoleId";
    private static final Log LOG = LogFactory.getLog(SelectRoleActionListener.class.getName());

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        String selectedRoleId = getRequestParameter(SELECTED_ROLE_ID_PARAM_NAME, null);

        if (selectedRoleId == null) {
            throw new NullPointerException("Selected Role id parameter not found.");
        }

        IRolesViewBean rolesViewBean = getRolesViewBean();

        Long selectedRoleIdAsLong = Long.valueOf(selectedRoleId);
        rolesViewBean.setSelectedRole(selectedRoleIdAsLong.longValue());
    }

    /**
     * Retrieve a reference to a Log
     * 
     * @return a reference to a Log
     */
    private Log getLog() {
        return LOG;
    }
}