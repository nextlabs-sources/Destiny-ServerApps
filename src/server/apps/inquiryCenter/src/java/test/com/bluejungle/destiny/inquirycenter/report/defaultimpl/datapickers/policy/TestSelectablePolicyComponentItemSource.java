/*
 * Created on May 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.policy;

import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.MockApplication;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.MockReportComponentQueryBroker;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentSelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.IFreeFormSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.FreeFormSearchSpecImpl;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.SearchBucketImpl;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.SelectedItemListImpl;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/policy/TestSelectablePolicyComponentItemSource.java#2 $
 */

public class TestSelectablePolicyComponentItemSource extends BaseJSFTest {

    /*
     * Private variables:
     */
    protected MockSelectablePolicyComponentItemSourceImpl policyComponentSourceToTest;
    protected MockReportComponentQueryBroker mockReportComponentQueryBrokerForTest;

    /**
     * Main:
     * 
     * @param args
     */
    public static void main(String[] args) {
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        this.facesContext = new MockFacesContext();
        this.facesContext.setApplication(new MockApplication());
        // Setup the report component query broker to be used:
        this.policyComponentSourceToTest = new MockSelectablePolicyComponentItemSourceImpl();
        this.policyComponentSourceToTest.setCurrentReportBinding("DoesNotMatter");
        //this.policyComponentSourceToTest.setCurrentReport(wrappedReportForTest);
        this.policyComponentSourceToTest.setReportComponentQueryBroker(new MockReportComponentQueryBroker());
        this.mockReportComponentQueryBrokerForTest = (MockReportComponentQueryBroker) this.policyComponentSourceToTest.getReportComponentQueryBroker();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() {
        super.tearDown();
    }

    /**
     * Constructor for TestSelectablePolicyComponentItemSource.
     * 
     * @param arg0
     */
    public TestSelectablePolicyComponentItemSource(String arg0) {
        super(arg0);
    }

    /*
     * This test just makes sure that a data model is returned containing the
     * elements that are expected to be returned. It does not check the disabled
     * items. That will be covered by a separate test plan.
     *  
     */
    public void testGetSelectableItemsISearchBucketSearchSpec() throws Exception {
    	Locale locale = Locale.US;
    	
        // FIX ME - Test with different values of maximum results when implemented on back end
        ISearchBucketSearchSpec searchBucketForTest = (new SearchBucketImpl(new Character[] { new Character('A'), new Character('B') }, locale)).getSeachSpec(0);

        // Test that the data model returned is correct:
        DataModel selectablePolicyItemsToTest = this.policyComponentSourceToTest.getSelectableItems(searchBucketForTest, null);
        assertEquals("Number of selectable items from search bucket search should match those returned", selectablePolicyItemsToTest.getRowCount(), this.mockReportComponentQueryBrokerForTest.getPolicyComponentCount());

        // Test the contents of the data model:
        ISelectedItem itemToSelect = null;
        for (int i = 0; i < selectablePolicyItemsToTest.getRowCount(); i++) {
            Object dataObject = selectablePolicyItemsToTest.getRowData();
            assertTrue("Dataobject should be of right type", dataObject instanceof SelectablePolicyItem);
            assertTrue("Dataobject should be of disableable type", dataObject instanceof IDisableableSelectableItem);

            SelectablePolicyItem policy = (SelectablePolicyItem) dataObject;
            assertNotNull("Selectable policy must be real", this.mockReportComponentQueryBrokerForTest.getPolicyComponentMap().get(policy.getId()));
            assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) policy).isSelectable());

