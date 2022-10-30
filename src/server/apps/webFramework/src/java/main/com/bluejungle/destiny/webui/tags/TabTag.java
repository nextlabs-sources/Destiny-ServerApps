/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

/**
 * TabTag is a jsp tag used to create a UITab component
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/TabTag.java#1 $
 */

public class TabTag extends UIComponentTag {

    private String disabled = "false";
    private String name = null;

    private static final String DISABLED_ATTR_NAME = "disabled";
    private static final String NAME_ATTR_NAME = "name";

    /**
     * The component type created from the TabTag
     */
    public static final String COMPONENT_TYPE = "com.bluejungle.destiny.Tab";

    /**
     * The renderer type used to render the component created by the TabTag
     */
    public static final String RENDERER_TYPE = "com.bluejungle.destiny.TabRenderer";

    /**
     * Create an instance of TabTag
     * 
     */
    public TabTag() {
        super();
    }

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
     * Set the disabled
     * 
     * @param disabled
     *            The disabled to set.
     */
    public void setDisabled(String disabled) {
        if (disabled == null) {
            throw new NullPointerException("disabled cannot be null.");
        }
        
        this.disabled = disabled;
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

    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        super.release();

        this.disabled = "false";
        this.name = null;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        TagUtil.setBoolean(component, DISABLED_ATTR_NAME, this.getDisabled());
        TagUtil.setString(component, NAME_ATTR_NAME, this.getName());
    }

    /**
     * Retrieve the name.
     * 
     * @return the name.
     */
    private String getName() {
        return this.name;
    }

    /**
     * Retrieve the disabled.
     * 
     * @return the disabled.
     */
    private String getDisabled() {
        return this.disabled;
    }
}