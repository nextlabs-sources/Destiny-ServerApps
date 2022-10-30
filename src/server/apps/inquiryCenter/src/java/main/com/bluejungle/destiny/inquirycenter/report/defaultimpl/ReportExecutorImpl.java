/*
 * Created on May 3, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.bluejungle.destiny.interfaces.report.v1.AccessDeniedFault;
import com.bluejungle.destiny.interfaces.report.v1.ExecutionFault;
import com.bluejungle.destiny.interfaces.report.v1.InvalidArgumentFault;
import com.bluejungle.destiny.interfaces.report.v1.ReportExecutionServiceStub;
import com.bluejungle.destiny.interfaces.report.v1.ServiceNotReadyFault;
import com.bluejungle.destiny.interfaces.report.v1.UnknownEntryFault;
import com.bluejungle.destiny.types.actions.v1.ActionType;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.inquirycenter.enumeration.ReportGroupByType;
import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.IReportDetailResult;
import com.bluejungle.destiny.inquirycenter.report.IReportExecutor;
import com.bluejungle.destiny.inquirycenter.report.IReportSummaryResult;
import com.bluejungle.destiny.inquirycenter.report.IResultsStatistics;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.UserComponentEntityResolver;
import com.bluejungle.destiny.types.basic.v1.SortDirection;
import com.bluejungle.destiny.types.basic.v1.StringList;
import com.bluejungle.destiny.types.effects.v1.EffectType;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportSortFieldName;
import com.bluejungle.destiny.types.report.v1.ReportSortSpec;
import com.bluejungle.destiny.types.report.v1.ReportSummaryType;
import com.bluejungle.destiny.types.report_result.v1.ActivityDetailResult;
import com.bluejungle.destiny.types.report_result.v1.DetailResultList;
import com.bluejungle.destiny.types.report_result.v1.DocumentActivityCustomResult;
import com.bluejungle.destiny.types.report_result.v1.DocumentActivityDetailResult;
import com.bluejungle.destiny.types.report_result.v1.LogDetailResult;
import com.bluejungle.destiny.types.report_result.v1.PolicyActivityCustomResult;
import com.bluejungle.destiny.types.report_result.v1.PolicyActivityDetailResult;
import com.bluejungle.destiny.types.report_result.v1.ReportDetailResult;
import com.bluejungle.destiny.types.report_result.v1.ReportResult;
import com.bluejungle.destiny.types.report_result.v1.ReportState;
import com.bluejungle.destiny.types.report_result.v1.ReportSummaryResult;
import com.bluejungle.destiny.types.report_result.v1.SummaryResult;
import com.bluejungle.destiny.types.report_result.v1.SummaryResultList;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.destiny.webui.framework.data.ExtensibleDataModel;
import com.bluejungle.destiny.webui.framework.faces.ILoadable;
import com.bluejungle.destiny.webui.framework.faces.IResetableBean;
import com.bluejungle.destiny.webui.framework.sort.ISortStateMgr;
import com.bluejungle.destiny.webui.framework.sort.SortStateMgrImpl;
import com.nextlabs.destiny.inquirycenter.report.defaultimpl.LogDetailResultImpl;

/**
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportExecutorImpl.java#1 $
 */

public class ReportExecutorImpl implements IReportExecutor, ILoadable, IResetableBean {

    /**
     * Default number of records in each page
     */
    private static final Integer DEFAULT_PAGE_SIZE = new Integer(25);

    /**
     * Default number of records that can ever be display within one query
     */
    private static final Integer DEFAULT_MAX_DISPLAY_RESULT = new Integer(500);

    /**
     * Default number of records on the printable view
     */
    private static final Integer DEFAULT_PRINT_SIZE = new Integer(10000);

    /**
     * Null String constant
     */
    private static final String NULL_STRING = "null";
    private static final String FIREFOX_NULL_STRING = ""; // this is the null string on FireFox

    /**
     * Name of the groupedType parameter to use when drilldown to details.
     */
    private static final String GROUPED_TYPE_REQ_PARAM = "groupedType";

    /**
     * Name of the groupedValue parameter to use when drilldown to details.
     */
    private static final String GROUPED_VALUE_REQ_PARAM = "groupedValue";

    /**
     * Report execution Id parameter when the report is first executed
     */
    private static final String EXECUTION_ID_REQ_PARAM = "execId";
    
    /**
     * Log Id parameter used to generate the log detail lookup link for each row
     */
    private static final String LOG_ID_PARAM = "logId";

    /**
     * Name of the service to access for report execution on the report web
     * service
     */
    private static final String REPORT_EXECUTION_SERVICE_SUFFIX = "/services/ReportExecutionService";
    
    /**
     * Name of the key in the resource bundle to display remote exception error
     */
    private static final String REPORT_EXECUTION_ERROR_REMOTE_ISSUE_BUNDLE_KEY = "reports_execution_error_remote_issue";

    /**
     * Name of the key in the resource bundle to display access denied error
     */
    private static final String REPORT_EXECUTION_ERROR_SERVICE_NOT_READY_BUNDLE_KEY = "reports_execution_error_service_not_ready";

    /**
     * Name of the action column name
     */
    private static final String ACTION_COLUMN_NAME = "action";

    /**
     * Name of the application name column
     */
    private static final String APPLICATION_COLUMN_NAME = "applicationName";

    /**
     * Name of the count column
     */
    private static final String COUNT_COLUMN_NAME = "count";

    /**
     * Name of the date column
     */
    private static final String DATE_COLUMN_NAME = "date";

    /**
     * Name of the "from resource" column
     */
    private static final String FROM_RESOURCE_COLUMN_NAME = "fromRes";

    /**
     * Name of the "host" column
     */
    private static final String HOST_COLUMN_NAME = "host";

    /**
     * Name of the "policy decision" column
     */
    private static final String POLICY_DECISION_COLUMN_NAME = "policyDecision";

    /**
     * Name of the "policy" column
     */
    private static final String POLICY_COLUMN_NAME = "policy";

    /**
     * Name of the "to resource" column
     */
    private static final String TO_RESOURCE_COLUMN_NAME = "toRes";

    /**
     * Name of the "user" column
     */
    private static final String USER_COLUMN_NAME = "user";
    
    /**
     * Name of the "logging level" column
     */
    private static final String LOGGING_LEVEL_COLUMN_NAME = "loggingLevel";

    /**
     * This set contains the various form of time grouping. The static code
     * below initializes the set
     */
    private static final Set TIME_GROUPINGS = new HashSet();

    static {
        TIME_GROUPINGS.add(ReportSummaryType.TimeDays);
        TIME_GROUPINGS.add(ReportSummaryType.TimeMonths);
    }

    /**
     * This set contains the name of the sort spec field to use based on the
     * column name that is sorted
     */
    private static final Map COLUMN_2_SORT_SPEC_FIELD_NAME = new HashMap();

