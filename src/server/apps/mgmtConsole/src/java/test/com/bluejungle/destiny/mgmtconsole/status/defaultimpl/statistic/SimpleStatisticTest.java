/*
 * Created on Apr 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic;

import java.util.Calendar;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/statistic/SimpleStatisticTest.java#1 $
 */

public class SimpleStatisticTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SimpleStatisticTest.class);
    }

    public void testConstructor() {
        // Test exceptions
        NullPointerException expectedException = null;
        try {
            new SimpleStatistic(null, Calendar.getInstance());
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException exception) {
            expectedException = exception;
        }

        assertNotNull("testConstructor - Ensure NullPointerException was thrown for null value", expectedException);      
        
        expectedException = null;
        try {
            new SimpleStatistic(new Integer(5), null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException exception) {
            expectedException = exception;
        }

        assertNotNull("testConstructor - Ensure NullPointerException was thrown for null timestamp", expectedException);  
    }
    
    public void testGetValue() {
        Object value = new Integer(5);
        SimpleStatistic statToTest = new SimpleStatistic(value, Calendar.getInstance());
        
        assertEquals("testGetValue - Ensure value is correct", value, statToTest.getValue());
    }

    public void testGetLastUpdatedTimestamp() {
        Calendar timestamp = Calendar.getInstance();
        SimpleStatistic statToTest = new SimpleStatistic(new Integer(5), timestamp);
        
        assertEquals("testGetLastUpdateTimestamp- Ensure timestamp is correct", timestamp, statToTest.getLastUpdatedTimestamp());     
    }

}
