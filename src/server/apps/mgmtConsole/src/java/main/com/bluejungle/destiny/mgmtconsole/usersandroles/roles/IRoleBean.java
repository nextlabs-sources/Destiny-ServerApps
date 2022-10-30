/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles;

import java.util.Collection;

import javax.faces.model.DataModel;

/**
 * IRole represents a single role in the system
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/IRoleBean.java#1 $
 */

public interface IRoleBean {
    
    /**
     * Retrieve the role id
     * 
     * @return the role id
     */
    long getRoleId();

    /**
     * Retrieve the role title
     * 
     * @return the role title
     */
    String getRoleTitle();

    Collection<ApplicationResourceBean> getAllResource();
    
    /**
     * Get a <code>ApplicationResourceBean</code> by name
     * @param appName is the internal name such as "Management Console", not the front end name.
     * @return
     */
    ApplicationResourceBean getResourceByName(String name);
    
    /**
     * Get a <code>ApplicationResourceBean</code> by id 
     * @param id is the id used for the html. Beware this is not the full id in the html. 
     *           You can get the id from the <code>ApplicationResourceBean</code>
     * @return
     */
    ApplicationResourceBean getResourceById(String id);
    
    /**
     * Retrieve the configured access settings for the Policy Author application
     * assigned to the associated role
     * 
     * @return the configured access settings for the Policy Author application
     *         assigned to the associated role
     */
    IPolicyAuthorComponentAccessBean getRolePolicyAuthorComponentAccess();

    /**
     * Retrieve the settings determing how default security access is configured
     * when objects are created by members of the associated role
     * 
     * @return a DataModel of IDefaultAccessAssignment instances, each
     *         specifying default object access assignments made for objects
     *         created by the associated role
     */
    DataModel getDefaultObjectSecurityAssignments();
}