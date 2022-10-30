/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.migration;

import com.bluejungle.framework.datastore.hibernate.BatchWriter;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.container.dac.datasync.Constants;
import com.nextlabs.destiny.container.dac.datasync.log.ReportPolicyActivityLog;

/**
 * This class is to migrate Archive policy logs to new schema
 * @author nnallagatla
 *
 */
public class ArchivePolicyActivityLogMigrationTask extends
		ReportPolicyActivityLogMigrationTask {

	public ArchivePolicyActivityLogMigrationTask() {
		super(ReportPolicyActivityLog.class, String.format(
				COUNT_QUERY_TEMPLATE, SharedLib.ARCHIVE_PA_TABLE));
	}
	
	
	@Override
	protected String getSelectLogQuery() {
		return String.format(SELECT_LOG_QUERY, SharedLib.ARCHIVE_PA_TABLE);
	}

	@Override
	protected String getSelectCustomAttrQuery() {
		/*
		 * replace %s with %s 
		 */
		return String.format(SELECT_CUSTOM_ATTR_QUERY_TEMPLATE, SharedLib.ARCHIVE_PA_CUST_ATTR_TABLE, "%s");
	}
	
	@Override
	protected String getInsertQuery() {
		return ReportPolicyActivityLog.getInsertQueryString().replace(
				Constants.REPORT_POLICY_ACTIVITY_LOG_TABLE, Constants.ARCHIVE_POLICY_ACTIVITY_LOG_TABLE);
	}

	@Override
	protected String getInsertCustomAttrQuery() {
		return ReportPolicyActivityLog.INSERT_CUSTOM_ATTR_QUERY.replace(
				Constants.REPORT_POLICY_CUSTOM_ATTR_TABLE, Constants.ARCHIVE_POLICY_CUSTOM_ATTR_TABLE); 
	}

	@Override
	protected BatchWriter<ReportPolicyActivityLog> getWriter() {
		return new ActivityLogMigrationWriter<ReportPolicyActivityLog>(
				getInsertQuery(),
				getInsertCustomAttrQuery());
	}
}
