/*
 * Created on Sep 8, 2005
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
 * @author sasha
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/CreateUserActionListener.java#1 $:
 */

public class CreateUserActionListener extends UsersViewActionListenerBase {
    private static final Log LOG = LogFactory.getLog(CreateUserActionListener.class.getName());

    private static final String USER_CREATE_FAILED_ERROR_MSG = "users_and_roles_users_create_failed_error_message_detail";
    
    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        IUsersViewBean usersViewBean = getUsersViewBean();
        
        try {
            usersViewBean.createUser();
        } catch (UsersException exception) {
            addErrorMessage(USER_CREATE_FAILED_ERROR_MSG);
            getLog().error("Failed to create user", exception);
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
