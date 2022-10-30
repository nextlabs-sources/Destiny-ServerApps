/*
 * Created on May 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import junit.framework.TestCase;

import com.bluejungle.destiny.webui.browsabledatapicker.ISearchBucket;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.framework.faces.CommonSelectItemResourceLists;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * JUnit test for DefaultBrowsableDataPickerBean
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/DefaultBrowsableDataPickerBeanTest.java#1 $
 */

public class DefaultBrowsableDataPickerBeanTest extends BaseJSFTest {

    private DefaultBrowsableDataPickerBean beanToTest;
    private ISelectableItem[] searchBucketSelectableItems;
    private ISelectableItem[] freeFormSelectableItems;
    private Map selectedItems = new HashMap();
    private MockSelectableItemSource mockSelectableItemSource;
    private ISearchBucketBean searchBucketBean;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DefaultBrowsableDataPickerBeanTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.searchBucketSelectableItems = new MockItem[1];
        this.searchBucketSelectableItems[0] = new MockItem("idOne", "styleClassOne", "DisplayValueOne", true);
        this.selectedItems.put(this.searchBucketSelectableItems[0].getId(), this.searchBucketSelectableItems[0]);

        this.freeFormSelectableItems = new MockItem[1];
        this.freeFormSelectableItems[0] = new MockItem("idTwo", "styleClassTwo", "DisplayValueTwo", true);
        this.selectedItems.put(this.freeFormSelectableItems[0].getId(), this.freeFormSelectableItems[0]);

