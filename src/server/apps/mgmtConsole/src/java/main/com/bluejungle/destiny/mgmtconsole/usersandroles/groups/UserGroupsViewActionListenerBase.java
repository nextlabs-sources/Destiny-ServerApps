package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import com.bluejungle.destiny.mgmtconsole.shared.MgmtConsoleActionListenerBase;

/**
 * Base JSF Action Listener for the User Group View
 * 
 * @author sgoldstein
 */
public abstract class UserGroupsViewActionListenerBase extends MgmtConsoleActionListenerBase {

    public static final String USER_GROUPS_VIEW_BEAN_NAME_PARAM_NAME = "userGroupsViewBeanName";

    /**
     * Retrive the User Groups View Bean as specified as a request parameter
     * with the name, {@see #USER_GROUPS_VIEW_BEAN_NAME_PARAM_NAME}
     * 
     * @return the User Groups View Bean as specified as a request parameter
     *         with the name, {@see #USER_GROUPS_VIEW_BEAN_NAME_PARAM_NAME}
     */
    public IUserGroupsViewBean getUserGroupsViewBean() {
        String userGroupsViewBeaName = getRequestParameter(USER_GROUPS_VIEW_BEAN_NAME_PARAM_NAME, null);

        if (userGroupsViewBeaName == null) {
            throw new NullPointerException("User groups view bean name parameter not found.");
        }

        IUserGroupsViewBean userGroupsViewBean = (IUserGroupsViewBean) getManagedBeanByName(userGroupsViewBeaName);
        if (userGroupsViewBean == null) {
            throw new IllegalArgumentException("User Groups View Bean instance with bean name, " + userGroupsViewBeaName + ", not found");
        }
        return userGroupsViewBean;
    }
}

