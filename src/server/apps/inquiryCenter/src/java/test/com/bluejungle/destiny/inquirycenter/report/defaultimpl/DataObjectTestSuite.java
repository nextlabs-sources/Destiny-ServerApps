/*
 * Created on May 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import junit.framework.TestSuite;

/**
 * This is the test suite for the inquiry center data objects
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/DataObjectTestSuite.java#1 $
 */

public class DataObjectTestSuite {

    /**
     * Returns the data object test suite
     * 
     * @return the data object test suite
     */
    public static final TestSuite suite() {
        TestSuite suite = new TestSuite("UI Data Objects");
        suite.addTest(new TestSuite(ReportDataObjectTest.class, "Report data object"));
        suite.addTest(new TestSuite(ReportDetailResultTest.class, "Report detail result data object"));
        suite.addTest(new TestSuite(ReportSummaryDateGroupingResultTest.class, "Report summary result by date data object"));
        suite.addTest(new TestSuite(ReportSummaryResultTest.class, "Report summary result by date data object"));
        suite.addTest(new TestSuite(ResultsStatisticsTest.class, "Result statistics data object"));
        return suite;
    }
}