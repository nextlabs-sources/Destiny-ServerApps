/*
 * All sources, binaries and HTML pages (C) copyright 2004-2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */

package com.nextlabs.destiny.inquirycenter.migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.datastore.hibernate.BatchWriter;
import com.bluejungle.framework.datastore.hibernate.exceptions.DataSourceException;
import com.nextlabs.destiny.container.dac.datasync.Constants;
import com.nextlabs.destiny.container.dac.datasync.IDataSyncTask;
import com.nextlabs.destiny.container.dac.datasync.log.AttributeColumnMappingInfoWrapper;
import com.nextlabs.destiny.container.dac.datasync.log.ReportLog;
import com.nextlabs.destiny.container.dac.datasync.log.ReportPolicyActivityLog;
import com.nextlabs.destiny.container.dac.datasync.sync.SyncTask;
import com.nextlabs.destiny.container.dac.datasync.sync.SyncTaskBase;

/**
 * @author nnallagatla
 *
 */
public abstract class ReportActivityLogMigrationTask<T extends ReportLog> extends SyncTaskBase<T>{

	protected long readMainTableTime = 0;
	protected long readAttrTableTime = 0;
	protected long fetchUserAttrTime = 0;
	protected long writeTime = 0;
	protected long buildObjectTime = 0;
	
	protected long callsToDictAPI = 0;
	protected long callsAvoided = 0;
	
	protected ReportPolicyActivityLog prevRecord = null;
	
    protected static final Log LOG = LogFactory.getLog(ReportActivityLogMigrationTask.class);
    protected static final Log PROFILE_LOG = LogFactory.getLog(ReportActivityLogMigrationTask.class.getName() + ".profiling");
	
	protected static final String COUNT_QUERY_TEMPLATE = "select count(*) from %s";
	protected PreparedStatement selectTotalCountStatement = null;
	protected String selectTotalCountQuery = null;
	protected PreparedStatement selectLogStatement = null;
	
	public ReportActivityLogMigrationTask(Class clazz,
			String selectTotalCountQuery) {
		super(clazz, selectTotalCountQuery);
		this.selectTotalCountQuery = selectTotalCountQuery;
	}
	
	/**
	 * estimated total number of rows needs to sync
	 * 
	 * @return 0 if unknown
	 */
	protected int getTotalCount() throws SQLException {
		int estimateRows;
		ResultSet r = null;
		
		LOG.info("selectTotalCountStatement is NULL: " + selectTotalCountStatement == null );
		
		try {
			r = selectTotalCountStatement.executeQuery();
			if (r.next()) {
				estimateRows = r.getInt(1);
			} else {
				estimateRows = 0;
			}
		} finally {
			close(r);
		}
		return estimateRows;
	}
	
	@Override
	protected void start() throws Exception {
		selectTotalCountStatement = connection
				.prepareStatement(selectTotalCountQuery);
		String sql = getSelectLogQuery();
		
		LOG.info("---------------SELECT LOG QUERY--------------");
		LOG.info(sql);
		selectLogStatement = connection.prepareStatement(sql);
		
        //initialize Attribute column mapping here
        populateAttrColumnMap();
	}
	
	/**
	 * This method writes the records present in transform attribute to the database
	 * @throws HibernateException
	 * @throws DataSourceException
	 */
    protected void writeProcessedRecords(BatchWriter<T> bWriter, Session session) throws HibernateException, DataSourceException{
    	
    	if (transform == null || transform.isEmpty())
    	{
    		LOG.info("Nothing to write..");
    		return;
    	}
    	
    	if ( session == null || !session.isOpen())
    	{
    		LOG.error("Session is null or not open");
    	}
    	
    	long start = System.currentTimeMillis();
		
		int[][] results = bWriter.log(transform.values(), session);
		logResult(results);
		
		long end = System.currentTimeMillis();
		
		writeTime += (end - start);
	}
	
	
	/**
	 * This method returns the query for selecting records that need to be migrated
	 * @return
	 */
	protected abstract String getSelectLogQuery();
	
	/**
	 * This method returns the query for selecting records from custom_attr tables
	 */
	protected abstract String getSelectCustomAttrQuery();
	
	/**
	 * This method returns query for inserting records in new log table
	 * @return
	 */
	protected abstract String getInsertQuery();
	
	/**
	 * This method returns query for inserting records in new custom attr table
	 * @return
	 */
	protected abstract String getInsertCustomAttrQuery();
	
	protected abstract BatchWriter<T> getWriter();
	
	/**
	 * call this public method to start the sync process
	 * 
	 * @param session
	 * @param config
	 * @param syncTask
	 * @return
	 */

	public boolean run(Session session, IConfiguration config, SyncTask syncTask) {
		LOG.info(this.getClass().getSimpleName() + " start");
		boolean success = false;

		this.update = config.get(IDataSyncTask.TASK_UPDATE_PARAMETER);

		try {
			connection = session.connection();
		} catch (HibernateException e) {
			LOG.error("Fail to open connection", e);
			return false;
		}

		
		String attrCount = (String)config.get(Constants.NUMBER_OF_EXTENDED_ATTRS_PROPERTY);
		
		LOG.info("-------------------------Number of extended Attributes: " + attrCount);
		
		if (attrCount != null && !attrCount.isEmpty())
		{
			try
			{
				int count = Integer.parseInt(attrCount);
				numberOfAdditionalColumns = count;
			}
			catch (NumberFormatException e)
			{
				LOG.error("invalid integer given as number of extended columns. Will proceed with default of " + DEFAULT_EXT_ATTR_COUNT);
			}
		}
		
		/*
		 * need to generate INSERT query based on the number of Additional columns
		 */
		generateInsertQuery(numberOfAdditionalColumns, ATTR_PREFIX);
		
		attrColumnMappingConfig = new AttributeColumnMappingInfoWrapper(numberOfAdditionalColumns, ATTR_PREFIX);
		
		try {
			start();
			int total = getTotalCount();
			LOG.info("Total records to Migrate: " + total);
			int processedRecords = parse();
			LOG.info("Processed Record count: " + processedRecords);
			success = true;
		} catch (Exception e) {
			LOG.info("Info", e);
			
			LOG.error("Error", e);
		}
		finally
		{
			LOG.info("Time taken to read from Main table (ms): " + readMainTableTime);
			LOG.info("Time taken to read from custom attr table (ms): " + readAttrTableTime);
			LOG.info("Time taken to fetch user data from dictionary (ms): " + fetchUserAttrTime);
			LOG.info("Time taken to write data to new tables (ms): " + writeTime);
			LOG.info("Total calls to DICT API: " + callsToDictAPI);
			LOG.info("Calls Avoided to DICT API: " + callsAvoided);
		}

		return success;
	}
	
	@Override
	public void generateInsertQuery(int numOfExtendedAttrs, String attrPrefix)
	{
		ReportPolicyActivityLog.generateInsertQuery(numOfExtendedAttrs, attrPrefix);
	}
}
