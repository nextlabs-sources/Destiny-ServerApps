/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

/**
 * This is the TagUtil test class. It tests the various utility methods.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/TagUtilTest.java#1 $
 */

public class TagUtilTest extends BaseJSFTest {

    /**
     * Constructor
     */
    public TagUtilTest() {
        super();
    }

    /**
     * Constructor
     * 
     * @param testName
     *            the name of the test
     */
    public TagUtilTest(String testName) {
        super(testName);
    }


    /**
     * This test verifies the set* functions accept only valid arguments.
     */
    public void testInvalidArguments() {
        boolean exThrown = false;
        try {
            TagUtil.setInteger(null, "attrName", "attrValue");
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("setInteger cannot accept null component argument", exThrown);

        exThrown = false;
        try {
            TagUtil.setString(null, "attrName", "attrValue");
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("setString cannot accept null component argument", exThrown);

        exThrown = false;
        UIComponent comp = new UIInput();
        try {
            TagUtil.setInteger(comp, null, "attrValue");
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("setInteger cannot accept null attribute name argument", exThrown);

        exThrown = false;
        try {
            TagUtil.setString(comp, null, "attrValue");
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("setString cannot accept null attribute name argument", exThrown);
    }

    /**
     * This test verifies that the value binding works for a given UI component.
     */
    public void testValueBinding() {
        final UIComponent comp = new UIInput();
        final String attrName = "myName";
        final String attrValue = "#{bean.prop}";
        TagUtil.setString(comp, attrName, attrValue);
        ValueBinding vb = comp.getValueBinding(attrName);
        assertNotNull("The expression should result in a value binding creation", vb);
        String value = (String) comp.getAttributes().get(attrName);
        assertNotNull("The component should evaluate the binding expression", value);
        final String result = attrValue + "_Value";
        assertEquals("Raw values should be set in the component attributes", result, value);
    }

    /**
     * This test verifies that if a raw value is specified, it is placed
     * accordingly into the component attribute list.
     */
    public void testRawValue() {
        final UIComponent comp = new UIInput();
        final String attrName = "myName";
        final String attrValue = "myValue";
        TagUtil.setString(comp, attrName, attrValue);
        String value = (String) comp.getAttributes().get(attrName);
        assertNotNull("Raw values should be set in the component attributes", value);
        assertEquals("Raw values should be set in the component attributes", attrValue, value);
        assertNull("Raw values should not result in a value binding creation", comp.getValueBinding(attrName));
    }

    /**
     * Test for void setBoolean(UIComponent component, String attrName, String attrValue)
     * @author sgoldstein
     */
    public void testSetBoolean() {
        UICommand component = new UICommand();
        
        /*
         * First, test with a value binding reference
         */
        String attrName = "foobar";
        String attrValue = "#{foo.bar}";
        
        TagUtil.setBoolean(component, attrName, attrValue);
        ValueBinding vbSet = component.getValueBinding(attrName);
        assertNotNull("testSetBoolean - Ensure value binding was set", vbSet);
        assertEquals("testSetBoolean - Ensure value binding set is correct", attrValue + "_Value", component.getAttributes().get(attrName));
        
        /*
         * Now, test with a boolean value
         */
        attrName = "immediate";
        attrValue = "true";
        
        TagUtil.setBoolean(component, attrName, attrValue);                
        assertTrue("testSetBoolean - Ensure boolean value set is correct", component.isImmediate());
        
        // Test for exception cases
        Exception caughtException = null;
        try {
            TagUtil.setBoolean(null, attrName, attrValue);
        } catch (NullPointerException exception) {
            caughtException = exception;
        }
        assertNotNull("testSetBoolean - Ensure null component leads to NullPointerException", caughtException);
        
        caughtException = null;
        try {
            TagUtil.setBoolean(component, null, attrValue);
        } catch (NullPointerException exception) {
            caughtException = exception;
        }
        assertNotNull("testSetBoolean - Ensure null attribute name leads to NullPointerException", caughtException);
    }
    
    /**
     * Test for void setAction(ActionSource component, String attrValue)
     * @author sgoldstein
     */
    public void testSetAction() {
        UICommand component = new UICommand();
        
        /*
         * First, test with a value binding reference
         */
        String action = "#{foo.bar}";
        
        TagUtil.setAction(component, action); 
        MethodBinding mbSet = component.getAction();
        assertNotNull("testActionListener - Ensure action method binding was set", mbSet);
        assertEquals("testActionListener - Ensure method binding set is correct", action, mbSet.getExpressionString());
        
        /*
         * Now, try with a simple outcome
         */
        action = "myAction";
        
        TagUtil.setAction(component, action); 
        mbSet = component.getAction();
        assertNotNull("testActionListener - Ensure simple action method binding was set", mbSet);
        assertEquals("testActionListener - Ensure simple method binding set is correct", action, mbSet.invoke(null, null));        
        
        // Test for exception cases
        Exception caughtException = null;
        try {
            TagUtil.setAction(null, action);
        } catch (NullPointerException exception) {
            caughtException = exception;
        }
        assertNotNull("testActionListener - Ensure null component leads to NullPointerException", caughtException);
    }
    
    /**
     * Test for void setActionListener(ActionSource component, String actionListener)
     * @author sgoldstein
     */
    public void testSetActionListener() {
        UICommand component = new UICommand();
        
        String actionListener = "#{foo.bar}";
        
        TagUtil.setActionListener(component, actionListener); 
        MethodBinding mbSet = component.getActionListener();
        assertNotNull("testSetActionListener - Ensure action method binding was set", mbSet);
        assertEquals("testSetActionListener - Ensure method binding set is correct", actionListener, mbSet.getExpressionString());
        
        // Test for exception cases
        Exception caughtException = null;
        try {
            TagUtil.setActionListener(null, actionListener);
        } catch (NullPointerException exception) {
            caughtException = exception;
        }
        assertNotNull("testSetActionListener - Ensure null component leads to NullPointerException", caughtException);
    }
}