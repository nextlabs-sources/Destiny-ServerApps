/*
 * Created on Mar 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIData;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.model.ListDataModel;

import org.apache.myfaces.custom.datascroller.HtmlDataScroller;

import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.tags.BaseJSFTest;

/**
 * This is the test class for the HTML scroller renderer class.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/renderers/HtmlListScrollerRendererTest.java#1 $
 */

public class HtmlListScrollerRendererTest extends BaseJSFTest {

    private static final int TOTAL_NB_RECORDS = 100;
    private static final int PAGE_SIZE = 5;
    private static final String ACTIVE_LINK = "activeLink";
    private static final String INACTIVE_LINK = "inactiveLink";

    /**
     * Checks that a given link is using the right style class
     * @param facetName name of the facet to analyze
     * @param expectedStyle name of the style class from 
     * @param writer
     */
    private void checkLinkStyle (String facetName, String expectedStyle, MockResponseWriter writer) {
        assertTrue("The '" + facetName + "' link should be renderered with the correct class", writer.getResponse().indexOf("class=\"" + expectedStyle + "\"") > 0);
    }
    
    /**
     * Create a data scroller control with the appropriate properties
     * 
     * @return a data scroller control
     */
    private HtmlDataScroller createDataScroller() {
        HtmlDataScroller scroller = new HtmlDataScroller();
        scroller.setId("myScroller");
        scroller.setPaginatorActiveColumnClass(ACTIVE_LINK);
        scroller.setPaginatorColumnClass(INACTIVE_LINK);
        return scroller;
    }

    /**
     * Creates a valid UIData control bound to a data model with 100 records,
     * displayed 5 by 5
     * 
     * @return a data table control
     */
    private UIData createTableAndDataModel() {
        UIData dataTable = new UIData();
        dataTable.setId("myDataTable");
        dataTable.setRendererType("org.apache.myfaces.Table");
        dataTable.setRows(PAGE_SIZE);
        ListDataModel dataModel = new ListDataModel();
        List data = new ArrayList();
        for (int i = 0; i < TOTAL_NB_RECORDS; i++) {
            data.add(new Object());
        }
        dataModel.setWrappedData(data);
        dataTable.setValue(dataModel);
        return (dataTable);
    }

    /**
     * This test verifies that the scroller can go first only if the data list
     * is in correct condition
     */
    public void testListScrollerCanGoFirst() {
        HtmlDataScroller dataScroller = new HtmlDataScroller();
        HtmlListScrollerRenderer renderer = new HtmlListScrollerRenderer();

        //Pass a wrong argument and make sure result is false
        UIInput wrongComp = new UIInput();
        boolean exThrown = false;
        try {
            renderer.isGoFirstEnabled(this.facesContext, wrongComp);
        } catch (IllegalArgumentException e) {
            exThrown = true;
        }
        assertTrue("List scroller renderer method canGoFirst should reject invalid arguments", exThrown);

        //Test the method with no UI data provided (and no parent UIData)
        exThrown = false;
        try {
            renderer.isGoFirstEnabled(this.facesContext, dataScroller);
        } catch (IllegalArgumentException e) {
            exThrown = true;
        }
        assertTrue("List scroller renderer method canGoFirst scroller without for and UIData control", exThrown);

        //Now, test with a real data table
        UIData parentDataTable = createTableAndDataModel();
        parentDataTable.getChildren().add(dataScroller);
        parentDataTable.setFirst(0); //We are at the beginning
        boolean result = renderer.isGoFirstEnabled(this.facesContext, dataScroller);
        assertFalse("Scroller cannot go first if the list is on the first active data set", result);

        for (int i = 0; i < PAGE_SIZE; i++) {
            parentDataTable.setFirst(i); //We are at the last row of the first
            // data set
            result = renderer.isGoFirstEnabled(this.facesContext, dataScroller);
            assertFalse("Scroller cannot go first if the list is on the first active data set", result);
        }
        parentDataTable.setFirst(PAGE_SIZE + 1); //We are beyond the first data
        // set
        result = renderer.isGoFirstEnabled(this.facesContext, dataScroller);
        assertTrue("Scroller can go first if the list is after the first active data set", result);
    }

