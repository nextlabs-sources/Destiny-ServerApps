/*
 * Created on Mar 25, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report;

/**
 * This interface handles transform the report query in IReport (or ReportImpl)
 * into something understandable for the reporting tool we're using 
 * underneath.  When this class is created, the reporting tool we're using is
 * BIRT, so there's an implementing class specifically for BIRT.  For the
 * future, the implementing class can potentially read transform definitions
 * from an XML file instead of having the transform logic in code.
 * 
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/IReportTransform.java#1 $
 */
public interface IReportTransform {

    /**
     * @return users string
     */
    public String getUsers();
    
    /**
     * @return actions string
     */
    public String getActions();
    
    /**
     * @return resources string
     */
    public String getResources();
    
    /**
     * @return hosts string
     */
    public String getHost();
    
    /**
     * @return policies string
     */
    public String getPolicies();
    
    /**
     * @return enforcement string
     */
    public String getEnforcements();
    
    /**
     * @return event level string
     */
    public String getEventLevel();
    
    /**
     * @return targeted data
     */
    public String getTargetData();
    
    /**
     * @return group by dimension
     */
    public String getGroupBy();
}
