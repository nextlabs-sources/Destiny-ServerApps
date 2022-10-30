/*
 * Created on May 18, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers;

import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/ISelectableReportComponentItem.java#1 $
 */

public interface ISelectableReportComponentItem extends IDisableableSelectableItem {

    /**
     * Creates a selected item from the current object and returns this new
     * object
     * 
     * @return a new selected item object
     */
    public ISelectedItem createSelected();
}