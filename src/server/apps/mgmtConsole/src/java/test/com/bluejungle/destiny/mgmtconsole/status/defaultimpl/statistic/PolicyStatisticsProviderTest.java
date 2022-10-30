/*
 * Created on Apr 18, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic;

import java.rmi.RemoteException;
import java.util.Calendar;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import com.bluejungle.destiny.framework.types.ServiceNotReadyFault;
import com.bluejungle.destiny.framework.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.IPolicyStatisticsProvider;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.PolicyStatisticsProvider;
import com.bluejungle.destiny.services.policy.DefaultPolicyEditorIF;
import com.bluejungle.destiny.services.policy.PolicyEditorIF;

/**
 * Test for PolicyStatisticsProvider
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/statistic/PolicyStatisticsProviderTest.java#1 $
 */
public class PolicyStatisticsProviderTest extends TestCase {

    private static final Long TEST_POLICY_COUNT = new Long(757574);

    private PolicyStatisticsProvider providerToTest;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PolicyStatisticsProviderTest.class);        
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        PolicyEditorIF testPolicyService = new TestPolicyService(TEST_POLICY_COUNT.intValue());
        this.providerToTest = new PolicyStatisticProviderExtension(testPolicyService);
    }
    
    /**
     * Test PolicyStatisticsProvider.pullStatistic()
     */
    public void testPullStatistic() {
        IStatisticSet policyStats = this.providerToTest.pullStatistic();
        IStatistic policyCountStatistic = policyStats.getStatistic(IPolicyStatisticsProvider.POLICY_COUNT_STAT_KEY);
        assertEquals("testPullStatistic - Ensure policy stat value is as expected", TEST_POLICY_COUNT, policyCountStatistic.getValue());
    }

    private class PolicyStatisticProviderExtension extends PolicyStatisticsProvider {

        private PolicyEditorIF policyService;

        public PolicyStatisticProviderExtension(PolicyEditorIF policyService) {
            super();
            this.policyService = policyService;
        }

        /**
         * @see com.bluejungle.destiny.mgmtconsole.status.defaultimpl.PolicyStatisticsProvider#getPolicyEditorService()
         */
        protected PolicyEditorIF getPolicyEditorService() throws ServiceNotReadyFault, UnauthorizedCallerFault, RemoteException, ServiceException {
            return this.policyService;
        }
    }

    private class TestPolicyService extends DefaultPolicyEditorIF {

        private int numDeployedPolicies;

        public TestPolicyService(int numDeployedPolicies) {
            super();
            this.numDeployedPolicies = numDeployedPolicies;
        }

        /**
         * @see com.bluejungle.destiny.services.policy.PolicyEditorIF#getNumDeployedPolicies()
         */
        public int getNumDeployedPolicies() throws RemoteException, ServiceNotReadyFault {
            return this.numDeployedPolicies;
        }

        /**
         * @see com.bluejungle.destiny.services.policy.PolicyEditorIF#getLatestDeploymentTime()
         */
        public Calendar getLatestDeploymentTime() throws RemoteException, ServiceNotReadyFault {
            return null;
        }

    }
}
