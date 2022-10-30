/*
 * Created on May 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.usersandroles.roles.IRoleMenuItemBean;
import com.bluejungle.destiny.services.policy.types.SubjectDTO;

/**
 * Internal extension of the IRoleMenuItemBean
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/defaultimpl/IInternalRoleMenuItemBean.java#1 $
 */

public interface IInternalRoleMenuItemBean extends IRoleMenuItemBean {
    public SubjectDTO getWrappedSubjectDTO();
}