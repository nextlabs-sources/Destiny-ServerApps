/*
 * Created on Jun 13, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report;

import static com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportNavigatorBeanImpl.SUPER_USER_USERNAME;
import static com.nextlabs.destiny.inquirycenter.AuditLogger.log;
import static com.nextlabs.destiny.inquirycenter.web.filter.ApplicationUserDetailsFilter.MANAGE_REPORTER_ACTION;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportNavigatorBeanImpl;
import com.bluejungle.destiny.services.management.UserRoleServiceStub;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupReducedList;
import com.bluejungle.destiny.services.management.types.UserGroupServiceStub;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.pf.destiny.lifecycle.DevelopmentEntity;
import com.bluejungle.pf.destiny.lifecycle.EntityType;
import com.nextlabs.destiny.container.shared.customapps.mapping.CustomReportJO;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.SavedReportDO;
import com.nextlabs.destiny.inquirycenter.SharedUtils;
import com.nextlabs.destiny.inquirycenter.customapps.CustomAppJO;
import com.nextlabs.destiny.inquirycenter.customapps.ExternalReportAppManager;
import com.nextlabs.destiny.inquirycenter.customapps.IExternalReportApplication;
import com.nextlabs.destiny.inquirycenter.monitor.service.ReportingService;
import com.nextlabs.destiny.inquirycenter.rcireport.RCIReportDataModel;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.CustomAttributeData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.DataGeneratorFactory;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.GroupByData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.ObligationLogData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.PADetailsTableData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.PALogDetailsData;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.ReportDataManager;
import com.nextlabs.destiny.inquirycenter.report.util.ExcelReportGenerator;
import com.nextlabs.destiny.inquirycenter.report.util.PDFReportGenerator;
import com.nextlabs.destiny.inquirycenter.savedreport.service.ReporterAccessControlService;
import com.nextlabs.destiny.inquirycenter.savedreport.service.SavedReportService;
import com.nextlabs.destiny.inquirycenter.savedreport.service.impl.ReporterAccessControlServiceImpl;
import com.nextlabs.destiny.inquirycenter.savedreport.service.impl.SavedReportServiceImpl;
import com.nextlabs.destiny.inquirycenter.sharepoint.datagen.DynamicField;
import com.nextlabs.destiny.inquirycenter.sharepoint.datagen.ISharePointDataGenerator;
import com.nextlabs.destiny.inquirycenter.sharepoint.datagen.SharePointCriteriaJSONModel;
import com.nextlabs.destiny.inquirycenter.sharepoint.datagen.SharePointReportTypeEnum;
import com.nextlabs.destiny.inquirycenter.user.service.AppUserMgmtService;
import com.nextlabs.destiny.inquirycenter.web.filter.ApplicationUserDetailsFilter;
import com.nextlabs.report.datagen.ResultData;

import net.sf.hibernate.HibernateException;

/**
 * <p>
 * ReportServletHelper
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public class ReportServletHelper {

    private static final Log log = LogFactory.getLog(ReportServletHelper.class);

    private SavedReportService savedReportService;
    private ResourceLookupService resourceLookupService;
    private ReporterAccessControlService reporterAccessControlService;
    private ReportDataManager reportDataManager;
    private AppUserMgmtService appUserMgmtService;
    private UserRoleServiceStub userService;
    private UserGroupServiceStub userGroupService;
    private static IExternalReportApplication extRepAppMgr;
    private Map<Integer, RCIReportDataModel> rciReportMap = new HashMap<Integer, RCIReportDataModel>();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");


    public ReportServletHelper() {

    }

    /**
     * <p>
     * Initialize the components
     * </p>
     * 
     * @throws Exception
     * 
     */
    public static void init() throws Exception {
        IComponentManager compMgr = ComponentManagerFactory
                .getComponentManager();
        extRepAppMgr = (IExternalReportApplication) compMgr
                .getComponent(ExternalReportAppManager.class);
        if (extRepAppMgr == null) {
            throw new RuntimeException(
                    "Could not access the  external report "
                            + " component - the server may not have been initialized correctly");
        }
        extRepAppMgr.load();
    }

    public void listSavedReportsByUser(HttpServletRequest request,
            HttpServletResponse response, String payload) {
        log.info("Request came to search for list user reports");
        JSONObject resObj = new JSONObject();

        try {

            JSONObject reqObj = new JSONObject(payload);
            boolean needSharedReport = reqObj.getBoolean("show_shared_reports");
            ILoggedInUser user = AppContext.getContext(request).getRemoteUser();
            Long userId = user.getPrincipalId();

            log.info(" List saved reports by user [ User Id :" + userId + "]");

            List<SavedReportDO> savedReports = getSavedReportService()
                    .getSavedReportsForUser(userId, needSharedReport);

            UserGroupReduced[] userGroups = getReporterAccessControlService(
                    request).findGroupsForUser(user);
            List<Long> userGrpIds = new ArrayList<Long>(5);

            if (userGroups != null) {
                for (UserGroupReduced userGroup : userGroups) {
                    userGrpIds.add(userGroup.getId().getID().longValue());
                }
            }

            JSONArray savedRptsArr = new JSONArray();

            for (SavedReportDO report : savedReports) {

                boolean hasAccess = getReporterAccessControlService(request)
                        .hasReportAccess(user, userGrpIds, report);
                if (!hasAccess)
                    continue;

                JSONObject obj = new JSONObject();
                obj.put("id", report.getId());
                String reportName = report.getSharedMode().equals(
                        SavedReportDO.SAVED_REPORT_ONLY_ME) ? report.getTitle()
                        : report.getTitle() + " (S)";
                obj.put("report_name", reportName);
                savedRptsArr.put(obj);
            }
            resObj.put("saved_reports", savedRptsArr);

            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in search saved reports,", e);
        }
    }

    public void getTableReportDataCount(HttpServletRequest request,
                                        HttpServletResponse response, String payload) {
        try {
            JSONObject jsonData = new JSONObject(payload);
            String jRptCriteria = jsonData.getString("report_criteria");
            int count = getTotalCount(jRptCriteria);
            JSONObject responseData = new JSONObject();
            responseData.put("recordsTotal", count);
            SharedUtils.writeJSONResponse(request, response, responseData);
        } catch (Exception e) {
            log.error("Error encountered while getting Table Report Data Count,", e);
        }
    }

    public void loadTableReportData(HttpServletRequest request,
            HttpServletResponse response, String payload) {

        try {
            JSONObject jsonData = new JSONObject(payload);
            String jRptCriteria = jsonData.getString("report_criteria");

            ReportResultData resultData = getChartData(jRptCriteria);

            JSONObject responseData = new JSONObject();

            JSONArray headerData = new JSONArray();
            headerData.put("id");
            headerData.put("Date");

            JSONArray headerArr = (JSONArray) new JSONObject(jRptCriteria)
                    .get("header");

            for (int i = 0; i < headerArr.length(); i++) {
                headerData.put(headerArr.get(i));
            }

            responseData.accumulate("header", headerData);

            int headerCount = headerData.length();
            JSONArray reportData = new JSONArray();

            for (int i = 0; i < resultData.size(); i++) {

                JSONArray rowData = new JSONArray();
                for (int j = 0; j < headerCount; j++) {
                    rowData.put(resultData.get(i, j));
                }
                reportData.put(rowData);
            }

            // responseData.put("total", getTotalCount(jRptCriteria));
            responseData.put("more", resultData.isHasMore());
            responseData.accumulate("report_data", reportData);
            SharedUtils.writeJSONResponse(request, response, responseData);
        } catch (Exception e) {
            log.error("Error encountered in load Table Report Data,", e);
        }
    }
    
    
    public void loadDetailReport(HttpServletRequest request,
            HttpServletResponse response, String payload) {
        try {
            JSONObject jsonData = new JSONObject(payload);
            String logId = jsonData.getString("log_id");

            PALogDetailsData detailData = getReportDataManager().getPALogDetailsDataV2(logId);

            JSONObject responseData = new JSONObject();
            JSONObject eventData = new JSONObject();
            JSONArray customAttributesData = new JSONArray();
            JSONArray obligationsData = new JSONArray();
            
            responseData.accumulate("event_data", eventData);
            responseData.accumulate("cus_attr_data", customAttributesData);
            responseData.accumulate("oblig_data", obligationsData);
            
            populateEventData(detailData, eventData);
            populateCustomAttribs(detailData, customAttributesData);
            populateObligations(detailData, obligationsData);
            
            SharedUtils.writeJSONResponse(request, response, responseData);
        } catch (Exception e) {
            log.error("Error encountered in loading Detail Report,", e);
        }
    }

    private void populateObligations(PALogDetailsData detailData,
            JSONArray obligationsData) throws JSONException {
        List<ObligationLogData> obligationLogs = detailData.getObligationLogData();
        if(obligationLogs == null) return;
        
        for(ObligationLogData obligLog : obligationLogs) {
                JSONObject obligationData = new JSONObject();
                obligationData.put("name", obligLog.getName());
                obligationData.put("attr_one", obligLog.getAttributeOne());
                obligationData.put("attr_two", obligLog.getAttributeTwo());
                obligationData.put("attr_three", obligLog.getAttributeThree());
                
                obligationsData.put(obligationData);
        }
    }

    private void populateCustomAttribs(PALogDetailsData detailData,
            JSONArray customAttributesData) throws JSONException {
        List<CustomAttributeData> custmAttribs = detailData.getCustAttrData();
        
        if (custmAttribs == null)
            return;

        for (CustomAttributeData attrib : custmAttribs) {
            JSONObject attributeData = new JSONObject();
            attributeData.put("id", attrib.getId());
            attributeData.put("policy_log_id", attrib.getLogId());
            attributeData.put("attribute_name", attrib.getAttributeName());
            attributeData.put("attribute_value", attrib.getAttributeValue());

            customAttributesData.put(attributeData);
        }
    }

    private void populateEventData(PALogDetailsData detailData,
            JSONObject eventData) throws JSONException {
        
        PADetailsTableData paTableData = detailData.getSingleLogDetailsData();
        eventData.put("policy", paTableData.getPolicyFullName());
        eventData.put("date", paTableData.getTime());
        eventData.put("user", paTableData.getUserName());
        eventData.put("from_resource_name", paTableData.getFromResourceName());
        eventData.put("to_resource_name", paTableData.getToResourceName());
        eventData.put("host_name", paTableData.getHostName());
        eventData.put("host_ip", paTableData.getHostIP());
        eventData.put("application_name", paTableData.getApplicationName());
        eventData.put("action", paTableData.getAction());
        eventData.put("enforcement", paTableData.getPolicyDecision());
        eventData.put("log_level", paTableData.getLogLevel());
    }
    
    
    public void exportTableData(HttpServletRequest request,
            HttpServletResponse response, String payload) {

        try {
            JSONObject jsonData = new JSONObject(payload);
            String exportType = jsonData.getString("export_type"); // PDF or EXCEL
            String jRptCriteria = jsonData.getString("report_criteria");

            ResultData resultData = getChartData(jRptCriteria);
            
            // Populate header Rows
            List<String> headerCols = new ArrayList<String>();
            headerCols.add("id");
            headerCols.add("Date");

            JSONArray headerArr = (JSONArray) new JSONObject(jRptCriteria).get("header");

            for (int i = 0; i < headerArr.length(); i++) {
                headerCols.add(headerArr.getString(i));
            }

            // Populate Data Rows
            List<List<Object>> dataRows = new ArrayList<List<Object>>();

            for (int i = 0; i < resultData.size(); i++) {

                List<Object> rowData = new ArrayList<Object>();
                for (int j = 0; j < headerCols.size(); j++) {
                    if ("plc_POLICY_FULLNAME".equals(headerCols.get(j))) {
                        String policyFullname = (String) resultData.get(i, j);
                        int beginIndex = policyFullname.lastIndexOf("/");
                        policyFullname = policyFullname.substring(beginIndex+1);
                        rowData.add(policyFullname);
                    } else {
                        rowData.add(resultData.get(i, j));
                    }
                }
                dataRows.add(rowData);
            }

            ByteArrayOutputStream outStream = null;
            
            if ("PDF".equals(exportType)) {
                String pdfFileName = "Reporter_" + sdf.format(Calendar.getInstance().getTime()) + ".pdf";
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "attachment; filename=" + pdfFileName);
                
                outStream = PDFReportGenerator.generateTableReport(headerCols, dataRows);

            } else if ("EXCEL".equals(exportType)) {
                String excelFileName = "Reporter_" + sdf.format(Calendar.getInstance().getTime()) + ".xls";
                response.setContentType("application/vnd.ms-excel");
                response.addHeader("Content-Disposition", "attachment; filename=" + excelFileName);

                outStream = ExcelReportGenerator.generateTableReport(headerCols, dataRows);
            }
            
            if(outStream != null) {
                log.debug("Setting cookies and headers for file download");
                Cookie cookie = new Cookie("fileDownload", "true");
                cookie.setPath("/");
                response.addCookie(cookie);
                response.setHeader("X-Frame-Options", "SAMEORIGIN");

                ServletOutputStream servletOutStream = response.getOutputStream();
                servletOutStream.write(outStream.toByteArray());
                servletOutStream.flush();
                
                response.setContentLength(outStream.size());
                outStream.close(); // close the byte array output stream
            }
        } catch (Exception e) {
            log.error("Error encountered in exporting report data,", e);
        }
    }
    
    public void exportDetailData(HttpServletRequest request,
            HttpServletResponse response, String payload) {

        try {
            JSONObject jsonData = new JSONObject(payload);
            String exportType = jsonData.getString("export_type"); // PDF or EXCEL
            String logId = jsonData.getString("log_id");

            PALogDetailsData detailData = getReportDataManager()
                    .getPALogDetailsDataV2(logId);

            ByteArrayOutputStream outStream = null;

            if ("PDF".equals(exportType)) {
                String pdfFileName = "Log_Detail_Report_"
                        + sdf.format(Calendar.getInstance().getTime()) + ".pdf";
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition",
                        "attachment; filename=" + pdfFileName);

                outStream = PDFReportGenerator.generateDetailReport(detailData);

            } else if ("EXCEL".equals(exportType)) {
                String excelFileName = "Log_Detail_Report_"
                        + sdf.format(Calendar.getInstance().getTime()) + ".xls";
                response.setContentType("application/vnd.ms-excel");
                response.addHeader("Content-Disposition",
                        "attachment; filename=" + excelFileName);

                outStream = ExcelReportGenerator
                        .generateDetailReport(detailData);
            }

            if (outStream != null) {
                log.debug("Setting cookies and headers for file download");
                Cookie cookie = new Cookie("fileDownload", "true");
                cookie.setPath("/");
                response.addCookie(cookie);
                response.setHeader("X-Frame-Options", "SAMEORIGIN");

                ServletOutputStream servletOutStream = response
                        .getOutputStream();
                servletOutStream.write(outStream.toByteArray());
                servletOutStream.flush();

                response.setContentLength(outStream.size());
                outStream.close(); // close the byte array output stream
            }
        } catch (Exception e) {
            log.error("Error encountered in exporting Detail report data,", e);
        }
    }

    public void fetchPIEChartData(HttpServletRequest request,
            HttpServletResponse response, String payload) {

        try {
            JSONObject jsonData = new JSONObject(payload);
            String jRptCriteria = jsonData.getString("report_criteria");

            ResultData resultData = getChartData(jRptCriteria);

            JSONArray pieChartData = populateChartData(resultData);

            SharedUtils.writeJSONResponse(request, response, pieChartData);
        } catch (Exception e) {
            log.error("Error encountered in loading saved Reports,", e);
        }
    }

    public void fetchBarChartData(HttpServletRequest request,
            HttpServletResponse response, String payload) {

        try {
            JSONObject jsonData = new JSONObject(payload);
            String jRptCriteria = jsonData.getString("report_criteria");
            ResultData resultData = getChartData(jRptCriteria);

            JSONArray barChartArry = new JSONArray();
            JSONObject barChartData = new JSONObject();
            JSONArray barChartValues = populateChartData(resultData);

            barChartData.put("values", barChartValues);
            barChartArry.put(barChartData);

            SharedUtils.writeJSONResponse(request, response, barChartArry);
        } catch (Exception e) {
            log.error("Error encountered in loading saved Reports,", e);
        }
    }

    public JSONArray populateChartData(ResultData resultData)
            throws JSONException {
        JSONArray chartValues = new JSONArray();
        for (int i = 0; i < resultData.size(); i++) {
            JSONObject jData = new JSONObject();
            jData.put("label", resultData.get(i, 0));
            jData.put("value", resultData.get(i, 1));

            chartValues.put(jData);
        }
        return chartValues;
    }

    private ReportResultData getChartData(String payload) throws Exception {
        ReportResultData resultData = getReportDataManager().getPADetailsTableDataV2(
                payload);

        return resultData;
    }
    
    private int getTotalCount(String payload) throws Exception {
        return getReportDataManager().getPADetailsTableDataTotalV2(payload);
    }

    public void getMappingData(HttpServletRequest request,
            HttpServletResponse response) {

        log.info("Request came to get Mapping Data");
        try {
            request.setCharacterEncoding("utf8");
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.print(ReportingService.getColumnMappingJSON(request
                    .getParameter("TYPE")));
            pw.flush();
            pw.close();

        } catch (Exception e) {
            log.error("Error encountered in get Mapping Data,", e);
        }
    }

    public void loadSavedReport(HttpServletRequest request,
            HttpServletResponse response, String payload) {
        log.info("Request came to load saved reports");
        JSONObject resObj = new JSONObject();

        try {
            JSONObject reqObj = new JSONObject(payload);

            log.info(reqObj.toString());

            Long reportId = reqObj.getLong("report_id");

            String jsonCriteria = null;
            JSONObject jsonData = null;
            SavedReportDO savedReport = null;
            if (reportId != -1) {
                savedReport = getSavedReportService().lookup(reportId);
                jsonCriteria = savedReport.getCriteriaJSON();
                jsonData = new JSONObject(jsonCriteria);
            } else {
                jsonData = reqObj.getJSONObject("jsonCriteria");
                jsonCriteria = jsonData.toString();
            }

            JSONObject jsonSaveInfo = jsonData.getJSONObject("save_info");
            JSONObject jSonFilter = jsonData.getJSONObject("filters");
            JSONObject jSonGeneral = jSonFilter.getJSONObject("general");

            Calendar startDate = Calendar.getInstance();
            ReportCriteriaJSONModel.convertRelativeDatesToAbsolute(jSonGeneral,
                    startDate);

            String startDateStr = jSonGeneral.getString("start_date");
            String endDateStr = jSonGeneral.getString("end_date");

            resObj.put("start_date", startDateStr);
            resObj.put("end_date", endDateStr);

            Long userId = AppContext.getContext(request).getRemoteUser()
                    .getPrincipalId();

            if (savedReport != null) {
                if (userId.equals(savedReport.getOwnerId())) {
                    resObj.put("is_owner", Boolean.TRUE);
                } else {
                    resObj.put("is_owner", Boolean.FALSE);
                }

                resObj.put("saved_report", savedReport.getCriteriaJSON());
                resObj.put("report_owner", savedReport.getOwnerId());
                resObj.put("shared_mode", savedReport.getSharedMode());
                resObj.put("isDashboardReport", savedReport.isInDashboard());
            } else {
                resObj.put("is_owner", Boolean.TRUE);
                resObj.put("saved_report", jsonCriteria.toString());
                resObj.put("report_owner", 0);
                resObj.put("shared_mode", SavedReportDO.SAVED_REPORT_ONLY_ME);
                resObj.put("isDashboardReport", Boolean.FALSE);
            }
            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in loading saved Reports,", e);
        }
    }

    public void searchSavedReports(HttpServletRequest request,
            HttpServletResponse response, String payload) {
        log.info("Request came to search for saved report");
        JSONObject resObj = new JSONObject();

        try {
            JSONObject reqObj = new JSONObject(payload);
            String reportName = reqObj.getString("report_name");
            ILoggedInUser user = AppContext.getContext(request).getRemoteUser();

            reportName = (reportName == null || reportName.isEmpty()) ? "*"
                    : reportName;

            log.info("Request came to search for saved report" + reportName);
            List<SavedReportDO> savedReports = getSavedReportService()
                    .lookupByReportName(reportName);

            log.info("Request came to search for saved report NO of records fetch :"
                    + savedReports.size());

            UserGroupReduced[] userGroups = getReporterAccessControlService(
                    request).findGroupsForUser(user);
            List<Long> userGrpIds = new ArrayList<Long>(5);

            if (userGroups != null) {
                for (UserGroupReduced userGroup : userGroups) {
                    userGrpIds.add(userGroup.getId().getID().longValue());
                }
            }

            JSONArray savedRptsArr = new JSONArray();

            for (SavedReportDO report : savedReports) {
                boolean hasAccess = getReporterAccessControlService(request)
                        .hasReportAccess(user, userGrpIds, report);
                if (!hasAccess)
                    continue;

                JSONObject obj = new JSONObject();
                obj.put("id", report.getId());
                obj.put("report_name", report.getTitle());
                savedRptsArr.put(obj);
            }
            resObj.put("saved_reports", savedRptsArr);

            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in search saved reports,", e);
        }
    }

    public void deleteReportCriteria(HttpServletRequest request,
            HttpServletResponse response, String payload) {

        log.info("Request came to delete report criteria");
        JSONObject resObj = new JSONObject();
        try {
            JSONObject reqObj = new JSONObject(payload);

            String reportName = reqObj.getString("report_name");
            SavedReportDO savedReportDO = getSavedReportService()
                    .lookupByReportName(reportName).get(0);
            getSavedReportService().delete(savedReportDO);

            log.info("Saved report deleted successfully");
            log("Saved report deleted,  [name :" + reportName + "] by " + getCurrentUserName(request));
            resObj.put("status", "SUCCESS");
            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in deleting the report,", e);
        }
        log.info("Report criteria deleted successfully");
    }

    /**
     * <p>
     * method to save the report criteria
     * </p>
     * 
     * @param request
     * @param response
     * @param payload
     */
    public void saveReportCriteria(HttpServletRequest request,
            HttpServletResponse response, String payload) {

        log.info("Request came to save report criteria");
        JSONObject resObj = new JSONObject();
        try {
            JSONObject reqObj = new JSONObject(payload);

            String reportName = reqObj.getString("report_name");
            String rptDesc = reqObj.getString("report_description");
            String reportData = reqObj.getString("save_report_data");
            String sharedMode = reqObj.getString("shared_mode");
            List<String> users = new ArrayList<String>();
            List<Long> groupIds = new ArrayList<Long>();
            String pqlData = "";

            if (sharedMode.toLowerCase().equals("users")) {
                JSONArray userIdArr = reqObj.getJSONArray("user_ids");
                if (userIdArr != null && userIdArr.length() > 0) {
                    for (int i = 0; i < userIdArr.length(); i++) {
                        String username = userIdArr.getString(i);
                        users.add(username);
                    }
                }

                JSONArray groupIdArr = reqObj.getJSONArray("group_ids");
                if (groupIdArr != null && groupIdArr.length() > 0) {
                    for (int i = 0; i < groupIdArr.length(); i++) {
                        Long groupId = groupIdArr.getLong(i);
                        groupIds.add(groupId);
                    }
                }
            }

            boolean isSaveOnlyMode = reqObj.getBoolean("save_only_mode");
            Long userId = AppContext.getContext(request).getRemoteUser()
                    .getPrincipalId();

            if (isSaveOnlyMode) {
                SavedReportDO savedReportDO = getSavedReportService()
                        .lookupByReportName(reportName).get(0);
                savedReportDO.setDescription(rptDesc);
                savedReportDO.setSharedMode(sharedMode);
                savedReportDO.setCriteriaJSON(reportData);
                savedReportDO.setOwnerId(userId);
                savedReportDO.setLastUpdatedDate(new Timestamp(Calendar
                        .getInstance().getTimeInMillis()));

                pqlData = getReporterAccessControlService(request)
                        .generateReportDataAccessPQL(savedReportDO, users,
                                groupIds);
                savedReportDO.setPqlData(pqlData);

                getSavedReportService().update(savedReportDO);
                log.info("Saved report updated successfully");

                if(users.size() > 0 || groupIds.size() > 0) {
                    log("Saved report modified,  [name :" + reportName 
                            + ", sharedMode :" + sharedMode 
                            + ", user :" + getUserNames(users) 
                            + ", group :" + getGroupNames(groupIds) 
                            + "] by " + getCurrentUserName(request));
                } else {
                	log("Saved report modified,  [name :" + reportName 
                            + ", sharedMode :" + sharedMode 
                            + "] by " + getCurrentUserName(request));
                }
            } else {
                SavedReportDO savedReportDO = new SavedReportDO();
                savedReportDO.setTitle(reportName);
                savedReportDO.setDescription(rptDesc);
                savedReportDO.setCriteriaJSON(reportData);
                savedReportDO.setSharedMode(sharedMode);
                savedReportDO.setOwnerId(userId);
                savedReportDO.setCreatedDate(new Timestamp(Calendar
                        .getInstance().getTimeInMillis()));
                savedReportDO.setLastUpdatedDate(new Timestamp(Calendar
                        .getInstance().getTimeInMillis()));
                savedReportDO.setInDashboard(false);

                pqlData = getReporterAccessControlService(request)
                        .generateReportDataAccessPQL(savedReportDO, users,
                                groupIds);
                savedReportDO.setPqlData(pqlData);

                getSavedReportService().create(savedReportDO);
                log.info("Saved report saved successfully");

                if(users.size() > 0 || groupIds.size() > 0) {
                    log("Saved report created,  [name :" + reportName
                            + ", sharedMode :" + sharedMode
                            + ", user :" + getUserNames(users) 
                            + ", group :" + getGroupNames(groupIds) 
                            + "] by " + getCurrentUserName(request));
                } else {
                    log("Saved report created,  [name :" + reportName
                            + ", sharedMode:" + sharedMode 
                            + "] by " + getCurrentUserName(request));
                }
            }

            List<SavedReportDO> saveReports = getSavedReportService()
                    .lookupByReportName(reportName);
            if (saveReports.size() > 0) {
                SavedReportDO saveRpt = saveReports.get(0);
                resObj.put("reportId", saveRpt.getId());
            }
            resObj.put("status", "SUCCESS");
            SharedUtils.writeJSONResponse(request, response, resObj);

        } catch (Exception e) {
            log.error("Error encountered in saving the report ,", e);
        }
        log.info("Request to save report criteria handled successfully");
    }

    /**
     * <p>
     * method to process the validation of the report name and return JSON
     * string as response.
     * </p>
     * 
     * @param request
     * @param response
     * @param payload
     */
    public void validateReportNameProcess(HttpServletRequest request,
            HttpServletResponse response, String payload) {

        log.info("Request came to validate report name");

        JSONObject resObj = new JSONObject();
        try {
            JSONObject reqObj = new JSONObject(payload);
            String reportName = reqObj.getString("report_name");
            List<SavedReportDO> savedReports = getSavedReportService()
                    .lookupByReportName(reportName);

            if (savedReports.isEmpty()) {
                resObj.put("status", "AVAILABLE");
            } else {
                resObj.put("status", "EXISTS");
            }

            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in validating the report name,", e);
        }
    }

    /**
     * <p>
     * Get the JSon array in string with given delimiter.
     * </p>
     * 
     * @param arr
     * @param delimiter
     * @return
     * @throws JSONException
     */
    public String getJsonArrayInString(JSONArray arr, String delimiter)
            throws JSONException {

        if (arr == null) {
            return "";
        }

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < arr.length(); i++) {
            if (i != 0) {
                str.append(delimiter);
            }
            str.append(arr.get(i));
        }
        return str.toString();
    }

    /**
     * <p>
     * All share point reports names load here.
     * </p>
     * 
     * @param request
     *            HTTP request
     * @param response
     *            HTTP response
     */
    public void loadAllSharePointReports(HttpServletRequest request,
            HttpServletResponse response) {
        log.info("Request came to load all share point reports");
        JSONObject resObj = new JSONObject();
        try {
            JSONArray resArray = new JSONArray();

            for (SharePointReportTypeEnum reportType : SharePointReportTypeEnum
                    .values()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("report_name", reportType.getName());
                resArray.put(jsonObject);
            }

            resObj.put("report_names", resArray);
            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in load all share point reports,", e);
        }
    }

    /**
     * <p>
     * Load all the application users
     * </p>
     * 
     * @param request
     *            HTTP request
     * @param response
     *            HTTP response
     */
    public void loadAllApplicationUsers(HttpServletRequest request,
            HttpServletResponse response) {
        log.info("Request came to load all application users");
        JSONObject resObj = new JSONObject();
        try {
            JSONArray resArray = new JSONArray();
            log.info("Request came to load all users :::::  Session :->"
                    + request.getSession());
            log.info("Request came to load all users :::::  Session Id :->"
                    + request.getSession().getId());
            UserDTOList userDTOs = getUserService(request).getAllUsers();

            if (userDTOs != null && userDTOs.getUsers() != null) {
                for (UserDTO user : userDTOs.getUsers()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("first_name", user.getFirstName());
                    jsonObject.put("last_name", user.getLastName());
                    jsonObject.put("username", user.getUniqueName());
                    resArray.put(jsonObject);
                }
            }

            resObj.put("app_users", resArray);
            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in load all application users,", e);
        }
    }

    /**
     * <p>
     * Load all the user groups
     * </p>
     * 
     * @param request
     *            HTTP request
     * @param response
     *            HTTP response
     */
    public void loadAllUSerGroups(HttpServletRequest request,
            HttpServletResponse response) {
        log.info("Request came to load all user groups");
        JSONObject resObj = new JSONObject();
        try {
            log.info("Request came to load all users Group :::::  Session :->"
                    + request.getSession());
            log.info("Request came to load all users Group :::::  Session Id :->"
                    + request.getSession().getId());
            JSONArray resArray = new JSONArray();
            UserGroupReducedList userGroups = getUserGroupServiceIF(request)
                    .getAllUserGroups();

            if (userGroups != null && userGroups.getUserGroupReduced() != null) {
                for (UserGroupReduced group : userGroups.getUserGroupReduced()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("grp_Id", group.getId());
                    jsonObject.put("grp_title", group.getTitle());
                    resArray.put(jsonObject);
                }
            }

            resObj.put("user_groups", resArray);
            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in load all user groups,", e);
        }

    }

    /**
     * <p>
     * create a parameters for selected share point report form in a JSON
     * response
     * </p>
     * 
     * @param request
     *            HTTP request
     * @param response
     *            HTTP response
     * @param payLoad
     *            data send with the request
     */
    public void loadSharePointReportForm(HttpServletRequest request,
            HttpServletResponse response, String payLoad) {

        log.info("Request came to load share point report");
        JSONObject resObj = new JSONObject();
        try {
            JSONObject reqObj = new JSONObject(payLoad);

            String reportName = reqObj.getString("report_name");
            SharePointReportTypeEnum reportType = SharePointReportTypeEnum
                    .getReportTypeByName(reportName);

            JSONArray fieldsArry = new JSONArray();

            for (DynamicField field : reportType.getFields()) {

                JSONArray jsonvalues = new JSONArray();
                for (String value : field.getValues()) {
                    jsonvalues.put(value);
                }

                JSONObject dynaField = new JSONObject();
                dynaField.put("name", field.getName());
                dynaField.put("display_name", field.getDisplayName());
                dynaField.put("type", field.getType().name());
                dynaField.put("value", jsonvalues);

                fieldsArry.put(dynaField);
            }

            resObj.put("report_name", reportType.getName());
            resObj.put("report_type", reportType);
            resObj.put("report_title", reportType.getTitle());
            resObj.put("report_desc", reportType.getDesc());
            resObj.put("fields", fieldsArry);

            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in load share point report,", e);
        }
    }

    /**
     * <p>
     * create a parameters for selected share point bar report in a JSON
     * </p>
     * 
     * @param request
     *            HTTP request
     * @param response
     *            HTTP response
     * @param payload
     *            data send with the request
     * @throws Exception
     */
    public void loadSharePointBarChart(HttpServletRequest request,
            HttpServletResponse response, String payload) throws Exception {
        log.info("Request came to load share point bar report");
        JSONObject resObj = new JSONObject();
        ISharePointDataGenerator dataGen = getDataGenerator();
        try {
            SharePointCriteriaJSONModel jsonModel = new SharePointCriteriaJSONModel(
                    payload);
            SharePointReportTypeEnum reportType = SharePointReportTypeEnum
                    .getReportTypeByName(jsonModel.getReportName());

            List<GroupByData> groupByData = dataGen
                    .generateSQLAndGetResults(jsonModel);

            log.info("Share point report grouped data size :"
                    + groupByData.size());

            JSONArray chartDataJSON = createBarChartDataResp(reportType,
                    groupByData);

            resObj.put("chartData", chartDataJSON);
            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in load all share point reports,", e);
        } finally {
            dataGen.cleanUp();
        }
    }

    /**
     * <p>
     * Load data for Share Point PIE chart.
     * </p>
     * 
     * @param request
     *            HTTP request
     * @param response
     *            HTTP response
     * @param payload
     *            data send with the request
     * @throws Exception
     */
    public void loadSharePointPIEChart(HttpServletRequest request,
            HttpServletResponse response, String payload) throws Exception {
        log.info("Request came to load share point PIE report");
        JSONObject resObj = new JSONObject();
        ISharePointDataGenerator dataGen = getDataGenerator();
        try {
            SharePointCriteriaJSONModel jsonModel = new SharePointCriteriaJSONModel(
                    payload);

            List<GroupByData> groupByData = dataGen
                    .generateSQLAndGetResults(jsonModel);

            log.info("Share point report grouped data size :"
                    + groupByData.size());

            JSONArray chartDataJSON = createPIEChartData(groupByData);

            resObj.put("chartData", chartDataJSON);
            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in load all share point reports,", e);
        } finally {
            dataGen.cleanUp();
        }
    }

    public void loadActionData(HttpServletRequest request,
            HttpServletResponse response) {
        log.info("Request came to load actions");
        JSONObject resObj = new JSONObject();
        try {

            JSONArray actionsArr = new JSONArray();
            Map<String, String> actionMap = ReportActions.getActionsMap();

            for (Map.Entry<String, String> entry : actionMap.entrySet()) {

                JSONObject jsonObj = new JSONObject();
                jsonObj.accumulate("name", entry.getKey());
                jsonObj.accumulate("label", entry.getValue());
                actionsArr.put(jsonObj);
            }

            resObj.put("actions", actionsArr);

            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception ex) {
            log.error(
                    "Error occurred in loading external resoource Application manager,",
                    ex);
        }
    }
    
    public void loadActionGroupByPolicyModel(HttpServletRequest request,
            HttpServletResponse response) {
        log.info("Request came to load actions group by policy model data");
        JSONObject resObj = new JSONObject();
        try {
        	Map<String, JSONArray> actionMap = ReportActions.getActionsGroupByPolicyModelData();
            
            resObj.put("actionMap", actionMap);

            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception ex) {
            log.error(
                    "Error occurred while loading the actions by policy models,",
                    ex);
        }
    }

    public void checkUserAccess(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Request came to check User Access");
        JSONObject resObj = new JSONObject();
        try {
            ILoggedInUser currentUser = AppContext.getContext(request).getRemoteUser();

            boolean hasAdminAccess = false;
            String usrname = currentUser.getUsername().trim();
            if (usrname.equalsIgnoreCase(SUPER_USER_USERNAME)) {
                hasAdminAccess = true;
            } else {
                DevelopmentEntity sysAdmin = getReporterAccessControlService(request)
                        .getDevelopmetEntity(EntityType.COMPONENT, ReportNavigatorBeanImpl.SYSTEM_ADMIN);
                if (sysAdmin != null) {
                    hasAdminAccess = getReporterAccessControlService(request).checkAccessForDevEntity(currentUser,
                            sysAdmin);
                    if (!hasAdminAccess) {
                        DevelopmentEntity rptAdmin = getReporterAccessControlService(request)
                                .getDevelopmetEntity(EntityType.COMPONENT, ReportNavigatorBeanImpl.REPORTER_ADMIN);
                        hasAdminAccess = getReporterAccessControlService(request).checkAccessForDevEntity(currentUser,
                                rptAdmin);
                    }
                }

                if (!hasAdminAccess) {
                    // Admin should have EDIT auth in both modules
                    Object authAttributes = AppContext.getContext()
                            .getAttribute(ApplicationUserDetailsFilter.APP_AUTHS_ATTR);
                    Set<String> auths = (Set<String>) authAttributes;
                    if (auths.contains(MANAGE_REPORTER_ACTION)) {
                        hasAdminAccess = true;
                    }
                }
            }
            resObj.append("is_reproter_admin", hasAdminAccess);
            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception e) {
            log.error("Error encountered in check user access,", e);
        }
    }

    public void loadAllCustomAppNames(HttpServletRequest request,
            HttpServletResponse response, String payload) throws Exception {
        log.info("Request came to load custome app report names");

        HttpSession session = (HttpSession) request.getSession(false);

        try {
            Collection<CustomAppJO> customReportApps = extRepAppMgr.getAll(
                    session.getId(), session.getMaxInactiveInterval());

            JSONArray jsonCusAppNames = new JSONArray();
            Integer key = 1000;
            for (CustomAppJO customApp : customReportApps) {

                List<CustomReportJO> reports = customApp
                        .getPolicyApplicationJO().getCustomReports();
                for (CustomReportJO rpt : reports) {
                    JSONObject cusRptJson = new JSONObject();
                    cusRptJson.accumulate("id", key);
                    cusRptJson.accumulate("title", rpt.getTitle());
                    jsonCusAppNames.put(cusRptJson);

                    RCIReportDataModel rciReport = new RCIReportDataModel();
                    rciReport.setKey(key);
                    rciReport.setTitle(rpt.getTitle());
                    rciReport.setDescription(rpt.getDescription());
                    rciReport.setCustomAppJO(customApp);

                    rciReportMap.put(key, rciReport);
                    key++;
                }
            }
            SharedUtils.writeJSONResponse(request, response, jsonCusAppNames);
        } catch (Exception ex) {
            log.error(
                    "Error occurred in loading external resoource Application manager,",
                    ex);
        }
    }

    public void loadCustomAppReportForm(HttpServletRequest request,
            HttpServletResponse response, String payload) throws Exception {
        log.info("Request came to load custome app report form ");
        // JSONObject resObj = new JSONObject();
        try {

        } catch (Exception e) {
            log.error("Error occurred in loading customer app report form,", e);
        }
    }

    private JSONArray createBarChartDataResp(
            SharePointReportTypeEnum reportType, List<GroupByData> groupByData)
            throws Exception {
        return createCategorizedSeriesDataResponse(groupByData);
    }

    /**
     * <p>
     * Create Categorized series data response for bar charts.
     * 
     * eg: { label : xxx, values : [{ x: "", y: ""}, { x: "", y: ""} ] }
     * </p>
     * 
     * @param groupByData
     * @return JSONArray
     * @throws Exception
     */
    private JSONArray createCategorizedSeriesDataResponse(
            List<GroupByData> groupByData) throws Exception {
        JSONArray charDataObj = new JSONArray();

        Map<String, JSONArray> dataByCategoryMap = new HashMap<String, JSONArray>();

        for (GroupByData grpData : groupByData) {
            String category = (grpData.getCategory() != null) ? grpData
                    .getCategory() : "-";

            JSONArray dataArry = dataByCategoryMap.get(category);
            if (dataArry == null) {
                dataArry = new JSONArray();
                dataByCategoryMap.put(category, dataArry);
            }

            JSONObject value = new JSONObject();
            value.accumulate("label", grpData.getDimension());
            value.accumulate("value", grpData.getResultCount());
            dataArry.put(value);
        }

        log.info("Data By Category Map: " + dataByCategoryMap);

        for (Map.Entry<String, JSONArray> entry : dataByCategoryMap.entrySet()) {

            JSONObject value = new JSONObject();
            value.put("key", entry.getKey());
            value.put("values", entry.getValue());

            charDataObj.put(value);
        }
        return charDataObj;
    }

    /**
     * <p>
     * Create data series response for PIE charts. eg: [{ label : xxx, value :
     * yyy} , { label : xxx, value : yyy}];
     * </p>
     * 
     * @param groupByData
     * @return
     * @throws Exception
     */
    private JSONArray createPIEChartData(List<GroupByData> groupByData)
            throws Exception {

        JSONArray charDataObj = new JSONArray();
        for (GroupByData grpData : groupByData) {
            JSONObject value = new JSONObject();
            value.put("label", grpData.getDimension());
            value.put("value", grpData.getResultCount());
            charDataObj.put(value);
        }
        return charDataObj;
    }

    public void validateUserSession(HttpServletRequest request,
            HttpServletResponse response, String payload) throws Exception {
        try {
            JSONObject resObj = new JSONObject();
            resObj.accumulate("valid_session", true);
            SharedUtils.writeJSONResponse(request, response, resObj);
        } catch (Exception ex) {
            log.error("Error occurred in validateUserSession,", ex);
        }
    }

    private ISharePointDataGenerator getDataGenerator() throws Exception {
        return DataGeneratorFactory.getInstance().getSharePointDataGenerator();
    }

    public SavedReportService getSavedReportService() {
        if (savedReportService == null) {
            savedReportService = new SavedReportServiceImpl();
        }
        return savedReportService;
    }

    public ResourceLookupService getResourceLookupService() {
        if (resourceLookupService == null) {
            resourceLookupService = new ResourceLookupService();
        }
        return resourceLookupService;
    }

    public AppUserMgmtService getApplicationUserService() {
        if(appUserMgmtService == null) {
            appUserMgmtService = new AppUserMgmtService();
        }

        return appUserMgmtService;
	}

    private static final String USER_SERVICE_LOCATION_SERVLET_PATH = "/services/UserRoleService";

    /**
     * Retrieve the User and User Service port
     * 
     * @return the User and User Service port
     * @throws AxisFault
     *             if the service port retrieval fails
     */
    private UserRoleServiceStub getUserService(HttpServletRequest request)
            throws AxisFault {
        if (this.userService == null) {
            String location = request.getSession().getServletContext()
                    .getInitParameter("DMSLocation")
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
    protected UserGroupServiceStub getUserGroupServiceIF(
            HttpServletRequest request) throws AxisFault {
        if (this.userGroupService == null) {
            String location = request.getSession().getServletContext()
                    .getInitParameter("DMSLocation")
                    + USER_GROUP_SERVICE_LOCATION_SERVLET_PATH;
            this.userGroupService = new UserGroupServiceStub(location);
        }

        return this.userGroupService;
    }

    public ReportDataManager getReportDataManager() {
        if (reportDataManager == null) {
            reportDataManager = new ReportDataManager();
        }
        return reportDataManager;
    }

    public ReporterAccessControlService getReporterAccessControlService(
            HttpServletRequest request) {
        if (reporterAccessControlService == null) {
            reporterAccessControlService = new ReporterAccessControlServiceImpl();
        }

        return reporterAccessControlService;
    }

    private String getUserNames(List<String> users) {
        StringBuilder names = new StringBuilder();

        for(String user : users) {
            log.debug("Getting user name [" + user + "].");
            try {
            	String parsedName = user.indexOf("@") > -1 ? user.substring(0, user.lastIndexOf("@")) : user;
                UserDTO userDTO = getApplicationUserService().findByUsername(parsedName);

                if(userDTO.getFirstName() != null && userDTO.getLastName() != null) {
                    names.append(names.length() > 0 ? ", " + userDTO.getFirstName() + " " + userDTO.getLastName() : userDTO.getFirstName() + " " + userDTO.getLastName());
                } else {
                    names.append(names.length() > 0 ? ", " + user : user);
                }
            } catch(SQLException err) {
                names.append(names.length() > 0 ? ", " + user : user);
                log.error(err.getMessage(), err);
            } catch(HibernateException err) {
                names.append(names.length() > 0 ? ", " + user : user);
                log.error(err.getMessage(), err);
            }
        }

        if(names.length() == 0) {
            names.append("-NONE-");
        }

        return names.toString();
    }

    private String getGroupNames(List<Long> groupIds) {
        StringBuilder names = new StringBuilder();

        for(Long groupId : groupIds) {
            log.debug("Getting group name [" + groupId + "].");
            try {
                UserGroupReduced userGroup = getApplicationUserService().findByUserGroupId(groupId);

                if(userGroup != null && userGroup.getTitle() != null) {
                    names.append(names.length() > 0 ? ", " + userGroup.getTitle() : userGroup.getTitle());
                } else {
                    names.append(names.length() > 0 ? ", id= " + groupId : "id= " + groupId);
                }
            } catch(SQLException err) {
                names.append(names.length() > 0 ? ", id= " + groupId : "id= " + groupId);
                log.error(err.getMessage(), err);
            } catch(HibernateException err) {
                names.append(names.length() > 0 ? ", id= " + groupId : "id= " + groupId);
                log.error(err.getMessage(), err);
            }
        }

        if(names.length() == 0) {
            names.append("-NONE-");
        }
        
        return names.toString();
	}

    private String getCurrentUserName(HttpServletRequest request) {
	    try {
	        AppContext ctx = AppContext.getContext(request);
	        ILoggedInUser remoteUser = ctx.getRemoteUser();
	        return remoteUser.getUsername();
	    } catch(Exception e) {
	        log.error("Error encountered in retrieving current user information, ", e);
	    }

	    return "UNKNOWN";
    }
}
