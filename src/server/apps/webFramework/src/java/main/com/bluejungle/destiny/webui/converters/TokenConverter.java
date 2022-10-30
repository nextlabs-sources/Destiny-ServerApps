package com.bluejungle.destiny.webui.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Converter an invalid Token to valid Token
 * remove the line feed (#xA), tab (#x9) characters, 
 * remove all eading or trailing spaces (#x20) 
 * remove all internal sequences of two or more spaces.
 *
 * @author hchan
 * @date Apr 3, 2007
 */
public class TokenConverter implements Converter {
	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return getAsString(context,component, value);
	}

	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String formattedValue = value.toString();
		formattedValue = formattedValue.trim();
		formattedValue = formattedValue.replaceAll("" + (char)0x0A , "");
		formattedValue = formattedValue.replaceAll("" + (char)0x09 , "");
		formattedValue = formattedValue.replaceAll(" +" , " ");
		
		return formattedValue;
	}
}
