/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

/**
 * This interface exposes various statistics about the summary results that have
 * been returned during the report execution. It extends the basic result
 * statistics interface
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/ISummaryResultsStatistics.java#1 $
 */

public interface ISummaryResultsStatistics extends IResultsStatistics {

    /**
     * Returns the maximum occurence count of a particular value
     * 
     * @return the maximum occurence count of a particular value
     */
    public long getMaxCount();

    /**
     * Returns the minimum occurence count of a particular value
     * 
     * @return the minimum occurence count of a particular value
     */
    public long getMinCount();

    /**
     * Returns the total number of occurences accross all values
     * 
     * @return the total number of occurences accross all values
     */
    public long getTotalSummaryCount();
}