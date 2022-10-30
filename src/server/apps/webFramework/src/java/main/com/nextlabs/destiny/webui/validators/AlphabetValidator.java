package com.nextlabs.destiny.webui.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class AlphabetValidator implements Validator {
	private static final String PATTERN = "[a-zA-Z]+";

	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (!(value instanceof String)) {
			throw new IllegalArgumentException("Unknow value type, "
					+ value.getClass().getName()
					+ ".  Expecting java.lang.String");
		}

		String valueString = (String) value;
		if (!valueString.matches(PATTERN)) {
			FacesMessage validationFailedMessage = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Invalid String",
					"The value \"" + value + "\"contains invalid char(s).");
			throw new ValidatorException(validationFailedMessage);
		}
	}
}
