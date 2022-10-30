/*
 * Created on Sep 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

import com.bluejungle.framework.exceptions.SingleErrorBlueJungleException;

/**
 * Generic UserGroupsViewException
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/UserGroupsViewException.java#1 $
 */

public class UserGroupsViewException extends SingleErrorBlueJungleException {

    /**
     * Create an instance of UserGroupsViewException
     *  
     */
    public UserGroupsViewException() {
        super();
    }

    /**
     * Create an instance of UserGroupsViewException
     * 
     * @param cause
     */
    public UserGroupsViewException(Throwable cause) {
        super(cause);
    }

}