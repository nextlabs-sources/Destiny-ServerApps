package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import com.bluejungle.destiny.services.management.types.UserDTO;

/**
 * Selected item implementation for potential members for users groups
 * 
 * @author sgoldstein
 */
public class PotentialMemberSelectedItemImpl extends BaseUserSelectedItemImpl {

    /**
     * Create an instance of PotentialMemberSelectedItemImpl
     * 
     * @param wrappedUser
     */
    public PotentialMemberSelectedItemImpl(UserDTO wrappedUser) {
        super(wrappedUser);
    }
}

