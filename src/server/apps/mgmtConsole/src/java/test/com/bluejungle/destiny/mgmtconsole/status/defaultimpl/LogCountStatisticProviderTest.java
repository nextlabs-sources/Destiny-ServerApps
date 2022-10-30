/*
 * Created on Apr 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status.defaultimpl;

import java.util.Date;
import java.util.Set;
import java.util.Calendar;

import com.bluejungle.destiny.container.shared.inquirymgr.IInquiry;
import com.bluejungle.destiny.container.shared.inquirymgr.IReport;
import com.bluejungle.destiny.container.shared.inquirymgr.IReportExecutionMgr;
import com.bluejungle.destiny.container.shared.inquirymgr.IReportMgr;
import com.bluejungle.destiny.container.shared.inquirymgr.IReportResultReader;
import com.bluejungle.destiny.container.shared.inquirymgr.IReportResultStatistics;
import com.bluejungle.destiny.container.shared.inquirymgr.IReportTimePeriod;
import com.bluejungle.destiny.container.shared.inquirymgr.IResultData;
import com.bluejungle.destiny.container.shared.inquirymgr.ISortSpec;
import com.bluejungle.destiny.container.shared.inquirymgr.InquiryTargetDataType;
import com.bluejungle.destiny.container.shared.inquirymgr.ReportSummaryType;
import com.bluejungle.destiny.mgmtconsole.BaseMgmtConsoleComponentTestCase;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatistic;
import com.bluejungle.destiny.mgmtconsole.status.defaultimpl.statistic.IStatisticSet;
import com.bluejungle.domain.action.ActionEnumType;
import com.bluejungle.domain.policydecision.PolicyDecisionEnumType;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;

/**
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/test/com/bluejungle/destiny/mgmtconsole/status/defaultimpl/LogCountStatisticProviderTest.java#2 $
 */

public class LogCountStatisticProviderTest extends BaseMgmtConsoleComponentTestCase {

    private static final String REPORT_MANAGER_COMP_NAME = "ReportManagerComp";
    private static final String REPORT_EXECUTION_MANAGER_COMP_NAME = "ReportExecutionManagerComp";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LogCountStatisticProviderTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPullStatistic() {
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
        ComponentInfo compInfo = new ComponentInfo(REPORT_MANAGER_COMP_NAME, ReportManager.class.getName(), IReportMgr.class.getName(), LifestyleType.SINGLETON_TYPE);
        compMgr.registerComponent(compInfo, true);

        compInfo = new ComponentInfo(REPORT_EXECUTION_MANAGER_COMP_NAME, ReportExecutionManager.class.getName(), IReportExecutionMgr.class.getName(), LifestyleType.SINGLETON_TYPE);
        ReportExecutionManager reportExecutionManager = (ReportExecutionManager) compMgr.getComponent(compInfo);

        ReportResultReader resultReader = reportExecutionManager.getTestResultReader();

        Long rowCountOne = new Long(10);
        resultReader.setRowCount(rowCountOne);

        compInfo = new ComponentInfo("TestLogCountProviderComp", LogCountStatisticsProvider.class.getName(), ILogCountStatisticsProvider.class.getName(), LifestyleType.TRANSIENT_TYPE);
        LogCountStatisticsProvider providerToTest = (LogCountStatisticsProvider) compMgr.getComponent(compInfo);

        IStatisticSet statSetRetrieved = providerToTest.pullStatistic();
        IStatistic trackingLogEntries = statSetRetrieved.getStatistic(ILogCountStatisticsProvider.TRACKING_ACTIVITY_LOG_COUNT_STAT_KEY);
        IStatistic policyLogEntries = statSetRetrieved.getStatistic(ILogCountStatisticsProvider.POLICY_ACTIVITY_LOG_COUNT_STAT_KEY);
        assertEquals("testPullStatistic - Ensure tracking count is as expected", rowCountOne, trackingLogEntries.getValue());
        assertEquals("testPullStatistic - Ensure policy count is as expected", rowCountOne, policyLogEntries.getValue());

        Long rowCountTwo = new Long(4587);
        resultReader.setRowCount(rowCountTwo);

        statSetRetrieved = providerToTest.pullStatistic();
        trackingLogEntries = statSetRetrieved.getStatistic(ILogCountStatisticsProvider.TRACKING_ACTIVITY_LOG_COUNT_STAT_KEY);
        policyLogEntries = statSetRetrieved.getStatistic(ILogCountStatisticsProvider.POLICY_ACTIVITY_LOG_COUNT_STAT_KEY);
        assertEquals("testPullStatistic - Ensure tracking count two is as expected", rowCountTwo, trackingLogEntries.getValue());
        assertEquals("testPullStatistic - Ensure policy count two is as expected", rowCountTwo, policyLogEntries.getValue());
    }

