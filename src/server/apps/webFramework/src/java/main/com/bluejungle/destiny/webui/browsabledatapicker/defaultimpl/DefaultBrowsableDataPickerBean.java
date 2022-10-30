/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import com.bluejungle.destiny.appframework.CommonConstants;
import com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean;
import com.bluejungle.destiny.webui.browsabledatapicker.ISearchBucket;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.FreeFormSearchSpecImpl;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.IInternalSelectedItemList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.SelectedItemListImpl;
import com.bluejungle.destiny.webui.framework.data.SizeLimitingDataModel;
import com.bluejungle.destiny.webui.framework.faces.CommonSelectItemResourceLists;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.naming.LimitExceededException;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean}
 * interface. This implementation delegates to an
 * {@see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItemSource}
 * instance to read the data to display in the browsable data picker view. Note
 * that due to this delegation model, this bean does not provide any caching
 * facilities. FIX ME - Build a BaseSelectableItemSourceImpl which provides
 * caching!
 * 
 * FIX ME - Javadoc private methods FIX ME
 * 
 * @author sgoldstein
 */
public class DefaultBrowsableDataPickerBean implements IBrowsableDataPickerBean {

    public static final String SELECTABLE_ITEM_SOURCE_BEAN_NAME = "selectableItemSourceBean";
    public static final String SEARCH_BUCKET_BEAN_NAME = "searchBucketBean";

    private static final String GENERIC_ERROR_MESSAGE_BUNDLE_KEY = "error_page_general_error_message";
    private static final String MAX_SELECTABLE_ITEMS_EXCEEDED_WARNING_MESSAGE_BUNDLE_KEY = "data_picker_max_selectable_items_exceeded_warning_message_label";
    
    private static final Log LOG = LogFactory.getLog(DefaultBrowsableDataPickerBean.class.getName());

    private ISelectableItemSource selectableItemSource;
    private ISearchBucketBean searchBucketBean;
    private ISearchBucketExtended[] searchBuckets;
    private ISearchBucketExtended selectedSearchBucket;
    private DataModel freeFormSearchSelectableItems;
    private DataModel selectedSearchBucketSelectableItems;
    private String freeFormSearchString;
    private IInternalSelectedItemList selectedItems = new SelectedItemListImpl();
    private int maxSelectableItemsToDisplay = -1;

