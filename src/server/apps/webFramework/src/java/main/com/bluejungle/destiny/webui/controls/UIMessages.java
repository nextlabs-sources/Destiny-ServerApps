/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import java.util.Iterator;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 * The UIMessages component is responsible for displaying FacesMessages added to
 * the FacesContext. This component differs from the standard component in that
 * it can display both messages for a particular client and all messages within
 * the faces context. In other words, it's a combination of the standad
 * UIMessage and UIMessages components
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UIMessages.java#1 $
 */

public class UIMessages extends UIComponentBase {

    private String forClientId;

    /**
     * Create an instance of UIMessages component
     *  
     */
    public UIMessages() {
        super();
    }

    /**
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return "com.bluejungle.destiny.Messages";
    }

    /**
     * Retrieve the messages to render for this component. If the "forClientId"
     * property has been set, the messages associated with the specified client
     * id will be returned. Otherwise, all messages with be returned.
     * 
     * @return the messages to render for this component.
     */
    public Iterator getMessagesToRender(FacesContext context) {
        Iterator messagesToReturn = null;

        if (forClientId != null) {
            messagesToReturn = context.getMessages(forClientId);
        } else {
            messagesToReturn = context.getMessages();
        }

        return messagesToReturn;
    }

    /**
     * Retrieve the forClientId.
     * 
     * @return the forClientId.
     */
    public String getForClientId() {
        return this.forClientId;
    }

    /**
     * Set the forClientId
     * 
     * @param forClientId
     *            The forClientId to set.
     */
    public void setForClientId(String forClientId) {
        this.forClientId = forClientId;
    }
}