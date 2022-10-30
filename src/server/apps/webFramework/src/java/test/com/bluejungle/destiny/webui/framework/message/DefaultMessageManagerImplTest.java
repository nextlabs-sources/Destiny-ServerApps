/*
 * Created on Nov 12, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.message;

import com.bluejungle.destiny.webui.framework.message.DefaultMessageManagerImpl;
import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.destiny.webui.renderers.MockResponseWriter;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import java.util.Iterator;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/framework/message/DefaultMessageManagerImplTest.java#1 $
 */

public class DefaultMessageManagerImplTest extends TestCase {

    private DefaultMessageManagerImpl messageManagerToTest = new DefaultMessageManagerImpl();

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
    }

    /**
     * Tests addMessage and getMessages() methods
     *
     */
    public void testAddGetMessages() {
        MockExternalContext mockExternalContext = new MockExternalContext("/foo");
        FacesContext facesContext = new MockFacesContext(mockExternalContext);
        facesContext.setResponseWriter(new MockResponseWriter());
        
        FacesMessage messageOne = new FacesMessage("Message One!");
        this.messageManagerToTest.addMessage(messageOne);

        FacesMessage messageTwo = new FacesMessage("Message Two!");
        this.messageManagerToTest.addMessage(messageTwo);

        FacesMessage messageThree = new FacesMessage("Message Three!");
        this.messageManagerToTest.addMessage(messageThree);
        
        Iterator messagesRetrieved = this.messageManagerToTest.getMessages();
        int i=0;
        while (messagesRetrieved.hasNext()) {
            FacesMessage nextMessage = (FacesMessage) messagesRetrieved.next();
            if (i==0) {
                assertEquals("Ensure first message received is as expected", messageOne, nextMessage);
            } else if (i==1) {
                assertEquals("Ensure second message received is as expected", messageTwo, nextMessage);
            } else if (i==2) {
                assertEquals("Ensure third message received is as expected", messageThree, nextMessage);
            }
            i++;
        }
        assertEquals("Ensure number of messages returned as expected", 3, i);
        
        i=0;
        messagesRetrieved = this.messageManagerToTest.getMessages();
        while (messagesRetrieved.hasNext()) {
            i++;
        }
        assertEquals("Ensure messages added can only be retrieved once", 0, i);
        
        // Test NPE
        NullPointerException npe = null;
        try {
            this.messageManagerToTest.addMessage(null);
        } catch (NullPointerException exception) {
            npe = exception;
        }
        assertNotNull("Ensure NPE thrown for null argument", npe);
    }
}
