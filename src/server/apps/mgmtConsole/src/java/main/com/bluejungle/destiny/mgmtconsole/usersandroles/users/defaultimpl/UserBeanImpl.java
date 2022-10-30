/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.mgmtconsole.CommonConstants;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IUserGroupServiceFacade;
import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.UserGroupServiceFacadeImpl;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.UsersException;
import com.bluejungle.destiny.services.management.UserRoleServiceException;
import com.bluejungle.destiny.services.management.types.CommitFault;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.types.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.services.management.types.UserGroupReducedList;
import com.bluejungle.destiny.services.policy.types.DMSUserData;
import com.bluejungle.destiny.services.policy.types.Role;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.domain.IHasId;

/**
 * Default implementation of the IInternalUser interface
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main
 *          /com/bluejungle/destiny/mgmtconsole/usersandroles/users/defaultimpl/
 *          UserBeanImpl.java#4 $
 */

class UserBeanImpl implements IInternalUserBean {

	private static final String NO_PRIMARY_USER_GROUP_LABEL_KEY = "users_and_roles_users_no_primary_group_selection_label";
	private static final String NO_PRIMARY_USER_GROUP_SELECT_ITEM_VALUE = "null";

	private static final String SYSTEM_ADMINISTRATOR_USER_TITLE = "System Administrator";
	private static final String POLICY_ADMINISTRATOR_USER_TITLE = "Policy Administrator";
	private static final String POLICY_ANALYST_USER_TITLE = "Policy Analyst";
	private static final String BUSINESS_ANALYST_USER_TITLE = "Business Analyst";
	private static final String REPORT_ADMINISTRATOR_USER_TITLE = "Report Administrator";
//	private static final String REPORT_ANALYST_TITLE = "Report Analyst";
	private static final Map USER_TITLE_TO_USER_MAP = new HashMap();
	
	static {
		USER_TITLE_TO_USER_MAP.put(SYSTEM_ADMINISTRATOR_USER_TITLE,
				Role.System_Administrator);
		USER_TITLE_TO_USER_MAP.put(POLICY_ADMINISTRATOR_USER_TITLE,
				Role.Policy_Administrator);
		USER_TITLE_TO_USER_MAP.put(POLICY_ANALYST_USER_TITLE,
				Role.Policy_Analyst);
		USER_TITLE_TO_USER_MAP.put(BUSINESS_ANALYST_USER_TITLE,
				Role.Business_Analyst);
		USER_TITLE_TO_USER_MAP.put(REPORT_ADMINISTRATOR_USER_TITLE,
				Role.Report_Administrator);
//		USER_TITLE_TO_USER_MAP.put(REPORT_ANALYST_TITLE, Role.Report_Analyst);
	}

	protected UserDTO userSubject;
	protected DMSUserData userData;
	private DataModel roleAssignmentDataModel;
	private SelectItem[] primaryUserGroups;
	private String primaryUserGroupId;

	private boolean systemAdmin;
	private boolean policyAdmin;
	private boolean policyAnalyst;
	private boolean businessAnalyst;
	private boolean reportAdmin;
//	private boolean reportAnalyst;

	/**
	 * Create an instance of UserBeanImpl
	 * 
	 * @param userSubject
	 * @param userData
	 * @throws UsersException
	 */
	public UserBeanImpl(UserDTO userSubject, DMSUserData userData)
			throws UsersException {
		// FIX ME - Refactor into methods
		if (userSubject == null) {
			throw new NullPointerException("userSubject cannot be null.");
		}

		if (userData == null) {
			throw new NullPointerException("userData cannot be null.");
		}

		this.userSubject = userSubject;
		this.userData = userData;

		Role[] roles = null;

		this.policyAdmin = this.policyAnalyst = this.businessAnalyst = false;

		roles = this.userData.getRoles();
		for (int i = 0; roles != null && i < roles.length; i++) {
			if (roles[i].getValue().equals("System_Administrator"))
				this.systemAdmin = true;
			if (roles[i].getValue().equals("Policy_Administrator"))
				this.policyAdmin = true;
			if (roles[i].getValue().equals("Policy_Analyst"))
				this.policyAnalyst = true;
			if (roles[i].getValue().equals("Business_Analyst"))
				this.businessAnalyst = true;
			if (roles[i].getValue().equals("Report_Administrator"))
				this.reportAdmin = true;
//			if (roles[i].getValue().equals("Report_Analyst"))
//				this.reportAnalyst = true;
		}

		this.roleAssignmentDataModel = new ArrayDataModel();

		extractPossiblePrimaryGroups(userSubject);
		BigInteger primaryUserGroupIdAsBigInteger = userSubject.getPrimaryUserGroupId().getID();
		if (primaryUserGroupIdAsBigInteger != null) {
			this.primaryUserGroupId = primaryUserGroupIdAsBigInteger.toString();
		} else {
			this.primaryUserGroupId = NO_PRIMARY_USER_GROUP_SELECT_ITEM_VALUE;
		}
	}

