/*
 * Created on Jul 25, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.BaseSelectableItemSource;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.SelectableItemSourceException;

/**
 * This is the base class for the selectable item source for the report object
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/BaseReportSelectableItemSource.java#1 $
 */

public abstract class BaseReportSelectableItemSource extends BaseSelectableItemSource {

    private Map viewedSelectableItems = new HashMap();
    private String currentReportBinding;
    private ReportComponentQueryBroker reportComponentQueryBroker;

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#generateSelectedItems(java.lang.String)
     */
    public Set generateSelectedItems(String selectableItemId) throws SelectableItemSourceException {
        if (selectableItemId == null) {
            throw new NullPointerException("selectableItemId cannot be null.");
        }

        ISelectedItem selectedItem = null;
        Map viewedSelectableItems = getViewedSelectableItems();
        if (viewedSelectableItems.containsKey(selectableItemId)) {
            ISelectableItem selectableItem = (ISelectableItem) viewedSelectableItems.get(selectableItemId);
            selectedItem = ((ISelectableReportComponentItem) selectableItem).createSelected();
        } else {
            throw new IllegalArgumentException("Unknown selectable item id, " + selectableItemId);
        }

        return Collections.singleton(selectedItem);
    }

    /**
     * Returns the current report binding expression
     * 
     * @return the current binding expression to retrieve the current report
     */
    protected String getCurrentReportBinding() {
        return this.currentReportBinding;
    }

    /**
     * Returns the current report that the selection applies to
     * 
     * @return the current report that the selection applies to
     */
    protected IReport getCurrentReport() {
        return ReportDataPickerUtil.getCurrentReport(getCurrentReportBinding());
    }

    /**
     * Retrieve the AgentQueryBroker
     * 
     * @return the AgentQueryBroker
     */
    public ReportComponentQueryBroker getReportComponentQueryBroker() {
        return this.reportComponentQueryBroker;
    }

    /**
     * Returns the viewedSelectableItems.
     * 
     * @return the viewedSelectableItems.
     */
    protected Map getViewedSelectableItems() {
        return this.viewedSelectableItems;
    }

    /**
     * Sets the binding expression to retrieve the selected report object. This
     * is used to apply the selection to the current report and also know what
     * the current selected on the selected report is.
     * 
     * @param bindingExpr
     *            binding expression to retrieve the currently selected report.
     */
    public void setCurrentReportBinding(final String bindingExpr) {
        this.currentReportBinding = bindingExpr;
    }

    /**
     * Set by the faces-config
     * 
     * @param broker
     */
    public void setReportComponentQueryBroker(ReportComponentQueryBroker broker) {
        this.reportComponentQueryBroker = broker;
    }
}