            // Get hold of one item that is to be set:
            if (itemToSelect == null) {
                itemToSelect = policy.createSelected();
            }
        }

        // Now use the 'itemToSelect' above as selected:
        SelectedItemListImpl selectedItemList = new SelectedItemListImpl();
        selectedItemList.addAll(Collections.singleton(itemToSelect));
        selectablePolicyItemsToTest = this.policyComponentSourceToTest.getSelectableItems(searchBucketForTest, selectedItemList);
        assertEquals("Number of selectable items from search bucket search should match those returned", selectablePolicyItemsToTest.getRowCount(), this.mockReportComponentQueryBrokerForTest.getPolicyComponentCount());

        // Test the contents of the data model:
        for (int i = 0; i < selectablePolicyItemsToTest.getRowCount(); i++) {
            Object dataObject = selectablePolicyItemsToTest.getRowData();
            assertTrue("Dataobject should be of right type", dataObject instanceof SelectablePolicyItem);
            assertTrue("Dataobject should be of disableable type", dataObject instanceof IDisableableSelectableItem);

            SelectablePolicyItem policy = (SelectablePolicyItem) dataObject;
            assertNotNull("Selectable policy must be real", this.mockReportComponentQueryBrokerForTest.getPolicyComponentMap().get(policy.getId()));

            // Disabled items should not be selectable, enabled items should be
            // selectable:
            if (itemToSelect.getId().equals(policy.getId())) {
                assertFalse("Already selected item should be marked as unselectable", ((IDisableableSelectableItem) policy).isSelectable());
            } else {
                assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) policy).isSelectable());
            }
        }
    }

    /*
     * Test for DataModel getSelectableItems(IFreeFormSearchSpec,
     * ISelectedItemList)
     */
    public void testGetSelectableItemsIFreeFormSearchSpec() throws Exception {
        
        // FIX ME - Test with different values of maximum results when implemented on back end
        IFreeFormSearchSpec freeFormSearchSpecForTest = new FreeFormSearchSpecImpl("", 0);

        // Test that the data model returned is correct:
        DataModel selectablePolicyItemsToTest = this.policyComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, null);
        assertEquals("Number of selectable items from search bucket search should match those returned", selectablePolicyItemsToTest.getRowCount(), this.mockReportComponentQueryBrokerForTest.getPolicyComponentCount());

        // Test the contents of the data model:
        ISelectedItem itemToSelect = null;
        for (int i = 0; i < selectablePolicyItemsToTest.getRowCount(); i++) {
            Object dataObject = selectablePolicyItemsToTest.getRowData();
            assertTrue("Dataobject should be of right type", dataObject instanceof SelectablePolicyItem);
            assertTrue("Dataobject should be of disableable type", dataObject instanceof IDisableableSelectableItem);

            SelectablePolicyItem policy = (SelectablePolicyItem) dataObject;
            assertNotNull("Selectable policy must be real", this.mockReportComponentQueryBrokerForTest.getPolicyComponentMap().get(policy.getId()));
            assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) policy).isSelectable());

            // Get hold of one item that is to be set:
            if (itemToSelect == null) {
                itemToSelect = policy.createSelected();
            }
        }

        // Now use the 'itemToSelect' above as selected:
        SelectedItemListImpl selectedItemList = new SelectedItemListImpl();
        selectedItemList.addAll(Collections.singleton(itemToSelect));
        selectablePolicyItemsToTest = this.policyComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, selectedItemList);
        assertEquals("Number of selectable items from search bucket search should match those returned", selectablePolicyItemsToTest.getRowCount(), this.mockReportComponentQueryBrokerForTest.getPolicyComponentCount());

        // Test the contents of the data model:
        for (int i = 0; i < selectablePolicyItemsToTest.getRowCount(); i++) {
            Object dataObject = selectablePolicyItemsToTest.getRowData();
            assertTrue("Dataobject should be of right type", dataObject instanceof SelectablePolicyItem);
            assertTrue("Dataobject should be of disableable type", dataObject instanceof IDisableableSelectableItem);

            SelectablePolicyItem policy = (SelectablePolicyItem) dataObject;
            assertNotNull("Selectable policy must be real", this.mockReportComponentQueryBrokerForTest.getPolicyComponentMap().get(policy.getId()));

            // Disabled items should not be selectable, enabled items should be
            // selectable:
            if (itemToSelect.getId().equals(policy.getId())) {
                assertFalse("Already selected item should be marked as unselectable", ((IDisableableSelectableItem) policy).isSelectable());
            } else {
                assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) policy).isSelectable());
            }
        }
    }

    public void testGenerateSelectedItems() throws Exception {
        String itemID = MockReportComponentQueryBroker.TEST_FULL_POLICY_NAME;
        
        // FIX ME - Test with different values of maximum results when implemented on back end
        IFreeFormSearchSpec freeFormSearchSpecForTest = new FreeFormSearchSpecImpl("", 0);
        DataModel selectablePolicyItemsToTest = this.policyComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, null);
        Set selectedItems = this.policyComponentSourceToTest.generateSelectedItems(itemID);

        assertTrue("Selected items should exist", ((selectedItems == null) || (selectedItems.size() == 1)));
        Iterator iter = selectedItems.iterator();
        Object item = iter.next();
        assertTrue("Selected item should be of right type", item instanceof ReportComponentSelectedItem);
        ReportComponentSelectedItem selectedItem = (ReportComponentSelectedItem) item;
        assertEquals("Selected item ids should match", selectedItem.getId(), itemID);
        assertEquals("Selected item display should match", selectedItem.getDisplayValue(), MockReportComponentQueryBroker.TEST_FULL_POLICY_NAME);

        // Now check to see if this element is disabled in the next call to
        // getSelectableItems:
        SelectedItemListImpl selectedItemList = new SelectedItemListImpl();
        selectedItemList.addAll(selectedItems);
        selectablePolicyItemsToTest = this.policyComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, selectedItemList);
        assertEquals("Number of selectable items from search bucket search should match those returned", selectablePolicyItemsToTest.getRowCount(), this.mockReportComponentQueryBrokerForTest.getPolicyComponentCount());

        // Test the contents of the data model:
        for (int i = 0; i < selectablePolicyItemsToTest.getRowCount(); i++) {
            Object dataObject = selectablePolicyItemsToTest.getRowData();
            assertTrue("Dataobject should be of right type", dataObject instanceof SelectablePolicyItem);
            assertTrue("Dataobject should be of disableable type", dataObject instanceof IDisableableSelectableItem);

            SelectablePolicyItem policy = (SelectablePolicyItem) dataObject;
            assertNotNull("Selectable policy must be real", this.mockReportComponentQueryBrokerForTest.getPolicyComponentMap().get(policy.getId()));

            // Disabled items should not be selectable, enabled items should be
            // selectable:
            if (selectedItem.getId().equals(policy.getId())) {
                assertFalse("Already selected item should be marked as unselectable", ((IDisableableSelectableItem) policy).isSelectable());
            } else {
                assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) policy).isSelectable());
            }
        }
    }

    /**
     * Tests that when the selected items are stored, they are reflected on the
     * backing report as well
     * 
     * @throws Exception
     */
    public void testStoreSelectedItems() throws Exception {
        String itemID = MockReportComponentQueryBroker.TEST_FULL_POLICY_NAME;
        String itemDisplay = ", " + MockReportComponentQueryBroker.TEST_FULL_POLICY_NAME;
        // FIX ME - Test with different values of maximum results when implemented on back end
        IFreeFormSearchSpec freeFormSearchSpecForTest = new FreeFormSearchSpecImpl("", 0);
        DataModel selectablePolicyItemsToTest = this.policyComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, null);
        Set selectedItems = this.policyComponentSourceToTest.generateSelectedItems(itemID);
        SelectedItemListImpl selectedItemList = new SelectedItemListImpl();
        selectedItemList.addAll(selectedItems);
        this.policyComponentSourceToTest.storeSelectedItems(selectedItemList);

        // Now check that the selected items have been set on the report:
        IReport report = this.policyComponentSourceToTest.getCurrentReport();
        String policyExpr = report.getPolicies();
        assertNotNull("Policy expression returned from report should not be null", policyExpr);
        policyExpr = policyExpr.trim();
        assertEquals("Poicy expression returned from report should match expected", itemDisplay, policyExpr);
    }

    /**
     * Tests to make sure that if cancelData selection is called (without
     * calling storeSelectedItems), the backing report does not reflect any
     * change.
     * 
     * @throws Exception
     */
    public void testCancelDataSelection() throws Exception {
        String itemID = MockReportComponentQueryBroker.TEST_FULL_POLICY_NAME;
        String itemDisplay = MockReportComponentQueryBroker.TEST_POLICY_NAME;
        // FIX ME - Test with different values of maximum results when implemented on back end
        IFreeFormSearchSpec freeFormSearchSpecForTest = new FreeFormSearchSpecImpl("", 0);
        DataModel selectablePolicyItemsToTest = this.policyComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, null);
        Set selectedItems = this.policyComponentSourceToTest.generateSelectedItems(itemID);
        this.policyComponentSourceToTest.cancelDataSelection();

        // Now check that the selected items have been set on the report:
        IReport report = this.policyComponentSourceToTest.getCurrentReport();
        String policyExpr = report.getPolicies();
        if (policyExpr != null) {
            policyExpr = policyExpr.trim();
            assertTrue("Poicy expression returned from report should not contain the test policy", (policyExpr.indexOf(itemDisplay) < 0));
        }
    }
}