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
 * CancelFreeFormSearchActionListener is a Faces ActionListener which will update the
 * Browsable Data Picker bean when a user cancels a free form search
 * 
 * @author sgoldstein
 */
public class CancelFreeFormSearchActionListener extends ActionListenerBase {

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) {
        IBrowsableDataPickerBean browsbleDataPickerBean = (IBrowsableDataPickerBean) getManagedBeanByName(DefaultBrowsableDataPickerBean.BEAN_NAME);
        browsbleDataPickerBean.cancelFreeFormSearch();
    }
}

