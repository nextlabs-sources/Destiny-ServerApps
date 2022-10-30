/*
 * Created on Jul 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/helpers/ReportHelpersTestSuite.java#1 $
 */

public class ReportHelpersTestSuite {

    /**
     * Main function
     * 
     * @param args
     *            none
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ReportHelpersTestSuite.suite());
    }

    /**
     * Test suite
     * 
     * @return the test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Report helper class tests");
        suite.addTestSuite(ExpressionCutterTest.class);
        suite.addTestSuite(UserComponentEntityResolverTest.class);
        return suite;
    }
}