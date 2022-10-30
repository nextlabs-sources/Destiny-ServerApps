/*
 * Created on May 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl;

import java.rmi.RemoteException;

import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.policy.types.DMSRoleData;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;
import com.bluejungle.destiny.services.policy.types.SubjectDTOList;

/**
 * The role service facade provides easy access to role information through the
 * Role Web Service
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/defaultimpl/IRoleServiceFacade.java#1 $
 */

public interface IRoleServiceFacade {

    public static final String COMPONENT_NAME = "RoleServiceFacadeComponent";

    /**
     * Retrieve all roles as a list of SubjectDTOList
     * 
     * @return all roles in the system
     * @throws RemoteException
     */
    public SubjectDTOList getAllRoles() throws RemoteException, UserRoleServiceException;

    /**
     * Get detailed role data for the specified subject dto
     * 
     * @param roleSubject
     *            the role for which to retrieve detailed role data
     * @return role data for the specified subject dto
     * @throws RemoteException
     * @throws ServiceException
     */
    public DMSRoleData getRoleData(SubjectDTO roleSubject) throws RemoteException, UserRoleServiceException;

    /**
     * Update the specified role
     * 
     * @param roleSubject
     *            the associated role subject
     * @param roleData
     *            the detailed role data
     * @throws RemoteException
     */
    public void updateRole(SubjectDTO roleSubject, DMSRoleData roleData) throws RemoteException, UserRoleServiceException;
}