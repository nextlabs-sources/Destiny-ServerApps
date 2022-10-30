/*
 * Created on Nov 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.message;

import java.util.Iterator;

import javax.faces.application.FacesMessage;

/**
 * Messager Manager which can be utilized to add end user message to the
 * response. It builds upon the FacesMessage framework by allowing messages to
 * be maintained accross redirects
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/message/MessageManager.java#1 $
 */

public abstract class MessageManager {

    private static final MessageManager messageManager = new DefaultMessageManagerImpl();

    public static MessageManager getInstance() {
        return messageManager;
    }

    /**
     * Add an end user facing message to the current request context
     * 
     * @param facesMessage
     */
    public abstract void addMessage(FacesMessage facesMessage);

    /**
     * Retrieve all end user facing messages associated with the current request
     * context
     * 
     * @return the end user facing messages associated with the current request
     *         context (FacesMessage instances)
     */
    public abstract Iterator getMessages();
}
