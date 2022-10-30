/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.webapp.UIComponentTag;

import com.bluejungle.destiny.webui.controls.UIBarItem;

/**
 * This is the test class for the bar item tag class
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/BarItemTagTest.java#1 $
 */

public class BarItemTagTest extends BaseJSFTest {

    /**
     * This test verifies the basic characteristics of the bar item tag class
     */
    public void testBarItemTagBasics() {
        BarItemTag tag = new BarItemTag();
        assertTrue("Bar item tag should extends the basic JSF tag", tag instanceof UIComponentTag);
        assertEquals("Bar Item tag should have its own component type", BarItemTag.COMPONENT_TYPE, tag.getComponentType());
        assertNull("Bar Item tag should have no renderer type", tag.getRendererType());
    }

    /**
     * This class verifies that the setter and getters work properly on the tag
     * class
     */
    public void testBarItemTagSetterGetter() {
        BarItemTag tag = new BarItemTag();
        final String barClass = "foo";
        tag.setBarClassName(barClass);
        final String barContainerClass = "barCont";
        tag.setContainerClassName(barContainerClass);
        final String orientation = UIBarItem.ORIENTATION_HOR;
        tag.setOrientation(orientation);
        final String maxBarSize = "150";
        tag.setMaxBarSize(maxBarSize);
        final String maxRange = "200";
        tag.setMaxRange(maxRange);
        final String value = "myValue";
        tag.setValue(value);

        assertEquals("Setters should set to the proper class member", barClass, tag.barClassName);
        assertEquals("Setters should set to the proper class member", barContainerClass, tag.containerClassName);
        assertEquals("Setters should set to the proper class member", orientation, tag.orientation);
        assertEquals("Setters should set to the proper class member", maxBarSize, tag.maxBarSize);
        assertEquals("Setters should set to the proper class member", maxRange, tag.maxRange);
        assertEquals("Setters should set to the proper class member", value, tag.value);
    }

    /**
     * This test verifies that the orientation value can be set properly
     */
    public void testBarItemTagOrientationRestrictions() {
        BarItemTag tag = new BarItemTag();
        assertEquals("By default, orientation should be horizontal", UIBarItem.ORIENTATION_HOR, tag.orientation);
        boolean exThrown = false;
        try {
            tag.setOrientation("badValue");
        } catch (IllegalArgumentException e) {
            exThrown = true;
        }
        assertTrue("Bar item tag should not accept bad values for orientation", exThrown);
        exThrown = false;
        try {
            tag.setOrientation("Horizontal");
            assertEquals("Setting the orientation to horizontal should work", UIBarItem.ORIENTATION_HOR, tag.orientation);
            tag.setOrientation("Vertical");
            assertEquals("Setting the orientation to vertical should work", UIBarItem.ORIENTATION_VER, tag.orientation);
        } catch (IllegalArgumentException e) {
            exThrown = true;
        }
        assertFalse("No exception should be thrown when setting correct orientation", exThrown);
    }

    /**
     * This test verifies that the tag stores property values properly and
     * passes them appropriately to the component object.
     */
    public void testBarItemTagProperties() {
        final String barClassName = "mybarName";
        final String containerClassName = "foo";
        final String maxBarSize = "100";
        final String maxRange = "200";
        final String value = "myValue";
        BarItemTag tag = new BarItemTag();
        tag.setBarClassName(barClassName);
        tag.setContainerClassName(containerClassName);
        tag.setMaxBarSize(maxBarSize);
        tag.setMaxRange(maxRange);
        tag.setValue(value);

        //Pass on the values to the component
        UIBarItem uiComp = new UIBarItem();
        tag.setProperties(uiComp);
        assertEquals("Bar Item Tag should pass the value to the UI component", uiComp.getValue(), value);
        assertEquals("Bar Item Tag should pass the bar class name to the UI component", uiComp.getAttributes().get(UIBarItem.BAR_CLASS_ATTR_NAME), barClassName);
        assertEquals("Bar Item Tag should pass the container class name to the UI component", uiComp.getAttributes().get(UIBarItem.CONTAINER_CLASS_ATTR_NAME), containerClassName);
        assertEquals("Bar Item Tag should pass the max bar size to the UI component", uiComp.getAttributes().get(UIBarItem.MAX_BAR_SIZE), new Integer(maxBarSize));
        assertEquals("Bar Item Tag should pass the max range to the UI component", uiComp.getAttributes().get(UIBarItem.MAX_RANGE_ATTR_NAME), new Long(maxRange));
    }
}