/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.user;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.BaseReportSelectableItem;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentSelectedItem;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.UserComponentEntityResolver;
import com.bluejungle.destiny.types.users.v1.User;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemPossibleStyleClassIds;

/**
 * Represents a selectable user item that can also be disabled in the UI.
 * 
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/SelectableUserItem.java#1 $
 */

public class SelectableUserItem extends BaseReportSelectableItem {

    private User user;
    private String displayValue;

    /**
     * Constructor
     * 
     * @param user
     *            user
     */
    public SelectableUserItem(User user) {
        super();
        if (user == null) {
            throw new NullPointerException("user cannot be null");
        }
        this.user = user;
        displayValue = buildDisplayValue(this.user);
        setStyleClassId(ISelectableItemPossibleStyleClassIds.DEFAULT);
        enable();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValueToolTip()
     */
    public String getDisplayValueToolTip() {
        return this.user.getDisplayName();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return UserComponentEntityResolver.createUserQualification(user.getDisplayName());
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ISelectableReportComponentItem#createSelected()
     */
    public ISelectedItem createSelected() {
        return new ReportComponentSelectedItem(getId(), getDisplayValue());
    }
    
    /**
     * Build the display value for this selectable item
     * @param user the user associated with this selectable item
     */
    private String buildDisplayValue(User user) {
        StringBuffer displayValueBuffer = new StringBuffer(user.getLastName());
        displayValueBuffer.append(", ");
        displayValueBuffer.append(user.getFirstName());
        displayValueBuffer.append(" (");
        displayValueBuffer.append(user.getDisplayName());
        displayValueBuffer.append(")");
        
        return displayValueBuffer.toString();
    }
}
