/*
 * Created on May 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the default implementation of the Browsable Data Picker backing beans
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/BrowsableDataPickerTestSuite.java#1 $
 */

public class BrowsableDataPickerTestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(BrowsableDataPickerTestSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl");
        //$JUnit-BEGIN$
        suite.addTestSuite(DefaultSearchBucketBeanTest.class);
        suite.addTestSuite(DefaultBrowsableDataPickerBeanTest.class);
        //$JUnit-END$
        return suite;
    }
}