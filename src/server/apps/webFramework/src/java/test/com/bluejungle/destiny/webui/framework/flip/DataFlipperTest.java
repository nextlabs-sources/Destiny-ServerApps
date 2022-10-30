/*
 * Created on May 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.flip;

import javax.faces.model.DataModel;

import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * This is the test class for the data flipper implementation.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/framework/flip/DataFlipperTest.java#1 $
 */

public class DataFlipperTest extends BaseDestinyTestCase {

    /**
     * This test verifies the data flipper class basics.
     */
    public void testDataFlipperClassBasics() {
        DataFlipperImpl dataFlipper = new DataFlipperImpl();
        assertTrue("Data flipper should implement the correct interface", dataFlipper instanceof IDataFlipper);
    }

    /**
     * This test verifies that the properties can be set / get properly.
     */
    public void testDataFlipperProperties() {
        DataFlipperImpl dataFlipper = new DataFlipperImpl();
        MockDataModel dm = new MockDataModel();
        assertNotNull("Even if no data model is set, a default column data model should be returned", dataFlipper.getColumnResults());
        dataFlipper.setDataModel(dm);
        assertEquals("The column data model should be returned", dm, dataFlipper.getColumnResults());
        assertNotNull("Even if no row count is set, a row data model should be returned", dataFlipper.getRowResults());
    }

    /**
     * This test verifies that setting the row count prepares the data model
     * properly
     */
    public void testDataFlipperSetRowCount() {
        DataFlipperImpl dataFlipper = new DataFlipperImpl();
        MockDataModel dm = new MockDataModel();
        dataFlipper.setDataModel(dm);
        final int rowCount = 5;
        dataFlipper.setRowCount(new Integer(rowCount));
        DataModel rows = dataFlipper.getRowResults();
        assertNotNull("There should be a row data model returned", rows);
        assertEquals("The size of the row data model should match the row count", rowCount, rows.getRowCount());
        assertEquals("The correct column data model should be returned", dm, dataFlipper.getColumnResults());
    }

    /**
     * This test verifies that setting a null row count for the data flipper is
     * supported.
     *  
     */
    public void testDataFlipperSetNullRowCount() {
        DataFlipperImpl dataFlipper = new DataFlipperImpl();
        MockDataModel dm = new MockDataModel();
        dataFlipper.setDataModel(dm);
        dataFlipper.setRowCount(null);
        DataModel rows = dataFlipper.getRowResults();
        assertNotNull("There should be a row data model returned even though no row count is set", rows);
        assertEquals("The size of the row data model should be 0 when no row count is set", 0, rows.getRowCount());
        assertEquals("The correct column data model should be returned", dm, dataFlipper.getColumnResults());
    }

    /**
     * This is a dummy data model object for the test cases.
     * 
     * @author ihanen
     */
    private class MockDataModel extends DataModel {

        /**
         * @see javax.faces.model.DataModel#isRowAvailable()
         */
        public boolean isRowAvailable() {
            return false;
        }

        /**
         * @see javax.faces.model.DataModel#getRowCount()
         */
        public int getRowCount() {
            return 0;
        }

        /**
         * @see javax.faces.model.DataModel#getRowData()
         */
        public Object getRowData() {
            return null;
        }

        /**
         * @see javax.faces.model.DataModel#getRowIndex()
         */
        public int getRowIndex() {
            return 0;
        }

        /**
         * @see javax.faces.model.DataModel#setRowIndex(int)
         */
        public void setRowIndex(int rowIndex) {
        }

        /**
         * @see javax.faces.model.DataModel#getWrappedData()
         */
        public Object getWrappedData() {
            return null;
        }

        /**
         * @see javax.faces.model.DataModel#setWrappedData(java.lang.Object)
         */
        public void setWrappedData(Object data) {
        }

    }
}