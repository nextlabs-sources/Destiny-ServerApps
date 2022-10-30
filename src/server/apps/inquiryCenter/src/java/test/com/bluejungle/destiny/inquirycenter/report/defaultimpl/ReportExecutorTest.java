/*
 * All sources, binaries and HTML pages (C) copyright 2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.faces.component.UIData;
import javax.faces.model.DataModel;
import javax.xml.rpc.ServiceException;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.IReportExecutor;
import com.bluejungle.destiny.inquirycenter.report.IReportSummaryResult;
import com.bluejungle.destiny.inquirycenter.report.IResultsStatistics;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.ExpressionCutter;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.UserComponentEntityResolver;
import com.bluejungle.destiny.interfaces.report.v1.ReportExecutionIF;
import com.bluejungle.destiny.types.basic.v1.SortDirection;
import com.bluejungle.destiny.types.basic.v1.StringList;
import com.bluejungle.destiny.types.basic_faults.v1.AccessDeniedFault;
import com.bluejungle.destiny.types.basic_faults.v1.ServiceNotReadyFault;
import com.bluejungle.destiny.types.basic_faults.v1.UnknownEntryFault;
import com.bluejungle.destiny.types.effects.v1.EffectType;
import com.bluejungle.destiny.types.report.v1.ExecutionFault;
import com.bluejungle.destiny.types.report.v1.InvalidArgumentFault;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportSortFieldName;
import com.bluejungle.destiny.types.report.v1.ReportSortSpec;
import com.bluejungle.destiny.types.report.v1.ReportSummaryType;
import com.bluejungle.destiny.types.report.v1.ReportTargetType;
import com.bluejungle.destiny.types.report_result.v1.ActivityDetailResult;
import com.bluejungle.destiny.types.report_result.v1.DetailResultList;
import com.bluejungle.destiny.types.report_result.v1.DocumentActivityDetailResult;
import com.bluejungle.destiny.types.report_result.v1.PolicyActivityDetailResult;
import com.bluejungle.destiny.types.report_result.v1.ReportDetailResult;
import com.bluejungle.destiny.types.report_result.v1.ReportResult;
import com.bluejungle.destiny.types.report_result.v1.LogDetailResult;
import com.bluejungle.destiny.types.report_result.v1.ReportState;
import com.bluejungle.destiny.types.report_result.v1.ReportSummaryResult;
import com.bluejungle.destiny.types.report_result.v1.SummaryResult;
import com.bluejungle.destiny.types.report_result.v1.SummaryResultList;
import com.bluejungle.destiny.webui.framework.faces.ILoadable;
import com.bluejungle.destiny.webui.framework.sort.ISortStateMgr;
import com.bluejungle.destiny.webui.framework.sort.SortStateMgrImpl;
import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;
import com.bluejungle.domain.action.ActionEnumType;

/**
 * This is the test class for the report executor.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportExecutorTest.java#1 $
 */

public class ReportExecutorTest extends BaseJSFTest {

    private MockExternalContext externalCtx = new MockExternalContext("/foo");
    private static final String EXEC_ID_REQ_PARAM = "execId";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.facesContext.setExternalContext(this.externalCtx);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws NullPointerException {
        super.tearDown();
    }

    /**
     * This test verifies that the back button action is detected properly
     */
    public void testReportExecutorBackButtonDetection() {
        ReportExecutorImpl exec = new ReportExecutorImpl();
        Long now = new Long(System.currentTimeMillis());
        Map reqMap = new HashMap();
        String reqValue = "" + now.longValue();
        reqMap.put(EXEC_ID_REQ_PARAM, reqValue);
        this.externalCtx.setRequestParameterMap(reqMap);
        Long before = new Long(now.longValue() - 1000);
        exec.setLastExecutionId(before);
        assertFalse("The execution manager should not consider navigation as back button", exec.isBackButtonNavigation());

        //Try the NULL case
        exec.setLastExecutionId(null);
        assertFalse("The execution manager should not consider navigation as back button", exec.isBackButtonNavigation());

        //Try back navigation
        reqValue = "" + before.longValue();
        reqMap.put(EXEC_ID_REQ_PARAM, reqValue);
        exec.setLastExecutionId(now);
        assertTrue("The execution manager should consider navigation as back button", exec.isBackButtonNavigation());
    }

