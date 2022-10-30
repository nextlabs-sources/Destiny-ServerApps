/*
 * Created on Sep 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;

import junit.framework.TestCase;

/**
 * Unit test for Input Password
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/controls/UIPasswordTest.java#1 $
 */

public class UIPasswordTest extends TestCase {

    private static final Object EXPECTED_COMPONENT_FAMILY = "com.bluejungle.destiny.InputPassword";

    private UIPassword componentToTest;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UIPasswordTest.class);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.componentToTest = new UIPassword();
    }

    /*
     * test for String getFamily()
     */
    public void testGetFamily() {
        assertTrue("testGetFamily - Verify component familly as expected", this.componentToTest.getFamily().equals(EXPECTED_COMPONENT_FAMILY));
    }

    /**
     * Test for String getConfirmingSubmittedValue() and void setConfirmingSubmittedValue()
     */
    public void testGetSetConfirmingSubmittedValue() {
        // Ensure it's initially null
        assertNull("testGetSetConfirmingSubmittedValue - Ensure initially null", this.componentToTest.getConfirmingSubmittedValue());
        
        // Now set it
        String testConfirmSubmittedValue = "testConfirmedSubmittedValue";
        this.componentToTest.setConfirmingSubmittedValue(testConfirmSubmittedValue);
        
        // Test that it was set
        assertEquals("testGetSetConfirmingSubmittedValue - Ensure confirming suibmitted value set as expected", testConfirmSubmittedValue, this.componentToTest.getConfirmingSubmittedValue());
    }

    /*
     * test for Object getValue() and void setValue
     */
    public void testGetSetValue() {
        //      Ensure it's initially null
        assertNull("testGetSetValue - Ensure initially null", this.componentToTest.getValue());
        
        // Now set it
        String testValue = "testValue";
        this.componentToTest.setValue(testValue);
        
        // Test that it was set
        assertEquals("testGetSetValue - Ensure value set as expected", testValue, this.componentToTest.getValue());

        // Ensure local value is set
        assertTrue("testGetSetValue - Ensure local value is set", this.componentToTest.isLocalValueSet());
    }

    /*
     * test for Object getLocalValue()
     */
    public void testGetLocalValue() {
        //      Ensure it's initially null
        assertNull("testGetLocalValue - Ensure initially null", this.componentToTest.getLocalValue());
        assertFalse("testGetLocalValue - Ensure local value is not set", this.componentToTest.isLocalValueSet());
        
        // Now set the component value.  Should set local value
        String testValue = "testValue";
        this.componentToTest.setValue(testValue);
        
        // Test that it was set
        assertEquals("testGetLocalValue - Ensure local value set as expected", testValue, this.componentToTest.getLocalValue());
        assertTrue("testGetLocalValue - Ensure local value is set", this.componentToTest.isLocalValueSet());        
    }

    /*
     * Class under test for void restoreState(FacesContext, Object)
     */
    public void testRestoreState() {
        // Simply ensure it doesn't crash
        this.componentToTest.restoreState(new MockFacesContext(), null);
    }

    /*
     * Class under test for Object saveState(FacesContext)
     */
    public void testSaveState() {
        assertNull("testSaveState - Ensure state returned is null", this.componentToTest.saveState(new MockFacesContext()));
    }

}