    public static class ReportManager implements IReportMgr {

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportMgr#createReport()
         */
        public IReport createReport() {
            return new Report();
        }

    }

    private static class Report implements IReport {

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReport#getInquiry()
         */
        public IInquiry getInquiry() {
            return new Inquiry();
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReport#getAsOf()
         */
        public Date getAsOf() {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReport#getSortSpec()
         */
        public ISortSpec getSortSpec() {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReport#getSummaryType()
         */
        public ReportSummaryType getSummaryType() {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReport#getTimePeriod()
         */
        public IReportTimePeriod getTimePeriod() {
            return new IReportTimePeriod() {

                public Calendar getBeginDate() {
                    return null;
                }

                public Calendar getEndDate() {
                    return null;
                }

                public void setBeginDate(Calendar beginDate) {
                }

                public void setEndDate(Calendar endDate) {
                }
            };
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReport#setAsOf(java.util.Date)
         */
        public void setAsOf(Date arg0) {
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReport#setSummaryType(com.bluejungle.destiny.container.shared.inquirymgr.ReportSummaryType)
         */
        public void setSummaryType(ReportSummaryType arg0) {
        }
    }

    private static class Inquiry implements IInquiry {

        public void addAction(ActionEnumType action) {
        }

        public void addApplication(String newApplication) {
        }

        public void addObligation(String obligation) {
        }

        public void addPolicy(String policy) {
        }

        public void addPolicyDecision(PolicyDecisionEnumType newDecision) {

        }

        public void addResource(String resource) {
        }

        public void addUser(String user) {
        }

        public Set getActions() {
            return null;
        }

        public Set getApplications() {
            return null;
        }

        public Set getObligations() {
            return null;
        }

        public Set getPolicies() {
            return null;
        }

        public Set getPolicyDecisions() {
            return null;
        }

        public Set getResources() {
            return null;
        }

        public InquiryTargetDataType getTargetData() {
            return null;
        }

        public Set getUsers() {
            return null;
        }
        
        public int getLoggingLevel(){
            return 0;
        }

        public void setTargetData(InquiryTargetDataType target) {
        }
        
        public void setLoggingLevel(int level){
        }
    }

    public static class ReportExecutionManager implements IReportExecutionMgr {

        private ReportResultReader resultReader = new ReportResultReader();

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportExecutionMgr#executeReport(com.bluejungle.destiny.container.shared.inquirymgr.IReport)
         */
        public IReportResultReader executeReport(IReport report) {
            return this.resultReader;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportExecutionMgr#executeReport(com.bluejungle.destiny.container.shared.inquirymgr.IReport,
         *      long)
         */
        public IReportResultReader executeReport(IReport report, int maxStoredRows) {
            return this.resultReader;
        }

        public ReportResultReader getTestResultReader() {
            return this.resultReader;
        }

    }

    private static class ReportResultReader implements IReportResultReader {

        private Long rowCount;
        private IReportResultStatistics resultStatistics;

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportResultReader#close()
         */
        public void close() {
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportResultReader#hasNextResult()
         */
        public boolean hasNextResult() {
            return false;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportResultReader#nextResult()
         */
        public IResultData nextResult() {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportResultReader#getRowCount()
         */
        public Long getRowCount() {
            return this.rowCount;
        }

        /**
         * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportResultReader#getStatistics()
         */
        public IReportResultStatistics getStatistics() {
            return this.resultStatistics;
        }

        public void setRowCount(Long rowCount) {
            this.rowCount = rowCount;

            this.resultStatistics = new IReportResultStatistics() {

                /**
                 * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportResultStatistics#getAvailableRowCount()
                 */
                public Long getAvailableRowCount() {
                    return null;
                }

                /**
                 * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportResultStatistics#getMaxValue()
                 */
                public Long getMaxValue() {
                    return null;
                }

                /**
                 * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportResultStatistics#getMinValue()
                 */
                public Long getMinValue() {
                    return null;
                }

                /**
                 * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportResultStatistics#getSumValue()
                 */
                public Long getSumValue() {
                    return null;
                }

                /**
                 * @see com.bluejungle.destiny.container.shared.inquirymgr.IReportResultStatistics#getTotalRowCount()
                 */
                public Long getTotalRowCount() {
                    return ReportResultReader.this.rowCount;
                }

            };
        }

    }
}