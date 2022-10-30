/*
 * Created on May 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIComponent;

import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * This is the test class for the row UI control
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/controls/UIRowTest.java#1 $
 */

public class UIRowTest extends BaseDestinyTestCase {

    /**
     * Tests the basic features of the UI control class
     */
    public void testRowControlBasics() {
        UIRow rowControl = new UIRow();
        assertTrue("Row control should be a JSF component", rowControl instanceof UIComponent);
        assertFalse("Row control should not render its own children", rowControl.getRendersChildren());
    }

    /**
     * This test verifies that the correct renderer type is returned.
     */
    public void testRowControlAssociatedTypes() {
        UIRow rowControl = new UIRow();
        assertEquals("Row control should not change its component type", "javax.faces.Data", rowControl.getFamily());
    }
}