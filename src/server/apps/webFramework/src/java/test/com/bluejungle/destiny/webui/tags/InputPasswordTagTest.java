/*
 * Created on Sep 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import java.util.Map;

import com.bluejungle.destiny.webui.controls.UIPassword;

import junit.framework.TestCase;

/**
 * Unit Test for Input password tag
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/branch/Destiny_Beta2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/InputPasswordTagTest.java#1 $
 */

public class InputPasswordTagTest extends TestCase {

    private static final String EXPECTED_TAG_COMPONENT_TYPE = "com.bluejungle.destiny.UIPassword";
    private static final String EXPECTED_TAG_RENDERER_TYPE = "com.bluejungle.destiny.HtmlPasswordRenderer";

    private InputPasswordTag tagToTest;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(InputPasswordTagTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.tagToTest = new InputPasswordTag();
    }

    /*
     * Test getComponentType()
     */
    public void testGetComponentType() {
        assertTrue("testGetComponentType - Verify component type as expected", tagToTest.getComponentType().equals(EXPECTED_TAG_COMPONENT_TYPE));
    }

    /*
     * Test getRendererType()
     */
    public void testGetRendererType() {
        assertTrue("testGetRendererType - Verify renderer type as expected", tagToTest.getRendererType().equals(EXPECTED_TAG_RENDERER_TYPE));
    }

    /*
     * Test all setters, getProperties, and release
     */
    public void testSetProperties() {
        UIPassword uiPasswordComponent = new UIPassword();

        // Test initial values - Ensure they're all null
        this.tagToTest.setProperties(uiPasswordComponent);

        Map componentAttributes = uiPasswordComponent.getAttributes();
        assertNull("testSetProperties - Ensure styleClass initially null", componentAttributes.get("styleClass"));
        assertNull("testSetProperties - Ensure inputFieldColumnStyleClass initially null", componentAttributes.get("inputFieldsColumnStyleClass"));
        assertNull("testSetProperties - Ensure labelColumnStyleClass initially null", componentAttributes.get("labelColumnStyleClass"));
        assertNull("testSetProperties - Ensure confirmPasswordInputFieldLabel initially null", componentAttributes.get("confirmPasswordInputFieldLabel"));
        assertNull("testSetProperties - Ensure passwordInputFieldLabel initially null", componentAttributes.get("passwordInputFieldLabel"));
        assertNull("testSetProperties - Ensure disabled initially null", componentAttributes.get("disabled"));
        assertNull("testSetProperties - Ensure value initially null", uiPasswordComponent.getValue());
        
        // UIPassword extends UIInput which has an explicit method for is/setRequired.  Therefore, this will be initially false rather than null
        assertEquals("testSetProperties - Ensure required initially null", Boolean.FALSE, componentAttributes.get("required"));
        assertNull("testSetProperties - Ensure size initially null", componentAttributes.get("size"));
        assertNull("testSetProperties - Ensure maxLength initially null", componentAttributes.get("maxLength"));

        String testStyleClass = "fooStyle";
        String testInputFieldsColumnStyleClass = "inputFieldsColumnFooStyle";
        String testLabelColumnStyleClass = "labelColumnFooStyle";
        String testConfirmPasswordInputFieldLabel = "confirmPasswordInputFieldFooLabel";
        String testPasswordInputFieldLabel = "passwordInputFieldFooLabel";
        String disabled = "true";
        String testValue = "fooValue";
        String required="true";
        String size = "5";
        String maxLength = "50";

        // Now, set value and verify that they're set
        this.tagToTest.setStyleClass(testStyleClass);
        this.tagToTest.setInputFieldsColumnStyleClass(testInputFieldsColumnStyleClass);
        this.tagToTest.setLabelColumnStyleClass(testLabelColumnStyleClass);
        this.tagToTest.setConfirmPasswordInputFieldLabel(testConfirmPasswordInputFieldLabel);
        this.tagToTest.setPasswordInputFieldLabel(testPasswordInputFieldLabel);
        this.tagToTest.setDisabled(disabled);
        this.tagToTest.setValue(testValue);
        this.tagToTest.setRequired(required);
        this.tagToTest.setSize(size);
        this.tagToTest.setMaxLength(maxLength);

        this.tagToTest.setProperties(uiPasswordComponent);

        componentAttributes = uiPasswordComponent.getAttributes();
        assertEquals("testSetProperties - Ensure styleClass is set as expected", testStyleClass, componentAttributes.get("styleClass"));
        assertEquals("testSetProperties - Ensure inputFieldColumnStyleClass is set as expected", testInputFieldsColumnStyleClass, componentAttributes.get("inputFieldsColumnStyleClass"));
        assertEquals("testSetProperties - Ensure labelColumnStyleClass is set as expected", testLabelColumnStyleClass, componentAttributes.get("labelColumnStyleClass"));
        assertEquals("testSetProperties - Ensure confirmPasswordInputFieldLabel is set as expected", testConfirmPasswordInputFieldLabel, componentAttributes.get("confirmPasswordInputFieldLabel"));
        assertEquals("testSetProperties - Ensure passwordInputFieldLabel is set as expected", testPasswordInputFieldLabel, componentAttributes.get("passwordInputFieldLabel"));
        assertEquals("testSetProperties - Ensure disabled is set as expected", disabled, componentAttributes.get("disabled"));
        assertEquals("testSetProperties - Ensure value is set as expected", testValue, uiPasswordComponent.getValue());
        assertEquals("testSetProperties - Ensure required is set as expected", Boolean.valueOf(required), componentAttributes.get("required"));
        assertEquals("testSetProperties - Ensure size is set as expected", size, componentAttributes.get("size"));
        assertEquals("testSetProperties - Ensure maxLength is set as expected", maxLength, componentAttributes.get("maxLength"));
    }
}