/*
 * Created on Mar 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;
import java.util.List;

import javax.faces.component.html.HtmlCommandButton;

import com.bluejungle.destiny.webui.controls.UIMenu;
import com.bluejungle.destiny.webui.controls.UIMenuItem;
import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * Test the MenuRenderer
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/MenuRendererTest.java#1 $
 */
public class MenuRendererTest extends BaseJSFTest {

    private MenuRenderer menuRenderToTest;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MenuRendererTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        menuRenderToTest = new MenuRenderer();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() {
        super.tearDown();
    }

    /**
     * Constructor for MenuRendererTest.
     * 
     * @param arg0
     */
    public MenuRendererTest(String arg0) {
        super(arg0);
    }

    /*
     * Test MenuRendere.getRendersChildren()
     */
    public void testGetRendersChildren() {
        assertTrue("Ensure reders children is true", menuRenderToTest.getRendersChildren());
    }

    /*
     * Test encoding
     */
    public void testEncode() throws IOException {
        // Make sure that it doesn't blow up
        UIMenu componentToRender = new UIMenu();
        UIMenuItem menuItemOne = new UIMenuItem();
        UIMenuItem menuItemTwo = new UIMenuItem();
        List children = componentToRender.getChildren();
        children.add(menuItemOne);
        children.add(menuItemTwo);

        MockResponseWriter mockWriter = new MockResponseWriter();
        this.facesContext.setResponseWriter(mockWriter);
        this.facesContext.setExternalContext(new MockExternalContext("/myApp"));

        menuRenderToTest.encodeBegin(this.facesContext, componentToRender);
        menuRenderToTest.encodeChildren(this.facesContext, componentToRender);
        menuRenderToTest.encodeEnd(this.facesContext, componentToRender);

        // Now, add an illegal child and make sure an IllegalStateException is
        // thrown
        children.add(new HtmlCommandButton());
        Exception expectedException = null;
        try {
            menuRenderToTest.encodeBegin(this.facesContext, componentToRender);
            menuRenderToTest.encodeChildren(this.facesContext, componentToRender);
        } catch (IllegalStateException exception) {
            expectedException = exception;
        }
        assertNotNull("testEncode - Ensure IllegalStateException is thrown for improper child element", expectedException);
        
        // Test the MenuRender with a non UI Menu component        
        expectedException = null;
        try {
            menuRenderToTest.encodeBegin(this.facesContext, new HtmlCommandButton());            
        } catch (IllegalStateException exception) {
            expectedException = exception;
        }
        assertNotNull("testEncode - Ensure IllegalStateException is thrown for improper componet", expectedException);
    }

    /*
     * Class under test for void decode(FacesContext, UIComponent)
     */
    public void testDecode() {
        // Make sure that it doesn't blow up
        UIMenu componentToDecode = new UIMenu();
        UIMenuItem menuItemOne = new UIMenuItem();
        UIMenuItem menuItemTwo = new UIMenuItem();
        List children = componentToDecode.getChildren();
        children.add(menuItemOne);
        children.add(menuItemTwo);

        this.facesContext.setExternalContext(new MockExternalContext("/myApp"));
        
        menuRenderToTest.decode(this.facesContext, componentToDecode);

        // Now, add an illegal child and make sure an IllegalStateException is
        // thrown
        children.add(new HtmlCommandButton());
        Exception expectedException = null;
        try {
            menuRenderToTest.decode(this.facesContext, componentToDecode);
        } catch (IllegalStateException exception) {
            expectedException = exception;
        }
        assertNotNull("testDecode - Ensure IllegalStateException is thrown for improper child element", expectedException);
    }

}