package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;

/**
 * Selected Item implementation for a user group principal
 * 
 * @author sgoldstein
 */
public class UserGroupPrincipalSelectedItemImpl implements ISelectedItem {

    private static final String USER_GROUP_ID_PREFIX = "USER_GROUP_";

    private final String id;

    private UserGroupReduced wrappedUserGroup;

    /**
     * Create an instance of ExistingGroupPrincipalSelectedItemImpl
     * 
     * @param userGroup
     */
    public UserGroupPrincipalSelectedItemImpl(UserGroupReduced userGroup) {
        if (userGroup == null) {
            throw new NullPointerException("wrappedUserGroup cannot be null.");
        }
        
        this.wrappedUserGroup = userGroup;

        this.id = USER_GROUP_ID_PREFIX + getWrappedUserGroup().getId();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return this.id;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.wrappedUserGroup.getTitle();
    }

    /**
     * @return
     */
    public UserGroupReduced getWrappedUserGroup() {
        return this.wrappedUserGroup;
    }
}

