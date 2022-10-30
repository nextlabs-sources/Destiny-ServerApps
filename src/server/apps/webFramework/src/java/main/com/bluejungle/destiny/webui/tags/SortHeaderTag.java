/*
 * Created on Mar 18, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;

import org.apache.myfaces.custom.sortheader.HtmlCommandSortHeaderTag;

/**
 * This is the command sort header tag. It extends the myFaces command sort
 * header tag.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/SortHeaderTag.java#1 $
 */

public class SortHeaderTag extends HtmlCommandSortHeaderTag {

    private static final String RENDERER_TYPE = "com.bluejungle.destiny.SortHeaderRenderer";
    private static final String SORTDOWN_CLASSNAME_ATTR = "sortDownClassName";
    private static final String SORTUP_CLASSNAME_ATTR = "sortUpClassName";
    private String sortDownClassName;
    private String sortUpClassName;

    /**
     * Constructor - Always render a sorting arrow.
     */
    public SortHeaderTag() {
        super();
        setArrow("true");
    }

    public String getRendererType()
    {
        return RENDERER_TYPE;
    }
    
    /**
     * Returns the style class to use when sorting is down
     * 
     * @return the style class to use when sorting is down
     */
    public String getSortDownClassName() {
        return this.sortDownClassName;
    }

    /**
     * Returns the style class to use when sorting is up
     * 
     * @return the style class to use when sorting is up
     */
    public String getSortUpClassName() {
        return this.sortUpClassName;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        component.getAttributes().put(SORTDOWN_CLASSNAME_ATTR, getSortDownClassName());
        component.getAttributes().put(SORTUP_CLASSNAME_ATTR, getSortUpClassName());
    }

    /**
     * Sets the style class when the sort is down
     * 
     * @param newClassName
     *            name of the CSS class
     */
    public void setSortDownClassName(String newClassName) {
        this.sortDownClassName = newClassName;
    }

    /**
     * Sets the style class when the sort is up
     * 
     * @param newClassName
     *            name of the CSS class
     */
    public void setSortUpClassName(String newClassName) {
        this.sortUpClassName = newClassName;
    }
}