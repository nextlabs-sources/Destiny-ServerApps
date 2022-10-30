/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.webapp.UIComponentTag;

/**
 * JSP Tag for a tabbed pane component
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/TabbedPaneTag.java#1 $
 */
public class TabbedPaneTag extends UIComponentTag {

    /**
     * The component type created from the TabbedPaneTag
     */
    public static final String COMPONENT_TYPE = "com.bluejungle.destiny.TabbedPane";

    /**
     * The renderer type used to render the component created by the
     * TabbedPaneTag
     */
    public static final String RENDERER_TYPE = "com.bluejungle.destiny.TabbedPaneRenderer";

    /**
     * Create an instance of TabbedPaneTag
     *  
     */
    public TabbedPaneTag() {
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
}