	/**
	 * Extract the possible primary user groups
	 * 
	 * @param userSubject
	 * @throws UsersException
	 */
	private void extractPossiblePrimaryGroups(UserDTO userSubject)
			throws UsersException {
		if (this.userSubject.getId().getID().longValue() == IHasId.UNKNOWN_ID.longValue()) {
			this.primaryUserGroups = new SelectItem[] { getNoPrimaryGroupSelectItem() };
		} else {
			try {
				UserGroupReducedList primaryUserGroupsList = getUserGroupServiceFacade()
						.getUserGroupsForUser(userSubject);
				UserGroupReduced[] primaryUserGroupsArray = primaryUserGroupsList
						.getUserGroupReduced();
				if (primaryUserGroupsArray == null) {
					this.primaryUserGroups = new SelectItem[] { getNoPrimaryGroupSelectItem() };
				} else {
					this.primaryUserGroups = new SelectItem[primaryUserGroupsArray.length + 1];
					this.primaryUserGroups[0] = getNoPrimaryGroupSelectItem();
					for (int i = 0; i < primaryUserGroupsArray.length; i++) {
						String value = primaryUserGroupsArray[i].getId()
								.toString();
						String label = primaryUserGroupsArray[i].getTitle();
						this.primaryUserGroups[i + 1] = new SelectItem(value,
								label);
					}
				}
			} catch (RemoteException | ServiceNotReadyFault | CommitFault | UnauthorizedCallerFault exception) {
				throw new UsersException(
						"Failed to load user groups for user with id, "
								+ this.userSubject.getId(), exception);
			}
		}
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#getUserId()
	 */
	public long getUserId() {
		return this.userSubject.getId().getID().longValue();
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUser#getUserTitle()
	 */
	public String getUserTitle() {
		return userSubject.getName();
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#getFirstName()
	 */
	public String getFirstName() {
		return userSubject.getFirstName();
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#getLastName()
	 */
	public String getLastName() {
		return userSubject.getLastName();
	}

	public String getLoginName() {
		return userSubject.getUid();
	}

	public boolean getLocal() {
		return userSubject.getLocal();
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#setFirstName(java.lang.String)
	 */
	public void setFirstName(String firstName) {
		userSubject.setFirstName(firstName);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#setLastName(java.lang.String)
	 */
	public void setLastName(String lastName) {
		userSubject.setLastName(lastName);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#setLoginName(java.lang.String)
	 */
	public void setLoginName(String loginName) {
		userSubject.setUid(loginName);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		userSubject.setPassword(password);
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IInternalUserBean#save()
	 */
	public void save() throws UsersException, RemoteException, UserRoleServiceException {
		if (this.primaryUserGroupId
				.equals(NO_PRIMARY_USER_GROUP_SELECT_ITEM_VALUE)) {
			this.userSubject.setPrimaryUserGroupId(null);
		} else {
			ID primaryUserGroupId = new ID();
			primaryUserGroupId.setID(new BigInteger(this.primaryUserGroupId));
			this.userSubject.setPrimaryUserGroupId(primaryUserGroupId);
		}

		IUserServiceFacade userService = ComponentManagerFactory
				.getComponentManager()
				.getComponent(UserServiceFacadeImpl.class);
		userService.updateUser(userSubject, getWrappedDMSUserData());
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IInternalUserBean#getWrappedDMSUserData()
	 */
	public DMSUserData getWrappedDMSUserData() {
		int numRoles = 0;
		if (isSystemAdmin())
			numRoles++;
		if (isPolicyAdmin())
			numRoles++;
		if (isPolicyAnalyst())
			numRoles++;
		if (isBusinessAnalyst())
			numRoles++;
		if (isReportAdmin())
			numRoles++;
//		if (isReportAnalyst())
//			numRoles++;

		Role[] roles = new Role[numRoles];
		int i = 0;
		if (isSystemAdmin()) {
			roles[i++] = Role.System_Administrator;
		}
		if (isPolicyAdmin()) {
			roles[i++] = Role.Policy_Administrator;
		}
		if (isPolicyAnalyst()) {
			roles[i++] = Role.Policy_Analyst;
		}
		if (isBusinessAnalyst()) {
			roles[i++] = Role.Business_Analyst;
		}
		if (isReportAdmin()) {
			roles[i++] = Role.Report_Administrator;
		}
//		if (isReportAnalyst()) {
//			roles[i++] = Role.Report_Analyst;
//		}

		this.userData.setRoles(roles);

		return this.userData;
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IInternalUserBean#getWrappedUserDTO()
	 */
	public UserDTO getWrappedUserDTO() {
		return this.userSubject;
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl.IUserBean#getRoleAssignments()
	 */
	public DataModel getRoleAssignments() {
		return this.roleAssignmentDataModel;
	}

	public boolean isReportAdmin() {
		return reportAdmin;
	}
	
	public boolean getReportAdmin() {
		return reportAdmin;
	}

	public void setReportAdmin(boolean reportAdmin) {
		this.reportAdmin = reportAdmin;
	}

	public boolean isSystemAdmin() {
		return this.systemAdmin;
	}

	public void setSystemAdmin(boolean val) {
		this.systemAdmin = val;
	}

	public boolean getPolicyAdmin() {
		return this.policyAdmin;
	}

	public void setPolicyAdmin(boolean val) {
		this.policyAdmin = val;
	}

	public boolean isPolicyAdmin() {
		return this.policyAdmin;
	}

	public boolean getPolicyAnalyst() {
		return this.policyAnalyst;
	}

	public void setPolicyAnalyst(boolean val) {
		this.policyAnalyst = val;
	}

	public boolean isPolicyAnalyst() {
		return this.policyAnalyst;
	}

	public boolean getBusinessAnalyst() {
		return this.businessAnalyst;
	}

	public void setBusinessAnalyst(boolean val) {
		this.businessAnalyst = val;
	}

	public boolean isBusinessAnalyst() {
		return this.businessAnalyst;
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#getPrimaryUserGroupId()
	 */
	public String getPrimaryUserGroupId() {
		return this.primaryUserGroupId;
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#setPrimaryUserGroupId(java.lang.String)
	 */
	public void setPrimaryUserGroupId(String primaryGroupId) {
		if (primaryGroupId == null) {
			throw new NullPointerException("primaryGroupId cannot be null.");
		}

		this.primaryUserGroupId = primaryGroupId;
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#getPrimaryUserGroups()
	 */
	public SelectItem[] getPrimaryUserGroups() {
		return this.primaryUserGroups;
	}

	/**
	 * @see com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserBean#isNew()
	 */
	public boolean isNew() {
		return this.userSubject.getId().getID().longValue() == IHasId.UNKNOWN_ID
				.longValue();
	}

	private SelectItem getNoPrimaryGroupSelectItem() {
		String noPrimaryGroupLabel = getManagementConsoleBundle().getString(
				NO_PRIMARY_USER_GROUP_LABEL_KEY);
		return new SelectItem(NO_PRIMARY_USER_GROUP_SELECT_ITEM_VALUE,
				noPrimaryGroupLabel);
	}

	/**
	 * Retrieve the Management Console Resource Bundle
	 * 
	 * @return the Management Console Resource Bundle
	 */
	private ResourceBundle getManagementConsoleBundle() {
		Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		return ResourceBundle.getBundle(
				CommonConstants.MGMT_CONSOLE_BUNDLE_NAME, currentLocale);
	}

	/**
	 * Retrieve the Group Service Facade
	 * 
	 * @return the Group Service Facade
	 */
	private IUserGroupServiceFacade getUserGroupServiceFacade() {
		return (IUserGroupServiceFacade) ComponentManagerFactory
				.getComponentManager().getComponent(
						UserGroupServiceFacadeImpl.class);
	}
}
