/*
 * Created on May 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl;

import java.math.BigInteger;
import java.rmi.RemoteException;

import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.services.management.DuplicateLoginNameException;
import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.management.UserRoleServiceStub;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserManagementMetadata;
import com.bluejungle.destiny.services.management.types.UserQuerySpec;
import com.bluejungle.destiny.services.management.types.UserQueryTerm;
import com.bluejungle.destiny.services.management.types.UserQueryTermSet;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.framework.types.RelationalOpDTO;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.DuplicateUserException;
import com.bluejungle.destiny.services.policy.types.DMSUserData;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;
import com.bluejungle.destiny.services.policy.types.SubjectDTOList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.LifestyleType;

import org.apache.axis2.databinding.types.NonNegativeInteger;

/**
 * Default implementation of the IUserServiceFacade
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/defaultimpl/UserServiceFacadeImpl.java#7 $
 */

public class UserServiceFacadeImpl implements IUserServiceFacade, IHasComponentInfo<UserServiceFacadeImpl> {

    private static final Log LOG = LogFactory.getLog(UserServiceFacadeImpl.class);
    
    private static final ComponentInfo<UserServiceFacadeImpl> COMPONENT_INFO = 
    	new ComponentInfo<UserServiceFacadeImpl>(
    			COMPONENT_NAME, 
    			UserServiceFacadeImpl.class, 
    			IUserServiceFacade.class, 
    			LifestyleType.SINGLETON_TYPE);
    private static final String USER_SERVICE_LOCATION_SERVLET_PATH = "/services/UserRoleService";

    private UserRoleServiceStub userService;

