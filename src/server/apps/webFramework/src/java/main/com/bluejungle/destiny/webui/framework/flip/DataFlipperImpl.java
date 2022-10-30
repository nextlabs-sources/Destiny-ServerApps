/*
 * Created on May 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.flip;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * This is the data flipper implementation class. The data flipper job is to
 * "flip" the data from rows to columns, so that iteration can be done in a
 * different order. The data flipper walks through the data model objects given
 * in the <code>setDataModel</code> function and prepares a list of flipped
 * data that can be retrieved through the <code>getResults</code> function.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/flip/DataFlipperImpl.java#1 $
 */

public class DataFlipperImpl implements IDataFlipper {

    /**
     * Dummy object used to populate the row data model
     */
    private static final Object DUMMY_OBJECT = new Object();

    /**
     * Default data models to use to avoid null data models being returned
     */
    private static final DataModel DEFAULT_DM = new ListDataModel();
    private static final DataModel DEFAULT_ROW_DM = new ListDataModel();

    private DataModel realDataModel = DEFAULT_DM;
    private DataModel rowDataModel = DEFAULT_ROW_DM;

    /**
     * @see com.bluejungle.destiny.webui.framework.flip.IDataFlipper#setDataModel(javax.faces.model.DataModel)
     */
    public void setDataModel(DataModel newDataModel) {
        this.realDataModel = newDataModel;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.flip.IDataFlipper#getColumnResults()
     */
    public DataModel getColumnResults() {
        return this.realDataModel;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.flip.IDataFlipper#getRowResults()
     */
    public DataModel getRowResults() {
        return this.rowDataModel;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.flip.IDataFlipper#setRowCount(java.lang.Integer)
     */
    public void setRowCount(Integer newRowCount) {
        List dummyList = new ArrayList();
        if (newRowCount == null) {
            this.rowDataModel = new ListDataModel(dummyList);
        } else {
            int size = newRowCount.intValue();
            for (int i = 0; i < size; i++) {
                dummyList.add(DUMMY_OBJECT);
            }
            this.rowDataModel = new ListDataModel(dummyList);
        }
    }
}