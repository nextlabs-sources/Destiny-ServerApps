/*
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

import static com.nextlabs.destiny.inquirycenter.SharedUtils.getFormatedDate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.AlertDO;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.MonitorDO;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.MonitorTagDO;
import com.bluejungle.destiny.services.management.UserRoleServiceStub;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupServiceStub;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.nextlabs.destiny.inquirycenter.monitor.dao.MonitorDAO;
import com.nextlabs.destiny.inquirycenter.monitor.dao.MonitorDAOImpl;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.ReportDataManager;
import com.nextlabs.destiny.inquirycenter.savedreport.service.ReporterAccessControlService;
import com.nextlabs.destiny.inquirycenter.savedreport.service.impl.ReporterAccessControlServiceImpl;
import com.nextlabs.report.datagen.ResultData;

import net.sf.hibernate.HibernateException;

/**
 * @author nnallagatla
 *
 */
public class MonitoringService {

	private static MonitorDAO monitorDAO = new MonitorDAOImpl();
	private static AlertService alertService = new AlertService();

	private UserRoleServiceStub userService;
	private UserGroupServiceStub userGroupService;
	private ReporterAccessControlService reporterAccessControlService;

	static final Log LOG = LogFactory.getLog(MonitoringService.class.getName());

	/**
	 * 
	 * @param monitor
	 */
	public void add(MonitorDO monitor) {
		LOG.debug("add monitor");
		if (monitor.getName() == null || monitor.getDescription() == null || monitor.getAlertMessage() == null) {
			LOG.error("Monitor data incomplete");
		}

		UUID id = UUID.randomUUID();
		// store UID as string
		monitor.setMonitorUID(id.toString());

		try {
			monitorDAO.create(monitor);
		} catch (HibernateException e) {
			LOG.error("Operation failed", e);
		}
	}

	/**
	 * 
	 * @param monitor
	 */
	public void edit(MonitorDO monitor) {
		LOG.debug("edit monitor");
		if (monitor.getName() == null || monitor.getDescription() == null || monitor.getName().isEmpty()
				|| monitor.getDescription().isEmpty()) {
			LOG.error("Monitor data incomplete");
		}

		try {
			monitorDAO.update(monitor);
		} catch (HibernateException e) {
			LOG.error("Operation failed", e);
		}
	}

	/**
	 * 
	 * @return @throws JSONException @throws
	 */
	public JSONArray getMonitorsJSON(HttpServletRequest request) throws Exception {
		List<MonitorDO> monitors = null;
		JSONArray arr = new JSONArray();

		ILoggedInUser currentUser = AppContext.getContext(request).getRemoteUser();

		try {
			monitors = monitorDAO.getAll();
		} catch (HibernateException e) {
			LOG.error("Operation failed", e);
			return arr;
		}

		UserGroupReduced[] userGroups = getReporterAccessControlService(request).findGroupsForUser(currentUser);
		List<Long> userGrpIds = new ArrayList<Long>(5);

		if (userGroups != null) {
			for (UserGroupReduced userGroup : userGroups) {
				userGrpIds.add(userGroup.getId().getID().longValue());
			}
		}

		for (MonitorDO monitor : monitors) {
			boolean hasAccess = getReporterAccessControlService(request).hasMonitorAccess(currentUser, userGrpIds,
					monitor);
			if (monitor.getOwnerId().equals(currentUser.getPrincipalId()) && hasAccess) {
				arr.put(getJSON(monitor, true));
			} else if (monitor.getSharedMode().equals(MonitorDO.SAVED_REPORT_USERS) && hasAccess) {
				arr.put(getJSON(monitor, false));
			} else if (monitor.getSharedMode().equals(MonitorDO.SAVED_REPORT_PUBLIC)) {
				arr.put(getJSON(monitor, false));
			}
		}

		return arr;
	}

