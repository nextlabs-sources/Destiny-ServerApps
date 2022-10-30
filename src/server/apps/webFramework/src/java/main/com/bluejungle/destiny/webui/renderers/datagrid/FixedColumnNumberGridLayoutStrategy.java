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
 * A Grid Layout strategy which will layout elements in a fixed set of columns,
 * with the elements equally dispersed amongst the columns
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/datagrid/FixedColumnNumberGridLayoutStrategy.java#1 $
 */

public class FixedColumnNumberGridLayoutStrategy extends MulticolumnGridLayoutStrategy implements IConfigurable, IInitializable {

    public static final String NUMBER_OF_COLUMNS_CONFIG_PROPERTY_NAME = "NumberOfColumns";
    private static final Integer DEFAULT_NUMBER_OF_COLUMNS = new Integer(3);

    private IConfiguration config;
    private int numColumns;

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
            this.numColumns = ((Integer) myConfig.get(NUMBER_OF_COLUMNS_CONFIG_PROPERTY_NAME, DEFAULT_NUMBER_OF_COLUMNS)).intValue();
        } else {
            this.numColumns = DEFAULT_NUMBER_OF_COLUMNS.intValue();
        }
    }
    
    /**
     * @see com.bluejungle.destiny.webui.renderers.datagrid.IGridLayoutStrategy#encodeChildren(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        writeColumns(context, component);
    }

    /**
     * @param firstRowToDisplay
     * @param lastRowToDisplay
     * @return
     */
    private int calculateNumRowsInColumn(int rowsLeftToDisplay, int columnNumber) {
        int columnsLeft = numColumns - columnNumber + 1;
        int numRowsInColumn = rowsLeftToDisplay / columnsLeft;

        /*
         * If it's not even, we may need one more 
         * Is there a bit manipulation way of doing this?
         */
        if ((rowsLeftToDisplay % columnsLeft) > 0) {
            numRowsInColumn += 1;
        }

        return numRowsInColumn;
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
    private void writeColumns(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
    
        ViewableDataParams viewableDataParams = getViewableDataParams(context, component);
        int firstRowToDisplay = viewableDataParams.getFirstRow();
        int totalRowCount = viewableDataParams.getTotalRows();
    
        int rowsDisplayed = 0;
        for (int j = 0; j < numColumns; j++) {
            writer.startElement(HTML.TD_ELEM, component);
            writer.startElement(HTML.TABLE_ELEM, component);
    
            int columnSize = calculateNumRowsInColumn(totalRowCount - rowsDisplayed, j + 1);
            int firstRowInColumn = rowsDisplayed + firstRowToDisplay;
            int lastRowInColumn = rowsDisplayed + firstRowToDisplay + columnSize - 1;
            if (lastRowInColumn > totalRowCount - 1) {
                lastRowInColumn = totalRowCount - 1;
            }
            for (int i = firstRowInColumn; i <= lastRowInColumn; i++) {                
                writeRow(context, component, i);
                rowsDisplayed++;
            }
    
            writer.endElement(HTML.TABLE_ELEM);
            writer.endElement(HTML.TD_ELEM);
        }
    }

}