    /**
     * This test verifies that the basics for the class are correct
     */
    public void testReportExecutorClassBasics() {
        ReportExecutorImpl exec = new ReportExecutorImpl();
        assertTrue("ReportExecutorImpl should implement the right interface", exec instanceof IReportExecutor);
        assertTrue("ReportExecutorImpl should implement the right interface", exec instanceof ILoadable);

        //test basic properties
        final String loc = "http://localhost";
        exec.setDataLocation(loc);
        assertEquals("Data location property should match", loc, exec.getDataLocation());
        final UIData resultTable = new UIData();
        exec.setResultTable(resultTable);
        assertEquals("Result table property should match", resultTable, exec.getResultTable());
        assertNotNull("Page size should always have a default", exec.getPageSize());
        assertNotNull("Print size should always have a default", exec.getPrintSize());
        boolean exThrown = false;
        try {
            exec.setPageSize(null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("Page size cannot be set to null", exThrown);
        exThrown = false;
        try {
            exec.setPrintSize(null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        exThrown = false;
        try {
            exec.setMaxDisplayResults(null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("Max display results cannot be set to null", exThrown);
        final Integer pageSize = new Integer(30);
        exec.setPageSize(pageSize);
        assertEquals("Page size property should match", pageSize, exec.getPageSize());
        final Integer printSize = new Integer(200);
        exec.setPrintSize(printSize);
        assertEquals("Print size property should match", printSize, exec.getPrintSize());
        final ReportState currentState = new ReportState();
        exec.setCurrentState(currentState);
        assertEquals("State property should match", currentState, exec.getCurrentState());

        final Integer maxDispResults = new Integer(100);
        exec.setMaxDisplayResults(maxDispResults);
        assertEquals("Max display results property should match", maxDispResults, exec.getMaxDisplayResults());

        //Checks the column name
        assertNotNull("Column name cannot be empty", exec.getCountColumnName());
        assertTrue("Column name cannot be empty", exec.getCountColumnName().length() > 0);
        assertNotNull("Column name cannot be empty", exec.getDateColumnName());
        assertTrue("Column name cannot be empty", exec.getDateColumnName().length() > 0);
        assertNotNull("Column name cannot be empty", exec.getFromResourceColumnName());
        assertTrue("Column name cannot be empty", exec.getFromResourceColumnName().length() > 0);
        assertNotNull("Column name cannot be empty", exec.getToResourceColumnName());
        assertTrue("Column name cannot be empty", exec.getToResourceColumnName().length() > 0);
        assertNotNull("Column name cannot be empty", exec.getUserColumnName());
        assertTrue("Column name cannot be empty", exec.getUserColumnName().length() > 0);
        assertNotNull("Column name cannot be empty", exec.getHostColumnName());
        assertTrue("Column name cannot be empty", exec.getHostColumnName().length() > 0);
    }

    /**
     * This test verifies that the correct summary result is created based on
     * the report to execute.
     */
    public void testReportExecutorSummaryResultCreation() {
        ReportExecutorImpl exec = new ReportExecutorImpl();
        boolean exThrown = false;
        try {
            exec.createReportSummaryResult(null, new Report());
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("createReportSummaryResult should not accept null arguments", exThrown);
        exThrown = false;
        try {
            exec.createReportSummaryResult(new SummaryResult(), null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("createReportSummaryResult should not accept null arguments", exThrown);
        SummaryResult wsResult = new SummaryResult();
        final long count = 2589;
        final String value = "myValue";
        wsResult.setCount(count);
        wsResult.setValue(value);
        final Report wsReport = new Report();
        //Test with None
        wsReport.setSummaryType(ReportSummaryType.None);
        IReportSummaryResult result = exec.createReportSummaryResult(wsResult, wsReport);
        assertNotNull("The result object should not be null", result);
        assertTrue("The result object should be the correct class name", result instanceof ReportSummaryResultImpl);
        //Test with Policy
        wsReport.setSummaryType(ReportSummaryType.Policy);
        result = exec.createReportSummaryResult(wsResult, wsReport);
        assertNotNull("The result object should not be null", result);
        assertTrue("The result object should be the correct class name", result instanceof ReportSummaryResultImpl);
        //Test with Resource
        wsReport.setSummaryType(ReportSummaryType.Resource);
        result = exec.createReportSummaryResult(wsResult, wsReport);
        assertNotNull("The result object should not be null", result);
        assertTrue("The result object should be the correct class name", result instanceof ReportSummaryResultImpl);
        //Test with Time in days
        wsReport.setSummaryType(ReportSummaryType.TimeDays);
        result = exec.createReportSummaryResult(wsResult, wsReport);
        assertNotNull("The result object should not be null", result);
        assertTrue("The result object should be the correct class name", result instanceof ReportSummaryResultDateGroupingImpl);
        //Test with Time in months
        wsReport.setSummaryType(ReportSummaryType.TimeMonths);
        result = exec.createReportSummaryResult(wsResult, wsReport);
        assertNotNull("The result object should not be null", result);
        assertTrue("The result object should be the correct class name", result instanceof ReportSummaryResultDateGroupingImpl);
        //Test with User
        wsReport.setSummaryType(ReportSummaryType.User);
        result = exec.createReportSummaryResult(wsResult, wsReport);
        assertNotNull("The result object should not be null", result);
        assertTrue("The result object should be the correct class name", result instanceof ReportSummaryResultImpl);
    }

    /**
     * This test verifies that the user can setup correctly the report to
     * execute.
     */
    public void testReportExecutorExecutionSettings() {
        ReportExecutorImpl exec = new ReportExecutorImpl();
        boolean exThrown = false;
        try {
            exec.setReportToExecute(new Report(), null);
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("setReportToExecute should not accept null arguments", exThrown);
        exThrown = false;
        try {
            exec.setReportToExecute(null, "dummy name");
        } catch (NullPointerException e) {
            exThrown = true;
        }
        assertTrue("setReportToExecute should not accept null arguments", exThrown);
        final Report reportToExec = new Report();
        reportToExec.setTarget(ReportTargetType.ActivityJournal);
        final String reportName = "myName";
        assertNull("By default, no report name is set", exec.getReportName());
        exec.setReportToExecute(reportToExec, reportName);
        assertEquals("The report name should be set properly", reportName, exec.getReportName());
        IReport uiReport = exec.getReport();
        assertNotNull("A UI report should be produced", uiReport);
        assertEquals("report data should be available for the UI", "Document Activity", uiReport.getTargetDisplayName());
        assertEquals("Default sort should be set on date column and descending", exec.getDateColumnName(), exec.getSortColumnName());
        assertFalse("Default sort should be set on date column and descending", exec.isSortAscending());
    }

    /**
     * This test verifies that the report executor takes into account the sort
     * specification of a report if it is specified when set.
     */
    public void testReportExecutorExecutionSettingsWithSorting() {
        ReportExecutorImpl exec = new ReportExecutorImpl();
        Report reportToExecute = new Report();
        ReportSortSpec sortSpec = new ReportSortSpec();
        sortSpec.setDirection(SortDirection.Ascending);
        sortSpec.setField(ReportSortFieldName.Count);
        reportToExecute.setSortSpec(sortSpec);
        exec.setReportToExecute(reportToExecute, "dummyName");
        ReportSortSpec currentSortSpec = exec.getCurrentSortSpec();
        assertEquals("The current sort direction should be adapted to the report", sortSpec.getDirection(), currentSortSpec.getDirection());
        assertEquals("The current sort field should be adapted to the report", sortSpec.getField(), currentSortSpec.getField());
    }

    /**
     * This test verifies that the correct outcome value is returned based on
     * the report to execute. The grouping and time set for the report
     * determines which result page to go to.
     */
    public void testReportExecutorExecutionOutcome() {
        ReportExecutorImpl exec = new ReportExecutorImpl();
        final Report reportToExec = new Report();
        final String reportName = "myName";
        reportToExec.setTarget(ReportTargetType.ActivityJournal);
        reportToExec.setSummaryType(null);
        exec.setReportToExecute(reportToExec, reportName);
        assertEquals("If the grouping is invalid, the error page should be shown", "reportExecution", exec.executeReport());
    }

    /**
     * This test verifies that the time window used for the report execution is
     * correct based on the report parameters.
     */
    public void testReportExecutorSetupTimeFrame() {
        ReportExecutorImpl exec = new ReportExecutorImpl();

        //Case one - No date specified
        final Report reportToExec = new Report();
        reportToExec.setTarget(ReportTargetType.ActivityJournal);
        reportToExec.setSummaryType(ReportSummaryType.TimeDays);
        reportToExec.setBeginDate(null);
        reportToExec.setEndDate(null);
        exec.setAppropriateTimeSummary(reportToExec);
        assertEquals("When no time is specified with time grouping, the query is summarized by month", ReportSummaryType.TimeMonths, reportToExec.getSummaryType());
        Calendar endDate = Calendar.getInstance();
        Calendar beginDate = Calendar.getInstance();
        beginDate.setTimeInMillis(endDate.getTimeInMillis());
        beginDate.add(Calendar.YEAR, -1);
        assertNotNull("When no time is specified with time grouping, the query time window starts a year ago", reportToExec.getBeginDate());
        assertEquals("When no time is specified with time grouping, the query time window starts a year ago", beginDate, reportToExec.getBeginDate());
        assertNotNull("When no time is specified with time grouping, the query time window starts a year ago", reportToExec.getEndDate());
        assertEquals("When no time is specified with time grouping, the query time window starts a year ago", endDate, reportToExec.getEndDate());

        //Only end date specified
        endDate = Calendar.getInstance();
        beginDate = Calendar.getInstance();
        beginDate.setTimeInMillis(endDate.getTimeInMillis());
        beginDate.add(Calendar.YEAR, -1);
        reportToExec.setBeginDate(null);
        reportToExec.setEndDate(endDate);
        exec.setAppropriateTimeSummary(reportToExec);
        assertEquals("When a time window with only end date is specified with time grouping, the query is summarized by months", ReportSummaryType.TimeMonths, reportToExec.getSummaryType());
        assertEquals("When a time window with only end date is specified with time grouping, begin date is set to one year ago", beginDate, reportToExec.getBeginDate());
        assertEquals("When a time window with only end date is specified with time grouping, end date are not modified", endDate, reportToExec.getEndDate());

        //Only begin date specified - less than a year ago
        endDate = Calendar.getInstance();
        beginDate = Calendar.getInstance();
        beginDate.add(Calendar.MONTH, -6);
        reportToExec.setBeginDate(beginDate);
        reportToExec.setEndDate(null);
        exec.setAppropriateTimeSummary(reportToExec);
        assertEquals("When a time window with only begin date is specified with time grouping, the query is summarized by months if begin date is more than a month ago", ReportSummaryType.TimeMonths, reportToExec.getSummaryType());
        assertEquals("When a time window with only begin date is specified with time grouping, if begin date is less than a year ago, begin date is unchanged", beginDate, reportToExec.getBeginDate());
        assertEquals("When a time window with only begin date is specified with time grouping, end date is set to now", endDate, reportToExec.getEndDate());

        //only begin date specified - more than a year ago
        endDate = Calendar.getInstance();
        beginDate = Calendar.getInstance();
        beginDate.add(Calendar.YEAR, -2);
        Calendar modifiedBeginDate = Calendar.getInstance();
        modifiedBeginDate.setTimeInMillis(endDate.getTimeInMillis());
        modifiedBeginDate.add(Calendar.YEAR, -1);
        reportToExec.setBeginDate(beginDate);
        reportToExec.setEndDate(null);
        exec.setAppropriateTimeSummary(reportToExec);
        assertEquals("When a time window with only begin date is specified with time grouping, the query is summarized by months if begin date is more than a month ago", ReportSummaryType.TimeMonths, reportToExec.getSummaryType());
        assertEquals("When a time window with only begin date is specified with time grouping, if begin date is more than a year ago, begin date is unchanged", beginDate, reportToExec.getBeginDate());
        assertEquals("When a time window with only begin date is specified with time grouping, if begin date is more than a year ago, end date is set 1 year after begin date", endDate, reportToExec.getEndDate());

        //Less than a month window
        reportToExec.setBeginDate(beginDate);
        reportToExec.setEndDate(endDate);
        beginDate.setTimeInMillis(endDate.getTimeInMillis());
        beginDate.add(Calendar.DATE, -5);
        exec.setAppropriateTimeSummary(reportToExec);
        assertEquals("When a time window of less than 1 month is specified with time grouping, the query is summarized by days", ReportSummaryType.TimeDays, reportToExec.getSummaryType());
        assertEquals("When a time window of less than 1 month is specified with time grouping, begin and end date are not modified", beginDate, reportToExec.getBeginDate());
        assertEquals("When a time window of less than 1 month is specified with time grouping, begin and end date are not modified", endDate, reportToExec.getEndDate());

        //Exactly one month window
        reportToExec.setEndDate(endDate);
        Date debugEndDate = endDate.getTime();
        beginDate.setTimeInMillis(endDate.getTimeInMillis());
        beginDate.add(Calendar.MONTH, -1);
        reportToExec.setBeginDate(beginDate);
        Date debugBeginDate = beginDate.getTime();
        exec.setAppropriateTimeSummary(reportToExec);
        assertEquals("When a time window of exactly 1 month is specified with time grouping, the query is summarized by days", ReportSummaryType.TimeDays, reportToExec.getSummaryType());
        assertEquals("When a time window of exactly 1 month is specified with time grouping, begin and end date are not modified", beginDate, reportToExec.getBeginDate());
        assertEquals("When a time window of exactly 1 month is specified with time grouping, begin and end date are not modified", endDate, reportToExec.getEndDate());

        //More than one month window
        reportToExec.setEndDate(endDate);
        beginDate.setTimeInMillis(endDate.getTimeInMillis());
        beginDate.add(Calendar.MONTH, -1);
        beginDate.add(Calendar.DATE, -1);
        reportToExec.setBeginDate(beginDate);
        exec.setAppropriateTimeSummary(reportToExec);
        assertEquals("When a time window of more than 1 month is specified with time grouping, the query is summarized by time", ReportSummaryType.TimeMonths, reportToExec.getSummaryType());
        assertEquals("When a time window of more than 1 month is specified with time grouping, begin and end date are not modified", beginDate, reportToExec.getBeginDate());
        assertEquals("When a time window of more than 1 month is specified with time grouping, begin and end date are not modified", endDate, reportToExec.getEndDate());
    }

    /**
     * This test verifies that the report sort specification is build properly.
     */
    public void testReportExecutorSetupReportSortSpecConstruction() {
        ReportExecutorImpl exec = new ReportExecutorImpl();
        exec.setSortColumnName(exec.getPolicyColumnName());
        exec.setSortAscending(true);
        ReportSortSpec sortSpec = exec.getCurrentSortSpec();
        assertEquals("Sort spec (field name) should be created properly", ReportSortFieldName.Policy, sortSpec.getField());
        assertEquals("Sort spec (direction) should be created properly", SortDirection.Ascending, sortSpec.getDirection());
        exec.setSortAscending(false);
        sortSpec = exec.getCurrentSortSpec();
        assertEquals("Sort spec (direction) should be created properly", SortDirection.Descending, sortSpec.getDirection());
        exec.setSortColumnName(exec.getDateColumnName());
        sortSpec = exec.getCurrentSortSpec();
        assertEquals("Sort spec (field name) should be created properly", ReportSortFieldName.Date, sortSpec.getField());
        exec.setSortColumnName(exec.getUserColumnName());
        sortSpec = exec.getCurrentSortSpec();
        assertEquals("Sort spec (field name) should be created properly", ReportSortFieldName.User, sortSpec.getField());
        exec.setSortColumnName(exec.getFromResourceColumnName());
        sortSpec = exec.getCurrentSortSpec();
        assertEquals("Sort spec (field name) should be created properly", ReportSortFieldName.FromResource, sortSpec.getField());
        exec.setSortColumnName(exec.getToResourceColumnName());
        sortSpec = exec.getCurrentSortSpec();
        assertEquals("Sort spec (field name) should be created properly", ReportSortFieldName.ToResource, sortSpec.getField());
    }

    /**
     * This test verifies that the reset function of the execution bean works
     * fine.
     */
    public void testReportExecutorReset() {
        Map reqMap = new HashMap();
        reqMap.put(EXEC_ID_REQ_PARAM, "123456");
        this.externalCtx.setRequestParameterMap(reqMap);
        ReportExecutorImpl exec = new ReportExecutionImplForTest();
        UIData table = new UIData();
        table.setFirst(20);
        exec.setResultTable(table);
        Report reportToExec = new Report();
        reportToExec.setTarget(ReportTargetType.ActivityJournal);
        reportToExec.setSummaryType(ReportSummaryType.User);
        exec.setReportToExecute(reportToExec, "name");
        exec.executeReport();
        exec.load();
        exec.reset();
        assertFalse("After reset, the executor should not be loaded", exec.isLoaded());
        assertNull("After reset, the result table should not be null", exec.getResultTable());
        assertEquals("After reset, the result table row index should have been moved to 0", 0, table.getFirst());
        assertNull("After reset, the statistics shoudl be cleared", exec.getResultsStatistics());
        assertNull("After reset, the current state should be cleared", exec.getCurrentState());
        assertNotNull("After reset, the default empty result should be returned", exec.getResults());
        assertEquals("After reset, the enpty list of results should be returned", 0, exec.getResults().getRowCount());
    }

    /**
     * This test verifies that the navigation to all details works fine
     */
    public void testReportExecutorNavigateToAllDetails() {
        Map reqMap = new HashMap();
        reqMap.put(EXEC_ID_REQ_PARAM, "1234567890");
        this.externalCtx.setRequestParameterMap(reqMap);
        ReportExecutorImpl exec = new ReportExecutionImplForTest();
        exec.setDataLocation("http://locahost");
        Report reportToExec = new Report();
        UIData table = new UIData();
        exec.setResultTable(table);
        reportToExec.setTarget(ReportTargetType.ActivityJournal);
        reportToExec.setSummaryType(ReportSummaryType.User);
        final String reportName = "name";
        exec.setReportToExecute(reportToExec, reportName);
        exec.executeReport();
        exec.load();
        exec.navigateToAllDetails(null);
        exec.setResultTable(table);
        assertNotNull("There should be an executed report", exec.getReport());
        exec.executeReport();
        exec.load();
        assertEquals("The drilldown report should have no grouping", ReportSummaryType.None, exec.getLastExecutedReport().getSummaryType());
    }

    /**
     * Tests the prepareReportForPolicyDetail() method
     *  
     */
    public void testReportExecutorPrepareReportExecutionForPolicyDetail() {
        Report reportForTest = new Report();
        StringList policiesForTest = ExpressionCutter.convertToStringList("a, b, c");
        reportForTest.setPolicies(policiesForTest);
        String policyForDetail = "TestPolicyForDetail";

        // We prepare the report and make sure that the users field has been set
        // appropriately:
        ReportExecutorImpl exec = new ReportExecutorImpl();
        exec.prepareReportExecutionForPolicyDetail(reportForTest, policyForDetail);

        StringList newPolicyList = reportForTest.getPolicies();
        assertNotNull("Policy list should have been set on the report", newPolicyList);
        assertNotNull("Policy list should have been set on the report", newPolicyList.getValues());
        assertEquals("Policy list should have the right number of policies set", 1, newPolicyList.getValues().length);
        assertEquals("Policy list should have the right poicy set", policyForDetail, newPolicyList.getValues()[0]);
    }

    /**
     * Tests the prepareReportForResourceDetail() method
     *  
     */
    public void testReportExecutorPrepareReportExecutionForResourceDetail() {
        Report reportForTest = new Report();
        StringList resourcesForTest = ExpressionCutter.convertToStringList("a, b, c");
        reportForTest.setResourceNames(resourcesForTest);
        String resourceForDetail = "TestResourceForDetail";

        // We prepare the report and make sure that the users field has been set
        // appropriately:
        ReportExecutorImpl exec = new ReportExecutorImpl();
        exec.prepareReportExecutionForResourceDetail(reportForTest, resourceForDetail);

        StringList newResourceList = reportForTest.getResourceNames();
        assertNotNull("Resource list should have been set on the report", newResourceList);
        assertNotNull("Resource list should have been set on the report", newResourceList.getValues());
        assertEquals("Resource list should have the right number of resources set", 1, newResourceList.getValues().length);
        assertEquals("Resource list should have the right poicy set", resourceForDetail, newResourceList.getValues()[0]);
    }

    /**
     * Tests the prepareReportForTimeInDaysDetail() method
     *  
     */
    public void testReportExecutorPrepareReportExecutionForTimeInDaysDetail() {
        final Report report = new Report();
        Calendar randomStartDate = Calendar.getInstance();
        randomStartDate.setTimeInMillis((new Random()).nextLong());
        Calendar randomEndDate = Calendar.getInstance();
        randomEndDate.setTimeInMillis((new Random()).nextLong());
        report.setBeginDate(randomStartDate);
        report.setEndDate(randomEndDate);

        final Calendar beginOfDay = Calendar.getInstance();
        beginOfDay.set(Calendar.HOUR_OF_DAY, 0);
        beginOfDay.set(Calendar.MINUTE, 0);
        beginOfDay.set(Calendar.SECOND, 0);
        Long timeInMs = new Long(beginOfDay.getTimeInMillis());
        String timeForDetail = timeInMs.toString();

        final long oneDayInMillis = 24 * 3600 * 1000;
        final Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTimeInMillis(timeInMs.longValue() + oneDayInMillis);

        // We prepare the report and make sure that the users field has been set
        // appropriately:
        ReportExecutorImpl exec = new ReportExecutorImpl();
        exec.prepareReportExecutionForTimeInDaysDetail(report, timeForDetail);

        final Calendar calculatedStartDate = report.getBeginDate();
        final Calendar calculatedEndDate = report.getEndDate();

        assertNotNull("Start date should have been set on the report", calculatedStartDate);
        assertEquals("Start date must be the start of the search day", beginOfDay, calculatedStartDate);
        assertEquals("End date must be the end of the search day", endOfDay, calculatedEndDate);
    }

    /**
     * Tests the prepareReportForTimeInMonthsDetail() method
     */
    public void testReportExecutorPrepareReportExecutionForTimeInMonthsDetail() {
        final Report report = new Report();
        Calendar randomStartDate = Calendar.getInstance();
        randomStartDate.setTimeInMillis((new Random()).nextLong());
        Calendar randomEndDate = Calendar.getInstance();
        randomEndDate.setTimeInMillis((new Random()).nextLong());
        report.setBeginDate(randomStartDate);
        report.setEndDate(randomEndDate);

        final Calendar beginOfMonth = Calendar.getInstance();
        beginOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        beginOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        beginOfMonth.set(Calendar.MINUTE, 0);
        beginOfMonth.set(Calendar.SECOND, 0);
        Long timeInMs = new Long(beginOfMonth.getTimeInMillis());
        String timeForDetail = timeInMs.toString();

        final Calendar endOfMonth = Calendar.getInstance();
        endOfMonth.setTimeInMillis(timeInMs.longValue());
        endOfMonth.add(Calendar.MONTH, 1);

        // We prepare the report and make sure that the users field has been set
        // appropriately:
        ReportExecutorImpl exec = new ReportExecutorImpl();
        exec.prepareReportExecutionForTimeInMonthsDetail(report, timeForDetail);

        final Calendar calculatedStartDate = report.getBeginDate();
        final Calendar calculatedEndDate = report.getEndDate();

        assertNotNull("Start date should have been set on the report", calculatedStartDate);
        assertEquals("Start date must be the start of the search month", beginOfMonth, calculatedStartDate);
        assertEquals("End date must be the end of the search month", endOfMonth, calculatedEndDate);
    }

    /**
     * Tests the prepareReportForUserDetail() method
     *  
     */
    public void testReportExecutorPrepareReportExecutionForUserDetail() {
        Report reportForTest = new Report();
        StringList usersForTest = ExpressionCutter.convertToStringList("a, b, c");
        reportForTest.setUsers(usersForTest);
        String userForDetail = "TestUserForDetail";
        String qualifiedUserForDetail = UserComponentEntityResolver.createUserQualification(userForDetail);

        // We prepare the report and make sure that the users field has been set
        // appropriately:
        ReportExecutorImpl exec = new ReportExecutorImpl();
        exec.prepareReportExecutionForUserDetail(reportForTest, userForDetail);
        StringList newUserList = reportForTest.getUsers();
        assertNotNull("User list should have been set on the report", newUserList);
        assertNotNull("User list should have been set on the report", newUserList.getValues());
        assertEquals("User list should have the right number of users set", 1, newUserList.getValues().length);
        assertEquals("User list should have the right user set", qualifiedUserForDetail, newUserList.getValues()[0]);
    }

    /**
     * This test verifies that the state is properly restored upon back button
     * navigation
     */
    public void testReportExecutorStateRestore() {
        ReportExecutorImpl exec = new ReportExecutionImplForTest();
        final ISortStateMgr sortMgrToRestore = new SortStateMgrImpl();
        final String reportNameToRestore = "reportToRestore";
        final Report reportToRestore = new Report();
        reportToRestore.setSummaryType(ReportSummaryType.Policy);

        //Sets things first
        Long now = new Long(System.currentTimeMillis());
        Long lastExec = new Long(now.longValue() - 10000);
        exec.setLastExecutionId(lastExec);
        Map reqMap = new HashMap();
        String click1ReqValue = "" + now.longValue();
        reqMap.put(EXEC_ID_REQ_PARAM, click1ReqValue);
        this.externalCtx.setRequestParameterMap(reqMap);
        exec.setReportToExecute(reportToRestore, reportNameToRestore);
        exec.load();
        final IResultsStatistics statsToRestore = exec.getResultsStatistics();
        assertNotNull("Statistics to restore should exist", statsToRestore);
        assertEquals("Statistics should have the correct data", 2, statsToRestore.getAvailableRowCount());
        assertEquals("Statistics should have the correct data", 2, statsToRestore.getTotalRowCount());
        final DataModel dmToRestore = exec.getDataModel();
        assertNotNull("Data model to restore should exist", dmToRestore);
        assertEquals("Data model should have the right size", 2, dmToRestore.getRowCount());

        //Execute another report after a little while
        now = new Long(now.longValue() + 1000);
        String click2ReqValue = "" + now.longValue();
        reqMap.put(EXEC_ID_REQ_PARAM, click2ReqValue);
        exec.setReportToExecute(new Report(), "dummy");
        exec.load();
        exec.setResultTable(new UIData());
        exec.setResultsStatistics(new ResultsStatisticsImpl(10, 10));
        assertNotSame(dmToRestore, exec.getDataModel());

        //Now, click the back button
        reqMap.put(EXEC_ID_REQ_PARAM, click1ReqValue);
        exec.setReportToExecute(reportToRestore, reportNameToRestore);
        exec.load();
        assertEquals("The report name should be restored", reportNameToRestore, exec.getReportName());
        
        // This code is no longer in use
//        assertEquals("Statistics should be restored", statsToRestore, exec.getResultsStatistics());
//        assertEquals("Data model should be restored", dmToRestore, exec.getDataModel());
//        assertNotSame("A clone version of the report name should be restored", reportToRestore, exec.getLastExecutedReport());
//        assertEquals("A clone version of the report name should be restored", reportToRestore.getSummaryType(), exec.getLastExecutedReport().getSummaryType());
//        assertEquals("The right timestamp should be saved", new Long(click1ReqValue), exec.getLastReportExecutionId());
    }

    /**
     * This test verifies that the state is properly saved
     */
    public void testReportExecutorStateSaving() {
        ReportExecutorImpl exec = new ReportExecutorImpl();
        Report report = new Report();
        exec.setReportToExecute(report, "dummyName");
        final Long key = new Long(1);
        exec.saveReportExecutionState(key);
        assertTrue(exec.isReportExecutionStateSaved(key));
    }

    /**
     * This test verifies that the back button action is detected properly and
     * that an invalid execution id is handled properly.
     */
    public void testReportExecutorWithInvalidExecutionId() {
        ReportExecutorImpl exec = new ReportExecutorImpl();
        Long now = new Long(System.currentTimeMillis());
        Map reqMap = new HashMap();
        String reqValue = "null";
        reqMap.put(EXEC_ID_REQ_PARAM, reqValue);
        this.externalCtx.setRequestParameterMap(reqMap);
        Long before = new Long(now.longValue() - 1000);
        exec.setLastExecutionId(before);
        assertFalse("The execution manager should not consider navigation as back button", exec.isBackButtonNavigation());

        reqMap.put(EXEC_ID_REQ_PARAM, "badValue");
        this.externalCtx.setRequestParameterMap(reqMap);
        assertFalse("The execution manager should not consider navigation as back button", exec.isBackButtonNavigation());
    }

    /**
     * This test verifies that the navigation to details from a grouped value
     * works fine.
     */
    //    public void testReportExecutorNavigateToSomeDetails() {
    //    }
    /**
     * This test verifies that the print display works properly and that the
     * appropriate number of results is there.
     */
    //    public void testReportExecutorPrintMode() {
    //    }
    /**
     * This test verifies that the appropriate number of records gets fetched as
     * the user navigates. Various cases can be possible, based on the fetch
     * size specified by the configuration.
     */
    //    public void testReportExecutorDataLoading() {
    //    }
    /**
     * This test verifies that an error message is displayed if fetching rows
     * failed for some reason.
     */
    //    public void testReportExecutorDisplayErrorMessage() {
    //    }
    /**
     * This test verifies that an info message is displayed if no rows have been
     * fetched (but the report execution did not fail).
     */
    //    public void testReportExecutorDisplayNoRecordsMessage() {
    //    }
    /**
     * This class extends the class to test in order to bypass the web service
     * call to fech the results
     * 
     * @author ihanen
     */
    protected class ReportExecutionImplForTest extends ReportExecutorImpl {

        /**
         * 
         * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportExecutorImpl#getReportExecutionService()
         */
        protected ReportExecutionIF getReportExecutionService() throws ServiceException {
            return new MockReportExecutionImpl();
        }

        protected class MockReportExecutionImpl implements ReportExecutionIF {

            /**
             * @see com.bluejungle.destiny.interfaces.report.v1.ReportExecutionIF#executeReport(com.bluejungle.destiny.types.report.v1.Report,
             *      int)
             */
            public ReportResult executeReport(Report report, int maxNbResults, int maxFetch) throws RemoteException, ExecutionFault, AccessDeniedFault, ServiceNotReadyFault, InvalidArgumentFault {
                ReportResult result = null;
                if (ReportSummaryType.None != report.getSummaryType()) {
                    ReportSummaryResult resultToReturn = new ReportSummaryResult();
                    resultToReturn.setState(new ReportState());
                    resultToReturn.setTotalRowCount(2);
                    resultToReturn.setAvailableRowCount(2);
                    SummaryResultList dataList = new SummaryResultList();
                    dataList.setMinCount(5);
                    dataList.setMaxCount(10);
                    SummaryResult result1 = new SummaryResult();
                    result1.setCount(10);
                    result1.setValue("result1");
                    SummaryResult result2 = new SummaryResult();
                    result2.setCount(5);
                    result2.setValue("result2");
                    dataList.setResults(new SummaryResult[] { result1, result2 });
                    resultToReturn.setData(dataList);
                    result = resultToReturn;
                } else if (ReportTargetType.PolicyEvents.equals(report.getTarget())) {
                    ReportDetailResult resultToReturn = new ReportDetailResult();
                    PolicyActivityDetailResult result1 = new PolicyActivityDetailResult();
                    result1.setAction(ActionEnumType.ACTION_DELETE.getName());
                    result1.setApplicationName("foo");
                    result1.setEffect(EffectType.deny);
                    result1.setFromResourceName("file:///c:/test.txt");
                    result1.setHostIPAddress("10.17.11.130");
                    result1.setId(BigInteger.ONE);
                    result1.setPolicyName("/folder1/policy");
                    result1.setTimestamp(Calendar.getInstance());
                    result1.setUserName("ihanen@bluejungle.com");
                    PolicyActivityDetailResult result2 = new PolicyActivityDetailResult();
                    result2.setAction(ActionEnumType.ACTION_DELETE.getName());
                    result2.setApplicationName("foo");
                    result2.setEffect(EffectType.deny);
                    result2.setFromResourceName("file:///c:/test.txt");
                    result2.setHostIPAddress("10.17.11.130");
                    result2.setId(BigInteger.ONE);
                    result2.setPolicyName("/folder1/policy");
                    result2.setTimestamp(Calendar.getInstance());
                    result2.setUserName("ihanen@bluejungle.com");
                    resultToReturn.setAvailableRowCount(2);
                    resultToReturn.setTotalRowCount(2);
                    resultToReturn.setState(new ReportState());
                    resultToReturn.setData(new DetailResultList(new ActivityDetailResult[] { result1, result2 }));
                    result = resultToReturn;
                } else {
                    ReportDetailResult resultToReturn = new ReportDetailResult();
                    DocumentActivityDetailResult result1 = new DocumentActivityDetailResult();
                    result1.setAction(ActionEnumType.ACTION_DELETE.getName());
                    result1.setApplicationName("foo");
                    result1.setFromResourceName("file:///c:/test.txt");
                    result1.setHostIPAddress("10.17.11.130");
                    result1.setId(BigInteger.ONE);
                    result1.setTimestamp(Calendar.getInstance());
                    result1.setUserName("ihanen@bluejungle.com");
                    DocumentActivityDetailResult result2 = new DocumentActivityDetailResult();
                    result2.setAction(ActionEnumType.ACTION_DELETE.getName());
                    result2.setApplicationName("foo");
                    result2.setFromResourceName("file:///c:/test.txt");
                    result2.setHostIPAddress("10.17.11.130");
                    result2.setId(BigInteger.ONE);
                    result2.setTimestamp(Calendar.getInstance());
                    result2.setUserName("ihanen@bluejungle.com");
                    resultToReturn.setAvailableRowCount(2);
                    resultToReturn.setTotalRowCount(2);
                    resultToReturn.setState(new ReportState());
                    resultToReturn.setData(new DetailResultList(new ActivityDetailResult[] { result1, result2 }));
                    result = resultToReturn;
                }
                return result;
            }

            /**
             * @see com.bluejungle.destiny.interfaces.report.v1.ReportExecutionIF#getNextResultSet(com.bluejungle.destiny.types.report_result.v1.ReportState,
             *      int)
             */
            public ReportResult getNextResultSet(ReportState currentState, int nbRows) throws RemoteException, UnknownEntryFault, ExecutionFault, AccessDeniedFault, ServiceNotReadyFault {
                return null;
            }

            /**
             * @see com.bluejungle.destiny.interfaces.report.v1.ReportExecutionIF#getLogDetail(long)
             */
            public LogDetailResult getLogDetail(Report report, long recordId) throws RemoteException, UnknownEntryFault, ExecutionFault, AccessDeniedFault, ServiceNotReadyFault {
                return null;
            }

            /**
             * @see com.bluejungle.destiny.interfaces.report.v1.ReportExecutionIF#terminateReportExecution(com.bluejungle.destiny.types.report_result.v1.ReportState)
             */
            public void terminateReportExecution(ReportState currentState) throws RemoteException, UnknownEntryFault, ExecutionFault, AccessDeniedFault, ServiceNotReadyFault {
            }

        }
    }

}
