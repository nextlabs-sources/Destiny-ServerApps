/*
 * Created on Sep 18, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import java.rmi.RemoteException;

import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import org.apache.commons.codec.binary.Base64;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.UserServiceFacadeImpl;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.framework.comp.ComponentManagerFactory;

/**
 * Utility methods shared by the data picker implementations in the user groups
 * view
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/datapicker/UserGroupDataPickerUtils.java#1 $
 */

final class UserGroupDataPickerUtils {

    /**
     * Retrieve the users for the specified search bucket search spec
     * 
     * @param searchSpec
     *            a search bucket specification
     * @return the matching users
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     */
    static UserDTO[] getUsersForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws ServiceNotReadyFault, RemoteException, UserRoleServiceException {
        IUserServiceFacade userServiceFacade = getUserServiceFacade();
        UserDTOList matchingUsersList = userServiceFacade.getUsersForSearchBucketSearchSpec(searchSpec);
        UserDTO[] matchingUsers = matchingUsersList.getUsers();
        if (matchingUsers == null) {
            matchingUsers = new UserDTO[0];
        }

        return matchingUsers;
    }

    /**
     * Retrieve the users for the free form search specification
     * 
     * @param searchSpec
     *            the free form search specification
     * @return the matchins users
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     */
    static UserDTO[] getUsersForFreeFormSearchSpec(IFreeFormSearchSpec searchSpec) throws ServiceNotReadyFault, RemoteException, UserRoleServiceException {
        IUserServiceFacade userServiceFacade = getUserServiceFacade();
        UserDTOList matchingUsersList = userServiceFacade.getUsersForFreeFormSearchSpec(searchSpec);
        UserDTO[] matchingUsers = matchingUsersList.getUsers();
        if (matchingUsers == null) {
            matchingUsers = new UserDTO[0];
        }

        return matchingUsers;
    }

    /**
     * Retrieve the User Service Facade
     * 
     * @return the user service facade
     */
    private static IUserServiceFacade getUserServiceFacade() {
        return ComponentManagerFactory.getComponentManager().getComponent(UserServiceFacadeImpl.class);
    }

    /**
     * Returns a base 64 encoding of the given byte array
     * 
     * @param id
     * @return
     */
    public static String getBase64EncodingOf(byte[] id) {
        byte[] base64EncodedExternalId = Base64.encodeBase64(id);
        String idStr = new String(base64EncodedExternalId);
        return idStr;
    }
}
