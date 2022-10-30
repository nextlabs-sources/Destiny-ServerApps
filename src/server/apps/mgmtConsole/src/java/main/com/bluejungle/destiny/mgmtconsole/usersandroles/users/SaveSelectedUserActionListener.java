/*
 * Created on May 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The SaveSelectedUserActionListener is invoked by the display layer to save
 * changes to the selected user
 * 
 * @author pkeni
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/SaveSelectedUserActionListener.java#1 $
 */

public class SaveSelectedUserActionListener extends UsersViewActionListenerBase {
    private static final Log LOG = LogFactory.getLog(SaveSelectedUserActionListener.class);
    
    private static final String USER_SAVE_SUCCESS_MSG = "users_and_roles_users_save_success_message_detail";
    private static final String USER_SAVE_FAILED_ERROR_MSG = "users_and_roles_users_save_failed_error_message_detail";
    private static final String DUPLICATE_LOGIN_MSG = "users_and_roles_user_creation_save_duplicate_login_message_detail";
     /**
      * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
      */
     public void processAction(ActionEvent event) throws AbortProcessingException {       
         try {
             getUsersViewBean().saveSelectedUser();
             addSuccessMessage(USER_SAVE_SUCCESS_MSG);
         } catch (DuplicateUserException due) {
             addErrorMessage(DUPLICATE_LOGIN_MSG);
             getLog().error(due);
         } catch (UsersException exception) {
             addErrorMessage(USER_SAVE_FAILED_ERROR_MSG);
             getLog().error("Failed to save user changes", exception);
        }
    }
         
     /**
      * Retrieve a Logger
      * @return a Logger
      */
     private Log getLog() {
         return LOG;
     } 
}
