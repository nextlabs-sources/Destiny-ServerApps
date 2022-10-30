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
 * RemoveItemActionListener is a Faces ActionListener which will update the
 * Browsable Data Picker bean when a user removes a selected item from the
 * selected item list
 * 
 * @author sgoldstein
 */
public class RemoveItemActionListener extends ActionListenerBase {

    private static final String SELECTED_ITEM_ID = "selectedItemId";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) {
        String selectedItemId = getRequestParameter(SELECTED_ITEM_ID, null);
        if (selectedItemId == null) {
            throw new IllegalStateException("Action called without required parameter");
        }

        IBrowsableDataPickerBean browsbleDataPickerBean = (IBrowsableDataPickerBean) getManagedBeanByName(DefaultBrowsableDataPickerBean.BEAN_NAME);
        browsbleDataPickerBean.deselectItem(selectedItemId);
    }
}

