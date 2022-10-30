/*
 *  All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.savedreport.service;

import static com.bluejungle.domain.policydecision.PolicyDecisionEnumType.POLICY_DECISION_ALLOW;
import static com.bluejungle.domain.policydecision.PolicyDecisionEnumType.POLICY_DECISION_DENY;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.bluejungle.destiny.container.shared.inquirymgr.IInquiry;
import com.bluejungle.destiny.container.shared.inquirymgr.IInquiryAction;
import com.bluejungle.destiny.container.shared.inquirymgr.IInquiryPolicy;
import com.bluejungle.destiny.container.shared.inquirymgr.IInquiryPolicyDecision;
import com.bluejungle.destiny.container.shared.inquirymgr.IInquiryResource;
import com.bluejungle.destiny.container.shared.inquirymgr.IInquiryUser;
import com.bluejungle.destiny.container.shared.inquirymgr.IReportOwner;
import com.bluejungle.destiny.container.shared.inquirymgr.InquiryTargetDataType;
import com.bluejungle.destiny.container.shared.inquirymgr.ReportSummaryType;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.ReportDO;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.DBUtil;
import com.bluejungle.domain.action.hibernateimpl.ActionEnumUserType;
import com.bluejungle.domain.policydecision.PolicyDecisionEnumType;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.SavedReportDO;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAO;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAOImpl;

/**
 * 
 * This class is for migrating existing saved reports to the new schema
 * 
 * @author trungkien
 * 
 */
public class SavedReportMigrationService {
	public static final Log LOG = LogFactory
			.getLog(SavedReportMigrationService.class);

	public static List<String> defaultHeader = new ArrayList<String>();

	public static ActionEnumUserType actionsLookup = new ActionEnumUserType();
	
	static {
		defaultHeader.add("usr_USER_NAME");
		defaultHeader.add("oth_HOST_NAME");
		defaultHeader.add("oth_APPLICATION_NAME");
		defaultHeader.add("plc_POLICY_FULLNAME");
		defaultHeader.add("plc_POLICY_DECISION");
	}

	public static final String EQUALS = "eq";
	public static final String IN = "in";
	public static final String LIKES = "like";
	public static final String NOT_EQUALS = "ne";

	
	
	/**
	 * This method
	 * 
	 * @throws HibernateException
	 */
	public static int migrateSavedReports() throws HibernateException {
		SavedReportDAO savedReportDAO = new SavedReportDAOImpl();
		List<ReportDO> existingReports = getExistingSavedReports();
		if (existingReports == null || existingReports.size() == 0) {
			LOG.error("No saved reports to migrate!!");
			return 0;
		}

		LOG.debug("Found " + existingReports.size()
				+ " Saved Reports for Migration");

		int count = 0;
		for (ReportDO report : existingReports) {
			SavedReportDO newReport = null;
			try {
				newReport = convertReportToNewSchema(report);
				if (newReport == null) {
					continue;
				}
				savedReportDAO.create(newReport);
				count++;
			} catch (Exception e) {
				if(report != null) {
					LOG.error("Could not migrate report with title: " + report.getTitle());
				}
				LOG.error(e);
			}
		}
		LOG.debug("Successfully Migrated " + count + " Saved Report(s)");
		return count;
	}

