/*
 * Created on May 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.user;

import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.faces.model.DataModel;

import junit.swingui.TestRunner;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.MockApplication;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.MockReportComponentQueryBroker;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentSelectedItem;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.UserComponentEntityResolver;
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
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/user/TestSelectableUserComponentItemSource.java#2 $
 */

public class TestSelectableUserComponentItemSource extends BaseJSFTest {

    /*
     * Private variables:
     */
    protected MockSelectableUserComponentItemSourceImpl userComponentSourceToTest;
    protected MockReportComponentQueryBroker mockReportComponentQueryBrokerForTest;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        this.facesContext = new MockFacesContext();
        this.facesContext.setApplication(new MockApplication());
        // Setup the report component query broker to be used:
        this.userComponentSourceToTest = new MockSelectableUserComponentItemSourceImpl();
        this.userComponentSourceToTest.setCurrentReportBinding("DoesNotMatter");
        this.userComponentSourceToTest.setReportComponentQueryBroker(new MockReportComponentQueryBroker());
        this.mockReportComponentQueryBrokerForTest = (MockReportComponentQueryBroker) this.userComponentSourceToTest.getReportComponentQueryBroker();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() {
        super.tearDown();
    }

    /**
     * Constructor for TestSelectableUserComponentItemSource.
     * 
     * @param arg0
     */
    public TestSelectableUserComponentItemSource(String arg0) {
        super(arg0);
    }

    /*
     * This test just makes sure that a data model is returned containing the
     * elements that are expected to be returned. It does not check the disabled
     * items. That will be covered by a separate test plan.
     *  
     */
    public void testGetSelectableItemsForSearchBucketSearchSpec() throws Exception {
    	Locale locale = Locale.US;
    	
        // FIX ME - Once maximum results are implementd on back end, add unit test for different values
        ISearchBucketSearchSpec searchBucketForTest = (new SearchBucketImpl(new Character[] { new Character('A'), new Character('B') }, locale)).getSeachSpec(10);

        // Test that the data model returned is correct:
        DataModel selectableItemsToTest = this.userComponentSourceToTest.getSelectableItems(searchBucketForTest, null);
        assertEquals("Number of selectable items from search bucket search should match those returned", 
                      selectableItemsToTest.getRowCount(), 
                      this.mockReportComponentQueryBrokerForTest.getUserComponentCount());
                // The search by group has been disabled (i.e. commented out) 
                // hence this part of the test is also commented out
                //+ this.mockReportComponentQueryBrokerForTest.getUserClassComponentCount());

        // Test the contents of the data model:
        ISelectedItem userItemToSelect = null;
        ISelectedItem userClassItemToSelect = null;
        for (int i = 0; i < selectableItemsToTest.getRowCount(); i++) {
            selectableItemsToTest.setRowIndex(i);
            Object dataObject = selectableItemsToTest.getRowData();
            assertTrue("Dataobject should be of disableable type", dataObject instanceof IDisableableSelectableItem);
            assertTrue("Dataobject should be of right type", dataObject instanceof SelectableUserItem || dataObject instanceof SelectableUserClassItem);

            // If this is a user:
            if (dataObject instanceof SelectableUserItem) {
                SelectableUserItem user = (SelectableUserItem) dataObject;
                assertNotNull("Selectable user must be real", this.mockReportComponentQueryBrokerForTest.getUserMap().get(user.getId()));
                assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) user).isSelectable());

