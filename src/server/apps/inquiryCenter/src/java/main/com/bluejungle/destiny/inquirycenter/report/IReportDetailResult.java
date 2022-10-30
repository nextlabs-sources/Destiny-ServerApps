/*
 * Created on May 3, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import java.util.Date;

/**
 * This interface is implemented by all detail result records of a report
 * execution.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/IReportDetailResult.java#2 $
 */

public interface IReportDetailResult {

    /**
     * Returns the id 
     * 
     * @return the id
     */
    public long getId();
    
    /**
     * Returns the action name
     * 
     * @return the action name
     */
    public String getAction();

    /**
     * Returns the application name
     * 
     * @return the application name
     */
    public String getApplicationName();

    /**
     * Returns the time of the detail result record
     * 
     * @return the time of the detail result record
     */
    public Date getDate();

    /**
     * Returns the enforcement name
     * 
     * @return the enforcement name
     */
    public String getEnforcement();

    /**
     * Returns the "from" resource file name
     * 
     * @return the "from" resource name
     */
    public String getFromResourceFilename();

    /**
     * Returns the "from" resource file name
     * 
     * @return the "from" resource path name
     */
    public String getFromResourcePath();

    /**
     * Returns the "from" resource full path with file name
     * 
     * @return the "from" resource full path with file name
     */
    public String getFromResource();

    /**
     * Returns the host name
     * 
     * @return the host name
     */
    public String getHost();

    /**
     * Returns the host IP address
     * 
     * @return the host IP address
     */
    public String getHostIPAddress();

    /**
     * Returns the policy name
     * 
     * @return the policy name
     */
    public String getPolicyName();

    /**
     * Returns the policy folder name
     * 
     * @return the policy folder name
     */
    public String getPolicyFolderName();

    /**
     * Returns the policy full name
     * 
     * @return the policy full name
     */
    public String getPolicyFullName();

    /**
     * Returns the "to" resource file name
     * 
     * @return the "to" resource name
     */
    public String getToResourceFilename();

    /**
     * Returns the "to" resource file name
     * 
     * @return the "to" resource path name
     */
    public String getToResourcePath();

    /**
     * Returns the "from" resource full path with file name
     * 
     * @return the "from" resource full path with file name
     */
    public String getToResource();

    /**
     * Returns the user name
     * 
     * @return the user name
     */
    public String getUser();
    
    /**
     * Returns the logging level
     * 
     * @return the logging level
     */
    public int getLoggingLevel();
}