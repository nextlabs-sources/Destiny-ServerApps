/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.user;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.faces.model.DataModel;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.UserComponentEntityResolver;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.DisablingItemDataModel;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/user/UserClassDisablingDataModel.java#1 $
 */

public class UserClassDisablingDataModel extends DisablingItemDataModel {

    private Set itemsToDisable;

    /**
     * Constructor
     * 
     * @param wrappedDataModel
     */
    public UserClassDisablingDataModel(DataModel wrappedDataModel, ISelectedItemList selectedItems, String[] existingSelections) {
        super(wrappedDataModel);
        this.itemsToDisable = new HashSet();

        // Add the existing selections + the selected values into a lookup set:
        if (existingSelections != null) {
            for (int i = 0; i < existingSelections.length; i++) {
                this.itemsToDisable.add(UserComponentEntityResolver.createUserClassQualification(existingSelections[i]));
            }
        }
        if (selectedItems != null) {
            Iterator iter = selectedItems.iterator();
            while (iter.hasNext()) {
                ISelectedItem selectedItem = (ISelectedItem) iter.next();
                this.itemsToDisable.add(selectedItem.getId());
            }
        }
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.DisablingItemDataModel#shouldItemBeDisabled(com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.helpers.IDisableableSelectableItem)
     */
    protected boolean shouldItemBeDisabled(IDisableableSelectableItem disableableItem) {
        if (this.itemsToDisable.contains(disableableItem.getId())) {
            return true;
        } else {
            return false;
        }
    }
}