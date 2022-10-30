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
 * SelectItemActionListener is a Faces Action Listener which will update the
 * Browsable Data Picker bean when a user selects a selectable item in the
 * browsable data picker view
 * 
 * @author sgoldstein
 */
public class SelectItemActionListener extends ActionListenerBase {

    private static final String SELECTABLE_ITEM_ID = "selectableItemid";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) {
        String selectableItemId = getRequestParameter(SELECTABLE_ITEM_ID, null);
        if (selectableItemId == null) {
            throw new IllegalStateException("Action called without required parameter");
        }

        IBrowsableDataPickerBean browsbleDataPickerBean = (IBrowsableDataPickerBean) getManagedBeanByName(DefaultBrowsableDataPickerBean.BEAN_NAME);
        browsbleDataPickerBean.selectItem(selectableItemId);
    }
}

