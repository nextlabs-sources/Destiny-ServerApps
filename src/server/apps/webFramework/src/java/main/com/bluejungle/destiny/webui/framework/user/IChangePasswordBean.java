/*
 * Created on Sep 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.user;

import java.rmi.RemoteException;

/**
 * Simple bean holding enough info to change a user's password
 * 
 * @author sasha
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/user/IChangePasswordBean.java#1 $:
 */

public interface IChangePasswordBean {

    /**
     * @return user's old password
     */
    String getOldPassword();    
    
    /**
     * Sets user's old password
     * @param oldPassword old password
     */
    void setOldPassword(String oldPassword);
    
    /**
     * @return user's new password
     */
    String getNewPassword();
    
    /**
     * Sets user's new password
     * @param newPassword new password
     */
    void setNewPassword(String newPassword);

    /**
     * Change the password in the user repository
     */
    void changePassword() throws InvalidPasswordException, RemoteException, PasswordHistoryException;
    
    /*
     * HACK - this added because a version mismatch for JSF and Ajax4jsf, will be removed later
     */
    String getNewConfirmPassword();
    
    void setNewConfirmPassword(String newConfirmPassword);
}
