/*
 * Created on Mar 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.webapp.UIComponentTag;

import com.bluejungle.destiny.webui.controls.UIJSInclude;

/**
 * This is the test class for the JS include tag
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/JSIncludeTagTest.java#1 $
 */

public class JSIncludeTagTest extends BaseJSFTest {

    /**
     * This test verifies the basic characteristics of the JS include tag class
     */
    public void testJSIncludeTagBasics() {
        JSIncludeTag tag = new JSIncludeTag();
        assertTrue("JS include tag should extends the basic JSF tag", tag instanceof UIComponentTag);
        assertEquals("JS include tag should have its own component type", JSIncludeTag.COMPONENT_TYPE, tag.getComponentType());
        assertNull("JS include tag should have no renderer type", tag.getRendererType());
    }

    /**
     * This test verifies that the tag stores property values properly and
     * passes them appropriately to the component object.
     */
    public void testCSSIncludeProperties() {
        final String myLocation = "myOwnLocation";
        JSIncludeTag tag = new JSIncludeTag();
        tag.setLocation(myLocation);
        assertEquals("JS Include tag should remember the location property", myLocation, tag.getLocation());
        
        //Pass on the values to the component
        UIJSInclude includeComp = new UIJSInclude ();
        tag.setProperties(includeComp);
        assertEquals("JSInclude Tag should pass the location value to the UI component", includeComp.getValue(), myLocation);
    }

}