/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker;

import javax.faces.model.DataModel;

import java.util.Collection;
import java.util.Map;

/**
 * An instance of IBrowsableDataBean is used to provide data to the browsable
 * data picker view.
 * 
 * @author sgoldstein
 */
public interface IBrowsableDataPickerBean {

    public static final String BEAN_NAME = "browsableDataPickerBean";

    /**
     * Retrieve the currently selected search bucket
     * 
     * @return the currently selected search bucket
     */
    public ISearchBucket getSelectedSearchBucket();

    /**
     * Set the specified search bucket to be the currently selected search
     * bucket
     * 
     * @param bucketIndex
     *            the index of the search bucket to select
     */
    public void setSelectedSearchBucket(int bucketIndex);

    /**
     * Retrieve the ordered array of search buckets to display within the
     * browsable data picker view.
     * 
     * @return the ordered array of search buckets to display within the
     *         browsable data picker view.
     */
    public ISearchBucket[] getSearchBuckets();

    /**
     * Retrieve the list of items which can be selected in the data picker view.
     * The list will be built based on the current view's current search state
     * 
     * @return the list of items which can be selected in the data picker view
     */
    public DataModel getSelectableItems();

    /**
     * Retrieve the list of items which have been selected
     * 
     * @return the list of items which have been selected
     */
    public DataModel getSelectedItems();

    /**
     * Retrieve the list of sizes to display in the maximum list size drop down
     * menu
     * 
     * @return the list of sizes to display in the maximum list size drop down
     *         menu as Collection of {@see javax.faces.model.SelectItem} instances
     *         
     * FIX ME - Not ideal:
     *          1.  Returning a Map to work with IResourceBundleResourceMap and f:selectItems tag.  Not type safe
     *          2.  This list doesn't belong on this bean.  Ideally, it would be taken by the jsp straight from a resource bundle.
     *          
     * Possible Solution - Use an enhanced Resource Bundle which returns a Map - Not sure if this is possible - Object ResourceBundle.getObject(String key)
     */
    public Collection getMaxSelectableItemsToDisplayOptions();

    /**
     * Retrieve the setting for the maximum number of selectable items to
     * display
     * 
     * @return the maximum number of selectable items to display
     */
    public int getMaxSelectableItemsToDisplay();

    /**
     * Set the maximum number of selectable items to display
     * 
     * @param maxSelectableItemsToDisplay
     *            the maximum number of selectable items to display
     */
    public void setMaxSelectableItemsToDisplay(int maxSelectableItemsToDisplay);

    /**
     * Cancel any free form search which had previously been run in the
     * browsable data picker view
     */
    public void cancelFreeFormSearch();

    /**
     * Set the free form search string. This will activate the free form search
     * mode, causing the selectable items to be retrieve by the free form search
     * string instead of the currently selected search bucket. To cancel free
     * form search, invoke {@see #cancelFreeFormSearch()}
     * 
     * @param freeFormSearchString
     *            the free form search string to set
     */
    public void setFreeFormSearchString(String freeFormSearchString);

    /**
     * Retrieve the currently selected free form search string. If the free form
     * search mode is not currently active, the empty string will be returned
     * 
     * @return the free form search string or the empty string when the free
     *         form search mode is not active
     */
    public String getFreeFormSearchString();

    /**
     * Determine if the free form search mode is currently active
     * 
     * @return true if the free form search mode is active; false otherwise
     */
    public boolean isFreeFormSearch();

    /**
     * Select the specified selectable item
     * 
     * @param selectableItemId
     *            the id of the item to select
     */
    public void selectItem(String selectableItemId);

    /**
     * Deselect the specified selected item
     * 
     * @param selectedItemId
     *            the id of the item to deselect
     */
    public void deselectItem(String selectedItemId);

    /**
     * Store the selected items
     * 
     * @return the navigation action to take after storing the items
     */
    public String storeSelectedItems();

    /**
     * Cancel data selection process
     * 
     * @return the navigation action to take after canceling the process
     */
    public String cancelDataSelection();
}