    public void testListScrollerCanGoNext() {
        HtmlDataScroller dataScroller = new HtmlDataScroller();
        HtmlListScrollerRenderer renderer = new HtmlListScrollerRenderer();

        //Pass a wrong argument and make sure result is false
        UIInput wrongComp = new UIInput();
        boolean exThrown = false;
        try {
            renderer.isGoNextEnabled(this.facesContext, wrongComp);
        } catch (IllegalArgumentException e) {
            exThrown = true;
        }
        assertTrue("List scroller renderer method canGoNext should reject invalid arguments", exThrown);

        //Test the method with no UI data provided (and no parent UIData)
        exThrown = false;
        try {
            renderer.isGoLastEnabled(this.facesContext, dataScroller);
        } catch (IllegalArgumentException e) {
            exThrown = true;
        }
        assertTrue("List scroller renderer method canGoNext scroller without for and UIData control", exThrown);

        //Test with a real data table
        UIData parentDataTable = createTableAndDataModel();
        parentDataTable.getChildren().add(dataScroller);
        parentDataTable.setFirst(0); //We are at the beginning
        boolean result = renderer.isGoNextEnabled(this.facesContext, dataScroller);
        assertTrue("Scroller can go to the next if the list is on the first active data set", result);

        parentDataTable.setFirst(TOTAL_NB_RECORDS - PAGE_SIZE); //We are in the
        // last page
        result = renderer.isGoNextEnabled(this.facesContext, dataScroller);
        assertFalse("Scroller cannot go to the next if the list is in the last page", result);

        //We are just before the last page
        parentDataTable.setFirst(TOTAL_NB_RECORDS - PAGE_SIZE - 1);
        result = renderer.isGoNextEnabled(this.facesContext, dataScroller);
        assertTrue("Scroller can go to the next if the list is before the last page", result);
    }

    /**
     * This test verifies that the scroller can go to the last data set when it
     * is possible.
     */

    public void testListScrollerCanGoLast() {
        HtmlDataScroller dataScroller = new HtmlDataScroller();
        HtmlListScrollerRenderer renderer = new HtmlListScrollerRenderer();

        //Pass a wrong argument and make sure result is false
        UIInput wrongComp = new UIInput();
        boolean exThrown = false;
        try {
            renderer.isGoLastEnabled(this.facesContext, wrongComp);
        } catch (IllegalArgumentException e) {
            exThrown = true;
        }
        assertTrue("List scroller renderer method canGoPrevious should reject invalid arguments", exThrown);

        //Test the method with no UI data provided (and no parent UIData)
        exThrown = false;
        try {
            renderer.isGoLastEnabled(this.facesContext, dataScroller);
        } catch (IllegalArgumentException e) {
            exThrown = true;
        }
        assertTrue("List scroller renderer method canGoLast scroller without for and UIData control", exThrown);

        //Test with a real data table
        UIData parentDataTable = createTableAndDataModel();
        parentDataTable.getChildren().add(dataScroller);
        parentDataTable.setFirst(0); //We are at the beginning
        boolean result = renderer.isGoLastEnabled(this.facesContext, dataScroller);
        assertTrue("Scroller can go to the last if the list is on the first active data set", result);

        parentDataTable.setFirst(TOTAL_NB_RECORDS - PAGE_SIZE); //We are in the
        // last page
        result = renderer.isGoLastEnabled(this.facesContext, dataScroller);
        assertFalse("Scroller cannot go to the last if the list is already in the last page", result);

        //We are just before the last page
        parentDataTable.setFirst(TOTAL_NB_RECORDS - PAGE_SIZE - 1);
        result = renderer.isGoLastEnabled(this.facesContext, dataScroller);
        assertTrue("Scroller can go to the last if the list is before the last page", result);
    }

    /**
     * This test verifies that the scroller can go previous when it is possible.
     */
    public void testListScrollerCanGoPrevious() {
        HtmlDataScroller dataScroller = new HtmlDataScroller();
        HtmlListScrollerRenderer renderer = new HtmlListScrollerRenderer();

        //Pass a wrong argument and make sure result is false
        UIInput wrongComp = new UIInput();
        boolean exThrown = false;
        try {
            renderer.isGoFirstEnabled(this.facesContext, wrongComp);
        } catch (IllegalArgumentException e) {
            exThrown = true;
        }
        assertTrue("List scroller renderer method canGoPrevious should reject invalid arguments", exThrown);

        //Test the method with no UI data provided (and no parent UIData)
        exThrown = false;
        try {
            renderer.isGoPreviousEnabled(this.facesContext, dataScroller);
        } catch (IllegalArgumentException e) {
            exThrown = true;
        }
        assertTrue("List scroller renderer method canGoPrevious scroller without for and UIData control", exThrown);

        //Now, test with a real data table
        UIData parentDataTable = createTableAndDataModel();
        parentDataTable.getChildren().add(dataScroller);
        parentDataTable.setFirst(0); //We are at the beginning
        boolean result = renderer.isGoPreviousEnabled(this.facesContext, dataScroller);
        assertFalse("Scroller cannot go previous if the list is on the first active data set", result);

        for (int i = 0; i < PAGE_SIZE; i++) {
            parentDataTable.setFirst(i); //We are at the last row of the first
            // data set
            result = renderer.isGoPreviousEnabled(this.facesContext, dataScroller);
            assertFalse("Scroller cannot go previous if the list is on the first active data set", result);
        }
        parentDataTable.setFirst(PAGE_SIZE + 1); //We leave the first dataset
        result = renderer.isGoPreviousEnabled(this.facesContext, dataScroller);
        assertTrue("Scroller can go previous if the list is after the first active data set", result);
    }

