/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.report;

import com.nextlabs.report.datagen.ResultData;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
public class ReportResultData extends ResultData {

    private boolean hasMore;
    
    /**
     * @param name
     * @param values
     */
    public ReportResultData(String[] name, Object[][] values) {
        super(name, values);
    }

    public ReportResultData(String[] name, Object[][] values, boolean hasMore) {
        super(name, values);
        this.hasMore = hasMore;
    }

    /**
     * @return the hasMore
     */
    public boolean isHasMore() {
        return hasMore;
    }

    /**
     * @param hasMore the hasMore to set
     */
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

}
