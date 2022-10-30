/*
 * Created on Apr 27, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

/**
 * This event is fired when a new selected item has been chosen in a list
 * control. This event takes a reference to the new selected object and can pass
 * the new selected object to the listeners.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/NewSelectedItemEvent.java#1 $
 */

public class NewSelectedItemEvent extends ActionEvent {

    private Object selectedItem;

    /**
     * Constructor
     * 
     * @param sourceComp
     *            component that fired the event
     */
    public NewSelectedItemEvent(UIComponent sourceComp) {
        super(sourceComp);
    }

    /**
     * Constructor
     * 
     * @param sourceComp
     *            component that fired the even
     * @param selectedItem
     *            new selected item
     */
    public NewSelectedItemEvent(UIComponent sourceComp, Object selectedItem) {
        super(sourceComp);
        this.selectedItem = selectedItem;
    }

    /**
     * Returns the selected item
     * 
     * @return the selected item
     */
    public Object getSelectedItem() {
        return this.selectedItem;
    }
}