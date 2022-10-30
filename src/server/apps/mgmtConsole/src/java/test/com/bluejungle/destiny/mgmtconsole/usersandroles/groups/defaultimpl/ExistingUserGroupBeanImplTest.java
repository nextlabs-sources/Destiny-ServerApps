/*
 * Created on Sep 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import java.math.BigInteger;

import javax.faces.model.DataModel;

import junit.framework.TestCase;

import com.bluejungle.destiny.services.management.types.UserGroupDTO;
import com.bluejungle.destiny.services.policy.types.Access;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/ExistingUserGroupBeanImplTest.java#1 $
 */

public class ExistingUserGroupBeanImplTest extends TestCase {

    private ExistingUserGroupBeanImpl beanToTest;
    private ExistingUserGroupBeanImpl externalBeanToTest;
    private UserGroupDTO wrappedGroup;
    private UserGroupDTO externalWrappedGroup;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ExistingUserGroupBeanImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        ComponentInfo mockUserGroupServiceFacadeInfo = new ComponentInfo(UserGroupServiceFacadeImpl.COMPONENT_NAME, MockUserGroupServiceFacadeImpl.class.getName(), LifestyleType.SINGLETON_TYPE);
        componentManager.registerComponent(mockUserGroupServiceFacadeInfo, true);
        
        this.wrappedGroup = new UserGroupDTO(new BigInteger("456"), "Internal Group", "Internal Group Description", null, false, null);
        this.beanToTest = new ExistingUserGroupBeanImpl(this.wrappedGroup);
        
        this.externalWrappedGroup = new UserGroupDTO(new BigInteger("456"), "Internal Group", "Internal Group Description", new byte[] {4, 6}, true, "domain/external_name");
        this.externalBeanToTest = new ExistingUserGroupBeanImpl(this.externalWrappedGroup);
    }

    public void testGetDefaultAccessAssignments() {
        DataModel defaultAccessAssignments = this.beanToTest.getDefaultAccessAssignments();
        for (int i = 0; i < defaultAccessAssignments.getRowCount(); i++) {
            defaultAccessAssignments.setRowIndex(i);
            assertEquals("testGetDefaultAccessAssignments - Ensure default access assignments principal is as expected", MockUserGroupData.DEFAULT_ACCESS_ASSIGNMENT_LIST.getDefaultAccessAssignment(i).getPrinciapl(), ((DefaultAccessAssignmentBeanImpl)defaultAccessAssignments.getRowData()).getWrappedDefaultAccessAssignment().getPrinciapl());
            
            Access[] mockDataAccessArray = MockUserGroupData.DEFAULT_ACCESS_ASSIGNMENT_LIST.getDefaultAccessAssignment(i).getDefaultAccess().getAccess();
            Access[] dataToTestAccessArray = ((DefaultAccessAssignmentBeanImpl)defaultAccessAssignments.getRowData()).getWrappedDefaultAccessAssignment().getDefaultAccess().getAccess();
            assertEquals("testGetDefaultAccessAssignments - Ensure default access assignments Access List length is as expected", mockDataAccessArray.length, dataToTestAccessArray.length);
            boolean contentsCorrect = true;
            for (int j = 0; j < mockDataAccessArray.length; j++) {
                Access nextAccess = mockDataAccessArray[j];
                boolean containsNextAccess = false;
                for (int k = 0; k < dataToTestAccessArray.length; k++) {
                    if (dataToTestAccessArray[k] == nextAccess) {
                        containsNextAccess = true;
                    }
                }
                contentsCorrect &= containsNextAccess;
            }
            assertTrue("testGetDefaultAccessAssignments - Ensure default access assignments Access List contents are as expected", contentsCorrect);
        }
    }

    public void testGetUserGroupId() {
        assertEquals("Ensure user group id as expected", this.wrappedGroup.getId().toString(), this.beanToTest.getUserGroupId());
    }

    public void testGetSetUserGroupTitle() {
        assertEquals("Ensure user group initial title as expected", this.wrappedGroup.getTitle(), this.beanToTest.getUserGroupTitle());
        
        String newTitle = "foobartitle";
        this.beanToTest.setUserGroupTitle(newTitle);
        assertEquals("Ensure user group title set as expected", newTitle, this.beanToTest.getUserGroupTitle());
        
        // NPE
        NullPointerException expectedException = null;
        try {
            this.beanToTest.setUserGroupTitle(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure NPE thrown for null title", expectedException);
    }

    public void testGetSetUserGroupDescription() {
        assertEquals("Ensure user group initial description as expected", this.wrappedGroup.getDescription(), this.beanToTest.getUserGroupDescription());
        
        String newDescription = "foobardescription";
        this.beanToTest.setUserGroupDescription(newDescription);
        assertEquals("Ensure user group description set as expected", newDescription, this.beanToTest.getUserGroupDescription());
        
        // NPE
        NullPointerException expectedException = null;
        try {
            this.beanToTest.setUserGroupDescription(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure NPE thrown for null description", expectedException);
    }

    public void testGetMembers() {
        DataModel members = this.beanToTest.getMembers();
        for (int i = 0; i < members.getRowCount(); i++) {
            members.setRowIndex(i);
            assertEquals("Ensure members are as expected", MockUserGroupData.USER_GROUP_MEMBERS.getUsers(i).getId().toString(), ((IInternalMemberBean)members.getRowData()).getMemberId());
        }
    }

    public void testIsExternallyManaged() {
        assertEquals("Ensure isExternallyManaged as expected for internal group", false, this.beanToTest.isExternallyManaged());
        assertEquals("Ensure isExternallyManaged as expected for external group", true, this.externalBeanToTest.isExternallyManaged());
    }

    public void testGetUserGroupQualifiedExternalName() {
        assertEquals("Ensure getUserGroupQualifiedExternalName as expected for external group", this.externalWrappedGroup.getQualifiedExternalName(), this.externalBeanToTest.getUserGroupQualifiedExternalName());
        
        // UOE
        UnsupportedOperationException expectedException = null;
        try {
            this.beanToTest.getUserGroupQualifiedExternalName();
        } catch (UnsupportedOperationException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure UOE thrown for internal group when trying to retrieve external name", expectedException);
    }

    public void testIsNew() {
        assertFalse("Ensure existing group is not new", this.beanToTest.isNew());
    }

    public void testGetWrappedUserGroupDTO() {
        assertEquals("Ensure wrapped group as expected", this.wrappedGroup, this.beanToTest.getWrappedUserGroupDTO());
    }

}