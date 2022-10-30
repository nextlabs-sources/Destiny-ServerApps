/*
 * Created on Jun 12, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.MonitorDO;
import com.bluejungle.destiny.services.management.UserRoleServiceStub;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupServiceStub;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.inquirycenter.monitor.dao.MonitorDAO;
import com.nextlabs.destiny.inquirycenter.monitor.dao.MonitorDAOImpl;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.DataGeneratorFactory;
import com.nextlabs.destiny.inquirycenter.savedreport.service.ReporterAccessControlService;
import com.nextlabs.destiny.inquirycenter.savedreport.service.impl.ReporterAccessControlServiceImpl;
import com.nextlabs.report.datagen.ResultData;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nnallagatla
 *
 */
public class AlertDataManager {

	private Log log = LogFactory.getLog(AlertDataManager.class.getName());
	Session session;
	IAlertDataGenerator dataGen;

	private static MonitorDAO monitorDAO = new MonitorDAOImpl();

	private UserRoleServiceStub userService;
	private UserGroupServiceStub userGroupService;
	private ReporterAccessControlService reporterAccessControlService;

	private void init() throws Exception {
		IHibernateRepository activityDataSrc = SharedLib.getActivityDataSource();
		try {
			session = activityDataSrc.getSession();

			if (session == null) {
				log.error("session is null");
			}

			dataGen = DataGeneratorFactory.getInstance().getAlertDataGenerator(session);
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("Could not obtain session or generate dataGen", ex);
			}
			throw ex;
		}
	}

	private void cleanup() throws Exception {
		if (dataGen != null) {
			dataGen.cleanup();
		}
		if (session != null) {
			try {
				session.close();
				session = null;
			} catch (Exception ex) {
				if (log.isErrorEnabled()) {
					log.error("Could not close session", ex);
				}
				throw ex;
			}
		}
	}

	public JSONObject getResultData(HttpServletRequest request, String jsonQuery) throws Exception {
		ResultData data = null;
		JSONObject jsonResults = null;
		try {
			init();
			data = dataGen.getResultData(jsonQuery);
			jsonResults = convertToJSON(request, data);
			jsonResults.put("param_obj", getParamObject((AbstractAlertDataGenerator) dataGen));
		} catch (Exception e) {
			log.error(e);
			throw e;
		} finally {
			cleanup();
		}
		return jsonResults;
	}

	/**
	 * This data is needed on the client side to populate missing dates for
	 * group by date
	 * 
	 * @param dataGen
	 * @return
	 * @throws JSONException
	 */
	private JSONObject getParamObject(AbstractAlertDataGenerator dataGen) throws JSONException {
		JSONObject paramObj = new JSONObject();
		paramObj.put("begin_date", dataGen.getBeginDate());
		paramObj.put("end_date", dataGen.getEndDate());
		paramObj.put("report_type", dataGen.getReportType());
		paramObj.put("grouping_field", dataGen.getGroupingField());
		return paramObj;
	}

	private JSONObject convertToJSON(HttpServletRequest request, ResultData results) throws Exception {

		JSONObject jsonResults = new JSONObject();

		if (results == null) {
			jsonResults.put("header", new JSONArray());
			jsonResults.put("data", new JSONArray());

			return jsonResults;
		}

		ILoggedInUser currentUser = AppContext.getContext(request).getRemoteUser();

		UserGroupReduced[] userGroups = getReporterAccessControlService(request).findGroupsForUser(currentUser);
		List<Long> userGrpIds = new ArrayList<Long>(5);

		if (userGroups != null) {
			for (UserGroupReduced userGroup : userGroups) {
				userGrpIds.add(userGroup.getId().getID().longValue());
			}
		}

		Map<String, MonitorDO> monitorUUIDMap = populateMonitors();

		JSONArray header = new JSONArray();
		JSONArray data = new JSONArray();

		int numberOfCols = results.getRowCount();

		int numberOfRecords = results.size();

		for (int i = 0; i < numberOfRecords; i++) {
			boolean canEdit = false;
			boolean canView = false;
			JSONObject record = new JSONObject();

			for (int j = 0; j < numberOfCols; j++) {
				String columnName = results.getName(j) == null ? "" : results.getName(j).toLowerCase();

				Object cellValue = results.get(i, j);

				if (columnName.equals("monitor_uid")) {

					MonitorDO monitor = monitorUUIDMap.get((String) cellValue);
					if (monitor != null) {

						canView = getReporterAccessControlService(request).hasMonitorAccess(currentUser, userGrpIds,
								monitor);

						if (currentUser.getPrincipalId().equals(monitor.getOwnerId()) && canView) {
							canEdit = true;
							canView = true;
						} else {
							canEdit = false;
						}

						record.put("can_edit", canEdit);
						record.put("can_view", canView);
					}

				} else if (i == 0) {
					JSONObject col = new JSONObject();
					col.put("sTitle", columnName);
					col.put("mData", columnName);
					header.put(col);
				}

				/*
				 * if record does not contain value for this property insert
				 * empty string so that properties in header and record match
				 */
				record.put(columnName, cellValue == null ? "" : cellValue);
			}
			data.put(record);
		}

		jsonResults.put("header", header);
		jsonResults.put("data", data);

		return jsonResults;
	}

	private Map<String, MonitorDO> populateMonitors() throws HibernateException {
		List<MonitorDO> monitors = monitorDAO.getNonArchivedMonitors();
		Map<String, MonitorDO> monitorUUIDMap = new HashMap<String, MonitorDO>();
		for (MonitorDO monitorDO : monitors) {
			monitorUUIDMap.put(monitorDO.getMonitorUID(), monitorDO);
		}

		return monitorUUIDMap;
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

	public ReporterAccessControlService getReporterAccessControlService(HttpServletRequest request) throws Exception {
		if (reporterAccessControlService == null) {
			reporterAccessControlService = new ReporterAccessControlServiceImpl();
			// reporterAccessControlService = new
			// ReporterAccessControlServiceImpl(
			// getUserService(request), getUserGroupServiceIF(request));
		}

		return reporterAccessControlService;
	}
}
