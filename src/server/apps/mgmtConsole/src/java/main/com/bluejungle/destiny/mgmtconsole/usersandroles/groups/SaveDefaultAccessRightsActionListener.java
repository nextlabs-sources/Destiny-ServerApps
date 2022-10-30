package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JSF Listener used to save the default access rights settingson the selected
 * group in the user groups view
 * 
 * @author sgoldstein
 */
public class SaveDefaultAccessRightsActionListener extends UserGroupsViewActionListenerBase {

    private static final Log LOG = LogFactory.getLog(SaveDefaultAccessRightsActionListener.class.getName());

    private static final String DEFAULT_ACCESS_RIGHTS_SAVE_SUCCESS_MSG = "users_and_roles_user_groups_default_access_rights_save_success_message_detail";
    private static final String DEFAULT_ACCESS_RIGHTS_SAVED_FAILED_ERROR_MSG = "users_and_roles_user_groups_default_access_rights_save_failed_error_message_detail";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {

        try {
            getUserGroupsViewBean().saveDefaultAccessRightsForSelectedUserGroup();
            addSuccessMessage(DEFAULT_ACCESS_RIGHTS_SAVE_SUCCESS_MSG);
        } catch (UserGroupsViewException exception) {
            addErrorMessage(DEFAULT_ACCESS_RIGHTS_SAVED_FAILED_ERROR_MSG);
            getLog().error("Failed to save user group changes", exception);
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
