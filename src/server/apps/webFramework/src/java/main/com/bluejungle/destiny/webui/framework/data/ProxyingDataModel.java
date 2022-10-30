/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.data;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

/**
 * Proxying data model allows data with a wrapped data model to be proxied and
 * possibly transformed before sending it to the client. The transformed object
 * is then saved in memory and returned to the client in case the client
 * traverses back through the data (i.e. it's only proxied once. To create a
 * ProxyingDataModel, extend this class and overide the
 * 
 * @see ProxyingDataModel#proxyRowData(Object) method
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/data/ProxyingDataModel.java#1 $
 */

public abstract class ProxyingDataModel extends DataModel {

    private DataModel wrappedDataModel;
    private List proxiedData;

    public ProxyingDataModel(DataModel wrappedDataModel) {
        super();
        this.wrappedDataModel = wrappedDataModel;
        int wrappedDataModelSize = this.wrappedDataModel.getRowCount();
        if (wrappedDataModelSize > 0) {
            this.proxiedData = new ArrayList(wrappedDataModelSize);
        } else {
            this.proxiedData = new ArrayList();
        }
    }

    /**
     * @see javax.faces.model.DataModel#isRowAvailable()
     */
    public boolean isRowAvailable() {
        return this.wrappedDataModel.isRowAvailable();
    }

    /**
     * @see javax.faces.model.DataModel#getRowCount()
     */
    public int getRowCount() {
        return this.wrappedDataModel.getRowCount();
    }

    /**
     * @see javax.faces.model.DataModel#getRowData()
     */
    public Object getRowData() {
        Object dataToReturn = null;

        int currentRowIndex = getRowIndex();

        // Need a synchronized block here to avoid corruption of the proxiedData data structure
        synchronized (proxiedData) {
            if (this.proxiedData.size() > currentRowIndex) {
                dataToReturn = this.proxiedData.get(currentRowIndex);
            } else {
                dataToReturn = proxyRowData(this.wrappedDataModel.getRowData());
                this.proxiedData.add(dataToReturn);
            }
        }
        
        return dataToReturn;
    }

    /**
     * @see javax.faces.model.DataModel#getRowIndex()
     */
    public int getRowIndex() {
        return this.wrappedDataModel.getRowIndex();
    }

    /**
     * @see javax.faces.model.DataModel#setRowIndex(int)
     */
    public void setRowIndex(int rowIndex) {
        this.wrappedDataModel.setRowIndex(rowIndex);
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

    /**
     * Retrieve the wrapped data model which is being proxied. Convenience
     * method for subclasses
     * 
     * @return the wrapped data model which is being proxied.
     */
    protected DataModel getWrappedDataModel() {
        return this.wrappedDataModel;
    }

    /**
     * Proxy the specified row data. The data returned will be the actual data
     * returned to the client. This method will only be called once for each
     * row, even if the client control allows reverse traversal of the data
     * 
     * @param rawData
     *            the data to proxy
     * @return the data which will be returned to the client
     */
    protected abstract Object proxyRowData(Object rawData);
}