/*
 * Created on May 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;

import org.apache.myfaces.custom.crosstable.HtmlColumnsTag;
import org.apache.myfaces.renderkit.JSFAttr;

/**
 * This is the test class for the "columns" tag class
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/ColumnsTagTest.java#1 $
 */

public class ColumnsTagTest extends BaseJSFTest {

    /**
     * This test verifies the basic characteristics of the columns tag class
     */
    public void testColumnsTagBasics() {
        ColumnsTag tag = new ColumnsTag();
        assertTrue("Columns tag should extends the basic JSF tag", tag instanceof HtmlColumnsTag);
        assertEquals("Columns tag should have its own component type", ColumnsTag.COMPONENT_TYPE, tag.getComponentType());
        assertNull("Columns tag should have no renderer type", tag.getRendererType());
    }

    /**
     * This test verifies that the column tag passes the properties properly
     */
    public void testColumnsTagProperties() {
        ColumnsTag tag = new ColumnsTag();
        final String footerClassName = "foo";
        tag.setFooterClass(footerClassName);
        assertEquals("The footerClass property should be set properly", footerClassName, tag.getFooterClass());

        UIComponent comp = new UIInput();
        tag.setProperties(comp);
        assertEquals("The footer class value should be passed to the UI component", comp.getAttributes().get(JSFAttr.FOOTER_CLASS_ATTR), footerClassName);
    }
}