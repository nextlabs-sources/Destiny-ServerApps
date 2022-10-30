/*
 * Created on May 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

/**
 * IDefaultAccessAssignmentBean specifies the default access assignments given to a
 * particular user when an object is created by another particular user
 * 
 * @author pkeni
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/IRoleAssignment.java#1 $
 */

public interface IRoleAssignment {

    /**
     * Retrieve the title of a Role for the user
     * 
     * @return the title of  Role for the user
     */
    public String getRoleTitle();

    /**
     * Determine if the is granted
     * 
     * @return true if the role is assigned to this user
     */
    public boolean isRoleAssigned ();

}
