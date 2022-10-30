package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.event.ActionEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RemoveSelectedPrincipalsActionListener extends UserGroupsViewActionListenerBase {

    private static final Log LOG = LogFactory.getLog(RemoveSelectedPrincipalsActionListener.class);

    private static final String PRINCIPALS_TO_REMOVE_PARAM_PREFIX = "remove_principal_";

    private static final String PRINCIPAL_REMOVAL_SUCCESS_MSG = "users_and_roles_user_groups_principals_removal_success_message_detail";
    private static final String PRINCIPAL_REMOVAL_FAILED_ERROR_MSG = "users_and_roles_user_groups_principals_removal_failed_error_message_detail";

    public void processAction(ActionEvent event) {
        Set principalsToRemove = new HashSet();

        Map requestParameterForRemoval = getRequestParametersWithPrefix(PRINCIPALS_TO_REMOVE_PARAM_PREFIX);
        Iterator requestParameterForRemovalIterator = requestParameterForRemoval.entrySet().iterator();
        while (requestParameterForRemovalIterator.hasNext()) {
            Map.Entry nextPrincipalToRemoveParam = (Map.Entry) requestParameterForRemovalIterator.next();
            String nextPrincipalToRemoveParamKey = (String) nextPrincipalToRemoveParam.getKey();
            String memberId = nextPrincipalToRemoveParamKey.substring(PRINCIPALS_TO_REMOVE_PARAM_PREFIX.length());
            principalsToRemove.add(Long.valueOf(memberId));
        }

        if (!principalsToRemove.isEmpty()) {
            try {
                getUserGroupsViewBean().removeDefaultAccessRightsPrincipalsFromSelectedUserGroup(principalsToRemove);
                addSuccessMessage(PRINCIPAL_REMOVAL_SUCCESS_MSG);
            } catch (UserGroupsViewException exception) {
                addErrorMessage(PRINCIPAL_REMOVAL_FAILED_ERROR_MSG);
                getLog().error("Failed to remove principals from selected user group", exception);
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
