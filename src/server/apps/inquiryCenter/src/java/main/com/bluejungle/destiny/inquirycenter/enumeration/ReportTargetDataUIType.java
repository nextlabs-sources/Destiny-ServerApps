/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.enumeration;

import com.bluejungle.destiny.webui.framework.faces.UIEnumBase;

/**
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/enum/ReportTargetDataUIType.java#1 $
 */

public class ReportTargetDataUIType extends UIEnumBase {

    private static final String INQUIRY_CENTER_BUNDLE_MESSAGES = "InquiryCenterMessages";
    public static final ReportTargetDataUIType POLICY_ACTIVITY = new ReportTargetDataUIType("PolicyEvents", "my_reports_enum_target_data_policy", 0);
    public static final ReportTargetDataUIType TRACKING_ACTIVITY = new ReportTargetDataUIType("ActivityJournal", "my_reports_enum_target_data_activity", 1);

    /**
     * Constructor
     * 
     * @param enumName
     * @param bundleKeyName
     * @param type
     */
    protected ReportTargetDataUIType(String enumName, String bundleKeyName, int type) {
        super(enumName, bundleKeyName, type);
    }

    /**
     * Returns the localized display value
     * 
     * @param enumeration
     *            enum to localize
     * @return the localized display value
     */
    public static String getDisplayValue(UIEnumBase enumeration) {
        return getDisplayValue(enumeration, INQUIRY_CENTER_BUNDLE_MESSAGES);
    }

    /**
     * Retrieve an ReportTargetDataUIType instance by name
     * 
     * @param name
     *            the name of the ReportTargetDataUIType
     * @return the ReportTargetDataUIType associated with the provided name
     * @throws IllegalArgumentException
     *             if no ReportTargetDataUIType exists with the specified name
     */
    public static ReportTargetDataUIType getReportTargetDataUIType(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }
        return getElement(name, ReportTargetDataUIType.class);
    }
}