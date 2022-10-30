/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles;

import javax.faces.model.DataModel;

/**
 * The Roles View bean is utilized by the display layer to retrieve information
 * necessary to render the roles administration view within the management
 * console
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/IRolesViewBean.java#1 $
 */

public interface IRolesViewBean {

    /**
     * Retrieve the roles to display. The list contains all roles defined within
     * the system
     * 
     * @return a DataModel interface to the list of all roles in the system
     */
    public DataModel getRoles();

    /**
     * Retrieve the currently selected role
     * 
     * @return the currently selected role
     */
    public IRoleBean getSelectedRole();

    /**
     * Set the role with the specified id to be the currently selected role
     * 
     * @param selectedRoleId
     *            the id of the role to select
     */
    public void setSelectedRole(long selectedRoleId);

    /**
     * Save changes to the selected role
     * 
     * @throws RolesException
     *             if an error occurs while saving the selected role
     */
    public void saveSelectedRole() throws RolesException;
}