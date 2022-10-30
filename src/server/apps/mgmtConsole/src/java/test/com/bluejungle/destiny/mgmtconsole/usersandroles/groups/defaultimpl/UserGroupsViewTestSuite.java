/*
 * Created on Sep 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/UserGroupsViewTestSuite.java#1 $
 */

public class UserGroupsViewTestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UserGroupsViewTestSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl");
        //$JUnit-BEGIN$
        suite.addTestSuite(UserGroupServiceFacadeImplTest.class);
        suite.addTestSuite(UserGroupsViewBeanImplTest.class);
        suite.addTestSuite(UserGroupMenuItemBeanImplTest.class);
        suite.addTestSuite(NewUserGroupBeanImplTest.class);
        suite.addTestSuite(MemberBeanImplTest.class);
        suite.addTestSuite(DefaultAccessAssignmentBeanImplTest.class);
        suite.addTestSuite(ExistingUserGroupBeanImplTest.class);
        //$JUnit-END$
        return suite;
    }
}