	/**
	 * This method fetches the
	 * 
	 * @return
	 * @throws HibernateException
	 */
	private static List<ReportDO> getExistingSavedReports()
			throws HibernateException {
		List<ReportDO> reports = null;
		Session s = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			s = dataSource.getSession();
			Criteria criteria = s.createCriteria(ReportDO.class);
			reports = criteria.list();
		} finally {
			HibernateUtils.closeSession(s, LOG);
		}
		return reports;
	}

	/**
	 * 
	 * @param oldReport
	 * @return
	 * @throws JSONException
	 */
	private static String getCriteriaJSON(ReportDO oldReport)
			throws JSONException {

		JSONObject criteria = new JSONObject();
		JSONArray header = new JSONArray();

		for (String field : defaultHeader) {
			header.put(field);
		}
		criteria.put("header", header);
		
		criteria.put("filters", getFiltersJSON(oldReport));

		criteria.put("aggregators", getAggregatorsByJSON());
		
		criteria.put("max_rows", getMaxRowsByJSON());
		
		populateRemainingJSON(criteria, oldReport);
		
		criteria.put("group_by", getGroupByByJSON());
		
		LOG.debug("New criteria created:" + criteria.toString());
		return criteria.toString();
	}

	/**
	 * 
	 * @return
	 * @throws JSONException 
	 */
	private static JSONArray getOrderByJSON(String field, String order) throws JSONException {
		JSONArray orderBy = new JSONArray();
		
		JSONObject obj = new JSONObject();
		obj.accumulate("col_name", field);
		obj.accumulate("sort_order", order);
		
		orderBy.put(obj);
		return orderBy;
	}

	/**
	 * 
	 * @return
	 */
	private static String getMaxRowsByJSON() {
		String maxRows = "-1";
		return maxRows;
	}

	/**
	 * 
	 * @param oldReport 
	 * @return
	 * @throws JSONException 
	 */
	private static String populateRemainingJSON(JSONObject criteria, ReportDO oldReport) throws JSONException {
		
		ReportSummaryType type = oldReport.getSummaryType();
		
		String groupingMode = "";
		
		
		if (ReportSummaryType.POLICY.equals(type))
		{
			groupingMode = "GROUP_BY_POLICY";
			criteria.put("order_by", getOrderByJSON("ResultCount", "Desc"));
			criteria.put("save_info", getSaveInfoByJSON(oldReport, "BAR_CHART"));
		}
		else if (ReportSummaryType.USER.equals(type))
		{
			groupingMode = "GROUP_BY_USER";
			criteria.put("order_by", getOrderByJSON("ResultCount", "Desc"));
			criteria.put("save_info", getSaveInfoByJSON(oldReport, "BAR_CHART"));
		}
		else if (ReportSummaryType.RESOURCE.equals(type))
		{
			groupingMode = "GROUP_BY_RESOURCE";
			criteria.put("order_by", getOrderByJSON("ResultCount", "Desc"));
			criteria.put("save_info", getSaveInfoByJSON(oldReport, "PIE_CHART"));
		}
		else if (ReportSummaryType.TIME_DAYS.equals(type))
		{
			groupingMode = "GROUP_BY_DAY";
			criteria.put("order_by", getOrderByJSON("ResultCount", "Desc"));
			criteria.put("save_info", getSaveInfoByJSON(oldReport, "BAR_CHART"));
		}
		else if (ReportSummaryType.TIME_MONTHS.equals(type))
		{
			groupingMode = "GROUP_BY_MONTH";
			criteria.put("order_by", getOrderByJSON("ResultCount", "Desc"));
			criteria.put("save_info", getSaveInfoByJSON(oldReport, "BAR_CHART"));
		}
		else
		{
			criteria.put("order_by", getOrderByJSON("oth_DATE", "Desc"));
			criteria.put("save_info", getSaveInfoByJSON(oldReport, "TABULAR"));
		}

		criteria.put("grouping_mode", groupingMode);
		
		return groupingMode;
	}

	/**
	 * 
	 * @return
	 */
	private static JSONArray getAggregatorsByJSON() {
		JSONArray aggregators = new JSONArray();
		return aggregators;
	}

	/**
	 * 
	 * @return
	 */
	private static Set<String> getGroupByByJSON() {
		Set<String> values = new HashSet<String>();
		return values;
	}

	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getSaveInfoByJSON(ReportDO oldReport, String reportType)
			throws JSONException {
		JSONObject obj = new JSONObject();
		obj.accumulate("report_name", oldReport.getTitle()); 
		obj.accumulate("report_desc", oldReport.getDescription());
		obj.accumulate("report_type", reportType);
		return obj;
	}

	/**
	 * 
	 * @param oldReport
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getFiltersJSON(ReportDO oldReport)
			throws JSONException {
		if (oldReport == null || oldReport.getInquiry() == null) {
			return new JSONObject();
		}

		JSONObject filter = new JSONObject();

		filter.put("general", getGeneralCriteriaJSON(oldReport));

		filter.put("user_criteria", getUserCriteriaJSON(oldReport));

		filter.put("resource_criteria", getResourceCriteriaJSON(oldReport));

		filter.put("policy_criteria", getPolicyCriteriaJSON(oldReport));

		filter.put("other_criteria", getOtherCriteriaJSON(oldReport));
		
		LOG.debug("filter: "+ filter.toString());

		return filter;
	}

	/**
	 * This method returns General criteria
	 * 
	 * @param
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getGeneralCriteriaJSON(ReportDO oldReport)
			throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("type", "report");
		obj.put("date_mode", "Relative");
		obj.put("window_mode", "last_7_days");
		obj.put("start_date", "2014-01-01 00:00:00" );
		obj.put("end_date", "2014-05-28 23:59:59");
		obj.put("fields", getGeneralFields(oldReport));
		return obj;
	}

	private static JSONArray getGeneralFields(ReportDO oldReport)
			throws JSONException {
		JSONArray values = new JSONArray();

		IInquiry inquiry = oldReport.getInquiry();

		JSONObject logLevelObject = new JSONObject();
		logLevelObject.put("name", "log_level");
		logLevelObject.put("operator", EQUALS);
		logLevelObject.put("value", String.valueOf(inquiry.getLoggingLevel()));
		logLevelObject.put("has_multi_value", "false");
		values.put(logLevelObject);
		LOG.debug("logLevelObject: "+logLevelObject.toString());		

		JSONObject decisionObject = new JSONObject();
		decisionObject.put("name", "decision");
		decisionObject.put("operator", EQUALS);
		decisionObject.put("value", getPolicyDecisionFilterJSON(inquiry.getPolicyDecisions()));
		decisionObject.put("has_multi_value", "false");
		values.put(decisionObject);
		LOG.debug("decisionObject: "+decisionObject.toString());		

		JSONObject actionObject = new JSONObject();
		actionObject.put("name", "action");
		actionObject.put("operator", IN);
		actionObject.put("value", getActionFilterJSON(inquiry.getActions()));
		actionObject.put("has_multi_value", "true");
		values.put(actionObject);
		LOG.debug("actionObject: "+actionObject.toString());
		
		return values;
	}

	/**
	 * This method returns User criteria
	 * 
	 * @param
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getUserCriteriaJSON(ReportDO oldReport)
			throws JSONException {
		JSONObject obj = new JSONObject();

		IInquiry inquiry = oldReport.getInquiry();

		JSONObject userObject = new JSONObject();
		userObject.put("name", "user_name");
		userObject.put("operator", IN);
		userObject.put("value", getUserFilterJSON(inquiry.getUsers()));
		userObject.put("has_multi_value", "true");
		obj.put("look_up_field", userObject);

		obj.put("fields", new JSONArray());

		return obj;
	}

	/**
	 * This method returns Resource criteria
	 * 
	 * @param
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getResourceCriteriaJSON(ReportDO oldReport)
			throws JSONException {
		JSONObject obj = new JSONObject();

		IInquiry inquiry = oldReport.getInquiry();

		JSONObject resourceObject = new JSONObject();
		resourceObject.put("name", "resource_path");
		resourceObject.put("operator", IN);
		resourceObject.put("value",
				getResourceFilterJSON(inquiry.getResources()));
		resourceObject.put("has_multi_value", "true");
		obj.put("look_up_field", resourceObject);

		obj.put("fields", new JSONArray());

		return obj;
	}

	/**
	 * This method returns Policy criteria
	 * 
	 * @param
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getPolicyCriteriaJSON(ReportDO oldReport)
			throws JSONException {
		JSONObject obj = new JSONObject();

		IInquiry inquiry = oldReport.getInquiry();

		JSONObject policyObject = new JSONObject();
		policyObject.put("name", "policy_name");
		policyObject.put("operator", IN);
		policyObject.put("value",
				getPolicyFilterJSON(inquiry.getPolicies()));
		policyObject.put("has_multi_value", "true");
		obj.put("look_up_field", policyObject);

		obj.put("fields", new JSONArray());

		return obj;
	}

	/**
	 * This method returns Policy criteria
	 * 
	 * @param
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getOtherCriteriaJSON(ReportDO oldReport)
			throws JSONException {
		JSONObject obj = new JSONObject();

		IInquiry inquiry = oldReport.getInquiry();

		JSONObject otherObject = new JSONObject();
		otherObject.put("name", "");
		otherObject.put("operator", IN);
		otherObject.put("value", "");
		otherObject.put("has_multi_value", "false");
		obj.put("look_up_field", otherObject);

		obj.put("fields", new JSONArray());

		return obj;
	}

	/**
	 * This method returns User displayName from {@link Set} of
	 * {@link IInquiryUser}
	 * 
	 * @param users
	 * @return
	 */
	private static Set<String> getUserFilterJSON(Set users) {
		Set<String> values = new HashSet<String>();
		if (users == null || users.isEmpty()) {
			return values;
		}
		for (Object obj : users) {
			IInquiryUser user = (IInquiryUser) obj;
			
			if ("any user".equalsIgnoreCase(user.getDisplayName())){
				continue;
			}
			
			values.add(user.getDisplayName());
		}
		return values;
	}

	/**
	 * This method returns {@link Set} of Resource names from {@link Set} of
	 * {@link IInquiryResource}
	 * 
	 * @param resources
	 * @return
	 */
	private static Set<String> getResourceFilterJSON(Set resources) {
		Set<String> values = new HashSet<String>();
		if (resources == null || resources.isEmpty()) {
			return values;
		}
		for (Object obj : resources) {
			IInquiryResource res = (IInquiryResource) obj;
			
			if ("any resource".equalsIgnoreCase(res.getName())){
				continue;
			}
			values.add(res.getName());
		}
		return values;
	}

	/**
	 * This method returns {@link Set} of Policy names from {@link Set} of
	 * {@link IInquiryPolicy}
	 * 
	 * @param policies
	 * @return
	 */
	private static Set<String> getPolicyFilterJSON(Set policies) {
		Set<String> values = new HashSet<String>();
		if (policies == null || policies.isEmpty()) {
			return values;
		}
		for (Object obj : policies) {
			IInquiryPolicy policy = (IInquiryPolicy) obj;
			if ("any policy".equalsIgnoreCase(policy.getName())){
				continue;
			}
			values.add(policy.getName());
		}
		return values;
	}

	/**
	 * This method returns {@link Set} of Policy decisions from {@link Set} of
	 * {@link IInquiryPolicyDecision}
	 * 
	 * @param policyDecisions
	 * @return
	 */
	private static String getPolicyDecisionFilterJSON(Set policyDecisions) {
		if (policyDecisions == null || policyDecisions.isEmpty() || policyDecisions.size() > 1) {
			return "B";
		} else {
			Iterator decisionItr = policyDecisions.iterator();
			if(decisionItr.hasNext()) {
				IInquiryPolicyDecision policyD = (IInquiryPolicyDecision) decisionItr.next();
				if( POLICY_DECISION_ALLOW.equals(policyD.getPolicyDecisionType()))
				{
					return "A";
				}
				else if( POLICY_DECISION_DENY.equals(policyD.getPolicyDecisionType()))
				{
					return "D";
				}
			}
		}
		return "B";
	}

	/**
	 * This method returns {@link Set} of Actions from {@link Set} of
	 * {@link IInquiryAction}
	 * 
	 * @param actions
	 * @return
	 */
	private static Set<String> getActionFilterJSON(Set actions) {
		Set<String> values = new HashSet<String>();
		if (actions == null || actions.isEmpty()) {
			return values;
		}
		for (Object obj : actions) {
			IInquiryAction action = (IInquiryAction) obj;
			values.add(actionsLookup.getCodeByType(action.getActionType()));
		}
		return values;
	}
	
	/**
	 * 
	 * 
{
    "filters": {
        "general": {
		    "type":"report|monitor",
            "date_mode": "Fixed|Relative",
		    "window_mode": "current_month",
            "start_date": "2014-01-0100: 00: 00",
            "end_date": "2014-05-2823: 59: 59",
            "fields": [
               { "name" : "activity", "operator" : "eq|ne|like|in", "value": "document", "has_multi_value" : "false"},  <-- ??
               { "name" : "log_level", "operator" : "eq", "value": "3", "has_multi_value" : "false"},
               { "name" : "decision", "operator" : "eq", "value": "B", "has_multi_value" : "false"},
               { "name" : "action", "operator" : "in", "value": ["val1", "val2"], "has_multi_value" : "true"},
            ]
        },
        "user_criteria" : {
            "look_up_field" :{ "name" : "user_name", "operator" : "$in", "value": [ "1", "2", "3"], "has_multi_value" : "true"},
            "fields" : [
                       { "name" : "usr_USER_NAME", "operator" : "$eq", "value": "fgfdg", "has_multi_value" : "false"},
                       { "name" : "usr_XXX", "operator" : "$in", "value": ["", ""], "has_multi_value" : "true"},
            ]
        },
        "resource_criteria" : {
            "look_up_field" :{ "name" : "resource_path", "operator" : "$in", "value": [ "AnyResource", "2", "3"], "has_multi_value" : "true"},
            "fields": [
                        { "name" : "res_application", "operator" : "$eq", "value": "gfdgdg", "has_multi_value" : "false"},
                        { "name" : "res_materialgroup", "operator" : "$in", "value": ["", ""], "has_multi_value" : "true"},
                        { "name" : "res_id", "operator" : "$eq", "value": ["125488"], "has_multi_value" : "false"},
            ]
        },
        "policy_criteria" : {
            "look_up_field" :{ "name" : "policy_name", "operator" : "$in", "value": [ "/drm/kent", "/AC/license"], "has_multi_value" : "true"},
            "fields": [
                   { "name" : "plc_POLICY_DECISION", "operator" : "$eq", "value": "gfdgdg", "has_multi_value" : "false"},
                   { "name" : "plc_POLICY_FULLNAME", "operator" : "$eq", "value": "rtet", "has_multi_value" : "false"},
            ]
        },
        "other_criteria" : {
            "look_up_field" :{ "name" : "", "operator" : "", "value": "", "has_multi_value" : "false"},
            "fields": [
                     { "name" : "oth_HOST_IP", "operator" : "$eq", "value": "gfdgdg", "has_multi_value" : "false"},
                     { "name" : "oth_HOST_ADDRESS", "operator" : "$eq", "value": "rtet", "has_multi_value" : "false"},
            ]                  
         },
	},	 
    "header": [
        "res_ip-address",
        "res_export-control",
        "res_application",
		"res_us_legalconfirmed",
		"res_user-citizenship-confirmed"
	],
	"order_by": [
		{ "col_name" : "res_ip-address", "sort_order" : "desc"}
	],             
	"max_rows": "-1",
	"grouping_mode": "GROUP_BY_DAY|GROUP_BY_MONTH|GROUP_BY_POLICY|GROUP_BY_USER|",
	"aggregators":[
	     {"name":"file_size", "function":"sum", "operator":"EQ", "value":1000}
	],
    "group_by":["user_name","from_resource_name"],
	"save_info": {
		"report_name": "sadadwqe",
		"report_desc": "adsadsadv2",
		"report_type": "TABULAR"
	}
}              
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param oldReport
	 * @return
	 * @throws JSONException
	 * 
	 * 
	 */
	private static SavedReportDO convertReportToNewSchema(ReportDO oldReport)
			throws JSONException {
		if (oldReport == null) {
			LOG.error("Old Report is NULL");
			return null;
		}
		
		IInquiry inquiry = oldReport.getInquiry();
		
		InquiryTargetDataType data = inquiry.getTargetData(); 
		
		if (InquiryTargetDataType.ACTIVITY.equals(data))
		{
			LOG.info("Skipping Document Activity Saved Report: " + oldReport.getTitle());
			return null;
		}
		
		SavedReportDO newReport = new SavedReportDO();
		newReport.setTitle(oldReport.getTitle());
		newReport.setDescription(oldReport.getDescription());
		newReport.setDeleted(false);
		newReport.setInDashboard(false);
		IReportOwner owner = oldReport.getOwner();

		if (owner != null) {
			newReport.setSharedMode(owner.getIsShared() ? SavedReportDO.SAVED_REPORT_PUBLIC : SavedReportDO.SAVED_REPORT_ONLY_ME);
			newReport.setOwnerId(owner.getOwnerId());
		}

		newReport.setCriteriaJSON(getCriteriaJSON(oldReport));
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		newReport.setCreatedDate(ts);
		newReport.setLastUpdatedDate(ts);

		return newReport;
	}
}
