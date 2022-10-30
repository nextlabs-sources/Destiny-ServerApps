/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.savedreport.service.impl;

import static com.bluejungle.destiny.container.shared.applicationusers.core.IApplicationUserManager.SUPER_USER_USERNAME;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.container.shared.applicationusers.core.IApplicationUserManager;
import com.bluejungle.destiny.container.shared.inquirymgr.hibernateimpl.MonitorDO;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.pf.destiny.lifecycle.DevelopmentEntity;
import com.bluejungle.pf.destiny.lifecycle.EntityManagementException;
import com.bluejungle.pf.destiny.lifecycle.EntityType;
import com.bluejungle.pf.destiny.lifecycle.LifecycleManager;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.SavedReportDO;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.UserServiceDao;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.UserServiceDaoImpl;
import com.nextlabs.destiny.inquirycenter.savedreport.service.ReporterAccessControlService;
import com.nextlabs.destiny.inquirycenter.web.filter.ApplicationUserDetailsFilter;

/**
 * @author asilva
 *
 */
public class ReporterAccessControlServiceImpl implements ReporterAccessControlService {

	public static final Log LOG = LogFactory.getLog(ReporterAccessControlServiceImpl.class);

	// private UserGroupServiceIF userGroupService;
	// private UserRoleServiceIF userService;

	/**
	 * Default Constructor
	 * 
	 * @param userGroupService
	 */
	public ReporterAccessControlServiceImpl() {

	}

