/*
 * Created on Apr 13, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

/**
 * A SelectedTabChangeEventListener instance responds to the selection of a tab
 * within a {@link com.bluejungle.destiny.webui.controls.UITabbedPane}
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/SelectedTabChangeEventListener.java#1 $
 */
public interface SelectedTabChangeEventListener extends FacesListener {

    /**
     * <p>
     * Invoked when the selected tab within a UITabbedPane is changed as
     * described by the specified {@link SelectedTabChangeEvent}.
     * </p>
     * 
     * @param event
     *            A {@link SelectedTabChangeEvent} describing the selected tab
     *            change event that has occurred
     * 
     * @exception AbortProcessingException
     *                Signal the JavaServer Faces implementation that no further
     *                processing on the current event should be performed
     */
    void processSelectedTabChange(SelectedTabChangeEvent event) throws AbortProcessingException;
}
