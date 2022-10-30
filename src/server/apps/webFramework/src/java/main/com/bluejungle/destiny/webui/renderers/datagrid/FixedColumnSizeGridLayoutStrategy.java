/*
 * Created on May 4, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers.datagrid;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.renderkit.html.HTML;

import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IInitializable;

/**
 * A Grid Layout Strategy which displays everything in fixed size columns. Once
 * a column contains the maximum number of rows, the data will spill into the
 * next columns
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/datagrid/FixedColumnSizeGridLayoutStrategy.java#1 $
 */

public class FixedColumnSizeGridLayoutStrategy extends MulticolumnGridLayoutStrategy implements IConfigurable, IInitializable {

    public static final String COLUMN_SIZE_CONFIG_PROPERTY_NAME = "ColumnSize";
    private static final Integer DEFAULT_COLUMN_SIZE = new Integer(5);

    private IConfiguration config;
    private int columnSize;

    /**
     * @see com.bluejungle.framework.comp.IConfigurable#getConfiguration()
     */
    public IConfiguration getConfiguration() {
        return this.config;
    }

    /**
     * @see com.bluejungle.framework.comp.IConfigurable#setConfiguration(com.bluejungle.framework.comp.IConfiguration)
     */
    public void setConfiguration(IConfiguration config) {
        this.config = config;
    }

    /**
     * @see com.bluejungle.framework.comp.IInitializable#init()
     */
    public void init() {
        IConfiguration myConfig = getConfiguration();
        if (myConfig != null) {
            this.columnSize = ((Integer) myConfig.get(COLUMN_SIZE_CONFIG_PROPERTY_NAME, DEFAULT_COLUMN_SIZE)).intValue();
        } else {
            this.columnSize = DEFAULT_COLUMN_SIZE.intValue();
        }
    }

    /**
     * @see com.bluejungle.destiny.webui.renderers.datagrid.IGridLayoutStrategy#encodeChildren(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        int numColumns = calculateNumColumns(context, component);

        writeColumns(context, component, numColumns);
    }

    /**
     * Calculate the number of columns to display when rendering the specified
     * component as a data grid
     * 
     * @param context
     * @param component
     * @return the number of columns to display
     */
    private int calculateNumColumns(FacesContext context, UIComponent component) {
        ViewableDataParams viewableDataParams = getViewableDataParams(context, component);

        int totalRows = viewableDataParams.getTotalRows();

        int numColumns = totalRows / columnSize;

        /*
         * If it's not even, we may need one more Is there a bit manipulation
         * way of doing this?
         */
        if ((totalRows % columnSize) > 0) {
            numColumns += 1;
        }

        return numColumns;
    }

    /**
     * Render the columns of the data grid
     * 
     * @param context
     * @param component
     * @param viewableDataParams
     * @param numColumns
     * @param writer
     * @throws IOException
     */
    private void writeColumns(FacesContext context, UIComponent component, int numColumns) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        ViewableDataParams viewableDataParams = getViewableDataParams(context, component);
        int firstRowToDisplay = viewableDataParams.getFirstRow();
        int totalRowCount = viewableDataParams.getTotalRows();

        for (int j = 0; j < numColumns; j++) {
            writer.startElement(HTML.TD_ELEM, component);
            writer.startElement(HTML.TABLE_ELEM, component);

            int firstRowInColumn = (j * columnSize) + firstRowToDisplay;
            int lastRowInColumn = ((j + 1) * columnSize) + firstRowToDisplay - 1;
            if (lastRowInColumn > totalRowCount - 1) {
                lastRowInColumn = totalRowCount - 1;
            }
            for (int i = firstRowInColumn; i <= lastRowInColumn; i++) {
                writeRow(context, component, i);
            }

            writer.endElement(HTML.TABLE_ELEM);
            writer.endElement(HTML.TD_ELEM);
        }
    }
}