/*
 * Created on May 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.AgentConfigViewTestSuite;
import com.bluejungle.destiny.mgmtconsole.status.StatusTestSuite;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.UserGroupsViewTestSuite;

/**
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/ManagementConsoleTestSuite.java#4 $
 */

public class ManagementConsoleTestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ManagementConsoleTestSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.bluejungle.destiny.mgmtconsole");
        //$JUnit-BEGIN$
        suite.addTest(AgentConfigViewTestSuite.suite());
        suite.addTest(StatusTestSuite.suite());
        suite.addTest(UserGroupsViewTestSuite.suite());
        //suite.addTestSuite(UserCreationBeanImplTest.class);
        //$JUnit-END$
        return suite;
    }
}
