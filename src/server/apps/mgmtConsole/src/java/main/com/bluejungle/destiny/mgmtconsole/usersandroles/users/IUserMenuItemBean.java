/*
 * Created on May 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

/**
 * IUserMenuItemBean represents a menu item in the users list menu
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/IUserMenuItemBean.java#1 $
 */

public interface IUserMenuItemBean {
    /**
     * Retrieve the user id
     * 
     * @return the user id
     */
    public long getUserId();

    /**
     * Retrieve the user title
     * 
     * @return the user title
     */
    public String getUserTitle();
    
    /**
     * Retrieve the display value tool-tip associated with this selectable item
     * 
     * @return the display value tool-tip associated with this selectable item
     */
    public String getUserTitleToolTip();
}
