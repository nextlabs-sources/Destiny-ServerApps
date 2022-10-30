package com.nextlabs.destiny.inquirycenter.report.defaultimpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.destiny.webui.framework.user.IChangePasswordBean;
import com.bluejungle.destiny.webui.framework.user.InvalidPasswordException;
import com.bluejungle.destiny.webui.framework.user.PasswordHistoryException;
import com.nextlabs.destiny.inquirycenter.user.service.AppUserMgmtService;

/**
 * BeanImpl to handle change password issues
 */

public class ChangePasswordBeanImpl implements IChangePasswordBean {

	private static final Log log = LogFactory.getLog(ChangePasswordBeanImpl.class);

	public static final String USER_PREFERENCES_SUFFIX = "/services/UserPreferencesService";
	
	private String oldPassword;
	private String newPassword;
	private String newConfirmPassword;

	private String dataLocation;

	/**
	 * Constructor
	 * 
	 */
	public ChangePasswordBeanImpl() {
		super();	
	}

	/**
	 * Returns the newPassword.
	 * 
	 * @return the newPassword.
	 */
	public String getNewPassword() {
		return this.newPassword;
	}

	/**
	 * Sets the newPassword
	 * 
	 * @param newPassword
	 *            The newPassword to set.
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * Returns the oldPassword.
	 * 
	 * @return the oldPassword.
	 */
	public String getOldPassword() {
		return this.oldPassword;
	}

	/**
	 * Sets the oldPassword
	 * 
	 * @param oldPassword
	 *            The oldPassword to set.
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.user.IChangePasswordBean#changePassword()
	 */
	public void changePassword() throws InvalidPasswordException, PasswordHistoryException {
		AppUserMgmtService userService = new AppUserMgmtService();
		try {
			AppContext ctx = AppContext.getContext();
			ILoggedInUser remoteUser = ctx.getRemoteUser();	
						
			if (remoteUser != null) {
				userService.changePassword(remoteUser.getPrincipalId(), oldPassword, newPassword);
			}
		} catch(InvalidPasswordException e) {
			throw e;
		} catch(PasswordHistoryException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidPasswordException(e);
		}
	}
	 

	/**
	 * Returns the dataLocation.
	 * 
	 * @return the dataLocation.
	 */
	
	  public String getDataLocation() { return this.dataLocation; }
	 
		/* * Sets the dataLocation
		 * 
		 * @param dataLocation
		 *            The dataLocation to set.
		 */
		 
	public void setDataLocation(String dataLocation) {
		this.dataLocation = dataLocation;
	}
		 

	/**
	 * Returns the newConfirmPassword.
	 * 
	 * @return the newConfirmPassword.
	 */
	public String getNewConfirmPassword() {
		return this.newConfirmPassword;
	}

	/**
	 * Sets the newConfirmPassword
	 * 
	 * @param newConfirmPassword
	 *            The newConfirmPassword to set.
	 */
	public void setNewConfirmPassword(String newConfirmPassword) {
		this.newConfirmPassword = newConfirmPassword;
	}
}
