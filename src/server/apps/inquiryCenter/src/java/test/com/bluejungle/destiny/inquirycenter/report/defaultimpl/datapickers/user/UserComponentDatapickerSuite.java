/*
 * Created on May 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.user;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/user/UserComponentDatapickerSuite.java#1 $
 */

public class UserComponentDatapickerSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UserComponentDatapickerSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("User picker");
        suite.addTest(new TestSuite(TestSelectableUserComponentItemSource.class, "User picker"));
        return suite;
    }
}