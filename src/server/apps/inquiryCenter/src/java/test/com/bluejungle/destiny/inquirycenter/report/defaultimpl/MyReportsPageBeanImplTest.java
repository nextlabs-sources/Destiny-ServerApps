/*
 * Created on Jun 27, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.bluejungle.destiny.inquirycenter.report.IReport;
//import com.bluejungle.destiny.inquirycenter.report.ISavedReport;
import com.bluejungle.destiny.inquirycenter.report.ReportViewException;
import com.bluejungle.destiny.interfaces.report.v1.ReportLibraryIF;
import com.bluejungle.destiny.types.basic.v1.SortDirection;
import com.bluejungle.destiny.types.report.v1.ReportVisibilityType;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportQuerySpec;
import com.bluejungle.destiny.types.report.v1.ReportQueryTermList;
import com.bluejungle.destiny.types.report.v1.ReportSortFieldName;
import com.bluejungle.destiny.types.report.v1.ReportSortTerm;
import com.bluejungle.destiny.types.report.v1.ReportSortTermList;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/MyReportsPageBeanImplTest.java#4 $
 */

public class MyReportsPageBeanImplTest extends BaseJSFTest {

    private MyReportsPageBeanImpl beanToTest;
    private TestReportLibraryIFImpl testReportService;

