package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.services.management.types.*;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import org.apache.axis2.AxisFault;

import java.rmi.RemoteException;

/**
 * A facade to the User Groups web service utilized by the default
 * implementation of the user groups view component
 *
 * @author sgoldstein
 */
public interface IUserGroupServiceFacade {

    public static final String COMPONENT_NAME = "GroupServiceFacadeComponent";

    public UserGroupReducedList getAllGroups() throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public UserGroupReducedList getUserGroupsForUser(UserDTO user) throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public UserGroupDTO getGroup(UserGroupReduced menuItemObject) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public void updateGroup(UserGroupDTO groupObjectToUpdate) throws ServiceNotReadyFault, CommitFault, UniqueConstraintViolationFault, UnauthorizedCallerFault, RemoteException;

    public UserGroupDTO insertGroup(UserGroupInfo groupDataObject) throws ServiceNotReadyFault, CommitFault, UniqueConstraintViolationFault, UnauthorizedCallerFault, RemoteException;

    public void deleteGroup(UserGroupDTO groupObjectToDelete) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public UserDTOList getUsersInUserGroup(UserGroupDTO userGroup) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public void addUsersToUserGroup(UserGroupDTO userGroup, IDList userIds) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public void removeUsersFromUserGroup(UserGroupDTO userGroup, IDList userIds) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public ExternalUserGroupList getExternalGroupsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public ExternalUserGroupList getExternalGroupsForFreeFormSearchSpec(IFreeFormSearchSpec freeFormSearchSpec) throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    // FIX ME - Should pass in UserGroupReduced[]
    public void linkExternalGroups(ExternalUserGroupList externalGroupIDList) throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public DefaultAccessAssignmentList getDefaultAccessAssignments(UserGroupDTO userGroupWithAccessAssignments) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public void setDefaultAccessAssignments(UserGroupDTO userGroupToUpdate, DefaultAccessAssignmentList defaultAccessAssignments) throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public UserGroupReducedList getUserGroupsForSearchBucketSearchSpec(ISearchBucketSearchSpec searchSpec) throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException;

    public UserGroupReducedList getUserGroupsForFreeFormSearchSpec(IFreeFormSearchSpec searchSpec) throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException;

}