    /**
     * @see com.bluejungle.framework.comp.IHasComponentInfo#getComponentInfo()
     */
    public ComponentInfo<UserServiceFacadeImpl> getComponentInfo() {
        return COMPONENT_INFO;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getAvailableUsersForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public SubjectDTOList getAvailableUsersForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws RemoteException, UserRoleServiceException {
        Character[] searchBucketCharacters = searchSpec.getCharactersInBucket();

        UserQueryTerm[] externalUserQueryTerms = new UserQueryTerm[searchBucketCharacters.length];
        for (int i = 0; i < searchBucketCharacters.length; i++) {
            externalUserQueryTerms[i] = getLastnameStartsWithQueryTerm(String.valueOf(searchBucketCharacters[i]));
        }

        return runExternalUserQuery(externalUserQueryTerms, searchSpec.getMaximumResultsToReturn());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getAvailableUsersForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public SubjectDTOList getAvailableUsersForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws RemoteException, UserRoleServiceException {
        UserQueryTerm[] externalUserQueryTerms = { getLastnameStartsWithQueryTerm(freeFormSearchSpec.getFreeFormSeachString()) };

        return runExternalUserQuery(externalUserQueryTerms, freeFormSearchSpec.getMaximumResultsToReturn());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#importAvailableUsers(com.bluejungle.destiny.services.policy.types.SubjectDTO[])
     */
    public UserDTOList importAvailableUsers(SubjectDTO[] usersToImport) throws RemoteException, com.bluejungle.destiny.services.management.UserRoleServiceException {
        if (usersToImport == null) {
            throw new NullPointerException("usersToImport cannot be null.");
        }

        SubjectDTOList subjectList = new SubjectDTOList();
        subjectList.setSubjects(usersToImport);

        UserDTOList importedUsers = getUserService().importExternalUsers(subjectList);
        LOG.info("External users imported,  [ No of imported users :" + (importedUsers.getUsers() != null ? importedUsers.getUsers().length : 0) + "] by " + this.getLoginUserName());
        return importedUsers;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getAllUsers()
     */
    public UserDTOList getAllUsers() throws RemoteException, com.bluejungle.destiny.services.management.UserRoleServiceException {
        return getUserService().getAllUsers();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getUser(java.lang.Long)
     */
    public UserDTO getUser(Long userId) throws RemoteException, UserRoleServiceException {
        if (userId == null) {
            throw new NullPointerException("userId cannot be null.");
        }

        ID id = new ID();
        id.setID(BigInteger.valueOf(userId.longValue()));

        return getUserService().getUser(id);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getUsersForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public UserDTOList getUsersForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws RemoteException, UserRoleServiceException {
        if (searchSpec == null) {
            throw new NullPointerException("searchSpec cannot be null.");
        }

        Character[] searchBucketCharacters = searchSpec.getCharactersInBucket();

        UserQueryTerm[] externalUserQueryTerms = new UserQueryTerm[searchBucketCharacters.length];
        for (int i = 0; i < searchBucketCharacters.length; i++) {
            externalUserQueryTerms[i] = getLastnameStartsWithQueryTerm(String.valueOf(searchBucketCharacters[i]));
        }

        return runUserQuery(externalUserQueryTerms, searchSpec.getMaximumResultsToReturn());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getUsersForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public UserDTOList getUsersForFreeFormSearchSpec(IFreeFormSearchSpec searchSpec) throws RemoteException, UserRoleServiceException {
        if (searchSpec == null) {
            throw new NullPointerException("searchSpec cannot be null.");
        }

        UserQueryTerm[] externalUserQueryTerms = { getLastnameStartsWithQueryTerm(searchSpec.getFreeFormSeachString()) };

        return runUserQuery(externalUserQueryTerms, searchSpec.getMaximumResultsToReturn());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getUserData(com.bluejungle.destiny.services.policy.types.SubjectDTO)
     */
    public DMSUserData getUserData(SubjectDTO userSubject) throws RemoteException, UserRoleServiceException {
        if (userSubject == null) {
            throw new NullPointerException("userSubject cannot be null.");
        }

        return getUserService().getUserData(userSubject);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#updateUser(com.bluejungle.destiny.services.policy.types.SubjectDTO,
     *      com.bluejungle.destiny.services.policy.types.DMSUserData)
     */
    public void updateUser(UserDTO userSubject, DMSUserData userData) throws RemoteException, UserRoleServiceException {
        if (userSubject == null) {
            throw new NullPointerException("userSubject cannot be null.");
        }

        if (userData == null) {
            throw new NullPointerException("userData cannot be null.");
        }

        getUserService().setUserData(userSubject, userData);
        LOG.info("Application user modified,  [username :" + trimUsername(userSubject.getUniqueName()) +
                ", name :" + userSubject.getFirstName() + " " + userSubject.getLastName() + "] by " + trimUsername(this.getLoginUserName()));
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#deleteUser(com.bluejungle.destiny.services.policy.types.SubjectDTO)
     */
    public void deleteUser(SubjectDTO userToDelete) throws RemoteException, UserRoleServiceException {
        if (userToDelete == null) {
            throw new NullPointerException("userToDelete cannot be null.");
        }

        getUserService().deleteFromUsers(userToDelete);
        LOG.info("Application user deleted,  [username :" + trimUsername(userToDelete.getUniqueName()) + ", name :" + userToDelete.getName() +"] by " + trimUsername(this.getLoginUserName()));
    }

    /**
     * @param string
     * @return
     */
    private UserQueryTerm getLastnameStartsWithQueryTerm(String value) {
        return getLastNameQueryTerm(value, RelationalOpDTO.starts_with);
    }

    /**
     * @param value
     * @param starts_with
     * @return
     */
    private UserQueryTerm getLastNameQueryTerm(String value, RelationalOpDTO operator) {
        UserQueryTerm userQueryTerm = new UserQueryTerm();
        userQueryTerm.setQueryField("lastname");
        userQueryTerm.setOperator(operator);
        userQueryTerm.setValue(value);

        return userQueryTerm; // FIX ME!!!
    }

    /**
     * 
     * @param userQueryTerms
     * @return
     * @throws RemoteException
     * @throws ServiceException
     */
    private UserDTOList runUserQuery(UserQueryTerm[] userQueryTerms, int maxResultsToReturn) throws RemoteException, UserRoleServiceException {
        UserRoleServiceStub userService = getUserService();
        UserQueryTermSet userQueryTermSet = new UserQueryTermSet();
        userQueryTermSet.setUserQueryTerm(userQueryTerms);

        NonNegativeInteger fetchSize = new NonNegativeInteger(String.valueOf(maxResultsToReturn));
        UserQuerySpec userQuerySpec = new UserQuerySpec();
        userQuerySpec.setUserQueryTermSet(userQueryTermSet);
        userQuerySpec.setMaxResults(fetchSize);

        return userService.runUserQuery(userQuerySpec).getMatchingUsers();
    }

    /**
     * @param dmsUserQueryTerms
     * @return
     * @throws RemoteException
     * @throws ServiceException
     */
    private SubjectDTOList runExternalUserQuery(UserQueryTerm[] externalUserQueryTerms, int maxResultsToReturn) throws RemoteException, UserRoleServiceException {
        UserRoleServiceStub userService = getUserService();
        UserQueryTermSet externalUserQueryTermSet = new UserQueryTermSet();
        externalUserQueryTermSet.setUserQueryTerm(externalUserQueryTerms);

        NonNegativeInteger fetchSize = new NonNegativeInteger(String.valueOf(maxResultsToReturn));
        UserQuerySpec userQuerySpec = new UserQuerySpec();
        userQuerySpec.setUserQueryTermSet(externalUserQueryTermSet);
        userQuerySpec.setMaxResults(fetchSize);

        return userService.runExternalUserQuery(userQuerySpec).getMatchingAgents();
    }

    /**
     * Retrieve the User and User Service port
     * 
     * @return the User and User Service port
     * @throws ServiceException
     *             if the service port retrieval fails
     */
    private UserRoleServiceStub getUserService() throws AxisFault {
        if (this.userService == null) {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            IConfiguration mainCompConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = (String) mainCompConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += USER_SERVICE_LOCATION_SERVLET_PATH;

            this.userService = new UserRoleServiceStub(location);
        }

        return this.userService;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#createUser(com.bluejungle.destiny.services.management.types.NewUser)
     */
    public UserDTO createUser(UserDTO newUser, DMSUserData data) throws RemoteException, DuplicateUserException {
        UserRoleServiceStub userService = getUserService();

        UserDTO userToReturn = null;
        try {
            userToReturn = userService.createUser(newUser, data);
            LOG.info("Application user created, [username :" + trimUsername(userToReturn.getUid()) + ", name :" + userToReturn.getFirstName() + " " + userToReturn.getLastName() + "] by " + trimUsername(this.getLoginUserName()));
        } catch (DuplicateLoginNameException | UserRoleServiceException e) {
            throw new DuplicateUserException("Duplicate User", e);
        }

        return userToReturn;
    }

    private String getLoginUserName() {
        return AppContext.getContext().getRemoteUser().getUsername();
    }
    
    private String trimUsername(String username) {
    	if(username != null && username.indexOf("@") > -1) {
    		return username.substring(0, username.lastIndexOf("@"));
    	}
    	
    	return username;
    }
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserServiceFacade#getUserManagementMetadata()
     */
    public UserManagementMetadata getUserManagementMetadata() throws RemoteException, UserRoleServiceException {
        UserRoleServiceStub userService = getUserService();

        UserManagementMetadata metaDataToReturn = null;
        metaDataToReturn = userService.getUserManagementMetadata();

        return metaDataToReturn;
    }
}
