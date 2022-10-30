/*
 * Created on Sep 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import com.bluejungle.destiny.services.management.types.UserGroupInfo;

/**
 * Extension of the IInternalUserGroupBean used to differentiate a Newly created
 * user group from an existing one
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/IInternalNewUserGroupBean.java#1 $
 */
public interface IInternalNewUserGroupBean extends IInternalUserGroupBean {

    /**
     * Retrieve the wrapped user group info instance
     * 
     * @return the wrapped user group info instance
     */
    UserGroupInfo getWrappedUserGroupInfo();

}