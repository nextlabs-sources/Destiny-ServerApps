/*
 * Created on May 4, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers.datagrid;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Instances of IGridLayoutStrategoryFactory provide a means of obtaining a
 * concrete IGridLayoutStrategy
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/datagrid/IGridLayoutStrategoryFactory.java#1 $
 */

public interface IGridLayoutStrategoryFactory {

    /**
     * @param context
     * @param component
     * @return
     */
    IGridLayoutStrategy getGridLayoutStrategy(FacesContext context, UIComponent component);

}