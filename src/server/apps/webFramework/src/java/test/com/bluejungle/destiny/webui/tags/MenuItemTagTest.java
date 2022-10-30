/*
 * Created on Mar 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.bluejungle.destiny.webui.controls.UIMenuItem;
import com.bluejungle.destiny.webui.jsfmock.MockApplication;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;

/**
 * Test MenuItemTag
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/MenuItemTagTest.java#1 $
 */
public class MenuItemTagTest extends TestCase {

    private MenuItemTag menuItemTagToTest;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MenuItemTagTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        menuItemTagToTest = new MenuItemTag();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for MenuItemTagTest.
     * 
     * @param arg0
     */
    public MenuItemTagTest(String arg0) {
        super(arg0);
    }

    /**
     * Retrieve the suite of tests for testing MenuItemTag
     * 
     * @return the suite of tests for testing MenuItemTag
     */
    public static Test suite() {
        TestSuite testToReturn = new TestSuite("MenuItemTagTestSuite");
        testToReturn.addTest(new MenuItemTagTest("testGetComponentType"));
        testToReturn.addTest(new MenuItemTagTest("testGetRendererType"));
        testToReturn.addTest(new MenuItemTagTest("testGetSetAction"));
        testToReturn.addTest(new MenuItemTagTest("testGetSetActionListener"));
        testToReturn.addTest(new MenuItemTagTest("testGetSetImmediate"));
        testToReturn.addTest(new MenuItemTagTest("testGetSetValue"));
        testToReturn.addTest(new MenuItemTagTest("testGetSetViewIdPattern"));        
        testToReturn.addTest(new MenuItemTagTest("testSetPropertiesUIComponent"));        
        testToReturn.addTest(new MenuItemTagTest("testRelease"));
                
        return testToReturn;
    }
    
    /*
     * Class under test for String getComponentType()
     */
    public void testGetComponentType() {
        assertEquals("com.bluejungle.destiny.MenuItem", menuItemTagToTest.getComponentType());
    }

    /*
     * Class under test for String getRendererType()
     */
    public void testGetRendererType() {
        assertNull(menuItemTagToTest.getRendererType());
    }

    /*
     * Class under test for String getAction() and void setAction(String action)
     */
    public void testGetSetAction() {
        assertNull("Ensure action is initially null", menuItemTagToTest.getAction());

        String action = "fooAction";
        menuItemTagToTest.setAction(action);

        assertEquals("Ensure action set is equals to the value returned from the getter", action, menuItemTagToTest.getAction());

        // Try setting it back to null
        menuItemTagToTest.setAction(null);
        assertNull("Ensure action is null after setting it to null", menuItemTagToTest.getAction());
    }

    /*
     * Class under test for String getActionListener() and void setActionListener(String action)
     */
    public void testGetSetActionListener() {
        assertNull("Ensure action listener is initially null", menuItemTagToTest.getActionListener());

        String actionListener = "#{fooActionListener.bar}";
        menuItemTagToTest.setActionListener(actionListener);

        assertEquals("Ensure action listener set is equals to the value returned from the getter", actionListener, menuItemTagToTest.getActionListener());

        // Try setting it back to null
        menuItemTagToTest.setActionListener(null);
        assertNull("Ensure action listener is null after setting it to null", menuItemTagToTest.getActionListener());
    }

    /*
     * Class under test for String getImmediate() and void setImmediate(String immediate)
     */
    public void testGetSetImmediate() {
        assertNull("Ensure immediate is initially null", menuItemTagToTest.getImmediate());

        String immediate = "true";
        menuItemTagToTest.setImmediate(immediate);

        assertEquals("Ensure immediate is equals to the value returned from the getter", immediate, menuItemTagToTest.getImmediate());

        // Try setting it back to null
        menuItemTagToTest.setImmediate(null);
        assertNull("Ensure immediate is null after setting it to null", menuItemTagToTest.getImmediate());        
    }

    /*
     * Class under test for String getValue() and void setValue(String value)
     */
    public void testGetSetValue() {
        assertNull("Ensure value is initially null", menuItemTagToTest.getValue());

        String value = "fooValue";
        menuItemTagToTest.setValue(value);

        assertEquals("Ensure value is equals to the value returned from the getter", value, menuItemTagToTest.getValue());

        // Try setting it back to null
        menuItemTagToTest.setValue(null);
        assertNull("Ensure value is null after setting it to null", menuItemTagToTest.getValue()); 
    }

