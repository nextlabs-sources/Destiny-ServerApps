/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

/**
 * This tag represents a row within a data table. It can store a style
 * associated with the row.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/RowTag.java#1 $
 */

public class RowTag extends UIComponentTag {

    protected static final String COMPONENT_TYPE = "com.bluejungle.destiny.tableRow";
    protected static final String RENDERER_TYPE = "com.bluejungle.destiny.tableRow";
    protected static final String STYLE_ATTR_NAME = "styleClass";
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
     * Returns the style class associated with the row
     * 
     * @return the style class associated with the row
     */
    protected String getStyleClass() {
        return this.styleClass;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        TagUtil.setString(component, "styleClass", getStyleClass());
    }

    /**
     * Sets the style class for the row
     * 
     * @param styleClass
     *            the class name to set
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
}