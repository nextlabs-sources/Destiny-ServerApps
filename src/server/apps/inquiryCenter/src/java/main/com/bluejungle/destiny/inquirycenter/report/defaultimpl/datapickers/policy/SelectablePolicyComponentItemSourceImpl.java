/*
 * Created on May 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.policy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.BaseReportSelectableItemSource;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ISelectableReportComponentItem;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentLookupException;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportDataPickerUtil;
import com.bluejungle.destiny.types.policies.v1.Policy;
import com.bluejungle.destiny.types.policies.v1.PolicyList;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.SelectableItemSourceException;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/policy/SelectablePolicyComponentItemSourceImpl.java#1 $
 */

public class SelectablePolicyComponentItemSourceImpl extends BaseReportSelectableItemSource {

    private static final String ITEM_NAME = "policies";

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.ISearchBucketSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItemList)
     */
    public DataModel getSelectableItems(ISearchBucketSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        DataModel modelToReturn = null;

        try {
            ReportComponentQueryBroker componentQueryBroker = getReportComponentQueryBroker();
            PolicyList policies = componentQueryBroker.getPoliciesForSearchBucketSearchSpec(searchSpec);
            modelToReturn = buildSelectableItemsDataModel(selectedItems, policies);
        } catch (ReportComponentLookupException exception) {
            throw new SelectableItemSourceException(exception);
        }
        return modelToReturn;
    }

    /**
     * @throws SelectableItemSourceException
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.IFreeFormSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItemList)
     */
    public DataModel getSelectableItems(IFreeFormSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        DataModel modelToReturn = null;

        try {
            ReportComponentQueryBroker componentQueryBroker = getReportComponentQueryBroker();
            PolicyList policies = componentQueryBroker.getPoliciesForFreeFormSearchSpec(searchSpec);
            modelToReturn = buildSelectableItemsDataModel(selectedItems, policies);
        } catch (ReportComponentLookupException exception) {
            throw new SelectableItemSourceException(exception);
        }

        return modelToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#generateSelectedItems(java.lang.String)
     */
    public Set generateSelectedItems(String selectableItemId) throws SelectableItemSourceException {
        if (selectableItemId == null) {
            throw new NullPointerException("selectableItemId cannot be null.");
        }

        ISelectedItem selectedItem = null;
        Map viewedSelectItems = getViewedSelectableItems();
        if (viewedSelectItems.containsKey(selectableItemId)) {
            ISelectableItem selectableItem = (ISelectableItem) viewedSelectItems.get(selectableItemId);
            selectedItem = ((ISelectableReportComponentItem) selectableItem).createSelected();
        } else {
            throw new IllegalArgumentException("Unknown selectable item id, " + selectableItemId);
        }

        return Collections.singleton(selectedItem);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#storeSelectedItems(com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItemList)
     */
    public String storeSelectedItems(ISelectedItemList selectedItems) throws SelectableItemSourceException {
        IReport report = getCurrentReport();
        report.setPolicies(ReportDataPickerUtil.createInputFieldSelection(selectedItems, report.getPolicies()));
        return getReturnAction();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.BaseSelectableItemSource#getItemName()
     */
    protected String getItemName() {
        return ITEM_NAME;
    }

    /**
     * @param selectedItems
     * @param matchingPolicies
     * @return data model
     */
    private DataModel buildSelectableItemsDataModel(ISelectedItemList selectedItems, PolicyList matchingPolicies) {
        ReportComponentQueryBroker componentQueryBroker = getReportComponentQueryBroker();

        DataModel policiesDataModel = new SelectablePolicyItemsDataModel(matchingPolicies == null || matchingPolicies.getPolicies() == null ? new Policy[] {} : matchingPolicies.getPolicies());

        // Calculate the policies that are already selected, and disable them:
        IReport reportBean = getCurrentReport();
        String existingSelections = reportBean.getPolicies();
        String[] alreadySelectedPolicyIds = PolicyComponentEntityResolver.getPolicyListFrom(existingSelections);

        // Create the disabling data model:
        DataModel disablingPolicyDataModel = new PolicyDisablingDataModel(policiesDataModel, selectedItems, alreadySelectedPolicyIds);
        return new MemorizingDataModel(disablingPolicyDataModel);
    }

    private class SelectablePolicyItemsDataModel extends ProxyingDataModel {

        /**
         * Create an instance of SelectablePolicyItemsDataModel
         * 
         * @param wrappedDataModel
         */
        private SelectablePolicyItemsDataModel(Policy[] policies) {
            super(new ArrayDataModel(policies));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new SelectablePolicyItem((Policy) rawData);
        }
    }

    private class MemorizingDataModel extends ProxyingDataModel {

        /**
         * Create an instance of MemorizingDataModel
         * 
         * @param wrappedDataModel
         */
        private MemorizingDataModel(DataModel wrappedDataModel) {
            super(wrappedDataModel);
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            Map viewedSelectItems = SelectablePolicyComponentItemSourceImpl.this.getViewedSelectableItems();
            if (getRowIndex() == 0) {
                // A bit of a hack to keep memory use in check
                viewedSelectItems.clear();
            }
            ISelectableItem selectableItem = (ISelectableItem) rawData;
            viewedSelectItems.put(selectableItem.getId(), selectableItem);
            return selectableItem;
        }
    }
}