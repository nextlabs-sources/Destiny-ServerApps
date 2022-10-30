/*
 * Created on Jul 25, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers;


/**
 * This is the base class for items that are selectable in a report object
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/BaseReportSelectableItem.java#1 $
 */

public abstract class BaseReportSelectableItem implements ISelectableReportComponentItem {

    private String styleClassId;
    private boolean isSelectable;

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem#disable()
     */
    public void disable() {
        this.isSelectable = false;
    }

    /**
     * Enables the item
     */
    protected void enable() {
        this.isSelectable = true;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getStyleClassId()
     */
    public String getStyleClassId() {
        return this.styleClassId;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#isSelectable()
     */
    public boolean isSelectable() {
        return this.isSelectable;
    }

    /**
     * Set the style class id
     * 
     * @param newStyleClassId
     */
    protected void setStyleClassId(String newStyleClassId) {
        this.styleClassId = newStyleClassId;
    }
}