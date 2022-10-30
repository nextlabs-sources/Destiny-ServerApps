/*
 * Created on May 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import junit.framework.TestSuite;

/**
 * This is the test suite for the inquiry center web application model objects.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ModelObjectTestSuite.java#1 $
 */

public class ModelObjectTestSuite {

    /**
     * Returns the data object test suite
     * 
     * @return the data object test suite
     */
    public static final TestSuite suite() {
        TestSuite suite = new TestSuite("Model Objects");
        suite.addTest(new TestSuite(ReportExecutorTest.class, "Report executor"));
        suite.addTest(new TestSuite(ReportPageBeanImplTest.class, "Base reports page bean"));
        suite.addTest(new TestSuite(MyReportsPageBeanImplTest.class, "My reports page bean"));
        return suite;
    }
}