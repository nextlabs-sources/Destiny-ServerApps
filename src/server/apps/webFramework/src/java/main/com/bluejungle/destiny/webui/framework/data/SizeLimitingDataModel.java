/*
 * Created on Oct 10, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.data;

import javax.faces.model.DataModel;

/**
 * A DataModel implementation which decorates another data model and limits the
 * number of rows that is returned to the client
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/data/SizeLimitingDataModel.java#1 $
 */

public class SizeLimitingDataModel extends DataModel {

    private final DataModel wrappedDataModel;
    private final int sizeLimit;

    /**
     * Create an instance of SizeLimitingDataModel
     * 
     * @param wrappedDataModel
     * @param sizeLimit
     */
    public SizeLimitingDataModel(final DataModel wrappedDataModel, final int sizeLimit) {
        if (wrappedDataModel == null) {
            throw new NullPointerException("wrappedDataModel cannot be null.");
        }

        this.wrappedDataModel = wrappedDataModel;
        int originalRowCount = this.wrappedDataModel.getRowCount();
        this.sizeLimit = originalRowCount > sizeLimit ? sizeLimit : originalRowCount;
    }

    /**
     * @see javax.faces.model.DataModel#getRowCount()
     */
    public int getRowCount() {
        return this.sizeLimit;
    }

    /**
     * @see javax.faces.model.DataModel#getRowData()
     */
    public Object getRowData() {
        return this.wrappedDataModel.getRowData();
    }

    /**
     * @see javax.faces.model.DataModel#getRowIndex()
     */
    public int getRowIndex() {
        return this.wrappedDataModel.getRowIndex();
    }

    /**
     * @see javax.faces.model.DataModel#getWrappedData()
     */
    public Object getWrappedData() {
        return this.wrappedDataModel.getWrappedData();
    }

    /**
     * @see javax.faces.model.DataModel#isRowAvailable()
     */
    public boolean isRowAvailable() {
        return this.wrappedDataModel.isRowAvailable() && this.getRowIndex() < this.sizeLimit;
    }

    /**
     * @see javax.faces.model.DataModel#setRowIndex(int)
     */
    public void setRowIndex(int rowIndex) {
        this.wrappedDataModel.setRowIndex(rowIndex);
    }

    /**
     * @see javax.faces.model.DataModel#setWrappedData(java.lang.Object)
     */
    public void setWrappedData(Object data) {
        this.wrappedDataModel.setWrappedData(data);
    }
}