    /**
     * This test is a bit redundant with the "canGo" tests. It walks through the
     * list and checks the total number of times, record by record, where
     * various paging controls are enabled.
     */
    public void testListScrollerControlsState() {
        UIData dataTable = createTableAndDataModel();
        HtmlDataScroller dataScroller = new HtmlDataScroller();
        HtmlListScrollerRenderer renderer = new HtmlListScrollerRenderer();
        int nbGoFirst = 0;
        int nbGoLast = 0;
        int nbGoNext = 0;
        int nbGoPrevious = 0;

        dataTable.getChildren().add(dataScroller);
        for (int currentRow = 0; currentRow < TOTAL_NB_RECORDS; currentRow++) {
            dataTable.setFirst(currentRow);
            if (renderer.isGoFirstEnabled(this.facesContext, dataScroller)) {
                nbGoFirst++;
            }
            if (renderer.isGoLastEnabled(this.facesContext, dataScroller)) {
                nbGoLast++;
            }
            if (renderer.isGoNextEnabled(this.facesContext, dataScroller)) {
                nbGoNext++;
            }
            if (renderer.isGoPreviousEnabled(this.facesContext, dataScroller)) {
                nbGoPrevious++;
            }
        }

        //Now, do the count and make sure the controls were enabled the right
        // number of times.
        assertEquals(TOTAL_NB_RECORDS - PAGE_SIZE, nbGoFirst);
        assertEquals(TOTAL_NB_RECORDS - PAGE_SIZE, nbGoLast);
        assertEquals(TOTAL_NB_RECORDS - PAGE_SIZE, nbGoNext);
        assertEquals(TOTAL_NB_RECORDS - PAGE_SIZE, nbGoPrevious);
    }

    /**
     * This test verifies that the rendering of the "previous" and "first"
     * control occurs properly.
     */
    public void testListScrollerEncodeBegin() {
        UIData dataTable = createTableAndDataModel();
        HtmlDataScroller dataScroller = createDataScroller();
        dataTable.getChildren().add(dataScroller);
        HtmlListScrollerRenderer renderer = new HtmlListScrollerRenderer();

        //Try bad input
        boolean exThrown = false;
        try {
            UIInput wrongComp = new UIInput();
            renderer.encodeBegin(this.facesContext, wrongComp);
        } catch (IllegalArgumentException e) {
            exThrown = true;
        } catch (IOException e) {
            fail("List scroller renderer encodeBegin should not throw IOException");
        }
        assertTrue("List scroller renderer method encodeBegin should reject invalid arguments", exThrown);

        //Try good input, but without any interesting facet
        try {
            MockResponseWriter writer = new MockResponseWriter();
            this.facesContext.setResponseWriter(writer);
            this.facesContext.setExternalContext(new MockExternalContext("/myApp"));
            renderer.encodeBegin(this.facesContext, dataScroller);
            assertEquals("Nothing for previous / first should be rendered by scroller renderer if no facets are there", "<div", writer.getResponse());

            //Checks that the style is rendered
            dataScroller.setStyleClass("foo");
            writer = new MockResponseWriter();
            this.facesContext.setResponseWriter(writer);
            renderer.encodeBegin(this.facesContext, dataScroller);
            assertEquals("Nothing for previous / first should be rendered by scroller renderer if no facets are there", "<div class=\"foo\"", writer.getResponse());
        } catch (IOException e) {
            fail("List scroller renderer encodeBegin should not throw IOException");
        }

        //Try good input, with a "first" facet
        try {
            MockResponseWriter writer = new MockResponseWriter();
            this.facesContext.setResponseWriter(writer);
            UIOutput linkText = new UIOutput();
            linkText.setValue("first");
            dataScroller.getFacets().put("first", linkText);
            renderer.encodeBegin(this.facesContext, dataScroller);
            checkLinkStyle ("first", INACTIVE_LINK, writer);

            //Make the facet active
            writer.reset();
            dataTable.setFirst(PAGE_SIZE + 1);
            renderer.encodeBegin(this.facesContext, dataScroller);
            checkLinkStyle ("first", ACTIVE_LINK, writer);
        } catch (IOException e) {
            fail("List scroller renderer encodeBegin should not throw IOException");
        }
    }

