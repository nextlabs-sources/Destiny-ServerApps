/*
 * Created on Mar 23, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIForm;

import org.apache.myfaces.taglib.html.HtmlFormTagBase;

/**
 * This is the HTML form tag test class.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/HtmlFormTagTest.java#1 $
 */

public class HtmlFormTagTest extends BaseJSFTest {

    /**
     * This test verifies the basic aspect of the tag class
     */
    public void testHtmlFormTagClassBasics() {
        HtmlFormTag tag = new HtmlFormTag();
        assertTrue("The form tag class should extend the myFaces form tag", tag instanceof HtmlFormTagBase);
        assertEquals("The form tag class should have its own component type", HtmlFormTag.COMPONENT_TYPE, tag.getComponentType());
        assertNull("The form tag class should have no renderer type by default", tag.getRendererType());
    }

    /**
     * This test verifies the various setter and getter method for the tag
     */
    public void testHtmlFormTagGetterSetter() {
        HtmlFormTag tag = new HtmlFormTag();
        final String myMethod = "myMethod";
        tag.setMethod(myMethod);
        assertEquals("The form tag should retain the method argument", myMethod, tag.getMethod());
    }

    /**
     * Test getter/setter pair for inputToFocus attribute     
     */
    public void testGetSetInputToFocus() {
        HtmlFormTag tag = new HtmlFormTag();
        
        // First, ensure that it's null
        assertNull("Ensure inputToFocus is null", tag.getInputToFocus());
        
        // Now set it and check if it was properly set
        final String inputToFocus = "myInputToFocus";
        tag.setInputToFocus(inputToFocus);
        assertEquals("Ensure inputToFocus properly set", inputToFocus, tag.getInputToFocus());        
    }
    
    /**
     * This test verifies that the tag class assigns the right properties to the
     * component
     */
    public void testHtmlFormTagSetProperties() {
        HtmlFormTag tag = new HtmlFormTag();
        final String myAccept = "myAccept";
        final String myAcceptCharset = "myAcceptCharset";
        final String myDir = "myDir";
        final String myEncType = "myEncType";
        final String myLang = "myLang";
        final String myMethod = "myMethod";
        final String myName = "myName";
        final String myOnClick = "myOnClick";
        final String myOndblClick = "myOndblClick";
        final String myKeyDown = "myKeyDown";
        final String myKeyPress = "myKeyPress";
        final String myKeyUp = "myKeyUp";
        final String myMouseDown = "myMouseDown";
        final String myMouseMove = "myMouseMove";
        final String myMouseOut = "myMouseOut";
        final String myMouseOver = "myMouseOver";
        final String myMouseUp = "myMouseUp";
        final String myReset = "myReset";
        final String myRendered = "false";
        final String myStyle = "myStyle";
        final String myStyleClass = "myStyleClass";
        final String myTarget = "myTarget";
        final String inputToFocus = "myInputToFocus";
        
        tag.setAccept(myAccept);
        tag.setAcceptCharset(myAcceptCharset);
        tag.setDir(myDir);
        tag.setEnctype(myEncType);
        tag.setLang(myLang);
        tag.setMethod(myMethod);
        tag.setName(myName);
        tag.setOnclick(myOnClick);
        tag.setOndblclick(myOndblClick);
        tag.setOnkeydown(myKeyDown);
        tag.setOnkeypress(myKeyPress);
        tag.setOnkeyup(myKeyUp);
        tag.setOnmousedown(myMouseDown);
        tag.setOnmousemove(myMouseMove);
        tag.setOnmouseout(myMouseOut);
        tag.setOnmouseover(myMouseOver);
        tag.setOnmouseup(myMouseUp);
        tag.setOnreset(myReset);
        tag.setRendered(myRendered);
        tag.setStyle(myStyle);
        tag.setStyleClass(myStyleClass);
        tag.setTarget(myTarget);
        tag.setInputToFocus(inputToFocus);
        
        //assign the properties to the component
        UIForm uiform = new UIForm();
        tag.setProperties(uiform);

        //check that the values have been properly assigned
        assertEquals(myAccept, uiform.getAttributes().get("accept"));
        assertEquals(myAcceptCharset, uiform.getAttributes().get("accept-charset"));
        assertEquals(myDir, uiform.getAttributes().get("dir"));
        assertEquals(myEncType, uiform.getAttributes().get("enctype"));
        assertEquals(myKeyDown, uiform.getAttributes().get("onkeydown"));
        assertEquals(myKeyPress, uiform.getAttributes().get("onkeypress"));
        assertEquals(myKeyUp, uiform.getAttributes().get("onkeyup"));
        assertEquals(myLang, uiform.getAttributes().get("lang"));
        assertEquals(myMethod, uiform.getAttributes().get("method"));
        assertEquals(myMouseDown, uiform.getAttributes().get("onmousedown"));
        assertEquals(myMouseMove, uiform.getAttributes().get("onmousemove"));
        assertEquals(myMouseOut, uiform.getAttributes().get("onmouseout"));
        assertEquals(myMouseOver, uiform.getAttributes().get("onmouseover"));
        assertEquals(myMouseUp, uiform.getAttributes().get("onmouseup"));
        assertEquals(myName, uiform.getAttributes().get("name"));
        assertEquals(myOnClick, uiform.getAttributes().get("onclick"));
        assertEquals(myOndblClick, uiform.getAttributes().get("ondblclick"));
        assertFalse(uiform.isRendered());
        assertEquals(myReset, uiform.getAttributes().get("onreset"));
        assertEquals(myStyle, uiform.getAttributes().get("style"));
        assertEquals(myStyleClass, uiform.getAttributes().get("styleClass"));
        assertEquals(myTarget, uiform.getAttributes().get("target"));
        assertEquals(inputToFocus, uiform.getAttributes().get("inputToFocus"));
        
    }
}