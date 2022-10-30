package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JSF Action Listener used to delete the selected user group
 * 
 * @author sgoldstein
 */
public class DeleteSelectedGroupActionListener extends UserGroupsViewActionListenerBase {

    private static final Log LOG = LogFactory.getLog(DeleteSelectedGroupActionListener.class);

    private static final String USER_GROUP_DELETE_SUCCESS_MSG = "users_and_roles_user_groups_delete_success_message_detail";
    private static final String USER_GROUP_DELETE_FAILED_ERROR_MSG = "users_and_roles_user_groups_delete_failed_error_message_detail";
    private static final String USER_GROUPS_VIEW_ACTION = "usersAndRolesUserGroups";
    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        IUserGroupsViewBean userGroupsViewBean = getUserGroupsViewBean();

        try {
            userGroupsViewBean.deleteSelectedUserGroup();
            addSuccessMessage(USER_GROUP_DELETE_SUCCESS_MSG);
        } catch (UserGroupsViewException exception) {
            addErrorMessage(USER_GROUP_DELETE_FAILED_ERROR_MSG);
            getLog().error("Failed to delete user group", exception);
        }
        
        // Set the action to ensure a redirect or forward based on navigation case
        super.setResponseAction(USER_GROUPS_VIEW_ACTION, event);
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

