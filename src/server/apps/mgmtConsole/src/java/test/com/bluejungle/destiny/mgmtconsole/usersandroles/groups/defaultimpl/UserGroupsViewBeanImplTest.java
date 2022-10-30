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
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.faces.model.DataModel;
import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import com.bluejungle.destiny.framework.types.IDList;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupMenuItemBean;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.UserGroupsViewException;
import com.bluejungle.destiny.services.management.types.UserGroupDTO;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;

/**
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/UserGroupsViewBeanImplTest.java#1 $
 */

public class UserGroupsViewBeanImplTest extends TestCase {

    private UserGroupsViewBeanImpl beanToTest;
    private MockUserGroupServiceFacadeImpl mockUserGroupServiceFacade;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UserGroupsViewBeanImplTest.class);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.beanToTest = new UserGroupsViewBeanImpl();

        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        ComponentInfo mockUserGroupServiceFacadeInfo = new ComponentInfo(UserGroupServiceFacadeImpl.COMPONENT_NAME, MockUserGroupServiceFacadeImpl.class.getName(), LifestyleType.SINGLETON_TYPE);
        componentManager.registerComponent(mockUserGroupServiceFacadeInfo, true);
        this.mockUserGroupServiceFacade = (MockUserGroupServiceFacadeImpl) componentManager.getComponent(UserGroupServiceFacadeImpl.COMPONENT_NAME);

        new MockFacesContext(); // Create a mocke faces context
    }

    public void testPrerender() throws RemoteException, ServiceException {
        // Ensure it doesn't fail with an error
        this.beanToTest.prerender();
    }

    
    public void testGetDefaultRightsTabName() {
        assertNotNull("Ensure default rights tab name is not null", this.beanToTest.getDefaultRightsTabName());
    }
    
    public void testGetGeneralTabName() {
        assertNotNull("Ensure Generate tab name is not null", this.beanToTest.getGeneralTabName());
    }
    
    public void testGetMembersTabName() {
        assertNotNull("Ensure Members tab name is not null", this.beanToTest.getMembersTabName());   
    }
    
    public void testClearSelectedNewUserGroup() throws RemoteException, ServiceException {
        this.beanToTest.prerender();
        this.beanToTest.createAndSelectNewUserGroup();
        assertTrue("Ensure new group is selected", this.beanToTest.getSelectedUserGroup().isNew());

        // Now clear it
        this.beanToTest.clearSelectedNewUserGroup();
        assertTrue("Ensure existing group is selected after new group is cleared", !this.beanToTest.getSelectedUserGroup().isNew());

        // try clearing again. Should not return an error
        this.beanToTest.clearSelectedNewUserGroup();
    }

    public void testCreateAndSelectNewUserGroup() throws RemoteException, ServiceException {
        this.beanToTest.prerender();
        // Ensure existing group is selected
        assertTrue("Ensure existing group is initially selected", !this.beanToTest.getSelectedUserGroup().isNew());

        this.beanToTest.createAndSelectNewUserGroup();
        assertTrue("Ensure new group is selected", this.beanToTest.getSelectedUserGroup().isNew());
    }

    public void testDeleteSelectedUserGroup() throws RemoteException, ServiceException, UserGroupsViewException {
        this.beanToTest.prerender();
        IUserGroupBean selectedBean = this.beanToTest.getSelectedUserGroup();
        this.beanToTest.deleteSelectedUserGroup();
        assertTrue("Ensure delete on service facade called with correct argument", this.mockUserGroupServiceFacade.wasDeleteUserGroupCalledWithArg(new BigInteger(selectedBean.getUserGroupId())));

        // Ensure deleteing a new group doesn't cause a problem
        this.beanToTest.prerender();
        this.beanToTest.createAndSelectNewUserGroup();
        this.beanToTest.deleteSelectedUserGroup();
        assertNotNull("Ensure selected group is not null", this.beanToTest.getSelectedUserGroup());

    }

    public void testGetUserGroups() throws RemoteException, ServiceException {
        this.beanToTest.prerender();

        DataModel userGroups = this.beanToTest.getUserGroups();
        for (int i = 0; i < userGroups.getRowCount(); i++) {
            userGroups.setRowIndex(i);
            IInternalUserGroupMenuItemBean nextGroup = (IInternalUserGroupMenuItemBean) userGroups.getRowData();
            assertEquals("Group i in data model is as expected", MockUserGroupData.USER_GROUP_REDUCED_LIST.getUserGroupReduced(i), nextGroup.getWrappedUserGroupReduced());
        }
    }

    public void testGetSelectedUserGroup() throws RemoteException, ServiceException {
        this.beanToTest.prerender();

        UserGroupReduced expectedMenuItem = MockUserGroupData.USER_GROUP_REDUCED_LIST.getUserGroupReduced(0);
        UserGroupDTO expectedGroup = (UserGroupDTO) MockUserGroupData.USER_GROUPS.get(expectedMenuItem.getId());
        assertEquals("Selected group is as expected", expectedGroup, ((IInternalExistingUserGroupBean) this.beanToTest.getSelectedUserGroup()).getWrappedUserGroupDTO());

        this.beanToTest.createAndSelectNewUserGroup();
        assertTrue("Ensure new bean is selected", this.beanToTest.getSelectedUserGroup().isNew());
    }

    public void testInsertSelectedNewUserGroup() throws RemoteException, ServiceException, UserGroupsViewException {
        this.beanToTest.prerender();
        this.beanToTest.createAndSelectNewUserGroup();
        NewUserGroupBeanImpl newBeanSelected = (NewUserGroupBeanImpl) this.beanToTest.getSelectedUserGroup();
        this.beanToTest.insertSelectedNewUserGroup();

        assertTrue("Ensure insert on service facade called with correct argument", this.mockUserGroupServiceFacade.wasInsertUserGroupCalledWithArg(newBeanSelected.getWrappedUserGroupInfo()));

        this.beanToTest.reset();
        this.beanToTest.prerender();

        IllegalStateException expectedException = null;
        try {
            this.beanToTest.insertSelectedNewUserGroup();
        } catch (IllegalStateException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure ILE thrown when inserting existing group", expectedException);

        // FIX ME - Should test whether or not newly inserted group is selected.
    }

    public void testSaveSelectedUserGroup() throws RemoteException, ServiceException, UserGroupsViewException {
        this.beanToTest.prerender();
        ExistingUserGroupBeanImpl beanSelected = (ExistingUserGroupBeanImpl) this.beanToTest.getSelectedUserGroup();
        this.beanToTest.saveSelectedUserGroup();
        assertTrue("Ensure save on service facade called with correct argument", this.mockUserGroupServiceFacade.wasUpdateUserGroupCalledWithArg(beanSelected.getWrappedUserGroupDTO()));

        // Make sure that if save is called with a new user group, it's still
        // handled as best as possible (with an insert)
        this.beanToTest.createAndSelectNewUserGroup();

        NewUserGroupBeanImpl newBeanSelected = (NewUserGroupBeanImpl) this.beanToTest.getSelectedUserGroup();
        this.beanToTest.saveSelectedUserGroup();

        assertTrue("Ensure insert on service facade called with correct argument", this.mockUserGroupServiceFacade.wasInsertUserGroupCalledWithArg(newBeanSelected.getWrappedUserGroupInfo()));

    }

    public void testSetSelectedUserGroup() throws RemoteException, ServiceException {
        this.beanToTest.prerender();
        DataModel allUserGroups = this.beanToTest.getUserGroups();
        allUserGroups.setRowIndex(2);
        IUserGroupMenuItemBean userGroupToSelect = (IUserGroupMenuItemBean) allUserGroups.getRowData();

        this.beanToTest.setSelectedUserGroup(userGroupToSelect.getUserGroupId());
        this.beanToTest.prerender();
        assertEquals("Ensure bean set as expected", userGroupToSelect.getUserGroupId(), this.beanToTest.getSelectedUserGroup().getUserGroupId());

        // Test NPE
        this.beanToTest.createAndSelectNewUserGroup();
        NullPointerException expectedException = null;
        try {
            this.beanToTest.setSelectedUserGroup(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure NPE thrown when saving new group", expectedException);
    }

    public void testRemoveMembersFromSelectedUserGroup() throws UserGroupsViewException, RemoteException, ServiceException {
        this.beanToTest.prerender();
        Long userToRemove = new Long(5);
        Set userToRemoveSet = Collections.singleton(userToRemove);
        this.beanToTest.removeMembersFromSelectedUserGroup(userToRemoveSet);
        IInternalExistingUserGroupBean beanSelected = (IInternalExistingUserGroupBean) this.beanToTest.getSelectedUserGroup();
        IDList userToRemoveIDList = new IDList(new BigInteger[] { BigInteger.valueOf(userToRemove.longValue()) });
        assertTrue("Ensure users removed as expected", this.mockUserGroupServiceFacade.wasRemoveUsersCalledWithArgs(beanSelected.getWrappedUserGroupDTO(), userToRemoveIDList));
        
        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.beanToTest.removeMembersFromSelectedUserGroup(null);   
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure NPE thrown when removing null user member ids", expectedException);
    }

    public void testReset() {
        // Simply ensure that it doesn't cause an error
        this.beanToTest.reset();
    }

    public void testResetAndSelectUserGroup() throws RemoteException, ServiceException {
        this.beanToTest.prerender();
        Iterator userGroups = MockUserGroupData.USER_GROUPS.values().iterator();
        userGroups.next();
        userGroups.next();
        UserGroupDTO userGroupToSelect = (UserGroupDTO) userGroups.next();
        this.beanToTest.resetAndSelectUserGroup(userGroupToSelect);
        assertEquals("Ensre user group selected after call to resetAndSelect", userGroupToSelect, ((IInternalExistingUserGroupBean) this.beanToTest.getSelectedUserGroup()).getWrappedUserGroupDTO());
    }
}