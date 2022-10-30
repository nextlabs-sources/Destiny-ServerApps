/*
 * Created on Apr 27, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import com.bluejungle.destiny.webui.controls.UIMessages;
import com.bluejungle.destiny.webui.framework.message.MessageManager;
import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;

import junit.framework.TestCase;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/MessagesRendererTest.java#1 $
 */

public class MessagesRendererTest extends TestCase {

    private MessagesRenderer rendererToTest;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MessagesRendererTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        this.rendererToTest = new MessagesRenderer();
    }

    /*
     * Test for void encodeBegin(FacesContext, UIComponent)
     */
    public void testEncodeBegin() throws IOException {
        MockExternalContext mockExternalContext = new MockExternalContext("/foo");
        FacesContext facesContext = new MockFacesContext(mockExternalContext);
        facesContext.setResponseWriter(new MockResponseWriter());
        
        // Are only positive test is to make sure it doesn't blow up
        // Add a message in both the global space and for a specific client id
        FacesMessage message = new FacesMessage("I am the best message ever!");
        facesContext.addMessage(null, message);
        
        String clientId = "TheClient";
        message = new FacesMessage("No, I am the best message ever!");
        facesContext.addMessage(clientId, message);
        
        // Add a message using the messages manager
        MessageManager.getInstance().addMessage(new FacesMessage("Actually, I am the greatest message of all time."));
        
        UIMessages messageComponent = new UIMessages();
        this.rendererToTest.encodeBegin(facesContext, messageComponent);
        
        IllegalArgumentException expectedException = null;
        try
        {
            this.rendererToTest.encodeBegin(facesContext, new UIInput());
        } catch (IllegalArgumentException exception) {
            expectedException = exception;
        }
        assertNotNull("Ensure expected exception was thrown", expectedException);
    }
}
