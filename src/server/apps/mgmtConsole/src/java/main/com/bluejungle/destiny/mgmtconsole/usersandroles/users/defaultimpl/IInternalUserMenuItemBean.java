/*
 * Created on May 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.usersandroles.users.IUserMenuItemBean;
import com.bluejungle.destiny.services.management.types.UserDTO;

/**
 * Internal extension of the IUserMenuItemBean
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/defaultimpl/IInternalUserMenuItemBean.java#1 $
 */

public interface IInternalUserMenuItemBean extends IUserMenuItemBean {
    public UserDTO getWrappedSubjectDTO();
}
