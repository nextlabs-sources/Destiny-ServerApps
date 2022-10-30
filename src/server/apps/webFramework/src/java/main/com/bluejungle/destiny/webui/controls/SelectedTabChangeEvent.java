/*
 * Created on Apr 13, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/SelectedTabChangeEvent.java#1 $
 */

public class SelectedTabChangeEvent extends FacesEvent {

    /**
     * Create an instance of SelectedTabChangeEvent
     * 
     * @param component
     */
    public SelectedTabChangeEvent(UITabbedPane sourceTabbedPane) {
        super(sourceTabbedPane);
    }

    /**
     * @see javax.faces.event.FacesEvent#isAppropriateListener(javax.faces.event.FacesListener)
     */
    public boolean isAppropriateListener(FacesListener listener) {
        return (listener instanceof SelectedTabChangeEventListener);
    }

    /**
     * @see javax.faces.event.FacesEvent#processListener(javax.faces.event.FacesListener)
     */
    public void processListener(FacesListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener cannot be null.");
        }

        ((SelectedTabChangeEventListener) listener).processSelectedTabChange(this);
    }

    /**
     * Retrieve the currently selected tab
     * 
     * @return the currently selected tab
     */
    public UITab getSelectedTab() {
        return ((UITabbedPane) this.getSource()).getSelectedTab();
    }
}
