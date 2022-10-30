/*
 * Created on May 4, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers.datagrid;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.logging.Log;
import org.apache.myfaces.renderkit.RendererUtils;
import org.apache.myfaces.renderkit.html.HTML;

import com.bluejungle.framework.comp.ILogEnabled;

/**
 * BaseGridLayoutStrategy is the base class for all layout strategies
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/datagrid/BaseGridLayoutStrategy.java#1 $
 */
abstract class BaseGridLayoutStrategy implements IGridLayoutStrategy, ILogEnabled {

    private Log log;

    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#getLog()
     */
    public Log getLog() {
        return this.log;
    }

    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#setLog(null)
     */
    public void setLog(Log log) {
        if (log == null) {
            throw new NullPointerException("log cannot be null.");
        }

        this.log = log;
    }

    /**
     * @see com.bluejungle.destiny.webui.renderers.datagrid.IGridLayoutStrategy#encodeBegin(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        writeTableStart(context, component);
    }

    /**
     * @see com.bluejungle.destiny.webui.renderers.datagrid.IGridLayoutStrategy#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        writeTableEnd(context, component);
    }

    /**
     * Retrieve parameters about the data to be viewed
     * 
     * @param context
     * @param component
     * @return parameters about the data to be viewed
     */
    protected ViewableDataParams getViewableDataParams(FacesContext context, UIComponent component) {
        if (!(component instanceof UIData)) {
            throw new IllegalArgumentException("Component type not supported: " + component.getClass().getName());
        }

        UIData uiData = (UIData) component;
        int firstRowToDisplay = uiData.getFirst();
        int numRowsToDisplay = uiData.getRows();
        int totalRowCount = uiData.getRowCount();
        if (numRowsToDisplay <= 0) {
            numRowsToDisplay = totalRowCount - firstRowToDisplay;
        }
        int lastRowToDisplay = firstRowToDisplay + numRowsToDisplay - 1;
        if (lastRowToDisplay > totalRowCount) {
            lastRowToDisplay = totalRowCount;
        }

        return new ViewableDataParams(firstRowToDisplay, lastRowToDisplay);
    }

    /**
     * Write the table start tag
     * 
     * @param context
     * @param component
     * @throws IOException
     */
    protected void writeTableStart(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement(HTML.TABLE_ELEM, component);
        String id = component.getId();
        if (id != null) {
            writer.writeAttribute(HTML.ID_ATTR, id, null);
        }

        String styleClass = (String) component.getAttributes().get(HTML.STYLE_CLASS_ATTR);
        if (styleClass != null) {
            writer.writeAttribute(HTML.CLASS_ATTR, styleClass, null);
        }
    }

    /**
     * Write the table end tag
     * 
     * @param context
     * @param component
     * @throws IOException
     */
    protected void writeTableEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement(HTML.TABLE_ELEM);
    }

    /**
     * Write a data row of the table
     * 
     * @param context
     * @param component
     * @param i
     *            the index into the DataModel for the row to write
     * @throws IOException
     */
    protected void writeRow(FacesContext context, UIComponent component, int i) throws IOException {
        if (!(component instanceof UIData)) {
            throw new IllegalArgumentException("Component type not supported: " + component.getClass().getName());
        }

        UIData uiData = (UIData) component;

        uiData.setRowIndex(i);
        if (uiData.isRowAvailable()) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement(HTML.TR_ELEM, uiData);

            List children = component.getChildren();
            Iterator childIterator = children.iterator();
            while (childIterator.hasNext()) {
                UIComponent nextChild = (UIComponent) childIterator.next();
                if (nextChild instanceof UIColumn) {
                    renderColumnBody(context, uiData, nextChild);
                }
            }

            writer.endElement(HTML.TD_ELEM);
        } else {
            getLog().warn("Row with index, " + i + "is not available.");
        }
    }

    /**
     * Render a single column within a data row
     * 
     * @param context
     * @param uiData
     * @param nextChild
     * @throws IOException
     */
    private void renderColumnBody(FacesContext context, UIData uiData, UIComponent nextChild) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement(HTML.TD_ELEM, uiData);
        RendererUtils.renderChild(context, nextChild);
        writer.endElement(HTML.TD_ELEM);
    }

    /**
     * ViewableDataParams encapulates parameters of the data to be displayed in
     * the data grid
     * 
     * @author sgoldstein
     */
    protected class ViewableDataParams {

        private int firstRow;
        private int lastRow;
        private int totalRows;

        /**
         * Create an instance of ViewableDataParams
         * 
         * @param firstRow
         * @param lastRow
         */
        private ViewableDataParams(int firstRow, int lastRow) {
            this.firstRow = firstRow;
            this.lastRow = lastRow;
            this.totalRows = lastRow - firstRow + 1;
        }

        /**
         * Retrieve the firstRow.
         * 
         * @return the firstRow.
         */
        protected int getFirstRow() {
            return this.firstRow;
        }

        /**
         * Retrieve the lastRow.
         * 
         * @return the lastRow.
         */
        protected int getLastRow() {
            return this.lastRow;
        }

        /**
         * Retrieve the totalRows.
         * 
         * @return the totalRows.
         */
        protected int getTotalRows() {
            return this.totalRows;
        }
    }
}