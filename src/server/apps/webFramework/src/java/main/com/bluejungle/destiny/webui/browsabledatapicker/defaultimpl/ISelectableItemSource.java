/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import javax.faces.model.DataModel;

import java.util.Locale;
import java.util.Set;

/**
 * For each instance of the Browsable Data Picker view within a web application,
 * an instance of ISelectableItemSource must be created to provide the data to
 * select. The name of the bean must be
 * {@see IBrowsableDataPickerBean#SELECTABLE_ITEM_SOURCE_BEAN_NAME}. Note that
 * having multiple Selectable Item Source beans within a single application can
 * be achieved using the "aliasbean" JSF component offered through the MyFaces
 * package
 * 
 * @author sgoldstein
 */
public interface ISelectableItemSource {

    /**
     * Retrieve the selectable items for the given search bucket search spec and
     * list of currently selected items
     * 
     * @param searchSpec
     *            a search specification
     * @param selectedItems
     *            the list of currently selected items
     * @return the selectable items which match the specified search spec and
     *         are adjusted accordingly based on the provided selected item list
     */
    public DataModel getSelectableItems(ISearchBucketSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException;

    /**
     * Retrieve the selectable items for the given free form search spec and
     * list of currently selected items
     * 
     * @param searchSpec
     *            a search specification
     * @param selectedItems
     *            the list of currently selected items
     * @return the selectable items which match the specified search spec and
     *         are adjusted accordingly based on the provided selected item list
     */
    public DataModel getSelectableItems(IFreeFormSearchSpec searchSpec, ISelectedItemList selectedItemSet) throws SelectableItemSourceException;

    /**
     * For the given selectable item id, generate the list of associated
     * selected items. For instance, if a selectable item is a host group, the
     * implementation may return the list of hosts within the group
     * 
     * @param selectableItemId
     *            the id of a selectable item
     * @return the list of selected items associated with the selectable item of
     *         the specified id
     */
    public Set generateSelectedItems(String selectableItemId) throws SelectableItemSourceException;

    /**
     * Store the selected items in the selected item list. This method is called
     * after the user has completed selecting items and would like to persist
     * the results. Once the items are stored, return the navigation action to
     * be executed
     * 
     * @param selectedItems
     *            the items selected by the user
     * @return the navigation action to take after the items have been stored
     */
    public String storeSelectedItems(ISelectedItemList selectedItems) throws SelectableItemSourceException;

    /**
     * Cancel the data selection process and return the navigation action to be
     * executed after the cancelation is complete
     * 
     * @return the navigation action to be executed after the cancelation is
     *         complete
     */
    public String cancelDataSelection();

    /**
     * Retrieve the locale in which the selectable item data is written. This
     * determines the locale in which the search buckets will be displayed (for
     * more information on the localizing the search buckets, please see
     * {@see ISearchBucketBean})
     * 
     * @return the locale in which the selectable item data is written
     */
    public Locale getItemLocale();

    /**
     * Reset any cached state
     */
    public void reset();
}

