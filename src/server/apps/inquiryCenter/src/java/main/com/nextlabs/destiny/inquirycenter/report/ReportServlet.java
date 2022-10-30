/*
 * Created on Apr 8, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.destiny.inquirycenter.SharedUtils;

/**
 * <p>
 * ReportServlet to serve all the report related requests and functionality
 * handling.
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public class ReportServlet extends HttpServlet {

	private static final long serialVersionUID = -1L;

	private static final Log log = LogFactory.getLog(ReportServlet.class);

	private static final String GET_MAPPING_DATA = "GET_MAPPING_DATA";
	private static final String VALIDATE_REPORT_NAME = "VALIDATE_REPORT_NAME";
	private static final String SAVE_REPORT = "SAVE_REPORT";
	private static final String DELETE_REPORT = "DELETE_REPORT";
	private static final String SEARCH_SAVED_REPORT = "SEARCH_SAVED_REPORT";
	private static final String LOAD_SAVED_REPORT = "LOAD_SAVED_REPORT";
	private static final String POLICY_LOOKUP = "POLICY_LOOKUP";
	private static final String POLICY_LOOKUP_PAGINATED = "POLICY_LOOKUP_PAGINATED";
	private static final String USER_LOOKUP = "USER_LOOKUP";
	private static final String USER_LOOKUP_PAGINATED = "USER_LOOKUP_PAGINATED";
	private static final String LOAD_ACTIONS_DATA = "LOAD_ACTIONS_DATA";
	private static final String LOAD_ACTIONS_WITH_PM_DATA = "LOAD_ACTIONS_WITH_PM_DATA";
	private static final String FETCH_BAR_CHART_DATA = "FETCH_BAR_CHART_DATA";
	private static final String FETCH_PIE_CHART_DATA = "FETCH_PIE_CHART_DATA";
	private static final String LIST_USER_REPORTS = "LIST_USER_REPORTS";
	private static final String LIST_APPLICATION_USERS = "LIST_APPLICATION_USERS";
	private static final String LIST_USER_GROUPS = "LIST_USER_GROUPS";
	private static final String CHECK_USER_ACCESS = "CHECK_USER_ACCESS";
	
	private static final String TABLE_REPORT_DATA = "TABLE_REPORT_DATA";
	private static final String TABLE_REPORT_DATA_COUNT = "TABLE_REPORT_DATA_COUNT";
	private static final String TABLE_REPORT_DATA_EXPORT = "TABLE_REPORT_DATA_EXPORT";
	private static final String DETAIL_REPORT_DATA = "DETAIL_REPORT_DATA";
	private static final String DETAIL_REPORT_DATA_EXPORT = "DETAIL_REPORT_DATA_EXPORT";

	private static final String SHARE_POINT_LOAD_ALL_REPORTS = "SHARE_POINT_LOAD_ALL_REPORTS";
	private static final String SHARE_POINT_LOAD_REPORT_FORM = "SHARE_POINT_LOAD_REPORT_FORM";
	private static final String SHARE_POINT_BAR_REPORT = "SHARE_POINT_BAR_REPORT";
	private static final String SHARE_POINT_PIE_REPORT = "SHARE_POINT_PIE_REPORT";

	private static final String CUSTOM_APP_RPT_LOAD_ALL_REPORTS = "CUSTOM_APP_RPT_LOAD_ALL_REPORTS";
	private static final String CUSTOM_APP_RPT_LOAD_FORM = "CUSTOM_APP_RPT_LOAD_FORM";

	private static final String VALIDATE_USER_SESSION = "VALIDATE_USER_SESSION";

	private ReportServletHelper reportHelper;

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			super.init(config);

			ReportServletHelper.init();
		} catch (Exception e) {
			log.error("Error in initialzing ReportServlet, ", e);
			throw new ServletException(e);
		}
	}

	/**
	 * All the get requests serves here.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * All the post requests serves here.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * <p>
	 * All requests controlled by this method. it will process the data can
	 * produce the response accordingly.
	 * </p>
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws IOException
	 *             exception if any error occurred
	 */
	private void doProcess(HttpServletRequest request,
			HttpServletResponse response) {

		FacesContext currentContext = null;
		try {
			String action = request.getParameter("action");
			String payload = request.getParameter("data");

			currentContext = SharedUtils.getFacesContext(request, response);

			log.info("Request came to process : [ Action :" + action
					+ ", Payload :" + payload + "] ");

			if (GET_MAPPING_DATA.equals(action)) {
				getReportHelper().getMappingData(request, response);

			} else if (VALIDATE_REPORT_NAME.equals(action)) {
				getReportHelper().validateReportNameProcess(request, response,
						payload);

			} else if (SAVE_REPORT.equals(action)) {
				getReportHelper()
						.saveReportCriteria(request, response, payload);

			} else if (DELETE_REPORT.equals(action)) {
				getReportHelper().deleteReportCriteria(request, response,
						payload);

			} else if (SEARCH_SAVED_REPORT.equals(action)) {
				getReportHelper()
						.searchSavedReports(request, response, payload);

			} else if (LOAD_SAVED_REPORT.equals(action)) {
				getReportHelper().loadSavedReport(request, response, payload);

			} else if (POLICY_LOOKUP.equals(action)) {
				SharedUtils.policyLookupData(request, response, payload);

			} else if (POLICY_LOOKUP_PAGINATED.equals(action)) {
				SharedUtils.policyLookupDataPaginated(request, response, payload);

			} else if (USER_LOOKUP.equals(action)) {
				SharedUtils.userLookupData(request, response, payload);

			} else if (USER_LOOKUP_PAGINATED.equals(action)) {
				SharedUtils.userLookupDataPaginated(request, response, payload);

			} else if (FETCH_PIE_CHART_DATA.equals(action)) {
				getReportHelper().fetchPIEChartData(request, response, payload);

			} else if (FETCH_BAR_CHART_DATA.equals(action)) {
				getReportHelper().fetchBarChartData(request, response, payload);

			} else if (LIST_USER_REPORTS.equalsIgnoreCase(action)) {
				getReportHelper().listSavedReportsByUser(request, response,
						payload);

			} else if (SHARE_POINT_LOAD_ALL_REPORTS.equalsIgnoreCase(action)) {
				getReportHelper().loadAllSharePointReports(request, response);

			} else if (SHARE_POINT_LOAD_REPORT_FORM.equalsIgnoreCase(action)) {
				getReportHelper().loadSharePointReportForm(request, response,
						payload);

			} else if (SHARE_POINT_BAR_REPORT.equalsIgnoreCase(action)) {
				getReportHelper().loadSharePointBarChart(request, response,
						payload);

			} else if (SHARE_POINT_PIE_REPORT.equalsIgnoreCase(action)) {
				getReportHelper().loadSharePointPIEChart(request, response,
						payload);

			} else if (LOAD_ACTIONS_DATA.equalsIgnoreCase(action)) {
				getReportHelper().loadActionData(request, response);
				
			} else if (LOAD_ACTIONS_WITH_PM_DATA.equalsIgnoreCase(action)) {
				getReportHelper().loadActionGroupByPolicyModel(request, response);

			} else if (CUSTOM_APP_RPT_LOAD_ALL_REPORTS.equalsIgnoreCase(action)) {
				getReportHelper().loadAllCustomAppNames(request, response,
						payload);

			} else if (CUSTOM_APP_RPT_LOAD_FORM.equalsIgnoreCase(action)) {
				getReportHelper().loadCustomAppReportForm(request, response,
						payload);

			} else if (VALIDATE_USER_SESSION.equalsIgnoreCase(action)) {
				getReportHelper().validateUserSession(request, response,
						payload);
			} else if (LIST_APPLICATION_USERS.equalsIgnoreCase(action)) {
				getReportHelper().loadAllApplicationUsers(request, response);
				
			} else if (CHECK_USER_ACCESS.equalsIgnoreCase(action)) {
				getReportHelper().checkUserAccess(request, response);
				
			} else if (LIST_USER_GROUPS.equalsIgnoreCase(action)) {
				getReportHelper().loadAllUSerGroups(request, response);
				
			} else if (TABLE_REPORT_DATA.equalsIgnoreCase(action)) {
				getReportHelper().loadTableReportData(request, response, payload);
				
			} else if (TABLE_REPORT_DATA_COUNT.equalsIgnoreCase(action)) {
				getReportHelper().getTableReportDataCount(request, response, payload);

			} else if (TABLE_REPORT_DATA_EXPORT.equalsIgnoreCase(action)) {
				getReportHelper().exportTableData(request, response, payload);
				
			} else if (DETAIL_REPORT_DATA.equalsIgnoreCase(action)) {
				getReportHelper().loadDetailReport(request, response, payload);
				
			} else if (DETAIL_REPORT_DATA_EXPORT.equalsIgnoreCase(action)) {
				getReportHelper().exportDetailData(request, response, payload);
				
			}
			
		} catch (Exception e) {
			log.error("Error encountered in processing the request,", e);
		} finally {
			if(currentContext != null)
				currentContext.release();
		}
	}

	public ReportServletHelper getReportHelper() {
		if (reportHelper == null) {
			reportHelper = new ReportServletHelper();
		}
		return reportHelper;
	}

}
