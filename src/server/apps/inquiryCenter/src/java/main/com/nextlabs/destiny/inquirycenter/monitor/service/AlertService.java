/*
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.AlertDO;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.MonitorDO;
import com.nextlabs.destiny.configclient.Config;
import com.nextlabs.destiny.configclient.ConfigClient;
import com.nextlabs.destiny.inquirycenter.monitor.dao.AlertDAO;
import com.nextlabs.destiny.inquirycenter.monitor.dao.AlertDAOImpl;
import com.nextlabs.destiny.inquirycenter.report.ReportCriteriaJSONModel;
import net.sf.hibernate.HibernateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.nextlabs.destiny.inquirycenter.SharedUtils.getFormatedDate;

/**
 * @author nnallagatla
 *
 */
public class AlertService {

	private final Config usePastDataForMonitoringConfig = ConfigClient.get("use.past.data.for.monitoring");

	private static AlertDAO alertDAO = new AlertDAOImpl();
//	private static MonitorDAO monitorDAO = new MonitorDAOImpl();

	
	static final Log LOG = LogFactory.getLog(AlertService.class);
	
	/**
	 * Add alert
	 * @param alert
	 */
	public void addAlert(AlertDO alert)
	{		
		if (alert == null)
		{
			LOG.error("Alert if NULL. Cannot be persisted");
			return;
		}
		
		MonitorDO monitor = alert.getMonitor();
		
		LOG.info("Raising alert for monitor Id: " + monitor.getId());
		
		try {
			alertDAO.create(alert);
		} catch (HibernateException e) {
			LOG.error("Error adding Alert", e);
		}
	}
	
	public JSONObject getAlertDetailsQuery(long alertId) throws JSONException
	{
		AlertDO alert = getAlertById(alertId);
		MonitorDO monitor = alert.getMonitor();
		String jsonQuery = monitor.getCriteriaJSON();
		
		JSONObject queryObject = new JSONObject(jsonQuery);
		Calendar pointOfReference = Calendar.getInstance();
		pointOfReference.setTimeInMillis(alert.getTriggeredAt().getTime());
		
		JSONObject filters = queryObject.getJSONObject("filters");
		JSONObject general = filters.getJSONObject("general");
		
		ReportCriteriaJSONModel.convertRelativeDatesToAbsolute(general, pointOfReference);
		
		if(!usePastDataForMonitoringConfig.toBoolean()) {
			Timestamp createdDate = monitor.getCreatedAt();
			Timestamp startDate = Timestamp.valueOf(general.getString("start_date"));
			
			if (startDate.getTime() < createdDate.getTime()) {
				String beginDateStr = getFormatedDate(new Date(createdDate.getTime()));
				general.put("start_date", beginDateStr);
			}
		}
		
		Timestamp endDate = Timestamp.valueOf(general.getString("end_date"));
		
		/*
		 * When alert was generated it did not (and cannot) use data that came in after it was triggered.
		 * So replacing end_date with alert triggered date when calculated end_date falls after
		 * alert triggered date 
		 */
		if (pointOfReference.getTime().getTime() < endDate.getTime())
		{
			String endDateStr = getFormatedDate(pointOfReference.getTime());
			general.put("end_date", endDateStr);
		}
		
		
		JSONObject saveInfo = queryObject.getJSONObject("save_info");
		
		/**
		 * Remove aggregators for detail report query;
		 */
		if(queryObject.has("aggregators"))
		   queryObject.put("aggregators", new ArrayList<Object>());
		
		/*
		 * set report type to tabular so that details are displayed
		 */
		saveInfo.put("report_type","TABULAR");
		
		/*
		 * set date mode to fixed as we have already calculated dates above
		 */
		general.put("date_mode", "fixed");
		queryObject.put("grouping_mode", "");
		//queryObject.put("header", new String[]{"usr_USER_NAME", "plc_POLICY_FULLNAME", "plc_POLICY_DECISION", "oth_HOST_NAME", "oth_APPLICATION_NAME"});
		queryObject.put("max_rows", "1000");
		
		JSONArray sortOrder = new JSONArray();
		JSONObject sortByTime = new JSONObject();
		sortByTime.put("col_name", "oth_DATE");
		sortByTime.put("sort_order", "desc");
		
		sortOrder.put(sortByTime);
		
		queryObject.put("order_by", sortOrder);
		
		return queryObject;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public AlertDO getAlertById(long id)
	{
		AlertDO alert = null;
		try {
			alert = alertDAO.lookup(id);
		} catch (HibernateException e) {
			LOG.error("Error fetching alert", e);
		}
		return alert;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteAlert(Long id){
		boolean success = true;
		try {
			AlertDO alert = alertDAO.lookup(id);
			alert.setDeleted(true);
			alertDAO.update(alert);
		} catch (HibernateException e) {
			success = false;
			LOG.error(e);
		}
		
		return success;
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	public String getAlertsJSON() throws Exception
	{
		JSONArray arr = new JSONArray();
		List<AlertDO> alerts = null;
		try {
			alerts = alertDAO.getActiveAlerts();
		} catch (HibernateException e) {
			LOG.error("Operation Failed", e);
		}
		
		for (AlertDO alert : alerts)
		{
			arr.put(getJSON(alert));
		}
		
		return arr.toString();
	}

	
	public JSONObject getJSON(AlertDO alert) throws JSONException
	{	
		JSONObject obj = new JSONObject();
		obj.accumulate("id", alert.getId())
			.accumulate("level", alert.getLevel())
			.accumulate("message", alert.getAlertMessage() == null ? "" : alert.getAlertMessage())
			.accumulate("monitor_name", alert.getMonitorName())
			.accumulate("monitor_uid", alert.getMonitorUID())
			.accumulate("raised_at", getFormatedDate(alert.getTriggeredAt()));
		return obj;
	}
	
	/**
	 * 
	 * @param jsonQuery
	 * @return
	 */
	public String getAlertsJSONByQuery(HttpServletRequest request, String jsonQuery)
	{
		AlertDataManager mgr = new AlertDataManager();
		JSONObject result = null;
		try {
			result = mgr.getResultData(request, jsonQuery);
		} catch (Exception e) {
			LOG.error(e);
		}
		return result == null ? "{}": result.toString();
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean updateAlertStatus(Long id, Long dismissingUserId, boolean requestForDismiss){
		boolean success = true;
		try {
			AlertDO alert = alertDAO.lookup(id);
			alert.setDismissed(requestForDismiss);
			/*
			 * if request was to undo dismiss update user with -1
			 */
			alert.setHiddenByUserId(requestForDismiss ? dismissingUserId : -1);
			alertDAO.update(alert);
		} catch (HibernateException e) {
			success = false;
			LOG.error(e);
		}
		
		return success;
	}
	
}
