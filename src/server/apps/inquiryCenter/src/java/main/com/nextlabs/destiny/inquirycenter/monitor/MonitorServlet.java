/*
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.monitor;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.MonitorDO;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.MonitorTagDO;
import com.bluejungle.destiny.services.management.UserRoleServiceStub;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupServiceStub;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.nextlabs.destiny.inquirycenter.SharedUtils;
import com.nextlabs.destiny.inquirycenter.monitor.service.AlertDataManager;
import com.nextlabs.destiny.inquirycenter.monitor.service.AlertService;
import com.nextlabs.destiny.inquirycenter.monitor.service.MonitoringService;
import com.nextlabs.destiny.inquirycenter.monitor.service.ReportingService;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.ReportDataManager;
import com.nextlabs.destiny.inquirycenter.savedreport.service.ReporterAccessControlService;
import com.nextlabs.destiny.inquirycenter.savedreport.service.impl.ReporterAccessControlServiceImpl;
import com.nextlabs.report.datagen.ResultData;

import net.sf.hibernate.HibernateException;

/**
 * @author nnallagatla
 * 
 */
public class MonitorServlet extends HttpServlet {

	private static final long serialVersionUID = 563824488919416804L;
	public static final Log LOG = LogFactory.getLog(MonitorServlet.class);
	private static final MonitoringService mService = new MonitoringService();
	private static final AlertService alertService = new AlertService();

