/*
 * Created on Apr 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic;

import java.util.Calendar;

import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/statistic/TimedPullStatisticProviderTest.java#1 $
 */

public class TimedPullStatisticProviderTest extends BaseDestinyTestCase {

    private static final long TEST_PERIOD = 2000l;
    private static final SimpleStatisticSet TEST_STAT_SET = new SimpleStatisticSet();
    private static final String TEST_STAT_NAME = "foo";
    private static final IStatistic TEST_STAT = new SimpleStatistic(new Integer(4), Calendar.getInstance());    
    static
    {        
        TEST_STAT_SET.setStatistic(TEST_STAT_NAME, TEST_STAT);
    }
    
    private TestTimedPullStatisticProvider testStatisticsProvider;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TimedPullStatisticProviderTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        HashMapConfiguration testStatProviderConfig = new HashMapConfiguration();
        testStatProviderConfig.setProperty(TestTimedPullStatisticProvider.PULL_DELAY_PROPERTY_NAME, new Long(TEST_PERIOD));
        ComponentInfo testStatProviderCompInfo = new ComponentInfo("TestStatProvider", TestTimedPullStatisticProvider.class.getName(), null, LifestyleType.SINGLETON_TYPE, testStatProviderConfig);
        this.testStatisticsProvider = (TestTimedPullStatisticProvider) componentManager.getComponent(testStatProviderCompInfo);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for TimedPullStatisticProviderTest.
     * @param arg0
     */
    public TimedPullStatisticProviderTest(String arg0) {
        super(arg0);
    }

    public void testGetStatistics() {        
        assertEquals("testGetStatistics - Ensure stat pulled is correct", TEST_STAT_SET, this.testStatisticsProvider.getStatistics());                
    }

    public void testTimedPullStatisticProvider() throws InterruptedException {        
        assertEquals("testTimedPullStatisticProvider - Ensure number times pulled is initiall 1", 1, this.testStatisticsProvider.getNumPulled());
        
        Thread.sleep(TEST_PERIOD + 100);  // Wait test period plus buffer to introduce test lag
        
        assertEquals("testTimedPullStatisticProvider - Ensure number times pulled is 2", 2, this.testStatisticsProvider.getNumPulled());
        
        Thread.sleep(TEST_PERIOD);  // Wait test period 
        
        assertEquals("testTimedPullStatisticProvider - Ensure number times pulled is 3", 3, this.testStatisticsProvider.getNumPulled());
    }

    public static class TestTimedPullStatisticProvider extends TimedPullStatisticProvider {      
        private int numPulled;
                                
        /**
         * @see com.bluejungle.framework.comp.IInitializable#init()
         */
        public void init() {
            super.init();
            numPulled = 0;
        }
        
        /**
         * @see com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.TimedPullStatisticProvider#pullStatistic()
         */
        public IStatisticSet pullStatistic() {
            this.numPulled++;
            return TEST_STAT_SET;
        }
        
        public int getNumPulled() {
          return this.numPulled;
        }
        
    }
}
