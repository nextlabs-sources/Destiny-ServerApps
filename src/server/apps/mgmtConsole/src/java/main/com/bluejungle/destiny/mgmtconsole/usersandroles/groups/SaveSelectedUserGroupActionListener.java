package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JSF Listener used to save the selected group in the user groups view
 * 
 * @author sgoldstein
 */
public class SaveSelectedUserGroupActionListener extends UserGroupsViewActionListenerBase {

    private static final Log LOG = LogFactory.getLog(SaveSelectedUserGroupActionListener.class.getName());

    private static final String USER_GROUP_SAVE_SUCCESS_MSG = "users_and_roles_user_groups_user_group_save_success_message_detail";
    private static final String USER_GROUP_SAVED_FAILED_ERROR_MSG = "users_and_roles_user_groups_user_group_save_failed_error_message_detail";
    private static final String USER_GROUP_TITLE_NOT_UNIQUE_ERROR_MSG = "users_and_roles_user_groups_user_group_title_not_unique_error_message_detail";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {

        try {
            getUserGroupsViewBean().saveSelectedUserGroup();
            addSuccessMessage(USER_GROUP_SAVE_SUCCESS_MSG);
        } catch (NonUniqueUserGroupTitleException exception) {
            addErrorMessage(USER_GROUP_TITLE_NOT_UNIQUE_ERROR_MSG);
            getLog().error("Failed to save user group changes.  Title not unique", exception);
        } catch (UserGroupsViewException exception) {
            addErrorMessage(USER_GROUP_SAVED_FAILED_ERROR_MSG);
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
