/*
 * Created on Mar 25, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.defaultimpl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.inquirycenter.enumeration.ReportGroupByType;
import com.bluejungle.destiny.inquirycenter.enumeration.ReportTargetUIType;
import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.server.shared.configuration.IActionConfigDO;
import com.bluejungle.destiny.server.shared.configuration.IActionListConfigDO;
import com.bluejungle.destiny.server.shared.configuration.impl.RepositoryConfigurationDO;
import com.bluejungle.destiny.types.report.v1.ReportSummaryType;
import com.bluejungle.domain.action.ActionEnumType;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.configuration.DestinyConfigurationStoreImpl;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.configuration.IDestinyConfigurationStore;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.inquirycenter.report.IReportTransform;

/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/defaultimpl/BirtReportTransform.java#1 $
 */
public class BirtReportTransform implements IReportTransform {

    private static final String SQL_EMPTY = "";
    private static final String SEPERATOR = ".";
    private static final String ALIAS_PREFIX = "t_";

    // actual table names
    private static final String TABLE_NAME_TRACKING_ACTIVITY_LOG = "tracking_activity_log";
    private static final String TABLE_NAME_POLICY_ACTIVITY_LOG = "policy_activity_log";
    private static final String TABLE_NAME_CACHED_USERGROUP = "cached_usergroup";
    private static final String TABLE_NAME_CACHED_POLICY = "cached_policy";
    private static final String TABLE_NAME_CACHED_USERGROUP_MEMBER = "cached_usergroup_member";

    private static final String GROUPBY_POLICY_COLUMN_NAME = "policy_fullname";

    private static final int TITLE_REPORT_NAME_MAX_LENGTH = 30;
    private static final String TITLE_FORMAT = "%1$." + TITLE_REPORT_NAME_MAX_LENGTH + "s  %2$tD  %2$tr";

    //table name alias
    private static final String ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG = 
        ALIAS_PREFIX + TABLE_NAME_TRACKING_ACTIVITY_LOG;
    private static final String ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG = 
        ALIAS_PREFIX + TABLE_NAME_POLICY_ACTIVITY_LOG;
    private static final String ALIAS_TABLE_NAME_CACHED_USERGROUP = 
        ALIAS_PREFIX +  TABLE_NAME_CACHED_USERGROUP;
    private static final String ALIAS_TABLE_NAME_CACHED_POLICY = 
        ALIAS_PREFIX + TABLE_NAME_CACHED_POLICY;
    private static final String ALIAS_TABLE_NAME_CACHED_USERGROUP_MEMBER = 
        ALIAS_PREFIX + TABLE_NAME_CACHED_USERGROUP_MEMBER;

    //column alias for cached_usergroup
    private static final String ALIAS_CACHED_USERGROUP_NAME = 
        ALIAS_TABLE_NAME_CACHED_USERGROUP + SEPERATOR + "name";
    private static final String ALIAS_CACHED_USERGROUP_ORGINAL_ID = 
        ALIAS_TABLE_NAME_CACHED_USERGROUP + SEPERATOR + "original_id";

    //column alias for cached_policy
    private static final String ALIAS_CACHED_POLICY_FULLNAME_STRING = 
        ALIAS_TABLE_NAME_CACHED_POLICY + SEPERATOR + "fullname";

    //column alias for cached_usergroup_member
    private static final String ALIAS_CACHED_USERGROUP_MEMBER_USERID_STRING = 
        ALIAS_TABLE_NAME_CACHED_USERGROUP_MEMBER + SEPERATOR + "userid";
    private static final String ALIAS_CACHED_USERGROUP_MEMBER_GROUP_ID_STRING = 
        ALIAS_TABLE_NAME_CACHED_USERGROUP_MEMBER + SEPERATOR + "groupid";

    //column alias for policy_activity_log
    private static final String ALIAS_PA_USERNAME_STRING = 
        ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG + SEPERATOR + "user_name";
    private static final String ALIAS_PA_USER_ID_STRING = 
        ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG + SEPERATOR + "user_id";
    private static final String ALIAS_PA_ACTION_STRING = 
        ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG + SEPERATOR + "action";
    private static final String ALIAS_PA_FROM_RESOURCE_NAME_STRING =
        ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG + SEPERATOR +  "from_resource_name";
    private static final String ALIAS_PA_HOST_NAME_STRING = 
        ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG + SEPERATOR + "host_name";
    private static final String ALIAS_PA_ENFORCEMENT_STRING =
        ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG + SEPERATOR + "policy_decision"; 
    private static final String ALIAS_PA_LEVEL_STRING = 
        ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG + SEPERATOR + "log_level";
    private static final String ALIAS_PA_DAY = 
        ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG + SEPERATOR + "day_nb";
    private static final String ALIAS_PA_MONTH = 
        ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG + SEPERATOR + "month_nb";

