/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIComponentBase;

/**
 * This class represents a table row component instance. The table row is an
 * optional child of a data table control.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UIRow.java#1 $
 */

public class UIRow extends UIComponentBase {

    private static final String COMPONENT_FAMILY = "javax.faces.Data";

    /**
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}