/*
 * Created on May 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;

import org.apache.myfaces.custom.crosstable.HtmlColumnsTag;
import org.apache.myfaces.renderkit.JSFAttr;

/**
 * This tag extends the myFaces "x:columns" tag. It uses a different component.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/ColumnsTag.java#1 $
 */

public class ColumnsTag extends HtmlColumnsTag {

    protected static final String COMPONENT_TYPE = "com.bluejungle.destiny.horDataColumn";
    private String footerClass;

    /**
     * @see javax.faces.webapp.UIComponentTag#getComponentType()
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * Returns the footer class name
     * 
     * @return the footer class name
     */
    protected String getFooterClass() {
        return this.footerClass;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        TagUtil.setString(component, JSFAttr.FOOTER_CLASS_ATTR, getFooterClass());
    }

    /**
     * Sets the footer class name
     * 
     * @param newClass
     *            footer class name to set
     */
    public void setFooterClass(String newClass) {
        this.footerClass = newClass;
    }
}