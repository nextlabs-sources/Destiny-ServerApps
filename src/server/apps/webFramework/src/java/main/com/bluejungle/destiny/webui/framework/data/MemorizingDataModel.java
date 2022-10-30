/*
 * Created on Jul 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.data;

import java.util.HashMap;
import java.util.Map;

import javax.faces.model.DataModel;

/**
 * A DataModel implementation which maintains a reference to each data item that
 * is returned. The data items must implement the IMemorizeableDataItem
 * interface. After a data item has been returned, it may be retrieved by id
 * through the {@see #getMemorizedDataItem(Long)}<br />
 * <br />
 * This implementation is useful, for example, when a list of menu items is
 * returned within a data model and displayed within the application. When a
 * menu item is selected, the id of the menu item is submitted to the model. The
 * menu item selected can be retrieved by id through an instance of this class
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/data/MemorizingDataModel.java#1 $
 */

public class MemorizingDataModel extends ProxyingDataModel {

    private final Map memorizedData = new HashMap();

    /**
     * Create an instance of MemorizingDataModel
     * 
     * @param wrappedDataModel
     */
    public MemorizingDataModel(DataModel wrappedDataModel) {
        super(wrappedDataModel);
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.data.ProxyingDataModel#proxyRowData(java.lang.Object)
     */
    protected Object proxyRowData(Object rawData) {
        IMemorizeableDataItem identifiableData = (IMemorizeableDataItem) rawData;
        memorizedData.put(identifiableData.getId(), rawData);

        return rawData;
    }

    /**
     * Retrieve a memorized data item
     * 
     * @param id
     *            the id of the memorized data item
     * @return the memorized data item
     * @throws IllegalArgumentException
     *             if there is no memorized data item which matches the
     *             specified id
     */
    public IMemorizeableDataItem getMemorizedDataItem(String id) {
        if (!this.memorizedData.containsKey(id)) {
            throw new IllegalArgumentException("A memorized item with the specified id, " + id + ", does not exist.");
        }

        return (IMemorizeableDataItem) memorizedData.get(id);
    }

    /**
     * Determine if an item with the specified id has been memorized
     * 
     * @param id
     *            the id of the item to test
     * @return true if an item with the specified id has been memorized; false
     *         otherwise
     */
    public boolean isDataItemMemorized(String id) {
        return this.memorizedData.containsKey(id);
    }
}