package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import com.bluejungle.destiny.services.management.types.UserGroupReduced;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemPossibleStyleClassIds;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.BaseDisableableSelectableItem;
import com.bluejungle.destiny.webui.framework.data.IMemorizeableDataItem;

/**
 * Selectable Item implementation for a user group principal
 * 
 * @author sgoldstein
 */
public class UserGroupPrincipalSelectableItemImpl extends BaseDisableableSelectableItem implements IMemorizeableDataItem {

    private static final String USER_GROUP_ID_PREFIX = "USER_GROUP_";

    private final String id;

    protected UserGroupReduced wrappedUserGroupReduced;

    /**
     * Create an instance of ExistingGroupPrincipalSelectableItemImpl
     * 
     * @param userGroup
     */
    public UserGroupPrincipalSelectableItemImpl(UserGroupReduced userGroup) {
        super(ISelectableItemPossibleStyleClassIds.BOLD_STYLE_CLASS_ID);
        
        if (userGroup == null) {
            throw new NullPointerException("reduced cannot be null.");
        }
        
        this.wrappedUserGroupReduced = userGroup;
        this.id = USER_GROUP_ID_PREFIX + getWrappedUserGroup().getId();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return this.id;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.wrappedUserGroupReduced.getTitle();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValueToolTip()
     */
    public String getDisplayValueToolTip() {
        return getDisplayValue();
    }

    /**
     * @return
     */
    protected UserGroupReduced getWrappedUserGroup() {
        return this.wrappedUserGroupReduced;
    }
}

