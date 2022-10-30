/*
 * Created on Jul 8, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/DeleteSelectedUserActionListener.java#1 $
 */

public class DeleteSelectedUserActionListener extends UsersViewActionListenerBase {

    private static final Log LOG = LogFactory.getLog(DeleteSelectedUserActionListener.class);
    
    private static final String USER_DELETE_SUCCESS_MSG = "users_and_roles_users_delete_success_message_detail";
    private static final String USER_DELETE_FAILED_ERROR_MSG = "users_and_roles_users_delete_failed_error_message_detail";
    private static final String USERS_VIEW_ACTION = "usersAndRolesUsers";
    
    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        IUsersViewBean usersViewBean = getUsersViewBean();
        
        try {
            usersViewBean.deleteSelectedUser();
            addSuccessMessage(USER_DELETE_SUCCESS_MSG);                        
        } catch (UsersException exception) {
            addErrorMessage(USER_DELETE_FAILED_ERROR_MSG);
            getLog().error("Failed to delete user", exception);
        }        
        
        // Set the action to ensure a redirect or forward based on navigation case
        super.setResponseAction(USERS_VIEW_ACTION, event);
    }
    
    /**
     * Retrieve a Logger
     * @return a Logger
     */
    private Log getLog() {
        return LOG;
    } 
}
