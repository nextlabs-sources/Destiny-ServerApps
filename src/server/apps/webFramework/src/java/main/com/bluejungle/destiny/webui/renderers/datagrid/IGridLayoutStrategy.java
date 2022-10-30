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

/**
 * Instances of IGridLayourStrategy provide a strategy for rending a data grid
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/datagrid/IGridLayoutStrategy.java#1 $
 */

public interface IGridLayoutStrategy {

    /**
     * Render the beginning of the data grid.
     * 
     * @param context
     * @param component
     * @throws IOException
     */
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException;

    /**
     * Render the children of the data grid.
     * 
     * @param context
     * @param component
     * @throws IOException
     */
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException;

    /**
     * Render the end of the data grid.
     * 
     * @param context
     * @param component
     * @throws IOException
     */
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException;
}