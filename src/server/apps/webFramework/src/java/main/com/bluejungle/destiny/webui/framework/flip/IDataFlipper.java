/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.flip;

import javax.faces.model.DataModel;

/**
 * The data flipper interface exposes the functionnality of the data flipper.
 * The data flipper takes data and changes rows to columns according to a
 * certain order.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/flip/IDataFlipper.java#1 $
 */

public interface IDataFlipper {

    /**
     * Returns the columns content
     * 
     * @return the columns content
     */
    public DataModel getColumnResults();

    /**
     * Returns the rows content
     * 
     * @return the rows content
     */
    public DataModel getRowResults();

    /**
     * Sets the original data model to use
     * 
     * @param newDataModel
     *            data model to set
     */
    public void setDataModel(DataModel newDataModel);

    /**
     * Sets the number of rows to display
     * 
     * @param newRowCount
     *            new row count
     */
    public void setRowCount(Integer newRowCount);
}