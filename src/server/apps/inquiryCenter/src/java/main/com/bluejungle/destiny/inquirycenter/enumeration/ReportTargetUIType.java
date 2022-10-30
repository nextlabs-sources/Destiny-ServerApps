/*
 * Created on May 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.enumeration;

import com.bluejungle.destiny.webui.framework.faces.UIEnumBase;
import com.bluejungle.framework.patterns.EnumBase;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/enumeration/ReportTargetUIType.java#1 $
 */

public class ReportTargetUIType extends UIEnumBase {

    private static final String INQUIRY_CENTER_RESOURCE_BUNDLE_NAME = "InquiryCenterMessages";

    public static final ReportTargetUIType POLICY_EVENTS = new ReportTargetUIType("PolicyEvents", "my_reports_enum_target_data_policy", 1);
    public static final ReportTargetUIType ACTIVITY_JOURNAL = new ReportTargetUIType("ActivityJournal", "my_reports_enum_target_data_activity", 2);

    /**
     * Constructor
     * 
     * @param enunName
     * @param bundleKeyName
     * @param type
     */
    public ReportTargetUIType(String enunName, String bundleKeyName, int type) {
        super(enunName, bundleKeyName, type);
    }

    /**
     * Retrieves the report target enum by name
     * 
     * @param name
     * @return
     */
    public static ReportTargetUIType getByName(String name) {
        return EnumBase.getElement(name, ReportTargetUIType.class);
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
}