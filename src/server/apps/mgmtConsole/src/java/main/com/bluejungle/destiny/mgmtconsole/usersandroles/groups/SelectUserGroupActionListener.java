package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JSF Action Listener used to select a user group from the left hand group list
 * menu
 * 
 * @author sgoldstein
 */
public class SelectUserGroupActionListener extends UserGroupsViewActionListenerBase {

    public static final String SELECTED_USER_GROUP_ID_PARAM_NAME = "selectedUserGroupId";
    private static final Log LOG = LogFactory.getLog(SelectUserGroupActionListener.class.getName());

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        String selectedUserGroupId = getRequestParameter(SELECTED_USER_GROUP_ID_PARAM_NAME, null);

        if (selectedUserGroupId == null) {
            throw new NullPointerException("Selected User Group ID parameter not found.");
        }

        IUserGroupsViewBean userGroupsViewBean = getUserGroupsViewBean();
        userGroupsViewBean.setSelectedUserGroup(selectedUserGroupId);
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

