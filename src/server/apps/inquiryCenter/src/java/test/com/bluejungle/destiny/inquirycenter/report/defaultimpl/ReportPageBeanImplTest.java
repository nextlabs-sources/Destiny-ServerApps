/*
 * Created on Jun 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIInput;
import javax.faces.event.ActionEvent;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.interfaces.report.v1.ReportLibraryIF;
import com.bluejungle.destiny.types.basic_faults.v1.AccessDeniedFault;
import com.bluejungle.destiny.types.basic_faults.v1.PersistenceFault;
import com.bluejungle.destiny.types.basic_faults.v1.ServiceNotReadyFault;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportQuerySpec;
import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * Unit test for ReportPageBeanImpl
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/branch/Destiny_112/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportPageBeanImplTest.java#1 $
 */

public class ReportPageBeanImplTest extends BaseJSFTest {

    private ReportPageBeanImpl beanToTest;
    private TestReportLibraryIFImpl testReportService;
    private Report[] Reports;
    private MockExternalContext externalCtx = new MockExternalContext("/foo");

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ReportPageBeanImplTest.class);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.facesContext.setExternalContext(this.externalCtx);

        this.Reports = new Report[5];
        for (int i = 0; i < this.Reports.length; i++) {
            this.Reports[i] = new Report();
            this.Reports[i].setId(BigInteger.valueOf(i));
            this.Reports[i].setTitle(String.valueOf(i));
        }

        this.testReportService = new TestReportLibraryIFImpl(this.Reports);

        this.beanToTest = new TestReportPageBeanImpl();
        this.beanToTest.load();
    }

    public void testGetReportList() {
        List reportList = this.beanToTest.getReportList();
        assertEquals("testGetReportList - Ensure report list is correct size", this.Reports.length, reportList.size());

        // Check ids of reports returned to make sure they match
        Set returnedReportIds = new HashSet();
        Iterator reportIterator = reportList.iterator();
        while (reportIterator.hasNext()) {
            IReport nextReport = (IReport) reportIterator.next();
            returnedReportIds.add(nextReport.getId());
        }

        for (int i = 0; i < this.Reports.length; i++) {
            Report nextReport = this.Reports[i];
            assertTrue("testGetReportList - Ensure saved report id is contained within returned reports ids", returnedReportIds.contains(new Long(nextReport.getId().longValue())));
        }
    }

    /**
     * Test getSelectedReport and setSelectedReportId
     */
    public void testGetSelectedReportSetSelectedReportId() {
        // First report should be set initially
        assertEquals("testGetSelectedReportSetSelectedReportId - Ensure initial report set", this.Reports[0], ((ReportImpl) this.beanToTest.getSelectedReport()).getWrappedReport());

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
     * Test load() and isLoaded()
     */
    public void testLoadIsLoaded() {
        assertTrue("testIsLoaded - Ensure bean is loaded", this.beanToTest.isLoaded());
        this.beanToTest.reset();
        assertTrue("testIsLoaded - Ensure bean is not loaded after reset", !this.beanToTest.isLoaded());
        this.beanToTest.load();
        assertTrue("testIsLoaded - Ensure bean is not loaded after calling load after reset", this.beanToTest.isLoaded());
    }

    public void testOnExecuteReport() {
        ReportExecutorImpl reportExecutor = new ReportExecutorImpl();
        this.beanToTest.setReportExecutor(reportExecutor);

        IReport selectedReport = (IReport) this.beanToTest.getSelectedReport();
        this.beanToTest.onExecuteReport(new ActionEvent(new UIInput()));
        assertEquals("testOnExecuteReport - Ensure current report set on the executor - correct report name", selectedReport.getTitle(), reportExecutor.getReportName());
    }

    public void testReset() {
        assertTrue("testReset - Ensure bean is initially loaded after reset", this.beanToTest.isLoaded());
        assertTrue("testReset - Ensure report list is initially not empty", !this.beanToTest.getReportList().isEmpty());
        assertNotNull("testReset - Ensure selected report is initially not null", this.beanToTest.getSelectedReport());

        this.beanToTest.reset();

        assertTrue("testReset - Ensure bean is not loaded after reset", !this.beanToTest.isLoaded());
        assertTrue("testReset - Ensure report list is empty after reset", this.beanToTest.getReportList().isEmpty());
        assertNull("testReset - Ensure selected report is null after reset", this.beanToTest.getSelectedReport());
    }

    public void testSetDataLocation() {
        // Simply ensure it doesn't throw error
        this.beanToTest.setDataLocation("foo");

        NullPointerException expectedException = null;
        try {
            this.beanToTest.setDataLocation(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("testSetDataLocation - Ensure null ptr thrown when calling setDataLocation(null)");
    }

    public void testGetSetReportExecutor() {
        ReportExecutorImpl executor = new ReportExecutorImpl();
        this.beanToTest.setReportExecutor(executor);
        assertEquals("testGetSetReportExecutor - The executor property should be set as expected", executor, this.beanToTest.getReportExecutor());

        NullPointerException expectedException = null;
        try {
            this.beanToTest.setReportExecutor(null);
        } catch (NullPointerException exception) {
            expectedException = exception;
        }
        assertNotNull("testGetSetReportExecutor - Should throw null ptr when calling setReportExecutor(null)", expectedException);
    }

    public void testResetAndSelectReport() throws AccessDeniedFault, ServiceNotReadyFault, PersistenceFault, RemoteException {
        assertEquals("testResetAndSelectReport - Ensure initial report set", this.Reports[0], ((ReportImpl) this.beanToTest.getSelectedReport()).getWrappedReport());
        assertEquals("testResetAndSelectReport - Ensure report list size if initially 5", 5, this.beanToTest.getReportList().size());

        // Insert report
        Report reportToInsert = new Report();
        reportToInsert.setTitle("foo");
        this.testReportService.insertReport(reportToInsert);

        this.beanToTest.resetAndSelectReport(new Long(this.Reports[3].getId().longValue()));

        assertEquals("testResetAndSelectReport - Ensure chosen report is selected", this.Reports[3], ((ReportImpl) this.beanToTest.getSelectedReport()).getWrappedReport());
        assertEquals("testResetAndSelectReport - Ensure report list after reset is 6", 6, this.beanToTest.getReportList().size());
    }

    public void testRemoveSelectedReportFromReportList() {
        IReport selectedReport = this.beanToTest.getSelectedReport();
        List reportList = this.beanToTest.getReportList();
        assertEquals("testRemoveSelectedReportFromReportList - Ensure report list size if initially 5", 5, reportList.size());
        assertTrue("testRemoveSelectedReportFromReportList - Ensure report list initially contains selected report", reportList.contains(selectedReport));

        this.beanToTest.removeSelectedReportFromReportList();

        reportList = this.beanToTest.getReportList();
        assertEquals("testRemoveSelectedReportFromReportList - Ensure report list size is now 4 after removal", 4, reportList.size());
        assertTrue("testRemoveSelectedReportFromReportList - Ensure report list no longer contains selected report", !reportList.contains(selectedReport));
    }

    private class TestReportPageBeanImpl extends ReportPageBeanImpl {

        /**
         * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportPageBeanImpl#getReportListQuerySpec()
         */
        protected ReportQuerySpec getReportListQuerySpec() {
            return new ReportQuerySpec();
        }

        /**
         * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportPageBeanImpl#getReportLibraryService()
         */
        protected ReportLibraryIF getReportLibraryService() {
            return ReportPageBeanImplTest.this.testReportService;
        }
    }
}