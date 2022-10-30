/*
 * Created on Mar 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.html.HtmlCommandLink;

/**
 * UIMenuItem represents an individual menu item within a menu placed in a
 * Destiny web application. Instances of UIMenuItem are added as children to an
 * instance of a UIMenu component to form a menu
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UIMenuItem.java#1 $
 */
public class UIMenuItem extends HtmlCommandLink {

    /*
     * Ideally, we would extend UICommand here, rather than HtmlCommandLink.
     * However, I want to reuse the my my faces html link render and it expects
     * an HtmlCommandLink component. Hopefully, in future versins of the spec,
     * this won't be necessary
     */

    /**
     * The component family of the UIMenuItem component
     */
    public static final String COMPONENT_FAMILY = "com.bluejungle.destiny.MenuItem";

    /**
     * The renderer type used to render a UIMenu by default
     */
    public static final String RENDERER_TYPE = "com.bluejungle.destiny.MenuItemRenderer";

    //private String viewIdPattern;

    /**
     * Create an instance of UIMenuItem
     */
    public UIMenuItem() {
        super();
        setRendererType(RENDERER_TYPE);
    }

    /**
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}