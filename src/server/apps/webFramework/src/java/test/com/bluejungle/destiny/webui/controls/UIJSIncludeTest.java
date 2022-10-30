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
 * This is the JS include component test class.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/controls/UIJSIncludeTest.java#1 $
 */

public class UIJSIncludeTest extends BaseDestinyTestCase {

    /**
     * Tests the basic features of the UI control class
     */
    public void testUIJSIncludeControlBasics() {
        UIJSInclude includeControl = new UIJSInclude();
        assertTrue("JS include control should be a JSF component", includeControl instanceof UIComponent);
        assertTrue("JS include control should extends a regular output control", includeControl instanceof UIOutput);
    }

    /**
     * This test verifies that the correct renderer type is returned.
     */
    public void testUIJSIncludeAssociatedTypes() {
        UIJSInclude includeControl = new UIJSInclude();
        assertEquals("JS include control should return its renderer type", UIJSInclude.RENDERER_TYPE, includeControl.getRendererType());
        assertEquals("JS include control should not change its component family", UIOutput.COMPONENT_FAMILY, includeControl.getFamily());
        assertEquals("JS include control should not change its component type", UIOutput.COMPONENT_TYPE, UICSSInclude.COMPONENT_TYPE);
    }
}