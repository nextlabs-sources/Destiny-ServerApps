/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/ReportComponentLookupException.java#1 $
 */

public class ReportComponentLookupException extends Exception {

    /**
     * Constructor
     * 
     */
    public ReportComponentLookupException() {
        super();
    }

    /**
     * Constructor
     * @param arg0
     */
    public ReportComponentLookupException(String arg0) {
        super(arg0);
    }

    /**
     * Constructor
     * @param arg0
     */
    public ReportComponentLookupException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Constructor
     * @param arg0
     * @param arg1
     */
    public ReportComponentLookupException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
