/*
 * Created on Apr 27, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;

import org.apache.myfaces.component.html.ext.HtmlDataTable;

/**
 * This component represents a data table.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UIDataTable.java#1 $
 */

public class UIDataTable extends HtmlDataTable {

    /**
     * Returns the client id for the table. The client id changes based on the
     * current row that is being processed. This behavior should be in the base
     * class, but the parents classes in myFaces do not respect this.
     * 
     * @see javax.faces.component.UIComponent#getClientId(javax.faces.context.FacesContext)
     */
    public String getClientId(FacesContext facesContext) {
        if (facesContext == null) {
            throw new NullPointerException();
        }
        String baseClientId = super.getClientId(facesContext);
        int currentRowIndex = getRowIndex();
        if (currentRowIndex >= 0) {
            return (baseClientId + NamingContainer.SEPARATOR_CHAR + currentRowIndex);
        } else {
            return (baseClientId);
        }
    }
}