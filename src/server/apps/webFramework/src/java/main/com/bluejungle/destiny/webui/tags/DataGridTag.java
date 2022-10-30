/*
 * Created on May 4, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;

import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.taglib.UIComponentTagBase;

/**
 * JSP Tag for adding a Data Grid to a Faces View. The Data Grid is similar to a
 * Data Table, but will layout rows in a multicolumn format (i.e. like a
 * newspaper). The number of columns displayed will be determined by the
 * assigned renderer
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/DataGridTag.java#1 $
 */
public class DataGridTag extends UIComponentTagBase {

    /**
     * The component type created from the DataGridTag
     */
    public static final String COMPONENT_TYPE = "com.bluejungle.destiny.dataTable";

    /**
     * The renderer type used to render the component created by the
     * TabbedPaneTag
     */
    public static final String RENDERER_TYPE = "com.bluejungle.destiny.DataGridRenderer";

    private static final String DATA_VAR_ATTR_NAME = "var";

    private String styleClass;
    private String var;

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
     * Set the style class of the top level table
     * 
     * @param styleClass
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * Set the data var
     * 
     * @param var
     *            The data var to set.
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        TagUtil.setString(component, DATA_VAR_ATTR_NAME, getVar());
        TagUtil.setString(component, HTML.STYLE_CLASS_ATTR, getStyleClass());
    }

    /**
     * Retrieve the styleClass.
     * 
     * @return the styleClass.
     */
    private String getStyleClass() {
        return this.styleClass;
    }

    /**
     * Retrieve the var.
     * 
     * @return the var.
     */
    private String getVar() {
        return this.var;
    }
}