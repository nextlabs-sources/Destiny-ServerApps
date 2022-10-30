/*
 * Created on May 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers;

import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/ReportComponentSelectedItem.java#1 $
 */

public class ReportComponentSelectedItem implements ISelectedItem {

    private String id;
    private String display;

    /**
     * Constructor
     * 
     * @param id
     * @param display
     */
    public ReportComponentSelectedItem(String id, String display) {
        this.id = id;
        this.display = display;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.display;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem#getId()
     */
    public String getId() {
        return this.id;
    }
}