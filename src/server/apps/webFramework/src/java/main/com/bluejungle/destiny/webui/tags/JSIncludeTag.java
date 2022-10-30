/*
 * Created on Mar 12, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

/**
 * This is JSP tag for the javaScript file inclusion.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/JSIncludeTag.java#1 $
 */

public class JSIncludeTag extends UIComponentTag {

    protected static final String COMPONENT_TYPE = "com.bluejungle.destiny.jsInclude";
    protected String location;

    /**
     * Returns the component type associated with this tag.
     * 
     * @return the component type associated with this tag.
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * Returns the renderer type. The renderer settings will be decided at
     * runtime, so return null.
     * 
     * @return the renderer type
     */
    public String getRendererType() {
        return null;
    }

    /**
     * Returns the location field value for the tag
     * 
     * @return the location field value for the tag
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        TagUtil.setString(component, "value", this.location);
    }

    /**
     * Sets the value location value for the tag
     * 
     * @param valueExpr
     *            new value expression
     */
    public void setLocation(String valueExpr) {
        this.location = valueExpr;
    }

}