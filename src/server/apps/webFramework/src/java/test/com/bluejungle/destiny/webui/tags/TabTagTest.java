/*
 * Created on Apr 28, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import com.bluejungle.destiny.webui.controls.UITab;

import java.util.Map;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/TabTagTest.java#1 $
 */

public class TabTagTest extends TestCase {

    private static final String EXPECTED_COMPONENT_TYPE = "com.bluejungle.destiny.Tab";
    private static final String EXPECTED_RENDERED_TYPE = "com.bluejungle.destiny.TabRenderer";

    private TabTag tagToTest;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        this.tagToTest = new TabTag();
    }

    /*
     * Test method for
     * 'com.bluejungle.destiny.webui.tags.TabTag.getComponentType()'
     */
    public void testGetComponentType() {
        assertEquals("Ensure compnent type as expected", EXPECTED_COMPONENT_TYPE, this.tagToTest.getComponentType());
    }

    /*
     * Test method for
     * 'com.bluejungle.destiny.webui.tags.TabTag.getRendererType()'
     */
    public void testGetRendererType() {
        assertEquals("Ensure compnent type as expected", EXPECTED_RENDERED_TYPE, this.tagToTest.getRendererType());
    }

    /*
     * Test method for
     * 'com.bluejungle.destiny.webui.tags.TabTag.setDisabled(String)' and
     */
    public void testSetDisabled() {
        // Test null
        try {
            this.tagToTest.setDisabled(null);
            fail("NPE should have been thrown");
        } catch (NullPointerException exception) {

        }

        /**
         * 
         * Setting values testing in
         * {@see TabTagTest#testSetPropertiesUIComponent()}
         */
    }

    /*
     * Test method for
     * 'com.bluejungle.destiny.webui.tags.TabTag.setName(String)'
     */
    public void testSetName() {
        // Test null
        try {
            this.tagToTest.setName(null);
            fail("NPE should have been thrown");
        } catch (NullPointerException exception) {

        }

        /**
         * 
         * Setting values testing in
         * {@see TabTagTest#testSetPropertiesUIComponent()}
         */
    }

    /*
     * Test method for
     * 'com.bluejungle.destiny.webui.tags.TabTag.setProperties(UIComponent)' and
     * 'com.bluejungle.destiny.webui.tags.TabTag.release()'
     */
    public void testSetPropertiesUIComponentAndRelease() {
        UITab tabComponent = new UITab();

        this.tagToTest.setProperties(tabComponent);
        Map tabComponentAttribute = tabComponent.getAttributes();
        assertEquals("Ensure disabled property initial value is as expected", Boolean.FALSE, tabComponentAttribute.get("disabled"));
        assertEquals("Ensure name property initial value is as expected", null, tabComponentAttribute.get("name"));

        // Set properties
        String nameToSet = "foo";
        Boolean disabledValueToSet = Boolean.TRUE;
        this.tagToTest.setDisabled(disabledValueToSet.toString());
        this.tagToTest.setName(nameToSet);

        this.tagToTest.setProperties(tabComponent);
        tabComponentAttribute = tabComponent.getAttributes();
        assertEquals("Ensure disabled property set as expected", disabledValueToSet, tabComponentAttribute.get("disabled"));
        assertEquals("Ensure name property set as expected", nameToSet, tabComponentAttribute.get("name"));

        // Test release - Can only call the method and see if it explodes.
        // Calling setProperties() with thrown NPE. I could change the
        // UITab.setName() to not throw NPE, but I think this is changing the
        // production code to enhance test code, which I don't do by principle
        this.tagToTest.release();

    }

}
