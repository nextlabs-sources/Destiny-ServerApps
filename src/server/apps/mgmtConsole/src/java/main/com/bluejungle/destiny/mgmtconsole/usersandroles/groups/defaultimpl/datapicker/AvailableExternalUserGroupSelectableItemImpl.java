package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import org.apache.axis2.databinding.utils.ConverterUtil;
import org.apache.commons.codec.binary.Base64;

import com.bluejungle.destiny.services.management.types.ExternalUserGroup;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.BaseDisableableSelectableItem;
import com.bluejungle.destiny.webui.framework.data.IMemorizeableDataItem;

/**
 * Selectable item implementation for external user groups which can be linked
 * to create user groups in the system
 * 
 * @author sgoldstein
 */
public class AvailableExternalUserGroupSelectableItemImpl extends BaseDisableableSelectableItem implements IMemorizeableDataItem {

    private final String id;
    protected ExternalUserGroup wrappedExternalUserGroup;

    /**
     * Create an instance of AvailableExternalGroupSelectableItemImpl
     * 
     * @param externalUserGroup
     */
    public AvailableExternalUserGroupSelectableItemImpl(ExternalUserGroup externalUserGroup) {
        if (externalUserGroup == null) {
            throw new NullPointerException("externalUserGroup cannot be null.");
        }

        this.wrappedExternalUserGroup = externalUserGroup;
        byte[] externalId = ConverterUtil.getStringFromDatahandler(this.wrappedExternalUserGroup.getExternalId()).getBytes();
        byte[] base64EncodedExternalId = Base64.encodeBase64(externalId);
        this.id = new String(base64EncodedExternalId);
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
        return this.wrappedExternalUserGroup.getTitle();
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
    protected ExternalUserGroup getWrappedExternalUserGroup() {
        return this.wrappedExternalUserGroup;
    }
}

