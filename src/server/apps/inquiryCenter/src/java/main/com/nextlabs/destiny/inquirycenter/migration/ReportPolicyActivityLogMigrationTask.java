/*
 * All sources, binaries and HTML pages (C) copyright 2004-2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.Session;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.DBUtil;
import com.bluejungle.framework.datastore.hibernate.BatchWriter;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.bluejungle.framework.utils.CollectionUtils;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.container.dac.datasync.log.ReportPolicyActivityLogWriter;
import com.nextlabs.destiny.container.dac.datasync.log.ReportPolicyActivityLog.CustomAttribute;
import com.nextlabs.destiny.container.dac.datasync.log.ReportPolicyActivityLog;

/**
 * This class is to migrate Report Policy logs to new schema
 * @author nnallagatla
 * 
 */
public class ReportPolicyActivityLogMigrationTask extends
		ReportActivityLogMigrationTask<ReportPolicyActivityLog> {

	/* policy activity log */
	private static final int ID_COLUMN;

	private static final int TIME_COLUMN;
	private static final int MONTH_COLUMN;
	private static final int DAY_COLUMN;

	private static final int HOST_ID_COLUMN;
	private static final int HOST_IP_COLUMN;
	private static final int HOST_NAME_COLUMN;

	private static final int USER_ID_COLUMN;
	private static final int USER_NAME_COLUMN;
	private static final int USER_SID_COLUMN;

	private static final int APP_ID_COLUMN;
	private static final int APP_NAME_COLUMN;

	private static final int ACTION_COLUMN;

	private static final int POLICY_ID_COLUMN;
	private static final int POLICY_FULLNAME_COLUMN;
	private static final int POLICY_NAME_COLUMN;
	private static final int POLICY_DECISION_COLUMN;
	private static final int DECISION_REQUEST_ID_COLUMN;

	private static final int LEVEL_COLUMN;

	private static final int FROM_RESOURCE_NAME_COLUMN;
	private static final int FROM_RESOURCE_SIZE_COLUMN;
	private static final int FROM_RESOURCE_OWNER_ID_COLUMN;
	private static final int FROM_RESOURCE_CREATED_DATE_COLUMN;
	private static final int FROM_RESOURCE_MODIFIED_DATE_COLUMN;
	private static final int FROM_RESOURCE_PREFIX;
	private static final int FROM_RESOURCE_PATH;
	private static final int FROM_RESOURCE_SHORT_NAME;

	private static final int TO_RESOURCE_NAME_COLUMN;

	protected static final String SELECT_LOG_QUERY;
	
	int batchSize = 0;

	static {
		int i = 1;
		ID_COLUMN = i++;

		TIME_COLUMN = i++;
		MONTH_COLUMN = i++;
		DAY_COLUMN = i++;

		HOST_ID_COLUMN = i++;
		HOST_IP_COLUMN = i++;
		HOST_NAME_COLUMN = i++;

		USER_ID_COLUMN = i++;
		USER_NAME_COLUMN = i++;
		USER_SID_COLUMN = i++;

		APP_ID_COLUMN = i++;
		APP_NAME_COLUMN = i++;

		ACTION_COLUMN = i++;

		POLICY_ID_COLUMN = i++;
		POLICY_FULLNAME_COLUMN = i++;
		POLICY_NAME_COLUMN = i++;
		POLICY_DECISION_COLUMN = i++;
		DECISION_REQUEST_ID_COLUMN = i++;

		LEVEL_COLUMN = i++;

		FROM_RESOURCE_NAME_COLUMN = i++;
		FROM_RESOURCE_SIZE_COLUMN = i++;
		FROM_RESOURCE_OWNER_ID_COLUMN = i++;
		FROM_RESOURCE_CREATED_DATE_COLUMN = i++;
		FROM_RESOURCE_MODIFIED_DATE_COLUMN = i++;
		FROM_RESOURCE_PREFIX = i++;
		FROM_RESOURCE_PATH = i++;
		FROM_RESOURCE_SHORT_NAME = i++;

		TO_RESOURCE_NAME_COLUMN = i++;
		
		SELECT_LOG_QUERY = "select id,time,month_nb,day_nb"
				+ ",host_id,host_ip,host_name"
				+ ",user_id,user_name,user_sid"
				+ ",application_id,application_name"
				+ ",action,policy_id,policy_fullname, policy_name, policy_decision"
				+ ",decision_request_id,log_level"
				+ ",from_resource_name,from_resource_size,from_resource_owner_id"
				+ ",from_resource_created_date,from_resource_modified_date"
				+ ",from_resource_prefix,from_resource_path,from_resource_short_name,to_resource_name"
				+ " from %s ORDER BY user_id ASC, time ASC";
	}

	/* policy custom attribute */
	private static final int CUSTOM_ID_COLUMN;
	private static final int CUSTOM_POLICY_ID_COLUMN;
	private static final int CUSTOM_ATTR_NAME_COLUMN;
	private static final int CUSTOM_ATTR_VALUE_COLUMN;

	protected static final String SELECT_CUSTOM_ATTR_QUERY_TEMPLATE;

	static {
		int i = 1;
		CUSTOM_ID_COLUMN = i++;
		CUSTOM_POLICY_ID_COLUMN = i++;
		CUSTOM_ATTR_NAME_COLUMN = i++;
		CUSTOM_ATTR_VALUE_COLUMN = i++;

		SELECT_CUSTOM_ATTR_QUERY_TEMPLATE = "select id,policy_log_id,attribute_name,attribute_value"
				+ " from %s"				
				+ " where policy_log_id in (%s)";
	}

	public ReportPolicyActivityLogMigrationTask() {
		super(ReportPolicyActivityLog.class, String.format(
				COUNT_QUERY_TEMPLATE, SharedLib.REPORT_PA_TABLE));
	}

	protected ReportPolicyActivityLogMigrationTask(Class clazz, String countQuery) {
		super(clazz, countQuery);
	}
	
	@Override
	protected int parse() throws Exception {
		
		boolean lctBeforeUserRecord = false;
		
		batchSize = getBatchSize();
		
		transform = new HashMap<Number, ReportPolicyActivityLog>(
				batchSize);

		ResultSet r = null;
		
		IHibernateRepository dataSource = DBUtil.getActivityDataSource();		
		Session s = null;
		
		BatchWriter<ReportPolicyActivityLog> batchWriter = getWriter();
		batchWriter.setLog(LogFactory.getLog(ActivityLogMigrationWriter.class));
		
		int i = 0;	
		try {
			
			s = dataSource.getSession();
			
			long startTime = 0;

			if (PROFILE_LOG.isDebugEnabled()) {
				startTime = System.currentTimeMillis();
			}

			r = selectLogStatement.executeQuery();

			readMainTableTime+= (System.currentTimeMillis() - startTime);
			
			if (PROFILE_LOG.isDebugEnabled()) {
				long time = System.currentTimeMillis() - startTime;

				if (time > 2000 || PROFILE_LOG.isTraceEnabled()) {
					PROFILE_LOG
							.debug("Time taken for selectLogStatement query: "
									+ (System.currentTimeMillis() - startTime)
									+ " ms");
				}
			}
			
			boolean saved = true;
			/*
			 * last consistent time of dictionary
			 */
			
			if ( dict == null)
			{
				LOG.error("dict is NULL");
			}
			
			Date lct = dict.getLatestConsistentTime();
			long end = 0;
			while (r!= null && r.next()) {
				
				long start = System.currentTimeMillis();
				saved = false;
				ReportPolicyActivityLog t = new ReportPolicyActivityLog(attrColumnMappingConfig);
				
				t.id = r.getLong(ID_COLUMN);
				
				t.time = r.getTimestamp(TIME_COLUMN);
				
				t.day = r.getLong(DAY_COLUMN);

				t.month = r.getLong(MONTH_COLUMN);

				t.hostId = r.getLong(HOST_ID_COLUMN);
				t.hostIp = r.getString(HOST_IP_COLUMN);
				t.hostName = r.getString(HOST_NAME_COLUMN);

				t.userId = r.getLong(USER_ID_COLUMN);
				t.userName = r.getString(USER_NAME_COLUMN);
				t.userSid = r.getString(USER_SID_COLUMN);

				t.applicationId = r.getLong(APP_ID_COLUMN);
				t.applicationName = r.getString(APP_NAME_COLUMN);

				t.action = r.getString(ACTION_COLUMN);

				t.policyDecision = r.getString(POLICY_DECISION_COLUMN);
				t.policyId = r.getLong(POLICY_ID_COLUMN);
				t.policyFullname = r.getString(POLICY_FULLNAME_COLUMN);
				t.policyName = r.getString(POLICY_NAME_COLUMN);
				t.decisionRequestId = r.getLong(DECISION_REQUEST_ID_COLUMN);
				t.logLevel = r.getInt(LEVEL_COLUMN);

				t.fromResourceName = r.getString(FROM_RESOURCE_NAME_COLUMN);
				t.fromResourceSize = r.getLong(FROM_RESOURCE_SIZE_COLUMN);
				t.fromResourceOwnerId = r
						.getString(FROM_RESOURCE_OWNER_ID_COLUMN);
				t.fromResourceCreatedDate = r
						.getLong(FROM_RESOURCE_CREATED_DATE_COLUMN);
				t.fromResourceModifiedDate = r
						.getLong(FROM_RESOURCE_MODIFIED_DATE_COLUMN);
				t.fromResourcePrefix = r.getString(FROM_RESOURCE_PREFIX);
				t.fromResourcePath = r.getString(FROM_RESOURCE_PATH);
				t.fromResourceShortName = r.getString(FROM_RESOURCE_SHORT_NAME);

				t.toResourceName = r.getString(TO_RESOURCE_NAME_COLUMN);

				i++;
				
				end = System.currentTimeMillis();
				
				readMainTableTime+=(end - start);
				
				start = System.currentTimeMillis();
				
				/*
				 * we are trying to optimize calls to dictionary API
				 * 
				 * We have changed select query to sort by user, time.
				 * If the sort order is changed, this optimization will not work
				 * 
				 */
				
				if (t.time == null)
				{
					LOG.info("WA LAO EH!! Time is null");
				}
				
				if (prevRecord != null) {
					
					if (prevRecord.userId != t.userId) {
						
						/*
						 * Did the first record for this new user happened
						 * after last consistent time
						 */
						if (t.time.getTime() >= lct.getTime()) {
							lctBeforeUserRecord = true;
						} else {
							lctBeforeUserRecord = false;
						}
						callsToDictAPI++;
						solveUserAttributes(t);
					}
					else
					{
						if (lctBeforeUserRecord)
						{
							/*
							 * if the user's first record has occurred after
							 * dictionary's last consistent time, then we can
							 * reuse the already lookup values
							 */
							t.userAttrs = prevRecord.userAttrs;
							callsAvoided++;
						}
						else if(t.time.getTime() == prevRecord.time.getTime())
						{
							t.userAttrs = prevRecord.userAttrs;
							callsAvoided++;
						}
						else
						{
							/*
							 * if the current log has occurred after dictionary's
							 * last consistent time, we can avoid dictionary API call for 
							 * next few records of the same 
							 * 
							 */
							if (t.time.getTime() >= lct.getTime()) {
								lctBeforeUserRecord = true;
							} else {
								lctBeforeUserRecord = false;
							}
							solveUserAttributes(t);
							callsToDictAPI++;
						}
					}
				}
				else
				{
					/*
					 * Did the first record for this first user happened
					 * after last consistent time
					 */
					if (t.time.getTime() >= lct.getTime()) {
						lctBeforeUserRecord = true;
					} else {
						lctBeforeUserRecord = false;
					}
					solveUserAttributes(t);
					callsToDictAPI++;
				}
				
				end = System.currentTimeMillis();
				
				prevRecord = t;
				
				fetchUserAttrTime += (end - start);
				
				transform.put(t.id, t);

				if (i % batchSize == 0) {
					LOG.info("time to write batch i= " + i + " batchSize= " + batchSize );
					solvePolicyTags();
					solveCustomAttribute();
					writeProcessedRecords(batchWriter, s);
					transform.clear();
					saved = true;
				}
			}
			
			/*
			 * if the last few records are not saved
			 */
			if (!saved) {
				LOG.info("exited loop");
				solvePolicyTags();
				solveCustomAttribute();
				writeProcessedRecords(batchWriter, s);
				transform.clear();
			}			
		} finally {
			close(r);
			HibernateUtils.closeSession(s, LOG);
		}

		if (LOG.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("get ").append(transform.size())
					.append(" from " + SharedLib.PA_TABLE);
			LOG.trace(sb.toString());

			LOG.trace("Processed " + "");
		}

		return i;
	}
	
	@Override
	protected void solveCustomAttribute(Set<Long> ids) throws SQLException {
		
		if (ids == null || ids.size() == 0) {
			return;
		}
		
		long start = System.currentTimeMillis();
		
		String sql = String.format(getSelectCustomAttrQuery(),
				CollectionUtils.asString(ids, ","));
		
        if (!resourceAttributesBlackList.isEmpty())
        {
        	sql = sql + " AND attribute_name " + getNOTINSQLQuery(resourceAttributesBlackList);
        }
        else
        {
        	LOG.info("ResourceAttributesBlackList is empty");
        }
		
		Statement statement = connection.createStatement();
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(sql);
			while (rs.next()) {
				long policyId = rs.getLong(CUSTOM_POLICY_ID_COLUMN);

				CustomAttribute tca = new CustomAttribute();
				tca.id = rs.getLong(CUSTOM_ID_COLUMN);
				tca.attributeName = rs.getString(CUSTOM_ATTR_NAME_COLUMN);
				tca.attributeValue = rs.getString(CUSTOM_ATTR_VALUE_COLUMN);

				ReportPolicyActivityLog t = transform.get(policyId);
				assert t != null;
				if (t.attrs == null) {
					t.attrs = new LinkedList<CustomAttribute>();
				}
				t.attrs.add(tca);
			}
		} 
		finally {
			close(rs);
			statement.close();
		}
		long end = System.currentTimeMillis();
		
		readAttrTableTime += (end - start);
	}

	@Override
	protected int getResultCheckIndex() {
		// TODO Auto-generated method stub
		return ReportPolicyActivityLogWriter.INSERT_LOG_QUERY_INDEX;
	}

	@Override
	protected String getSelectLogQuery() {
		return String.format(SELECT_LOG_QUERY, SharedLib.REPORT_PA_TABLE);
	}

	@Override
	protected String getSelectCustomAttrQuery() {
		/*
		 * replace %s with %s 
		 */
		return String.format(SELECT_CUSTOM_ATTR_QUERY_TEMPLATE, SharedLib.REPORT_PA_CUST_ATTR_TABLE, "%s");
	}

	@Override
	protected String getInsertCustomAttrQuery() {
		return ReportPolicyActivityLog.INSERT_CUSTOM_ATTR_QUERY;
	}

	@Override
	protected BatchWriter<ReportPolicyActivityLog> getWriter() {
		
		BatchWriter<ReportPolicyActivityLog> writer = new ActivityLogMigrationWriter<ReportPolicyActivityLog>(
				getInsertQuery(),
				getInsertCustomAttrQuery());
		writer.setLog(LogFactory.getLog(ActivityLogMigrationWriter.class));
		return writer;
	}

	@Override
	protected String getInsertQuery() {
		return ReportPolicyActivityLog.getInsertQueryString();
	}
}
