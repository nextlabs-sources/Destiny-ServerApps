/*
 * Created on Apr 25, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import org.apache.myfaces.custom.calendar.HtmlInputCalendarTag;

/**
 * This is the tag implementation for the input calendar control.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/InputCalendarTag.java#1 $
 */

public class InputCalendarTag extends HtmlInputCalendarTag {

    private static final String COMPONENT_TYPE = "com.bluejungle.destiny.datePicker";
    private static final String RENDERER_TYPE = "com.bluejungle.destiny.HTMLDatePickerRenderer";

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