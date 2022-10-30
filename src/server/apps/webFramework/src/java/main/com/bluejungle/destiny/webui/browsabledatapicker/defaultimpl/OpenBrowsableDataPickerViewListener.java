/*
 * Created on May 10, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean;
import com.bluejungle.destiny.webui.framework.faces.ActionListenerBase;

/**
 * OpenBrowsableDataPickerViewListener is a listener specific to the
 * {@see DefaultBrowsableDataPickerBean}implementation of the
 * {@see com.bluejungle.destiny.webui.browsabledatapicker.IBrowsableDataPickerBean}
 * interface. Its purpose is to set the current ISelectableItemSource bean
 * instance based on a parameter in the request with the name
 * {@see SELECTABLE_ITEM_SOURCE_BEAN_NAME_PARAM}
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/OpenBrowsableDataPickerViewListener.java#1 $
 */
public class OpenBrowsableDataPickerViewListener extends ActionListenerBase {

    private static final String SELECTABLE_ITEM_SOURCE_BEAN_NAME_PARAM = "selectableItemSourceBeanName";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException {
        IBrowsableDataPickerBean browsableDataPickerBean = (IBrowsableDataPickerBean) getManagedBeanByName(IBrowsableDataPickerBean.BEAN_NAME);
        if (!(browsableDataPickerBean instanceof DefaultBrowsableDataPickerBean)) {
            throw new IllegalStateException("Unknown IBrowsableDataPickerBean implementation, " + browsableDataPickerBean.getClass().getName());
        }

        String selectableItemSourceBeanName = getRequestParameter(SELECTABLE_ITEM_SOURCE_BEAN_NAME_PARAM, null);
        if (selectableItemSourceBeanName == null) {
            throw new IllegalStateException("Missing required request parameter, " + SELECTABLE_ITEM_SOURCE_BEAN_NAME_PARAM);
        }
        
        ISelectableItemSource selectableItemSourceBean = (ISelectableItemSource) getManagedBeanByName(selectableItemSourceBeanName);
        if (selectableItemSourceBean == null) {
            StringBuffer errorMessage = new StringBuffer("Selectable Item Source Bean with name, ");
            errorMessage.append(selectableItemSourceBeanName);
            errorMessage.append(", not found.");
            
            throw new IllegalStateException(errorMessage.toString());
        }
        
        // Reset each time the picker is opened
        selectableItemSourceBean.reset();
        
        ((DefaultBrowsableDataPickerBean)browsableDataPickerBean).setSelectableItemSource(selectableItemSourceBean);
    }
}