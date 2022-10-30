/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.renderkit.RendererUtils;
import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.renderkit.html.HtmlTextRendererBase;

import com.bluejungle.destiny.webui.controls.UIBarItem;

/**
 * This is the renderer class for the bar item control. It renderers a histogram
 * bar using a div technique. A sample of the output is given below:
 * <blockquote><div class="barcontainer"> <div class="bar" style="height:
 * 11px;"> </div><a href="#" class="barlink"> </a> </div> </blockquote>
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/branch/Destiny_112/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/BarItemRenderer.java#1 $
 */

public class BarItemRenderer extends HtmlTextRendererBase {

    /**
     * Default class name to use for the bar class
     */
    protected static final String DEFAULT_BAR_CLASS_NAME = "bar";

    /**
     * Default class name to use for the container class
     */
    protected static final String DEFAULT_CONTAINER_CLASS_NAME = "barcontainer";

    /**
     * Default maximum bar size in pixel
     */
    protected static final long DEFAULT_MAX_BAR_SIZE = 100;

    /**
     * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        RendererUtils.checkParamValidity(facesContext, component, null);
        String containerClassName = (String) component.getAttributes().get(UIBarItem.CONTAINER_CLASS_ATTR_NAME);
        if (containerClassName == null) {
            containerClassName = DEFAULT_CONTAINER_CLASS_NAME;
        }

        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement(HTML.DIV_ELEM, component);
        writer.writeAttribute(HTML.CLASS_ATTR, containerClassName, null);
        renderBarItem(facesContext, component);
    }

    /**
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        RendererUtils.checkParamValidity(facesContext, component, null);
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.endElement(HTML.DIV_ELEM);
    }

    /**
     * This renderer does not render its own children
     * 
     * @see javax.faces.render.Renderer#getRendersChildren()
     */
    public boolean getRendersChildren() {
        return false;
    }

    /**
     * Returns the bar class name to use for the bar to render
     * 
     * @param component
     *            UI component to use
     * @return the bar class name to use
     */
    protected String getBarClassName(final UIComponent component) {
        if (component == null) {
            throw new NullPointerException("Component cannot be null");
        }
        String result = (String) component.getAttributes().get(UIBarItem.BAR_CLASS_ATTR_NAME);
        if (result == null) {
            result = DEFAULT_BAR_CLASS_NAME;
        }
        return result;
    }

    /**
     * Returns the maximum bar size, in pixels
     * 
     * @param component
     *            UI component to use
     * @return the maximum bar size, in pixels
     */
    protected long getMaxBarSize(final UIComponent component) {
        if (component == null) {
            throw new NullPointerException("Component cannot be null");
        }
        Integer maxBarSize = (Integer) component.getAttributes().get(UIBarItem.MAX_BAR_SIZE);
        long lMaxBarSize = DEFAULT_MAX_BAR_SIZE;
        if (maxBarSize != null) {
            lMaxBarSize = maxBarSize.longValue();
        }
        return lMaxBarSize;
    }

    /**
     * Returns a style expression based on the component attributes. The style
     * expression is created based on the orientation and the size of the range
     * to use in the component.
     * 
     * @param facesContext
     *            JSF context to use
     * @param component
     *            UI component containing the bar item information
     * @return an HTML style expression
     */
    protected String getStyleExpression(FacesContext facesContext, UIComponent component) {
        RendererUtils.checkParamValidity(facesContext, component, null);

        String orientation = (String) component.getAttributes().get(UIBarItem.ORIENTATION_ATTR_NAME);
        if (UIBarItem.ORIENTATION_HOR.equals(orientation)) {
            orientation = HTML.WIDTH_ATTR;
        } else {
            orientation = HTML.HEIGHT_ATTR;
        }

        Long maxRange = (Long) component.getAttributes().get(UIBarItem.MAX_RANGE_ATTR_NAME);
        long maxBarSize = getMaxBarSize(component);
        String value = RendererUtils.getStringValue(facesContext, component);
        long lValue = (new Integer(value)).longValue();
        //If range values are invalid, draw a 0 px bar
        long lMaxRange = maxRange.longValue();
        long size = (lValue * maxBarSize) / lMaxRange;
        if (size > maxBarSize) {
            //Should never happen, but in case..
            size = maxBarSize;
        }
        //To make sure the bar is always rendered
        if (size == 0) {
            size = 1;
        }
        String result = orientation + ": " + size + "px;";
        return result;
    }

    /**
     * This function renders an actual bar item. It invokes the appropriate code
     * to calculate the size of the bar, and uses the orientation attribute to
     * display the bar the right way.
     * 
     * @param facesContext
     *            JSF context to use
     * @param component
     *            UI component containing the bar item information
     * @throws IOException
     *             if writing to the output stream failed.
     */
    public void renderBarItem(FacesContext facesContext, UIComponent component) throws IOException {
        RendererUtils.checkParamValidity(facesContext, component, null);
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement(HTML.DIV_ELEM, component);
        writer.writeAttribute(HTML.CLASS_ATTR, getBarClassName(component), null);
        writer.writeAttribute(HTML.STYLE_ATTR, getStyleExpression(facesContext, component), null);
        writer.endElement(HTML.DIV_ELEM);
    }
}