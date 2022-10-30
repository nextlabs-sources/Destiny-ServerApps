/*
 * Created on May 12, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.converters;

import java.util.Enumeration;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.http.HttpServletRequest;

/**
 * A converter which converts a String, "A", into a request attribute with the
 * key, "A".
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/converters/RequestAttributeConverter.java#1 $
 */
public class RequestAttributeConverter implements Converter {

    /**
     * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Object valueToReturn = null;

        HttpServletRequest servletRequest = (HttpServletRequest) context.getExternalContext().getRequest();
        Enumeration attributeNames = servletRequest.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String nextAttributeName = (String) attributeNames.nextElement();
            Object nextAttribute = servletRequest.getAttribute(nextAttributeName);
            if (value.equals(nextAttribute)) {
                valueToReturn = nextAttribute;
            }

        }

        return valueToReturn;
    }

    /**
     * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        HttpServletRequest servletRequest = (HttpServletRequest) context.getExternalContext().getRequest();
        return (String) servletRequest.getAttribute(value.toString());
    }

}