/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.data;

import javax.faces.model.DataModel;

/**
 * Linking Data Model links two wrapped DataModel instances in sequential order.
 * Note that the wrapped data models must provide an accurate row count for this
 * class to function properly
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/data/LinkingDataModel.java#1 $
 */

public class LinkingDataModel extends DataModel {

    private DataModel firstDataModel;
    private DataModel secondDataModel;
    int totalRowCount;
    int currentIndex = 0;

    /**
     * Create an instance of LinkingDataModel
     * 
     * @param firstDataModel
     *            the data model to display first
     * @param secondDataModel
     *            the data model to display second
     */
    public LinkingDataModel(DataModel firstDataModel, DataModel secondDataModel) {
        this.firstDataModel = firstDataModel;
        this.secondDataModel = secondDataModel;
        final int count1 = this.firstDataModel.getRowCount();
        final int count2 = this.secondDataModel.getRowCount();
        if (count1 == -1 || count2 == -1) {
            this.totalRowCount = -1;
        } else {
            this.totalRowCount = count1 + count2;
        }
    }

    /**
     * @see javax.faces.model.DataModel#isRowAvailable()
     */
    public boolean isRowAvailable() {
        boolean valueToReturn = false;
        if (isRowInFirstDataModel()) {
            valueToReturn = this.firstDataModel.isRowAvailable();
        } else {
            valueToReturn = this.secondDataModel.isRowAvailable();
        }

        return valueToReturn;
    }

    /**
     * @see javax.faces.model.DataModel#getRowCount()
     */
    public int getRowCount() {
        return this.totalRowCount;
    }

    /**
     * @see javax.faces.model.DataModel#getRowData()
     */
    public Object getRowData() {
        Object valueToReturn = null;
        if (isRowInFirstDataModel()) {
            valueToReturn = this.firstDataModel.getRowData();
        } else {
            valueToReturn = this.secondDataModel.getRowData();
        }

        return valueToReturn;
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
        if (isRowInFirstDataModel()) {
            this.firstDataModel.setRowIndex(this.currentIndex);
        } else {
            int secondRowIndex = adjustRowIndexForSecondModel();
            this.secondDataModel.setRowIndex(secondRowIndex);
        }
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
     * Determine if the current row is in the first data model
     * 
     * @return true if in the first data model; false otherwie;
     */
    private boolean isRowInFirstDataModel() {
        return (this.firstDataModel.getRowCount() > this.currentIndex);
    }

    /**
     * Adjust the current row index to select the proper element from the second
     * data modek
     * 
     * @return the adjusted index
     */
    private int adjustRowIndexForSecondModel() {
        return this.currentIndex - this.firstDataModel.getRowCount();
    }
}