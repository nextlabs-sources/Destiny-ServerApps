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

import junit.framework.TestCase;

import com.bluejungle.destiny.framework.types.CommitFault;
import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.framework.types.ServiceNotReadyFault;
import com.bluejungle.destiny.framework.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.framework.types.UnknownEntryFault;
import com.bluejungle.destiny.services.management.types.ExternalUserGroupList;
import com.bluejungle.destiny.services.management.types.ExternalUserGroupQueryResults;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserGroupDTO;
import com.bluejungle.destiny.services.management.types.UserGroupInfo;
import com.bluejungle.destiny.services.management.types.UserGroupQueryResults;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupReducedList;
import com.bluejungle.destiny.services.management.types.UserGroupServiceIF;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;

/**
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/UserGroupServiceFacadeImplTest.java#3 $
 */

public class UserGroupServiceFacadeImplTest extends TestCase {

    private MockUserGroupService mockUserGroupService = new MockUserGroupService();
    private UserGroupServiceFacadeImpl facadeToTest = new TestUserGroupServiceFacadeImpl();

    public ExternalUserGroupQueryResults externalGroupQueryResults = new ExternalUserGroupQueryResults(MockUserGroupData.EXTERNAL_USER_GROUP_LIST);
    public UserGroupQueryResults groupQueryResults = new UserGroupQueryResults(MockUserGroupData.USER_GROUP_REDUCED_LIST);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UserGroupServiceFacadeImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Constructor for UserGroupServiceFacadeImplTest.
     * 
     * @param arg0
     */
    public UserGroupServiceFacadeImplTest(String arg0) {
        super(arg0);
    }

    public void testDeleteGroup() throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupDTO groupToDelete = (UserGroupDTO) MockUserGroupData.USER_GROUPS.values().iterator().next();
        this.facadeToTest.deleteGroup(groupToDelete);
        assertTrue("Ensure group to delete was deleted through web service", this.mockUserGroupService.deleteGroupWasCalledWithArgument(groupToDelete.getId()));

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.deleteGroup(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking deleteGroup with null argument should lead to NPE", expectedException);
    }

