/*
 * Created on May 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.sort;

import com.bluejungle.framework.test.BaseDestinyTestCase;

/**
 * This is the sort state manager test class.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/framework/sort/SortStateMgrTest.java#1 $
 */

public class SortStateMgrTest extends BaseDestinyTestCase {

    /**
     * This test verifies the class basics.
     */
    public void testSortStateMgrClassBasics() {
        SortStateMgrImpl sortMgr = new SortStateMgrImpl();
        assertTrue(sortMgr instanceof ISortStateMgr);
        assertNull("By default, no sort field names are assigned", sortMgr.getSortFieldName());
        assertFalse("By default, sort order is descending", sortMgr.isSortAscending());
    }

    /**
     * This test verifies the setters and getters of the class.
     */
    public void testSortStateMgrStateProperties() {
        SortStateMgrImpl sortMgr = new SortStateMgrImpl();
        final boolean sort = true;
        sortMgr.setSortAscending(sort);
        final String fieldName = "foo";
        sortMgr.setSortFieldName(fieldName);
        assertEquals("Sort direction should match", sort, sortMgr.isSortAscending());
        assertEquals("Sort field name should match", fieldName, sortMgr.getSortFieldName());
    }

    /**
     * This test verifies that the sort state manager saves the state properly
     * and returns the right information for the <code>isStateChanged</code>
     * function.
     */
    public void testSortStateMgrSaveState() {
        SortStateMgrImpl sortMgr = new SortStateMgrImpl();
        final boolean sort = true;
        sortMgr.setSortAscending(sort);
        final String fieldName = "foo";
        final String fieldName2 = "foo2";
        sortMgr.setSortFieldName(fieldName);
        sortMgr.saveState();
        assertEquals("Sort direction should match", sort, sortMgr.isSortAscending());
        assertEquals("Sort field name should match", fieldName, sortMgr.getSortFieldName());
        //Sets and resets the sort field name
        sortMgr.setSortFieldName(fieldName2);
        sortMgr.setSortFieldName(fieldName);
        sortMgr.saveState();
        assertFalse("The state should not be changed when a field is set and reset", sortMgr.isSortStateChanged());
        sortMgr.setSortAscending(!sort);
        sortMgr.setSortAscending(sort);
        sortMgr.saveState();
        assertFalse("The state should not be changed when the sort direction is set and reset", sortMgr.isSortStateChanged());

        //Now changes the field name
        sortMgr.setSortFieldName(fieldName2);
        sortMgr.saveState();
        assertTrue("The state should change when the sort field name changed", sortMgr.isSortStateChanged());
        assertEquals("The sort field name should be correct", fieldName2, sortMgr.getSortFieldName());
        assertEquals("The sort direction should be correct", sort, sortMgr.isSortAscending());

        //Change the sort direction
        sortMgr.setSortAscending(!sort);
        sortMgr.saveState();
        assertTrue("The state should change when the sort field name changed", sortMgr.isSortStateChanged());
        assertEquals("The sort field name should be correct", fieldName2, sortMgr.getSortFieldName());
        assertEquals("The sort direction should be correct", !sort, sortMgr.isSortAscending());

        //Tests the null value assignment
        sortMgr.setSortFieldName(null);
        sortMgr.saveState();
        assertTrue("The state should change when the sort field name changed", sortMgr.isSortStateChanged());
        assertNull("The sort field name should be correct", sortMgr.getSortFieldName());
        assertEquals("The sort direction should be correct", !sort, sortMgr.isSortAscending());
    }
}