	/**
	 * Constructor
	 * 
	 * @param userService
	 * @param userGroupService
	 */
	// public ReporterAccessControlServiceImpl(UserRoleServiceIF userService,
	// UserGroupServiceIF userGroupService) {
	// super();
	// this.userService = userService;
	// this.userGroupService = userGroupService;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nextlabs.destiny.inquirycenter.savedreport.service.
	 * ReportAccessControlService
	 * #getComponentPQL(com.bluejungle.pf.destiny.lifecycle.EntityType,
	 * java.lang.String)
	 */
	@Override
	public DevelopmentEntity getDevelopmetEntity(EntityType type, String componentName)
			throws EntityManagementException {

		DevelopmentEntity devEntity = getLifecycleManager().getEntityForName(type, componentName,
				LifecycleManager.MUST_EXIST);

		return devEntity;
	}

	@Override
	public boolean checkAccessForDevEntity(ILoggedInUser user, DevelopmentEntity entity) {
		boolean hasAccess = false;
		if (entity != null) {
			String pql = entity.getPql();
			hasAccess = checkAccess(pql, user.getPrincipalName());
		}
		return hasAccess;

		// try {
		// IDSubject userSubject = new Subject(
		// null
		// , user.getUsername()
		// , user.getUsername()
		// , user.getPrincipalId()
		// , SubjectType.USER);
		//
		// DomainObjectBuilder appObjectBuilder = new DomainObjectBuilder(
		// entity.getPql());
		// IDSpec appSpec;
		// appSpec = (SpecBase) appObjectBuilder.processSpec();
		//
		// DomainObjectBuilder accessPolicyBuilder = new DomainObjectBuilder(
		// entity.getApPql());
		// AccessPolicy accessPolicy = (AccessPolicy) accessPolicyBuilder
		// .processAccessPolicy();
		// appSpec.setAccessPolicy(accessPolicy);
		//
		// // We don't need id, real name for the purposes of ownership
		// appSpec.setOwner(new Subject(entity.getOwner().toString(), entity
		// .getOwner().toString(), entity.getOwner().toString(), entity
		// .getOwner(), SubjectType.USER));
		//
		// hasAccess = appSpec.checkAccess(userSubject,
		// DAction.getAction(IDAction.READ_NAME));
		//
		//
		// } catch (PQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return hasAccess;
	}

	@Override
	public boolean hasReportAccess(ILoggedInUser user, List<Long> userGroupIds, SavedReportDO report) {

		Object authAttributes = AppContext.getContext().getAttribute(ApplicationUserDetailsFilter.APP_AUTHS_ATTR);
		if (report.getOwnerId().equals(user.getPrincipalId()) || user.getPrincipalName().equals(SUPER_USER_USERNAME)) {
			return true;
		} else if (report.getSharedMode().trim().equals(SavedReportDO.SAVED_REPORT_PUBLIC)) {
			return true;
		} else if (report.getSharedMode().trim().equals(SavedReportDO.SAVED_REPORT_USERS)) {
			boolean hasAccess = checkAccess(report.getPqlData(), user.getPrincipalName());
			return (hasAccess) ? hasAccess : checkHasGroupLevelAccess(report.getPqlData(), userGroupIds);
		} else if (authAttributes != null) {
			Set<String> auths = (Set<String>) authAttributes;
			for (String auth : auths) {
				if (auth.contains("REPORTER")) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean hasMonitorAccess(ILoggedInUser user, List<Long> userGroupIds, MonitorDO monitor) {

		Object authAttributes = AppContext.getContext().getAttribute(ApplicationUserDetailsFilter.APP_AUTHS_ATTR);
		if (monitor.getOwnerId().equals(user.getPrincipalId()) || user.getPrincipalName().equals(SUPER_USER_USERNAME)) {
			return true;
		} else if (monitor.getSharedMode().trim().equals(MonitorDO.SAVED_REPORT_PUBLIC)) {
			return true;
		} else if (monitor.getSharedMode().trim().equals(MonitorDO.SAVED_REPORT_USERS)) {
			boolean hasAccess = checkAccess(monitor.getPqlData(), user.getPrincipalName());
			return (hasAccess) ? hasAccess : checkHasGroupLevelAccess(monitor.getPqlData(), userGroupIds);
		} else if (authAttributes != null) {
			Set<String> auths = (Set<String>) authAttributes;
			for (String auth : auths) {
				if (auth.contains("MONITOR")) {
					return true;
				}
			}
		}
		return false;
	}

	public UserGroupReduced[] findGroupsForUser(ILoggedInUser remoteUser) {
		UserGroupReduced[] userGroups = null;
		try {
			UserServiceDao userDao = new UserServiceDaoImpl();

			// Administrator - Super User Id = 0
			/*
			 * UserGroupReducedList userGroupList = null; if
			 * (remoteUser.getPrincipalId().longValue() == 0) { userGroupList =
			 * userGroupService.getAllUserGroups(); } else { UserDTO user =
			 * userService.getUser(BigInteger
			 * .valueOf(remoteUser.getPrincipalId())); userGroupList =
			 * userGroupService.getUserGroupsForUser(user); }
			 * 
			 * if (userGroupList != null && userGroupList.getUserGroupReduced()
			 * != null) userGroups = userGroupList.getUserGroupReduced();
			 */

			if (remoteUser == null || remoteUser.getPrincipalId().longValue() == 0) {
				userGroups = userDao.getAllUserGroups();
			} else {
				userGroups = userDao.getUserGroupsByUserId(remoteUser.getPrincipalId());
			}

		} catch (Exception e) {
			LOG.error("Error occurred in loading the user groups for current user", e);
		}
		return userGroups;
	}

	private boolean checkAccess(String pql, String principalname) {
		int startIndx = pql.lastIndexOf("(");
		int endIndx = pql.lastIndexOf(")");

		if (startIndx > -1 && endIndx > -1) {

			String userRoleData = pql.substring(startIndx + 1, endIndx);
			String[] usernameGroups = userRoleData.split(" OR ");
			for (String value : usernameGroups) {
				String[] keyValue = value.split(" = ");

				if (keyValue.length == 2 && keyValue[0].trim().equals("user.name")) {
					String uname = keyValue[1].replace("\"", "").trim();
					if (uname.equals(principalname))
						return true;
				}
			}

		} else {

			startIndx = pql.lastIndexOf("=");
			if (startIndx > -1) {
				String username = pql.substring(startIndx + 1, pql.length()).replace("\"", "").trim();
				if (username.equals(principalname)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkHasGroupLevelAccess(String pql, List<Long> groupIds) {

		if (pql.lastIndexOf("group.id") < 0)
			return false;

		int startIndx = pql.lastIndexOf("(");
		int endIndx = pql.lastIndexOf(")");

		if (startIndx > -1 && endIndx > -1) {

			String userRoleData = pql.substring(startIndx + 1, endIndx);
			String[] usernameGroups = userRoleData.split(" OR ");
			for (String value : usernameGroups) {
				String[] keyValue = value.split(" = ");

				if (keyValue.length == 2 && keyValue[0].equals("group.id")) {
					String groupId = keyValue[1].replace("\"", "").trim();
					if (groupIds.contains(Long.valueOf(groupId)))
						return true;
				}
			}

		} else {

			startIndx = pql.lastIndexOf("=");
			if (startIndx > -1) {
				String groupId = pql.substring(startIndx + 1, pql.length()).replace("\"", "").trim();
				if (groupIds.contains(Long.valueOf(groupId)))
					return true;

			}
		}
		return false;
	}

	private static final String SAVED_REPORT_PQL_TEMPLATE = "ID {pql_id} STATUS APPROVED CREATOR \"{current_user}\" \n"
			+ "ACCESS_POLICY\n" + "ACCESS_CONTROL\n" + "ALLOWED_ENTITIES\n"
			+ "HIDDEN COMPONENT \"Save Report\" = ({user_ids})";

	@Override
	public String generateReportDataAccessPQL(SavedReportDO savedReport, List<String> users, List<Long> groupIds) {
		LOG.debug("Generate Report Data Access PQL ");
		String pqlId = (savedReport.getId() == null) ? "" + (int) (Math.random() * 1000) : "" + savedReport.getId();
		String owner = "" + savedReport.getOwnerId();

		boolean hasNext = false;
		StringBuilder buffer = new StringBuilder();
		for (String user : users) {
			if (hasNext) {
				buffer.append(" OR ");
			}
			buffer.append("user.name = \"").append(user).append("\"");
			hasNext = true;
		}

		for (Long grpId : groupIds) {
			if (hasNext) {
				buffer.append(" OR ");
			}
			buffer.append("group.id = \"").append(grpId).append("\"");
			hasNext = true;
		}

		String pqlData = SAVED_REPORT_PQL_TEMPLATE.replace("{pql_id}", pqlId).replace("{current_user}", owner);
		pqlData = pqlData.replace("{user_ids}", buffer.toString());

		LOG.debug("Generate Report Data Access PQL - Completed");
		return pqlData;
	}

	private static final String MONITOR_PQL_TEMPLATE = "ID {pql_id} STATUS APPROVED CREATOR \"{current_user}\" \n"
			+ "ACCESS_POLICY\n" + "ACCESS_CONTROL\n" + "ALLOWED_ENTITIES\n"
			+ "HIDDEN COMPONENT \"Monitor\" = ({user_ids})";

	@Override
	public String generateMonitorDataAccessPQL(MonitorDO monitorDO, List<String> users, List<Long> groupIds) {
		LOG.debug("Generate Monitor Data Access PQL ");
		String pqlId = (monitorDO.getId() == null) ? "" + (int) (Math.random() * 1000) : "" + monitorDO.getId();
		String owner = "" + monitorDO.getOwnerId();

		boolean hasNext = false;
		StringBuilder buffer = new StringBuilder();
		for (String user : users) {
			if (hasNext) {
				buffer.append(" OR ");
			}
			buffer.append("user.name = \"").append(user).append("\"");
			hasNext = true;
		}

		for (Long grpId : groupIds) {
			if (hasNext) {
				buffer.append(" OR ");
			}
			buffer.append("group.id = \"").append(grpId).append("\"");
			hasNext = true;
		}

		String pqlData = MONITOR_PQL_TEMPLATE.replace("{pql_id}", pqlId).replace("{current_user}", owner);
		pqlData = pqlData.replace("{user_ids}", buffer.toString());

		LOG.debug("Generate Monitor Data Access PQL - Completed");
		return pqlData;
	}

	private LifecycleManager getLifecycleManager() {
		IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
		ComponentInfo<LifecycleManager> COMP_INFO = new ComponentInfo(LifecycleManager.class,
				LifestyleType.SINGLETON_TYPE);
		return (LifecycleManager) componentManager.getComponent(LifecycleManager.COMP_INFO);
	}

}
