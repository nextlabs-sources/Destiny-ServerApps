/*
 * Created on May 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles;

/**
 * IRoleMenuItemBean represents a menu item in the roles list menu
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/IRoleMenuItemBean.java#1 $
 */

public interface IRoleMenuItemBean {
    /**
     * Retrieve the role id
     * 
     * @return the role id
     */
    public long getRoleId();

    /**
     * Retrieve the role title
     * 
     * @return the role title
     */
    public String getRoleTitle();
}
