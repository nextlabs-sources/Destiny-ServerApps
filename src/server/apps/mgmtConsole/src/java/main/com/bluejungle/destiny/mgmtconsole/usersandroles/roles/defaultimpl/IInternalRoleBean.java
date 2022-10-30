/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRoleBean;
import com.bluejungle.destiny.services.policy.types.DMSRoleData;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;

/**
 * Extension of the IRole interface for internal use
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/defaultimpl/IInternalRoleBean.java#1 $
 */

public interface IInternalRoleBean extends IRoleBean {

    /**
     * Retrieve the Subject DTO associated with this role bean
     * 
     * @return the Subject DTO associated with this role bean
     */
    public SubjectDTO getWrappedSubjectDTO();

    /**
     * Retrieve the role data associated with this role bean
     * 
     * @return the role data associated with this role bean
     */
    public DMSRoleData getWrappedDMSRoleData();

}