    /**
     * This test verifies that the "first" facet is properly working
     */
    public void testListScrollerFirstFacet() {
        UIData dataTable = createTableAndDataModel();
        HtmlDataScroller dataScroller = createDataScroller();
        dataTable.getChildren().add(dataScroller);
        HtmlListScrollerRenderer renderer = new HtmlListScrollerRenderer();
        MockResponseWriter writer = new MockResponseWriter();
        this.facesContext.setResponseWriter(writer);
        this.facesContext.setExternalContext(new MockExternalContext("/myApp"));

        try {
            UIOutput linkText = new UIOutput();
            linkText.setValue("first");
            dataScroller.getFacets().put("first", linkText);
            renderer.encodeBegin(this.facesContext, dataScroller);
            checkLinkStyle ("first", INACTIVE_LINK, writer);

            //Make the facet active
            writer.reset();
            dataTable.setFirst(PAGE_SIZE + 1);
            renderer.encodeBegin(this.facesContext, dataScroller);
            String response = writer.getResponse();
            checkLinkStyle ("first", ACTIVE_LINK, writer);
        } catch (IOException e) {
            fail("List scroller renderer encodeBegin should not throw IOException");
        }
    }

    /**
     * This test verifies that the "last" facet is properly working
     */
    public void testListScrollerLastFacet() {
        UIData dataTable = createTableAndDataModel();
        HtmlDataScroller dataScroller = createDataScroller();
        dataTable.getChildren().add(dataScroller);
        HtmlListScrollerRenderer renderer = new HtmlListScrollerRenderer();
        MockResponseWriter writer = new MockResponseWriter();
        this.facesContext.setResponseWriter(writer);
        this.facesContext.setExternalContext(new MockExternalContext("/myApp"));

        try {
            //Make the facet inactive
            dataTable.setFirst(TOTAL_NB_RECORDS - PAGE_SIZE);
            UIOutput linkText = new UIOutput();
            linkText.setValue("last");
            dataScroller.getFacets().put("last", linkText);
            renderer.encodeEnd(this.facesContext, dataScroller);
            String response = writer.getResponse();
            checkLinkStyle ("last", INACTIVE_LINK, writer);

            //Make the facet active
            writer.reset();
            dataTable.setFirst(TOTAL_NB_RECORDS - PAGE_SIZE - 1);
            renderer.encodeEnd(this.facesContext, dataScroller);
            response = writer.getResponse();
            checkLinkStyle ("last", ACTIVE_LINK, writer);
        } catch (IOException e) {
            fail("List scroller renderer encodeBegin should not throw IOException");
        }
    }

    /**
     * This test verifies that the "next" facet is properly working
     */
    public void testListScrollerNextFacet() {
        UIData dataTable = createTableAndDataModel();
        HtmlDataScroller dataScroller = createDataScroller();
        dataTable.getChildren().add(dataScroller);
        HtmlListScrollerRenderer renderer = new HtmlListScrollerRenderer();
        MockResponseWriter writer = new MockResponseWriter();
        this.facesContext.setResponseWriter(writer);
        this.facesContext.setExternalContext(new MockExternalContext("/myApp"));

        try {
            //Make the facet inactive
            dataTable.setFirst(TOTAL_NB_RECORDS - PAGE_SIZE);
            UIOutput linkText = new UIOutput();
            linkText.setValue("next");
            dataScroller.getFacets().put("next", linkText);
            renderer.encodeEnd(this.facesContext, dataScroller);
            String response = writer.getResponse();
            checkLinkStyle ("next", INACTIVE_LINK, writer);

            //Make the facet active
            writer.reset();
            dataTable.setFirst(TOTAL_NB_RECORDS - PAGE_SIZE - 1);
            renderer.encodeEnd(this.facesContext, dataScroller);
            response = writer.getResponse();
            checkLinkStyle ("last", ACTIVE_LINK, writer);
        } catch (IOException e) {
            fail("List scroller renderer encodeBegin should not throw IOException");
        }
    }

    /**
     * This test verifies that the "previous" facet is properly working
     */
    public void testListScrollerPreviousFacet() {
        UIData dataTable = createTableAndDataModel();
        HtmlDataScroller dataScroller = createDataScroller();
        dataTable.getChildren().add(dataScroller);
        HtmlListScrollerRenderer renderer = new HtmlListScrollerRenderer();
        MockResponseWriter writer = new MockResponseWriter();
        this.facesContext.setResponseWriter(writer);
        this.facesContext.setExternalContext(new MockExternalContext("/myApp"));

        try {
            UIOutput linkText = new UIOutput();
            linkText.setValue("previous");
            dataScroller.getFacets().put("previous", linkText);
            renderer.encodeBegin(this.facesContext, dataScroller);
            checkLinkStyle ("previous", INACTIVE_LINK, writer);

            //Make the facet active
            writer.reset();
            dataTable.setFirst(PAGE_SIZE + 1);
            renderer.encodeBegin(this.facesContext, dataScroller);
            String response = writer.getResponse();
            checkLinkStyle ("previous", ACTIVE_LINK, writer);
        } catch (IOException e) {
            fail("List scroller renderer encodeBegin should not throw IOException");
        }
    }
}