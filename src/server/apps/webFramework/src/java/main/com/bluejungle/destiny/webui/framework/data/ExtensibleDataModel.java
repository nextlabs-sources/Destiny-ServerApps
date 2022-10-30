/*
 * Created on May 3, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.DataModel;

/**
 * The extensible data model is a special data model implementation that can
 * load records by chunks. Initially, only a subset of the total result is
 * loaded, but subsequent additions are possible with the use of the
 * <code>addNewRecords</code> API. The total number of expected rows can be
 * set through the <code>setTotalRowCount</code> API.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/data/ExtensibleDataModel.java#1 $
 */

public class ExtensibleDataModel extends DataModel {

    private int currentRowIndex;
    private int totalNbRows;
    private int realNbOfRows;
    private List records = new ArrayList();

    /**
     * Adds new records to the data model
     * 
     * @param recordsToAdd
     */
    public void addNewRecords(Collection recordsToAdd) {
        this.records.addAll(recordsToAdd);
        this.realNbOfRows = this.records.size();
    }

    /**
     * @see javax.faces.model.DataModel#isRowAvailable()
     */
    public boolean isRowAvailable() {
        return this.currentRowIndex < getRealNbOfRows();
    }

    /**
     * Returns the real number of rows actually filled up in the data model.
     * This number is obviously smaller than the total number of rows.
     * 
     * @return the real number of rows actually filled up in the data model
     */
    public int getRealNbOfRows() {
        return this.realNbOfRows;
    }

    /**
     * Returns the total number of rows. These rows may not actually be in the
     * data model yet, as it is progressively filled based on the user needs.
     * 
     * @see javax.faces.model.DataModel#getRowCount()
     */
    public int getRowCount() {
        return this.totalNbRows;
    }

    /**
     * @see javax.faces.model.DataModel#getRowData()
     */
    public Object getRowData() {
        return this.records.get(getRowIndex());
    }

    /**
     * @see javax.faces.model.DataModel#getRowIndex()
     */
    public int getRowIndex() {
        return this.currentRowIndex;
    }

    /**
     * @see javax.faces.model.DataModel#setRowIndex(int)
     */
    public void setRowIndex(int rowIndex) {
        this.currentRowIndex = rowIndex;
    }

    /**
     * Sets the total number of expected rows. This is normally higher than the
     * current number of rows in the wrapped data.
     * 
     * @param newTotalRowCount
     *            the total number of rows
     */
    public void setTotalRowCount(int newTotalRowCount) {
        this.totalNbRows = newTotalRowCount;
    }

    /**
     * @see javax.faces.model.DataModel#getWrappedData()
     */
    public Object getWrappedData() {
        return this.records;
    }

    /**
     * @see javax.faces.model.DataModel#setWrappedData(java.lang.Object)
     */
    public void setWrappedData(Object data) {
    }
}