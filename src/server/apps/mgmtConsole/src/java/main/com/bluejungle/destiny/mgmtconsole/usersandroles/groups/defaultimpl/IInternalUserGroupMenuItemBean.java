package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupMenuItemBean;
import com.bluejungle.destiny.services.management.types.UserGroupReduced;

/**
 * Internal extension of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupMenuItemBean}
 * 
 * @author sgoldstein
 */
public interface IInternalUserGroupMenuItemBean extends IUserGroupMenuItemBean {

    /**
     * Retrieve the wrapped reduced user group object
     * 
     * @return the wrapped reduced user group object
     */
    public UserGroupReduced getWrappedUserGroupReduced();
    
    /**
     * Retrieve the display value tool-tip associated with this selectable item
     * 
     * @return the display value tool-tip associated with this selectable item
     */
    public String getDomainAsToolTip();
}

