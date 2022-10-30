/*
 * Created on Mar 16, 2005
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
 * This is the test class for the CSS inclusion control test
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/controls/UICSSIncludeTest.java#1 $
 */

public class UICSSIncludeTest extends BaseDestinyTestCase {

    /**
     * Tests the basic features of the UI control class
     */
    public void testUICSSIncludeControlBasics() {
        UICSSInclude includeControl = new UICSSInclude();
        assertTrue("CSS include control should be a JSF component", includeControl instanceof UIComponent);
        assertTrue("CSS include control should extends a regular output control", includeControl instanceof UIOutput);
    }

    /**
     * This test verifies that the correct renderer type is returned.
     */
    public void testUICSSIncludeAssociatedTypes() {
        UICSSInclude includeControl = new UICSSInclude();
        assertEquals("CSS include control should return its renderer type", UICSSInclude.RENDERER_TYPE, includeControl.getRendererType());
        assertEquals("CSS include control should not change its component family", UIOutput.COMPONENT_FAMILY, includeControl.getFamily());
        assertEquals("CSS include control should not change its component type", UIOutput.COMPONENT_TYPE, UICSSInclude.COMPONENT_TYPE);
    }
}