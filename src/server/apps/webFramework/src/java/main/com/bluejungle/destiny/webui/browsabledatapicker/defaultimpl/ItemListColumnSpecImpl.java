/*
 * Created on Jun 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import com.bluejungle.destiny.webui.browsabledatapicker.IItemListColumnSpec;

/**
 * Default implementation of the IItemListColumnSpec interface
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/ItemListColumnSpecImpl.java#1 $
 */

public class ItemListColumnSpecImpl implements IItemListColumnSpec {

    private String columnHeader;
    private String columnDisplayablePropertyId;

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IItemListColumnSpec#getColumnHeader()
     */
    public String getColumnHeader() {
        return this.columnHeader;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IItemListColumnSpec#getColumnDisplayablePropertyId()
     */
    public String getColumnDisplayablePropertyId() {
        return this.columnDisplayablePropertyId;
    }

    /**
     * Set the columnDisplayablePropertyId
     * 
     * @param columnDisplayablePropertyId
     *            The columnDisplayablePropertyId to set.
     */
    public void setColumnDisplayablePropertyId(String columnDisplayablePropertyId) {
        this.columnDisplayablePropertyId = columnDisplayablePropertyId;
    }

    /**
     * Set the columnHeader
     * 
     * @param columnHeader
     *            The columnHeader to set.
     */
    public void setColumnHeader(String columnHeader) {
        this.columnHeader = columnHeader;
    }
}