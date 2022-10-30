/*
 * Created on May 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

/**
 * IUser represents a single user in the system
 * 
 * @author pkeni
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/IUserBean.java#2 $
 */

public interface IUserBean {

    /**
     * Retrieve the user id
     * 
     * @return the user id
     */
    public long getUserId();

    /**
     * Retrieve the user title
     * 
     * @return the user title
     */
    public String getUserTitle();

    /**
     * @return user's first name
     */
    public String getFirstName();

    /**
     * Sets the user's first name
     * 
     * @param firstName
     *            first name
     */
    public void setFirstName(String firstName);

    /**
     * @return user's last name
     */
    public String getLastName();

    /**
     * Sets the user's last name
     * 
     * @param lastName
     *            last name
     */
    public void setLastName(String lastName);

    /**
     * @return user's login name
     */
    public String getLoginName();

    /**
     * Sets the user's login name
     * 
     * @param loginName
     *            login name
     */
    public void setLoginName(String loginName);

    /**
     * @return true if this user is stored in Destiny's own (local) repository,
     *         false otherwise.
     */
    public boolean getLocal();

    /**
     * Set's the user's password
     * 
     * @param password
     */
    public void setPassword(String password);

    /**
     * Retrieve the settings determing how role assignement is configured for
     * the user..
     * 
     * @return a DataModel of IRoleAssignment instances, each specifying role
     *         assignments made for the user
     */
    public DataModel getRoleAssignments();

    public void setPolicyAdmin(boolean val);

    public boolean isPolicyAdmin();

    public void setPolicyAnalyst(boolean val);

    public boolean isPolicyAnalyst();

    public void setBusinessAnalyst(boolean val);

    public boolean isBusinessAnalyst();

    public void setSystemAdmin(boolean val);

    public boolean isSystemAdmin();
    
    public boolean isReportAdmin();

	public void setReportAdmin(boolean reportAdmin);


    /**
     * Retrieve the id of the primary group assigned to this user
     * 
     * @return the id of the primary group assigned to this user
     */
    public String getPrimaryUserGroupId();

    /**
     * Set the id of the primary group assigned to this user
     * 
     * @param primaryGroupId
     *            the primary group to be assigned to this user
     */
    public void setPrimaryUserGroupId(String primaryGroupId);

    /**
     * Retrieve the possible primary user groups which can be assigned to this
     * user. The array will include one user group representing the case when no
     * primary user group has been assigned
     * 
     * @return an array of possible primary users groups which can be assigned
     *         to this user.
     */
    public SelectItem[] getPrimaryUserGroups();

    /**
     * Determine if this user bean represents a new user (one which has not been
     * persisted)
     * 
     * @return true if the user is new; false otherwise
     */
    public boolean isNew();
}
