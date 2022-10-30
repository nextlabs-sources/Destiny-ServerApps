/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;

import javax.faces.component.UIOutput;

import org.apache.myfaces.renderkit.html.HtmlTextRendererBase;

import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * This is the test class for the CSS include renderer.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/DatePickerRendererTest.java#2 $
 */

public class CSSIncludeRendererTest extends BaseJSFTest {

    /**
     * Constructor
     */
    public CSSIncludeRendererTest() {
        super();
    }

    /**
     * Constructor
     * 
     * @param testName
     *            name of the test
     */
    public CSSIncludeRendererTest(String testName) {
        super(testName);
    }

    /**
     * This test verifies the basic aspects of the class.
     */
    public void testCSSIncludeRendererClassBasics() {
        CSSIncludeRenderer renderer = new CSSIncludeRenderer();
        assertTrue("CSS include renderer class should extend the basic HTML text renderer", renderer instanceof HtmlTextRendererBase);
    }

    /**
     * This test verifies the rendering of CSS inclusion
     */
    public void testCSSIncludeRendererRendering() throws IOException {
        MockFacesContext facesContext = new MockFacesContext();
        facesContext.setExternalContext(new MockExternalContext("/foo"));
        CSSIncludeRenderer renderer = new CSSIncludeRenderer();

        boolean exThrown = false;
        try {
            //Test that null component is not accepted
            renderer.encodeEnd(facesContext, null);
        } catch (NullPointerException e) {
            exThrown = true;
        } catch (IOException e) {
            fail("No IOException should be thrown");
        }
        assertTrue("Null component is an invalid argument", exThrown);

        //Tests the rendering
        MockResponseWriter mockWriter = new MockResponseWriter();
        facesContext.setResponseWriter(mockWriter);
        UIOutput component = new UIOutput();
        component.setValue("/bar");
        renderer.encodeEnd(facesContext, component);

        String result = mockWriter.getResponse();
        final String expectedResult = "<LINK REL=\"StyleSheet\" href=\"/foo/bar\" type=\"text/css\">";
        assertEquals("CSS include Rendering should match", expectedResult, result);
    }
}