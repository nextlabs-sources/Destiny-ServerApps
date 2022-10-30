/*
 * Created on Nov 11, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.message;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of MessageManager. Simply stores messages in the
 * current user's session and retrieves them by request, removing them from the
 * session when retrieved. Ideally, we could stored the messages in the session
 * with some sort of request token, to support multiple browsers and to avoid
 * displaying stale messages. Unfortunately, there's no way at the moment to
 * achieve this easily. Need to research this more in the future
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/message/DefaultMessageManagerImpl.java#1 $
 */

public class DefaultMessageManagerImpl extends MessageManager {

    private static final String MESSAGES_SESSION_KEY = "com.bluejungle.faces.messages";

    DefaultMessageManagerImpl() {
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.message.MessageManager#addMessage(javax.faces.application.FacesMessage)
     */
    public void addMessage(FacesMessage facesMessage) {
        if (facesMessage == null) {
            throw new NullPointerException("facesMessage cannot be null.");
        }

        FacesContext currentContext = FacesContext.getCurrentInstance();
        List facesMessagesStore = null;

        /**
         * Although synchronization isn't absolutely necessary, we do it just in
         * case a single request is being handled by multiple threads
         */
        synchronized (this) {
            Map sessionAttributes = currentContext.getExternalContext().getSessionMap();
            facesMessagesStore = (List) sessionAttributes.get(MESSAGES_SESSION_KEY);
            if (facesMessagesStore == null) {
                facesMessagesStore = new LinkedList();
                sessionAttributes.put(MESSAGES_SESSION_KEY, facesMessagesStore);
            }
        }

        facesMessagesStore.add(facesMessage);
    }

    /**
     * @see com.bluejungle.destiny.webui.framework.message.MessageManager#getMessages()
     */
    public Iterator getMessages() {
        Iterator iteratorToReturn = null;

        /**
         * Assume that once here, messages have already been added. No
         * synchronization necessary
         */
        FacesContext currentContext = FacesContext.getCurrentInstance();
        Map sessionAttributes = currentContext.getExternalContext().getSessionMap();
        List facesMessagesStore = (List) sessionAttributes.get(MESSAGES_SESSION_KEY);
        if (facesMessagesStore == null) {
            iteratorToReturn = Collections.EMPTY_LIST.iterator();
        } else {
            iteratorToReturn = facesMessagesStore.iterator();
        }

        /**
         * Remove store from session
         */
        sessionAttributes.remove(MESSAGES_SESSION_KEY);

        return iteratorToReturn;
    }
}
