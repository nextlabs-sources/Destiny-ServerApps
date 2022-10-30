/*
 * Created on Jul 12, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.bluejungle.destiny.inquirycenter.enumeration;

import com.bluejungle.destiny.webui.framework.faces.UIEnumBase;


/**
 * @author rlin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReportLoggingLevelUIType extends UIEnumBase {

    private static final String INQUIRY_CENTER_RESOURCE_BUNDLE_NAME = "InquiryCenterMessages";
    public static final ReportLoggingLevelUIType LOGGING_LEVEL_ALL = new ReportLoggingLevelUIType("All", "my_reports_enum_logging_level_all", 1);
    public static final ReportLoggingLevelUIType LOGGING_LEVEL_APPLICATION = new ReportLoggingLevelUIType("Application", "my_reports_enum_logging_level_application", 2);
    public static final ReportLoggingLevelUIType LOGGING_LEVEL_USER = new ReportLoggingLevelUIType("User", "my_reports_enum_logging_level_user", 3);
    public static final ReportLoggingLevelUIType LOGGING_LEVEL_ONE = new ReportLoggingLevelUIType("One", "my_reports_enum_logging_level_one", 4);
    public static final ReportLoggingLevelUIType LOGGING_LEVEL_TWO = new ReportLoggingLevelUIType("Two", "my_reports_enum_logging_level_two", 5);
    public static final ReportLoggingLevelUIType LOGGING_LEVEL_THREE = new ReportLoggingLevelUIType("Three", "my_reports_enum_logging_level_three", 6);

    
    /**
     * @param enunName
     * @param bundleKeyName
     * @param type
     */
    public ReportLoggingLevelUIType(String enunName, String bundleKeyName, int type) {
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
     * Retrieve an ReportLoggingLevelUIType instance by name
     * 
     * @param type
     *            the type of the ReportLoggingLevelUIType
     * @return the ReportLoggingLevelUIType associated with the provided name
     * @throws IllegalArgumentException
     *             if no ReportLoggingLevelUIType exists with the specified name
     */
    public static ReportLoggingLevelUIType getReportLoggingLevelUIType(int type) {
        return getElement(type, ReportLoggingLevelUIType.class);
    }
}
