/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker;

import javax.faces.event.ActionEvent;

import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.DefaultBrowsableDataPickerBean;
import com.bluejungle.destiny.webui.framework.faces.ActionListenerBase;

/**
 * SelectSearchBucketActionListener is a Faces Action Listener which will update
 * the selected search bucket in the browsable data picker bean when a user
 * select a new search bucket
 * 
 * @author sgoldstein
 */
public class SelectSearchBucketActionListener extends ActionListenerBase {

    private static final String SEARCH_BUCKET_INDEX_PARAM_NAME = "searchBucketIndex";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) {
        String searchBucketIndexString = getRequestParameter(SEARCH_BUCKET_INDEX_PARAM_NAME, null);
        if (searchBucketIndexString == null) {
            throw new IllegalStateException("Action called without required parameter");
        }

        int searchBucketIndex = Integer.valueOf(searchBucketIndexString).intValue();

        IBrowsableDataPickerBean browsbleDataPickerBean = (IBrowsableDataPickerBean) getManagedBeanByName(DefaultBrowsableDataPickerBean.BEAN_NAME);
        browsbleDataPickerBean.setSelectedSearchBucket(searchBucketIndex);
    }
}

