/*
 * Created on May 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.flip;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is the test suite for the data flipper
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/framework/flip/DataFlipperTestSuite.java#1 $
 */

public class DataFlipperTestSuite {

     /**
     * Returns the set of tests to be run in the test suite
     * 
     * @return the set of tests to be run in the test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("JSF Data flipper");
        suite.addTest(new TestSuite(DataFlipperTest.class, "data flipper"));
        return suite;
    }
}