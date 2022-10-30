/*
 * Created on Apr 3, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.webui.tags;

import com.sun.faces.taglib.html_basic.CommandLinkTag;


/**
 * This is the tag of the PopupLink component.  
 * 
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/nextlabs/destiny/webui/tags/PopupLinkTag.java#1 $
 */

public class PopupLinkTag extends CommandLinkTag {

    /**
     * The component type created from the PopupLink
     */
    public static final String COMPONENT_TYPE = "com.nextlabs.destiny.popupLink";

    /**
     * The renderer type used to render the component created by the
     * TabbedPaneTag
     */
    public static final String RENDERER_TYPE = "com.nextlabs.destiny.popupLinkRenderer";

    /**
     * Create an instance of TabbedPaneTag
     *  
     */
    public PopupLinkTag() {
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