    public Report[] Reports;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MyReportsPageBeanImplTest.class);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.Reports = new Report[5];
        for (int i = 0; i < this.Reports.length; i++) {
            this.Reports[i] = new Report();
            this.Reports[i].setId(BigInteger.valueOf(i));
            this.Reports[i].setTitle(String.valueOf(i));
        }

        this.testReportService = new TestReportLibraryIFImpl(this.Reports);

        this.beanToTest = new TestMyReportsPageBeanImpl();
        this.beanToTest.load();
    }

    /**
     * Test getSelectedReport and setSelectedReportId
     */
    public void testGetSelectedReportSetSelectedReportId() {
        IReport selectedReport = this.beanToTest.getSelectedReport();
//        assertTrue("testGetSelectedReportSetSelectedReportId - Ensure initial selected report is a saved report - instance of SavedReportImpl", ((selectedReport instanceof ReportImpl) && (selectedReport instanceof SavedReportImpl)));

        this.beanToTest.setSelectedReportId(new Long(this.Reports[1].getId().longValue()));
        assertEquals("testGetSelectedReportSetSelectedReportId - Ensure report set as expected", this.Reports[1], ((ReportImpl) this.beanToTest.getSelectedReport()).getWrappedReport());

        NullPointerException expectedException = null;
        try {
            this.beanToTest.setSelectedReportId(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("testGetSelectedReportSetSelectedReportId - Should throw null ptr when calling setSelectedReportId(null)", expectedException);
    }

    /**
     * Test load(), loadNew(), and isLoaded()
     */
    public void testLoadNewLoadIsLoaded() {
        assertTrue("testIsLoaded - Ensure bean is loaded", this.beanToTest.isLoaded());
        this.beanToTest.reset();
        assertTrue("testIsLoaded - Ensure bean is not loaded after reset", !this.beanToTest.isLoaded());
        this.beanToTest.load();
        assertTrue("testIsLoaded - Ensure bean is not loaded after calling load after reset", this.beanToTest.isLoaded());
        this.beanToTest.reset();
//        assertTrue("testIsLoaded - Ensure bean is not loaded after reset two", !this.beanToTest.isLoaded());
//        this.beanToTest.loadNew();
//        assertTrue("testIsLoaded - Ensure bean is not loaded after calling loadNew after reset", this.beanToTest.isLoaded());
//
//        // Test mechanism to switch saved/new report
//        assertTrue("testLoadIsLoaded - Ensure report is initially new report", !(this.beanToTest.getSelectedReport() instanceof ISavedReport));
//        this.beanToTest.load();
//        assertTrue("testLoadIsLoaded - Ensure report switched to saved report", this.beanToTest.getSelectedReport() instanceof ISavedReport);
//        this.beanToTest.loadNew();
//        assertTrue("testLoadIsLoaded - Ensure report switched to new report after call to load new", !(this.beanToTest.getSelectedReport() instanceof ISavedReport));
    }

    public void testGetReportListQuerySpec() {
        ReportQuerySpec querySpec = beanToTest.getReportListQuerySpec();
        ReportVisibilityType visibility = querySpec.getVisibility();
        assertEquals("testGetReportListQuerySpec - The my reports page qujery spec should use the 'All' visibility", ReportVisibilityType.All, visibility);

        ReportSortTermList sortTermList = querySpec.getSortSpec();
        ReportSortTerm[] sortTermArray = sortTermList.getTerms();
        assertEquals("testGetReportListQuerySpec - Ensure sort term size is as expected", 1, sortTermArray.length);
        assertEquals("testGetReportListQuerySpec - Ensure single sort term field name is as expected", ReportSortFieldName.Title, sortTermArray[0].getFieldName());
        assertEquals("testGetReportListQuerySpec - Ensure single sort term direction is as expected", SortDirection.Ascending, sortTermArray[0].getDirection());

        ReportQueryTermList queryTermList = querySpec.getSearchSpec();
        assertNull("testGetReportListQuerySpec - Ensure query term list is null", queryTermList);
    }

    // These tests are no longer relevant, will need entirely new set of unit tests
//    public void testDeleteSelectedReport() throws ReportViewException {
//        // test illegal state exception
//        this.beanToTest.createNewQuickReport();
//        IllegalStateException expectedException = null;
//        try {
//            this.beanToTest.deleteSelectedReport();
//        } catch (IllegalStateException exception) {
//            expectedException = exception;
//        }
//        assertNotNull("testDeleteSelectedReport - Should throw illegal state exception when calling deleteSelectedReport with quick report selected", expectedException);
//
//        // Delete one in the middle of the list
//        this.beanToTest.setSelectedReportId(new Long(this.Reports[2].getId().longValue()));
//        this.beanToTest.deleteSelectedReport();
//        assertEquals("testDeleteSelectedReport - Ensure delete was called on report service with correct id", this.testReportService.getLastReportDeleted(), this.Reports[2].getId());
//
//        assertEquals("testDeleteSelectedReport - Ensure new report was selected", this.Reports[3], ((ReportImpl) this.beanToTest.getSelectedReport()).getWrappedReport());
//
//        // Delete that last one in the list
//        this.beanToTest.setSelectedReportId(new Long(this.Reports[4].getId().longValue()));
//        this.beanToTest.deleteSelectedReport();
//        assertEquals("testDeleteSelectedReport - Ensure previous report was selected", this.Reports[3], ((ReportImpl) this.beanToTest.getSelectedReport()).getWrappedReport());
//
//        // Delete the first one in the list
//        this.beanToTest.setSelectedReportId(new Long(this.Reports[0].getId().longValue()));
//        this.beanToTest.deleteSelectedReport();
//        assertEquals("testDeleteSelectedReport - Ensure next report was selected after first in list was deleted", this.Reports[1], ((ReportImpl) this.beanToTest.getSelectedReport()).getWrappedReport());
//
//        // Delete remaining reports
//        this.beanToTest.setSelectedReportId(new Long(this.Reports[1].getId().longValue()));
//        this.beanToTest.deleteSelectedReport();
//        this.beanToTest.setSelectedReportId(new Long(this.Reports[3].getId().longValue()));
//        this.beanToTest.deleteSelectedReport();
//        ResourceBundle bundle = ResourceBundle.getBundle("InquiryCenterMessages");
//        assertEquals("testDeleteSelectedReport - Ensure selected report updated to quick report after remaining deleted", bundle.getString("my_reports_quick_report_title"), this.beanToTest.getSelectedReport().getTitle());
//    }
//
//    public void testUpdateSelectedReport() throws ReportViewException {
//        // test illegal state exception
//        this.beanToTest.createNewQuickReport();
//        IllegalStateException expectedException = null;
//        try {
//            this.beanToTest.updateSelectedReport();
//        } catch (IllegalStateException exception) {
//            expectedException = exception;
//        }
//        assertNotNull("testUpdateSelectedReport - Should throw illegal state exception when calling updateSelectedReport with quick report selected", expectedException);
//
//        this.beanToTest.setSelectedReportId(new Long(this.Reports[1].getId().longValue()));
//        this.beanToTest.updateSelectedReport();
//        assertEquals("testUpdateSelectedReport - Ensure update was called on report service with correct value", this.testReportService.getLastReportUpdated(), this.Reports[1]);
//    }
//
//    public void testCancelSelectedReportEdit() {
//        // test illegal state exception
//        this.beanToTest.createNewQuickReport();
//        IllegalStateException expectedException = null;
//        try {
//            this.beanToTest.cancelSelectedReportEdit();
//        } catch (IllegalStateException exception) {
//            expectedException = exception;
//        }
//        assertNotNull("testCancelSelectedReportEdit - Should throw illegal state exception when calling cancelSelectedReportEdit with quick report selected", expectedException);
//
//        this.beanToTest.setSelectedReportId(new Long(this.Reports[1].getId().longValue()));
//        IReport selectedReport = this.beanToTest.getSelectedReport();
//        String originalUsersString = selectedReport.getUsers();
//        String testUserString = "foobarfooagain";
//        selectedReport.setUsers(testUserString);
//        assertEquals("testCancelSelectedReportEdit - Ensure user string set as expected.", testUserString, selectedReport.getUsers());
//
//        this.beanToTest.cancelSelectedReportEdit();
//        selectedReport = this.beanToTest.getSelectedReport();
//        assertEquals("testCancelSelectedReportEdit - Ensure user string reset as expected.", originalUsersString, selectedReport.getUsers());
//    }
//
//    public void testCreateNewQuickReport() {
//        this.beanToTest.createNewQuickReport();
//        IReport selectedReport = this.beanToTest.getSelectedReport();
//        assertTrue("testGetSelectedReportSetSelectedReportId - Ensure initial selected report is quick report - instance of ReportImpl", selectedReport instanceof ReportImpl);
//
//        ResourceBundle bundle = ResourceBundle.getBundle("InquiryCenterMessages");
//        assertEquals("testGetSelectedReportSetSelectedReportId - Ensure initial selected report is quick report - has quick report title", bundle.getString("my_reports_quick_report_title"), selectedReport.getTitle());
//
//        // Now, create a new one
//        this.beanToTest.createNewQuickReport();
//        IReport newSelectedReport = this.beanToTest.getSelectedReport();
////        assertTrue("testGetSelectedReportSetSelectedReportId - Ensure new quick report is selected - instance of ReportImpl", ((newSelectedReport instanceof ReportImpl) && !(newSelectedReport instanceof SavedReportImpl)));
////        ResourceBundle bundle = ResourceBundle.getBundle("InquiryCenterMessages");
//        assertEquals("testGetSelectedReportSetSelectedReportId - Ensure new quick report is selected - has quick report title", bundle.getString("my_reports_quick_report_title"), newSelectedReport.getTitle());
//        assertNotSame("testGetSelectedReportSetSelectedReportId - Ensure new quick report is not equal to previous quick report", selectedReport, newSelectedReport);
//    }
//
//    /**
//     * This test verifies the selected report insertion
//     * 
//     * @throws ReportViewException
//     */
//    public void testInsertSelectedReport() throws ReportViewException {
//        this.beanToTest.createNewQuickReport();
//        IReport currentlySelectedReport = this.beanToTest.getSelectedReport();
//        String testTitle = "fooBar";
//        currentlySelectedReport.setTitle(testTitle);
//        this.beanToTest.insertSelectedReport();
//        assertEquals("testInsertSelectedReport - Ensure insert called with correct report", this.testReportService.getLastReportInserted(), ((ReportImpl) currentlySelectedReport).getWrappedReport());
//        assertEquals("testInsertSelectedReport - Ensure insert called with correct insertion info", this.testReportService.getLastReportInserted().getTitle(), ((ReportImpl) currentlySelectedReport).getWrappedReport().getTitle());
//
//        // Ensure new report is selected
//        IReport selectedReport = this.beanToTest.getSelectedReport();
//        assertEquals("testInsertSelectedReport - New inserted report is selected", selectedReport.getTitle(), selectedReport.getTitle());
//
//        this.beanToTest.createNewQuickReport();
//        NullPointerException expectedException = null;
//        try {
//            this.beanToTest.insertSelectedReport();
//        } catch (NullPointerException exception) {
//            expectedException = exception;
//        }
//        assertNotNull("testInsertSelectedReport - Should throw null ptr exception when calling insertSelectedReport with null", expectedException);
//
//        this.beanToTest.setSelectedReportId(new Long(this.Reports[1].getId().longValue()));
//        IllegalStateException expectedIllegalStateException = null;
//        try {
//            this.beanToTest.insertSelectedReport();
//        } catch (IllegalStateException exception) {
//            expectedIllegalStateException = exception;
//        }
//        assertNotNull("testInsertSelectedReport - Should throw illegal state exception when calling insertSelectedReport with saved report selected", expectedIllegalStateException);
//    }

    /**
     * This test verifies no duplicated title could be insert
     * 
     * @throws ReportViewException
     */
    public void testInsertDuplicatedTitleReport() throws ReportViewException {
        this.beanToTest.createNewQuickReport();
        IReport currentlySelectedReport = this.beanToTest.getSelectedReport();
        String testTitle = "duplicatedTitle";
        currentlySelectedReport.setTitle(testTitle);
        this.beanToTest.insertSelectedReport();
        assertEquals("testInsertSelectedReport - Ensure insert called with correct report", this.testReportService.getLastReportInserted(), ((ReportImpl) currentlySelectedReport).getWrappedReport());
        assertEquals("testInsertSelectedReport - Ensure insert called with correct insertion info", this.testReportService.getLastReportInserted().getTitle(), ((ReportImpl) currentlySelectedReport).getWrappedReport().getTitle());

        // Ensure new report is selected
        IReport selectedReport = this.beanToTest.getSelectedReport();
        assertEquals("testInsertSelectedReport - New inserted report is selected", selectedReport.getTitle(), selectedReport.getTitle());

        this.beanToTest.createNewQuickReport();
        this.beanToTest.getSelectedReport().setTitle(testTitle);
        try {
            this.beanToTest.insertSelectedReport();
            fail("should not able to create report with duplicated titles.");
        } catch (ReportViewException e) {
            assertNotNull(e.toString());
        }
    }

    /**
     * This test verifies that the useless report elements are deleted before
     * being saved.
     * 
     * @throws ReportViewException
     */
    public void testInsertOnlyCleanedUpReports() throws ReportViewException {
        this.beanToTest.createNewQuickReport();
        // Make sure that useless report parts are not saved
        IReport currentlySelectedReport = this.beanToTest.getSelectedReport();
        final String myPolicy = "my Policy";
        currentlySelectedReport.setPolicies(myPolicy);
        currentlySelectedReport.setTargetData("ActivityJournal");
        final List enforcementList = new ArrayList();
        enforcementList.add("allow");
        currentlySelectedReport.setEnforcementsAsList(enforcementList);
        currentlySelectedReport.setTitle("new title");
        this.beanToTest.insertSelectedReport();
        assertNotNull("The list should not be null", currentlySelectedReport.getEnforcementsAsList());
        List list = currentlySelectedReport.getEnforcementsAsList();
        assertEquals("The list of enforcements should not be empty", 1, list.size());
        assertEquals("The default enforcement should be allow", "allow", (String) list.get(0));
        assertNotNull("The list of policies shoudl not be null", currentlySelectedReport.getPolicies());
        assertFalse("The list of policy should be empty", myPolicy.equals(currentlySelectedReport.getPolicies()));
    }

    private class TestMyReportsPageBeanImpl extends MyReportsPageBeanImpl {

        /**
         * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportPageBeanImpl#getReportLibraryService()
         */
        protected ReportLibraryIF getReportLibraryService() {
            return MyReportsPageBeanImplTest.this.testReportService;
        }
    }
}
