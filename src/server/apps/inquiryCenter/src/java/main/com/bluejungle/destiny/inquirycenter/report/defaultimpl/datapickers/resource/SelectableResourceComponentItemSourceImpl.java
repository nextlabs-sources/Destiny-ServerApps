/*
 * Created on May 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.resource;

import java.util.Map;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.BaseReportSelectableItemSource;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentLookupException;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentQueryBroker;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportDataPickerUtil;
import com.bluejungle.destiny.types.resources.v1.ResourceClass;
import com.bluejungle.destiny.types.resources.v1.ResourceClassList;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.SelectableItemSourceException;
import com.bluejungle.destiny.webui.framework.data.ProxyingDataModel;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/resource/SelectableResourceComponentItemSourceImpl.java#1 $
 */

public class SelectableResourceComponentItemSourceImpl extends BaseReportSelectableItemSource {

    private static final String ITEM_NAME = "resourceClasses";

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.ISearchBucketSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItemList)
     */
    public DataModel getSelectableItems(ISearchBucketSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {

        DataModel modelToReturn = null;

//      NOTE: Disabled per Bug 4286
//        try {
//            ReportComponentQueryBroker componentQueryBroker = getReportComponentQueryBroker();
//            ResourceClassList resourceClasses = componentQueryBroker.getResourceClassesForSearchBucketSearchSpec(searchSpec);
//            modelToReturn = buildSelectableItemsDataModel(selectedItems, resourceClasses);
//            modelToReturn = buildSelectableItemsDataModel(selectedItems, r);
//        } catch (ReportComponentLookupException exception) {
//            throw new SelectableItemSourceException(exception);
//        }
        
        return modelToReturn;
    }

    /**
     * @throws SelectableItemSourceException
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.IFreeFormSearchSpec,
     *      com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItemList)
     */
    public DataModel getSelectableItems(IFreeFormSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
        DataModel modelToReturn = null;
        
//      NOTE: Disabled per Bug 4286
//        try {
//            ReportComponentQueryBroker componentQueryBroker = getReportComponentQueryBroker();
//            ResourceClassList resourceClasses = componentQueryBroker.getResourceClassesForFreeFormSearchSpec(searchSpec);
//            modelToReturn = buildSelectableItemsDataModel(selectedItems, resourceClasses);
//        } catch (ReportComponentLookupException exception) {
//            throw new SelectableItemSourceException(exception);
//        }

        return modelToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#storeSelectedItems(com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItemList)
     */
    public String storeSelectedItems(ISelectedItemList selectedItems) throws SelectableItemSourceException {
        IReport report = getCurrentReport();
        report.setResources(ReportDataPickerUtil.createInputFieldSelection(selectedItems, report.getResources()));
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
    private DataModel buildSelectableItemsDataModel(ISelectedItemList selectedItems, ResourceClassList matchingResourceClasses) {
        ReportComponentQueryBroker componentQueryBroker = getReportComponentQueryBroker();

        DataModel resourceClassesDataModel = new SelectableResourceClassItemsDataModel(matchingResourceClasses == null || matchingResourceClasses.getClasses() == null ? new ResourceClass[] {} : matchingResourceClasses.getClasses());

        // Calculate the policies that are already selected, and disable them:
        IReport reportBean = getCurrentReport();
        String existingSelections = reportBean.getResources();
        String[] alreadySelectedResourceClassIds = ResourceClassComponentEntityResolver.getResourceClassListFrom(existingSelections);

        // Create the disabling data model:
        DataModel disablingResourceClassDataModel = new ResourceClassDisablingDataModel(resourceClassesDataModel, selectedItems, alreadySelectedResourceClassIds);
        return new MemorizingDataModel(disablingResourceClassDataModel);
    }

    private class SelectableResourceClassItemsDataModel extends ProxyingDataModel {

        /**
         * Create an instance of SelectablePolicyItemsDataModel
         * 
         * @param wrappedDataModel
         */
        private SelectableResourceClassItemsDataModel(ResourceClass[] resourceClasses) {
            super(new ArrayDataModel(resourceClasses));
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
         */
        protected Object proxyRowData(Object rawData) {
            return new SelectableResourceClassItem((ResourceClass) rawData);
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
            Map viewedSelectItems = getViewedSelectableItems();
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