/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 29, 2016
 *
 */
package com.nextlabs.destiny.console.dao.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.dao.DashboardDataDao;
import com.nextlabs.destiny.console.enums.DateTimeUnit;
import com.nextlabs.destiny.console.enums.EnrollmentType;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;

/**
 * DAO implementation for Dashboard data
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Repository
public class DashboardDataDaoImpl implements DashboardDataDao {

	private static final Logger log = LoggerFactory.getLogger(DashboardDataDaoImpl.class);

	private static final String SQL_ACTIVITY_SUMMARY_BY_RESOURCE =
			"select a1.from_resource_name, a1.allow_count, d1.deny_count, ( %s(a1.allow_count,0) + %s(d1.deny_count,0) ) total_count\n" +
					"from\n" +
					"    ( (\n" +
					"        select from_resource_name, 'A' decision, count(from_resource_name) allow_count\n" +
					"        from rpa_log\n" +
					"        where policy_decision = 'A' and day_nb >= :fromDate and day_nb <= :toDate\n" +
					"        group by from_resource_name\n" +
					"    ) a1\n" +
					"    inner join (\n" +
					"        select from_resource_name, 'D' decision, count(from_resource_name) deny_count\n" +
					"        from rpa_log\n" +
					"        where policy_decision = 'D' and day_nb >= :fromDate and day_nb <= :toDate\n" +
					"        group by from_resource_name\n" +
					"    ) d1 on ( a1.from_resource_name = d1.from_resource_name ) )\n" +
					"order by total_count desc";

	@Resource
	private ApplicationUserSearchRepository appUserSearchRepository;

	@Autowired
	private MessageBundleService msgBundle;

	@PersistenceContext(unitName = MGMT_UNIT)
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getEnrolledElementSummary() {
		final String queryStr = "SELECT k.name, COUNT(k.name) FROM "
				+ " (SELECT t.name, e.element_id  from DICT_LEAF_ELEMENTS e LEFT JOIN  DICT_ELEMENT_TYPES t ON (t.id = e.type_id))"
				+ " k GROUP BY k.name ";
		Query query = entityManager.createNativeQuery(queryStr);

		List<Object[]> results = query.getResultList();
		JSONArray array = new JSONArray();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("type", row[0]);
			rowData.put("count", row[1]);
			array.add(rowData);
		}
		log.info("Enrolled element summary, No of records {}", array.size());
		return array;
	}

	public static final long DATETIME_MAX_TICKS = 253402000000000L;

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getEnrollmentStatusInfo() {
		final String queryStr = "  SELECT e.id, e.domain_name, e.enrollment_type, e.isActive, u.start_time,"
				+ " u.end_time, u.is_successful, u.err_msg, e.isSyncing FROM DICT_ENROLLMENTS e " + " LEFT JOIN DICT_UPDATES u "
				+ " ON (e.id = u.enrollment_id AND u.active_to > :active_to)"
				+ " WHERE e.isactive = '1' ORDER BY u.end_time desc ";
		Query query = entityManager.createNativeQuery(queryStr);
		query.setParameter("active_to", DATETIME_MAX_TICKS);

		List<Object[]> results = query.getResultList();
		JSONArray array = new JSONArray();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("id", row[0]);
			rowData.put("domain_name", row[1]);
			EnrollmentType enrollType = getEnrollmentType(row[2]);
			rowData.put("enrollment_type", enrollType);
			boolean isActive = row[3] instanceof Boolean ? (Boolean) row[3] : readLong(row[3]) > 0;
			rowData.put("isActive", isActive);
			long startTime = readLong(row[4]);
			long endTime = readLong(row[5]);
			rowData.put("start_time", startTime);
			rowData.put("end_time", endTime);
			boolean isSyncing = row[8] instanceof Boolean ? (Boolean) row[8] : readLong(row[8]) > 0;

			String status = "";
			if (isSyncing) {
				status = msgBundle.getText("dashboard.widgets.enrollment.status.sync.in.progress");
			} else if (startTime <= 0 || endTime <= 0) {
				status = msgBundle.getText("dashboard.widgets.enrollment.status.sync.not.done");
			} else {
				status = ("Y".equalsIgnoreCase(readChar(row[6])))
						? msgBundle.getText("dashboard.widgets.enrollment.status.sync.success")
						: msgBundle.getText("dashboard.widgets.enrollment.status.sync.failed");
			}

			rowData.put("status", status);
			rowData.put("err_msg", row[7]);
			array.add(rowData);
		}
		log.info("Enrolled status info summary, No of records {}", array.size());
		return array;
	}

	private EnrollmentType getEnrollmentType(Object enrollTypeObj) {
		String name = (String) enrollTypeObj;
		return EnrollmentType.fromName(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getICENetConfiguraions() {
		final String queryStr = "  SELECT id, name, type, typeDisplayName, lastHeartbeat, heartbeatRate FROM COMPONENT"
				+ " where type = :type ";
		Query query = entityManager.createNativeQuery(queryStr);
		query.setParameter("type", "DABS");

		List<Object[]> results = query.getResultList();
		JSONArray array = new JSONArray();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("id", row[0]);
			rowData.put("name", row[1]);
			rowData.put("type", row[2]);
			rowData.put("display_name", row[3]);
			long lastHeartbeat = readLong(row[4]);
			long heartbeatRate = readLong(row[5]);
			rowData.put("last_heartbeat", lastHeartbeat);
			rowData.put("heartbeat_rate_secs", heartbeatRate);
			boolean isActive = ((lastHeartbeat + (heartbeatRate * 1000)) > System.currentTimeMillis());
			rowData.put("is_active", isActive);
			array.add(rowData);
		}
		log.info("ICEnet configuration and summary, No of records {}", array.size());
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getPDPConfiguraions() {
		long lastDeployementRecordTime = getLastDeploymentRecordsTime();

		final String queryStr = "SELECT a.id, a.host, a.type, a.registered, a.version, a.lastHeartbeat, "
				+ " a.deployment_bundle_ts, c.heart_beat_freq_time, c.heart_beat_freq_time_unit, c.dabs_location "
				+ " FROM AGENT a INNER JOIN COMM_PROFILE c ON (a.comm_profile_id = c.id) where a.registered = :registered";
		Query query = entityManager.createNativeQuery(queryStr);

		String dbType = checkDBServerType();
		if ("Postgre".equals(dbType)) {
			query.setParameter("registered", true);
		} else {
			query.setParameter("registered", 1);
		}

		List<Object[]> results = query.getResultList();
		JSONArray array = new JSONArray();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("id", row[0]);
			rowData.put("host", row[1]);
			rowData.put("type", row[2]);
			rowData.put("registered", row[3]);
			rowData.put("version", row[4]);
			long lastHeartbeat = readLong(row[5]);
			long bundleDeployedTs = readLong(row[6]);
			long heartbeatFrqTime = readLong(row[7]);
			String heartbeatFrqRate = (String) row[8];
			rowData.put("dabs_location", row[9]);

			heartbeatFrqTime = getHeartbeatFreqInMillis(heartbeatFrqTime, heartbeatFrqRate);
			rowData.put("last_heartbeat", lastHeartbeat);
			boolean isActive = ((lastHeartbeat + heartbeatFrqTime) >= System.currentTimeMillis());
			rowData.put("is_active", isActive);
			rowData.put("is_bundle_upto_date", (lastDeployementRecordTime <= bundleDeployedTs));
			array.add(rowData);
		}
		log.info("PDP configuration and summary, No of records {}", array.size());
		return array;
	}

	@SuppressWarnings("unchecked")
	private long getLastDeploymentRecordsTime() {
		final String queryStr = "SELECT MAX(as_of) last_deployment_record FROM DEPLOYMENT_RECORDS";
		Query query = entityManager.createNativeQuery(queryStr);
		List<Object> results = query.getResultList();
		if (!results.isEmpty()) {
			return readLong(results.get(0));
		}
		return -1L;
	}

	private long getHeartbeatFreqInMillis(long heartbeatFrqTime, String heartbeatFrqRate) {
		if (heartbeatFrqRate.equalsIgnoreCase("minutes")) {
			return heartbeatFrqTime * 60000;
		} else if (heartbeatFrqRate.equalsIgnoreCase("hours")) {
			return heartbeatFrqTime * 60 * 60000;
		} else {
			return heartbeatFrqTime * 1000;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, JSONObject> getPDPThroughputDetails(Long fromDate, Long toDate, DateTimeUnit groupBy) {
		String groupByCol = getGroupByColumn(groupBy);
		final String rpaLogQuery = String.format("select %s time_unit, count(%s) total_requests " +
				"from rpa_log " +
				"where request_date_time >= :fromDate and request_date_time <= :toDate " +
				"group by %s " +
				"order by %s", groupByCol, groupByCol, groupByCol, groupByCol);

		Query query = entityManager.createNativeQuery(rpaLogQuery);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);

		List<Object[]> results = query.getResultList();
		Map<Long, JSONObject> map = new TreeMap<>();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			long time = readLong(row[0]);
			rowData.put("date_time", time);
			rowData.put("count", row[1]);
			map.put(time, rowData);
		}
		log.info("PDP throughput data loaded, No of records {}", map.size());
		return map;
	}

	private String getGroupByColumn(DateTimeUnit groupBy) {
		String groupByCol;
		if (groupBy.equals(DateTimeUnit.MINUTES)) {
			groupByCol = "minute_nb";
		} else if (groupBy.equals(DateTimeUnit.HOURS)) {
			groupByCol = "hour_nb";
		} else if (groupBy.equals(DateTimeUnit.DAYS)) {
			groupByCol = "day_nb";
		} else if (groupBy.equals(DateTimeUnit.WEEKS)) {
			groupByCol = "week_nb";
		} else if (groupBy.equals(DateTimeUnit.MONTHS)) {
			groupByCol = "month_nb";
		} else {
			groupByCol = "hour_nb";
		}
		return groupByCol;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, JSONArray> generatedAlertSummary(Long fromDate, Long toDate, DateTimeUnit groupBy) {
		final String alertQuery = "SELECT DAY_NB, MONITOR_ID, MONITOR_NAME, count(MONITOR_NAME) "
				+ " FROM ALERT WHERE DAY_NB >= :fromDate AND DAY_NB <= :toDate GROUP BY DAY_NB, MONITOR_ID, MONITOR_NAME ORDER BY DAY_NB";

		Query query = entityManager.createNativeQuery(alertQuery);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);

		List<Object[]> results = query.getResultList();
		Map<Long, JSONArray> map = new TreeMap<>();

		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			long date = readLong(row[0]);
			rowData.put("date_time", date);
			rowData.put("monitor_id", row[1]);
			rowData.put("monitor_name", row[2]);
			rowData.put("count", row[3]);

			JSONArray dataArr = map.getOrDefault(date, new JSONArray());
			dataArr.add(rowData);
			map.put(date, dataArr);
		}
		log.info("Alert summary loaded, No of records {}", map.size());
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getActivitySummaryByUser(Long fromDate, Long toDate, String decision, int pageSize) {

		final String queryStr = ""
				+ "SELECT r.user_id user_id, r.user_name, count(r.user_id) total_count from RPA_LOG r"
				+ "      WHERE r.policy_decision = :decision AND r.day_nb >= :fromDate AND r.day_nb <= :toDate "
				+ "      GROUP BY r.user_id, r.user_name ORDER BY total_count desc";

		Query query = entityManager.createNativeQuery(queryStr);
		query.setParameter("decision", decision);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		query.setMaxResults(pageSize);

		List<Object[]> results = query.getResultList();
		JSONArray array = new JSONArray();

		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("user_id", row[0]);
			rowData.put("display_name", row[1]);
			rowData.put("count", row[2]);

			array.add(rowData);
		}
		log.info("Activity summary by user summary loaded, No of records {}", array.size());
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getActivitySummaryByResource(Long fromDate, Long toDate, int pageSize) {

		String dbType = checkDBServerType();
		String isNullFunction = null;
		if ("MSSQL".equals(dbType)) {
			isNullFunction = "isnull";
		} else if ("Oracle".equals(dbType) || "Db2".equals(dbType)) {
			isNullFunction = "nvl";
		} else if ("Postgre".equals(dbType)) {
			isNullFunction = "coalesce";
		}

		Query query = entityManager.createNativeQuery(String.format(SQL_ACTIVITY_SUMMARY_BY_RESOURCE, isNullFunction,
				isNullFunction));
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		query.setMaxResults(pageSize);

		List<Object[]> results = query.getResultList();
		JSONArray array = new JSONArray();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("resource_name", row[0]);
			rowData.put("allow_count", readLong(row[1]));
			rowData.put("deny_count", readLong(row[2]));
			rowData.put("total_count", readLong(row[3]));
			array.add(rowData);
		}
		log.info("Activity summary by resource, No of records {}", array.size());
		return array;
	}

	private String checkDBServerType() {
        Object dialect = entityManager.getEntityManagerFactory().getProperties().get("hibernate.dialect");
        if (dialect != null) {
			if (dialect.toString().contains("SqlServer")) {
				return "MSSQL";
			} else if (dialect.toString().contains("Oracle")) {
				return "Oracle";
			} else if (dialect.toString().contains("Postgre")) {
				return "Postgre";
			} else if (dialect.toString().contains("DB2")) {
				return "Db2";
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getActivitySummaryByPolicy(Long fromDate, Long toDate, int pageSize) {
		final String queryStr = "SELECT policy_fullname, policy_name, policy_decision, COUNT(policy_fullname) total_count FROM RPA_LOG r "
				+ " WHERE day_nb >= :fromDate AND day_nb <= :toDate "
				+ " GROUP BY policy_fullname, policy_name, policy_decision ORDER BY total_count DESC";

		Query query = entityManager.createNativeQuery(queryStr);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		query.setMaxResults(pageSize);

		List<Object[]> results = query.getResultList();
		JSONArray array = new JSONArray();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("policy_fullname", row[0]);
			rowData.put("policy_name", row[1]);
			rowData.put("decision", row[2]);
			rowData.put("count", readLong(row[3]));
			array.add(rowData);
		}
		log.info("Activity summary by policy, No of records {}", array.size());
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getNotMatchingPolicies(Long fromDate, Long toDate, int pageSize) {
		final String queryStr = " SELECT l.fullname, l.deployer FROM "
				+ "  (SELECT d.name fullname, d.submitter deployer, k.policy_fullname policy_fullname FROM DEVELOPMENT_ENTITIES d LEFT JOIN "
				+ "    (SELECT r.policy_fullname policy_fullname FROM RPA_LOG r "
				+ "      WHERE r.day_nb >= :fromDate AND r.day_nb <= :toDate GROUP BY r.policy_fullname) k "
				+ "  ON (k.policy_fullname = CONCAT('/', d.name)) WHERE d.type = 'PO' AND d.status = 'AP' AND d.hidden = 'N') l"
				+ " WHERE l.policy_fullname is null";

		Query query = entityManager.createNativeQuery(queryStr);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		query.setMaxResults(pageSize);

		List<Object[]> results = query.getResultList();
		JSONArray array = new JSONArray();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("policy_fullname", row[0]);
			String policyFullName = (String) row[0];
			String simplePolicyName = getSimplePolicyName(policyFullName);
			rowData.put("policy_name", simplePolicyName);
			Long userId = readLong(row[1]);
			rowData.put("deployer_id", userId);
			String username = getDeployerDisplayName(userId);
			rowData.put("deployer", username);
			array.add(rowData);
		}

		log.info("Not matching policies for given period of time, No of records {}", array.size());
		return array;
	}

	private String getDeployerDisplayName(Long userId) {
        return appUserSearchRepository.findById(userId)
                .map(ApplicationUser::getDisplayName)
                .orElse(StringUtils.EMPTY);
	}

	private String getSimplePolicyName(String policyFullName) {
		int index = policyFullName.lastIndexOf('/');
		return policyFullName.substring(index + 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, JSONObject> rpaLogDataSummary(Long fromDate, Long toDate, String decision) {
		final String rpaLogQuery = "SELECT DAY_NB, POLICY_DECISION, count(ID) FROM RPA_LOG "
				+ " WHERE DAY_NB >= :fromDay AND DAY_NB <= :toDay AND POLICY_DECISION = :decision GROUP BY DAY_NB, POLICY_DECISION ORDER BY DAY_NB";
		Query query = entityManager.createNativeQuery(rpaLogQuery);
		query.setParameter("fromDay", fromDate);
		query.setParameter("toDay", toDate);
		query.setParameter("decision", decision);

		List<Object[]> results = query.getResultList();
		Map<Long, JSONObject> map = new TreeMap<>();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("date", readLong(row[0]));
			rowData.put("decision", row[1]);
			rowData.put("count", row[2]);
			map.put(readLong(row[0]), rowData);
		}
		log.info("RPA log data summary loaded, No of records {}", map.size());
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray registeredAgents() {
		final String agentsQuery = "SELECT a.ID, a.HOST, a.TYPE, a.REGISTERED, a.LASTHEARTBEAT, a.DEPLOYMENT_BUNDLE_TS, "
				+ " c.HEART_BEAT_FREQ_TIME, c.HEART_BEAT_FREQ_TIME_UNIT FROM AGENT a INNER JOIN  COMM_PROFILE c "
				+ " on (a.COMM_PROFILE_ID = c.ID) WHERE a.REGISTERED = :registered";
		Query query = entityManager.createNativeQuery(agentsQuery);
		query.setParameter("registered", 1);

		List<Object[]> results = query.getResultList();
		JSONArray jsonArray = new JSONArray();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("id", row[0]);
			rowData.put("host", row[1]);
			rowData.put("type", row[2]);
			rowData.put("registered", row[3]);
			rowData.put("last_heartbeat_at", readLong(row[4]));
			rowData.put("last_bundle_deployed_at", readLong(row[5]));
			rowData.put("heart_beat_freq", readLong(row[6]));
			rowData.put("heart_beat_freq_unit", row[7]);
			jsonArray.add(rowData);
		}
		log.info("Registered Agent details loaded, No of records {}", jsonArray.size());
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray dictionaryEnrollments() {
		final String dicEnrollments = "SELECT e.id, e.DOMAIN_NAME , e.ENROLLMENT_TYPE , u.IS_SUCCESSFUL, u.ERR_MSG,"
				+ " u.ACTIVE_FROM FROM DICT_ENROLLMENTS e LEFT JOIN DICT_UPDATES u " + " ON ( e.ID = u.ENROLLMENT_ID) "
				+ "WHERE ACTIVE_TO >= :currentTime";
		Query query = entityManager.createNativeQuery(dicEnrollments);
		query.setParameter("currentTime", System.currentTimeMillis());

		List<Object[]> results = query.getResultList();
		JSONArray jsonArray = new JSONArray();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("id", row[0]);
			rowData.put("domain_name", row[1]);
			rowData.put("enrollement_type", row[2]);
			rowData.put("is_successful", row[3]);
			rowData.put("msg", row[4]);
			rowData.put("active_from", row[5]);
			jsonArray.add(rowData);
		}
		log.info("Enrollment details loaded, No of records {}", jsonArray.size());
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray createdMonitors() {
		final String monitorsSQL = "SELECT ID, NAME FROM MONITOR WHERE IS_ACTIVE = :active";
		Query query = entityManager.createNativeQuery(monitorsSQL);
		query.setParameter("active", 1);

		List<Object[]> results = query.getResultList();
		JSONArray jsonArray = new JSONArray();
		for (Object[] row : results) {
			JSONObject rowData = new JSONObject();
			rowData.put("id", row[0]);
			rowData.put("name", row[1]);
			jsonArray.add(rowData);
		}
		log.info("Monitor details loaded, No of records {}", jsonArray.size());
		return jsonArray;
	}

	@Override
	public Long getArchiveLogRowCount() {
		final String monitorsSQL = "SELECT COUNT(*) FROM ARCHIVE_POLICY_ACTIVITY_LOG";
		Query query = entityManager.createNativeQuery(monitorsSQL);

		Object result = query.getSingleResult();
		long archiveLogRowCount = readLong(result);

		log.debug("Archive log table total rows {}", archiveLogRowCount);
		return archiveLogRowCount;
	}

	@Override
	public Long getActivityLogRowCount() {
		final String monitorsSQL = "SELECT COUNT(*) FROM POLICY_ACTIVITY_LOG";
		Query query = entityManager.createNativeQuery(monitorsSQL);

		Object result = query.getSingleResult();
		long activityLogRowCount = readLong(result);
		log.debug("Policy activity logs table total rows {}", activityLogRowCount);
		return activityLogRowCount;
	}

	@Override
	public Long getActivityLogRowCountForGivenPeriod(Long fromDay, Long toDay) {
		final String monitorsSQL = "SELECT COUNT(*) FROM POLICY_ACTIVITY_LOG WHERE DAY_NB >=  :fromDay AND DAY_NB <= :toDay";
		Query query = entityManager.createNativeQuery(monitorsSQL);
		query.setParameter("fromDay", fromDay);
		query.setParameter("toDay", toDay);

		Object result = query.getSingleResult();
		long activityLogRowCount = readLong(result);
		log.debug("Policy activity logs table total rows for given period {}", activityLogRowCount);
		return activityLogRowCount;
	}

	private long readLong(Object val) {
		if (val != null) {
			if (val instanceof Integer) {
				return ((Integer) val).longValue();
			} else if (val instanceof Byte) {
				return ((Byte) val).longValue();
			} else if (val instanceof BigDecimal) {
				return ((BigDecimal) val).longValue();
			} else if (val instanceof BigInteger) {
				return ((BigInteger) val).longValue();
			}
		}
		return 0L;
	}

	private String readChar(Object val) {
		if (val != null) {
			return ((Character) val).toString();
		}
		return null;
	}

}
