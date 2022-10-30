package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RemoveSelectedMembersActionListener extends UserGroupsViewActionListenerBase {

    private static final Log LOG = LogFactory.getLog(RemoveSelectedMembersActionListener.class);

    private static final String MEMBERS_TO_REMOVE_PARAM_PREFIX = "remove_member_";

    private static final String USER_GROUP_MEMBERS_REMOVAL_SUCCESS_MSG = "users_and_roles_user_groups_members_removal_success_message_detail";
    private static final String USER_GROUP_MEMBERS_REMOVAL_FAILED_ERROR_MSG = "users_and_roles_user_groups_members_remove_failed_error_message_detail";

    public void processAction(ActionEvent event) {
        Set membersToRemove = new HashSet();

        Map requestParameterForRemoval = getRequestParametersWithPrefix(MEMBERS_TO_REMOVE_PARAM_PREFIX);
        Iterator requestParameterForRemovalIterator = requestParameterForRemoval.entrySet().iterator();
        while (requestParameterForRemovalIterator.hasNext()) {
            Map.Entry nextMemberToRemoveParam = (Map.Entry) requestParameterForRemovalIterator.next();
            String nextMemberToRemoveParamKey = (String) nextMemberToRemoveParam.getKey();
            String memberId = nextMemberToRemoveParamKey.substring(MEMBERS_TO_REMOVE_PARAM_PREFIX.length());
            membersToRemove.add(Long.valueOf(memberId));
        }

        if (!membersToRemove.isEmpty()) {
            try {
                getUserGroupsViewBean().removeMembersFromSelectedUserGroup(membersToRemove);
                addSuccessMessage(USER_GROUP_MEMBERS_REMOVAL_SUCCESS_MSG);
            } catch (UserGroupsViewException exception) {
                addErrorMessage(USER_GROUP_MEMBERS_REMOVAL_FAILED_ERROR_MSG);
                getLog().error("Failed to delete user group", exception);
            }
        }
    }

    /**
     * Retrieve the LOG wrapper
     * 
     * @return the LOG wrapper
     */
    private Log getLog() {
        return LOG;
    }
}