                // Get hold of one item that is to be set:
                if (userItemToSelect == null) {
                    userItemToSelect = user.createSelected();
                }
            } else if (dataObject instanceof SelectableUserClassItem) {
                SelectableUserClassItem userClass = (SelectableUserClassItem) dataObject;
                assertNotNull("Selectable user class must be real", this.mockReportComponentQueryBrokerForTest.getUserClassMap().get(userClass.getId()));
                assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) userClass).isSelectable());

                // Get hold of one item that is to be set:
                if (userClassItemToSelect == null) {
                    userClassItemToSelect = userClass.createSelected();
                }
            }
        }

        // Now use the 'selected' items above:
        SelectedItemListImpl selectedItemList = new SelectedItemListImpl();
        selectedItemList.addAll(Collections.singleton(userItemToSelect));
        if (userClassItemToSelect != null) {
            selectedItemList.addAll(Collections.singleton(userClassItemToSelect));
        }

        selectableItemsToTest = this.userComponentSourceToTest.getSelectableItems(searchBucketForTest, selectedItemList);
        assertEquals("Number of selectable items from search bucket search should match those returned",
                      selectableItemsToTest.getRowCount(),
                      this.mockReportComponentQueryBrokerForTest.getUserComponentCount());
            // The search by group has been disabled (i.e. commented out) 
            // hence this part of the test is also commented out
            //    + this.mockReportComponentQueryBrokerForTest.getUserClassComponentCount());

        // Test the contents of the data model:
        for (int i = 0; i < selectableItemsToTest.getRowCount(); i++) {
            selectableItemsToTest.setRowIndex(i);
            Object dataObject = selectableItemsToTest.getRowData();
            assertTrue("Dataobject should be of disableable type", dataObject instanceof IDisableableSelectableItem);
            assertTrue("Dataobject should be of right type", dataObject instanceof SelectableUserItem || dataObject instanceof SelectableUserClassItem);

            if (dataObject instanceof SelectableUserItem) {
                SelectableUserItem user = (SelectableUserItem) dataObject;
                assertNotNull("Selectable user must be real", this.mockReportComponentQueryBrokerForTest.getUserMap().get(user.getId()));

                // Disabled items should not be selectable, enabled items should
                // be selectable:
                if (userItemToSelect.getId().equals(user.getId())) {
                    assertFalse("Already selected item should be marked as unselectable", ((IDisableableSelectableItem) user).isSelectable());
                } else {
                    assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) user).isSelectable());
                }
            } else if (dataObject instanceof SelectableUserClassItem) {
                SelectableUserClassItem userClass = (SelectableUserClassItem) dataObject;
                assertNotNull("Selectable user class must be real", this.mockReportComponentQueryBrokerForTest.getUserClassMap().get(userClass.getId()));

                // Disabled items should not be selectable, enabled items should
                // be selectable:
                if (userClassItemToSelect.getId().equals(userClass.getId())) {
                    assertFalse("Already selected item should be marked as unselectable", ((IDisableableSelectableItem) userClass).isSelectable());
                } else {
                    assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) userClass).isSelectable());
                }
            }
        }
    }

    /*
     * Test for DataModel getSelectableItems(IFreeFormSearchSpec,
     * ISelectedItemList)
     */
    public void testGetSelectableItemsForFreeFormSearchSpec() throws Exception {
        // FIX ME - Test with different values of maximum results when implemented on back end
        IFreeFormSearchSpec freeFormSearchSpecForTest = new FreeFormSearchSpecImpl("", 10);

        // Test that the data model returned is correct:
        DataModel selectableItemsToTest = this.userComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, null);
        assertEquals("Number of selectable items from search bucket search should match those returned", 
                    selectableItemsToTest.getRowCount(), 
                    this.mockReportComponentQueryBrokerForTest.getUserComponentCount());
            // The search by group has been disabled (i.e. commented out) 
            // hence this part of the test is also commented out
            //    + this.mockReportComponentQueryBrokerForTest.getUserClassComponentCount());

        // Test the contents of the data model:
        ISelectedItem userItemToSelect = null;
        ISelectedItem userClassItemToSelect = null;
        for (int i = 0; i < selectableItemsToTest.getRowCount(); i++) {
            selectableItemsToTest.setRowIndex(i);
            Object dataObject = selectableItemsToTest.getRowData();
            assertTrue("Dataobject should be of disableable type", dataObject instanceof IDisableableSelectableItem);
            assertTrue("Dataobject should be of right type", dataObject instanceof SelectableUserItem || dataObject instanceof SelectableUserClassItem);

            if (dataObject instanceof SelectableUserItem) {
                SelectableUserItem user = (SelectableUserItem) dataObject;
                assertNotNull("Selectable user must be real", this.mockReportComponentQueryBrokerForTest.getUserMap().get(user.getId()));
                assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) user).isSelectable());

                // Get hold of one item that is to be set:
                if (userItemToSelect == null) {
                    userItemToSelect = user.createSelected();
                }
            } else if (dataObject instanceof SelectableUserClassItem) {
                SelectableUserClassItem userClass = (SelectableUserClassItem) dataObject;
                assertNotNull("Selectable user class must be real", this.mockReportComponentQueryBrokerForTest.getUserClassMap().get(userClass.getId()));
                assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) userClass).isSelectable());

                // Get hold of one item that is to be set:
                if (userClassItemToSelect == null) {
                    userClassItemToSelect = userClass.createSelected();
                }
            }
        }

        // Now use the 'selected' items above:
        SelectedItemListImpl selectedItemList = new SelectedItemListImpl();
        selectedItemList.addAll(Collections.singleton(userItemToSelect));
        if (userClassItemToSelect != null) {
            selectedItemList.addAll(Collections.singleton(userClassItemToSelect));
        }

        selectableItemsToTest = this.userComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, selectedItemList);
        assertEquals("Number of selectable items from search bucket search should match those returned",
                selectableItemsToTest.getRowCount(), 
                this.mockReportComponentQueryBrokerForTest.getUserComponentCount());
             // The search by group has been disabled (i.e. commented out) 
            // hence this part of the test is also commented out
            //+ this.mockReportComponentQueryBrokerForTest.getUserClassComponentCount());

        // Test the contents of the data model:
        for (int i = 0; i < selectableItemsToTest.getRowCount(); i++) {
            selectableItemsToTest.setRowIndex(i);
            Object dataObject = selectableItemsToTest.getRowData();
            assertTrue("Dataobject should be of disableable type", dataObject instanceof IDisableableSelectableItem);
            assertTrue("Dataobject should be of right type", dataObject instanceof SelectableUserItem || dataObject instanceof SelectableUserClassItem);

            if (dataObject instanceof SelectableUserItem) {
                SelectableUserItem user = (SelectableUserItem) dataObject;
                assertNotNull("Selectable user must be real", this.mockReportComponentQueryBrokerForTest.getUserMap().get(user.getId()));

                // Disabled items should not be selectable, enabled items should
                // be selectable:
                if (userItemToSelect.getId().equals(user.getId())) {
                    assertFalse("Already selected item should be marked as unselectable", ((IDisableableSelectableItem) user).isSelectable());
                } else {
                    assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) user).isSelectable());
                }
            } else if (dataObject instanceof SelectableUserClassItem) {
                SelectableUserClassItem userClass = (SelectableUserClassItem) dataObject;
                assertNotNull("Selectable user must be real", this.mockReportComponentQueryBrokerForTest.getUserClassMap().get(userClass.getId()));

                // Disabled items should not be selectable, enabled items should
                // be selectable:
                if (userClassItemToSelect.getId().equals(userClass.getId())) {
                    assertFalse("Already selected item should be marked as unselectable", ((IDisableableSelectableItem) userClass).isSelectable());
                } else {
                    assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) userClass).isSelectable());
                }
            }

        }
    }

    public void testGenerateSelectedItems() throws Exception {
        String userItemID = UserComponentEntityResolver.createUserQualification(MockReportComponentQueryBroker.TEST_USER_NAME);
        String userClassItemID = UserComponentEntityResolver.createUserClassQualification(MockReportComponentQueryBroker.TEST_USER_CLASS_NAME);

        // FIX ME - Test with different values of maximum results when implemented on back end
        IFreeFormSearchSpec freeFormSearchSpecForTest = new FreeFormSearchSpecImpl("", 10);
        DataModel selectableItemsToTest = this.userComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, null);

        // Users:
        this.userComponentSourceToTest.warmUpDataModelCache(selectableItemsToTest);
        Set selectedUserItems = this.userComponentSourceToTest.generateSelectedItems(userItemID);
        assertTrue("Selected items should exist", ((selectedUserItems == null) || (selectedUserItems.size() == 1)));
        Iterator userIter = selectedUserItems.iterator();
        Object userItem = userIter.next();
        assertTrue("Selected item should be of right type", userItem instanceof ReportComponentSelectedItem);
        ReportComponentSelectedItem selectedUserItem = (ReportComponentSelectedItem) userItem;
        assertEquals("Selected item ids should match", selectedUserItem.getId(), userItemID);

        // User classes:
        // The search by group has been disabled (i.e. commented out) 
        // hence this part of the test is also commented out
        /*
        this.userComponentSourceToTest.warmUpDataModelCache(selectableItemsToTest);
        Set selectedUserClassItems = this.userComponentSourceToTest.generateSelectedItems(userClassItemID);
        assertTrue("Selected items should exist", ((selectedUserClassItems == null) || (selectedUserClassItems.size() == 1)));
        Iterator userClassIter = selectedUserClassItems.iterator();
        Object userClassItem = userClassIter.next();
        assertTrue("Selected item should be of right type", userClassItem instanceof ReportComponentSelectedItem);
        ReportComponentSelectedItem selectedUserClassItem = (ReportComponentSelectedItem) userClassItem;
        assertEquals("Selected item ids should match", selectedUserClassItem.getId(), userClassItemID);
        */
        // Now check to see if the user and user-class elements are disabled in
        // the next call to getSelectableItems:
        SelectedItemListImpl selectedItemList = new SelectedItemListImpl();
        selectedItemList.addAll(selectedUserItems);
        //    selectedItemList.addAll(selectedUserClassItems);

        selectableItemsToTest = this.userComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, selectedItemList);
        assertEquals("Number of selectable items from search bucket search should match those returned",
                      selectableItemsToTest.getRowCount(), 
                      this.mockReportComponentQueryBrokerForTest.getUserComponentCount());
        // The search by group has been disabled (i.e. commented out) 
        // hence this part of the test is also commented out
        //        + this.mockReportComponentQueryBrokerForTest.getUserClassComponentCount());

        // Test the contents of the data model:
        for (int i = 0; i < selectableItemsToTest.getRowCount(); i++) {
            selectableItemsToTest.setRowIndex(i);
            Object dataObject = selectableItemsToTest.getRowData();
            assertTrue("Dataobject should be of disableable type", dataObject instanceof IDisableableSelectableItem);
            assertTrue("Dataobject should be of right type", dataObject instanceof SelectableUserItem || dataObject instanceof SelectableUserClassItem);

            if (dataObject instanceof SelectableUserItem) {
                SelectableUserItem user = (SelectableUserItem) dataObject;
                assertNotNull("Selectable user must be real", this.mockReportComponentQueryBrokerForTest.getUserMap().get(user.getId()));

                // Disabled items should not be selectable, enabled items should
                // be
                // selectable:
                if (selectedUserItem.getId().equals(user.getId())) {
                    assertFalse("Already selected item should be marked as unselectable", ((IDisableableSelectableItem) user).isSelectable());
                } else {
                    assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) user).isSelectable());
                }
            }
            //     The search by group has been disabled (i.e. commented out) 
            // hence this part of the test is also commented out
            /*else if (dataObject instanceof SelectableUserClassItem) {
                SelectableUserClassItem userClass = (SelectableUserClassItem) dataObject;
                assertNotNull("Selectable user class must be real", this.mockReportComponentQueryBrokerForTest.getUserClassMap().get(userClass.getId()));

                // Disabled items should not be selectable, enabled items should
                // be
                // selectable:                
                if (selectedUserClassItem.getId().equals(userClass.getId())) {
                    assertFalse("Already selected item should be marked as unselectable", ((IDisableableSelectableItem) userClass).isSelectable());
                } else {
                    assertTrue("Previously unselected item should be marked as selectable", ((IDisableableSelectableItem) userClass).isSelectable());
                }
            }*/

        }
    }

    /**
     * Tests that when the selected items are stored, they are reflected on the
     * backing report as well
     * 
     * @throws Exception
     */
    public void testStoreSelectedItems() throws Exception {
        String userItemID = UserComponentEntityResolver.createUserQualification(MockReportComponentQueryBroker.TEST_USER_NAME);
        String userItemDisplay = UserComponentEntityResolver.createUserQualification(MockReportComponentQueryBroker.TEST_USER_NAME);
        String userClassItemID = UserComponentEntityResolver.createUserClassQualification(MockReportComponentQueryBroker.TEST_USER_CLASS_NAME);
        String userClassItemDisplay = UserComponentEntityResolver.createUserClassQualification(MockReportComponentQueryBroker.TEST_USER_CLASS_NAME);

        // FIX ME - Test with different values of maximum results when implemented on back end
        IFreeFormSearchSpec freeFormSearchSpecForTest = new FreeFormSearchSpecImpl("", 10);
        DataModel selectableItemsToTest = this.userComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, null);
        Set selecteUserItems = this.userComponentSourceToTest.generateSelectedItems(userItemID);
        //      The search by group has been disabled (i.e. commented out) 
        // hence this part of the test is also commented out
        //Set selectedUserClassItems = this.userComponentSourceToTest.generateSelectedItems(userClassItemID);

        SelectedItemListImpl selectedItemList = new SelectedItemListImpl();
        selectedItemList.addAll(selecteUserItems);
        //selectedItemList.addAll(selectedUserClassItems);
        this.userComponentSourceToTest.storeSelectedItems(selectedItemList);

        //Now check that the selected items have been set on the report:
        IReport report = this.userComponentSourceToTest.getCurrentReport();
        String userExpr = report.getUsers();
        assertNotNull("User expression returned from report should not be null", userExpr);
        userExpr = userExpr.trim();
        String expectedUserExpr = ", " + userItemDisplay /*+ ", " + userClassItemDisplay*/;
        assertEquals("User expression returned from report should match expected", expectedUserExpr, userExpr);
    }

    /**
     * Tests to make sure that if cancelData selection is called (without
     * calling storeSelectedItems), the backing report does not reflect any
     * change.
     * 
     * @throws Exception
     */
    public void testCancelDataSelection() throws Exception {
        String userItemID = UserComponentEntityResolver.createUserQualification(MockReportComponentQueryBroker.TEST_USER_NAME);
        String userItemDisplay = UserComponentEntityResolver.createUserQualification(MockReportComponentQueryBroker.TEST_USER_NAME);
        String userClassItemID = UserComponentEntityResolver.createUserClassQualification(MockReportComponentQueryBroker.TEST_USER_CLASS_NAME);
        String userClassItemDisplay = UserComponentEntityResolver.createUserClassQualification(MockReportComponentQueryBroker.TEST_USER_CLASS_NAME);

        // FIX ME - Test with different values of maximum results when implemented on back end
        IFreeFormSearchSpec freeFormSearchSpecForTest = new FreeFormSearchSpecImpl("", 10);
        DataModel selectableItemsToTest = this.userComponentSourceToTest.getSelectableItems(freeFormSearchSpecForTest, null);
        Set selecteUserItems = this.userComponentSourceToTest.generateSelectedItems(userItemID);
        
        //      The search by group has been disabled (i.e. commented out) 
        // hence this part of the test is also commented out
        //Set selectedUserClassItems = this.userComponentSourceToTest.generateSelectedItems(userClassItemID);

        SelectedItemListImpl selectedItemList = new SelectedItemListImpl();
        selectedItemList.addAll(selecteUserItems);
        //selectedItemList.addAll(selectedUserClassItems);

        this.userComponentSourceToTest.cancelDataSelection();

        // Now check that the selected items have NOT been set on the report:
        //        ReportImpl report =
        // this.userComponentSourceToTest.getCurrentReportBinding();
        //        String userExpr = report.getUsers();
        //        assertNotNull("User expression returned from report should not be
        // null", userExpr);
        //        userExpr = userExpr.trim();
        //        assertEquals("The default user expr should be displayed", userExpr,
        // "Any User");
    }
}