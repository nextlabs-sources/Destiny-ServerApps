/*
 * Created on May 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.policy;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.BaseReportSelectableItem;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ReportComponentSelectedItem;
import com.bluejungle.destiny.types.policies.v1.Policy;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemPossibleStyleClassIds;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/policy/SelectablePolicyItem.java#1 $
 */

public class SelectablePolicyItem extends BaseReportSelectableItem {

    /**
     * Policy folder separator
     */
    private static final String POLICY_FOLDER_SEPARATOR = "/";
    private Policy policy;

    /**
     * Constructor
     * 
     * @param policy
     *            policy object
     */
    public SelectablePolicyItem(Policy policy) {
        super();
        if (policy == null) {
            throw new NullPointerException("policy cannot be null");
        }
        this.policy = policy;
        setStyleClassId(ISelectableItemPossibleStyleClassIds.DEFAULT);
        enable();
    }

    /**
     * Returns the complete policy display value
     * 
     * @return the complete policy display value
     */
    protected String getFullPolicyDisplayValue() {
        String displayValue = this.policy.getFolderName();
        displayValue += POLICY_FOLDER_SEPARATOR.equals(displayValue) ? this.policy.getName() : POLICY_FOLDER_SEPARATOR + this.policy.getName();
        return displayValue;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.policy.getName();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getDisplayValueToolTip()
     */
    public String getDisplayValueToolTip() {
        return getFullPolicyDisplayValue();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectableItem#getId()
     */
    public String getId() {
        return getFullPolicyDisplayValue();
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.ISelectableReportComponentItem#createSelected()
     */
    public ISelectedItem createSelected() {
        return new ReportComponentSelectedItem(getId(), getFullPolicyDisplayValue());
    }
}