    public void setSearchBucketBean(ISearchBucketBean searchBucketBean) {
        if (searchBucketBean == null) {
            throw new NullPointerException("searchBucketBean cannot be null.");
        }

        this.searchBucketBean = searchBucketBean;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#prerender()
     */
    public void prerender() {
        if (this.maxSelectableItemsToDisplay == -1) {
            SelectItem firstMaximumSelectableItemsMenuOption = (SelectItem) getMaxSelectableItemsToDisplayOptions().iterator().next();
            this.maxSelectableItemsToDisplay = ((Integer)firstMaximumSelectableItemsMenuOption.getValue()).intValue();
        }

        loadSearchBucketsIfNecessary();
        loadSelectableItemsIfNecessary();               
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#getSelectedSearchBucket()
     */
    public ISearchBucket getSelectedSearchBucket() {
        return this.selectedSearchBucket;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#getSearchBuckets()
     */
    public ISearchBucket[] getSearchBuckets() {
        return this.searchBuckets;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#getSelectableItems()
     */
    public DataModel getSelectableItems() {
        DataModel selectableItemToReturn = null;
        if (isFreeFormSearch()) {
            selectableItemToReturn = this.freeFormSearchSelectableItems;
        } else {
            selectableItemToReturn = this.selectedSearchBucketSelectableItems;
        }

        return selectableItemToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#getSelectedItems()
     */
    public DataModel getSelectedItems() {
        return this.selectedItems.getDataModel();
    }

    
    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#getMaxSelectableItemsToDisplayOptions()
     */
    public Collection getMaxSelectableItemsToDisplayOptions() {
        Locale currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return CommonSelectItemResourceLists.MAX_UI_ELEMENT_LIST_SIZE_SELECT_ITEMS.getSelectItemResources(currentLocal);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#getMaxSelectableItemsToDisplay()
     */
    public int getMaxSelectableItemsToDisplay() {
        return this.maxSelectableItemsToDisplay;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#setMaxSelectableItemsToDisplay(int)
     */
    public void setMaxSelectableItemsToDisplay(int maxSelectableItemsToDisplay) {
        this.maxSelectableItemsToDisplay = maxSelectableItemsToDisplay;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#setSelectedSearchBucket(int)
     */
    public void setSelectedSearchBucket(int bucketIndex) {
        if ((bucketIndex < 0) || (bucketIndex >= this.searchBuckets.length)) {
            throw new IllegalArgumentException("bucketIndex out of bounds");
        }

        this.selectedSearchBucket = this.searchBuckets[bucketIndex];
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#cancelFreeFormSearch()
     */
    public void cancelFreeFormSearch() {
        this.freeFormSearchString = null;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#getFreeFormSearchString()
     */
    public String getFreeFormSearchString() {
        return (this.freeFormSearchString != null) ? this.freeFormSearchString : "";
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#setFreeFormSearchString(java.lang.String)
     */
    public void setFreeFormSearchString(String freeFormSearchString) {
        if (freeFormSearchString == null) {
            throw new NullPointerException("freeFormSearchString cannot be null.");
        }

        this.freeFormSearchString = freeFormSearchString;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#isFreeFormSearch()
     */
    public boolean isFreeFormSearch() {
        return (this.freeFormSearchString != null);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#selectItem(java.lang.String)
     */
    public void selectItem(String selectableItemId) {
        if (selectableItemId == null) {
            throw new NullPointerException("selectableItemId cannot be null.");
        }

        ISelectableItemSource selectableItemSource = getSelectableItemSource();

        try {
            Set selectedItems = selectableItemSource.generateSelectedItems(selectableItemId);
            addSelectedItems(selectedItems);
        } catch (SelectableItemSourceException exception) {
            StringBuffer errorMessage = new StringBuffer("Failed to select item with id, ");
            errorMessage.append(selectableItemId);
            errorMessage.append(", in browsable data picker view.");
            getLog().error(errorMessage.toString(), exception);

            addErrorMessage();
        }
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#deselectItem(java.lang.String)
     */
    public void deselectItem(String selectedItemId) {
        this.removeSelectedItem(selectedItemId);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#storeSelectedItems()
     */
    public String storeSelectedItems() {
        ISelectableItemSource selectableItemSource = getSelectableItemSource();

        String actionToReturn = null;
        try {
            actionToReturn = selectableItemSource.storeSelectedItems(this.selectedItems);
            this.reset();
        } catch (SelectableItemSourceException exception) {
            getLog().error("Failed to store items in browsable data picker view.", exception);

            addErrorMessage();
        }

        return actionToReturn;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean#cancelDataSelection()
     */
    public String cancelDataSelection() {
        String actionToReturn = getSelectableItemSource().cancelDataSelection();
        this.reset();
        return actionToReturn;
    }

    // FIX ME- Should this be added to interface?
    public void reset() {
        this.selectedItems.clear();
        this.selectableItemSource.reset();
        this.searchBuckets = null; // Clear in case next use is in different
        // locale
        this.selectedSearchBucket = null;
        this.freeFormSearchSelectableItems = null;
        this.selectedSearchBucketSelectableItems = null;
        this.freeFormSearchString = null;
    }

    void setSelectableItemSource(ISelectableItemSource selectableItemSource) {
        if (selectableItemSource == null) {
            throw new NullPointerException("selectableItemSource cannot be null.");
        }

        this.selectableItemSource = selectableItemSource;
    }

    /**
     * 
     */
    private void loadSearchBucketsIfNecessary() {
        if (this.searchBuckets == null) {
            loadSearchBuckets();
        }
    }

    /**
     * 
     */
    private void loadSearchBuckets() {
        ISelectableItemSource selectableItemSource = getSelectableItemSource();
        Locale selectableItemLocale = selectableItemSource.getItemLocale();
        this.searchBuckets = getSearchBucketBean().getSearchBuckets(selectableItemLocale);

        // Select the first bucket when first loaded
        selectFirstSearchBucket();
    }

    private void loadSelectableItemsIfNecessary() {
        if (isFreeFormSearch()) {
            loadSelectableItemsFromFreeFormSearch();
        } else {
            loadSelectableItemsFromSelectedBucketIfNecessary();
        }        
    }

    private void loadSelectableItemsFromSelectedBucketIfNecessary() {
        ISelectableItemSource selectableItemSource = getSelectableItemSource();
        ISearchBucketSearchSpec searchSpec = this.selectedSearchBucket.getSeachSpec(this.maxSelectableItemsToDisplay + 1);

        try {
            this.selectedSearchBucketSelectableItems = selectableItemSource.getSelectableItems(searchSpec, this.selectedItems);
            if (this.selectedSearchBucketSelectableItems.getRowCount() > this.maxSelectableItemsToDisplay) {
                addSearchWarningMessage();
                this.selectedSearchBucketSelectableItems = new SizeLimitingDataModel(this.selectedSearchBucketSelectableItems, this.maxSelectableItemsToDisplay);
            }
        } catch (SelectableItemSourceException exception) {
            StringBuffer errorMessage = new StringBuffer("Failed to load selectable items for select bucket, ");
            errorMessage.append(this.selectedSearchBucket.getDisplayValue());
            errorMessage.append(", in browsable data picker view.");
            getLog().error(errorMessage.toString(), exception);

            addErrorMessage();
        }
    }

    private void loadSelectableItemsFromFreeFormSearch() {
        ISelectableItemSource selectableItemSource = getSelectableItemSource();
        IFreeFormSearchSpec searchSpec = new FreeFormSearchSpecImpl(getFreeFormSearchString(), this.maxSelectableItemsToDisplay + 1);

        try {
            this.freeFormSearchSelectableItems = selectableItemSource.getSelectableItems(searchSpec, this.selectedItems);
            if (this.freeFormSearchSelectableItems.getRowCount() > this.maxSelectableItemsToDisplay) {
                addSearchWarningMessage();  
                this.freeFormSearchSelectableItems = new SizeLimitingDataModel(this.freeFormSearchSelectableItems, this.maxSelectableItemsToDisplay);
            }
        } catch (SelectableItemSourceException exception) {
            StringBuffer errorMessage = new StringBuffer("Failed to load selectable items for free form search string, ");
            errorMessage.append(getFreeFormSearchString());
            errorMessage.append(", in browsable data picker view.");
            getLog().error(errorMessage.toString(), exception);

            addErrorMessage();
        }
    }

    private void selectFirstSearchBucket() {
        if ((this.searchBuckets == null) || (this.searchBuckets.length == 0)) {
            throw new IllegalStateException("Search buckets not properly loaded");
        }

        this.selectedSearchBucket = this.searchBuckets[0];
    }

    private void clearSelectableItemsList() {
        this.selectedSearchBucketSelectableItems = null;
        this.freeFormSearchSelectableItems = null;
    }

    private void addSelectedItems(Set selectedItemsToAdd) {
        this.selectedItems.addAll(selectedItemsToAdd);
        this.clearSelectableItemsList();
    }

    private void removeSelectedItem(String selectedItemId) {
        if (selectedItemId == null) {
            throw new NullPointerException("selectedItemId cannot be null.");
        }

        this.selectedItems.removeSelectedItem(selectedItemId);
        this.clearSelectableItemsList();
    }

    /**
     * @return
     */
    private ISearchBucketBean getSearchBucketBean() {
        return this.searchBucketBean;
    }

    /**
     * @return
     */
    private ISelectableItemSource getSelectableItemSource() {
        return this.selectableItemSource;
    }

    /**
     * Add a non-parameterized single line error message with the specified
     * bundle key
     */
    private void addErrorMessage() {
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);

        String messageDetail = getCommonBundle().getString(GENERIC_ERROR_MESSAGE_BUNDLE_KEY);
        facesMessage.setDetail(messageDetail);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    /**
     * Add a warning message that the list of displayed items isn't complete
     */
    private void addSearchWarningMessage() {
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setSeverity(FacesMessage.SEVERITY_WARN);

        String messageDetail = getCommonBundle().getString(MAX_SELECTABLE_ITEMS_EXCEEDED_WARNING_MESSAGE_BUNDLE_KEY);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Locale currentLocale = facesContext.getViewRoot().getLocale();
        MessageFormat messageDetailMessageFormat = new MessageFormat(messageDetail, currentLocale);
        String formattedMessageDetail = messageDetailMessageFormat.format(new Object[]{new Integer(this.maxSelectableItemsToDisplay)});
        facesMessage.setDetail(formattedMessageDetail);
        facesContext.addMessage(null, facesMessage);
    }
    
    /**
     * Retrieve the Common Bundle
     * 
     * @return the Common Bundle
     */
    private static ResourceBundle getCommonBundle() {
        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return ResourceBundle.getBundle(CommonConstants.COMMON_BUNDLE_NAME, currentLocale);
    }

    /**
     * Retrieve a Log
     * 
     * @return a Log for logging
     */
    private Log getLog() {
        return LOG;
    }
}