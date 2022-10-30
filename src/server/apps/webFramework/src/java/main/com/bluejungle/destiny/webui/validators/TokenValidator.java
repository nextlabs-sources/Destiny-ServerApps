/**
 * 
 */
package com.bluejungle.destiny.webui.validators;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.axis2.databinding.types.Token;

import com.bluejungle.destiny.appframework.CommonConstants;

/**
 * TODO description
 *
 * @author hchan
 * @date Apr 3, 2007
 */
public class TokenValidator implements Validator {
	
	private static final String VALIDATION_ERROR_SUMMARY;
    static {
        ResourceBundle commonBundle = ResourceBundle.getBundle(CommonConstants.COMMON_BUNDLE_NAME);
        VALIDATION_ERROR_SUMMARY = commonBundle.getString("conversion_failed_message_summary");
    }
	
	/* (non-Javadoc)
	 * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Unknow value type, " + value.getClass().getName() + ".  Expecting java.lang.String");
        }

        if (!Token.isValid((String)value)) {
            FacesMessage validationFailedMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, VALIDATION_ERROR_SUMMARY, "Token String is invalid.");
            throw new ValidatorException(validationFailedMessage);
        }
    }
	

}
