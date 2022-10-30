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
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/controls/UIMenuTest.java#1 $
 */

public class UIMenuTest extends TestCase {

    private UIMenu uiMenuToTest;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(UIMenuTest.class);
    }

    /**
     * Retrieve the UI Menu Test suite
     */
    public static Test suite() {
        TestSuite testSuite = new TestSuite("UIMenuTestSuite");
        testSuite.addTest(new UIMenuTest("testGetFamily"));
        testSuite.addTest(new UIMenuTest("testProcessDecodes"));
        
        return testSuite;
    }
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        uiMenuToTest = new UIMenu();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for UIMenuTest.
     * @param arg0
     */
    public UIMenuTest(String arg0) {
        super(arg0);
    }

    /*
     * Class under test for String getFamily()
     */
    public void testGetFamily() {
        assertEquals(UIMenu.COMPONENT_FAMILY, uiMenuToTest.getFamily());
    }

    /*
     * Class under test for void processDecodes(FacesContext)
     */
    public void testProcessDecodes() {
        // Not really a great way to test this
    }    
}
