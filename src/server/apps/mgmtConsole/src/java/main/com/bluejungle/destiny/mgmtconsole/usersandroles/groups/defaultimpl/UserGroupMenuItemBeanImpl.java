package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import com.bluejungle.destiny.services.management.types.UserGroupReduced;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalUserGroupMenuItemBean}
 * 
 * @author sgoldstein
 */
public class UserGroupMenuItemBeanImpl implements IInternalUserGroupMenuItemBean {

    private UserGroupReduced wrappedUserGroupReduced;

    /**
     * Create an instance of GroupMenuItemBeanImpl
     * 
     * @param userGroupReduced
     */
    public UserGroupMenuItemBeanImpl(UserGroupReduced userGroupReduced) {
        if (userGroupReduced == null) {
            throw new NullPointerException("userGroupReduced cannot be null.");
        }

        this.wrappedUserGroupReduced = userGroupReduced;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupMenuItemBean#getUserGroupId()
     */
    public String getUserGroupId() {
        return this.wrappedUserGroupReduced.getId().toString();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupMenuItemBean#getUserGroupTitle()
     */
    public String getUserGroupTitle() {
        return this.wrappedUserGroupReduced.getTitle();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IInternalExistingUserGroupBean#getDomainAsToolTip()
     */
    public String getDomainAsToolTip() {
        return this.wrappedUserGroupReduced.getDomain().toLowerCase();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupMenuItemBean#isExternallyManaged()
     */
    public boolean isExternallyManaged() {
        return this.wrappedUserGroupReduced.getExternallyLinked();
    }
    
    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IUserGroupMenuItemBean#isOrphaned()
     */
    public boolean isOrphaned() {
        return this.wrappedUserGroupReduced.getOrphaned();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalUserGroupMenuItemBean#getWrappedUserGroupReduced()
     */
    public UserGroupReduced getWrappedUserGroupReduced() {
        return this.wrappedUserGroupReduced;
    }
}

