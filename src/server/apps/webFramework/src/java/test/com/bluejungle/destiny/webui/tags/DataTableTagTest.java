/*
 * Created on Apr 29, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIData;

import org.apache.myfaces.taglib.html.ext.HtmlDataTableTag;

/**
 * This is the data table tag test class.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/DataTableTagTest.java#1 $
 */

public class DataTableTagTest extends BaseJSFTest {

    /**
     * This test verifies the basic for the tag class.
     */
    public void testDataTableTagClassBasics() {
        DataTableTag tag = new DataTableTag();
        assertTrue("The Destiny table tag should extend the myFaces table tag", tag instanceof HtmlDataTableTag);
        assertEquals("The Destiny table tag should have its own component type", DataTableTag.COMPONENT_TYPE, tag.getComponentType());
        assertEquals("The Destiny table tag should have its own renderer type", DataTableTag.RENDERER_TYPE, tag.getRendererType());
    }

    /**
     * This test verifies that the attributes of the tag are set properly on the
     * component
     */
    public void testDataTableTagAttributes() {
        DataTableTag tag = new DataTableTag();
        assertFalse("The rows should not have stripes by default", tag.getStripeRows());
        tag.setStripeRows(true);
        assertTrue("The setter and getter for stripe rows should work", tag.getStripeRows());
        UIData comp = new UIData();
        tag.setProperties(comp);
        Boolean stripe = (Boolean) comp.getAttributes().get(DataTableTag.STRIPE_ATTR_NAME);
        assertNotNull("The stripe rows attribe should not be null", stripe);
        assertEquals("The component should receive the stripe attribute", new Boolean(true), stripe);
    }
}