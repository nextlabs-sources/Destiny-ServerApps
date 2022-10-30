/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Java Server Faces component for creating a Tab within a Tabbe Pane. Works in
 * conjunction with the
 * 
 * @see UITabbedPane component
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UITab.java#2 $
 */
public class UITab extends UIPanel {

    /**
     * The component family of the UITab component
     */
    public static final String COMPONENT_FAMILY = "com.bluejungle.destiny.Tab";

    /**
     * The renderer type used to render a UITab by default
     */
    public static final String DEFAULT_RENDERER_TYPE = "com.bluejungle.destiny.TabRenderer";

    private Boolean disabled = null;
    private String name = null;
    private static final String DISABLED_VALUE_BINDING = "disabled";
    private static final String NAME_VALUE_BINDING = "name";

    /**
     * Create an instance of UITab
     * 
     */
    public UITab() {
        super();
        this.setRendererType(DEFAULT_RENDERER_TYPE);
    }

    /**
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * Retrieve the disabled.
     * 
     * @return the disabled.
     */
    public boolean isDisabled() {
        boolean valueToReturn = false;

        if (this.disabled != null) {
            valueToReturn = this.disabled.booleanValue();
        } else {
            ValueBinding valueBinding = getValueBinding(DISABLED_VALUE_BINDING);
            if (valueBinding != null) {
                Boolean bindedValue = (Boolean) valueBinding.getValue(getFacesContext());
                if (bindedValue != null) {
                    valueToReturn = bindedValue.booleanValue();
                }
            }
        }

        return valueToReturn;
    }

    /**
     * Set the disabled
     * 
     * @param disabled
     *            The disabled to set.
     */
    public void setDisabled(boolean disabled) {
        this.disabled = Boolean.valueOf(disabled);
    }

    /**
     * Retrieve the name.
     * 
     * @return the name.
     */
    public String getName() {
        String valueToReturn = null;
        if (this.name != null) {
            valueToReturn = this.name;
        } else {
            ValueBinding valueBinding = getValueBinding(NAME_VALUE_BINDING);
            if (valueBinding != null) {
                valueToReturn = (String) valueBinding.getValue(getFacesContext());
            }
        }

        return valueToReturn;
    }

    /**
     * Set the name
     * 
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }

        this.name = name;
    }

    public Collection getUIParameters() {
        Set parametersToReturn = new HashSet();
        
        List children = getChildren();
        Iterator childrenIterator = children.iterator();
        while (childrenIterator.hasNext()) {
            UIComponent nextChild = (UIComponent) childrenIterator.next();
            if (nextChild instanceof UIParameter) {
                parametersToReturn.add(nextChild);
            }            
        }
        
        return parametersToReturn;
    }
    /**
     * @see javax.faces.component.UIComponent#decode(javax.faces.context.FacesContext)
     */
    public void decode(FacesContext context) {
        // all decoding is handled by UITabbedPane
    }
}