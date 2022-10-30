/*
 * Created on Mar 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.webapp.UIComponentTag;

import com.bluejungle.destiny.webui.controls.UICSSInclude;

/**
 * This is the test class for the CSS include tag
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/CSSIncludeTagTest.java#1 $
 */

public class CSSIncludeTagTest extends BaseJSFTest {

    /**
     * This test verifies the basic characteristics of the CSS include tag class
     */
    public void testCSSIncludeTagBasics() {
        CSSIncludeTag tag = new CSSIncludeTag();
        assertTrue("CSS include tag should extends the basic JSF tag", tag instanceof UIComponentTag);
        assertEquals("CSS include tag should have its own component type", CSSIncludeTag.COMPONENT_TYPE, tag.getComponentType());
        assertNull("CSS include tag should have no renderer type", tag.getRendererType());
    }

    /**
     * This test verifies that the tag stores property values properly and
     * passes them appropriately to the component object.
     */
    public void testCSSIncludeProperties() {
        final String myLocation = "myOwnLocation";
        CSSIncludeTag tag = new CSSIncludeTag();
        tag.setLocation(myLocation);
        assertEquals("CSS Include tag should remember the location property", myLocation, tag.getLocation());
        
        //Pass on the values to the component
        UICSSInclude includeComp = new UICSSInclude ();
        tag.setProperties(includeComp);
        assertEquals("CSSInclude Tag should pass the location value to the UI component", includeComp.getValue(), myLocation);
    }

}