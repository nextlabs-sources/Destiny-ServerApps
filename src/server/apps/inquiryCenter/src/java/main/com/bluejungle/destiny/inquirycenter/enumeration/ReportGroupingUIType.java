/*
 * Created on Apr 25, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.enumeration;

import com.bluejungle.destiny.webui.framework.faces.UIEnumBase;

/**
 * This class holds the various report grouping types available to the end user.
 * It should typically be loaded once in the application scope and can be reused
 * throughout the application wherever needed.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/enum/ReportGroupingType.java#1 $
 */

public class ReportGroupingUIType extends UIEnumBase {

    private static final String INQUIRY_CENTER_RESOURCE_BUNDLE_NAME = "InquiryCenterMessages";
    public static final ReportGroupingUIType UI_GROUPING_NONE = new ReportGroupingUIType("None", "my_reports_enum_grouping_none", 0);
    public static final ReportGroupingUIType UI_GROUPING_POLICY = new ReportGroupingUIType("Policy", "my_reports_enum_grouping_policy", 1);
    public static final ReportGroupingUIType UI_GROUPING_RESOURCE = new ReportGroupingUIType("Resource", "my_reports_enum_grouping_resource", 2);
    public static final ReportGroupingUIType UI_GROUPING_TIME = new ReportGroupingUIType("Time", "my_reports_enum_grouping_time", 3);
    public static final ReportGroupingUIType UI_GROUPING_USER = new ReportGroupingUIType("User", "my_reports_enum_grouping_user", 4);

    /**
     * Constructor
     * 
     * @param enunName
     * @param bundleKeyName
     * @param type
     */
    protected ReportGroupingUIType(String enunName, String bundleKeyName, int type) {
        super(enunName, bundleKeyName, type);
    }

    /**
     * Returns the localized display value
     * 
     * @param enumeration
     *            enum to localize
     * @return the localized display value
     */
    public static String getDisplayValue(UIEnumBase enumeration) {
        return getDisplayValue(enumeration, INQUIRY_CENTER_RESOURCE_BUNDLE_NAME);
    }

    /**
     * Retrieve an ReportGroupingUIType instance by name
     * 
     * @param type
     *            the type of the ReportGroupingUIType
     * @return the ReportGroupingUIType associated with the provided name
     * @throws IllegalArgumentException
     *             if no ReportGroupingUIType exists with the specified name
     */
    public static ReportGroupingUIType getReportGroupingUIType(int type) {
        return getElement(type, ReportGroupingUIType.class);
    }
}