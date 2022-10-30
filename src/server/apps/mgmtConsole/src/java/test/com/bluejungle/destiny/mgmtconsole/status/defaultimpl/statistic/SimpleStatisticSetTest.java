/*
 * Created on Apr 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic;

import java.util.Calendar;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/statistic/SimpleStatisticSetTest.java#1 $
 */

public class SimpleStatisticSetTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SimpleStatisticSetTest.class);
    }

    public void testGetSetStatistic() {
        SimpleStatisticSet setToTest = new SimpleStatisticSet();
        
        
        String statName = "statName";
        IStatistic stat = new SimpleStatistic(new Integer(5), Calendar.getInstance());
        
        assertNull("testGetSetStatistic - Ensure stat is initially null", setToTest.getStatistic(statName));
        
        // Now add it
        setToTest.setStatistic(statName, stat);
        
        // Test that it is returned
        assertEquals("testGetSetStatistic - Ensure stat which is set is returned", stat, setToTest.getStatistic(statName));
        
        // Test null pointers
        NullPointerException expectedException = null;
        try {
            setToTest.getStatistic(null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException exception) {
            expectedException = exception;
        }

        assertNotNull("testGetSetStatistic - Ensure NullPointerException was thrown null get stat name", expectedException);
        
        expectedException = null;
        try {
            setToTest.setStatistic(null, stat);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException exception) {
            expectedException = exception;
        }

        assertNotNull("testGetSetStatistic - Ensure NullPointerException was thrown for null set stat name", expectedException);
        
        expectedException = null;
        try {
            setToTest.setStatistic(statName, null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException exception) {
            expectedException = exception;
        }

        assertNotNull("testGetSetStatistic - Ensure NullPointerException was thrown for null set stat value", expectedException);
    }
    
    public void testIterator() {
        SimpleStatisticSet setToTest = new SimpleStatisticSet();
        
        Iterator iterator = setToTest.iterator();
        assertNotNull("testIterator - Ensure iterator is initially not null", iterator);
        assertTrue("testIterator - Ensure iterator is initially empty", !iterator.hasNext());
        
        // add a stat
        String statName = "statName";
        IStatistic stat = new SimpleStatistic(new Integer(5), Calendar.getInstance());
        setToTest.setStatistic(statName, stat);
        
        iterator = setToTest.iterator();
        assertNotNull("testIterator - Ensure iterator is still not null", iterator);
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            IStatistic nextStat = (IStatistic) iterator.next();
            assertEquals("testIterator - Ensure item return from iterator is that which was set", stat, nextStat);
        }
        assertEquals("testIterator - Ensure iterator count is correct", 1, count);
    }
}
