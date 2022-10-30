package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.service.AppUserMgmtService;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import net.sf.hibernate.HibernateException;

public class UnlockSelectedUserActionListener extends UsersViewActionListenerBase {
    private static final Log LOG = LogFactory.getLog(DeleteSelectedUserActionListener.class);
    
    private static final String UNLOCK_USER_ID_PARAM_NAME = "unlockUserId";
    private static final String USER_UNLOCK_SUCCESS_MSG = "users_and_roles_users_unlock_success_message_detail";
    private static final String USER_UNLOCK_FAILED_ERROR_MSG = "users_and_roles_users_unlock_failed_error_message_detail";
    private static final String USERS_VIEW_ACTION = "usersAndRolesUsers";
    
    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        String unlockUserId = getRequestParameter(UNLOCK_USER_ID_PARAM_NAME, null);
        if (unlockUserId == null) {
            throw new NullPointerException("unlockUserId parameter not found.");
        }
        
        Long userId = Long.parseLong(unlockUserId);
        try {
        	AppUserMgmtService userService = new AppUserMgmtService();
        	userService.unlockUser(userId);
        	
            addSuccessMessage(USER_UNLOCK_SUCCESS_MSG);                        
        } catch (HibernateException exception) {
            addErrorMessage(USER_UNLOCK_FAILED_ERROR_MSG);
            getLog().error("Failed to unlock user", exception);
        } catch (SQLException exception) {
            addErrorMessage(USER_UNLOCK_FAILED_ERROR_MSG);
            getLog().error("Failed to unlock user", exception);
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