	/**
	 * Returns monitor UID and name as JSONArray. Needed for monitor dropdown in
	 * Alerts page and also charting
	 * 
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getMonitorsUIDNameInfo() throws JSONException {
		List<MonitorDO> monitors = null;
		JSONArray arr = new JSONArray();

		try {
			monitors = monitorDAO.getAll();
		} catch (HibernateException e) {
			LOG.error("Operation failed", e);
			return arr;
		}

		for (MonitorDO mon : monitors) {
			JSONObject monitor = new JSONObject();

			monitor.put("monitor_uid", mon.getMonitorUID());
			monitor.put("name", mon.getName());

			arr.put(monitor);
		}
		return arr;
	}

	public JSONObject getJSON(MonitorDO monitor, boolean canEdit) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.accumulate("id", monitor.getId()).accumulate("name", monitor.getName())
				.accumulate("description", monitor.getDescription()).accumulate("monitor_uid", monitor.getMonitorUID())
				.accumulate("created_at",
						(monitor.getCreatedAt() != null) ? getFormatedDate(monitor.getCreatedAt()) : "")
				.accumulate("updated_at",
						(monitor.getLastUpdatedAt() != null) ? getFormatedDate(monitor.getLastUpdatedAt()) : "")
				.accumulate("is_active", monitor.isActive()).accumulate("shared_mode", monitor.getSharedMode())
				.accumulate("owner_id", monitor.getOwnerId()).accumulate("can_edit", canEdit);

		if (monitor.getTags() != null && monitor.getTags().get("level") != null) {
			obj.accumulate("level", monitor.getTags().get("level").getValue());
		} else {
			obj.accumulate("level", "NA");
		}
		return obj;
	}

	/**
	 * 
	 * @return
	 */
	public List<MonitorDO> list() {
		List<MonitorDO> monitors = null;
		try {
			monitors = monitorDAO.getAll();
		} catch (HibernateException e) {
			LOG.error("Error while fetching monitors", e);
		}
		return monitors;
	}

	public boolean deleteMonitor(Long id) {
		boolean success = true;
		try {
			MonitorDO monitor = monitorDAO.lookup(id);
			monitorDAO.delete(monitor);
		} catch (HibernateException e) {
			success = false;
			LOG.error(e);
		}

		return success;
	}

	public boolean deactivateMonitor(Long id) {
		boolean success = true;
		try {
			MonitorDO monitor = monitorDAO.lookup(id);
			monitor.setActive(false);
			monitorDAO.directUpdate(monitor);
		} catch (HibernateException e) {
			success = false;
			LOG.error(e);
		}

		return success;
	}

	public boolean activateMonitor(Long id) {
		boolean success = true;
		try {
			MonitorDO monitor = monitorDAO.lookup(id);
			monitor.setActive(true);
			monitorDAO.directUpdate(monitor);
		} catch (HibernateException e) {
			success = false;
			LOG.error(e);
		}

		return success;
	}

	public JSONObject getMonitorJSON(HttpServletRequest request, long id) throws JSONException {
		JSONObject result = null;
		try {
			MonitorDO monitor = monitorDAO.lookup(id);
			result = getDetailsJSON(request, monitor);
		} catch (Exception e) {
			LOG.error(e);
		}

		return result;
	}

