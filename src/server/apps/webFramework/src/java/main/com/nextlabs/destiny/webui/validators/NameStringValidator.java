/**
 * 
 */
package com.nextlabs.destiny.webui.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Validator for Profile Title, Userid, first name and last name
 *
 * @author hchan
 * @date Apr 3, 2007
 */
public class NameStringValidator implements Validator {
	private static final Pattern NAME_PATTERN = Pattern.compile("[\\.#%\\^_\\+=~():\"<>\\|{}\\[\\]]"); 
	/* (non-Javadoc)
	 * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (!(value instanceof String)) {
			throw new IllegalArgumentException("Unknow value type, " + value.getClass().getName()
					+ ".  Expecting java.lang.String");
		}

		Matcher matcher = NAME_PATTERN.matcher((String) value);
		if (matcher.find()) {
			FacesMessage validationFailedMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid String",
					"The value \"" + value + "\"contains invalid char(s).");
			throw new ValidatorException(validationFailedMessage);
		}
	}
}