    //column alias for tracking_activity_log
    private static final String ALIAS_TR_USERNAME_STRING = 
        ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG + SEPERATOR + "user_name";
    private static final String ALIAS_TR_USER_ID_STRING = 
        ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG + SEPERATOR + "user_id";
    private static final String ALIAS_TR_ACTION_STRING = 
        ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG + SEPERATOR + "action";
    private static final String ALIAS_TR_FROM_RESOURCE_NAME_STRING =
        ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG + SEPERATOR +  "from_resource_name";
    private static final String ALIAS_TR_HOST_STRING = 
        ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG + SEPERATOR + "host_name";
    private static final String ALIAS_TR_LEVEL_STRING = 
        ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG + SEPERATOR + "log_level";
    private static final String ALIAS_TR_DAY = 
        ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG + SEPERATOR + "day_nb";
    private static final String ALIAS_TR_MONTH = 
        ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG + SEPERATOR + "month_nb";

    //rptdesign file names
    private static final String PA_GROUPBY_REPORT_NAME = "PA_Single_GroupBy";
    private static final String TR_GROUPBY_REPORT_NAME = "TR_Single_GroupBy";
    private static final String PA_GROUPBYRESOURCE_REPORT_NAME = "PA_Single_GroupByResource";
    private static final String TR_GROUPBYRESOURCE_REPORT_NAME = "TR_Single_GroupByResource";
    private static final String PA_GROUPBYTIME_REPORT_NAME = "PA_Single_GroupByTime";
    private static final String TR_GROUPBYTIME_REPORT_NAME = "TR_Single_GroupByTime";
    private static final String PA_DETAILS_TABLE_NAME = "PA_Details_Table";
    private static final String TR_DETAILS_TABLE_NAME = "TR_Details_Table";
    private static final String DESIGN_FILE_EXTENSION = ".rptdesign";

