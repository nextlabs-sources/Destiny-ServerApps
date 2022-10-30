package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import com.bluejungle.destiny.services.management.types.UserDTO;

/**
 * Selectable Item implementation for user principals
 * 
 * @author sgoldstein
 */
public class UserPrincipalSelectedItemImpl extends BaseUserSelectedItemImpl {

    private static final String USER_ID_PREFIX = "USER_";

    private String id;

    /**
     * Create an instance of ExistingUserPrincipalSelectedItemImpl
     * 
     * @param wrappedUser
     */
    public UserPrincipalSelectedItemImpl(UserDTO wrappedUser) {
        super(wrappedUser);
        this.id = USER_ID_PREFIX + super.getId();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return this.id;
    }
}

