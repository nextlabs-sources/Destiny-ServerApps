/*
 * Created on Mar 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.enumeration;

import com.bluejungle.framework.patterns.EnumBase;

/**
 * This class is the enumeration for the report grouping types available.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/enumeration/ReportGroupByType.java#1 $
 */

public class ReportGroupByType extends EnumBase {

    public static final ReportGroupByType NONE = new ReportGroupByType("None");
    public static final ReportGroupByType POLICY = new ReportGroupByType("Policy");
    public static final ReportGroupByType RESOURCE = new ReportGroupByType("Resource");
    public static final ReportGroupByType TIME = new ReportGroupByType("Time");
    public static final ReportGroupByType USER = new ReportGroupByType("User");    
    
    /**
     * The constructor is private to prevent unwanted instanciations from the
     * outside.
     * 
     * @param name
     *            is passed through to the constructor of the superclass.
     */
    private ReportGroupByType(String name) {
        super(name);
    }

    /**
     * Retrieve an ReportGroupByType instance by name
     * 
     * @param name
     *            the name of the ReportGroupByType
     * @return the ReportGroupByType associated with the provided name
     * @throws IllegalArgumentException
     *             if no ReportGroupByType exists with the specified name
     */
    public static ReportGroupByType getReportGroupByEnum(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }
        return getElement(name, ReportGroupByType.class);
    }
}