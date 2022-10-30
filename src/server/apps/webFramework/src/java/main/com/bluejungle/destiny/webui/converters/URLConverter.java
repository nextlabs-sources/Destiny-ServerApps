/*
 * Created on Apr 22, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.converters;

import com.bluejungle.destiny.appframework.CommonConstants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * URLConverter is a Faces converter for a java.net.URL type
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/converters/URLConverter.java#1 $
 */
public class URLConverter implements Converter {

    private static final String CONVERSION_FAILED_SUMMARY_MSG = "conversion_failed_message_summary";
    private static final String URL_CONVERSION_FAILED_DETAIL_MSG = "url_conversion_message_detail";

    /**
     * Create an instance of URLConverter
     * 
     */
    public URLConverter() {
        super();
    }

    /**
     * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        URL urlToReturn = null;
        if ((value == null) || (value.equals(""))) {
            urlToReturn = null;
        } else {
            try {
                urlToReturn = new URL(value);
            } catch (MalformedURLException exception) {
                FacesMessage errorMessage = getErrorMessage(context);

                throw new ConverterException(errorMessage, exception);
            }
        }

        return urlToReturn;
    }

    /**
     * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String valueToReturn = "";
        
        if (value != null) {
            valueToReturn = value.toString();
        }

        return valueToReturn;
    }

    /**
     * Retrieve the error message associated with this converster FIX ME - Get
     * input from ui. If we need to add specifics from the component, we may be
     * able to use the "title" attribute
     * 
     * @return the error message associated with this converster
     */
    private FacesMessage getErrorMessage(FacesContext context) {
        ResourceBundle bundle = ResourceBundle.getBundle(CommonConstants.COMMON_BUNDLE_NAME, context.getViewRoot().getLocale());
        return new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(CONVERSION_FAILED_SUMMARY_MSG), bundle.getString(URL_CONVERSION_FAILED_DETAIL_MSG));
    }
}