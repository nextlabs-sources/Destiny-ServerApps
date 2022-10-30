package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.datapicker;

import com.bluejungle.destiny.services.management.types.ExternalUserGroup;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import org.apache.axis2.databinding.utils.ConverterUtil;

/**
 * Selected item implementation for external user groups which can be linked to
 * create user groups in the system
 * 
 * @author sgoldstein
 */
public class AvailableExternalUserGroupSelectedItemImpl implements ISelectedItem {

    private final String id;
    private ExternalUserGroup wrappedExternalUserGroup;

    /**
     * Create an instance of AvailableExternalGroupSelectedItemImpl
     * 
     * @param wrappedExternalUserGroup
     */
    public AvailableExternalUserGroupSelectedItemImpl(ExternalUserGroup externalUserGroup) {
        if (externalUserGroup == null) {
            throw new NullPointerException("wrappedUserGroup cannot be null.");
        }

        this.wrappedExternalUserGroup = externalUserGroup;
        this.id = UserGroupDataPickerUtils.getBase64EncodingOf(ConverterUtil.getStringFromDatahandler(this.wrappedExternalUserGroup.getExternalId()).getBytes());
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getId()
     */
    public String getId() {
        return this.id;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.wrappedExternalUserGroup.getTitle();
    }

    /**
     * @return
     */
    public ExternalUserGroup getWrappedExternalUserGroup() {
        return this.wrappedExternalUserGroup;
    }
}

