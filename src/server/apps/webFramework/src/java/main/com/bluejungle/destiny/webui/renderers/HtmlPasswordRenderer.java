/*
 * Created on Sep 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.apache.myfaces.renderkit.html.HTML;

import com.bluejungle.destiny.webui.controls.UIPassword;
import com.bluejungle.framework.utils.PasswordUtils;

/**
 * HTML Renderer for the UIPassword component
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/HtmlPasswordRenderer.java#1 $
 */

public class HtmlPasswordRenderer extends Renderer {

    private static final String LABEL_COLUMNS_STYLE_CLASS_ATTR_NAME = "labelColumnStyleClass";
    private static final String INPUT_FIELDS_COLUMNS_STYLE_CLASS_ATTR_NAME = "inputFieldsColumnStyleClass";
    private static final String PASSWORD_INPUT_FIELD_LABEL_ATTR_NAME = "passwordInputFieldLabel";
    private static final String CONFIRM_PASSWORD_INPUT_FIELD_LABEL_ATTR_NAME = "confirmPasswordInputFieldLabel";
    private static final String DEFAULT_PASSWORD_INPUT_FIELD_LABEL = "New Password";
    private static final String DEFAULT_CONFIRM_PASSWORD_INPUT_FIELD_LABEL = "Confirm Password";
    private static final String DISABLED_ATTR_NAME = "disabled";
    private static final String INPUT_FIELDS_SIZE_ATTR_NAME = "size";
    private static final String INPUT_FIELDS_MAX_LENGTH_ATTR_NAME = "maxLength";

    private static final String DEFAULT_INPUT_FIELD_SIZE = String.valueOf(PasswordUtils.DEFAULT_PASSWORD_MAX_LENGTH);

    /**
     * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void decode(FacesContext facesContext, UIComponent component) {
        super.decode(facesContext, component);

        if (!(component instanceof UIPassword)) {
            throw new IllegalArgumentException("component must be of type UIPassword: " + component.getClass());
        }

        UIPassword inputPasswordComponent = (UIPassword) component;

        Map requestParameters = facesContext.getExternalContext().getRequestParameterMap();
        String passwordInputFieldName = getPasswordInputFieldName(facesContext, component);
        String submittedPasswordValue = (String) requestParameters.get(passwordInputFieldName);
        if (submittedPasswordValue != null) {
            //request parameter found, set submittedValue
            inputPasswordComponent.setSubmittedValue(submittedPasswordValue);
        }

        String confirmPasswordInputFieldName = getConfirmPasswordInputFieldName(facesContext, component);
        String submittedConfirmedPasswordValue = (String) requestParameters.get(confirmPasswordInputFieldName);
        if (submittedConfirmedPasswordValue != null) {
            //request parameter found, set submittedValue
            inputPasswordComponent.setConfirmingSubmittedValue(submittedConfirmedPasswordValue);
        }
        
        // Special case - If the new password field is empty and the confirm password field is not, set invalid
        if ((submittedPasswordValue != null) && 
            (submittedPasswordValue.length() == 0) && 
            (submittedConfirmedPasswordValue != null) &&
            (submittedConfirmedPasswordValue.length() > 0)) {
            inputPasswordComponent.setValid(false);
        } else {
            inputPasswordComponent.setValid(true);
        }
        
    }

    /**
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement(HTML.TABLE_ELEM, component);

        String styleClass = (String) component.getAttributes().get(HTML.STYLE_CLASS_ATTR);
        if (styleClass != null) {
            writer.writeAttribute(HTML.CLASS_ATTR, styleClass, null);
        }

        writePasswordField(context, component, writer);
        writeConfirmPasswordField(context, component, writer);

        writer.endElement(HTML.TABLE_ELEM);
    }

    /**
     * Write the password field
     * 
     * @param context
     * @param component
     * @param writer
     * @throws IOException
     */
    private void writePasswordField(FacesContext context, UIComponent component, ResponseWriter writer) throws IOException {
        Map componentAttributes = component.getAttributes();
        String passwordInputFieldLabel = (String) componentAttributes.get(PASSWORD_INPUT_FIELD_LABEL_ATTR_NAME);
        if (passwordInputFieldLabel == null) {
            passwordInputFieldLabel = DEFAULT_PASSWORD_INPUT_FIELD_LABEL;
        }

        String passwordInputFieldName = getPasswordInputFieldName(context, component);

        writeInputFieldRow(component, writer, passwordInputFieldLabel, passwordInputFieldName);
    }

