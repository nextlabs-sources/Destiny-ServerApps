/*
 * Created on Aug 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

/**
 * @author sasha
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/IUserCreationBean.java#1 $:
 */

public interface IUserCreationBean {

    /**
     * Returns the confirmPassword.
     * @return the confirmPassword.
     */
    String getConfirmPassword();

    /**
     * Sets the confirmPassword
     * @param confirmPassword The confirmPassword to set.
     */
    void setConfirmPassword(String confirmPassword);

    /**
     * Returns the displayName.
     * @return the displayName.
     */
    String getDisplayName();

    /**
     * Sets the displayName
     * @param displayName The displayName to set.
     */
    void setDisplayName(String displayName);

    /**
     * Returns the loginName.
     * @return the loginName.
     */
    String getLoginName();

    /**
     * Sets the loginName
     * @param loginName The loginName to set.
     */
    void setLoginName(String loginName);

    /**
     * Returns the password.
     * @return the password.
     */
    String getPassword();

    /**
     * Sets the password
     * @param password The password to set.
     */
    void setPassword(String password);
    
    /**
     * Saves the created user into directory
     *
     */
    void saveCreatedUser() throws UsersException;
}