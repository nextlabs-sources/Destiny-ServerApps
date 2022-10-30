/*
 * Created on Apr 27, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;

import junit.framework.TestCase;

/**
 * JUnit test for UIMessages component
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/controls/UIMessagesTest.java#1 $
 */
public class UIMessagesTest extends TestCase {

    private UIMessages componentToTest;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UIMessagesTest.class);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        componentToTest = new UIMessages();
    }

    /*
     * Class under test for String getFamily()
     */
    public void testGetFamily() {
        assertEquals("Ensure correct component family is retrieved.", "com.bluejungle.destiny.Messages", componentToTest.getFamily());
    }

    public void testGetMessagesToRender() {
        FacesContext facesContext = new MockFacesContext();
        
        // If there are no messages, make sure empty iterator is returned
        Iterator messageIterator = componentToTest.getMessagesToRender(facesContext);
        int numMessage = 0;
        while (messageIterator.hasNext()) {
            messageIterator.next();
            numMessage++;
        }
        assertEquals("Ensure no messages initially", 0, numMessage);
        
        // Add a message in both the global space and for a specific client id
        FacesMessage message = new FacesMessage("I am the best message ever!");
        facesContext.addMessage(null, message);
        
        String clientId = "TheClient";
        message = new FacesMessage("No, I am the best message ever!");
        facesContext.addMessage(clientId, message);
        
        // Ensure that there are two messages returned when client id is not set
        messageIterator = componentToTest.getMessagesToRender(facesContext);
        numMessage = 0;
        while (messageIterator.hasNext()) {
            messageIterator.next();
            numMessage++;
        }
        assertEquals("Ensure two messages retrieved", 2, numMessage);
        
        // Now, set the for client id and make sure only the one messages is retrieved
        componentToTest.setForClientId(clientId);
        messageIterator = componentToTest.getMessagesToRender(facesContext);
        numMessage = 0;
        while (messageIterator.hasNext()) {
            messageIterator.next();
            numMessage++;
        }
        assertEquals("Ensure one messages retrieved for specific client", 1, numMessage);        

    }

    public void testGetSetForClientId() {
        assertNull("Ensure client id is initially null", componentToTest.getForClientId());

        // Now, set it
        String clientId = "foo";
        componentToTest.setForClientId(clientId);

        assertEquals("Ensure client id set is that retrieved", clientId, componentToTest.getForClientId());
    }
}