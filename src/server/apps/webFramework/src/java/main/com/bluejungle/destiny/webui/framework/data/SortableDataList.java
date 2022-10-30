/*
 * Created on Mar 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.data;

import java.util.List;

/**
 * This is the sortable data list implementation. It is an abstract class that
 * should be overwritten based on the specific data type that should be
 * manipulated. Faces-config will specified the child classes for each data
 * type.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/data/SortableDataList.java#1 $:
 */
public abstract class SortableDataList implements ISortableDataList {

    private boolean sortAscending;
    private String sortFieldName;

    /**
     * Constructor
     * 
     * @param defaultSortColumn
     *            column that should be sorted by default
     */
    protected SortableDataList(String defaultSortColumn) {
        sortFieldName = defaultSortColumn;
        sortAscending = getDefaultSortAscending(defaultSortColumn);
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.data.ISortableDataList#getDefaultSortAscending(java.lang.String)
     */
    public abstract boolean getDefaultSortAscending(String fieldName);

    /**
     * @see com.bluejungle.destiny.webui.framework.data.ISortableDataList#getSortAscending()
     */
    public boolean getSortAscending() {
        return this.sortAscending;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.data.ISortableDataList#getSortFieldName()
     */
    public String getSortFieldName() {
        return this.sortFieldName;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.data.ISortableDataList#setSortAscending(boolean)
     */
    public void setSortAscending(boolean newSort) {
        this.sortAscending = newSort;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.data.ISortableDataList#setSortFieldName(java.lang.String)
     */
    public void setSortFieldName(String newSortFieldName) {
        this.sortFieldName = newSortFieldName;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.data.IDataList#getData()
     */
    public abstract List getData();

    /**
     * Sort the list (this may involve performing a new data query, based on the
     * nature of the data set).
     */
    protected abstract void sort(String column, boolean ascending);

    public void sort(String sortColumn) {
        if (sortColumn == null) {
            throw new IllegalArgumentException("Argument sortColumn must not be null.");
        }

        if (this.sortFieldName.equals(sortColumn)) {
            //current sort equals new sortColumn -> reverse sort order
            this.sortAscending = !this.sortAscending;
        } else {
            //sort new column in default direction
            this.sortFieldName = sortColumn;
            this.sortAscending = getDefaultSortAscending(this.sortFieldName);
        }
        sort(this.sortFieldName, this.sortAscending);
    }
}