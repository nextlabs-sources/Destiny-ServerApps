/*
 * Created on Mar 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;

import javax.faces.component.UIComponent;

import org.apache.myfaces.component.html.ext.HtmlDataTable;
import org.apache.myfaces.custom.sortheader.HtmlCommandSortHeader;

import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * This is the test class for the sort column header renderer
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/HtmlSortColumnHeaderRendererTest.java#1 $
 */

public class HtmlSortColumnHeaderRendererTest extends BaseJSFTest {

    /**
     * This test verifies that the parent class is not called for the encode end
     * function.
     */
    public void testSortColumnHeaderRendererEncodeEnd() {
        MockResponseWriter writer = new MockResponseWriter();
        this.facesContext.setResponseWriter(writer);
        HtmlSortColumnHeaderRenderer renderer = new HtmlSortColumnHeaderRenderer();
        HtmlCommandSortHeader component = new HtmlCommandSortHeader();
        try {
            renderer.encodeEnd(facesContext, component);
        } catch (IOException e) {
            fail("No IOException should be fired during the encodedEnd call in sortColumnHeaderRenderer");
        }
        assertEquals("No response should be generated", 0, writer.getResponse().length());
    }

    /**
     * This test verifies that the correct style class is picked based on the
     * state of the UI component.
     */
    public void testSortColumnHeaderRendererStyleClassRendering() {
        MockFacesContext facesContext = new MockFacesContext();
        MockExternalContext extCtx = new MockExternalContext("/myApp");
        facesContext.setExternalContext(extCtx);
        MockResponseWriter writer = new MockResponseWriter();
        facesContext.setResponseWriter(writer);
        HtmlSortColumnHeaderRenderer renderer = new HtmlSortColumnHeaderRenderer();

        HtmlDataTable dataComponent = new HtmlDataTable();
        HtmlCommandSortHeader sortComponent = new HtmlCommandSortHeader();

        //Leaves the sort header alone and verify the code does not break
        UIComponent parent = sortComponent.findParentDataTable();
        assertNull("There should be no parent data table", parent);
        String styleClass = renderer.getStyleClass(facesContext, sortComponent);
        assertNull("If sort header does not have a parent table, style class should be null", styleClass);

        //Adds the sort header as a child of the table component
        dataComponent.getChildren().add(sortComponent);

        //Make sure the child can find the parent
        parent = sortComponent.findParentDataTable();
        assertEquals("Sort header should be able to find its parent table", dataComponent, parent);

        //Prepare the sort component
        final String downClassName = "downClass";
        final String upClassName = "upClass";
        sortComponent.getAttributes().put(HtmlSortColumnHeaderRenderer.SORT_DOWN_CLASS_NAME_ATTR, downClassName);
        sortComponent.getAttributes().put(HtmlSortColumnHeaderRenderer.SORT_UP_CLASS_NAME_ATTR, upClassName);

        //Make sure the right class is picked based on the component state
        sortComponent.setColumnName("foo");
        dataComponent.setSortColumn("foo");
        dataComponent.setSortAscending(true);
        styleClass = renderer.getStyleClass(facesContext, sortComponent);
        assertNull("No style should be picked if the sort component is not arrow enabled", styleClass);

        //Try sorting up
        sortComponent.setArrow(true);
        styleClass = renderer.getStyleClass(facesContext, sortComponent);
        assertEquals("When arrow enabled, style should be picked if column names match", upClassName, styleClass);

        //Try sorting down
        dataComponent.setSortAscending(false);
        styleClass = renderer.getStyleClass(facesContext, sortComponent);
        assertEquals("When arrow enabled, style should be picked if column names match", downClassName, styleClass);

        //Change the sort column
        dataComponent.setSortColumn("bar");
        styleClass = renderer.getStyleClass(facesContext, sortComponent);
        assertNull("if column names don't match, no sort style class should be used", styleClass);

        //See if user role is taken into account
        dataComponent.setSortColumn("foo");
        sortComponent.setEnabledOnUserRole("dummyRole1, dummyRole2");
        styleClass = renderer.getStyleClass(facesContext, sortComponent);
        assertNull("if control does not show up, no sort style class should be used", styleClass);
    }
}