	public JSONObject getDetailsJSON(HttpServletRequest request, MonitorDO monitor) throws Exception {
		ILoggedInUser currentUser = AppContext.getContext(request).getRemoteUser();

		JSONObject obj = new JSONObject();

		obj.put("id", monitor.getId()).put("name", monitor.getName()).put("description", monitor.getDescription())
				.put("created_at", (monitor.getCreatedAt() != null) ? getFormatedDate(monitor.getCreatedAt()) : "")
				.put("updated_at",
						(monitor.getLastUpdatedAt() != null) ? getFormatedDate(monitor.getLastUpdatedAt()) : "")
				.put("is_active", monitor.isActive()).put("monitor_uid", monitor.getMonitorUID())
				// .put("duration", getFilters().get("duration"))
				.put("message", monitor.getAlertMessage())
				// .put("log_to_dashboard", monitor.isShowInDashboard())
				.put("auto_dismiss", monitor.isAutoDismiss()).put("send_email", monitor.isSendEmail())
				.put("email", monitor.getEmailAddress())
				.put("can_edit", (monitor.getOwnerId().equals(currentUser.getPrincipalId())) ? true : false)
				.put("level", monitor.getTags().get("level"));

		obj.put("criteria", new JSONObject(monitor.getCriteriaJSON()));

		if (monitor.getTags() != null) {
			obj.put("tags", getTagJSON(monitor.getTags().values()));
		}
		return obj;
	}

	private static Object getTagJSON(Collection<MonitorTagDO> values) throws JSONException {
		JSONArray tagArray = new JSONArray();

		if (values == null || values.size() == 0) {
			return tagArray;
		}

		for (MonitorTagDO tag : values) {
			tagArray.put(getJSON(tag));
		}

		LOG.debug("tags: " + tagArray.toString());

		return tagArray;
	}

	public static JSONObject getJSON(MonitorTagDO tag) throws JSONException {
		// {"name":"key","value":"value"}
		JSONObject obj = new JSONObject();
		obj.put("name", tag.getName()).put("value", tag.getValue());

		return obj;
	}

	public MonitorDO getMonitorById(long id) {
		MonitorDO monitor = null;
		try {
			monitor = monitorDAO.lookup(id);
		} catch (HibernateException e) {
			LOG.error(e);
		}

		return monitor;
	}

	/**
	 * This method will return the active monitors
	 * 
	 * @return
	 */
	public List<MonitorDO> getRunnableMonitors() {
		List<MonitorDO> monitors = null;
		try {
			monitors = monitorDAO.getRunnableMonitors();
		} catch (HibernateException e) {
			LOG.error("Error while fetching runnable monitors", e);
		}
		return monitors;
	}

	/**
	 * This method will execute the given monitor and persists alert if raised
	 * 
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	public AlertDO executeMonitorAndPersistAlert(MonitorDO monitor) throws Exception {
		ReportDataManager mgr = new ReportDataManager();
		JSONObject obj = new JSONObject(monitor.getCriteriaJSON());
		obj.put("monitor_created_date", getFormatedDate(monitor.getCreatedAt()));

		// setting the header to empty, for alert generation not required select
		// columns
		obj.put("header", new JSONArray());

		ResultData results = mgr.getPADetailsTableDataV2(obj.toString());
		AlertDO alert = null;
		if (results != null) {
			int numberOfRecords = results.size();
			if (numberOfRecords > 0) {
				alert = new AlertDO();

				Calendar dayCal = getCalendar();
				alert.setDay(dayCal.getTimeInMillis());

				Calendar monthCal = getCalendar();
				monthCal.set(Calendar.DAY_OF_MONTH, 1);
				alert.setMonth(monthCal.getTimeInMillis());
				alert.setYear(monthCal.get(Calendar.YEAR));

				alert.setTriggeredAt(new Timestamp(System.currentTimeMillis()));
				alert.setMonitor(monitor);
				alert.setMonitorName(monitor.getName());
				alert.setMonitorUID(monitor.getMonitorUID());
				alert.setDeleted(false);
				alert.setDismissed(monitor.isAutoDismiss());
				alert.setAlertMessage(monitor.getAlertMessage());
				Map<String, MonitorTagDO> tags = monitor.getTags();
				MonitorTagDO levelTag = tags.get("level");

				alert.setLevel(levelTag != null ? levelTag.getValue() : "L1");
				alertService.addAlert(alert);
			}
		}
		return alert;
	}

	private Calendar getCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public List<MonitorDO> lookupByMonitorName(String name) throws HibernateException {
		return monitorDAO.lookupByMonitorName(name);
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
