package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import com.bluejungle.destiny.services.management.types.UserDTO;

/**
 * Selectable Item Implementation for a User Principal
 * 
 * @author sgoldstein
 */
public class UserPrincipalSelectableItemImpl extends BaseUserSelectableItemImpl {

    private static final String USER_ID_PREFIX = "USER_";

    private String id;

    /**
     * Create an instance of ExistingUserPrincipalSelectedItemImpl
     * 
     * @param wrappedUser
     */
    public UserPrincipalSelectableItemImpl(UserDTO wrappedUser) {
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

