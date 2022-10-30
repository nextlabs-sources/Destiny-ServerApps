/*
 * Created on Nov 8, 2005
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

/**
 * A Grid Layout Strategy for when the component contains no data
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/datagrid/EmptyGridLayoutStrategy.java#1 $
 */

public class EmptyGridLayoutStrategy extends BaseGridLayoutStrategy {

    /**
     * The message to display within the table if the data is empty
     */
    private static final String EMPTY_TABLE_MESSAGE_ATTR_NAME = "emptyTableMessage";
    private static final String DEFAULT_EMPTY_MESSAGE = "- No Records Found -";
    private static final Object DEFAULT_EMPTY_MESSAGE_CELL_STYLE_CLASS = "emptymessagetablerow";
    
    /**
     * @see com.bluejungle.destiny.webui.renderers.datagrid.IGridLayoutStrategy#encodeChildren(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        String emptyMessage = (String) component.getAttributes().get(EMPTY_TABLE_MESSAGE_ATTR_NAME);
        if (emptyMessage == null) {
            emptyMessage = DEFAULT_EMPTY_MESSAGE;
        }

        ResponseWriter writer = context.getResponseWriter();
        writer.startElement(HTML.TR_ELEM, component);
        writer.writeAttribute(HTML.CLASS_ATTR, DEFAULT_EMPTY_MESSAGE_CELL_STYLE_CLASS, null);
        writer.startElement(HTML.TD_ELEM, component);
        writer.write(emptyMessage);
        writer.endElement(HTML.TD_ELEM);
        writer.endElement(HTML.TR_ELEM);
    }

}
