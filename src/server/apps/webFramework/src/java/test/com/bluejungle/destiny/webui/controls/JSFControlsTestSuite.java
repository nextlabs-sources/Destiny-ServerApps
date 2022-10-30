/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is the test suite for the JSF UI controls.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/controls/JSFControlsTestSuite.java#1 $
 */

public class JSFControlsTestSuite {

    /**
     * Returns the set of tests to be run in the test suite
     * 
     * @return the set of tests to be run in the test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("JSF UI controls");
        suite.addTest(new TestSuite(UIBarItemTest.class, "Bar item control"));
        suite.addTest(new TestSuite(UICSSIncludeTest.class, "CSS inclusion control"));
        suite.addTest(new TestSuite(UIHorColumnTest.class, "Horizontal column control"));
        suite.addTest(new TestSuite(UIJSIncludeTest.class, "JS inclusion control"));
        suite.addTest(new TestSuite(UIRowTest.class, "Row control"));
        suite.addTest(getMenuTestSuite());
        suite.addTest(new TestSuite(UIMessagesTest.class, "Messages control"));
        suite.addTest(new TestSuite(UIPasswordTest.class, "Messages control"));
        return suite;
    }

    /**
     * Retrieve the set of test for testing the Menu related components
     * 
     * @return
     */
    private static Test getMenuTestSuite() {
        TestSuite testToReturn = new TestSuite("MenuTestSuite");
        testToReturn.addTest(UIMenuTest.suite());
        testToReturn.addTest(UIMenuItemTest.suite());
        return testToReturn;
    }
}