/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIOutput;

/**
 * This class represents an instance of a histogram bar item object.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UIBarItem.java#1 $
 */

public class UIBarItem extends UIOutput {

    protected static final String RENDERER_TYPE = "com.bluejungle.destiny.webui.renderers.BarItemRenderer";

    /**
     * Attribute name for the bar class to use
     */
    public static final String BAR_CLASS_ATTR_NAME = "barClass";

    /**
     * Attribute name for the container class to use
     */
    public static final String CONTAINER_CLASS_ATTR_NAME = "containerClass";

    /**
     * Attribute name for the maximum bar size (in pixels)
     */
    public static final String MAX_BAR_SIZE = "maxSize";

    /**
     * Attribute name for the maximum range value
     */
    public static final String MAX_RANGE_ATTR_NAME = "maxRange";

    /**
     * Attribute name for the orientation
     */
    public static final String ORIENTATION_ATTR_NAME = "Orientation";

    /**
     * Supported orientation values
     */
    public static final String ORIENTATION_HOR = "Horizontal";
    public static final String ORIENTATION_VER = "Vertical";

    /**
     * @see javax.faces.component.UIComponent#getRendererType()
     */
    public String getRendererType() {
        return RENDERER_TYPE;
    }

    /**
     * This component does not render its own children
     * 
     * @see javax.faces.component.UIComponent#getRendersChildren()
     */
    public boolean getRendersChildren() {
        return false;
    }
}