    public void testGetAllGroups() throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupReducedList allGroups = this.facadeToTest.getAllGroups();
        assertEquals("Ensure get all groups returned as expedcted", MockUserGroupData.USER_GROUP_REDUCED_LIST, allGroups);
    }

    public void testGetUserGroupsForUser() throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupReducedList userGroupsForUser = this.facadeToTest.getUserGroupsForUser(new UserDTO());
        assertEquals("Ensure proper groups were retuned for user", MockUserGroupData.USER_GROUPS_FOR_USER_LIST, userGroupsForUser);

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.getUserGroupsForUser(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking getUserGroupsForUser with null user should lead to NPE", expectedException);
    }

    public void testGetUsersInUserGroup() throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupDTO group = (UserGroupDTO) MockUserGroupData.USER_GROUPS.values().iterator().next();
        UserDTOList members = this.facadeToTest.getUsersInUserGroup(group);
        assertEquals("Ensure user group members are retreieved as expected", MockUserGroupData.USER_GROUP_MEMBERS, members);

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.getUsersInUserGroup(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking deleteGroup with null argument should lead to NPE", expectedException);
    }

    public void testAddUsersToUserGroup() throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupDTO group = (UserGroupDTO) MockUserGroupData.USER_GROUPS.values().iterator().next();
        IDList listOfMembers = new IDList();
        this.facadeToTest.addUsersToUserGroup(group, listOfMembers);
        assertTrue("Ensure addUsers was called with expected arguments", this.mockUserGroupService.wasAddUsersToUserGroupInvokedWithArguments(group.getId(), listOfMembers));

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.addUsersToUserGroup(group, null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking addUsersToUserGroup with null user id list should lead to NPE", expectedException);

        expectedException = null;
        try {
            this.facadeToTest.addUsersToUserGroup(null, listOfMembers);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking addUsersToUserGroup with null group should lead to NPE", expectedException);
    }

    public void testRemoveUsersFromUserGroup() throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupDTO group = (UserGroupDTO) MockUserGroupData.USER_GROUPS.values().iterator().next();
        IDList listOfMembers = new IDList();
        this.facadeToTest.removeUsersFromUserGroup(group, listOfMembers);
        assertTrue("Ensure removeUsers was called with expected arguments", this.mockUserGroupService.wasRemoveUsersFromUserGroupInvokedWithArguments(group.getId(), listOfMembers));

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.removeUsersFromUserGroup(group, null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking removeUsersFromUserGroup with null user id list should lead to NPE", expectedException);

        expectedException = null;
        try {
            this.facadeToTest.removeUsersFromUserGroup(null, listOfMembers);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking removeUsersFromUserGroup with null group should lead to NPE", expectedException);
    }

    // FIX ME - When implemented, add unit test for different values of max search results
    public void testGetExternalGroupsForFreeFormSearchSpec() throws RemoteException, ServiceException {
        IFreeFormSearchSpec searchSpec = new IFreeFormSearchSpec() {

            public String getFreeFormSeachString() {
                return "foo";
            }

            /**
             * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSearchSpec#getMaximumResultsToReturn()
             */
            public int getMaximumResultsToReturn() {
                return 0;
            }                        
        };
        ExternalUserGroupList groupsFromSearch = this.facadeToTest.getExternalGroupsForFreeFormSearchSpec(searchSpec);
        assertEquals("Ensure external group query run as expected", this.externalGroupQueryResults.getMatchingExternalUserGroups(), groupsFromSearch);

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.getExternalGroupsForFreeFormSearchSpec(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking getExternalGroupsForFreeFormSearchSpec with null search spec should lead to NPE", expectedException);
    }

    // FIX ME - When implemented, add unit test for different values of max search results
    public void testGetExternalGroupsForSearchBucketSearchSpec() throws RemoteException, ServiceException {
        ISearchBucketSearchSpec searchSpec = new ISearchBucketSearchSpec() {

            public Character[] getCharactersInBucket() {
                return new Character[] { new Character('c') };
            }

            /**
             * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSearchSpec#getMaximumResultsToReturn()
             */
            public int getMaximumResultsToReturn() {
                return 0;
            }
        };
        ExternalUserGroupList groupsFromSearch = this.facadeToTest.getExternalGroupsForSearchBucketSearchSpec(searchSpec);
        assertEquals("Ensure external group query run as expected with search buckets search spec", this.externalGroupQueryResults.getMatchingExternalUserGroups(), groupsFromSearch);

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.getExternalGroupsForSearchBucketSearchSpec(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking getExternalGroupsForSearchBucketSearchSpec with null search spec should lead to NPE", expectedException);
    }

    public void testLinkExternalGroups() throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        this.facadeToTest.linkExternalGroups(MockUserGroupData.EXTERNAL_USER_GROUP_LIST);
        assertTrue("Ensure linkExternalGroups was invoked as expected", this.mockUserGroupService.wasLinkExternalGroupsCalledWithArg(MockUserGroupData.EXTERNAL_USER_GROUP_LIST));

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.linkExternalGroups(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking linkExternalGroups with null should lead to NPE", expectedException);
    }

    public void testGetGroup() throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupReduced groupReduced = MockUserGroupData.USER_GROUP_REDUCED_LIST.getUserGroupReduced(0);
        UserGroupDTO userGroupRetrieved = this.facadeToTest.getGroup(groupReduced);
        assertEquals("Ensure group retrieved as expected", MockUserGroupData.USER_GROUPS.get(groupReduced.getId()), userGroupRetrieved);

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.getGroup(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking getGroup with null should lead to NPE", expectedException);
    }

    public void testGetDefaultAccessAssignments() throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupDTO group = (UserGroupDTO) MockUserGroupData.USER_GROUPS.values().iterator().next();
        DefaultAccessAssignmentList accessAssignments = this.facadeToTest.getDefaultAccessAssignments(group);
        assertEquals("Ensure default access assignment list retrieved as expected", MockUserGroupData.DEFAULT_ACCESS_ASSIGNMENT_LIST, accessAssignments);

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.getDefaultAccessAssignments(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking getDefaultAccessAssignments with null should lead to NPE", expectedException);
    }

    public void testSetDefaultAccessAssignments() throws ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupDTO group = (UserGroupDTO) MockUserGroupData.USER_GROUPS.values().iterator().next();
        DefaultAccessAssignmentList accessAssignmentsToTest = new DefaultAccessAssignmentList();
        this.facadeToTest.setDefaultAccessAssignments(group, MockUserGroupData.DEFAULT_ACCESS_ASSIGNMENT_LIST);
        assertTrue("Ensure setDefaultAccessAssignments called with expected arg", this.mockUserGroupService.wasSetDefaultAccessAssignmentsCalledWithArgs(group.getId(), MockUserGroupData.DEFAULT_ACCESS_ASSIGNMENT_LIST));

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.setDefaultAccessAssignments(null, MockUserGroupData.DEFAULT_ACCESS_ASSIGNMENT_LIST);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking setDefaultAccessAssignments with null group should lead to NPE", expectedException);

        expectedException = null;
        try {
            this.facadeToTest.setDefaultAccessAssignments(group, null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking setDefaultAccessAssignments with null access assignment list should lead to NPE", expectedException);
    }

    // FIX ME - When implemented, add unit test for different values of max search results
    public void testGetUserGroupsForFreeFormSearchSpec() throws RemoteException, ServiceException {
        IFreeFormSearchSpec searchSpec = new IFreeFormSearchSpec() {

            public String getFreeFormSeachString() {
                return "foo";
            }

            /**
             * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSearchSpec#getMaximumResultsToReturn()
             */
            public int getMaximumResultsToReturn() {
                return 0;
            }
            
            
        };
        UserGroupReducedList groupsFromSearch = this.facadeToTest.getUserGroupsForFreeFormSearchSpec(searchSpec);
        assertEquals("Ensure group query run as expected", groupsFromSearch, this.groupQueryResults.getMatchingUserGroups());

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.getUserGroupsForFreeFormSearchSpec(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking getGroupPrincipalsForFreeFormSearchSpec with null search spec should lead to NPE", expectedException);
    }

    // FIX ME - When implemented, add unit test for different values of max search results
    public void testGetGroupPrincipalsForSearchBucketSearchSpec() throws RemoteException, ServiceException {
        ISearchBucketSearchSpec searchSpec = new ISearchBucketSearchSpec() {

            public Character[] getCharactersInBucket() {
                return new Character[] { new Character('c') };
            }

            /**
             * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSearchSpec#getMaximumResultsToReturn()
             */
            public int getMaximumResultsToReturn() {
                return 0;
            }

            
        };
        UserGroupReducedList groupsFromSearch = this.facadeToTest.getUserGroupsForSearchBucketSearchSpec(searchSpec);
        assertEquals("Ensure external group query run as expected with search buckets search spec", groupsFromSearch, this.groupQueryResults.getMatchingUserGroups());

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.getUserGroupsForSearchBucketSearchSpec(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking getGroupPrincipalsForSearchBucketSearchSpec with null search spec should lead to NPE", expectedException);
    }

    public void testInsertGroup() throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupInfo group = new UserGroupInfo();
        this.facadeToTest.insertGroup(group);

        assertTrue("Ensure insert group called with correct args", this.mockUserGroupService.wasCreateGroupCalledWithArg(group));

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.insertGroup(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking insertGroup with null should lead to NPE", expectedException);
    }

    public void testUpdateGroup() throws ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault, RemoteException, ServiceException {
        UserGroupDTO group = (UserGroupDTO) MockUserGroupData.USER_GROUPS.values().iterator().next();
        this.facadeToTest.updateGroup(group);

        assertTrue("Ensure update group called with correct args", this.mockUserGroupService.wasUpdateGroupCalledWithArg(group));

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.facadeToTest.updateGroup(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Invoking updateGroup with null should lead to NPE", expectedException);
    }

    public void testGetComponentInfo() {
        assertNotNull("Ensure component info is not null", this.facadeToTest.getComponentInfo());
    }

    private class TestUserGroupServiceFacadeImpl extends UserGroupServiceFacadeImpl {

        /**
         * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.UserGroupServiceFacadeImpl#getUserGroupServiceIF()
         */
        protected UserGroupServiceIF getUserGroupServiceIF() throws ServiceException {
            return UserGroupServiceFacadeImplTest.this.mockUserGroupService;
        }
    }

    private class MockUserGroupService implements UserGroupServiceIF {

        private BigInteger lastDeletedGroup;
        private IDList addUsersLastCalledWithUserIDList;
        private BigInteger addUsersLastCalledWithGroup;
        private IDList removeUsersLastCalledWithUserIDList;
        private BigInteger removeUsersLastCalledWithGroup;
        private ExternalUserGroupList linkExternalGroupLastCalledWithArg;
        private UserGroupDTO updateGroupCalledWithArg;
        private UserGroupInfo createGroupCalledWithArg;
        private BigInteger setDefaultAccessAssignmentsCalledWithGroupId;
        private DefaultAccessAssignmentList setDefaultAccessAssignmentsCalledWithAccessAssignmentList;

        public com.bluejungle.destiny.services.management.types.UserGroupReducedList getAllUserGroups() throws RemoteException, ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault {
            return MockUserGroupData.USER_GROUP_REDUCED_LIST;
        }

        /**
         * @see com.bluejungle.destiny.services.management.types.UserGroupServiceIF#getUserGroupsForUser(com.bluejungle.destiny.services.management.types.UserDTO)
         */
        public UserGroupReducedList getUserGroupsForUser(UserDTO arg0) throws RemoteException, ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault {
            return MockUserGroupData.USER_GROUPS_FOR_USER_LIST;
        }

        public com.bluejungle.destiny.services.management.types.UserGroupDTO getUserGroup(java.math.BigInteger userGroupId) throws java.rmi.RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault {
            return (UserGroupDTO) MockUserGroupData.USER_GROUPS.get(userGroupId);
        }

        /**
         * @see com.bluejungle.destiny.services.management.types.UserGroupServiceIF#getUsersInUserGroup(java.math.BigInteger)
         */
        public UserDTOList getUsersInUserGroup(BigInteger userGroupId) throws RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault {
            return MockUserGroupData.USER_GROUP_MEMBERS;
        }

        public void addUsersToUserGroup(java.math.BigInteger userGroupId, IDList userIds) throws java.rmi.RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault {
            this.addUsersLastCalledWithGroup = userGroupId;
            this.addUsersLastCalledWithUserIDList = userIds;
        }


        public boolean wasAddUsersToUserGroupInvokedWithArguments(BigInteger groupId, IDList listOfMembers) {
            return (groupId.equals(this.addUsersLastCalledWithGroup)) && (listOfMembers.equals(this.addUsersLastCalledWithUserIDList));
        }

        public void removeUsersFromUserGroup(java.math.BigInteger userGroupId, IDList userIds) throws java.rmi.RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault {
            this.removeUsersLastCalledWithGroup = userGroupId;
            this.removeUsersLastCalledWithUserIDList = userIds;
        }

        public boolean wasRemoveUsersFromUserGroupInvokedWithArguments(BigInteger groupId, IDList listOfMembers) {
            return (groupId.equals(this.removeUsersLastCalledWithGroup)) && (listOfMembers.equals(this.removeUsersLastCalledWithUserIDList));
        }
        
        public com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList getDefaultAccessAssignments(java.math.BigInteger userGroupId) throws java.rmi.RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault {
            return MockUserGroupData.DEFAULT_ACCESS_ASSIGNMENT_LIST;
        }

        public void setDefaultAccessAssignments(java.math.BigInteger userGroupId, com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList defaultAccessAssignments) throws java.rmi.RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault {
            this.setDefaultAccessAssignmentsCalledWithGroupId = userGroupId;
            this.setDefaultAccessAssignmentsCalledWithAccessAssignmentList = MockUserGroupData.DEFAULT_ACCESS_ASSIGNMENT_LIST;
        }

        public boolean wasSetDefaultAccessAssignmentsCalledWithArgs(BigInteger id, DefaultAccessAssignmentList defaultAccessAssignmentList) {
            return ((id.equals(this.setDefaultAccessAssignmentsCalledWithGroupId)) && (defaultAccessAssignmentList.equals(this.setDefaultAccessAssignmentsCalledWithAccessAssignmentList)));
        }

        public void linkExternalGroups(ExternalUserGroupList externalUserGroupList) throws RemoteException, ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault {
            this.linkExternalGroupLastCalledWithArg = externalUserGroupList;
        }

        public boolean wasLinkExternalGroupsCalledWithArg(ExternalUserGroupList argToTest) {
            return argToTest.equals(this.linkExternalGroupLastCalledWithArg);
        }

        public void updateGroup(com.bluejungle.destiny.services.management.types.UserGroupDTO userGroupToUpdate) throws java.rmi.RemoteException, ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault {
            this.updateGroupCalledWithArg = userGroupToUpdate;
        }

        public boolean wasUpdateGroupCalledWithArg(UserGroupDTO group) {
            return group.equals(this.updateGroupCalledWithArg);
        }

        public void deleteGroup(java.math.BigInteger userGroupId) throws java.rmi.RemoteException, ServiceNotReadyFault, UnknownEntryFault, CommitFault, UnauthorizedCallerFault {
            this.lastDeletedGroup = userGroupId;
        }

        public boolean deleteGroupWasCalledWithArgument(BigInteger id) {
            return id.equals(this.lastDeletedGroup);
        }

        public com.bluejungle.destiny.services.management.types.UserGroupQueryResults runUserGroupQuery(com.bluejungle.destiny.services.management.types.UserGroupQuerySpec userGroupQuerySpec) {                
            return UserGroupServiceFacadeImplTest.this.groupQueryResults;
        }

        public ExternalUserGroupQueryResults runExternalUserGroupQuery(com.bluejungle.destiny.services.management.types.UserGroupQuerySpec userGroupQuerySpec) throws java.rmi.RemoteException, ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault {
            return UserGroupServiceFacadeImplTest.this.externalGroupQueryResults;
        }

        public com.bluejungle.destiny.services.management.types.UserGroupDTO createUserGroup(com.bluejungle.destiny.services.management.types.UserGroupInfo userGroupInfo) throws java.rmi.RemoteException, ServiceNotReadyFault, CommitFault, UnauthorizedCallerFault {
            this.createGroupCalledWithArg = userGroupInfo;
            return null;
        }

        public boolean wasCreateGroupCalledWithArg(UserGroupInfo group) {
            return group.equals(this.createGroupCalledWithArg);
        }

    }

}