        this.mockSelectableItemSource = new MockSelectableItemSource();
        this.beanToTest = new DefaultBrowsableDataPickerBean();
        this.beanToTest.setSelectableItemSource(this.mockSelectableItemSource);
        this.searchBucketBean = new DefaultSearchBucketBean();
        this.beanToTest.setSearchBucketBean(this.searchBucketBean);       
    }

    public void testPrerender() {
        this.beanToTest.prerender();
        assertTrue("Ensure getSelectableItems was invoked", this.mockSelectableItemSource.wasGetSelectableItemsWithSearchBucketInvoked());
        this.beanToTest.prerender();
        assertTrue("Ensure getSelectableItems was invoked second time", this.mockSelectableItemSource.wasGetSelectableItemsWithSearchBucketInvoked());
        this.beanToTest.setFreeFormSearchString("foo");
        this.beanToTest.prerender();
        assertTrue("Ensure getSelectableItems with free form search was invoked", this.mockSelectableItemSource.wasGetSelectableItemsWithFreeFormSearchInvoked());
    }

    public void testGetSetSelectedSearchBucket() {
        this.beanToTest.prerender();
        ISearchBucket selectedSearchBucket = this.beanToTest.getSelectedSearchBucket();
        assertNotNull("Ensure selected search bucket is not null", selectedSearchBucket);
        Character[] expectedCharArray = { new Character('A'), new Character('B') };
        assertTrue("Ensure first search bucket initially set", Arrays.equals(expectedCharArray, ((ISearchBucketExtended) selectedSearchBucket).getSeachSpec(0).getCharactersInBucket()));

        this.beanToTest.setSelectedSearchBucket(4);
        selectedSearchBucket = this.beanToTest.getSelectedSearchBucket();
        assertNotNull("Ensure selected search bucket is not null", selectedSearchBucket);
        expectedCharArray[0] = new Character('I');
        expectedCharArray[1] = new Character('J');
        assertTrue("Ensure 5th search bucket set as expected", Arrays.equals(expectedCharArray, ((ISearchBucketExtended) selectedSearchBucket).getSeachSpec(0).getCharactersInBucket()));

        IllegalArgumentException expectedException = null;
        try {
            this.beanToTest.setSelectedSearchBucket(1000);
        } catch (IllegalArgumentException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure IllegalArgumentException was thrown", expectedException);
    }

    public void testGetSearchBuckets() {
        this.beanToTest.prerender();
        ISearchBucket[] searchBuckets = this.beanToTest.getSearchBuckets();
        assertTrue("Ensure search buckets are as expected", Arrays.equals(this.searchBucketBean.getSearchBuckets(this.mockSelectableItemSource.getItemLocale()), searchBuckets));
    }

    public void testGetSelectableItems() {
        this.beanToTest.prerender();
        DataModel selectableItems = this.beanToTest.getSelectableItems();
        ISelectableItem[] wrappedData = (ISelectableItem[]) selectableItems.getWrappedData();
        assertTrue("Ensure searchBucket selectable item data is as expected", Arrays.equals(this.searchBucketSelectableItems, wrappedData));

        this.beanToTest.setFreeFormSearchString("foo");
        this.beanToTest.prerender();
        selectableItems = this.beanToTest.getSelectableItems();
        wrappedData = (ISelectableItem[]) selectableItems.getWrappedData();
        assertTrue("Ensure free form selectable item data is as expected", Arrays.equals(this.freeFormSelectableItems, wrappedData));
    }

    /**
     * Tests getSelectedItems, selectItem(), and deselectItem
     */
    public void testGetSelectedItemsSelectItemDelectItem() {
        this.beanToTest.prerender();
        DataModel selectedItems = this.beanToTest.getSelectedItems();
        assertEquals("Ensure that selectedItems initially empty", 0, selectedItems.getRowCount());

        // Now, select an item
        this.beanToTest.selectItem(this.searchBucketSelectableItems[0].getId());
        selectedItems = this.beanToTest.getSelectedItems();
        assertEquals("Ensure that selectedItems is now size one after selecting item", 1, selectedItems.getRowCount());
        assertEquals("Ensure that selectedItems contains item selected", this.searchBucketSelectableItems[0], selectedItems.getRowData());

        this.beanToTest.deselectItem(this.searchBucketSelectableItems[0].getId());
        selectedItems = this.beanToTest.getSelectedItems();
        assertEquals("Ensure that selectedItems empty after deselcting item", 0, selectedItems.getRowCount());

    }

    /**
     * Tests cancelFreeFormSearch() and isFreeFormSearch()
     */
    public void testCancelIsFreeFormSearch() {
        this.beanToTest.setFreeFormSearchString("foo");
        this.beanToTest.prerender();
        assertTrue("Ensure that free form search is set", this.beanToTest.isFreeFormSearch());
        assertTrue("Ensure selected items were retrieve for free form search", this.mockSelectableItemSource.wasGetSelectableItemsWithFreeFormSearchInvoked());

        this.beanToTest.cancelFreeFormSearch();
        assertFalse("Ensure that free form search is no longer set", this.beanToTest.isFreeFormSearch());
        assertFalse("Ensure selected items were retrieved for search bucket search", this.mockSelectableItemSource.wasGetSelectableItemsWithFreeFormSearchInvoked());
    }

    public void testGetSetFreeFormSearchString() {
        String freeFormSearchString = "search string";
        this.beanToTest.setFreeFormSearchString(freeFormSearchString);
        assertEquals("Ensure free form search string retrieved as expected", freeFormSearchString, this.beanToTest.getFreeFormSearchString());

        this.beanToTest.cancelFreeFormSearch();
        assertEquals("Ensure emptry string returned when not in free form search mode", "", this.beanToTest.getFreeFormSearchString());
    }

    public void testStoreSelectedItems() {
        this.beanToTest.prerender();
        this.beanToTest.selectItem(this.searchBucketSelectableItems[0].getId());
        this.beanToTest.storeSelectedItems();
        assertTrue("Ensure selectable item source store method was invoked", this.mockSelectableItemSource.wasStoreSelectedItemsInvoked());
        assertEquals("Ensure selected items cleared", 0, this.beanToTest.getSelectedItems().getRowCount());
    }

    public void testCancelDataSelection() {
        this.beanToTest.prerender();
        this.beanToTest.selectItem(this.searchBucketSelectableItems[0].getId());
        this.beanToTest.cancelDataSelection();
        assertTrue("Ensure selectable item source cancel method was invoked", this.mockSelectableItemSource.wasCancelDataSelectionInvoked());
        assertEquals("Ensure selected items cleared", 0, this.beanToTest.getSelectedItems().getRowCount());
    }

    public void testReset() {
        this.beanToTest.prerender();
        this.beanToTest.selectItem(this.searchBucketSelectableItems[0].getId());
        this.beanToTest.setFreeFormSearchString("foo");
        assertTrue("Ensure selected items is not empty", this.beanToTest.getSelectedItems().getRowCount() > 0);
        assertTrue("Ensure free for search is set", this.beanToTest.isFreeFormSearch());

        this.beanToTest.reset();
        this.beanToTest.getSelectedItems();

        assertEquals("Ensure after reset that selected items is empty", 0, this.beanToTest.getSelectedItems().getRowCount());
        assertFalse("Ensure free for search is not set", this.beanToTest.isFreeFormSearch());
        assertTrue("Ensure selectable item source reset method called", this.mockSelectableItemSource.wasResetInvoked());
    }

    public void testSetSelectableItemSource() {
        this.beanToTest.setSelectableItemSource(mockSelectableItemSource);
        this.beanToTest.prerender();
        assertTrue("Ensure selectable item source was called", this.mockSelectableItemSource.wasGetSelectableItemsWithSearchBucketInvoked());

        NullPointerException expectedException = null;
        try {
            this.beanToTest.setSelectableItemSource(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure null pointer was thrown", expectedException);
    }

    public void testGetSetMaxSelectableItemsToDisplay() {
        this.beanToTest.prerender();
        Collection maximumSelectableItemsToDisplayObjects = this.beanToTest.getMaxSelectableItemsToDisplayOptions();
        assertEquals("Ensure maximum items initially set at first option", ((Integer)((SelectItem)maximumSelectableItemsToDisplayObjects.iterator().next()).getValue()).intValue(), this.beanToTest.getMaxSelectableItemsToDisplay());
        
        // Now set it
        int maxToSet = 5999;
        this.beanToTest.setMaxSelectableItemsToDisplay(maxToSet);
        assertEquals("Ensure max selectable items set as expected", maxToSet, this.beanToTest.getMaxSelectableItemsToDisplay());
    }
    
    public void testGetMaxSelectableItemsToDisplayOptions() {
        Locale currenTestLocal = this.facesContext.getViewRoot().getLocale();
        assertEquals("Ensure max selectable items options are as expected", CommonSelectItemResourceLists.MAX_UI_ELEMENT_LIST_SIZE_SELECT_ITEMS.getSelectItemResources(currenTestLocal), this.beanToTest.getMaxSelectableItemsToDisplayOptions());
    }
    
    /**
     * @author sgoldstein
     */
    public class MockSelectableItemSource implements ISelectableItemSource {

        private boolean getSelectableItemsWithSearchBucketInvoked;
        private boolean getSelectableItemsWithFreeFormSearchInvoked;
        private boolean storeSelectedItemsInvoked;
        private boolean cancelDataSelectionInvoked;
        private boolean resetInvoked;

        /**
         * Create an instance of MockSelectableItemSource
         *  
         */
        public MockSelectableItemSource() {
            super();
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec,
         *      com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
         */
        public DataModel getSelectableItems(ISearchBucketSearchSpec searchSpec, ISelectedItemList selectedItems) throws SelectableItemSourceException {
            this.getSelectableItemsWithSearchBucketInvoked = true;
            return new ArrayDataModel(DefaultBrowsableDataPickerBeanTest.this.searchBucketSelectableItems);
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getSelectableItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec,
         *      com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
         */
        public DataModel getSelectableItems(IFreeFormSearchSpec searchSpec, ISelectedItemList selectedItemSet) throws SelectableItemSourceException {
            this.getSelectableItemsWithFreeFormSearchInvoked = true;
            return new ArrayDataModel(DefaultBrowsableDataPickerBeanTest.this.freeFormSelectableItems);
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#generateSelectedItems(java.lang.String)
         */
        public Set generateSelectedItems(String selectableItemId) throws SelectableItemSourceException {
            ISelectedItem selectedItemToReturn = (ISelectedItem) DefaultBrowsableDataPickerBeanTest.this.selectedItems.get(selectableItemId);
            return Collections.singleton(selectedItemToReturn);
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#storeSelectedItems(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList)
         */
        public String storeSelectedItems(ISelectedItemList selectedItems) throws SelectableItemSourceException {
            this.storeSelectedItemsInvoked = true;
            return null;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#cancelDataSelection()
         */
        public String cancelDataSelection() {
            this.cancelDataSelectionInvoked = true;
            return null;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getItemLocale()
         */
        public Locale getItemLocale() {
            return Locale.US;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#reset()
         */
        public void reset() {
            this.resetInvoked = true;
        }

        /**
         * Retrieve the getSelectableItemsWithFreeFormSearchInvoked.
         * 
         * @return the getSelectableItemsWithFreeFormSearchInvoked.
         */
        private boolean wasGetSelectableItemsWithFreeFormSearchInvoked() {
            boolean valueToReturn = this.getSelectableItemsWithFreeFormSearchInvoked;
            this.getSelectableItemsWithFreeFormSearchInvoked = false;
            return valueToReturn;
        }

        /**
         * Retrieve the getSelectableItemsWithSearchBucketInvoked.
         * 
         * @return the getSelectableItemsWithSearchBucketInvoked.
         */
        private boolean wasGetSelectableItemsWithSearchBucketInvoked() {
            boolean valueToReturn = this.getSelectableItemsWithSearchBucketInvoked;
            this.getSelectableItemsWithSearchBucketInvoked = false;
            return valueToReturn;
        }

        /**
         * Retrieve the cancelDataSelectionInvoked.
         * 
         * @return the cancelDataSelectionInvoked.
         */
        private boolean wasCancelDataSelectionInvoked() {
            boolean valueToReturn = this.cancelDataSelectionInvoked;
            this.cancelDataSelectionInvoked = false;
            return valueToReturn;
        }

        /**
         * Retrieve the resetInvoked.
         * 
         * @return the resetInvoked.
         */
        private boolean wasResetInvoked() {
            boolean valueToReturn = this.resetInvoked;
            this.resetInvoked = false;
            return valueToReturn;
        }

        /**
         * Retrieve the storeSelectedItemsInvoked.
         * 
         * @return the storeSelectedItemsInvoked.
         */
        private boolean wasStoreSelectedItemsInvoked() {
            boolean valueToReturn = this.storeSelectedItemsInvoked;
            this.storeSelectedItemsInvoked = false;
            return valueToReturn;
        }
    }

    private class MockItem implements ISelectableItem, ISelectedItem {

        private String id;
        private String styleClassId;
        private String displayValue;
        private boolean isSelectable;

        /**
         * Create an instance of MockItem
         * 
         * @param id
         * @param styleClassId
         * @param displayValue
         * @param isSelectable
         */
        public MockItem(String id, String styleClassId, String displayValue, boolean isSelectable) {
            this.id = id;
            this.styleClassId = styleClassId;
            this.displayValue = displayValue;
            this.isSelectable = isSelectable;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
         */
        public String getId() {
            return this.id;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getStyleClassId()
         */
        public String getStyleClassId() {
            return this.styleClassId;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValue()
         */
        public String getDisplayValue() {
            return this.displayValue;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#isSelectable()
         */
        public boolean isSelectable() {
            return this.isSelectable;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValueToolTip()
         */
        public String getDisplayValueToolTip() {
            return getDisplayValue();
        }
    }
}