    /**
     * Array of action enumeration
     */
    private static final Map<ActionEnumType, String> ENUM_TO_DB_MAP = new HashMap<ActionEnumType, String>();
    static {
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_CHANGE_ATTRIBUTES, "Ca");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_ABNORMAL_AGENT_SHUTDOWN, "As");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_CHANGE_SECURITY, "Cs");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_COPY, "Co");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_PASTE, "CP");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_DELETE, "De");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_EMBED, "Em");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_MOVE, "Mo");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_PRINT, "Pr");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_OPEN, "Op");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_EDIT, "Ed");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_RENAME, "Rn");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_SEND_EMAIL, "SE");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_SEND_IM, "SI");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_START_AGENT, "Au");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_STOP_AGENT, "Ad");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_AGENT_USER_LOGIN, "Uu");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_AGENT_USER_LOGOUT, "Ud");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_ACCESS_AGENT_CONFIG, "Ac");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_ACCESS_AGENT_LOGS, "Al");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_ACCESS_AGENT_BINARIES, "Ab");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_INVALID_BUNDLE, "Ib");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_BUNDLE_RECEIVED, "Br");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_ACCESS_AGENT_BUNDLE, "Ap");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_EXPORT, "Ex");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_ATTACH, "At");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_RUN, "Ru");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_AVD, "Av");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_MEETING, "Me");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_PRESENCE, "Ps");

        // added 6 more that exists in Reporter GUI
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_SHARE, "Sh");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_RECORD, "Re");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_QUESTION, "Qu");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_VOICE, "Vo");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_VIDEO, "Vi");
        ENUM_TO_DB_MAP.put(ActionEnumType.ACTION_JOIN, "Jo");
    }

    private static final int TEMP_REPORT_INDEX_LIMIT = 100000;

    private static final Log LOG = LogFactory.getLog(BirtReportTransform.class);

    private static int tempReportIndex = 0;

    private final Properties props;

    private IReport currentReport;

    public BirtReportTransform(IReport report) {
        this.currentReport = report;
        this.props = loadProperties();
    }

    protected Properties loadProperties() {
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream("DataSource.properties");
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            LOG.warn(e);
        }
        return props;
    }

    public synchronized int getTempReportIndex() {
        if (tempReportIndex >= TEMP_REPORT_INDEX_LIMIT) {
            tempReportIndex = 0;
        }
        return tempReportIndex++;
    }

    /**
     * @see com.nextlabs.destiny.inquirycenter.report.IReportTransformBean#getUsers()
     */
    public String getUsers() {
        // a single string with multiple results
        String result = this.currentReport.getUsers();

        // only put the search condition if the user is specified
        if (result.equals("Any User")) {
            return SQL_EMPTY;
        } else {
            return result;
        }
    }

    /**
     * @see com.nextlabs.destiny.inquirycenter.report.IReportTransform#getActions()
     */
    public String getActions() {
        // the result is a list
        List<String> actionList = this.currentReport.getActionsAsList();

        if (actionList.size() > 0) {
            // convert enum to database value
            List<String> actionsDb = new ArrayList<String>(actionList.size());
            for (String action : actionList) {
                ActionEnumType actionResult = ActionEnumType
                .getActionEnum(action);
                String value = ENUM_TO_DB_MAP.get(actionResult);
                if (value != null) {
                    actionsDb.add(value);
                } else {
                    IComponentManager compMgr = ComponentManagerFactory
                    .getComponentManager();
                    final IDestinyConfigurationStore configMgr = (IDestinyConfigurationStore) compMgr
                    .getComponent(DestinyConfigurationStoreImpl.COMP_INFO);
                    IActionListConfigDO actionListConfig = configMgr
                    .retrieveActionListConfig();
                    IActionConfigDO[] actionDos = actionListConfig.getActions();
                    for (IActionConfigDO item : actionDos) {
                        if (item.getName().equals(action)) {
                            actionsDb.add(item.getShortName());
                            break;
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            boolean firstItem = true;
            for (String value : actionsDb) {
                if (!firstItem) {
                    sb.append(", ");
                }
                firstItem = false;
                sb.append(value);
            }

            return sb.toString();
        } else {
            return SQL_EMPTY;
        }
    }

    /**
     * @see com.nextlabs.destiny.inquirycenter.report.IReportTransformBean#getEnforcements()
     */
    public String getEnforcements() {
        // only one result
        String result = this.currentReport.getEnforcements();

        if (result.equals("Allow/Monitor")) {
            return "A";
        } else if (result.equals("Deny")) {
            return "D";
        } else {
            return "";
        }
    }

    /**
     * @see com.nextlabs.destiny.inquirycenter.report.IReportTransformBean#getEventLevel()
     */
    public String getEventLevel() {
        return Integer.toString(this.currentReport.getLoggingLevel());
    }

    /**
     * @see com.nextlabs.destiny.inquirycenter.report.IReportTransformBean#getHost()
     */
    public String getHost() {
        // getHost is currently unsupported
        // String hostString = this.getCurrentReport().getTargetData().equals(
        // ReportTargetUIType.ACTIVITY_JOURNAL.getName())
        // ? TR_HOST_STRING
        // : PA_HOST_STRING;
        // String result = this.currentReport.getHosts();
        // // if (result.equals("Any Computer")){
        // result = "("+ hostString + " LIKE '*' OR " + hostString + " IS
        // NULL)";
        // // } else {
        // // result = queryBuilder(result, HOST_STRING);
        // // }
        return SQL_EMPTY;
    }

    /**
     * @see com.nextlabs.destiny.inquirycenter.report.IReportTransformBean#getPolicies()
     */
    public String getPolicies() {
        // a single string with multiple result
        String result = this.currentReport.getPolicies();

        if (result.equals("Any Policy")) {
            return SQL_EMPTY;
        } else {
            return result;
        }
    }

    /**
     * @see com.nextlabs.destiny.inquirycenter.report.IReportTransformBean#getResources()
     */
    public String getResources() {
        String result = this.currentReport.getResources();
        if (result.equals("Any Resource")) {
            return SQL_EMPTY;
        } else {
            return result;
        }
    }

    public String getPolicyGroupBy() {
        //looks like this is called by the TargetData parameter 
        // of the submitReport.jsp, but not used in the reports now.
        if (getGroupBy().equals(GROUPBY_POLICY_COLUMN_NAME)) {
            return SharedLib.REPORT_PA_TABLE;
        }  else if (getGroupBy().startsWith(ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG)){
            return ALIAS_TABLE_NAME_TRACKING_ACTIVITY_LOG;
        } else {
            return ALIAS_TABLE_NAME_POLICY_ACTIVITY_LOG;
        }

    }

    /**
     * @see com.nextlabs.destiny.inquirycenter.report.IReportTransformBean#getTargetData()
     */
    public String getTargetData() {
        String result = this.currentReport.getTargetData();

        return result;
    }

    /**
     * @see com.nextlabs.destiny.inquirycenter.report.IReportTransform#getGroupBy()
     */
    public String getGroupBy() {
        ReportGroupByType type = this.currentReport.getGroupByType();
        String result = "";
        if (type.getName().equals("User")) {
            result = "User";
        } else if (type.getName().equals("Policy")) {
            result = "Policy";
        } else if (type.getName().equals("Resource")) {
            result = "Resource";
        } else if (type.getName().equals("Time")) {
            long dayDiff = ((this.getCurrentReport().getEndDate().getTime() - this
                    .getCurrentReport().getBeginDate().getTime()) / (1000 * 60 * 60 * 24));
            if (dayDiff <= 45) {
                result = "time_day";
            } else {
                result = "time_month";
            }
        }
        	
        return result;
    }

    public String getBeginDate(){
        Date date = this.getCurrentReport().getBeginDate();
        if(date == null){
            date = new Date();
        }
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp.toString();
    }

    public String getEndDate(){
        Date date = this.getCurrentReport().getEndDate();
        if(date == null){
            date = new Date();
        }
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp.toString();
    }

    public String getTitle() {
        Date date = currentReport.getRunTime();
        if(date == null){
            date = new Date();
        }
        String title = currentReport.getTitle();
        if(title.length() > TITLE_REPORT_NAME_MAX_LENGTH){
            title = title.substring(0, TITLE_REPORT_NAME_MAX_LENGTH - 3) + "...";
        }

        return String.format(TITLE_FORMAT, title, date);
    }

    public String getReportName(){

        String result;
        if (this.currentReport.getGroupByType().getName().equals("User")
                || this.currentReport.getGroupByType().getName().equals("Policy")) {
            result = isTrackingActivityReport() ? 
                    TR_GROUPBY_REPORT_NAME : PA_GROUPBY_REPORT_NAME;
        } else if (this.currentReport.getGroupByType().getName().equals("Resource")) {
            result = isTrackingActivityReport() ? 
                    TR_GROUPBYRESOURCE_REPORT_NAME : 
                        PA_GROUPBYRESOURCE_REPORT_NAME;
        } else if (this.currentReport.getGroupByType().getName().equals("Time")) {
            result = isTrackingActivityReport() ? 
                    TR_GROUPBYTIME_REPORT_NAME : PA_GROUPBYTIME_REPORT_NAME;
        } else {
            result = isTrackingActivityReport() ? 
                    TR_DETAILS_TABLE_NAME : PA_DETAILS_TABLE_NAME;
        }
        if (!this.currentReport.getSummaryType().equals(
                ReportSummaryType.None) || !isTrackingActivityReport()) {
            return result + DESIGN_FILE_EXTENSION;
        }
        return result + DESIGN_FILE_EXTENSION;
    }

    private boolean isTrackingActivityReport() {
        return this.getCurrentReport().getTargetData().equals(
                ReportTargetUIType.ACTIVITY_JOURNAL.getName());
    }


    /**
     * Returns the report.
     * 
     * @return the report.
     */
    public IReport getCurrentReport() {
        return this.currentReport;
    }

    /**
     * Sets the report
     * 
     * @param report
     *            The report to set.
     */
    public void setCurrentReport(IReport report) {
        this.currentReport = report;
    }

    public String getDriver() {
        return this.props.getProperty("database.driver");
    }

    public String getURL() {
        return this.props.getProperty("database.url");
    }

    public String getUsername() {
        return this.props.getProperty("database.username");
    }

    public String getPassword() {
        return this.props.getProperty("database.password");
    }
}
