/*
 * Created on Mar 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;

/**
 * The UIMenu JSF component represents a menu in a Destiny web application.
 * Menu items are added to the menu by adding child components of type UIMenuItem.
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UIMenu.java#1 $
 */
public class UIMenu extends UIPanel {

    /**
     * The component family of the UIMenu component
     */
    public static final String COMPONENT_FAMILY = "com.bluejungle.destiny.Menu";
    
    /**
     * The renderer type used to render a UIMenu by default
     */
    public static final String RENDERER_TYPE = "com.bluejungle.destiny.MenuRenderer";

    /**
     * Create an instance of UIMenu
     *  
     */
    public UIMenu() {
        super();
        setRendererType(RENDERER_TYPE);
    }

    /**
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * @see javax.faces.component.UIComponent#processDecodes(javax.faces.context.FacesContext)
     */
    public void processDecodes(FacesContext context) {
        if (context == null) {
            throw new NullPointerException("context cannot be null.");
        }

        if (isRendered()) {
            try {
                decode(context);
            } catch (RuntimeException exception) {
                context.renderResponse();
                throw exception;
            }
        }
    }
}