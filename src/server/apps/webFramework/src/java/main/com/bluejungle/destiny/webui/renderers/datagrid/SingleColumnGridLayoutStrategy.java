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
 * A Grid Layout Strategy which displays everything in a single column
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/datagrid/SingleColumnGridLayoutStrategy.java#1 $
 */
public class SingleColumnGridLayoutStrategy extends BaseGridLayoutStrategy {
    
    /**
     * @see com.bluejungle.destiny.webui.renderers.datagrid.IGridLayoutStrategy#encodeChildren(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        ViewableDataParams viewableDataParams = getViewableDataParams(context, component);

        for (int i = viewableDataParams.getFirstRow(); i <= viewableDataParams.getLastRow(); i++) {
            writeRow(context, component, i);
        }
    }    
}