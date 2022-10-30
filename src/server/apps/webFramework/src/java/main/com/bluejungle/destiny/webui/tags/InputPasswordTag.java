/*
 * Created on Sep 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

import org.apache.myfaces.renderkit.html.HTML;

/**
 * A JSF tag used to display a password/confirm password component
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/branch/Destiny_Beta2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/InputPasswordTag.java#1 $
 */

public class InputPasswordTag extends UIComponentTag {

    private static final String COMPONENT_TYPE = "com.bluejungle.destiny.UIPassword";
    private static final String RENDERER_TYPE = "com.bluejungle.destiny.HtmlPasswordRenderer";

    private static final String VALUE_ATTR_NAME = "value";
    private static final String LABEL_COLUMNS_STYLE_CLASS_ATTR_NAME = "labelColumnStyleClass";
    private static final String INPUT_FIELDS_COLUMNS_STYLE_CLASS_ATTR_NAME = "inputFieldsColumnStyleClass";
    private static final String PASSWORD_INPUT_FIELD_LABEL_ATTR_NAME = "passwordInputFieldLabel";
    private static final String CONFIRM_PASSWORD_INPUT_FIELD_LABEL_ATTR_NAME = "confirmPasswordInputFieldLabel";
    private static final String DISABLED_ATTR_NAME = "disabled";
    private static final String REQUIRED_ATTR_NAME = "required";
    private static final String INPUT_FIELDS_SIZE_ATTR_NAME = "size";
    private static final String INPUT_FIELDS_MAX_LENGTH_ATTR_NAME = "maxLength";

    private String value;
    private String styleClass;
    private String labelColumnStyleClass;
    private String inputFieldsColumnStyleClass;
    private String passwordInputFieldLabel;
    private String confirmPasswordInputFieldLabel;
    private String disabled;
    private String required;
    private String size;
    private String maxLength;

    /**
     * @see javax.faces.webapp.UIComponentTag#getComponentType()
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#getRendererType()
     */
    public String getRendererType() {
        return RENDERER_TYPE;
    }

    /**
     * Set the style class of the top level table
     * 
     * @param styleClass
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * Set the input fields column css style class
     * 
     * @param inputFieldsColumnStyleClass
     *            the input fields column css style class
     */
    public void setInputFieldsColumnStyleClass(String inputFieldsColumnStyleClass) {
        this.inputFieldsColumnStyleClass = inputFieldsColumnStyleClass;
    }

    /**
     * Set the label column css style class
     * 
     * @param labelColumnStyleClass
     *            the label column css style class
     */
    public void setLabelColumnStyleClass(String labelColumnStyleClass) {
        this.labelColumnStyleClass = labelColumnStyleClass;
    }

    /**
     * Set the confirmPasswordInputFieldLabel
     * 
     * @param confirmPasswordInputFieldLabel
     *            The confirmPasswordInputFieldLabel to set.
     */
    public void setConfirmPasswordInputFieldLabel(String confirmPasswordInputFieldLabel) {
        this.confirmPasswordInputFieldLabel = confirmPasswordInputFieldLabel;
    }

    /**
     * Set the passwordInputFieldLabel
     * 
     * @param passwordInputFieldLabel
     *            The passwordInputFieldLabel to set.
     */
    public void setPasswordInputFieldLabel(String passwordInputFieldLabel) {
        this.passwordInputFieldLabel = passwordInputFieldLabel;
    }

    /**
     * Set the component value binding
     * 
     * @param value
     *            The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Set the required
     * 
     * @param required
     *            The required to set.
     */
    public void setRequired(String required) {
        this.required = required;
    }

    /**
     * Set the disabled
     * 
     * @param disabled
     *            The disabled to set.
     */
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    /**
     * Set the maxLength
     * @param maxLength The maxLength to set.
     */
    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }
    /**
     * Set the size
     * @param size The size to set.
     */
    public void setSize(String size) {
        this.size = size;
    }
    
    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        TagUtil.setString(component, VALUE_ATTR_NAME, getValue());
        TagUtil.setString(component, HTML.STYLE_CLASS_ATTR, getStyleClass());
        TagUtil.setString(component, LABEL_COLUMNS_STYLE_CLASS_ATTR_NAME, getLabelColumnStyleClass());
        TagUtil.setString(component, INPUT_FIELDS_COLUMNS_STYLE_CLASS_ATTR_NAME, getInputFieldsColumnStyleClass());
        TagUtil.setString(component, PASSWORD_INPUT_FIELD_LABEL_ATTR_NAME, getPasswordInputFieldLabel());
        TagUtil.setString(component, CONFIRM_PASSWORD_INPUT_FIELD_LABEL_ATTR_NAME, getConfirmPasswordInputFieldLabel());
        TagUtil.setString(component, DISABLED_ATTR_NAME, getDisabled());
        TagUtil.setBoolean(component, REQUIRED_ATTR_NAME, getRequired());
        TagUtil.setString(component, DISABLED_ATTR_NAME, getDisabled());
        TagUtil.setString(component, INPUT_FIELDS_SIZE_ATTR_NAME, getSize());
        TagUtil.setString(component, INPUT_FIELDS_MAX_LENGTH_ATTR_NAME, getMaxLength());
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        this.value = null;
        this.styleClass = null;
        this.labelColumnStyleClass = null;
        this.inputFieldsColumnStyleClass = null;
        this.passwordInputFieldLabel = null;
        this.confirmPasswordInputFieldLabel = null;
        this.required = null;
        this.size = null;
        this.maxLength = null;
    }

    /**
     * Retrieve the styleClass.
     * 
     * @return the styleClass.
     */
    private String getStyleClass() {
        return this.styleClass;
    }

    /**
     * Retrieve the inputFieldsColumnStyleClass.
     * 
     * @return the inputFieldsColumnStyleClass.
     */
    private String getInputFieldsColumnStyleClass() {
        return this.inputFieldsColumnStyleClass;
    }

    /**
     * Retrieve the labelColumnStyleClass.
     * 
     * @return the labelColumnStyleClass.
     */
    private String getLabelColumnStyleClass() {
        return this.labelColumnStyleClass;
    }

    /**
     * Retrieve the component value binding
     * 
     * @return the value.
     */
    private String getValue() {
        return this.value;
    }

    /**
     * Retrieve the confirmPasswordInputFieldLabel.
     * 
     * @return the confirmPasswordInputFieldLabel.
     */
    private String getConfirmPasswordInputFieldLabel() {
        return this.confirmPasswordInputFieldLabel;
    }

    /**
     * Retrieve the passwordInputFieldLabel.
     * 
     * @return the passwordInputFieldLabel.
     */
    private String getPasswordInputFieldLabel() {
        return this.passwordInputFieldLabel;
    }

    /**
     * Retrieve the disabled.
     * 
     * @return the disabled.
     */
    private String getDisabled() {
        return this.disabled;
    }

    /**
     * Retrieve the required.
     * 
     * @return the required.
     */
    private String getRequired() {
        return this.required;
    }

    /**
     * Retrieve the maxLength.
     * @return the maxLength.
     */
    private String getMaxLength() {
        return this.maxLength;
    }
    /**
     * Retrieve the size.
     * @return the size.
     */
    private String getSize() {
        return this.size;
    }
}