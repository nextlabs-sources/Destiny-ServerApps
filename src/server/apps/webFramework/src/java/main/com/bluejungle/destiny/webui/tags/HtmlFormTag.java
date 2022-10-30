/*
 * Created on Mar 22, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;

import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.taglib.html.HtmlFormTagBase;

/**
 * This is the Html form tag class. This tag allows the user to specify whether
 * the form should use GET or POST as the method argument.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/HtmlFormTag.java#1 $
 */

public class HtmlFormTag extends HtmlFormTagBase {

    protected static final String COMPONENT_TYPE = "com.bluejungle.destiny.Form";
    private static final String INPUT_TO_FOCUS_ATTR_NAME = "inputToFocus";
    
    private String method;
    private String inputToFocus;

    /**
     * @see javax.faces.webapp.UIComponentTag#getComponentType()
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * Returns the method argument
     * 
     * @return the method argument
     */
    protected String getMethod() {
        return this.method;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#getRendererType()
     */
    public String getRendererType() {
        return null;
    }

    /**
     * Sets the method argument
     * 
     * @param newMethod
     *            method name to set
     */
    public void setMethod(String newMethod) {
        this.method = newMethod;
    }

    /**
     * Retrieve the html id of the input element to focus.
     * @return the inputToFocus.
     */
    public String getInputToFocus() {
        return this.inputToFocus;
    }
    /**
     * Set the html id of the input element to focus
     * @param inputToFocus The inputToFocus to set.
     */
    public void setInputToFocus(String inputToFocus) {
        this.inputToFocus = inputToFocus;
    }
    
    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        TagUtil.setString(component, HTML.METHOD_ATTR, getMethod());
        TagUtil.setString(component, INPUT_TO_FOCUS_ATTR_NAME, getInputToFocus());
    }
}