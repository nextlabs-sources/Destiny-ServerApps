/*
 * Created on Apr 29, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIData;
import javax.faces.webapp.UIComponentTag;

/**
 * This is the row tag test class
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/RowTagTest.java#1 $
 */

public class RowTagTest extends BaseJSFTest {

    /**
     * This test verifies the basic for the row tag class.
     */
    public void testDataTableTagClassBasics() {
        RowTag tag = new RowTag();
        assertTrue("The Destiny table row tag should extend the base UI tag", tag instanceof UIComponentTag);
        assertEquals("The Destiny table row should have its own component type", RowTag.COMPONENT_TYPE, tag.getComponentType());
        assertEquals("The Destiny table row should have its own renderer type", RowTag.RENDERER_TYPE, tag.getRendererType());
    }

    /**
     * This test verifies that the attributes of the tag are set properly on the
     * component
     */
    public void testDataTableTagAttributes() {
        RowTag tag = new RowTag();
        assertNull("The style class should be null by default", tag.getStyleClass());
        final String styleClassName = "foo";
        tag.setStyleClass(styleClassName);
        assertEquals("The setter and getter for style class should work", styleClassName, tag.getStyleClass());
        UIData comp = new UIData();
        tag.setProperties(comp);
        String style = (String) comp.getAttributes().get(RowTag.STYLE_ATTR_NAME);
        assertNotNull("The component should have the style attribute", style);
        assertEquals("The component should receive the stripe attribute", styleClassName, style);
    }
}