/*
 * Created on May 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

import com.bluejungle.destiny.mgmtconsole.shared.MgmtConsoleActionListenerBase;

/**
 * Base action listener for the users view. Provides reusable functionality for
 * all users view action listeners
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/UsersViewActionListenerBase.java#1 $
 */

public abstract class UsersViewActionListenerBase extends MgmtConsoleActionListenerBase {

    public static final String USERS_VIEW_BEAN_NAME_PARAM_NAME = "usersViewBeanName";

    /**
     * Retrieve the IUserViewBean associated with the invoking page. The bean is
     * found through a request parameter with the name,
     * {@see #USERS_VIEW_BEAN_NAME_PARAM_NAME}, specifying the name of the bean
     * 
     * @return the IUsersViewBean associated with the invoking page
     */
    protected IUsersViewBean getUsersViewBean() {
        String usersViewBeaName = getRequestParameter(USERS_VIEW_BEAN_NAME_PARAM_NAME, null);

        if (usersViewBeaName == null) {
            throw new NullPointerException("Users view bean name parameter not found.");
        }

        IUsersViewBean usersViewBean = (IUsersViewBean) getManagedBeanByName(usersViewBeaName);
        if (usersViewBean == null) {
            throw new IllegalArgumentException("Users View Bean instance with bean name, " + usersViewBeaName + ", not found");
        }
        return usersViewBean;
    }
}
