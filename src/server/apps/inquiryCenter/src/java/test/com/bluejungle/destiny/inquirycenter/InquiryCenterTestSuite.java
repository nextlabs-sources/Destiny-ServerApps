/*
 * Created on May 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.DataObjectTestSuite;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.ModelObjectTestSuite;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.policy.PolicyComponentDatapickerSuite;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.user.UserComponentDatapickerSuite;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.ReportHelpersTestSuite;

/**
 * This is the master test suite for the inquiry center web application.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/InquiryCenterTestSuite.java#1 $
 */

public class InquiryCenterTestSuite {

     /**
     * Returns the test suite
     * 
     * @return the test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Inquiry Center Web Application");
        suite.addTest(DataObjectTestSuite.suite());
        suite.addTest(ModelObjectTestSuite.suite());
        suite.addTest(PolicyComponentDatapickerSuite.suite());
        suite.addTest(UserComponentDatapickerSuite.suite());
        suite.addTest(ReportHelpersTestSuite.suite());
        return suite;
    }
}