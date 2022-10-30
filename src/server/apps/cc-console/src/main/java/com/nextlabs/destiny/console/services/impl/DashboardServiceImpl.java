/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 30, 2016
 *
 */
package com.nextlabs.destiny.console.services.impl;

import static com.nextlabs.destiny.console.enums.PolicyStatus.APPROVED;
import static com.nextlabs.destiny.console.enums.PolicyStatus.DRAFT;
import static com.nextlabs.destiny.console.enums.PolicyStatus.OBSOLETE;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.DashboardDataDao;
import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.enums.DateTimeUnit;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;
import com.nextlabs.destiny.console.model.AuditLog;
import com.nextlabs.destiny.console.model.SysInfo;
import com.nextlabs.destiny.console.services.AuditLogService;
import com.nextlabs.destiny.console.services.DashboardService;
import com.nextlabs.destiny.console.services.policy.PolicySearchService;
import com.nextlabs.destiny.console.utils.LicenseCheckerUtil;
import com.nextlabs.destiny.console.utils.SearchCriteriaBuilder;
import com.nextlabs.destiny.console.utils.ServerLicenseUtil;

/**
 * Dashboard report service implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class DashboardServiceImpl implements DashboardService {

	private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

	@Autowired
	private DashboardDataDao dashboardDataDao;

	@Autowired
	private PolicySearchService policySearchService;

	@Autowired
	private ConfigurationDataLoader configDataLoader;

	@Autowired
	private AuditLogService auditLogService;
	
    private LicenseCheckerUtil licenseCheckerUtil; 

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getSystemDetails() throws ConsoleException {

		String licenseFolderLocation = configDataLoader.getServerLicensePath();
		Properties licenseProperties = ServerLicenseUtil.readLicense(licenseFolderLocation);

		String osName = System.getProperty("os.name");
		String osVersion = System.getProperty("os.version");
		int cores = Runtime.getRuntime().availableProcessors();

		String appVersion = configDataLoader.getApplicationVersion();
		String build = configDataLoader.getApplicationBuild();
		if (StringUtils.isNotEmpty(build)) {
			appVersion = String.format("%s-%s", appVersion, build);
		}

		long folderSize = getFileFolderSize(new File(configDataLoader.getLogQueueFolderPath()));
		String logQueueSize = FileUtils.byteCountToDisplaySize(folderSize);

		JSONObject systemDetails = new JSONObject();
		systemDetails.put("os_name", osName);
		systemDetails.put("os_version", osVersion);
		systemDetails.put("app_version", appVersion);
		systemDetails.put("log_queue_dir_size", logQueueSize);
		systemDetails.put("no_of_cores", cores);
		SysInfo sysInfo = licenseCheckerUtil.getSysInfo();
		if(null != sysInfo && null != sysInfo.getLicenseInfo()) {
			systemDetails.put("pdp_sys_info", sysInfo.getPdpInfo());
			JSONObject licenseProps = new JSONObject();
			String licenseExpiryDate = sysInfo.getLicenseInfo().getExpiryDate();
			if (licenseExpiryDate.equals("-1")) {
				licenseProps.put("no_of_days_left", -1);
			} else {
				// 12/01/2016 convert to timestamp
				DateTimeFormatter formatter = DateTimeFormat
						.forPattern(configDataLoader.getMmDDyyyyFormat());
				DateTime dt = formatter.parseDateTime(licenseExpiryDate);
				DateTime current = DateTime.now();
				int noOfDaysLeft = Days
						.daysBetween(current.withTimeAtStartOfDay(), dt.withTimeAtStartOfDay()).getDays();
				licenseProps.put("expiration_milis", dt.getMillis());
				licenseProps.put("no_of_days_left", noOfDaysLeft);
			}
			licenseProps.put("subcriptionMode",  sysInfo.getLicenseInfo().getSubcriptionMode());
			systemDetails.put("license_properties", licenseProps);
		} else {
			JSONObject licenseProps = new JSONObject();

			if (licenseProperties != null) {
				Enumeration<Object> keys = licenseProperties.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					if ("expiration".equalsIgnoreCase(key)) {

						try {
							String expirationDate = licenseProperties.getProperty(key);
							if (expirationDate.equals("-1")) {
								licenseProps.put("no_of_days_left", -1);
							} else {
								// 12/01/2016 convert to timestamp
								DateTimeFormatter formatter = DateTimeFormat
										.forPattern(configDataLoader.getMmDDyyyyFormat());
								DateTime dt = formatter.parseDateTime(expirationDate);
								DateTime current = DateTime.now();
								int noOfDaysLeft = Days
										.daysBetween(current.withTimeAtStartOfDay(), dt.withTimeAtStartOfDay()).getDays();
								licenseProps.put("expiration_milis", dt.getMillis());
								licenseProps.put("no_of_days_left", noOfDaysLeft);
							}
						} catch (Exception e) {
							log.error("Error in reading license expiration data,", e);
							licenseProps.put("no_of_days_left", -1);
						}
					}
					licenseProps.put(key, licenseProperties.get(key));
				}
			}
			systemDetails.put("license_properties", licenseProps);
		}

		return systemDetails;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public JSONObject getEnrollmentDetails() throws ConsoleException {

		JSONArray summaryArray = dashboardDataDao.getEnrolledElementSummary();
		JSONArray statusInfoArray = dashboardDataDao.getEnrollmentStatusInfo();

		JSONObject entrollmentDetails = new JSONObject();
		entrollmentDetails.put("element_summary", summaryArray);
		entrollmentDetails.put("enrollments", statusInfoArray);

		log.debug("Enrollment details loaded");

		return entrollmentDetails;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public JSONObject getSystemConfigDetails() throws ConsoleException {
		JSONObject iceNetSummary = getICEnetConfigSummary();
		JSONObject pdpConfigSummary = getPDPConfigSummary();

		JSONObject systemConfigDetails = new JSONObject();
		systemConfigDetails.put("ice_net_summary", iceNetSummary);
		systemConfigDetails.put("pdp_config_summary", pdpConfigSummary);
		return systemConfigDetails;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getPDPConfigSummary() {
		JSONArray pdpConfigs = dashboardDataDao.getPDPConfiguraions();

		int activePDPCount = 0;
		int failedPDPCount = 0;
		int bundleUptoDateCount = 0;
		int bundleUpdateFailedCount = 0;

		for (int i = 0; i < pdpConfigs.size(); i++) {
			JSONObject pdpConfig = (JSONObject) pdpConfigs.get(i);
			if ((boolean) pdpConfig.get("is_active")) {
				activePDPCount++;
			} else {
				failedPDPCount++;
			}

			if ((boolean) pdpConfig.get("is_bundle_upto_date")) {
				bundleUptoDateCount++;
			} else {
				bundleUpdateFailedCount++;
			}
		}

		JSONObject pdpConfigSummary = new JSONObject();
		pdpConfigSummary.put("active_pdps", activePDPCount);
		pdpConfigSummary.put("failed_pdps", failedPDPCount);
		pdpConfigSummary.put("bundle_update_success", bundleUptoDateCount);
		pdpConfigSummary.put("bundle_update_failed", bundleUpdateFailedCount);
		pdpConfigSummary.put("total_pdps", pdpConfigs.size());
		pdpConfigSummary.put("details", pdpConfigs);
		return pdpConfigSummary;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getICEnetConfigSummary() {
		JSONArray iceNetConfigs = dashboardDataDao.getICENetConfiguraions();

		int activeIceNetCount = 0;
		int failedIceNetCount = 0;
		for (int i = 0; i < iceNetConfigs.size(); i++) {
			JSONObject iceNetConfig = (JSONObject) iceNetConfigs.get(i);
			if ((boolean) iceNetConfig.get("is_active")) {
				activeIceNetCount++;
			} else {
				failedIceNetCount++;
			}
		}

		JSONObject iceNetSummary = new JSONObject();
		iceNetSummary.put("active_icenets", activeIceNetCount);
		iceNetSummary.put("failed_icenets", failedIceNetCount);
		iceNetSummary.put("total_icenets", iceNetConfigs.size());
		iceNetSummary.put("details", iceNetConfigs);
		return iceNetSummary;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public JSONObject getPDPThroughputDetails(Long fromDate, Long toDate, String groupBy) throws ConsoleException {
		DateTimeUnit dateTimeUnit = DateTimeUnit.get(groupBy);
		Map<Long, JSONObject> pdpThroughputDetails = dashboardDataDao.getPDPThroughputDetails(fromDate, toDate,
				dateTimeUnit);

		long noOfUnits = getNoOfDTUnitsInBetween(dateTimeUnit, fromDate, toDate);

		for (int i = 0; i < noOfUnits; i++) {
			long millis = 0l;
			millis = getTimeByPlusUnits(dateTimeUnit, fromDate, toDate, i);

			JSONObject value = pdpThroughputDetails.get(millis);
			if (value == null) {
				JSONObject data = new JSONObject();
				data.put("time_unit", millis);
				data.put("count", 0);
				pdpThroughputDetails.put(millis, data);
			}
		}

		JSONObject pdpThroughputSummary = new JSONObject();
		pdpThroughputSummary.put("results", pdpThroughputDetails);
		pdpThroughputSummary.put("size", pdpThroughputDetails.size());
		return pdpThroughputSummary;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public JSONObject getAlertSummary(Long fromDate, Long toDate, String groupBy) throws ConsoleException {
		DateTimeUnit dateTimeUnit = DateTimeUnit.get(groupBy);
		Map<Long, JSONArray> alertSummaryMap = dashboardDataDao.generatedAlertSummary(fromDate, toDate, dateTimeUnit);

		long noOfUnits = getNoOfDTUnitsInBetween(dateTimeUnit, fromDate, toDate);

		for (int i = 0; i < noOfUnits; i++) {
			long millis = 0l;
			millis = getTimeByPlusUnits(dateTimeUnit, fromDate, toDate, i);

			JSONArray value = alertSummaryMap.get(millis);
			if (value == null) {
				alertSummaryMap.put(millis, new JSONArray());
			}
		}

		JSONObject alertSummary = new JSONObject();
		alertSummary.put("results", alertSummaryMap);
		alertSummary.put("size", alertSummaryMap.size());
		return alertSummary;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public JSONObject getActivitySummaryByUser(Long fromDate, Long toDate, String decision, int pageSize)
			throws ConsoleException {
		JSONArray activitySummary = dashboardDataDao.getActivitySummaryByUser(fromDate, toDate, decision, pageSize);

		JSONObject userActivitySummary = new JSONObject();
		userActivitySummary.put("results", activitySummary);
		userActivitySummary.put("size", activitySummary.size());

		log.debug("User activity summary details loaded");
		return userActivitySummary;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public JSONObject getActivitySummaryByResource(Long fromDate, Long toDate, int pageSize) throws ConsoleException {
		JSONArray activitySummary = dashboardDataDao.getActivitySummaryByResource(fromDate, toDate, pageSize);

		JSONObject resourceActSummary = new JSONObject();
		resourceActSummary.put("results", activitySummary);
		resourceActSummary.put("size", activitySummary.size());

		log.debug("Resource activity summary details loaded");
		return resourceActSummary;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public JSONObject getActivitySummaryByPolicy(Long fromDate, Long toDate, int pageSize) throws ConsoleException {
		JSONArray activitySummary = dashboardDataDao.getActivitySummaryByPolicy(fromDate, toDate, pageSize);

		JSONObject policyActSummary = new JSONObject();
		policyActSummary.put("results", activitySummary);
		policyActSummary.put("size", activitySummary.size());

		log.debug("Policy activity summary details loaded");
		return policyActSummary;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public JSONObject getNotMatchingPolicies(Long fromDate, Long toDate, int pageSize) throws ConsoleException {
		JSONArray nonMatchingPolicies = dashboardDataDao.getNotMatchingPolicies(fromDate, toDate, pageSize);

		JSONObject notMatchingPolicySummary = new JSONObject();
		notMatchingPolicySummary.put("results", nonMatchingPolicies);
		notMatchingPolicySummary.put("size", nonMatchingPolicies.size());

		log.debug("Non matching policy details loaded");
		return notMatchingPolicySummary;
	}

	private long getNoOfDTUnitsInBetween(DateTimeUnit dateTimeUnit, long fromDate, long toDate) {
		long noOfUnits = 0;
		DateTime startDateTime = new DateTime(new Date(fromDate));
		DateTime endDateTime = new DateTime(new Date(toDate));
		Duration duration = new Duration(fromDate, toDate);

		switch (dateTimeUnit) {
		case SECONDS:
			noOfUnits = duration.getStandardSeconds() + 1;
			break;
		case MINUTES:
			noOfUnits = duration.getStandardMinutes() + 1;
			break;
		case HOURS:
			noOfUnits = duration.getStandardHours() + 1;
			break;
		case DAYS:
			noOfUnits = duration.getStandardDays() + 1;
			break;
		case WEEKS:
			noOfUnits = Weeks.weeksBetween(startDateTime, endDateTime).getWeeks() + 1L;
			break;
		case MONTHS:
			noOfUnits = Months.monthsBetween(startDateTime, endDateTime).getMonths() + 1L;
			break;
		case YEARS:
			noOfUnits = Years.yearsBetween(startDateTime, endDateTime).getYears() + 1L;
			break;
		default:
			break;
		}
		return noOfUnits;
	}

	private long getTimeByPlusUnits(DateTimeUnit dateTimeUnit, long fromDate, long toDate, int plusCount) {

		fromDate = (fromDate / 1000) * 1000;
		LocalDate startDate = LocalDate.fromDateFields(new Date(fromDate));
		DateTime startDateTime = new DateTime(new Date(fromDate));
		long millis = 0l;
		switch (dateTimeUnit) {
		case SECONDS:
			startDateTime.withMillisOfSecond(0);
			millis = startDateTime.plusSeconds(plusCount).toDateTime().getMillis();
			break;
		case MINUTES:
			startDateTime.withMillisOfSecond(0);
			startDateTime.withSecondOfMinute(0);
			millis = startDateTime.plusMinutes(plusCount).toDateTime().getMillis();
			break;
		case HOURS:
			startDateTime.withMillisOfSecond(0);
			startDateTime.withSecondOfMinute(0);
			startDateTime.withMinuteOfHour(0);
			millis = startDateTime.plusHours(plusCount).toDateTime().getMillis();
			break;
		case DAYS:
			millis = startDate.plusDays(plusCount).toDate().getTime();
			break;
		case WEEKS:
			millis = startDate.plusWeeks(plusCount).toDate().getTime();
			break;
		case MONTHS:
			millis = startDate.plusMonths(plusCount).toDate().getTime();
			break;
		case YEARS:
			millis = startDate.plusYears(plusCount).toDate().getTime();
			break;
		default:
			break;
		}
		return millis;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public JSONObject policySummaryByStatus() throws ConsoleException {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setPageNo(0);
		criteria.setPageSize(65000);

		Page<PolicyLite> policyLitePage = policySearchService.findPolicyByCriteria(criteria);

		int draftCount = 0;
		int draftNewCount = 0;
		int draftEditCount = 0;
		int deployedCount = 0;
		int unDeployedCount = 0;

		for (PolicyLite policyLite : policyLitePage.getContent()) {
			if (policyLite.getStatus().equals(DRAFT.name()) && policyLite.getActionType().equals("N/A")) {
				draftCount++;
				draftNewCount++;
			} else if (policyLite.getStatus().equals(DRAFT.name()) && policyLite.getActionType().equals("DE")) {
				draftCount++;
				draftEditCount++;
			} else if (policyLite.getStatus().equals(APPROVED.name()) && policyLite.getActionType().equals("DE")) {
				deployedCount++;
			} else if (policyLite.getStatus().equals(OBSOLETE.name()) && policyLite.getActionType().equals("N/A")) {
				unDeployedCount++;
			}
		}

		JSONArray array = new JSONArray();
		if (draftCount == 0 && draftNewCount == 0 && draftEditCount == 0 && deployedCount == 0
				&& unDeployedCount == 0) {
			log.info("Policy summary by status is empty");
		} else {
			array.add(getStatusRecord("DRAFT", draftCount));
			array.add(getStatusRecord("DRAFT_NEW", draftNewCount));
			array.add(getStatusRecord("DRAFT_EDIT", draftEditCount));
			array.add(getStatusRecord("DEPLOYED", deployedCount));
			array.add(getStatusRecord("UNDEPLOYED", unDeployedCount));
		}

		JSONObject policySummaryStatus = new JSONObject();
		policySummaryStatus.put("results", array);
		policySummaryStatus.put("size", array.size());

		log.info("Policy summary by status, [ Results :{} ]", array.size());

		return policySummaryStatus;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public JSONObject policySummaryByStatus(long fromDate, long toDate) throws ConsoleException {

		SearchCriteriaBuilder criteriaBuilder = SearchCriteriaBuilder.create();
		criteriaBuilder.addDateField("lastUpdatedDate", fromDate, toDate);
		SearchCriteria criteria = criteriaBuilder.getCriteria();
		criteria.setPageNo(0);
		criteria.setPageSize(65000);

		Page<PolicyLite> policyLitePage = policySearchService.findPolicyByCriteria(criteria);

		int draftCount = 0;
		int draftNewCount = 0;
		int draftEditCount = 0;
		int deployedCount = 0;
		int unDeployedCount = 0;

		for (PolicyLite policyLite : policyLitePage.getContent()) {
			if (policyLite.getStatus().equals(DRAFT.name()) && policyLite.getActionType().equals("N/A")) {
				draftCount++;
				draftNewCount++;
			} else if (policyLite.getStatus().equals(DRAFT.name()) && policyLite.getActionType().equals("DE")) {
				draftCount++;
				draftEditCount++;
			} else if (policyLite.getStatus().equals(APPROVED.name()) && policyLite.getActionType().equals("DE")) {
				deployedCount++;
			} else if (policyLite.getStatus().equals(OBSOLETE.name()) && policyLite.getActionType().equals("N/A")) {
				unDeployedCount++;
			}
		}

		JSONArray array = new JSONArray();
		if (draftCount == 0 && draftNewCount == 0 && draftEditCount == 0 && deployedCount == 0
				&& unDeployedCount == 0) {
			log.info("Policy summary by status is empty");
		} else {
			array.add(getStatusRecord("DRAFT", draftCount));
			array.add(getStatusRecord("DRAFT_NEW", draftNewCount));
			array.add(getStatusRecord("DRAFT_EDIT", draftEditCount));
			array.add(getStatusRecord("DEPLOYED", deployedCount));
			array.add(getStatusRecord("UNDEPLOYED", unDeployedCount));
		}

		JSONObject policySummaryStatus = new JSONObject();
		policySummaryStatus.put("results", array);
		policySummaryStatus.put("size", array.size());

		log.info("Policy summary by status for given period, [ Results :{} ]", array.size());

		return policySummaryStatus;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getStatusRecord(String status, int draftCount) {
		JSONObject statusRec = new JSONObject();
		statusRec.put("term", status);
		statusRec.put("count", draftCount);
		return statusRec;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject policySummaryByTags(int dataSize) throws ConsoleException {
		FacetResult facetResult = policySearchService.aggregatedPoliciesByTags(dataSize);

		JSONObject policyAggratedByTags = new JSONObject();
		policyAggratedByTags.put("results", facetResult.getTerms());
		policyAggratedByTags.put("size", facetResult.getTerms().size());

		log.info("Policy aggregated by tags, [ Results :{} ]", facetResult.getTerms().size());

		return policyAggratedByTags;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getActivityStream(int dataSize) throws ConsoleException {
		List<AuditLog> auditLogs = auditLogService.findByLastXRecords(dataSize);

		JSONArray dataArray = new JSONArray();
		for (AuditLog auditLog : auditLogs) {
			JSONObject log = new JSONObject();
			log.put("createdDate", auditLog.getCreatedDate());
			log.put("ownerId", auditLog.getOwnerId());
			log.put("ownerDisplayName", auditLog.getOwnerDisplayName());
			log.put("activityMsg", auditLog.getActivityMsg());
			log.put("type", auditLog.getComponent());
			dataArray.add(log);
		}

		JSONObject activities = new JSONObject();
		activities.put("results", dataArray);
		activities.put("size", dataArray.size());

		log.info("Activity stream, [ Results :{} ]", dataArray.size());

		return activities;
	}

	public static long getFileFolderSize(File dir) {
		long size = 0;
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isFile()) {
					size += file.length();
				} else
					size += getFileFolderSize(file);
			}
		} else if (dir.isFile()) {
			size += dir.length();
		}
		return size;
	}

	@PostConstruct
	public void initLicenceLoader() throws Exception {
		this.licenseCheckerUtil = new LicenseCheckerUtil(this.configDataLoader.getServerLicensePath());
	}
	
	
}
