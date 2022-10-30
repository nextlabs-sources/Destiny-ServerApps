/*
 * Created on May 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;

/**
 * Default Implementation of the IInternalSelectedItemList interface. Note that
 * this class is not thread safe
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/SelectedItemListImpl.java#2 $
 */
public class SelectedItemListImpl implements IInternalSelectedItemList {

    // Decalaring exact types below to be specific about the requirements
    private ArrayList itemsList = new ArrayList();
    private HashMap idToItemMap = new HashMap();

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.IInternalSelectedItemList#removeSelectedItem(java.lang.String)
     */
    public void removeSelectedItem(String id) {
        if (id == null) {
            throw new NullPointerException("id cannot be null.");
        }
        ISelectedItem itemRemoved = (ISelectedItem) this.idToItemMap.remove(id);
        this.itemsList.remove(itemRemoved);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.IInternalSelectedItemList#addAll(java.util.Collection)
     */
    public void addAll(Collection selectedItems) {
        if (selectedItems == null) {
            throw new NullPointerException("selectedItems cannot be null.");
        }

        Iterator selectedItemIterator = selectedItems.iterator();
        while (selectedItemIterator.hasNext()) {
            ISelectedItem nextSelectedItem = (ISelectedItem) selectedItemIterator.next();
            String selectedItemId = nextSelectedItem.getId();
            if (!this.idToItemMap.containsKey(selectedItemId)) {
                this.idToItemMap.put(nextSelectedItem.getId(), nextSelectedItem);
                this.itemsList.add(nextSelectedItem);
            }
        }
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.IInternalSelectedItemList#getSelectedItem(int)
     */
    public ISelectedItem getSelectedItem(int index) {
        return (ISelectedItem) this.itemsList.get(index);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList#iterate()
     */
    public Iterator iterator() {
        return this.itemsList.iterator();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList#containsSelectedItem(java.lang.String)
     */
    public boolean containsSelectedItem(String id) {
        if (id == null) {
            throw new NullPointerException("id cannot be null.");
        }

        return this.idToItemMap.containsKey(id);
    }
    
    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList#size()
     */
    public int size() {
        return this.itemsList.size();
    }
    
    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.IInternalSelectedItemList#clear()
     */
    public void clear() {
        this.idToItemMap.clear();
        this.itemsList.clear();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.IInternalSelectedItemList#getDataModel()
     */
    public DataModel getDataModel() {
        return new DataModelImpl();
    }

    private class DataModelImpl extends DataModel {

        private int currentIndex = 0;

        /**
         * @see javax.faces.model.DataModel#isRowAvailable()
         */
        public boolean isRowAvailable() {
            return (SelectedItemListImpl.this.itemsList.size() > this.currentIndex);
        }

        /**
         * @see javax.faces.model.DataModel#getRowCount()
         */
        public int getRowCount() {
            return SelectedItemListImpl.this.itemsList.size();
        }

        /**
         * @see javax.faces.model.DataModel#getRowData()
         */
        public Object getRowData() {
            return SelectedItemListImpl.this.itemsList.get(currentIndex);
        }

        /**
         * @see javax.faces.model.DataModel#getRowIndex()
         */
        public int getRowIndex() {
            return this.currentIndex;
        }

        /**
         * @see javax.faces.model.DataModel#setRowIndex(int)
         */
        public void setRowIndex(int rowIndex) {
            this.currentIndex = rowIndex;
        }

        /**
         * @see javax.faces.model.DataModel#getWrappedData()
         */
        public Object getWrappedData() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see javax.faces.model.DataModel#setWrappedData(java.lang.Object)
         */
        public void setWrappedData(Object data) {
            throw new UnsupportedOperationException();
        }
    }
}