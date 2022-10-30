/*
 * Created on Sep 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import com.bluejungle.destiny.appframework.CommonConstants;
import com.bluejungle.framework.utils.PasswordUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import java.util.ResourceBundle;

/**
 * A component used to display a password/confirm password component
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/branch/Destiny_Beta2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UIPassword.java#3 $
 */
public class UIPassword extends UIInput {

    /**
     * The component family of the UIPassword component
     */
    public static final String COMPONENT_FAMILY = "com.bluejungle.destiny.InputPassword";

    /**
     * The renderer type used to render a UITabbedPane by default
     */
    public static final String DEFAULT_RENDERER_TYPE = "com.bluejungle.destiny.InputPasswordRenderer";

    private static final FacesMessage PASSWORD_CONFIRMATION_FAILED_VALIDATION_ERROR_MESSAGE;
    private static final FacesMessage PASSWORD_CONTENT_INVALID_VALIDATION_ERROR_MESSAGE;
    private static final FacesMessage PASSWORD_EMPTY_FAILED_VALIDATION_ERROR_MESSAGE ;
    static {        
        ResourceBundle commonBundle = ResourceBundle.getBundle(CommonConstants.COMMON_BUNDLE_NAME);
        
        String passwordConfirmationFailedErrorMessageSummary = commonBundle.getString("confirmed_password_mismatch_summary");
        String passwordConfirmationFailedErrorMessageDetail = commonBundle.getString("confirmed_password_mismatch_detail");
        PASSWORD_CONFIRMATION_FAILED_VALIDATION_ERROR_MESSAGE = new FacesMessage(passwordConfirmationFailedErrorMessageSummary, passwordConfirmationFailedErrorMessageDetail);

        String passwordContentInvalidErrorMessageSummary = commonBundle.getString("password_content_invalid_summary");
        String passwordContentInvalidErrorMessageDetail = commonBundle.getString("password_content_invalid_detail");
        PASSWORD_CONTENT_INVALID_VALIDATION_ERROR_MESSAGE = new FacesMessage(passwordContentInvalidErrorMessageSummary, passwordContentInvalidErrorMessageDetail);
        
        String passwordEmptyErrorMessageSummary = commonBundle.getString("password_empty_summary");
        String passwordEmptyErrorMessageDetail = commonBundle.getString("password_empty_detail");
        PASSWORD_EMPTY_FAILED_VALIDATION_ERROR_MESSAGE = new FacesMessage(passwordEmptyErrorMessageSummary, passwordEmptyErrorMessageDetail);
    }

    private Object confirmingSubmittedValue = null;
    private Object value = null;

    /**
     * Create an instance of UIPassword
     * 
     */
    public UIPassword() {
        this.addValidator(new RequiredValidator());
        this.addValidator(new EqualityValidator());
        this.addValidator(new PasswordContentValidator());      
    }

    /**
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * Set the confirming password value
     * 
     * @param confirmingValue
     */
    public void setConfirmingSubmittedValue(Object confirmingValue) {
        this.confirmingSubmittedValue = confirmingValue;
    }

    /**
     * Retrieve the confirming submitted password value
     * 
     * @return the confirming submitted password value
     */
    public Object getConfirmingSubmittedValue() {
        return this.confirmingSubmittedValue;
    }

    /**
     * Ignores the value binding and only returns this components value when
     * explicitly set. Ensures Password is always empty by default
     * 
     * @see javax.faces.component.ValueHolder#getValue()
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * @see javax.faces.component.ValueHolder#setValue(java.lang.Object)
     */
    public void setValue(Object value) {
        // Accept every value but the empty value. Empty value is submitted when
        // the form field is empty
        if ((value == null) || (!value.equals(""))) {
            this.value = value;
            this.setLocalValueSet(true);
        }
    }

    /**
     * 
     * @see javax.faces.component.ValueHolder#getLocalValue()
     */
    public Object getLocalValue() {
        return (this.value);
    }

    /*
     * -------------------------- State Methods
     * ---------------------------------- The UI Password component does not
     * save state. All password set are lost after submission of form
     */

    /**
     * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext,
     *      java.lang.Object)
     */
    public void restoreState(FacesContext context, Object state) {
    }

    /**
     * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
     */
    public Object saveState(FacesContext context) {
        return null;
    }

    protected void validateValue(FacesContext context, Object newValue) {
        Validator[] validators = this.getValidators();
        for (int i = 0; i < validators.length; i++) {
            Validator nextValidator = validators[i];
            try {
                nextValidator.validate(context, this, newValue);
            } catch (ValidatorException exception) {
                // If the validator throws an exception, we're
                // invalid, and we need to add a message
                this.setValid(false);
                FacesMessage message = exception.getFacesMessage();
                if (message != null) {
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);
                    context.addMessage(getClientId(context), message);
                }
            }
        }

        // Note - Does not currently support validator binding.  See super.validateValue() to add support
    }

    private class RequiredValidator implements Validator {
        /**
         * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext,
         *      javax.faces.component.UIComponent, java.lang.Object)
         */
        public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
            Object confirmedSubmittedValue = UIPassword.this.getConfirmingSubmittedValue();
            if ((UIPassword.this.isRequired()) && 
                ((value == null) ||
                 (value.toString().length() == 0)) &&
                ((confirmedSubmittedValue == null) ||
                 (confirmedSubmittedValue.toString().length() == 0))) {
                throw new ValidatorException(PASSWORD_EMPTY_FAILED_VALIDATION_ERROR_MESSAGE);
            }
        }        
    }
    
    private class EqualityValidator implements Validator {

        /**
         * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext,
         *      javax.faces.component.UIComponent, java.lang.Object)
         */
        public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
            if (!value.equals(UIPassword.this.getConfirmingSubmittedValue())) {
                throw new ValidatorException(PASSWORD_CONFIRMATION_FAILED_VALIDATION_ERROR_MESSAGE);
            }
        }
    }

    private class PasswordContentValidator implements Validator {

        /**
         * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext,
         *      javax.faces.component.UIComponent, java.lang.Object)
         */
        public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
            // Must check an empty value. This happens the password field is
            // left empty.
            if ((!value.equals("")) && (!PasswordUtils.isValidPasswordDefault((String) value))) {
                throw new ValidatorException(PASSWORD_CONTENT_INVALID_VALIDATION_ERROR_MESSAGE);
            }
        }
    }
}