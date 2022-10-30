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
import javax.faces.render.Renderer;

import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;

/**
 * JSf Renderer which renders a UIData component in a grid layout format. There
 * are three ways in which the grid can be formatted dependent upon the number
 * of row in the associated DataModel: <br/><br />
 * x <= 10 - Displayed in a single column <br />
 * 10 < x <= 25 Displayed in columns of 5 or less. Once a column of 5 is
 * complete, another is started <br />
 * 25 < x Displayed in 5 equally dispersed columns
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/datagrid/HtmlDataGridRenderer.java#1 $
 */
public class HtmlDataGridRenderer extends Renderer {

    private IGridLayoutStrategoryFactory gridLayoutFactory;

    /**
     * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        IGridLayoutStrategy layoutStrategy = getGridLayoutStrategy(context, component);
        layoutStrategy.encodeBegin(context, component);
    }

    /**
     * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        IGridLayoutStrategy layoutStrategy = getGridLayoutStrategy(context, component);
        layoutStrategy.encodeChildren(context, component);
    }

    /**
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        IGridLayoutStrategy layoutStrategy = getGridLayoutStrategy(context, component);
        layoutStrategy.encodeEnd(context, component);
    }

    /**
     * @see javax.faces.render.Renderer#getRendersChildren()
     */
    public boolean getRendersChildren() {
        return true;
    }

    /**
     * Retrieve the grid layout strategy to use to render the specified
     * component
     * 
     * @param context
     * @param component
     * @return
     */
    private IGridLayoutStrategy getGridLayoutStrategy(FacesContext context, UIComponent component) {
        IGridLayoutStrategoryFactory strategyFactory = getGridLayoutStrategyFactory();
        return strategyFactory.getGridLayoutStrategy(context, component);
    }

    /**
     * Retrieve the grid layout strategy factory
     * 
     * @return
     */
    private IGridLayoutStrategoryFactory getGridLayoutStrategyFactory() {
        if (this.gridLayoutFactory == null) {
            IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
            this.gridLayoutFactory = (IGridLayoutStrategoryFactory) componentManager.getComponent(DefaultGridLayoutStrategyFactory.class);
        }

        return this.gridLayoutFactory;
    }
}