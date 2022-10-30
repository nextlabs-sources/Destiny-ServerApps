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

/**
 * An abstract strategy which can be used as a base for strategies which render
 * the data in more than one column
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/datagrid/MulticolumnGridLayoutStrategy.java#1 $
 */
public abstract class MulticolumnGridLayoutStrategy extends BaseGridLayoutStrategy {

    /**
     * @see com.bluejungle.destiny.webui.renderers.datagrid.IGridLayoutStrategy#encodeBegin(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);

        ResponseWriter writer = context.getResponseWriter();
        writer.startElement(HTML.TR_ELEM, component);
    }

    /**
     * @see com.bluejungle.destiny.webui.renderers.datagrid.IGridLayoutStrategy#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement(HTML.TR_ELEM);
        super.encodeEnd(context, component);
    }
}