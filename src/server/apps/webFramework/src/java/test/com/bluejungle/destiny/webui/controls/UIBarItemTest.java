/*
 * Created on May 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;

import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * This is the test class for the bar item control
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/controls/UIBarItemTest.java#1 $
 */

public class UIBarItemTest extends BaseDestinyTestCase {

    /**
     * Tests the basic features of the UI control class
     */
    public void testBarItemControlBasics() {
        UIBarItem barItemControl = new UIBarItem();
        assertTrue("Bar item control should be a JSF component", barItemControl instanceof UIComponent);
        assertTrue("Bar item control should extends a regular output control", barItemControl instanceof UIOutput);
        assertFalse("Bar item control should not render its own children", barItemControl.getRendersChildren());
    }

    /**
     * This test verifies that the correct renderer type is returned.
     */
    public void testBarItemControlAssociatedTypes() {
        UIBarItem barItemControl = new UIBarItem();
        assertEquals("Bar item control should return its renderer type", UIBarItem.RENDERER_TYPE, barItemControl.getRendererType());
        assertEquals("Bar item control should not change its component family", UIOutput.COMPONENT_FAMILY, barItemControl.getFamily());
        assertEquals("Bar item control should not change its component type", UIOutput.COMPONENT_TYPE, UIBarItem.COMPONENT_TYPE);
    }
}