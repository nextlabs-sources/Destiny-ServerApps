/*
 * Created on May 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl.browsablehostpicker.HostSelectableItemSourceImplTest;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/agentconfig/defaultimpl/AgentConfigViewTestSuite.java#1 $
 */

public class AgentConfigViewTestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AgentConfigViewTestSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.bluejungle.destiny.mgmtconsole.agentconfig.defaultimpl");
        //$JUnit-BEGIN$
        suite.addTestSuite(NewProfileBeanImplTest.class);
        suite.addTestSuite(ProfileBeanUtilsTest.class);
        suite.addTestSuite(ProfileBeanImplTest.class);
        suite.addTestSuite(AgentConfigurationBeanImplTest.class);
        suite.addTestSuite(HostSelectableItemSourceImplTest.class);
        //$JUnit-END$
        return suite;
    }
}
