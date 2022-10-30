/*
 * Created on Mar 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentBodyTag;

/**
 * MenuTag is a JSF tag used to add component of component type
 * com.bluejungle.destiny.Menu to a page using a renderer of renderer type
 * com.bluejungle.destiny.MenuRenderer
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/MenuTag.java#1 $
 */

public class MenuTag extends UIComponentBodyTag {

    private static final String COMPONENT_TYPE = "com.bluejungle.destiny.Menu";
    private static final String RENDERER_TYPE = "com.bluejungle.destiny.MenuRenderer";

    private static final String STYLE_CLASS_ATTR_NAME = "styleClass";
    private static final String SELECTED_ITEM_STYLE_CLASS_ATTR_NAME = "selectedItemStyleClass";

    private String selectedItemStyleClass;
    private String styleClass;

    /**
     * @see javax.faces.webapp.UIComponentTag#getComponentType()
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#getRendererType()
     */
    public String getRendererType() {
        return RENDERER_TYPE;
    }

    /**
     * Retrieve the selectedItemStyleClass, a style class applied to the child
     * menu item that is currently selected
     * 
     * @return the selectedItemStyleClass.
     */
    public String getSelectedItemStyleClass() {
        return this.selectedItemStyleClass;
    }

    /**
     * Retreive the value of the styleClass attribute, a css style class applied
     * to the menu.
     * 
     * @return the styleClass.
     */
    public String getStyleClass() {
        return this.styleClass;
    }

    /**
     * Set the selectedItemStyleClass attribute value.  
     * 
     * @param selectedItemStyleClass
     *            The selectedItemStyleClass to set.
     */
    public void setSelectedItemStyleClass(String selectedItemStyleClass) {
        this.selectedItemStyleClass = selectedItemStyleClass;
    }

    /**
     * Set the value of the styleClass attribute
     * 
     * @param styleClass
     *            The styleClass to set.
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        TagUtil.setString(component, STYLE_CLASS_ATTR_NAME, this.styleClass);
        TagUtil.setString(component, SELECTED_ITEM_STYLE_CLASS_ATTR_NAME, this.selectedItemStyleClass);
    }
    
    
    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        super.release();
        
        this.selectedItemStyleClass = null;
        this.styleClass = null;
    }
}