    /*
     * Class under test for String getViewIdPattern() and void setViewIdPattern(String value)
     */
    public void testGetSetViewIdPattern() {
        assertNull("Ensure viewIdPattern is initially null", menuItemTagToTest.getViewIdPattern());

        String viewIdPattern = "fooViewIdPattern";
        menuItemTagToTest.setViewIdPattern(viewIdPattern);

        assertEquals("Ensure viewIdPattern is equals to the viewIdPattern returned from the getter", viewIdPattern, menuItemTagToTest.getViewIdPattern());

        // Try setting it back to null
        menuItemTagToTest.setViewIdPattern(null);
        assertNull("Ensure viewIdPattern is null after setting it to null", menuItemTagToTest.getViewIdPattern()); 
    }

    /*
     * Class under test for void setProperties(UIComponent)
     */
    public void testSetPropertiesUIComponent() {
        UIMenuItem menuItemComponent = new UIMenuItem();
        
        String action = "fooAction";
        String actionListener = "#{fooActionListener.bar}";
        String immediate = "true";
        String value = "fooValue";
        String viewIdPattern = "fooViewIdPattern";
        
        menuItemTagToTest.setAction(action);
        menuItemTagToTest.setActionListener(actionListener);
        menuItemTagToTest.setImmediate(immediate);
        menuItemTagToTest.setValue(value);
        menuItemTagToTest.setViewIdPattern(viewIdPattern);

        assertEquals("testSetProperties - Ensure action set is equals to the value returned from the getter", action, menuItemTagToTest.getAction());
        assertEquals("testSetProperties - Ensure action listener set is equals to the value returned from the getter", actionListener, menuItemTagToTest.getActionListener());
        assertEquals("testSetProperties - Ensure immediate is equals to the value returned from the getter", immediate, menuItemTagToTest.getImmediate());
        assertEquals("testSetProperties - Ensure value is equals to the value returned from the getter", value, menuItemTagToTest.getValue());
        assertEquals("testSetProperties - Ensure viewIdPattern is equals to the viewIdPattern returned from the getter", viewIdPattern, menuItemTagToTest.getViewIdPattern());
        
        
        // We need a non null FacesContext and Application
        MockFacesContext mockContext = new MockFacesContext();
        mockContext.setApplication(new MockApplication());
        
        menuItemTagToTest.setProperties(menuItemComponent);
                        
        assertEquals("Ensure action is set", action, menuItemComponent.getAction().invoke(null, null));
        assertEquals("Ensure actionListener set", actionListener, menuItemComponent.getActionListener().getExpressionString());
        assertEquals("Ensure value is set", value, menuItemComponent.getValue());
        assertEquals("Ensure immediate set", new Boolean(immediate).booleanValue(), menuItemComponent.isImmediate());
        
        Map menuAttributes = menuItemComponent.getAttributes();
        assertEquals("Ensure view id pattern is set", viewIdPattern, menuAttributes.get("viewIdPattern"));                
    }

    /*
     * Class under test for void release()
     */
    public void testRelease() {
        String action = "fooAction";
        String actionListener = "#{fooActionListener.bar}";
        String immediate = "true";
        String value = "fooValue";
        String viewIdPattern = "fooViewIdPattern";
        
        menuItemTagToTest.setAction(action);
        menuItemTagToTest.setActionListener(actionListener);
        menuItemTagToTest.setImmediate(immediate);
        menuItemTagToTest.setValue(value);
        menuItemTagToTest.setViewIdPattern(viewIdPattern);

        assertEquals("testRelease - Ensure action set is equals to the value returned from the getter", action, menuItemTagToTest.getAction());
        assertEquals("testRelease - Ensure action listener set is equals to the value returned from the getter", actionListener, menuItemTagToTest.getActionListener());
        assertEquals("testRelease - Ensure immediate is equals to the value returned from the getter", immediate, menuItemTagToTest.getImmediate());
        assertEquals("testRelease - Ensure value is equals to the value returned from the getter", value, menuItemTagToTest.getValue());
        assertEquals("testRelease - Ensure viewIdPattern is equals to the viewIdPattern returned from the getter", viewIdPattern, menuItemTagToTest.getViewIdPattern());
 
        menuItemTagToTest.release();

        assertNull("testRelease - Ensure action is null after release", menuItemTagToTest.getAction());
        assertNull("testRelease - Ensure action listener is null after release", menuItemTagToTest.getActionListener());
        assertNull("testRelease - Ensure immediate is null after release", menuItemTagToTest.getImmediate());
        assertNull("testRelease - Ensure value is null after release", menuItemTagToTest.getValue());
        assertNull("testRelease - Ensure view id pattern is null after release", menuItemTagToTest.getViewIdPattern());
    }
}