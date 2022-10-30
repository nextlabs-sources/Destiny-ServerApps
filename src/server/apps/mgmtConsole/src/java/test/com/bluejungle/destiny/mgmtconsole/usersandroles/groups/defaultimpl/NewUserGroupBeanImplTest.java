/*
 * Created on Sep 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/NewUserGroupBeanImplTest.java#1 $
 */

public class NewUserGroupBeanImplTest extends TestCase {

    private NewUserGroupBeanImpl beanToTest;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NewUserGroupBeanImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.beanToTest = new NewUserGroupBeanImpl();
    }

    /**
     * Constructor for NewUserGroupBeanImplTest.
     * 
     * @param arg0
     */
    public NewUserGroupBeanImplTest(String arg0) {
        super(arg0);
    }

    public void testGetUserGroupId() {
        UnsupportedOperationException expectedException = null;
        try {
            this.beanToTest.getUserGroupId();
        } catch (UnsupportedOperationException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure getUserId() throws UOE", expectedException);
    }

    public void testGetSetUserGroupTitle() {
        String initialTitle = this.beanToTest.getUserGroupTitle();
        assertEquals("Ensure initial title as expected", "New Group", initialTitle);
        
        // FIX ME - Test for initial title!
        
        String newTitle = "fooBarTitle";
        this.beanToTest.setUserGroupTitle(newTitle);
        assertEquals("Ensure title set as expected", newTitle, this.beanToTest.getUserGroupTitle());

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.beanToTest.setUserGroupTitle(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure null title leads to throws NPE", expectedException);
    }

    public void testGetSetUserGroupDescription() {
        String initialDescription = this.beanToTest.getUserGroupDescription();
        assertEquals("Ensure initial description as expected", "New Group", initialDescription);
        
        // FIX ME - Test for initial description!
        
        String newDescription = "fooBarDescription";
        this.beanToTest.setUserGroupDescription(newDescription);
        assertEquals("Ensure description set as expected", newDescription, this.beanToTest.getUserGroupDescription());

        // Test NPE
        NullPointerException expectedException = null;
        try {
            this.beanToTest.setUserGroupDescription(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure null description leads to throws NPE", expectedException);
    }

    public void testGetMembers() {
        UnsupportedOperationException expectedException = null;
        try {
            this.beanToTest.getMembers();
        } catch (UnsupportedOperationException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure getMembers() throws UOE", expectedException);
    }

    public void testGetDefaultAccessAssignments() {
        UnsupportedOperationException expectedException = null;
        try {
            this.beanToTest.getDefaultAccessAssignments();
        } catch (UnsupportedOperationException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure getDefaultAccessAssignments() throws UOE", expectedException);
    }

    public void testIsExternallyManaged() {
        assertFalse("Ensure isExternallyManaged as expected", this.beanToTest.isExternallyManaged());
    }

    public void testIsNew() {
        assertTrue("Ensure isNew as expected", this.beanToTest.isNew());
    }

    public void testGetUserGroupQualifiedExternalName() {
        UnsupportedOperationException expectedException = null;
        try {
            this.beanToTest.getUserGroupQualifiedExternalName();
        } catch (UnsupportedOperationException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure getUserGroupQualifiedExternalName() throws UOE", expectedException);
    }

    public void testGetWrappedUserGroupInfo() {
        assertNotNull("Ensure wrapped group is not null", this.beanToTest.getWrappedUserGroupInfo());
    }

}