/*
 * Created on May 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles;

import com.bluejungle.destiny.mgmtconsole.shared.MgmtConsoleActionListenerBase;

/**
 * Base action listener for the roles view. Provides reusable functionality for
 * all roles view action listeners
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/RolesViewActionListenerBase.java#1 $
 */

public abstract class RolesViewActionListenerBase extends MgmtConsoleActionListenerBase {

    public static final String ROLES_VIEW_BEAN_NAME_PARAM_NAME = "rolesViewBeanName";

    /**
     * Retrieve the IRoleViewBean associated with the invoking page. The bean is
     * found through a request parameter with the name,
     * {@see #ROLES_VIEW_BEAN_NAME_PARAM_NAME}, specifying the name of the bean
     * 
     * @return the IRolesViewBean associated with the invoking page
     */
    protected IRolesViewBean getRolesViewBean() {
        String rolesViewBeaName = getRequestParameter(ROLES_VIEW_BEAN_NAME_PARAM_NAME, null);

        if (rolesViewBeaName == null) {
            throw new NullPointerException("Roles view bean name parameter not found.");
        }

        IRolesViewBean rolesViewBean = (IRolesViewBean) getManagedBeanByName(rolesViewBeaName);
        if (rolesViewBean == null) {
            throw new IllegalArgumentException("Roles View Bean instance with bean name, " + rolesViewBeaName + ", not found");
        }
        return rolesViewBean;
    }

}