/*
 * Created on Nov 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.html.HtmlDataTable;

/**
 * JSP tag for the simple data table. Creates a standard UIData component, but
 * allows for an message to be specified for display when the data is empty
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/StandardDataTableTag.java#1 $
 */

public class StandardDataTableTag extends DataTableTag {

    /**
     * @see com.bluejungle.destiny.webui.tags.DataTableTag#getComponentType()
     */
    public String getComponentType() {
        return HtmlDataTable.COMPONENT_TYPE;
    }
}
