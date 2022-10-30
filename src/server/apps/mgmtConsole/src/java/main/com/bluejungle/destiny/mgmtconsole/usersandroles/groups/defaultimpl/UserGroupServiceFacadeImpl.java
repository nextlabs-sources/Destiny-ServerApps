/*
 * Created on Aug 30, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.framework.types.RelationalOpDTO;
import com.bluejungle.destiny.services.management.types.*;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.LifestyleType;

import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.NonNegativeInteger;

import java.rmi.RemoteException;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade}
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/UserGroupServiceFacadeImpl.java#5 $
 */

public class UserGroupServiceFacadeImpl implements IUserGroupServiceFacade, IHasComponentInfo<UserGroupServiceFacadeImpl> {

    private static final ComponentInfo<UserGroupServiceFacadeImpl> COMPONENT_INFO = 
    	new ComponentInfo<UserGroupServiceFacadeImpl>(
    			COMPONENT_NAME, 
    			UserGroupServiceFacadeImpl.class, 
    			IUserGroupServiceFacade.class, 
    			LifestyleType.SINGLETON_TYPE);

    private static final String USER_GROUP_SERVICE_LOCATION_SERVLET_PATH = "/services/UserGroupService";

    private UserGroupServiceStub userGroupService;

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#deleteGroup(UserGroupDTO)
     */
    public void deleteGroup(UserGroupDTO groupObjectToDelete) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault {
        UserGroupServiceStub userGroupService = getUserGroupService();
        userGroupService.deleteGroup(groupObjectToDelete.getId());
    }
        
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getAllGroups()
     */
    public UserGroupReducedList getAllGroups() throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        UserGroupServiceStub userGroupService = getUserGroupService();
        return userGroupService.getAllUserGroups();
    }

    public UserGroupReducedList getUserGroupsForUser(UserDTO user) throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        if (user == null) {
            throw new NullPointerException("user cannot be null.");
        }

        UserGroupServiceStub userGroupService = getUserGroupService();
        return userGroupService.getUserGroupsForUser(user);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getUsersInUserGroup(com.bluejungle.destiny.services.management.types.UserGroupDTO)
     */
    public UserDTOList getUsersInUserGroup(UserGroupDTO userGroup) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        if (userGroup == null) {
            throw new NullPointerException("userGroup cannot be null.");
        }

        UserGroupServiceStub userGroupService = getUserGroupService();
        return userGroupService.getUsersInUserGroup(userGroup.getId());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#addUsersToUserGroup(com.bluejungle.destiny.services.management.types.UserGroupDTO,
     *      com.bluejungle.destiny.services.common.types.IDList)
     */
    public void addUsersToUserGroup(UserGroupDTO userGroup, IDList userIds) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        if (userGroup == null) {
            throw new NullPointerException("userGroup cannot be null.");
        }

        if (userIds == null) {
            throw new NullPointerException("userIds cannot be null.");
        }

        UserGroupServiceStub userGroupService = getUserGroupService();
        userGroupService.addUsersToUserGroup(userGroup.getId(), userIds);
    }

    
    /**
     * @throws ServiceNotReadyFault
     * @throws UnknownEntryFault
     * @throws CommitFault
     * @throws  UnauthorizedCallerFault
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#removeUsersFromUserGroup(com.bluejungle.destiny.services.management.types.UserGroupDTO, com.bluejungle.destiny.services.common.types.IDList)
     */
    public void removeUsersFromUserGroup(UserGroupDTO userGroup, IDList userIds) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        if (userGroup == null) {
            throw new NullPointerException("userGroup cannot be null.");
        }

        if (userIds == null) {
            throw new NullPointerException("userIds cannot be null.");
        }

        UserGroupServiceStub userGroupService = getUserGroupService();
        userGroupService.removeUsersFromUserGroup(userGroup.getId(), userIds);        
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getExternalGroupsForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public ExternalUserGroupList getExternalGroupsForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws UnauthorizedCallerFault, CommitFault, ServiceNotReadyFault, RemoteException {
        if (freeFormSearchSpec == null) {
            throw new NullPointerException("freeFormSearchSpec cannot be null.");
        }
        
        UserGroupQueryTerm[] externalUserGroupQueryTerms = {getTitleStartsWithQueryTerm(freeFormSearchSpec.getFreeFormSeachString())};
        return runExternalUserGroupQuery(externalUserGroupQueryTerms, freeFormSearchSpec.getMaximumResultsToReturn());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getExternalGroupsForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public ExternalUserGroupList getExternalGroupsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws UnauthorizedCallerFault, CommitFault, ServiceNotReadyFault, RemoteException {
        if (searchSpec == null) {
            throw new NullPointerException("searchSpec cannot be null.");
        }

        UserGroupQueryTerm[] externalUserGroupQueryTerms = buildUserGroupQueryTermsForSearchBucketSeachSpec(searchSpec);
        return runExternalUserGroupQuery(externalUserGroupQueryTerms, searchSpec.getMaximumResultsToReturn());
    }


    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#linkExternalGroups(ExternalUserGroupList)
     */
    public void linkExternalGroups(ExternalUserGroupList externalGroupIDList) throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        if (externalGroupIDList == null) {
            throw new NullPointerException("externalGroupIDList cannot be null.");
        }

        UserGroupServiceStub userGroupService = getUserGroupService();
        userGroupService.linkExternalGroups(externalGroupIDList);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getGroup(UserGroupReduced)
     */
    public UserGroupDTO getGroup(UserGroupReduced menuItemObject) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        if (menuItemObject == null) {
            throw new NullPointerException("menuItemObject cannot be null.");
        }

        UserGroupServiceStub userGroupService = getUserGroupService();
        return userGroupService.getUserGroup(menuItemObject.getId());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getDefaultAccessAssignments(com.bluejungle.destiny.services.management.types.UserGroupDTO)
     */
    public DefaultAccessAssignmentList getDefaultAccessAssignments(UserGroupDTO userGroupWithAccessAssignments) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        if (userGroupWithAccessAssignments == null) {
            throw new NullPointerException("userGroupWithAccessAssignments cannot be null.");
        }

        UserGroupServiceStub userGroupService = getUserGroupService();
        return userGroupService.getDefaultAccessAssignments(userGroupWithAccessAssignments.getId());
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#setDefaultAccessAssignments(com.bluejungle.destiny.services.management.types.UserGroupDTO,
     *      com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList)
     */
    public void setDefaultAccessAssignments(UserGroupDTO userGroupToUpdate, DefaultAccessAssignmentList defaultAccessAssignments) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        if (userGroupToUpdate == null) {
            throw new NullPointerException("userGroupToUpdate cannot be null.");
        }
        
        if (defaultAccessAssignments == null) {
            throw new NullPointerException("defaultAccessAssignments cannot be null.");
        }

        UserGroupServiceStub userGroupService = getUserGroupService();
        userGroupService.setDefaultAccessAssignments(userGroupToUpdate.getId(), defaultAccessAssignments);
    }
 
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getUserGroupsForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public UserGroupReducedList getUserGroupsForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws UnauthorizedCallerFault, CommitFault, ServiceNotReadyFault, RemoteException {
        if (freeFormSearchSpec == null) {
            throw new NullPointerException("freeFormSearchSpec cannot be null.");
        }
        
        UserGroupQueryTerm[] userGroupPrincipalsQueryTerms = {getTitleStartsWithQueryTerm(freeFormSearchSpec.getFreeFormSeachString())};
        return runUserGroupQuery(userGroupPrincipalsQueryTerms, freeFormSearchSpec.getMaximumResultsToReturn()).getMatchingUserGroups();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getUserGroupsForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public UserGroupReducedList getUserGroupsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws UnauthorizedCallerFault, CommitFault, ServiceNotReadyFault, RemoteException {
        if (searchSpec == null) {
            throw new NullPointerException("searchSpec cannot be null.");
        }
        
        UserGroupQueryTerm[] userGroupQueryTerms = buildUserGroupQueryTermsForSearchBucketSeachSpec(searchSpec);
        return runUserGroupQuery(userGroupQueryTerms, searchSpec.getMaximumResultsToReturn()).getMatchingUserGroups();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#insertGroup(UserGroupInfo)
     */
    public UserGroupDTO insertGroup(UserGroupInfo groupDataObject) throws ServiceNotReadyFault, CommitFault, UniqueConstraintViolationFault, UnauthorizedCallerFault, RemoteException {
        if (groupDataObject == null) {
            throw new NullPointerException("groupDataObject cannot be null.");
        }

        UserGroupServiceStub userGroupService = getUserGroupService();
        return userGroupService.createUserGroup(groupDataObject);
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#updateGroup(UserGroupDTO)
     */
    public void updateGroup(UserGroupDTO groupObjectToUpdate) throws ServiceNotReadyFault, CommitFault, UniqueConstraintViolationFault, UnauthorizedCallerFault, RemoteException {
        if (groupObjectToUpdate == null) {
            throw new NullPointerException("groupObjectToUpdate cannot be null.");
        }

        UserGroupServiceStub userGroupService = getUserGroupService();
        userGroupService.updateGroup(groupObjectToUpdate);
    }

    /**
     * @see com.bluejungle.framework.comp.IHasComponentInfo#getComponentInfo()
     */
    public ComponentInfo<UserGroupServiceFacadeImpl> getComponentInfo() {
        return COMPONENT_INFO;
    }

    /**
     * Build an array of query terms for the specified search bucket search spec
     * 
     * @param searchSpec
     *            the search spec for which to build the query terms
     * @return an array of query terms for the specified search bucket search
     *         spec
     */
    private UserGroupQueryTerm[] buildUserGroupQueryTermsForSearchBucketSeachSpec(ISearchBucketSearchSpec searchSpec) {
        Character[] searchBucketCharacters = searchSpec.getCharactersInBucket();

        UserGroupQueryTerm[] externalUserGroupQueryTerms = new UserGroupQueryTerm[searchBucketCharacters.length];
        for (int i = 0; i < searchBucketCharacters.length; i++) {
            externalUserGroupQueryTerms[i] = getTitleStartsWithQueryTerm(String.valueOf(searchBucketCharacters[i]));
        }
        return externalUserGroupQueryTerms;
    }
    
    /**
     * 
     * @param externalUserGroupQueryTerms
     * @return
     */
    private ExternalUserGroupList runExternalUserGroupQuery(UserGroupQueryTerm[] externalUserGroupQueryTerms, int maxResultsToReturn) throws UnauthorizedCallerFault, CommitFault, ServiceNotReadyFault, RemoteException {
        UserGroupServiceStub userGroupService = getUserGroupService();
        UserGroupQueryTermSet externalUserGroupQueryTermSet = new UserGroupQueryTermSet();
        externalUserGroupQueryTermSet.setUserGroupQueryTerm(externalUserGroupQueryTerms);

        NonNegativeInteger maxResults = new NonNegativeInteger(String.valueOf(maxResultsToReturn));
        UserGroupQuerySpec externalUserGroupQuerySpec = new UserGroupQuerySpec();
        externalUserGroupQuerySpec.setUserGroupQueryTermSet(externalUserGroupQueryTermSet);
        externalUserGroupQuerySpec.setMaxResults(maxResults);

        return userGroupService.runExternalUserGroupQuery(externalUserGroupQuerySpec).getMatchingExternalUserGroups();
    }

    /**
     * 
     * @param userGroupQueryTerms
     * @return
     */
    private UserGroupQueryResults runUserGroupQuery(UserGroupQueryTerm[] userGroupQueryTerms, int maxResultsToReturn) throws UnauthorizedCallerFault, CommitFault, ServiceNotReadyFault, RemoteException {
        UserGroupServiceStub userGroupService = getUserGroupService();
        UserGroupQueryTermSet userGroupQueryTermSet = new UserGroupQueryTermSet();
        userGroupQueryTermSet.setUserGroupQueryTerm(userGroupQueryTerms);

        NonNegativeInteger maxResults = new NonNegativeInteger(String.valueOf(maxResultsToReturn));
        UserGroupQuerySpec userGroupQuerySpec = new UserGroupQuerySpec();
        userGroupQuerySpec.setUserGroupQueryTermSet(userGroupQueryTermSet);
        userGroupQuerySpec.setMaxResults(maxResults);

        return userGroupService.runUserGroupQuery(userGroupQuerySpec);
    }
    
    /**
     * @param string
     * @return
     */
    private UserGroupQueryTerm getTitleStartsWithQueryTerm(String value) {
        return getTitleQueryTerm(value, RelationalOpDTO.starts_with);
    }

    /**
     * @param value
     * @param starts_with
     * @return
     */
    private UserGroupQueryTerm getTitleQueryTerm(String value, RelationalOpDTO operator) {
        UserGroupQueryTerm userGroupQueryTerm = new UserGroupQueryTerm();
        userGroupQueryTerm.setQueryField(UserGroupQueryField.TITLE);
        userGroupQueryTerm.setQueryOperator(operator);
        userGroupQueryTerm.setQueryValue(value);

        return userGroupQueryTerm;
    }
    
    /**
     * Retrieve the User Group Service.  (protected to allow unit testing of this class) 
     */
    protected UserGroupServiceStub getUserGroupService() throws AxisFault {
        if (this.userGroupService == null) {
            IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
            IConfiguration mainCompConfig = (IConfiguration) componentManager.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = mainCompConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            location += USER_GROUP_SERVICE_LOCATION_SERVLET_PATH;

            this.userGroupService = new UserGroupServiceStub(location);
        }

        return this.userGroupService;
    }
}