    /**
     * Write the confirm password field
     * 
     * @param context
     * @param component
     * @param writer
     * @throws IOException
     */
    private void writeConfirmPasswordField(FacesContext context, UIComponent component, ResponseWriter writer) throws IOException {
        Map componentAttributes = component.getAttributes();
        String confirmPasswordInputFieldLabel = (String) componentAttributes.get(CONFIRM_PASSWORD_INPUT_FIELD_LABEL_ATTR_NAME);
        if (confirmPasswordInputFieldLabel == null) {
            confirmPasswordInputFieldLabel = DEFAULT_CONFIRM_PASSWORD_INPUT_FIELD_LABEL;
        }

        String confirmPasswordInputFieldName = getConfirmPasswordInputFieldName(context, component);

        writeInputFieldRow(component, writer, confirmPasswordInputFieldLabel, confirmPasswordInputFieldName);
    }

    /**
     * Write a single input field row, including label and form input field
     * 
     * @param component
     * @param writer
     * @param inputFieldLabel
     * @param inputFieldName
     * @throws IOException
     */
    private void writeInputFieldRow(UIComponent component, ResponseWriter writer, String inputFieldLabel, String inputFieldName) throws IOException {
        writer.startElement(HTML.TR_ELEM, component);
        writer.startElement(HTML.TD_ELEM, component);

        Map componentAttributes = component.getAttributes();
        String labelColumnsStyleClass = (String) componentAttributes.get(LABEL_COLUMNS_STYLE_CLASS_ATTR_NAME);
        if (labelColumnsStyleClass != null) {
            writer.writeAttribute(HTML.CLASS_ATTR, labelColumnsStyleClass, null);
        }

        writer.write(inputFieldLabel);
        writer.endElement(HTML.TD_ELEM);
        writer.startElement(HTML.TD_ELEM, component);

        String inputFieldsColumnsStyleClass = (String) componentAttributes.get(INPUT_FIELDS_COLUMNS_STYLE_CLASS_ATTR_NAME);
        if (inputFieldsColumnsStyleClass != null) {
            writer.writeAttribute(HTML.CLASS_ATTR, inputFieldsColumnsStyleClass, null);
        }

        writer.startElement(HTML.INPUT_ELEM, component);
        writer.writeAttribute(HTML.TYPE_ATTR, "password", null);
        writer.writeAttribute("name", inputFieldName, "clientId");
        
        String size = (String) componentAttributes.get(INPUT_FIELDS_SIZE_ATTR_NAME);
        if (size == null) {
            size = DEFAULT_INPUT_FIELD_SIZE;
        }
        writer.writeAttribute(HTML.SIZE_ATTR, size, null);
        
        String maxLength = (String) componentAttributes.get(INPUT_FIELDS_MAX_LENGTH_ATTR_NAME);
        if (maxLength == null) {
            maxLength = DEFAULT_INPUT_FIELD_SIZE;
        }        

        writer.writeAttribute(HTML.MAXLENGTH_ATTR, maxLength, null);

        Object disabledO = componentAttributes.get(DISABLED_ATTR_NAME);
        boolean disabled = false;
        if (disabledO !=null) {
            if (disabledO instanceof Boolean) {
                disabled = ((Boolean) disabledO).booleanValue();
            } else if (disabledO instanceof String) {
                disabled = Boolean.valueOf((String) disabledO).booleanValue();
            }
        }
        
        if (disabled) {
            writer.writeAttribute(HTML.DISABLED_ATTR, Boolean.TRUE, null);
        }

        writer.endElement(HTML.INPUT_ELEM);
        writer.endElement(HTML.TD_ELEM);
        writer.endElement(HTML.TR_ELEM);
    }

    /**
     * Retrieve the input field name of the password input field
     * 
     * @param context
     * @param component
     * @return the input field name of the password input field
     */
    private String getPasswordInputFieldName(FacesContext context, UIComponent component) {
        return component.getClientId(context);
    }

    /**
     * Retrieve the input field name of the confirm password input field
     * 
     * @param context
     * @param component
     * @return the input field name of the confirm password input field
     */
    private String getConfirmPasswordInputFieldName(FacesContext context, UIComponent component) {
        return component.getClientId(context) + "_confirm";
    }
}