	private UserRoleServiceStub userService;
	private UserGroupServiceStub userGroupService;
	private ReporterAccessControlService reporterAccessControlService;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	private void doProcess(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String action = request.getParameter("action");
		LOG.debug("action: " + action);

		FacesContext currentContext = SharedUtils.getFacesContext(request, response);
		try {
			if (action.equalsIgnoreCase("CREATE_MONITOR"))
				createMonitor(request, response);
			else if (action.equalsIgnoreCase("DELETE_MONITOR"))
				deleteMonitor(request, response);
			else if (action.equalsIgnoreCase("DEACTIVATE_MONITOR"))
				deactivateMonitor(request, response);
			else if (action.equalsIgnoreCase("EDIT_MONITOR"))
				editMonitor(request, response);
			else if (action.equalsIgnoreCase("ACTIVATE_MONITOR"))
				activateMonitor(request, response);
			else if (action.equalsIgnoreCase("RUN_MONITOR"))
				runMonitor(request, response);
			else if (action.equalsIgnoreCase("DELETE_ALERT"))
				deleteAlert(request, response);
			else if (action.equalsIgnoreCase("DISMISS_ALERT"))
				dismissAlert(request, response);
			else if (action.equalsIgnoreCase("DETAIN_ALERT"))
				detainAlert(request, response);
			else if (action.equalsIgnoreCase("MONITOR_DETAILS"))
				fetchMonitorDetails(request, response);
			else if (action.equalsIgnoreCase("WATCHES_LIST"))
				fetchwatches(request, response);
			else if (action.equalsIgnoreCase("FIELDS_LIST"))
				fetchFields(request, response);
			else if (action.equalsIgnoreCase("ALERTS_REPORTING"))
				getAlertsReportingData(request, response);
			else if (action.equalsIgnoreCase("ALERT_DETAILS"))
				getAlertDetails(request, response);
			else if (action.equalsIgnoreCase("MONITORS_LIST"))
				fetchMonitors(request, response);
			else if (action.equalsIgnoreCase("VALIDATE_MONITOR_NAME"))
				validateMonitorName(request, response);
			else {
				LOG.error("Action not supported: " + action);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			LOG.error("Error occurred doProcess ", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		} finally {
			if (currentContext != null)
				currentContext.release();
		}

	}

	private void validateMonitorName(HttpServletRequest request, HttpServletResponse response)
			throws HibernateException, IOException, JSONException {
		String name = request.getParameter("name");
		String id = request.getParameter("id");
		LOG.debug("validate monitor name: " + name);
		boolean flag = false;
		if (name != null && !name.isEmpty()) {
			List<MonitorDO> monitors = mService.lookupByMonitorName(name);
			if (monitors == null || monitors.size() == 0) {
				flag = true;
			}

			/*
			 * if edit operation allow the monitor with its name to have it
			 */
			if (monitors.size() == 1) {
				MonitorDO monitor = monitors.get(0);
				long monitorId = monitor.getId();

				LOG.debug("Monitor client id:" + id + " server id:" + monitorId);

				long temp = -1;

				if (id != null && !id.isEmpty()) {
					try {
						temp = Long.parseLong(id);
					} catch (NumberFormatException ne) {
						LOG.error("Incorrect Moitor Id passed for validation: " + id, ne);
					}
					if (temp == monitorId) {
						flag = true;
					}
				}
			}
		}
		SharedUtils.writeJSONResponse(request, response, new JSONObject("{\"status\":\"" + flag + "\"}"));
	}

	private void fetchMonitors(HttpServletRequest request, HttpServletResponse response) throws Exception {

		LOG.info("Request came to fetch monitors");

		ILoggedInUser user = AppContext.getContext(request).getRemoteUser();
		UserGroupReduced[] userGroups = getReporterAccessControlService(request).findGroupsForUser(user);
		List<Long> userGrpIds = new ArrayList<Long>(5);

		if (userGroups != null) {
			for (UserGroupReduced userGroup : userGroups) {
				userGrpIds.add(userGroup.getId().getID().longValue());
			}
		}

		JSONArray monitorArr = new JSONArray();

		for (MonitorDO monitor : mService.list()) {
			boolean hasAccess = getReporterAccessControlService(request).hasMonitorAccess(user, userGrpIds, monitor);

			if (hasAccess) {
				monitorArr.put(mService.getJSON(monitor, false));
			}
		}

		LOG.debug("Total no of monitors accessible monitors for user :" + monitorArr.length());

		SharedUtils.writeJSONResponse(request, response, monitorArr);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws JSONException
	 * @throws IOException
	 */
	private void getAlertDetails(HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {
		String id = request.getParameter("id");
		LOG.debug("Monitor Id: " + id);
		if (id == null || id.isEmpty()) {
			return;
		}
		Long alertId = Long.parseLong(id);
		JSONObject query = alertService.getAlertDetailsQuery(alertId);
		SharedUtils.writeJSONResponse(request, response, query);
	}

	/**
	 * @param request
	 * @param response
	 */
	private void getAlertsReportingData(HttpServletRequest request, HttpServletResponse response) {
		String query = request.getParameter("query");

		AlertDataManager mgr = new AlertDataManager();
		try {
			JSONObject result = mgr.getResultData(request, query);
			SharedUtils.writeJSONResponse(request, response, result);
		} catch (Exception e) {
			LOG.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		LOG.debug(query);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws JSONException
	 */
	private void fetchFields(HttpServletRequest request, HttpServletResponse response)
			throws IOException, JSONException {
		SharedUtils.writeJSONResponse(request, response, ReportingService.getAllColumnMappingJSON());
	}

	private void fetchMonitorDetails(HttpServletRequest request, HttpServletResponse response)
			throws IOException, JSONException {
		String id = request.getParameter("id");
		LOG.debug("Monitor Id: " + id);
		if (id == null || id.isEmpty()) {
			return;
		}
		Long monitorId = Long.parseLong(id);
		JSONObject jsonObject = mService.getMonitorJSON(request, monitorId);
		SharedUtils.writeJSONResponse(request, response, jsonObject);

	}

	private void dismissAlert(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		LOG.debug("Alert Id: " + id);
		if (id == null || id.isEmpty()) {
			return;
		}

		Long userId = AppContext.getContext(request).getRemoteUser().getPrincipalId();

		Long alertId = Long.parseLong(id);
		boolean status = alertService.updateAlertStatus(alertId, userId, true);
		LOG.debug("Dismiss status: " + status);
		if (!status) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void detainAlert(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		LOG.debug("Alert Id: " + id);
		if (id == null || id.isEmpty()) {
			return;
		}

		Long alertId = Long.parseLong(id);
		boolean status = alertService.updateAlertStatus(alertId, -1L, false);
		LOG.debug("Detain status: " + status);
		if (!status) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void deleteAlert(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		if (id == null || id.isEmpty()) {
			return;
		}
		Long alertId = Long.parseLong(id);
		boolean status = alertService.deleteAlert(alertId);
		if (!status) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void deactivateMonitor(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		if (id == null || id.isEmpty()) {
			return;
		}
		Long monitorId = Long.parseLong(id);
		boolean status = mService.deactivateMonitor(monitorId);
		if (!status) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void activateMonitor(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		if (id == null || id.isEmpty()) {
			return;
		}
		Long monitorId = Long.parseLong(id);
		boolean status = mService.activateMonitor(monitorId);
		if (!status) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void deleteMonitor(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		if (id == null || id.isEmpty()) {
			return;
		}
		Long monitorId = Long.parseLong(id);
		boolean status = mService.deleteMonitor(monitorId);
		if (!status) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void createMonitor(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {

		/*
		 * 
		 * name=NextLabs+JPC+Shared+Libs&description=test&watches%5B%5D=1&
		 * watches %5B%5D=2
		 * &duration=last_30_days&message=test&log_to_dashboard=true&send_email
		 * =false
		 * &email=admin%40test.com&level=L1&filters%5B0%5D%5Bname%5D=department
		 * &filters%5B0%5D%5Boperator%5D=EQ&filters%5B0%5D%5Bvalue%5D=hr
		 * &aggregators
		 * %5B0%5D%5Bname%5D=number_records&aggregators%5B0%5D%5Boperator%5D=GE
		 * &
		 * aggregators%5B0%5D%5Bvalue%5D=1000&aggregators%5B0%5D%5Bfunction%5D=
		 * sum &action=CREATE
		 */

		String payLoad = request.getParameter("data");

		LOG.debug(payLoad.toString());

		MonitorDO monitor = parseMonitor(request, payLoad.toString());

		Timestamp ts = new Timestamp(System.currentTimeMillis());

		monitor.setCreatedAt(ts);
		monitor.setLastUpdatedAt(ts);
		monitor.setDeleted(false);

		LOG.debug("adding monitor");
		mService.add(monitor);
		LOG.debug("added monitor");

		SharedUtils.writeJSONResponse(request, response, mService.getJSON(monitor, true));
	}

	private void editMonitor(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {

		String payLoad = request.getParameter("data");

		LOG.debug(payLoad.toString());

		MonitorDO monitor = parseMonitor(request, payLoad.toString());

		Timestamp ts = new Timestamp(System.currentTimeMillis());

		monitor.setLastUpdatedAt(ts);

		LOG.debug("updating monitor");
		mService.edit(monitor);
		LOG.debug("updated monitor");

		SharedUtils.writeJSONResponse(request, response, mService.getJSON(monitor, true));
	}

	private void fetchwatches(HttpServletRequest request, HttpServletResponse response)
			throws IOException, JSONException {
		SharedUtils.policyLookupData(request, response, "{}");
	}

	private void runMonitor(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String id = request.getParameter("id");
		if (id == null || id.isEmpty()) {
			return;
		}
		Long monitorId = Long.parseLong(id);

		LOG.debug("-------------------------------Executing Monitor with id: " + monitorId);

		MonitorDO monitor = mService.getMonitorById(monitorId);

		ReportDataManager mgr = new ReportDataManager();

		ResultData results = mgr.getPADetailsTableDataV2(monitor.getCriteriaJSON());

		if (results != null) {
			int numberOfRecords = results.size();
			if (numberOfRecords > 0) {
				LOG.debug("resulted in Alert");
			}
		}

		LOG.debug("-------------------------------Executed Monitor with id: " + monitorId);

	}

	private MonitorDO parseMonitor(HttpServletRequest request, String string) throws Exception {

		JSONObject obj = new JSONObject(string);
		Long id = (obj.has("id") && !obj.getString("id").equals("")) ? obj.getLong("id") : null;

		String name = obj.getString("name");
		String description = obj.getString("description");
		String message = obj.getString("message");
		boolean autoDismiss = obj.getBoolean("auto_dismiss");
		boolean sendEmail = obj.getBoolean("send_email");
		String email = obj.getString("email");

		JSONArray tags = obj.getJSONArray("tags");
		JSONObject criteria = obj.getJSONObject("criteria");
		JSONObject jsonSaveInfo = criteria.getJSONObject("save_info");

		List<String> users = new ArrayList<String>();
		List<Long> groupIds = new ArrayList<Long>();

		String sharedMode = jsonSaveInfo.getString("shared_mode");
		String pqlData = "";

		if (sharedMode.toLowerCase().equals("users")) {
			JSONArray userIdArr = jsonSaveInfo.getJSONArray("user_ids");
			if (userIdArr != null && userIdArr.length() > 0) {
				for (int i = 0; i < userIdArr.length(); i++) {
					String username = userIdArr.getString(i);
					users.add(username);
				}
			}

			JSONArray groupIdArr = jsonSaveInfo.getJSONArray("group_ids");
			if (groupIdArr != null && groupIdArr.length() > 0) {
				for (int i = 0; i < groupIdArr.length(); i++) {
					Long groupId = groupIdArr.getLong(i);
					groupIds.add(groupId);
				}
			}

		}

		Long userId = AppContext.getContext(request).getRemoteUser().getPrincipalId();

		MonitorDO monitor = new MonitorDO();
		monitor.setId(id);
		monitor.setName(name);
		monitor.setDescription(description);
		monitor.setActive(true);
		monitor.setCriteriaJSON(criteria.toString());
		monitor.setAlertMessage(message);
		monitor.setEmailAddress(email);
		monitor.setSendEmail(sendEmail);
		monitor.setAutoDismiss(autoDismiss);
		monitor.setOwnerId(userId);
		monitor.setSharedMode(sharedMode);

		pqlData = getReporterAccessControlService(request).generateMonitorDataAccessPQL(monitor, users, groupIds);
		monitor.setPqlData(pqlData);

		Map<String, MonitorTagDO> tagMap = new HashMap<String, MonitorTagDO>();

		for (int i = 0; i < tags.length(); i++) {
			JSONObject tagObj = tags.getJSONObject(i);
			String tagName = tagObj.getString("name");
			String tagValue = tagObj.getString("value");

			MonitorTagDO tag = new MonitorTagDO();
			tag.setMonitor(monitor);
			tag.setName(tagName);
			tag.setValue(tagValue);
			tagMap.put(tagName, tag);
		}

		monitor.setCriteriaJSON(criteria.toString());
		monitor.setTags(tagMap);

		LOG.debug("Monitor Data : " + monitor);
		LOG.debug("Monitor criteria : " + monitor.getCriteriaJSON());
		return monitor;
	}

	private static final String USER_SERVICE_LOCATION_SERVLET_PATH = "/services/UserRoleService";

	/**
	 * Retrieve the User and User Service port
	 * 
	 * @return the User and User Service port
	 * @throws AxisFault
	 *             if the service port retrieval fails
	 */
	private UserRoleServiceStub getUserService(HttpServletRequest request) throws AxisFault {
		if (this.userService == null) {
			String location = request.getSession().getServletContext().getInitParameter("DMSLocation")
					+ USER_SERVICE_LOCATION_SERVLET_PATH;
			this.userService = new UserRoleServiceStub(location);
		}

		return this.userService;
	}

	private static final String USER_GROUP_SERVICE_LOCATION_SERVLET_PATH = "/services/UserGroupService";

	/**
	 * Retrieve the User Group Service. (protected to allow unit testing of this
	 * class)
	 */
	protected UserGroupServiceStub getUserGroupService(HttpServletRequest request) throws AxisFault {
		if (this.userGroupService == null) {
			String location = request.getSession().getServletContext().getInitParameter("DMSLocation")
					+ USER_GROUP_SERVICE_LOCATION_SERVLET_PATH;
			this.userGroupService = new UserGroupServiceStub(location);
		}

		return this.userGroupService;
	}

	public ReporterAccessControlService getReporterAccessControlService(HttpServletRequest request) {
		if (reporterAccessControlService == null) {
			reporterAccessControlService = new ReporterAccessControlServiceImpl();
			// reporterAccessControlService = new
			// ReporterAccessControlServiceImpl(
			// getUserService(request), getUserGroupServiceIF(request));
		}

		return reporterAccessControlService;
	}
}
