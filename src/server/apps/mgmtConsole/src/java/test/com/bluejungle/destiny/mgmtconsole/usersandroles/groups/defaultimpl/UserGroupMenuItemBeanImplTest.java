/*
 * Created on Sep 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import java.math.BigInteger;

import com.bluejungle.destiny.services.management.types.UserGroupReduced;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/UserGroupMenuItemBeanImplTest.java#1 $
 */
public class UserGroupMenuItemBeanImplTest extends TestCase {

    private UserGroupMenuItemBeanImpl menuItemToTest;
    private UserGroupReduced wrappedUserGroupReduced;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(UserGroupMenuItemBeanImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        this.wrappedUserGroupReduced = new UserGroupReduced(new BigInteger("44"), "My User Group", new byte[]{4, 55}, true, true, MockUserGroupData.TEST_DOMAIN);
        this.menuItemToTest = new UserGroupMenuItemBeanImpl(wrappedUserGroupReduced);
    }

    /**
     * Constructor for UserGroupMenuItemBeanImplTest.
     * @param arg0
     */
    public UserGroupMenuItemBeanImplTest(String arg0) {
        super(arg0);
    }

    public void testGetUserGroupId() {
        assertEquals(this.wrappedUserGroupReduced.getId().toString(), this.menuItemToTest.getUserGroupId());
    }

    public void testGetUserGroupTitle() {
        assertEquals(this.wrappedUserGroupReduced.getTitle(), this.menuItemToTest.getUserGroupTitle());
    }

    public void testIsExternallyManaged() {
        assertEquals(this.wrappedUserGroupReduced.isExternallyLinked(), this.menuItemToTest.isExternallyManaged());
    }

    public void testIsOrphaned() {
        assertEquals(this.wrappedUserGroupReduced.isOrphaned(), this.menuItemToTest.isOrphaned());
    }

    public void testGetWrappedUserGroupReduced() {
        assertEquals(this.wrappedUserGroupReduced, this.menuItemToTest.getWrappedUserGroupReduced());
    }

}
