package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import com.bluejungle.destiny.services.management.types.UserDTO;

/**
 * Selectable item implementation for potential members for users groups
 * 
 * @author sgoldstein
 */
public class PotentialMemberSelectableItemImpl extends BaseUserSelectableItemImpl {
    /**
     * Create an instance of PotentialMemberSelectableItemImpl
     * @param userDTO
     */
    public PotentialMemberSelectableItemImpl(UserDTO userDTO) {
        super(userDTO);        
    }
}
 
