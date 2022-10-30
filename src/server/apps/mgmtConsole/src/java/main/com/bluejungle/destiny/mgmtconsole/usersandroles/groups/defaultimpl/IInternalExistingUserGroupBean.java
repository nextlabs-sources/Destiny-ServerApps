/*
 * Created on Sep 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import com.bluejungle.destiny.services.management.types.UserGroupDTO;
import com.bluejungle.destiny.services.policy.types.DefaultAccessAssignmentList;

/**
 * Extension of the IInternalUserGroupBean used to differentiate an existing
 * user group from a newly created one
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/groups/defaultimpl/IInternalExistingUserGroupBean.java#1 $
 */

public interface IInternalExistingUserGroupBean extends IInternalUserGroupBean {

    /**
     * Retrieve the wrapped user group dto instance
     * 
     * @return the wrapped user group dto instance
     */
    public UserGroupDTO getWrappedUserGroupDTO();

    /**
     * Retrieve the wrapped default access assignments
     * 
     * @return the wrapped default access assignments
     */
    public DefaultAccessAssignmentList getWrappedDefaultAccessAssignments();
}