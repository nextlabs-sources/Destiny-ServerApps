/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.user;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.sharepoint.SharePointEnroller;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ISelectableReportComponentItem;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentSelectedItem;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.UserComponentEntityResolver;
import com.bluejungle.destiny.types.users.v1.UserClass;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemPossibleStyleClassIds;

/**
 * Represents a selectable user-class item that can also be disabled in the UI.
 * 
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/user/SelectableUserClassItem.java#2 $
 */

public class SelectableUserClassItem implements ISelectableReportComponentItem {

    private UserClass userClass;
    private String styleClassId;
    private boolean isSelectable;

    /**
     * Constructor
     * 
     * @param userClass
     *            user class to use
     */
    public SelectableUserClassItem(UserClass userClass) {
        super();
        if (userClass == null) {
            throw new NullPointerException("userClass cannot be null");
        }
        this.userClass = userClass;
        if (userClass.getEnrollmentType().equals(SharePointEnroller.class.getName())){
            this.styleClassId = ISelectableItemPossibleStyleClassIds.BOLD_ITALICIZED_STYLE_CLASS_ID;
        } else {
            this.styleClassId = ISelectableItemPossibleStyleClassIds.BOLD_STYLE_CLASS_ID;
        }
        this.isSelectable = true;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.userClass.getDisplayName();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValueToolTip()
     */
    public String getDisplayValueToolTip() {
        return this.userClass.getName();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return UserComponentEntityResolver.createUserClassQualification(this.userClass.getName());
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getStyleClassId()
     */
    public String getStyleClassId() {
        return this.styleClassId;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#isSelectable()
     */
    public boolean isSelectable() {
        return this.isSelectable;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem#disable()
     */
    public void disable() {
        this.isSelectable = false;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ISelectableUserClassItem#createSelected()
     */
    public ISelectedItem createSelected() {
        return new ReportComponentSelectedItem(getId(), getDisplayValue());
    }
}
