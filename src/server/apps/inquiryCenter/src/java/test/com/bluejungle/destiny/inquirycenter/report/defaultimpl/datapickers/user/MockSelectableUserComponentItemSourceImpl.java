/*
 * Created on May 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.user;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.SelectableItemSourceException;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/user/MockSelectableUserComponentItemSourceImpl.java#1 $
 */

public class MockSelectableUserComponentItemSourceImpl extends SelectableUserComponentItemSourceImpl {

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.BaseReportSelectableItemSource#getCurrentReport()
     */
    public IReport getCurrentReport() {
        return super.getCurrentReport();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
     */
    public DataModel getSelectableItems(IFreeFormSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        DataModel data = super.getSelectableItems(searchSpec, selectedItems);

        // Slight hack: iterate datamodel date to populate the caching mechanism
        // on which generation of selectable items is based:
        warmUpDataModelCache(data);

        return data;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
     */
    public DataModel getSelectableItems(ISearchBucketSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        DataModel data = super.getSelectableItems(searchSpec, selectedItems);

        // Slight hack: iterate datamodel date to populate the caching mechanism
        // on which generation of selectable items is based:
        warmUpDataModelCache(data);

        return data;
    }

    /**
     * Iterates over the data model to enable caching that is used to generate
     * the list of selectable items
     * 
     * @param data
     */
    public void warmUpDataModelCache(DataModel data) {
        if (data != null) {
            for (int i = 0; i < data.getRowCount(); i++) {
                data.setRowIndex(i);
                data.getRowData();
            }
        }
    }
}