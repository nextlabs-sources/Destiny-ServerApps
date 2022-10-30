/*
 * Created on Mar 18, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/controls/UIMenuItemTest.java#1 $
 */

public class UIMenuItemTest extends TestCase {

    private UIMenuItem menuItemToTest;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(UIMenuItemTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        menuItemToTest = new UIMenuItem();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for UIMenuItemTest.
     * 
     * @param arg0
     */
    public UIMenuItemTest(String arg0) {
        super(arg0);
    }

    /**
     * Retrieve the UI Menu Item Test suite
     */
    public static Test suite() {
        TestSuite testSuite = new TestSuite("UIMenuItemTestSuite");
        testSuite.addTest(new UIMenuItemTest("testGetFamily"));
        
        return testSuite;
    }
        
    /*
     * Class under test for String getFamily()
     */
    public void testGetFamily() {
        assertEquals(UIMenuItem.COMPONENT_FAMILY, menuItemToTest.getFamily());
    }
}