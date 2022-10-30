/*
 * Created on Sep 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import java.math.BigInteger;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.bluejungle.destiny.framework.types.CommitFault;
import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.framework.types.ServiceNotReadyFault;
import com.bluejungle.destiny.framework.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.framework.types.UnknownEntryFault;
import com.bluejungle.destiny.services.management.types.ExternalUserGroupList;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserGroupDTO;
import com.bluejungle.destiny.services.management.types.UserGroupInfo;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupReducedList;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/MockUserGroupServiceFacadeImpl.java#1 $
 */

public class MockUserGroupServiceFacadeImpl implements IUserGroupServiceFacade {

    private UserGroupDTO removeUsersLastCalledWithGroupArg;
    private IDList removeUsersLastCalledWithMemberIdsArg;
    private BigInteger deleteUserGroupLastCalledWithArg;
    private UserGroupInfo insertUserGroupLastCalledWithArg;
    private UserGroupDTO updateUserGroupLastCalledWithArg;

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#addUsersToUserGroup(com.bluejungle.destiny.services.management.types.UserGroupDTO, com.bluejungle.destiny.services.common.types.IDList)
     */
    public void addUsersToUserGroup(UserGroupDTO userGroup, IDList userIDs) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, ServiceException {
    }
    
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#removeUsersFromUserGroup(com.bluejungle.destiny.services.management.types.UserGroupDTO, com.bluejungle.destiny.services.common.types.IDList)
     */
    public void removeUsersFromUserGroup(UserGroupDTO userGroup, IDList userIds) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.removeUsersLastCalledWithGroupArg = userGroup;
        this.removeUsersLastCalledWithMemberIdsArg = userIds;
        
    }


    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#deleteGroup(com.bluejungle.destiny.services.management.types.UserGroupDTO)
     */
    public void deleteGroup(UserGroupDTO groupObjectToDelete) throws ServiceException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        this.deleteUserGroupLastCalledWithArg = groupObjectToDelete.getId();
    }
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getAllGroups()
     */
    public UserGroupReducedList getAllGroups() throws ServiceException, ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException {        
        return MockUserGroupData.USER_GROUP_REDUCED_LIST;
    }
        
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getUserGroupsForUser(com.bluejungle.destiny.services.management.types.UserDTO)
     */
    public UserGroupReducedList getUserGroupsForUser(UserDTO user) throws ServiceException, ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        // TODO Auto-generated method stub
        return MockUserGroupData.USER_GROUPS_FOR_USER_LIST;
    }
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getDefaultAccessAssignments(com.bluejungle.destiny.services.management.types.UserGroupDTO)
     */
    public DefaultAccessAssignmentList getDefaultAccessAssignments(UserGroupDTO userGroupWithAccessAssignments) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, ServiceException {
        return MockUserGroupData.DEFAULT_ACCESS_ASSIGNMENT_LIST;
    }
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getExternalGroupsForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public ExternalUserGroupList getExternalGroupsForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws RemoteException, ServiceException {
        return null;
    }
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getExternalGroupsForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public ExternalUserGroupList getExternalGroupsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws RemoteException, ServiceException {
        return null;
    }
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getGroup(com.bluejungle.destiny.services.management.types.UserGroupReduced)
     */
    public UserGroupDTO getGroup(UserGroupReduced menuItemObject) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        return (UserGroupDTO) MockUserGroupData.USER_GROUPS.get(menuItemObject.getId());
    }
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getUserGroupsForFreeFormSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec)
     */
    public UserGroupReducedList getUserGroupsForFreeFormSearchSpec(IFreeFormSearchSpec searchSpec) {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getUserGroupsForSearchBucketSearchSpec(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec)
     */
    public UserGroupReducedList getUserGroupsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#getUsersInUserGroup(com.bluejungle.destiny.services.management.types.UserGroupDTO)
     */
    public UserDTOList getUsersInUserGroup(UserGroupDTO userGroup) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, ServiceException {
        return MockUserGroupData.USER_GROUP_MEMBERS;
    }
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#insertGroup(com.bluejungle.destiny.services.management.types.UserGroupInfo)
     */
    public UserGroupDTO insertGroup(UserGroupInfo groupDataObject) throws ServiceException, ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException {
        this.insertUserGroupLastCalledWithArg = groupDataObject;
        
        // Return a group that already exists
        return (UserGroupDTO) MockUserGroupData.USER_GROUPS.values().iterator().next();
        
    }
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#linkExternalGroups(ExternalUserGroupList)
     */
    public void linkExternalGroups(ExternalUserGroupList externalGroupIDList) throws RemoteException, ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, ServiceException {
    }
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#setDefaultAccessAssignments(com.bluejungle.destiny.services.management.types.UserGroupDTO, com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList)
     */
    public void setDefaultAccessAssignments(UserGroupDTO userGroupToUpdate, DefaultAccessAssignmentList defaultAccessAssignments) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault,
            ServiceException {
    }
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade#updateGroup(com.bluejungle.destiny.services.management.types.UserGroupDTO)
     */
    public void updateGroup(UserGroupDTO groupObjectToUpdate) throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.updateUserGroupLastCalledWithArg = groupObjectToUpdate;
    }

    public boolean wasDeleteUserGroupCalledWithArg(BigInteger userGroupId) {
        return userGroupId.equals(this.deleteUserGroupLastCalledWithArg);
    }

    public boolean wasInsertUserGroupCalledWithArg(UserGroupInfo userGroupInfo) {
        return userGroupInfo.equals(this.insertUserGroupLastCalledWithArg);
    }
    
    public boolean wasUpdateUserGroupCalledWithArg(UserGroupDTO userGroupDTO) {
        return userGroupDTO.equals(this.updateUserGroupLastCalledWithArg);
    }
    
    public boolean wasRemoveUsersCalledWithArgs(UserGroupDTO userGroupDTO, IDList memberIds) {
        return ((userGroupDTO.equals(this.removeUsersLastCalledWithGroupArg)) && memberIds.equals(this.removeUsersLastCalledWithMemberIdsArg));
    }
}
