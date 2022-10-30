/*
 * Created on Feb 20, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;


/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/ParamTag.java#1 $
 */

public class ParamTag extends UIComponentTag {
    private static final String COMPONENT_TYPE = "javax.faces.Parameter";
    private static final String NAME_ATTR_NAME = "name";
    private static final String VALUE_ATTR_NAME = "value";
    
    private String name;
    private String value;
    
    /**
     * @see javax.faces.webapp.UIComponentTag#getComponentType()
     */
    @Override
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#getRendererType()
     */
    @Override
    public String getRendererType() {
        return null;
    }

    /**
     * Set the name
     * @param name The name to set.
     */
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }
        
        this.name = name;
    } 
    
    /**
     * Set the value
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    } 
    
    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    @Override
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        TagUtil.setString(component, NAME_ATTR_NAME, this.getName());
        TagUtil.setString(component, VALUE_ATTR_NAME, this.getValue());
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#release()
     */
    @Override
    public void release() {
        this.name = null;
    }

    
    /**
     * Retrieve the name.
     * @return the name.
     */
    private String getName() {
        return this.name;
    }

    
    /**
     * Retrieve the value.
     * @return the value.
     */
    private String getValue() {
        return this.value;
    }   
}
