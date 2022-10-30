/*
 * Created on Apr 28, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import junit.framework.TestCase;

/**
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/SelectedTabChangeListenerTagTest.java#1 $
 */

public class SelectedTabChangeListenerTagTest extends TestCase {
    private SelectedTabChangeListenerTag tagToTest;
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        this.tagToTest = new SelectedTabChangeListenerTag();
    }

    /*
     * Test method for 'com.bluejungle.destiny.webui.tags.SelectedTabChangeListenerTag.release()'
     */
    public void testRelease() {
        // Can only make sure that it doesn't blow up
        this.tagToTest.release();
    }

    /*
     * Test method for 'com.bluejungle.destiny.webui.tags.SelectedTabChangeListenerTag.setType(String)'
     */
    public void testSetType() {
        // Just test NPE
        try {
            this.tagToTest.setType(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException exception) {            
        }
    }
}
