/*
 * Created on May 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import java.util.Iterator;

import javax.faces.component.UIComponent;

import org.apache.myfaces.component.UIColumns;

/**
 * This component represents an horizontal column. It extends the basic myFaces
 * UIColumns component to workaround a client id bug.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UIHorColumn.java#1 $
 */

public class UIHorColumn extends UIColumns {

    private static final String FOOTER_FACET_NAME = "footer";
    private static final String HEADER_FACET_NAME = "header";

    /**
     * @see javax.faces.component.UIData#setRowIndex(int)
     */
    public void setRowIndex(int rowIndex) {
        super.setRowIndex(rowIndex);
        resetChildIds(this);
    }

    /**
     * Resets recursively the client id of the component given in argument, as
     * well as all the children and faces of the component. To reset the client
     * id, setting the id to the same value is sufficient.
     * 
     * @param parent
     *            component to process
     */
    private void resetChildIds(UIComponent parent) {
        if (parent != null) {
            Iterator it = parent.getChildren().iterator();
            while (it.hasNext()) {
                UIComponent child = (UIComponent) it.next();
                if (parent.equals(child)) {
                    continue;
                }
                resetChildIds(child);
            }
            resetChildIds(parent.getFacet(HEADER_FACET_NAME));
            resetChildIds(parent.getFacet(FOOTER_FACET_NAME));
            parent.setId(parent.getId());
        }
    }
}