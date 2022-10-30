package com.nextlabs.destiny.inquirycenter.migration;

import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.comp.IConfiguration;
import com.nextlabs.destiny.container.dac.datasync.sync.SyncTask;

public class MigrationTask extends SyncTask {
    
    private long startTime;
    private long timeout;
    private boolean status;
    private static final Log LOG = LogFactory.getLog(SyncTask.class);
    
    private String taskType = null;
    
    private volatile static boolean alreadyInExecution;
    
    public SyncType getType() {
        return SyncType.SYNC;
    }
    
    public MigrationTask(String type)
    {
    	taskType = type;
    }
    
    public void run(Session session, long timeout, IConfiguration config) {
    	
    	try
    	{
    	synchronized (this) {
			if (alreadyInExecution)
			{
				LOG.error("Another instance of migration task is already running");
				return;
			}
			alreadyInExecution = true;
		}
    	
        startTime = System.currentTimeMillis();
        this.timeout = timeout;
        
        if (MigrationServlet.MIGRATE_REPORT_LOGS.equalsIgnoreCase(taskType))
        {
        	runReportLogsMigration(session, config);
        }

        if (MigrationServlet.MIGRATE_ARCHIVE_LOGS.equalsIgnoreCase(taskType))
        {
        	runArchiveLogsMigration(session, config);
        }
        
    	}
    	finally{
    		synchronized (this) {
				alreadyInExecution = false;
			}
    	}
    }
    
    private void runArchiveLogsMigration(Session session, IConfiguration config) {
    	
        LOG.info("Running Migration Task----------------------------------------------------------------------------------------------");
        
        status = false;

        status = new ArchivePolicyActivityLogMigrationTask().run(session, config, this);

        if (status)
        {
        	LOG.info("Completed Archive Policy Log Migration Task----------------------------------------------------------------------------------------------");
        }
        else
        {
        	LOG.info("Error during Archive Policy Log Migration----------------------------------------------------------------------------------------------");
        }
        
        /*status = false;
        
        status = new ArchiveTrackingActivityLogMigrationTask().run(session, config, this);
        
        if (status)
        {
        	LOG.info("Completed Archive Tracking Log Migration Task----------------------------------------------------------------------------------------------");
        }
        else
        {
        	LOG.info("Error during Archive Tracking Log Migration----------------------------------------------------------------------------------------------");
        }*/
		
	}

	private void runReportLogsMigration(Session session, IConfiguration config)
    {
        LOG.info("Running Migration Task----------------------------------------------------------------------------------------------");
        
        status = false;

        
        long start = System.nanoTime();
        status = new ReportPolicyActivityLogMigrationTask().run(session, config, this);
        long end = System.nanoTime();
        
        if (status)
        {
        	LOG.info("Completed Report Policy Log Migration Task----------------------------------------------------------------------------------------------");
        }
        else
        {
        	LOG.info("Error during Report Policy Log Migration----------------------------------------------------------------------------------------------");
        }
        LOG.info("Total time spent for migration = ms " + (end - start)/1000000.00);
        
        /*status = false;
        
        status = new ReportTrackingActivityLogMigrationTask().run(session, config, this);
        
        if (status)
        {
        	LOG.info("Completed Report Tracking Log Migration Task----------------------------------------------------------------------------------------------");
        }
        else
        {
        	LOG.info("Error during Report Tracking Log Migration----------------------------------------------------------------------------------------------");
        }*/
    }
    
    /**
     * 
     * @return
     */
    public boolean getStatus()
    {
    	return status;
    }
    
    /**
     * @return in seconds
     */
    protected int getRemainingTime(){
    	return 0;
    }
}
