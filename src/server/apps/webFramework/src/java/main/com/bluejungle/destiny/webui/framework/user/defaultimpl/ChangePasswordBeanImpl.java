/*
 * Created on Sep 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.user.defaultimpl;

import java.rmi.RemoteException;

import com.bluejungle.destiny.interfaces.user_preferences.v1.AccessDeniedFault;
import com.bluejungle.destiny.interfaces.user_preferences.v1.InvalidPasswordFault;
import com.bluejungle.destiny.interfaces.user_preferences.v1.UserPreferencesServiceStub;
import com.bluejungle.destiny.webui.framework.user.IChangePasswordBean;
import com.bluejungle.destiny.webui.framework.user.InvalidPasswordException;
import com.bluejungle.destiny.webui.framework.user.PasswordHistoryException;
import org.apache.axis2.AxisFault;

/**
 * @author sasha
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/user/defaultimpl/ChangePasswordBeanImpl.java#1 $:
 */

public class ChangePasswordBeanImpl implements IChangePasswordBean {
    
    public static final String USER_PREFERENCES_SUFFIX = "/services/UserPreferencesService";
    
    private int enforcePasswordHistory;
    private String oldPassword;
    private String newPassword;
    private String newConfirmPassword;
    
    private String dataLocation;
    private UserPreferencesServiceStub userPreferencesService;

    /**
     * Constructor
     * 
     */
    public ChangePasswordBeanImpl() {
        super();
    }
    
    /**
     * Returns the newPassword.
     * @return the newPassword.
     */
    public String getNewPassword() {
        return this.newPassword;
    }
    /**
     * Sets the newPassword
     * @param newPassword The newPassword to set.
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    /**
     * Returns the oldPassword.
     * @return the oldPassword.
     */
    public String getOldPassword() {
        return this.oldPassword;
    }
    /**
     * Sets the oldPassword
     * @param oldPassword The oldPassword to set.
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * Returns the enforcePasswordHistory
     * @return the enforcePasswordHistory
     */
    public int getEnforcePasswordHistory() {
    	return this.enforcePasswordHistory;
    }
    
    /**
     * Sets the enforcePasswordHistory
     * @param enforcePasswordHistory The enforcePasswordHistory to set.
     */
    public void setEnforcePasswordHistory(int enforcePasswordHistory) {
    	this.enforcePasswordHistory = enforcePasswordHistory;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.user.IChangePasswordBean#changePassword()
     */
    public void changePassword() throws InvalidPasswordException, RemoteException, PasswordHistoryException {
        try {
            getUserManagementService().changePassword(oldPassword, newPassword);
        } catch (InvalidPasswordFault e) {
            throw new InvalidPasswordException(e);
        } catch (AccessDeniedFault exception) {
            // FIX ME - Should not see this
            throw new RemoteException("User is not authorized.",  exception);
        }
    }
    
    private UserPreferencesServiceStub getUserManagementService() throws AxisFault {
        if(this.userPreferencesService == null) {
            final String userManagementServiceLocation = dataLocation + USER_PREFERENCES_SUFFIX;
            this.userPreferencesService = new UserPreferencesServiceStub(userManagementServiceLocation);
        }
        return this.userPreferencesService;
    }    
    
    /**
     * Returns the dataLocation.
     * @return the dataLocation.
     */
    public String getDataLocation() {
        return this.dataLocation;
    }
    /**
     * Sets the dataLocation
     * @param dataLocation The dataLocation to set.
     */
    public void setDataLocation(String dataLocation) {
        this.dataLocation = dataLocation;
    }

    /**
     * Returns the newConfirmPassword.
     * @return the newConfirmPassword.
     */
    public String getNewConfirmPassword() {
        return this.newConfirmPassword;
    }
    
    /**
     * Sets the newConfirmPassword
     * @param newConfirmPassword The newConfirmPassword to set.
     */
    public void setNewConfirmPassword(String newConfirmPassword) {
        this.newConfirmPassword = newConfirmPassword;
    }
}