    static {
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(ACTION_COLUMN_NAME, ReportSortFieldName.Action);
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(APPLICATION_COLUMN_NAME, ReportSortFieldName.Application);
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(COUNT_COLUMN_NAME, ReportSortFieldName.Count);
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(DATE_COLUMN_NAME, ReportSortFieldName.Date);
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(FROM_RESOURCE_COLUMN_NAME, ReportSortFieldName.FromResource);
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(HOST_COLUMN_NAME, ReportSortFieldName.Host);
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(POLICY_COLUMN_NAME, ReportSortFieldName.Policy);
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(POLICY_DECISION_COLUMN_NAME, ReportSortFieldName.PolicyDecision);
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(TO_RESOURCE_COLUMN_NAME, ReportSortFieldName.ToResource);
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(USER_COLUMN_NAME, ReportSortFieldName.User);
        COLUMN_2_SORT_SPEC_FIELD_NAME.put(LOGGING_LEVEL_COLUMN_NAME, ReportSortFieldName.LoggingLevel);
    }

    private static final Map SORT_SPEC_FIELD_NAME_2_COLUMN = new HashMap();
    static {
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.Action, ACTION_COLUMN_NAME);
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.Application, APPLICATION_COLUMN_NAME);
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.Count, COUNT_COLUMN_NAME);
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.Date, DATE_COLUMN_NAME);
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.FromResource, FROM_RESOURCE_COLUMN_NAME);
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.Host, HOST_COLUMN_NAME);
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.Policy, POLICY_COLUMN_NAME);
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.PolicyDecision, POLICY_DECISION_COLUMN_NAME);
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.ToResource, TO_RESOURCE_COLUMN_NAME);
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.User, USER_COLUMN_NAME);
        SORT_SPEC_FIELD_NAME_2_COLUMN.put(ReportSortFieldName.LoggingLevel, LOGGING_LEVEL_COLUMN_NAME);
    }

    private final Log log = LogFactory.getLog(ReportExecutorImpl.class);

    /**
     * Outcome returned to navigate to the detail result page
     */
    protected static final String OUTCOME_DETAIL_RESULTS = "detailResults";

    /**
     * Outcome returned to navigate to the error page
     */
    protected static final String OUTCOME_ERROR = "error";

    /**
     * Outcome returned to navigate to the printable view for detail results
     */
    protected static final String OUTCOME_PRINT_DETAIL_RESULTS = "printDetailResults";

    /**
     * Outcome returned to navigate to the page of results grouped by day
     */
    protected static final String OUTCOME_DAY_GROUPING_RESULTS = "dayGroupingResults";

    /**
     * Outcome returned to navigate to the page of results grouped by month
     */
    protected static final String OUTCOME_MONTH_GROUPING_RESULTS = "monthGroupingResults";

    /**
     * Outcome returned to navigate to the page of results grouped by policy
     */
    protected static final String OUTCOME_POLICY_GROUPING_RESULTS = "policyGroupingResults";

    /**
     * Outcome returned to navigate to the page of results grouped by resource
     */
    protected static final String OUTCOME_RESOURCE_GROUPING_RESULTS = "resourceGroupingResults";

    /**
     * Outcome returned to navigate to the page of results grouped by user
     */
    protected static final String OUTCOME_USER_GROUPING_RESULTS = "userGroupingResults";
    
    /**
     * Outcome returned to navigate to the page of results grouped by user
     */
    protected static final String OUTCOME_SINGLE_LOG_RESULT = "singleLogResult";

    /**
     * Mapping of grouping type to result page. Based on the grouping type used,
     * the result page varies
     */
    private static final Map GROUPING_TO_OUTCOME = new HashMap();
    static {
        GROUPING_TO_OUTCOME.put(ReportSummaryType.None, OUTCOME_DETAIL_RESULTS);
        GROUPING_TO_OUTCOME.put(ReportSummaryType.Policy, OUTCOME_POLICY_GROUPING_RESULTS);
        GROUPING_TO_OUTCOME.put(ReportSummaryType.Resource, OUTCOME_RESOURCE_GROUPING_RESULTS);
        GROUPING_TO_OUTCOME.put(ReportSummaryType.TimeDays, OUTCOME_DAY_GROUPING_RESULTS);
        GROUPING_TO_OUTCOME.put(ReportSummaryType.TimeMonths, OUTCOME_MONTH_GROUPING_RESULTS);
        GROUPING_TO_OUTCOME.put(ReportSummaryType.User, OUTCOME_USER_GROUPING_RESULTS);
    }

    private int mode = 1;
    
    
    private ReportState currentState;
    private String dataLocation;
    private ExtensibleDataModel dataModel = new ExtensibleDataModel();
    private Integer fetchSize = new Integer(100); //Default fetch size
    private boolean loaded;
    private UIData resultTable;
    private Integer maxDisplayResults = DEFAULT_MAX_DISPLAY_RESULT;
    private Integer pageSize = DEFAULT_PAGE_SIZE;
    private Integer printSize = DEFAULT_PRINT_SIZE;
    private IResultsStatistics resultsStats;
    private ISortStateMgr sortStateMgr = new SortStateMgrImpl();
    private IReport uiReport;
    private Report lastExecutedReport;
    private Long lastExecutionId;
    private String reportName;
    private Report wrappedReport;
    private Map reportExecutionStates = new ExecutionStateCache(40);
    private IReportDetailResult myCurrentRow;
    private IReportDetailResult myCurrentDetailRow;
    private ReportExecutionServiceStub reportExecutionService;



    /**
     * Returns a result object that can be displayed on the UI. This function
     * converts the results returned from the server to user visible data. For
     * results grouped by user name or policy name, no change is necessary.
     * However, for time grouping, it is required to localize and format the
     * displayed data properly.
     * 
     * @param wsResult
     *            result record returned by the back end
     * @param wsReportToExecute
     *            report that was executed on the back end
     * @return a "UI ready" object to be displayed to the end user
     */
    protected IReportSummaryResult createReportSummaryResult(SummaryResult wsResult, Report wsReportToExecute) {
        if (wsResult == null) {
            throw new NullPointerException("Current result cannot be null");
        }

        if (wsReportToExecute == null) {
            throw new NullPointerException("Report to execute cannot be null");
        }

        IReportSummaryResult result = null;
        if (TIME_GROUPINGS.contains(wsReportToExecute.getSummaryType())) {
            result = new ReportSummaryResultDateGroupingImpl(wsResult);
        } else {
            result = new ReportSummaryResultImpl(wsResult);
        }
        return result;
    }

    /**
     * This action choose the results page to display based on the type of
     * report to be run. If the report definition indicates a grouping by time,
     * then the action should take the user to a specific time grouping page.
     * Similarly for detail results (without grouping), a detail page outcome
     * can be returned. All other groupings are displayed in a generic grouping
     * page.
     * 
     * @see com.bluejungle.destiny.inquirycenter.report.IReportExecutor#executeReport()
     */
    public String executeReport() {
        this.load();
        uiReport.setRunTime(Calendar.getInstance().getTime());
        
        String outcome = null;
        if (this.wrappedReport != null) {
            Report reportToExecute = getWSReportToExecute();
            outcome = (String) GROUPING_TO_OUTCOME.get(reportToExecute.getSummaryType());
        }

        if (outcome == null) {
            outcome = OUTCOME_ERROR;
        }
//        return outcome;
        return "reportExecution";
    }
    
    /**
     * This action first retrieves the current row of the data, and calls the 
     * reporter API for the log details, and then redirects to the page for 
     * log details (singleLogDetailResult.jsp).  OUTCOME_SINGLE_LOG_RESULT is 
     * the navigation case, please see faces-config.xml.
     * 
     * @return the navigation case that redirects to the log details page
     */
    public String getLogDetail(){
        String outcome = null;
        Map requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String value = (String) requestParams.get(LOG_ID_PARAM);
        Long logId = new Long(value);
        ReportExecutionServiceStub executionService = null;
        try {
            executionService = getReportExecutionService();
            Report wsReport = this.lastExecutedReport;
            try {
                LogDetailResult wsLogDetailResult = executionService.getLogDetail(wsReport, logId.longValue());
                ActivityDetailResult wsActivityDetailResult = wsLogDetailResult.getData();
                IReportDetailResult logDetailResult;
                if (wsActivityDetailResult instanceof PolicyActivityCustomResult) {
                    logDetailResult = new LogDetailResultImpl((PolicyActivityCustomResult)wsActivityDetailResult);
                } else {
                    logDetailResult = new LogDetailResultImpl((DocumentActivityCustomResult)wsActivityDetailResult);
                }
                this.myCurrentDetailRow = logDetailResult;
            } catch (UnknownEntryFault e) {
                log.error(e.getMessage(), e);
            } catch (ExecutionFault e) {
                log.error(e.getMessage(), e);
            } catch (AccessDeniedFault e) {
                log.error(e.getMessage(), e);
            } catch (ServiceNotReadyFault e) {
                log.error(e.getMessage(), e);
            } catch (RemoteException e) {
                log.error(e.getMessage(), e);
            }
        } catch (AxisFault e) {
            log.error(e.getMessage(), e);
        }
        outcome = OUTCOME_SINGLE_LOG_RESULT;
        return outcome;
    }
 
    /**
     * Returns the first results from the report execution
     * 
     * @return a collection containing the first results from the report
     *         execution
     */
    protected List fetchResults(ReportState resultState, int fetchSize) {
        ReportExecutionServiceStub executionService = null;
        List newResults = new ArrayList();
        try {
            executionService = getReportExecutionService();
            setLastExecutedReport(getWSReportToExecute());
            Report wsReport = this.lastExecutedReport;
            try {
                ReportResult wsReportExecutionResult = null;
                if (resultState == null) {
                    wsReportExecutionResult = executionService.executeReport(wsReport, fetchSize, getMaxDisplayResults().intValue());
                    this.dataModel.setTotalRowCount((new Long(wsReportExecutionResult.getAvailableRowCount())).intValue());
                } else {
                    wsReportExecutionResult = executionService.getNextResultSet(resultState, fetchSize);
                }
                final long availableRowCount = wsReportExecutionResult.getAvailableRowCount();
                final long totalRowCount = wsReportExecutionResult.getTotalRowCount();
                if (totalRowCount > 0) {
                    if (ReportSummaryType.None.equals(wsReport.getSummaryType())) {
                        setResultsStatistics(new ResultsStatisticsImpl(availableRowCount, totalRowCount));
                        ReportDetailResult wsDetailResults = (ReportDetailResult) wsReportExecutionResult;
                        DetailResultList wsDetailResultsList = wsDetailResults.getData();
                        if (wsDetailResultsList != null) {
                            ActivityDetailResult[] wsResults = wsDetailResultsList.getResults();
                            if (wsResults != null) {
                                int size = wsResults.length;
                                for (int index = 0; index < size; index++) {
                                    ActivityDetailResult currentWsResult = wsResults[index];
                                    IReportDetailResult uiDetailResult = null;
                                    if (currentWsResult instanceof PolicyActivityDetailResult) {
                                        uiDetailResult = new ReportDetailResultImpl((PolicyActivityDetailResult) currentWsResult);
                                    } else {
                                        //If has to be tracking result
                                        uiDetailResult = new ReportDetailResultImpl((DocumentActivityDetailResult) currentWsResult);
                                    }
                                    newResults.add(uiDetailResult);
                                }
                            }
                        }
                    } else {
                        ReportSummaryResult wsSummaryResults = (ReportSummaryResult) wsReportExecutionResult;
                        SummaryResultList resultList = wsSummaryResults.getData();
                        if (resultList != null) {
                            SummaryResult[] wsResults = resultList.getResults();
                            if (wsResults != null) {
                                int size = wsResults.length;
                                for (int index = 0; index < size; index++) {
                                    SummaryResult currentWsResult = wsResults[index];
                                    IReportSummaryResult uiSummaryResult = createReportSummaryResult(currentWsResult, wsReport);
                                    newResults.add(uiSummaryResult);
                                }
                            }
                            setResultsStatistics(new SummaryResultsStatisticsImpl(availableRowCount, totalRowCount, resultList.getMinCount(), resultList.getMaxCount(), resultList.getTotalCount()));
                        }
                    }
                } else {
                    setResultsStatistics(new SummaryResultsStatisticsImpl(0, 0, 0, 0, 0));
                }
                setCurrentState(wsReportExecutionResult.getState());
            } catch (AccessDeniedFault e) {
                //Session timeout - logout the user
                AppContext.getContext().releaseContext();
                FacesContext.getCurrentInstance().renderResponse();
            } catch (ServiceNotReadyFault e) {
                MessageUtil.addMessage(MessageUtil.getInquiryCenterResourceBundle(), REPORT_EXECUTION_ERROR_SERVICE_NOT_READY_BUNDLE_KEY, FacesMessage.SEVERITY_ERROR, new Object[] { getReportName() });
                getLog().error("Error during report execution", e);
            } catch (InvalidArgumentFault | UnknownEntryFault| ExecutionFault | RemoteException e) {
                MessageUtil.addMessage(MessageUtil.getInquiryCenterResourceBundle(), REPORT_EXECUTION_ERROR_REMOTE_ISSUE_BUNDLE_KEY, FacesMessage.SEVERITY_ERROR, new Object[] { getReportName() });
                getLog().error("Error during report execution", e);
            }
        } catch (AxisFault e) {
            MessageUtil.addMessage(MessageUtil.getInquiryCenterResourceBundle(), REPORT_EXECUTION_ERROR_REMOTE_ISSUE_BUNDLE_KEY, FacesMessage.SEVERITY_ERROR, new Object[] { getReportName() });
            getLog().error("Error during report execution", e);
        }
        return newResults;
    }

    /**
     * This function assigns the appropriate summary type for time based on the
     * time period expressed in the query. For now, only two time groupings are
     * available : days or months. The following logic is used to figure out
     * which one to pick.
     * 
     * 1) if the query has no time period specification, then the query is month
     * based over the last 12 months before today.
     * 
     * 2) If the query has an end time specified, and if the end time is greater
     * than today, then today is considered as the end time.
     * 
     * 3) If the time period is partial (only start or end time), consider today
     * as the end time (if no end time) and a year ago as the begin time (if no
     * begin time)
     * 
     * 4) If the time period spans accross more than 31 days, group by months,
     * if less than 31 days, group by days.
     * 
     * @param reportToExecute
     *            report to execute
     *  
     */
    protected void setAppropriateTimeSummary(Report reportToExecute) {
        ReportSummaryType result = null;
        boolean hadBegin = false;
        boolean hadEnd = false;
        Calendar begin = reportToExecute.getBeginDate();
        Calendar end = reportToExecute.getEndDate();

        Calendar now = Calendar.getInstance();
        if (begin != null) {
            hadBegin = true;
        }
        if (end != null) {
            hadEnd = true;
        }
        if (end == null || end.after(now)) {
            end = now;
        }

        if (begin == null) {
            begin = Calendar.getInstance();
            begin.setTimeInMillis(end.getTimeInMillis());
            begin.add(Calendar.YEAR, -1);
        }

        reportToExecute.setBeginDate(begin);
        reportToExecute.setEndDate(end);

        Calendar tmpCal = Calendar.getInstance();
        tmpCal.setTimeInMillis(end.getTimeInMillis());
        tmpCal.add(Calendar.YEAR, -1);
        //See if gap is more than one year. If yes, then we need to see what
        // was specified. Based on that decision is to trim from beginning or
        // end. Either way, the final query time window won't exceed one year.
        if (tmpCal.after(begin)) {
            if (hadBegin && hadEnd) {
                //Move the begin one year before the end
                begin = Calendar.getInstance();
                begin.setTimeInMillis(now.getTimeInMillis());
                begin.add(Calendar.YEAR, -1);
            } else if (hadBegin && !hadEnd) {
                //Move the end one year after the begin
                end = Calendar.getInstance();
                end.setTimeInMillis(begin.getTimeInMillis());
                end.add(Calendar.YEAR, +1);
            }
        }
        //See if the gap is more than one month.
        tmpCal.setTimeInMillis(end.getTimeInMillis());
        tmpCal.add(Calendar.MONTH, -1);
        if (tmpCal.before(begin) || tmpCal.equals(begin)) {
            reportToExecute.setSummaryType(ReportSummaryType.TimeDays);
        } else {
            reportToExecute.setSummaryType(ReportSummaryType.TimeMonths);
        }
    }

    /**
     * Returns the action column name
     * 
     * @return the action column name
     */
    public String getActionColumnName() {
        return ACTION_COLUMN_NAME;
    }

    /**
     * Returns the application column name
     * 
     * @return the application column name
     */
    public String getApplicationColumnName() {
        return APPLICATION_COLUMN_NAME;
    }

    /**
     * Returns the current report sort specification based on the UI sort
     * settings.
     * 
     * @return a report sort specification based on the current UI settings.
     */
    protected ReportSortSpec getCurrentSortSpec() {
        //Puts a default sort specification first
        ReportSortSpec sortSpec = new ReportSortSpec();
        sortSpec.setField((ReportSortFieldName) COLUMN_2_SORT_SPEC_FIELD_NAME.get(getSortColumnName()));

        if (isSortAscending()) {
            sortSpec.setDirection(SortDirection.Ascending);
        } else {
            sortSpec.setDirection(SortDirection.Descending);
        }
        return sortSpec;
    }

    /**
     * Returns the name of the count column
     * 
     * @return the name of the count column
     */
    public String getCountColumnName() {
        return COUNT_COLUMN_NAME;
    }

    /**
     * Returns the last known report state
     * 
     * @return the last known report state
     */
    protected ReportState getCurrentState() {
        return this.currentState;
    }

    /**
     * Returns the location of the report execution service
     * 
     * @return the location of the report execution service
     */
    protected String getDataLocation() {
        return this.dataLocation;
    }

    /**
     * Returns the extensible data model
     * 
     * @return the extensible data model
     */
    protected ExtensibleDataModel getDataModel() {
        return this.dataModel;
    }

    /**
     * Returns the name of the date column name
     * 
     * @return the name of the date column name
     */
    public String getDateColumnName() {
        return DATE_COLUMN_NAME;
    }

    /**
     * Returns the actual report object that was executed last.
     * 
     * @return the actual report object that was executed last.
     */
    public Report getLastExecutedReport() {
        return this.lastExecutedReport;
    }

    /**
     * Returns the log object
     * 
     * @return the log object
     */
    private Log getLog() {
        return this.log;
    }

    /**
     * Returns the number of records to pre-fetch from the server at one time
     * 
     * @return the number of records to pre-fetch from the server at one time
     */
    protected Integer getFetchSize() {
        return this.fetchSize;
    }

    /**
     * Returns the name of the "from resource" column
     * 
     * @return the name of the "from resource" column
     */
    public String getFromResourceColumnName() {
        return FROM_RESOURCE_COLUMN_NAME;
    }

    /**
     * Returns the value of the "grouped value" parameter, and decodes it from a
     * URL parameter into an "internal" value
     * 
     * @param paramMap
     *            request parameter map
     * @return the decoded grouped value
     */
    protected String getGroupedValue(Map paramMap) {
        String result = null;
        if (paramMap != null) {
            final String urlValue = (String) paramMap.get(GROUPED_VALUE_REQ_PARAM);
            try {
                result = URLDecoder.decode(urlValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                result = urlValue;
            }
        }
        return result;
    }

    /**
     * Returns the name of the host column name
     * 
     * @return the name of the host column name
     */
    public String getHostColumnName() {
        return HOST_COLUMN_NAME;
    }

    /**
     * Returns the inquiry center resource bundle.
     * 
     * @return the inquiry center resource bundle.
     */
    protected ResourceBundle getInquiryCenterResourceBundle() {
        return MessageUtil.getInquiryCenterResourceBundle();
    }

    /**
     * Returns the last report execution id
     * 
     * @return the last report execution id
     */
    protected Long getLastReportExecutionId() {
        return this.lastExecutionId;
    }

    /**
     * Returns the maximum number of results that could be displayed for this
     * query
     * 
     * @return the maximum number of results that could be displayed for this
     *         query
     */
    protected Integer getMaxDisplayResults() {
        return (this.maxDisplayResults);
    }

    /**
     * Returns the number of results to display per page
     * 
     * @return the number of results to display per page
     */
    public Integer getPageSize() {
        return this.pageSize;
    }

    /**
     * Returns the policy column name
     * 
     * @return the policy column name
     */
    public String getPolicyColumnName() {
        return POLICY_COLUMN_NAME;
    }

    /**
     * Returns the policy decision column name
     * 
     * @return the policy decision column name
     */
    public String getPolicyDecisionColumnName() {
        return POLICY_DECISION_COLUMN_NAME;
    }

    /**
     * Returns the number of records to display when going to the printable view
     * 
     * @return the number of records to display when going to the printable view
     */
    public Integer getPrintSize() {
        return this.printSize;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportExecutor#getReport()
     */
    public IReport getReport() {
        return this.uiReport;
    }

    /**
     * Returns the current report execution id extracted from the request
     * 
     * @return the current report execution id extracted from the request
     */
    protected Long getReportExecutionId() {
        Map requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String value = (String) requestParams.get(EXECUTION_ID_REQ_PARAM);
        Long result = null;
        if (value != null && !NULL_STRING.equals(value) && !FIREFOX_NULL_STRING.equals(value)) {
            try {
                result = new Long(value);
            } catch (NumberFormatException e) {
                //Should never happen
                getLog().warn("Error when getting report execution id.", e);
                result = null;
            }
        }
        return result;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportExecutor#getReportName()
     */
    public String getReportName() {
        return this.reportName;
    }

    /**
     * Returns a report execution service client
     * 
     * @return a report execution service client
     */
    protected ReportExecutionServiceStub getReportExecutionService() throws AxisFault {
        if(this.reportExecutionService == null) {
            String serviceLocation = getDataLocation() + REPORT_EXECUTION_SERVICE_SUFFIX;
            this.reportExecutionService = new ReportExecutionServiceStub(serviceLocation);

        }

        return this.reportExecutionService;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportExecutor#getResults()
     */
    public DataModel getResults() {
        return getDataModel();
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportExecutor#getResultsStatistics()
     */
    public IResultsStatistics getResultsStatistics() {
        return this.resultsStats;
    }

    /**
     * Returns the results table component
     * 
     * @return the results table component
     */
    public UIData getResultTable() {
        return this.resultTable;
    }

    /**
     * Returns the "to resource" column name
     * 
     * @return the "to resource" column name
     */
    public String getToResourceColumnName() {
        return TO_RESOURCE_COLUMN_NAME;
    }

    /**
     * Returns the name of the sort column name
     * 
     * @return the name of the sort column name
     */
    public String getSortColumnName() {
        return this.sortStateMgr.getSortFieldName();
    }

    /**
     * Returns the sort state manager
     * 
     * @return the sort state manager
     */
    private ISortStateMgr getSortStateMgr() {
        return this.sortStateMgr;
    }

    /**
     * Returns the user column name
     * 
     * @return the user column name
     */
    public String getUserColumnName() {
        return USER_COLUMN_NAME;
    }
    
    /**
     * Returns the logging level column name
     * 
     * @return the logging level column name
     */
    public String getLoggingLevelColumnName() {
        return LOGGING_LEVEL_COLUMN_NAME;
    }
    
    /**
     * Returns the last execution time of the report
     * 
     * @return the last execution time of the report
     */
    public String getReportRunTime(){
        return DateFormat.getDateTimeInstance().format(this.uiReport.getRunTime());
    }

    /**
     * Returns the web service report object to execute. Ideally, we should not
     * know about the details of the report here. But to avoid going through
     * another round of conversion, we use the package visibility to access the
     * real wrapped report. The returned object is partially a clone of the real
     * report, because adjustements to the report definition may have to be made
     * in case the report time period is not consistent with the grouping used.
     * 
     * @return a web service report object ready for use
     */
    protected Report getWSReportToExecute() {
        ReportImpl reportDefinition = (ReportImpl) getReport();
        Report wsReportDef = reportDefinition.getWrappedReport();

        //Trims the useless object. They cause serialization problems
        if (wsReportDef.getActions() != null) {
            ActionType[] array = wsReportDef.getActions().getActions();
            if (array == null) {
                wsReportDef.setActions(null);
//            } else {
//                // this is added due to Bug4213, we treat EMBED as part of copy
//                for (int i = 0; i < array.length; i++){
//                    if (array[i].equals(ActionEnumType.ACTION_COPY.getName())){
//                        String[] tempArray = new String[array.length+1];
//                        System.arraycopy(array, 0, tempArray, 0, array.length);
//                        tempArray[array.length] = ActionEnumType.ACTION_EMBED.getName();
//                        wsReportDef.getActions().setActions(tempArray);
//                    }
//                }
            }
        }
        if (wsReportDef.getEffects() != null) {
            EffectType[] array = wsReportDef.getEffects().getValues();
            if (array == null) {
                wsReportDef.setEffects(null);
            }
        }
        if (wsReportDef.getPolicies() != null) {
            String[] array = wsReportDef.getPolicies().getValues();
            if (array == null) {
                wsReportDef.setPolicies(null);
            }
        }
        if (wsReportDef.getResourceNames() != null) {
            String[] array = wsReportDef.getResourceNames().getValues();
            if (array == null) {
                wsReportDef.setResourceNames(null);
            }
        }
        if (wsReportDef.getUsers() != null) {
            String[] array = wsReportDef.getUsers().getValues();
            if (array == null) {
                wsReportDef.setUsers(null);
            }
        }

        wsReportDef.setSortSpec(getCurrentSortSpec());
        Report reportToExecute = wsReportDef;

        //Special case for reports grouped by time. We don't want to change the
        // user report definition here, but simply adapt the report on the fly
        if (TIME_GROUPINGS.contains(wsReportDef.getSummaryType())) {
            reportToExecute = new Report();
            reportToExecute.setTitle(wsReportDef.getTitle());
            reportToExecute.setDescription(wsReportDef.getDescription());
            reportToExecute.setShared(wsReportDef.getShared());
            reportToExecute.setActions(wsReportDef.getActions());
            reportToExecute.setEffects(wsReportDef.getEffects());
            reportToExecute.setPolicies(wsReportDef.getPolicies());
            reportToExecute.setResourceNames(wsReportDef.getResourceNames());
            reportToExecute.setSortSpec(wsReportDef.getSortSpec());
            reportToExecute.setTarget(wsReportDef.getTarget());
            reportToExecute.setUsers(wsReportDef.getUsers());
            reportToExecute.setLoggingLevel(wsReportDef.getLoggingLevel());

            Calendar beginDate = null;
            Calendar endDate = null;
            Calendar defBeginDate = wsReportDef.getBeginDate();
            Calendar defEndDate = wsReportDef.getEndDate();
            if (defBeginDate != null) {
                beginDate = Calendar.getInstance();
                beginDate.setTimeInMillis(defBeginDate.getTimeInMillis());
                reportToExecute.setBeginDate(beginDate);
            }
            if (defEndDate != null) {
                endDate = Calendar.getInstance();
                endDate.setTimeInMillis(defEndDate.getTimeInMillis());
                reportToExecute.setEndDate(endDate);
            }
            setAppropriateTimeSummary(reportToExecute);
        }
        return reportToExecute;
    }

    /**
     * This function returns if the back button has been pressed by the user. If
     * the current report execution id is lower than the last known report
     * execution id, then the back button was pressed.
     * 
     * @return true if the back button was pressed, false otherwise
     */
    protected boolean isBackButtonNavigation() {
        Long lastId = getLastReportExecutionId();
        Long currentId = getReportExecutionId();
        return (lastId != null && currentId != null && currentId.compareTo(lastId) < 0);
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.faces.ILoadable#isLoaded()
     */
    public boolean isLoaded() {
        return this.loaded;
    }

    /**
     * Returns whether there is a report execution saved with a given key
     * 
     * @param asOf
     *            key to saved the report state execution
     * @return true if a report execution state exists, false otherwise
     */
    protected boolean isReportExecutionStateSaved(Long asOf) {
        return this.reportExecutionStates.containsKey(asOf);
    }

    /**
     * Returns if the sort is ascending or not
     * 
     * @return true if the sort is ascending, false otherwise
     */
    public boolean isSortAscending() {
        return this.sortStateMgr.isSortAscending();
    }

    /**
     * Loads the data into the data model. This function figures out if the data
     * model needs to be extented (as the user navigates forward) or if it needs
     * to be reloaded completely (if sort spec has changed for example). Also,
     * special care needs to be given to the fetch size, to guarantee that the
     * page is filled up properly.
     * 
     * @see com.bluejungle.destiny.webui.framework.faces.ILoadable#load()
     */
    public void load() {
        getSortStateMgr().saveState();
        if (needNewDataFetch()) {
            reset();
            int nbRecordsPerPage = getPageSize().intValue();
            int initialFetchSize = getFetchSize().intValue();
            if (initialFetchSize < nbRecordsPerPage) {
                initialFetchSize = nbRecordsPerPage;
            }
            this.dataModel.addNewRecords(fetchResults(getCurrentState(), initialFetchSize));
            this.loaded = true;
        } else {
            UIData table = getResultTable();
            if (table != null) {
                int lastRow = table.getFirst() + table.getRows();
                if (lastRow >= this.dataModel.getRealNbOfRows() && this.dataModel.getRealNbOfRows() < this.dataModel.getRowCount()) {
                    //We need to load up some more rows in the data set, as we
                    // are
                    // dangerously approaching the end of the list.
                    this.dataModel.addNewRecords(fetchResults(getCurrentState(), getFetchSize().intValue()));
                }
            }
        }

        Report report = null;
        final Long executionId = getReportExecutionId();
        if (isBackButtonNavigation()) {
            if (isReportExecutionStateSaved(executionId)) {
                this.loaded = true;
                ReportExecutionState oldState = (ReportExecutionState) this.reportExecutionStates.get(executionId);
                this.dataModel = (ExtensibleDataModel) oldState.getDataModel();
                setSortStateMgr(oldState.getSortStateMgr());
                report = oldState.getReport();
                Report reportClone = ReportExecutorUtil.clone(report);
                setLastExecutedReport(reportClone);
                setResultsStatistics(oldState.getResultStatistics());
                setResultTable(oldState.getResultTable());
                this.uiReport = new ReportImpl(reportClone);
                setReportName(oldState.getReportName());
                setCurrentState(oldState.getReportState());
            }
        } else {
            report = ReportExecutorUtil.clone(getLastExecutedReport());
            if (report != null) {
//                if (!ReportSummaryType.None.equals(report.getSummaryType())) {
//                    saveReportExecutionState(executionId);
//                }
            }
        }
        setLastExecutionId(executionId);
    }

    /**
     * This function is called before going to the printable view. In the case
     * of the printable view, more records have to be fetched from the back end.
     * However, based on how many records have already been fetched by the user
     * when navigating the list, it may not be necessary to fetch all of them.
     * Only the remaining items should be fetched. If enough items have been
     * fetched already (unlikely, but possible), then no action is required.
     */
    public void loadForPrint() {
        int fetchedRows = this.dataModel.getRealNbOfRows();
        int rowsForPrint = getPrintSize().intValue();
        if (fetchedRows < rowsForPrint) {
            int totalExpectedRows = this.dataModel.getRowCount();
            if (fetchedRows < totalExpectedRows) {
                this.dataModel.addNewRecords(fetchResults(getCurrentState(), rowsForPrint - fetchedRows));
            }
        }
    }

    /**
     * This function is called when the user navigates from one of the summary
     * links exposed in the grouping pages. When this action listener is
     * invoked, the currently executed report is transformed so that it no
     * longer has grouping. However, other query specifications remain.
     * 
     * @param event
     *            UI event fired by the command link object
     */
    public void navigateToAllDetails(ActionEvent event) {
        Report currentReport = getLastExecutedReport();
        if (currentReport != null) {
            currentReport.setSummaryType(ReportSummaryType.None);
            this.sortStateMgr.setSortFieldName(DATE_COLUMN_NAME);
            currentReport.setSortSpec(this.getCurrentSortSpec());
            setReportToExecute(currentReport, getReportName());
        }
    }

    /**
     * This function is called when the user clicks on a summary record link. In
     * this case, the user navigates to the detail view (without any grouping),
     * but the value of the current field is also added to the query constraint.
     * 
     * @param event
     */
    public void navigateToRecordDetails(ActionEvent event) {
        Report currentReport = ReportExecutorUtil.clone(getLastExecutedReport());
        if (currentReport != null) {
            Map paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String groupedValue = getGroupedValue(paramMap);
            String groupedType = (String) paramMap.get(GROUPED_TYPE_REQ_PARAM);
            if (groupedValue != null) {
                if (ReportSummaryType.Policy.getValue().equals(groupedType)) {
                    prepareReportExecutionForPolicyDetail(currentReport, groupedValue);
                } else if (ReportSummaryType.Resource.getValue().equals(groupedType)) {
                    prepareReportExecutionForResourceDetail(currentReport, groupedValue);
                } else if (ReportSummaryType.TimeDays.getValue().equals(groupedType)) {
                    prepareReportExecutionForTimeInDaysDetail(currentReport, groupedValue);
                } else if (ReportSummaryType.TimeMonths.getValue().equals(groupedType)) {
                    prepareReportExecutionForTimeInMonthsDetail(currentReport, groupedValue);
                } else if (ReportSummaryType.User.getValue().equals(groupedType)) {
                    prepareReportExecutionForUserDetail(currentReport, groupedValue);
                } else {
                    getLog().warn("Invalid request object: '" + GROUPED_TYPE_REQ_PARAM + "' request parameter is not valid. (Value was '" + groupedType + "')");
                }
                currentReport.setSummaryType(ReportSummaryType.None);
                this.sortStateMgr.setSortFieldName(DATE_COLUMN_NAME);
                currentReport.setSortSpec(this.getCurrentSortSpec());
                setReportToExecute(currentReport, getReportName());
            }
        }
    }

    /**
     * Returns true if the report needs to be re-executed from scratch
     * 
     * @return true if the report should be executed from the beginning, false
     *         otherwise
     */
    protected boolean needNewDataFetch() {
        return (!isLoaded() || this.sortStateMgr.isSortStateChanged());
    }

    /**
     * Prepares the last executed report objected to return the previously
     * returned details against a fixed Policy.
     * 
     * @param currentReport
     * @param groupedValue
     */
    protected void prepareReportExecutionForPolicyDetail(Report currentReport, String groupedValue) {
        StringList detailForPolicySetting = new StringList();
        detailForPolicySetting.setValues(new String[] { groupedValue });
        currentReport.setPolicies(detailForPolicySetting);
    }

    /**
     * Prepares the last executed report objected to return the previously
     * returned details against a fixed Resource Name.
     * 
     * @param currentReport
     * @param groupedValue
     */
    protected void prepareReportExecutionForResourceDetail(Report currentReport, String groupedValue) {
        StringList detailForResourceNameSetting = new StringList();
        detailForResourceNameSetting.setValues(new String[] { groupedValue });
        currentReport.setResourceNames(detailForResourceNameSetting);
    }

    /**
     * Prepares the last executed report objected to be rerun against a fixed
     * day.
     * 
     * @param currentReport
     *            current report to be executed
     * @param dayTimeStamp
     *            timestamp of the beginning of the current day
     */
    protected void prepareReportExecutionForTimeInDaysDetail(Report currentReport, String dayTimeStamp) {
        long timeInMillis = (new Long(dayTimeStamp)).longValue();
        Calendar beginDate = Calendar.getInstance();
        beginDate.setTimeInMillis(timeInMillis);

        // For day-based reports, the duration is a day,
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(timeInMillis);
        endDate.add(Calendar.DATE, 1);

        // Setup the report with the start/end dates calculated above:
        currentReport.setBeginDate(beginDate);
        currentReport.setEndDate(endDate);
    }

    /**
     * Prepares the last executed report objected to be rerun against a fixed
     * Month.
     * 
     * @param currentReport
     *            report currently executed
     * @param monthTimeStamp
     *            timestamp of the beginning of the current month
     */
    protected void prepareReportExecutionForTimeInMonthsDetail(Report currentReport, String monthTimeStamp) {
        long timeInMillis = (new Long(monthTimeStamp)).longValue();
        Calendar beginDate = Calendar.getInstance();
        beginDate.setTimeInMillis(timeInMillis);

        // For month-based reports, the duration is one month,
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(timeInMillis);
        endDate.add(Calendar.MONTH, 1);
        currentReport.setBeginDate(beginDate);
        currentReport.setEndDate(endDate);
    }

    /**
     * Prepares the last executed report objected to return the previously
     * returned details against a fixed User.
     * 
     * @param currentReport
     * @param groupedValue
     */
    protected void prepareReportExecutionForUserDetail(Report currentReport, String groupedValue) {
        String qualifiedGroupedValue = UserComponentEntityResolver.createUserQualification(groupedValue);
        StringList detailForUserSetting = new StringList();
        detailForUserSetting.setValues(new String[] { qualifiedGroupedValue });
        currentReport.setUsers(detailForUserSetting);
    }

    /**
     * Returns the appropriate outcome for the printable view. Different
     * printable view are displayed based on the nature of the report.
     * 
     * @return an outcome for the printable view
     */
    public String printResults() {
        String outcome = null;
        if (this.wrappedReport != null) {
            ReportGroupByType groupType = this.uiReport.getGroupByType();
            //No other print support required for other grouping
            if (ReportGroupByType.NONE.equals(groupType)) {
                outcome = OUTCOME_PRINT_DETAIL_RESULTS;
            } else {
                outcome = OUTCOME_ERROR;
            }
        } else {
            outcome = OUTCOME_ERROR;
        }
        return outcome;
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.faces.ILoadable#reset()
     */
    public void reset() {
        this.loaded = false;
        UIData table = getResultTable();
        if (table != null) {
            //Resets the table to read from the beginning of the dataset
            table.setFirst(0);
        }
        setResultsStatistics(null);
        setResultTable(null);
        this.dataModel = new ExtensibleDataModel();
        setCurrentState(null);
        this.myCurrentRow = null;
        this.myCurrentDetailRow = null;
    }

    /**
     * This function saves the report execution state. It saves the data model,
     * the report object and the sort state
     * 
     * @param asOf
     *            timestamp associated with the report state
     */
    protected void saveReportExecutionState(Long asOf) {
        if (asOf == null) {
            throw new NullPointerException("timestamp value cannot be null");
        }
        //Extract the current state
        ISortStateMgr currentSortStateMgr = getSortStateMgr();
        ISortStateMgr newSortStateMgr = new SortStateMgrImpl();
        newSortStateMgr.setSortAscending(currentSortStateMgr.isSortAscending());
        newSortStateMgr.setSortFieldName(currentSortStateMgr.getSortFieldName());
        newSortStateMgr.saveState();
        newSortStateMgr.saveState(); //To make sure it is not changed
        setSortStateMgr(newSortStateMgr);
        final DataModel currentDataModel = getDataModel();
        final Report currentReport = ReportExecutorUtil.clone(getLastExecutedReport());
        final String currentReportName = getReportName();
        ReportState currentState = getCurrentState();
        ReportState newCurrentState = null;
        if (currentState != null) {
            Object[] srcArray = currentState.getState();
            if (srcArray != null) {
                int len = srcArray.length;
                Object[] targetArray = new Object[len];
                System.arraycopy(srcArray, 0, targetArray, 0, len);
                newCurrentState = new ReportState();
                newCurrentState.setState(targetArray);
            }
        }
        //Save the current state
        this.reportExecutionStates.put(asOf, new ReportExecutionState(currentDataModel, currentReport, currentReportName, getResultTable(), getResultsStatistics(), currentSortStateMgr, newCurrentState));
    }

    /**
     * Sets the current report state
     * 
     * @param newState
     *            new report state to set
     */
    protected void setCurrentState(ReportState newState) {
        this.currentState = newState;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportExecutor#setDataLocation(java.lang.String)
     */
    public void setDataLocation(String location) {
        this.dataLocation = location;
    }

    /**
     * Sets the pre-fetch size for the result records
     * 
     * @param newFetchSize
     *            new fetch size to use
     */
    public void setFetchSize(Integer newFetchSize) {
        this.fetchSize = newFetchSize;
    }

    /**
     * Sets the last executed report
     * 
     * @param wsLastExecuted
     *            last executed report to set
     */
    protected void setLastExecutedReport(Report wsLastExecuted) {
        this.lastExecutedReport = wsLastExecuted;
    }

    /**
     * Sets the last report execution id
     * 
     * @param newId
     *            id to set
     */
    protected void setLastExecutionId(Long newId) {
        this.lastExecutionId = newId;
    }

    /**
     * Sets the maximum number of results that can ever be displayed on one
     * query. This number corresponds to the maximum number of results that will
     * be cached on the back-end when the query executes.
     * 
     * @param newMaxDisplayResult
     *            new value to set
     */
    public void setMaxDisplayResults(Integer newMaxDisplayResult) {
        if (newMaxDisplayResult == null) {
            throw new NullPointerException("Max display results cannot be null");
        }
        this.maxDisplayResults = newMaxDisplayResult;
    }

    /**
     * Sets the page size for the result records. The page size represents the
     * number of records to be displayed in each page.
     * 
     * @param newPageSize
     *            new page size to set
     */
    public void setPageSize(Integer newPageSize) {
        if (newPageSize == null) {
            throw new NullPointerException("The page size cannot be null");
        }
        this.pageSize = newPageSize;
    }

    /**
     * Sets the maximum number of records to be displayed in the printable view
     * 
     * @param newPrintSize
     *            new maximum number of records to set
     */
    public void setPrintSize(Integer newPrintSize) {
        if (newPrintSize == null) {
            throw new NullPointerException("The print size cannot be null");
        }
        this.printSize = newPrintSize;
    }

    /**
     * Sets the name of the current report to execute
     * 
     * @param newName
     *            new name to set
     */
    protected void setReportName(String newName) {
        this.reportName = newName;
    }

    /**
     * Upon calling this function, it is assumed that a brand new report
     * execution starts. Therefore, the state of the bean is reset.
     * 
     * @see com.bluejungle.destiny.inquirycenter.report.IReportExecutor#setReportToExecute(com.bluejungle.destiny.types.report.v1.Report)
     */
    public void setReportToExecute(final Report newReport, final String uiName) {
        if (newReport == null) {
            throw new NullPointerException("new report cannot be null");
        }
        if (uiName == null) {
            throw new NullPointerException("report display name cannot be null");
        }

        Report reportToSet = newReport;
        String reportNameToSet = uiName;
        reset();
        if (!ReportSummaryType.None.equals(newReport.getSummaryType()) && isBackButtonNavigation()) {
            Long execId = getReportExecutionId();
            if (isReportExecutionStateSaved(execId)) {
                this.loaded = true;
                ReportExecutionState oldState = (ReportExecutionState) this.reportExecutionStates.get(execId);
                this.dataModel = (ExtensibleDataModel) oldState.getDataModel();
                setSortStateMgr(oldState.getSortStateMgr());
                reportToSet = oldState.getReport();
                setLastExecutedReport(ReportExecutorUtil.clone(reportToSet));
                reportNameToSet = oldState.getReportName();
                setResultsStatistics(oldState.getResultStatistics());
                setResultTable(oldState.getResultTable());
                setCurrentState(oldState.getReportState());
            }
        }

        Report clonedReport = ReportExecutorUtil.clone(reportToSet);
        this.wrappedReport = clonedReport;
        this.uiReport = new ReportImpl(clonedReport);
        setReportName(reportNameToSet);
        ReportSortSpec sortSpec = newReport.getSortSpec();
        //If a sort specification is specified, set it properly
        if (sortSpec != null) {
            setSortColumnName((String) SORT_SPEC_FIELD_NAME_2_COLUMN.get(sortSpec.getField()));
            if (SortDirection.Ascending.equals(sortSpec.getDirection())) {
                setSortAscending(true);
            } else {
                setSortAscending(false);
            }
        } else {
            setSortColumnName(getDateColumnName());
            setSortAscending(false);
        }
    }

    /**
     * Sets the results statistics
     * 
     * @param newResultsStats
     *            new statistics to set
     */
    protected void setResultsStatistics(IResultsStatistics newResultsStats) {
        this.resultsStats = newResultsStats;
    }

    /**
     * Sets the sort ascending flag
     * 
     * @param newSortAscending
     *            true if the sort should be ascending, false otherwise
     */
    public void setSortAscending(boolean newSortAscending) {
        this.sortStateMgr.setSortAscending(newSortAscending);
    }

    /**
     * Sets the name of the column that should be sorted
     * 
     * @param newSortColumnName
     *            name of the column that should be sorted
     */
    public void setSortColumnName(String newSortColumnName) {
        this.sortStateMgr.setSortFieldName(newSortColumnName);
    }

    /**
     * Sets the sort state manager
     * 
     * @param newSortStateMgr
     *            sort state manager to set
     */
    private void setSortStateMgr(ISortStateMgr newSortStateMgr) {
        this.sortStateMgr = newSortStateMgr;
    }

    /**
     * Sets the result table UI component
     * 
     * @param newResultTable
     *            new result table UI component to set.
     */
    public void setResultTable(UIData newResultTable) {
        this.resultTable = newResultTable;
    }
    
    /**
     * Returns the myCurrentRow.
     * @return the myCurrentRow.
     */
    public IReportDetailResult getMyCurrentRow() {
        return this.myCurrentRow;
    }
    
    /**
     * Sets the myCurrentRow
     * @param myCurrentRow The myCurrentRow to set.
     */
    public void setMyCurrentRow(IReportDetailResult myCurrentRow) {
        this.myCurrentRow = myCurrentRow;
    }

    /**
     * Returns the myCurrentDetailRow.
     * @return the myCurrentDetailRow.
     */
    public IReportDetailResult getMyCurrentDetailRow() {
        return this.myCurrentDetailRow;
    }
    
    /**
     * Sets the myCurrentDetailRow
     * @param myCurrentDetailRow The myCurrentDetailRow to set.
     */
    public void setMyCurrentDetailRow(IReportDetailResult myCurrentDetailRow) {
        this.myCurrentDetailRow = myCurrentDetailRow;
    }

    
    /**
     * This class saves the state of a given report execution
     * 
     * @author ihanen
     */
    protected class ReportExecutionState {

        private DataModel dataModel;
        private Report report;
        private String reportName;
        private UIData resultTable;
        private ReportState reportState;
        private IResultsStatistics resultStatistics;
        private ISortStateMgr sortStateMgr;

        /**
         * Constructor
         * 
         * @param dm
         *            data model
         * @param sort
         *            sort state manager
         */
        public ReportExecutionState(final DataModel dm, final Report report, final String reportName, UIData uiTable, IResultsStatistics stats, final ISortStateMgr sort, final ReportState reportState) {
            this.dataModel = dm;
            this.report = report;
            this.reportName = reportName;
            this.reportState = reportState;
            this.resultTable = uiTable;
            this.resultStatistics = stats;
            this.sortStateMgr = sort;
        }

        /**
         * Returns the data model object
         * 
         * @return the data model object
         */
        protected DataModel getDataModel() {
            return this.dataModel;
        }

        /**
         * Returns the report object
         * 
         * @return the report object
         */
        protected Report getReport() {
            return this.report;
        }

        /**
         * Returns the report name
         * 
         * @return the report name
         */
        protected String getReportName() {
            return this.reportName;
        }

        /**
         * Returns the report state
         * 
         * @return the report state
         */
        protected ReportState getReportState() {
            return this.reportState;
        }

        /**
         * Returns the result statistics
         * 
         * @return the result statistics
         */
        protected IResultsStatistics getResultStatistics() {
            return this.resultStatistics;
        }

        /**
         * Returns the result table
         * 
         * @return the result table
         */
        protected UIData getResultTable() {
            return this.resultTable;
        }

        /**
         * Returns the sort state manager
         * 
         * @return the sort state manager
         */
        protected ISortStateMgr getSortStateMgr() {
            return this.sortStateMgr;
        }
    }

    /**
     * Small LRU cache for the execution states. The class constructor allows
     * setting the cache size.
     * 
     * @author ihanen
     */
    protected class ExecutionStateCache extends LinkedHashMap {

        private final int MAX_SIZE;

        /**
         * Constructor
         * 
         * @param maxSize
         *            maximum cache size
         */
        public ExecutionStateCache(int maxSize) {
            super(15, 0.75F, true);
            MAX_SIZE = maxSize;
        }

        /**
         * Returns true if the oldest entry should be removed
         * 
         * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
         */
        protected boolean removeEldestEntry(Entry eldest) {
            return size() > MAX_SIZE;
        }
    }
    
    /**
     * Returns the mode.
     * @return the mode.
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * Sets the mode
     * @param i The mode to set.
     */
    public void setMode(int i) {
        this.mode = i;
    }
}
