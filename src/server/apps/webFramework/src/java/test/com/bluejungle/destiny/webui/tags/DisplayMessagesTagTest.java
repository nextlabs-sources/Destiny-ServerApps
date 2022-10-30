/*
 * Created on Apr 27, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import com.bluejungle.destiny.webui.controls.UIMessages;

import junit.framework.TestCase;

/**
 * JUNit test class for DisplayMessagesTag
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/DisplayMessagesTagTest.java#1 $
 */

public class DisplayMessagesTagTest extends TestCase {

    private DisplayMessagesTag tagToTest;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DisplayMessagesTagTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        tagToTest = new DisplayMessagesTag();
    }

    public void testRelease() {
        UIMessages messagesComponent = new UIMessages();

        // Set some proeprties and make sure they get set
        String forClientId = "clientId";
        String fatalStyleClass = "fatalStyleClass";
        String errorStyleClass = "errorStyleClass";
        String warnStyleClass = "warnStyleClass";
        String infoStyleClass = "infoStyleClass";

        tagToTest.set_for(forClientId);
        tagToTest.setFatalClass(fatalStyleClass);
        tagToTest.setErrorClass(errorStyleClass);
        tagToTest.setWarnClass(warnStyleClass);
        tagToTest.setInfoClass(infoStyleClass);

        tagToTest.setProperties(messagesComponent);

        assertEquals("Ensure forClientId was set propertly", forClientId, messagesComponent.getForClientId());
        assertEquals("Ensure fatal class attribute was set propertly", fatalStyleClass, messagesComponent.getAttributes().get("fatalStyleClass"));
        assertEquals("Ensure error class attribute was set propertly", errorStyleClass, messagesComponent.getAttributes().get("errorStyleClass"));
        assertEquals("Ensure warn class attribute was set property", warnStyleClass, messagesComponent.getAttributes().get("warnStyleClass"));
        assertEquals("Ensure info class attribute was set properly", infoStyleClass, messagesComponent.getAttributes().get("infoStyleClass"));

        // Now, release and make sure they are null
        tagToTest.release();

        messagesComponent = new UIMessages();
        tagToTest.setProperties(messagesComponent);

        assertNull("Ensure forClientId is null", messagesComponent.getForClientId());
        assertNull("Ensure fatal class attribute is null", messagesComponent.getAttributes().get("fatalStyleClass"));
        assertNull("Ensure error class attribute is null", messagesComponent.getAttributes().get("errorStyleClass"));
        assertNull("Ensure warn class attribute is null", messagesComponent.getAttributes().get("warnStyleClass"));
        assertNull("Ensure info class attribute is null", messagesComponent.getAttributes().get("infoStyleClass"));
    }

    /*
     * Test for String getComponentType()
     */
    public void testGetComponentType() {
        assertEquals("Ensure correct component type is retrieved.", "com.bluejungle.destiny.Messages", tagToTest.getComponentType());
    }

    /*
     * Test for String getRendererType()
     */
    public void testGetRendererType() {
        assertEquals("Ensure correct rendere type is retrieved.", "com.bluejungle.destiny.MessagesRenderer", tagToTest.getRendererType());
    }

    /*
     * Test for methods void setProperties(UIComponent), void set_for(String),
     * and all void setXXXXClass(String)
     */
    public void testSetProperties() {
        UIMessages messagesComponent = new UIMessages();

        // Try without any properties set
        tagToTest.setProperties(messagesComponent);

        assertNull("Ensure forClientId is initially null", messagesComponent.getForClientId());
        assertNull("Ensure fatal class attribute is initially null", messagesComponent.getAttributes().get("fatalStyleClass"));
        assertNull("Ensure error class attribute is initially null", messagesComponent.getAttributes().get("errorStyleClass"));
        assertNull("Ensure warn class attribute is initially null", messagesComponent.getAttributes().get("warnStyleClass"));
        assertNull("Ensure info class attribute is initially null", messagesComponent.getAttributes().get("infoStyleClass"));

        // Now, set some proeprties and make sure they get set
        String forClientId = "clientId";
        String fatalStyleClass = "fatalStyleClass";
        String errorStyleClass = "errorStyleClass";
        String warnStyleClass = "warnStyleClass";
        String infoStyleClass = "infoStyleClass";

        tagToTest.set_for(forClientId);
        tagToTest.setFatalClass(fatalStyleClass);
        tagToTest.setErrorClass(errorStyleClass);
        tagToTest.setWarnClass(warnStyleClass);
        tagToTest.setInfoClass(infoStyleClass);

        tagToTest.setProperties(messagesComponent);

        assertEquals("Ensure forClientId was set propertly", forClientId, messagesComponent.getForClientId());
        assertEquals("Ensure fatal class attribute was set propertly", fatalStyleClass, messagesComponent.getAttributes().get("fatalStyleClass"));
        assertEquals("Ensure error class attribute was set propertly", errorStyleClass, messagesComponent.getAttributes().get("errorStyleClass"));
        assertEquals("Ensure warn class attribute was set property", warnStyleClass, messagesComponent.getAttributes().get("warnStyleClass"));
        assertEquals("Ensure info class attribute was set properly", infoStyleClass, messagesComponent.getAttributes().get("infoStyleClass"));
    }
}