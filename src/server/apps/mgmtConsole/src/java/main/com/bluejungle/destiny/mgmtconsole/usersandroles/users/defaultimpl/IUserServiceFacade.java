/*
 * Created on May 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl;

import java.rmi.RemoteException;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.DuplicateUserException;
import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserManagementMetadata;
import com.bluejungle.destiny.services.policy.types.DMSUserData;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;
import com.bluejungle.destiny.services.policy.types.SubjectDTOList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;

/**
 * The user service facade provides easy access to user information through the
 * User Web Service
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/defaultimpl/IUserServiceFacade.java#1 $
 */

public interface IUserServiceFacade {

    public static final String COMPONENT_NAME = "UserServiceFacadeComponent";

    /**
     * Retrieve the available users for the specified search bucket search spec
     * 
     * @param searchSpec
     * @return the matching available users
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     */
    public SubjectDTOList getAvailableUsersForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws ServiceNotReadyFault, RemoteException, UserRoleServiceException;

    /**
     * Retrieve the available users for the specified free form search
     * specification
     * 
     * @param freeFormSearchSpec
     * @return the matching available users
     * @throws ServiceNotReadyFault
     * @throws RemoteException
     */
    public SubjectDTOList getAvailableUsersForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ServiceNotReadyFault, RemoteException, UserRoleServiceException;

    /**
     * Retrieve all users as a list of UserDTOList
     * 
     * @return all users in the system
     * @throws RemoteException
     */
    public UserDTOList getAllUsers() throws RemoteException, UserRoleServiceException;

    /**
     * Retrieve a user by ID
     * 
     * @param userId
     * @return the user associated with the specified id
     * @throws RemoteException
     */
    public UserDTO getUser(Long userId) throws RemoteException, UserRoleServiceException;

    /**
     * Retrieve all users which match the specified search spec
     * 
     * @param searchSpec
     * @return the matching users
     */
    public UserDTOList getUsersForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws ServiceNotReadyFault, RemoteException, UserRoleServiceException;

    /**
     * Retrieve all users which match the specified free form search spec
     * 
     * @param searchSpec
     * @return the matching users
     */
    public UserDTOList getUsersForFreeFormSearchSpec(IFreeFormSearchSpec searchSpec) throws ServiceNotReadyFault, RemoteException, UserRoleServiceException;

    /**
     * Get detailed user data for the specified subject dto
     * 
     * @param userSubject
     *            the user for which to retrieve detailed user data
     * @return user data for the specified subject dto
     * @throws RemoteException
     */
    public DMSUserData getUserData(SubjectDTO userSubject) throws RemoteException, UserRoleServiceException;

    /**
     * Update the specified user
     * 
     * @param userSubject
     *            the associated user subject
     * @param userData
     *            the detailed user data
     * @throws RemoteException
     */
    public void updateUser(UserDTO userSubject, DMSUserData userData) throws RemoteException, UserRoleServiceException;

    /**
     * Delete the specified user
     * 
     * @param userToDelete
     *            the user to delete
     * @throws RemoteException
     */
    public void deleteUser(SubjectDTO userToDelete) throws RemoteException, UserRoleServiceException;

    /**
     * Import the available users into the local repository
     * 
     * @param usersToImport
     */
    public UserDTOList importAvailableUsers(SubjectDTO[] usersToImport) throws RemoteException, UserRoleServiceException;

    /**
     * Creates a new user in the local repository
     * 
     * @param newUser
     *            user to create
     * @throws RemoteException
     */
    public UserDTO createUser(UserDTO newUser, DMSUserData userData) throws RemoteException, DuplicateUserException;

    /**
     * Retrieve meta data about the Destiny user management system
     * 
     * @return meta data about the Destiny user management system
     * @throws RemoteException 
     */
    public UserManagementMetadata getUserManagementMetadata() throws RemoteException, UserRoleServiceException;
}
