/*
 * Created on Mar 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import java.util.Map;

import com.bluejungle.destiny.webui.controls.UIMenu;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test MenuTag
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/MenuTagTest.java#1 $
 */
public class MenuTagTest extends TestCase {

    private MenuTag menuTagToTest;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MenuTagTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        menuTagToTest = new MenuTag();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for MenuTagTest.
     * 
     * @param arg0
     */
    public MenuTagTest(String arg0) {
        super(arg0);
    }

    /**
     * Retrieve the suite of tests for testing MenuTag
     * 
     * @return the suite of tests for testing MenuTag
     */
    public static Test suite() {
        TestSuite testToReturn = new TestSuite("MenuTagTestSuite");
        testToReturn.addTest(new MenuTagTest("testGetComponentType"));
        testToReturn.addTest(new MenuTagTest("testGetRendererType"));
        testToReturn.addTest(new MenuTagTest("testGetSetSelectedItemStyleClass"));
        testToReturn.addTest(new MenuTagTest("testGetSetStyleClass"));
        testToReturn.addTest(new MenuTagTest("testSetPropertiesUIComponent"));
        testToReturn.addTest(new MenuTagTest("testRelease"));
        
        return testToReturn;
    }
    
    /*
     * Class under test for String getComponentType()
     */
    public void testGetComponentType() {
        assertEquals("com.bluejungle.destiny.Menu", menuTagToTest.getComponentType());
    }

    /*
     * Class under test for String getRendererType()
     */
    public void testGetRendererType() {
        assertEquals("com.bluejungle.destiny.MenuRenderer", menuTagToTest.getRendererType());
    }

    /*
     * Class under test for String getSelectedItemStyleClass() and void setSelectedItemStyleClass() 
     */
    public void testGetSetSelectedItemStyleClass() {
        assertNull("Ensure selected style class is initially null", menuTagToTest.getSelectedItemStyleClass());

        String selectedItemStyleClass = "fooStyle";
        menuTagToTest.setSelectedItemStyleClass(selectedItemStyleClass);

        assertEquals("Ensure style class set is equals to the value returned from the getter", selectedItemStyleClass, menuTagToTest.getSelectedItemStyleClass());

        // Try setting it back to null
        menuTagToTest.setSelectedItemStyleClass(null);
        assertNull("Ensure selected style class is null after setting it to nul", menuTagToTest.getSelectedItemStyleClass());
    }

    /*
     * Class under test for String getStyleClass() and void setStyleClass() 
     */
    public void testGetSetStyleClass() {
        assertNull("Ensure style class is initially null", menuTagToTest.getStyleClass());

        String styleClass = "fooStyle";
        menuTagToTest.setStyleClass(styleClass);

        assertEquals("Ensure style class set is equals to the value returned from the getter", styleClass, menuTagToTest.getStyleClass());

        // Try setting it back to null
        menuTagToTest.setStyleClass(null);
        assertNull("Ensure style class is null after setting it to null", menuTagToTest.getStyleClass());
    }

    /*
     * Class under test for void setProperties(UIComponent)
     */
    public void testSetPropertiesUIComponent() {
        UIMenu menuComponent = new UIMenu();
        
        String selectedItemStyleClass = "fooStyle";
        String styleClass = "fooStyleToo";
        menuTagToTest.setSelectedItemStyleClass(selectedItemStyleClass);
        menuTagToTest.setStyleClass(styleClass);
        
        menuTagToTest.setProperties(menuComponent);
        
        Map menuAttributes = menuComponent.getAttributes();
        assertEquals("Ensure style class is set", styleClass, menuAttributes.get("styleClass"));
        assertEquals("Ensure selected item style class is set", selectedItemStyleClass, menuAttributes.get("selectedItemStyleClass"));
    }

    /*
     * Class under test for void release()
     */
    public void testRelease() {
        String selectedItemStyleClass = "fooStyle";
        String styleClass = "fooStyleToo";
        menuTagToTest.setSelectedItemStyleClass(selectedItemStyleClass);
        menuTagToTest.setStyleClass(styleClass);      
        assertEquals("testRelease - Ensure style class set is equals to the value returned from the getter", styleClass, menuTagToTest.getStyleClass());
        assertEquals("testRelease - Ensure sslected style class set is equals to the value returned from the getter", selectedItemStyleClass, menuTagToTest.getSelectedItemStyleClass());

        menuTagToTest.release();
        
        assertNull("testRelease - Ensure style class is null after setting it to null", menuTagToTest.getStyleClass());
        assertNull("testRelease - Ensure selected style class is null after setting it to nul", menuTagToTest.getSelectedItemStyleClass());
    }
}