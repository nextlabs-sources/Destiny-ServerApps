/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl;

import java.rmi.RemoteException;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.UsersException;
import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.policy.types.DMSUserData;

/**
 * Extension of the IUser interface for internal use
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/defaultimpl/IInternalUserBean.java#1 $
 */

public interface IInternalUserBean extends IUserBean {

    /**
     * Retrieve the Subject DTO associated with this user bean
     * 
     * @return the Subject DTO associated with this user bean
     */
    public UserDTO getWrappedUserDTO();

    /**
     * Retrieve the user data associated with this user bean
     * 
     * @return the user data associated with this user bean
     */
    public DMSUserData getWrappedDMSUserData();

    
    public void save() throws UsersException, RemoteException, UserRoleServiceException;

}
