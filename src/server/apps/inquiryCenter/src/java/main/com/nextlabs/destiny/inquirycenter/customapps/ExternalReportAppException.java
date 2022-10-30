/*
 * Created on Mar 17, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.customapps;
/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/customapps/ExternalReportAppException.java#1 $
 */

public class ExternalReportAppException extends Exception {

    public ExternalReportAppException(String message) {
        super(message);
    }
    
    public ExternalReportAppException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalReportAppException(Throwable cause) {
        super(cause);
    }

    public static ExternalReportAppException notFound(long id){
        return new ExternalReportAppException("The custom app with id, " + id + ", is not found");
    }
    
    public static ExternalReportAppException notActive(long id){
        return new ExternalReportAppException("The custom app with id, " + id + ", is no longer active.");
    }
}
