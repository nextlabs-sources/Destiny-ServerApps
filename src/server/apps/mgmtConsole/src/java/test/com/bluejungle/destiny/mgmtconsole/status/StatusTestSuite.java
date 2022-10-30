/*
 * Created on Apr 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status;

import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.AgentStatisticsProviderTest;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.ComponentDataBeanTest;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.ComponentStatusBeanTest;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.LogCountStatisticProviderTest;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.ServerStatisticsBeanImplTest;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.PolicyStatisticsProviderTest;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.SimpleStatisticSetTest;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.SimpleStatisticTest;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.TimedPullStatisticProviderTest;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/status/StatusTestSuite.java#1 $
 */

public class StatusTestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(StatusTestSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.bluejungle.destiny.mgmtconsole.status");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(SimpleStatisticTest.class, "SimpleStatisticTest"));
        suite.addTest(new TestSuite(SimpleStatisticSetTest.class, "SimpleStatisticSetTest"));
        suite.addTest(new TestSuite(TimedPullStatisticProviderTest.class, "TimedPullStatisticProviderTest"));
        suite.addTest(new TestSuite(ServerStatisticsBeanImplTest.class, "ServerStatisticsBeanImplTest"));
        suite.addTest(new TestSuite(LogCountStatisticProviderTest.class, "LogCountStatisticsProviderTest"));
        suite.addTest(new TestSuite(AgentStatisticsProviderTest.class, "AgentStatisticsProviderTest"));
        suite.addTest(new TestSuite(PolicyStatisticsProviderTest.class, "PolicyStatisticsProviderTest"));
        suite.addTest(new TestSuite(ComponentDataBeanTest.class, "ComponentDataBeanTest"));
        suite.addTest(new TestSuite(ComponentStatusBeanTest.class, "ComponentStatusBeanTest"));
        //$JUnit-END$
        return suite;
    }
}
