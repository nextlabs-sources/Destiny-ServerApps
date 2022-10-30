/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.savedreport.service;

import java.util.List;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.MonitorDO;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.pf.destiny.lifecycle.DevelopmentEntity;
import com.bluejungle.pf.destiny.lifecycle.EntityManagementException;
import com.bluejungle.pf.destiny.lifecycle.EntityType;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.SavedReportDO;

/**
 * @author asilva
 *
 */
public interface ReporterAccessControlService {

	/**
	 * Retrive the Development entity by type and component name
	 * 
	 * @param type
	 * @param componentName
	 * @return
	 * @throws EntityManagementException
	 */
	DevelopmentEntity getDevelopmetEntity(EntityType type, String componentName)
			throws EntityManagementException;

	/**
	 * Check given user has access.
	 * 
	 * @param user
	 * @param entity
	 * @return
	 */
	boolean checkAccessForDevEntity(ILoggedInUser user, DevelopmentEntity entity);
	
	/**
	 * Check current user has access to the monitoring
	 * 
	 * @param user
	 * @param userGrpIds
	 * @param report
	 * @return
	 */
	boolean hasMonitorAccess(ILoggedInUser user, List<Long> userGroupIds,
			MonitorDO monitor); 

	/**
	 * Check current user has access to the report
	 * 
	 * @param user
	 * @param userGrpIds
	 * @param report
	 * @return
	 */
	boolean hasReportAccess(ILoggedInUser user, List<Long> userGrpIds,
			SavedReportDO report);

	/**
	 * Generate PQL for the report access control.
	 * 
	 * @param users
	 * @param groupIds
	 * @return
	 */
	String generateReportDataAccessPQL(SavedReportDO savedReport,
			List<String> users, List<Long> groupIds);
	
	
	/**
	 * Generate PQL for the monitor access control.
	 * 
	 * @param users
	 * @param groupIds
	 * @return
	 */
	String generateMonitorDataAccessPQL(MonitorDO monitorDo,
			List<String> users, List<Long> groupIds);

	/**
	 * Find the groups assigned to the user.
	 * 
	 * @param remoteUser
	 * @return
	 */
	UserGroupReduced[] findGroupsForUser(ILoggedInUser remoteUser);

}
