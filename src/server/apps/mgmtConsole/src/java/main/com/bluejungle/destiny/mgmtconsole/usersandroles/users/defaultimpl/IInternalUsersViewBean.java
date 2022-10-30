/*
 * Created on May 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUsersViewBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.UsersException;
import com.bluejungle.destiny.services.management.UserRoleServiceException;

import java.rmi.RemoteException;

/**
 * Internal extension of the IUsersViewBean interface
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/defaultimpl/IInternalUsersViewBean.java#2 $
 */

public interface IInternalUsersViewBean extends IUsersViewBean {

    /**
     * Reset the state of the Users view bean
     */
    void reset();

    /**
     * Reset and select the specified user
     * 
     * @param userToSelect
     *            the user to select
     * @throws RemoteException
     * @throws UsersException 
     */
    void resetAndSelectUser(long userToSelect) throws RemoteException, UsersException, UserRoleServiceException;
}
