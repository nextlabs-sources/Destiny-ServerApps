/*
 * Created on Aug 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.renderkit.html.HtmlTextRendererBase;

import com.bluejungle.destiny.webui.controls.UIBarItem;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * This is the bar item renderer test class
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/BarItemRendererTest.java#1 $
 */

public class BarItemRendererTest extends BaseJSFTest {

    /**
     * This test verifies the basics of the renderer class
     */
    public void testBarItemRendererClassBasics() {
        BarItemRenderer renderer = new BarItemRenderer();
        assertTrue("The BarItemRenderer class should extend the correct base class", renderer instanceof HtmlTextRendererBase);
        assertFalse("The BarItemRenderer should not render its own children", renderer.getRendersChildren());
    }

    /**
     * This test verifies that the bar class name is picked properly
     */
    public void testBarItemRendererGetBarClassName() {
        BarItemRenderer renderer = new BarItemRenderer();

        //Test null argument
        boolean exThrown = false;
        try {
            renderer.getBarClassName(null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("getBarClassName function should not accept null arg", exThrown);

        //Test default value
        UIComponent comp = new UIInput();
        String result = renderer.getBarClassName(comp);
        assertEquals("The default class name should be used if no class attribute is present", BarItemRenderer.DEFAULT_BAR_CLASS_NAME, result);

        //Test custom value
        final String classToExpect = "foo";
        comp.getAttributes().put(UIBarItem.BAR_CLASS_ATTR_NAME, classToExpect);
        result = renderer.getBarClassName(comp);
        assertEquals("The class attribute should be used", classToExpect, result);
    }

    /**
     * This test verifies that the bar maximum size is calculated properly
     */
    public void testBarItemRendererGetMaxBarSize() {
        BarItemRenderer renderer = new BarItemRenderer();
        //Test null argument
        boolean exThrown = false;
        try {
            renderer.getMaxBarSize(null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("getMaxBarSize function should not accept null arg", exThrown);

        //Test default value
        UIComponent comp = new UIInput();
        long result = renderer.getMaxBarSize(comp);
        assertEquals("The default bar size should be used if no class attribute is present", BarItemRenderer.DEFAULT_MAX_BAR_SIZE, result);

        //Test custom value
        final Integer maxBarSizeToExpect = new Integer(1234);
        comp.getAttributes().put(UIBarItem.MAX_BAR_SIZE, maxBarSizeToExpect);
        result = renderer.getMaxBarSize(comp);
        assertEquals("The class attribute should be used", maxBarSizeToExpect, new Integer((new Long(result)).intValue()));
    }

    /**
     * This test verifies that the expression displaying the style and size is
     * working properly.
     */
    public void testBarItemRendererStyleExpression() {
        BarItemRenderer renderer = new BarItemRenderer();
        //Do not put orientation explicitely
        final UIInput comp = new UIInput();
        comp.setValue("20");
        comp.getAttributes().put(UIBarItem.MAX_BAR_SIZE, new Integer(100));
        comp.getAttributes().put(UIBarItem.MAX_RANGE_ATTR_NAME, new Long(200));
        String result = renderer.getStyleExpression(FacesContext.getCurrentInstance(), comp);
        assertEquals(HTML.HEIGHT_ATTR + ": 10px;", result);

        //Test with an explicit orientation
        comp.getAttributes().put(UIBarItem.ORIENTATION_ATTR_NAME, UIBarItem.ORIENTATION_HOR);
        comp.setValue("40");
        result = renderer.getStyleExpression(FacesContext.getCurrentInstance(), comp);
        assertEquals(HTML.WIDTH_ATTR + ": 20px;", result);

        //Test with a value too large (should never happen in theory)
        comp.setValue("4000");
        result = renderer.getStyleExpression(FacesContext.getCurrentInstance(), comp);
        assertEquals(HTML.WIDTH_ATTR + ": 100px;", result);
    }

    /**
     * This test verifies that the bar is rendered with at least 1 pixel in
     * size, so that it is always visible.
     */
    public void testBarItemRendererMinimumSize() {
        BarItemRenderer renderer = new BarItemRenderer();
        //Test the 0 value case
        final UIInput comp = new UIInput();
        comp.getAttributes().put(UIBarItem.ORIENTATION_ATTR_NAME, UIBarItem.ORIENTATION_HOR);
        comp.setValue("0");
        comp.getAttributes().put(UIBarItem.MAX_BAR_SIZE, new Integer(100));
        comp.getAttributes().put(UIBarItem.MAX_RANGE_ATTR_NAME, new Long(200));
        String result = renderer.getStyleExpression(FacesContext.getCurrentInstance(), comp);
        assertEquals(HTML.WIDTH_ATTR + ": 1px;", result);

        //Test the non-zero (but very small in comparison) value case
        comp.setValue("5");
        comp.getAttributes().put(UIBarItem.MAX_BAR_SIZE, new Integer(100));
        comp.getAttributes().put(UIBarItem.MAX_RANGE_ATTR_NAME, new Long(2000000));
        result = renderer.getStyleExpression(FacesContext.getCurrentInstance(), comp);
        assertEquals(HTML.WIDTH_ATTR + ": 1px;", result);
    }
}