/*
 * Created on May 14, 2005
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

import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * // * This test class verifies the behavior of the
 * <code>ExtensibleDataModel</code> class.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/framework/data/ExtensibleDataModelTest.java#1 $
 */

public class ExtensibleDataModelTest extends BaseDestinyTestCase {

    /**
     * This test verifies the basics of the class
     */
    public void testExtensibleDataModelClassBasics() {
        ExtensibleDataModel dm = new ExtensibleDataModel();
        assertTrue("Extensible data model should extends the JSF data model", dm instanceof DataModel);
    }

    /**
     * This test verifies the various properties of the class
     */
    public void testExtensibleDataModelSettersAndGetters() {
        ExtensibleDataModel dm = new ExtensibleDataModel();
        final int rowIndex = 25;
        dm.setRowIndex(rowIndex);
        assertEquals("Row index property should match", rowIndex, dm.getRowIndex());
        final int totalrc = 30;
        dm.setTotalRowCount(totalrc);
        assertEquals("Row count property should match", totalrc, dm.getRowCount());
        assertNotNull("By default, an empty wrapped data should be set", dm.getWrappedData());
        Object data = dm.getWrappedData();
        dm.setWrappedData(new Object());
        assertEquals("no wrapped data can be set with the setter API", data, dm.getWrappedData());
    }

    /**
     * This test verifies the initial state of the data model
     */
    public void testExtensibleDataModelInitialState() {
        ExtensibleDataModel dm = new ExtensibleDataModel();
        assertEquals("Initially, row count is 0", 0, dm.getRealNbOfRows());
        assertEquals("Initially, row count is 0", 0, dm.getRowCount());
        assertEquals("Initially, row index is 0", 0, dm.getRowIndex());
        Object wd = dm.getWrappedData();
        assertTrue("Initially, the wrapped data is an empty collection", wd instanceof Collection);
        assertEquals("Initially, the wrapped data is an empty collection", 0, ((Collection) wd).size());
        dm.setRowIndex(0);
        assertFalse("Initially no rows should be available", dm.isRowAvailable());
    }

    /**
     * This test verifies that APIs behave properly as the data model grows.
     */
    public void testExtensibleDataModelExtension() {
        ExtensibleDataModel dm = new ExtensibleDataModel();
        List newRecs = new ArrayList();
        final Object rec1 = new Object();
        final Object rec2 = new Object();
        final Object rec3 = new Object();
        newRecs.add(rec1);
        newRecs.add(rec2);
        newRecs.add(rec3);
        dm.addNewRecords(newRecs);
        final int totalRowCount = 30;
        dm.setTotalRowCount(totalRowCount);

        assertEquals("Real number of rows should be calculated properly", 3, dm.getRealNbOfRows());
        assertEquals("Total number of rows should be calculated properly", totalRowCount, dm.getRowCount());
        dm.setRowIndex(0);
        assertTrue("Data model should expose the row", dm.isRowAvailable());
        assertEquals("Data model should expose the correct row data", rec1, dm.getRowData());
        dm.setRowIndex(1);
        assertTrue("Data model should expose the row", dm.isRowAvailable());
        assertEquals("Data model should expose the correct row data", rec2, dm.getRowData());
        dm.setRowIndex(2);
        assertTrue("Data model should expose the row", dm.isRowAvailable());
        assertEquals("Data model should expose the correct row data", rec3, dm.getRowData());
        dm.setRowIndex(3);
        assertFalse("Data model should not expose the row", dm.isRowAvailable());

        //Adds some more rows
        List newRecs2 = new ArrayList();
        final Object rec4 = new Object();
        final Object rec5 = new Object();
        newRecs2.add(rec4);
        newRecs2.add(rec5);
        dm.addNewRecords(newRecs2);
        assertTrue("Data model should expose the row", dm.isRowAvailable());
        assertEquals("Data model should expose the correct row data", rec4, dm.getRowData());
        assertEquals("Real number of rows should be calculated properly", 5, dm.getRealNbOfRows());
        assertEquals("Total number of rows should be calculated properly", totalRowCount, dm.getRowCount());
    }
}