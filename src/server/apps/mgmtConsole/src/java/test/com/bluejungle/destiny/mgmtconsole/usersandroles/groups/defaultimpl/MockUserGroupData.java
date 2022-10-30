/*
 * Created on Sep 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.bluejungle.destiny.services.management.types.ExternalUserGroup;
import com.bluejungle.destiny.services.management.types.ExternalUserGroupList;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserGroupDTO;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupReducedList;
import com.bluejungle.destiny.services.policy.types.Access;
import com.bluejungle.destiny.services.policy.types.AccessList;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignment;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList;
import com.bluejungle.destiny.services.policy.types.Principal;
import com.bluejungle.destiny.services.policy.types.PrincipalType;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/MockUserGroupData.java#1 $
 */

public final class MockUserGroupData {

    public static final String TEST_DOMAIN = "TestDomain";
    public static UserGroupReducedList USER_GROUP_REDUCED_LIST = new UserGroupReducedList();
    public static Map USER_GROUPS = new HashMap();
    static 
    {
        UserGroupReduced[] userGroupReduceds = new UserGroupReduced[3];
        userGroupReduceds[0] = new UserGroupReduced(new BigInteger("0"), "Group 1", null, false, false, TEST_DOMAIN);
        userGroupReduceds[1] = new UserGroupReduced(new BigInteger("1"), "Group 2", null, false, false, TEST_DOMAIN);
        userGroupReduceds[2] = new UserGroupReduced(new BigInteger("2"), "Group 3", new byte[2], true, false, TEST_DOMAIN);
        MockUserGroupData.USER_GROUP_REDUCED_LIST.setUserGroupReduced(userGroupReduceds);

        UserGroupDTO nextGroup = new UserGroupDTO(userGroupReduceds[0].getId(), userGroupReduceds[0].getTitle(), "description 1", userGroupReduceds[0].getExternalId(), userGroupReduceds[0].isExternallyLinked(), null);
        MockUserGroupData.USER_GROUPS.put(nextGroup.getId(), nextGroup);
        nextGroup = new UserGroupDTO(userGroupReduceds[1].getId(), userGroupReduceds[1].getTitle(), "description 2", userGroupReduceds[1].getExternalId(), userGroupReduceds[1].isExternallyLinked(), null);
        MockUserGroupData.USER_GROUPS.put(nextGroup.getId(), nextGroup);
        nextGroup = new UserGroupDTO(userGroupReduceds[2].getId(), userGroupReduceds[2].getTitle(), "description 3", userGroupReduceds[2].getExternalId(), userGroupReduceds[2].isExternallyLinked(), "qualifiedname");
        MockUserGroupData.USER_GROUPS.put(nextGroup.getId(), nextGroup);
    }
    
    public static final UserGroupReducedList USER_GROUPS_FOR_USER_LIST = new UserGroupReducedList();
    static 
    {
        UserGroupReduced[] userGroupReduceds = new UserGroupReduced[3];
        userGroupReduceds[0] = USER_GROUP_REDUCED_LIST.getUserGroupReduced(0);
        userGroupReduceds[1] = USER_GROUP_REDUCED_LIST.getUserGroupReduced(1);
        MockUserGroupData.USER_GROUPS_FOR_USER_LIST.setUserGroupReduced(userGroupReduceds);
    }

    public static DefaultAccessAssignmentList DEFAULT_ACCESS_ASSIGNMENT_LIST;
    static
    {
        DefaultAccessAssignment[] defaultAccessAssignemnts = new DefaultAccessAssignment[3];
        Principal principal = new Principal(new BigInteger("0"), "Principal 1", PrincipalType.USER);
        Access[] principalAccess = { Access.READ, Access.WRITE };
        AccessList principalAccessList = new AccessList(principalAccess);
        defaultAccessAssignemnts[0] = new DefaultAccessAssignment(principal, principalAccessList);

        principal = new Principal(new BigInteger("1"), "Principal 2", PrincipalType.USER);
        defaultAccessAssignemnts[1] = new DefaultAccessAssignment(principal, principalAccessList);

        principal = new Principal(new BigInteger("2"), "Principal 3", PrincipalType.USER_GROUP);
        defaultAccessAssignemnts[2] = new DefaultAccessAssignment(principal, principalAccessList);

        MockUserGroupData.DEFAULT_ACCESS_ASSIGNMENT_LIST = new DefaultAccessAssignmentList(defaultAccessAssignemnts);

    }

    public static UserDTOList USER_GROUP_MEMBERS;
    static 
    {
        UserDTO[] userGroupMembersArray = new UserDTO[3];
        userGroupMembersArray[0] = new UserDTO("User 1 First", "User 1 Last", null, true, null);
        userGroupMembersArray[0].setId(new BigInteger("0"));
        userGroupMembersArray[0].setType("user");
        userGroupMembersArray[0].setUniqueName("User1@foo.com");

        userGroupMembersArray[1] = new UserDTO("User 2 First", "User 2 Last", null, true, null);
        userGroupMembersArray[1].setId(new BigInteger("1"));
        userGroupMembersArray[1].setType("user");
        userGroupMembersArray[1].setUniqueName("User2@foo.com");

        userGroupMembersArray[2] = new UserDTO("User 3 First", "User 3 Last", null, true, null);
        userGroupMembersArray[2].setId(new BigInteger("2"));
        userGroupMembersArray[2].setType("user");
        userGroupMembersArray[2].setUniqueName("User3@foo.com");

        MockUserGroupData.USER_GROUP_MEMBERS = new UserDTOList(userGroupMembersArray);
    }

    public static ExternalUserGroupList EXTERNAL_USER_GROUP_LIST = new ExternalUserGroupList();
    static
    {
        ExternalUserGroup[] externalUserGroups = new ExternalUserGroup[3];
        externalUserGroups[0] = new ExternalUserGroup("External Group 1", new byte[] { 1, 2 }, "bluejungle.com");
        externalUserGroups[1] = new ExternalUserGroup("External Group 2", new byte[] { 3, 4 }, "bluejungle.com");
        externalUserGroups[2] = new ExternalUserGroup("External Group 3", new byte[] { 5, 6 }, "bluejungle.com");
        MockUserGroupData.EXTERNAL_USER_GROUP_LIST.setExternalUserGroup(externalUserGroups);
    }
}
