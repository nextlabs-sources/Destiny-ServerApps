/*
 * Created on Sep 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;


/**
 * Exception throw when a user group is created with or modified to have a title which is not unique
 * 
 * @author sgoldstein
 */
public class NonUniqueUserGroupTitleException extends UserGroupsViewException {

    /**
     * Create an instance of UserGroupsViewException
     *  
     */
    public NonUniqueUserGroupTitleException() {
        super();
    }

    /**
     * Create an instance of UserGroupsViewException
     * 
     * @param cause
     */
    public NonUniqueUserGroupTitleException(Throwable cause) {
        super(cause);
    }

}