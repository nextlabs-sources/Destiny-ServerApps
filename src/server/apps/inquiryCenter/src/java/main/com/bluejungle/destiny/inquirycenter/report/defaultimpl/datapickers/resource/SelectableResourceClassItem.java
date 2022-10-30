/*
 * Created on May 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.resource;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.BaseReportSelectableItem;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentSelectedItem;
import com.bluejungle.destiny.types.resources.v1.ResourceClass;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemPossibleStyleClassIds;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/resource/SelectableResourceClassItem.java#1 $
 */

public class SelectableResourceClassItem extends BaseReportSelectableItem {

    private ResourceClass resourceClass;

    /**
     * Constructor
     * 
     * @param resourceClass
     *            resource class
     */
    public SelectableResourceClassItem(ResourceClass resourceClass) {
        super();
        if (resourceClass == null) {
            throw new NullPointerException("resourceClass cannot be null");
        }
        this.resourceClass = resourceClass;
        setStyleClassId(ISelectableItemPossibleStyleClassIds.DEFAULT);
        enable();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValue()
     */
    public String getDisplayValue() {
        final String displayValue = this.resourceClass.getName();
        return displayValue;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValueToolTip()
     */
    public String getDisplayValueToolTip() {
        return getDisplayValue();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return this.resourceClass.getName();
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ISelectableReportComponentItem#createSelected()
     */
    public ISelectedItem createSelected() {
        return new ReportComponentSelectedItem(getId(), getDisplayValue());
    }
}