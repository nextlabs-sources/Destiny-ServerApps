/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.migration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.dialect.Dialect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.server.shared.configuration.IReporterComponentConfigurationDO;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyConfigurationStoreImpl;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.configuration.IDestinyConfigurationStore;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.nextlabs.destiny.container.dac.datasync.Constants;
import com.nextlabs.destiny.container.dac.datasync.IDataSyncTask;
import com.nextlabs.destiny.inquirycenter.savedreport.service.SavedReportMigrationService;

/**
 * @author nnallagatla
 *
 */
public class MigrationServlet extends HttpServlet {

	public static final String MIGRATE_REPORT_LOGS = "MIGRATE_REPORT_LOGS";
	public static final String MIGRATE_ARCHIVE_LOGS = "MIGRATE_ARCHIVE_LOGS";
	public static final String MIGRATE_SAVED_REPORTS = "MIGRATE_SAVED_REPORTS";
	
	public static final Log LOG = LogFactory.getLog(MigrationServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	private void doProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		
		String statusMessage = "Migration failed. Please check logs. Clear tables and run again";
		if(action.equalsIgnoreCase(MIGRATE_REPORT_LOGS) || action.equalsIgnoreCase(MIGRATE_ARCHIVE_LOGS))
		{
			IComponentManager compMgr = ComponentManagerFactory
					.getComponentManager();
			
	        final IDestinyConfigurationStore configMgr = compMgr.getComponent(DestinyConfigurationStoreImpl.COMP_INFO);
	        final IReporterComponentConfigurationDO reporterCfg =
	            (IReporterComponentConfigurationDO) configMgr
	                        .retrieveComponentConfiguration(ServerComponentType.REPORTER.getName());
			
			IHibernateRepository dataSource = (IHibernateRepository) compMgr
					.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());
			
			MigrationTask task = new MigrationTask(action);
			boolean migrationStatus = false;
			try {
				task.run(dataSource.getSession(), Integer.MAX_VALUE, getCommonConfig(reporterCfg.getProperties(), dataSource.getDialect()));
				migrationStatus = task.getStatus();
			} catch (HibernateException e) {
				LOG.error(e.getStackTrace());
			} catch (IllegalArgumentException e) {
				LOG.error(e.getStackTrace());
			}			

			if (migrationStatus)
			{
				statusMessage = "Migration Successful";
			}
		}
		else if (action.equalsIgnoreCase(MIGRATE_SAVED_REPORTS))
		{
			try {
				int count = SavedReportMigrationService.migrateSavedReports();
				statusMessage = count  + " Saved Reports have been migrated to new schema";
			} catch (HibernateException e) {
				LOG.error(e.getStackTrace());
			}
		}
		else
		{
			statusMessage = "EXPECTED PARAMETER. NONE WAS PASSED";
		}
		
		PrintWriter pw = response.getWriter();
		pw.write(statusMessage);
		pw.close();
	}

    protected HashMapConfiguration getCommonConfig(Properties props, Dialect dialect){
        HashMapConfiguration config = new HashMapConfiguration();
        config.setProperty(IDataSyncTask.DIALECT_CONFIG_PARAMETER, dialect);
        config.setProperty(IDataSyncTask.TASK_UPDATE_PARAMETER, new MigrationTaskUpdate());
        
        if (props != null)
        {
        	config.setProperty(Constants.USER_ATTRIBUTES_BLACKLIST_PROPERTY, 
        			props.getProperty(Constants.USER_ATTRIBUTES_BLACKLIST_PROPERTY));
        	
        	config.setProperty(Constants.RESOURCE_ATTRIBUTES_BLACKLIST_PROPERTY, 
        			props.getProperty(Constants.RESOURCE_ATTRIBUTES_BLACKLIST_PROPERTY));
        	
        	config.setProperty(Constants.POLICY_ATTRIBUTES_BLACKLIST_PROPERTY, 
        			props.getProperty(Constants.POLICY_ATTRIBUTES_BLACKLIST_PROPERTY));
        	
        	config.setProperty(Constants.NUMBER_OF_EXTENDED_ATTRS_PROPERTY, 
        			props.getProperty(Constants.NUMBER_OF_EXTENDED_ATTRS_PROPERTY));
        }
        
        return config;
    }
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}
}
