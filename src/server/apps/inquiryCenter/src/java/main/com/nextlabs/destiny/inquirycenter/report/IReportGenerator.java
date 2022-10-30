/*
 * Created on Apr 17, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report;


/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/IReportGenerator.java#1 $
 */

public interface IReportGenerator {

    
    /**
     * <code>COMP_NAME</code> is the name of the ILogQueueMgr component. This
     * name is used to retrieve an instance of a ILogQueueMgr from the
     * ComponentManager
     */
    public static final String COMP_NAME = "reportGenerator";
    
    public void generateReport();
}
