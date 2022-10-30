/*
 * Created on Mar 15, 2005
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

import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.renderkit.html.ext.HtmlTextRenderer;

import com.bluejungle.destiny.webui.controls.UINumberSelector;

/**
 * This is the renderer class for the number selector. It takes an
 * UINumberSelector control and renders an input text and adds up and down
 * arrows triggering JavaScript to modify the number directly on the client
 * side.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/HtmlNumberSelectorRenderer.java#1 $
 */

public class HtmlNumberSelectorRenderer extends HtmlTextRenderer {

    protected static final String BUTTON_DIV_ID = "_btn";

    /**
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        super.encodeEnd(facesContext, component);

        //TODO - adapt the renderer after number selector is done

        /*
         * ResponseWriter writer = facesContext.getResponseWriter(); String
         * inputId = component.getClientId(facesContext);
         * writer.startElement(HTML.DIV_ELEM, component);
         * writer.writeAttribute(HTML.ID_ATTR, getButtonsDivName(facesContext,
         * component), null); writer.writeAttribute(HTML.CLASS_ATTR,
         * "nbSelectorButtonsContainer", null);
         * 
         * writer.startElement(HTML.DIV_ELEM, component);
         * writer.writeAttribute(HTML.ONCLICK_ATTR, "alert('up')", null);
         * writer.writeAttribute(HTML.CLASS_ATTR, "nbSelectorButton", null);
         * writer.startElement(HTML.ANCHOR_ELEM, component);
         * writer.startElement(HTML.IMG_ELEM, component);
         * writer.writeAttribute(HTML.CLASS_ATTR, "nbSelectorImage", null);
         * writer.writeAttribute(HTML.SRC_ATTR,
         * "/inquiry/numberselector/images/up.gif", null);
         * writer.endElement(HTML.IMG_ELEM);
         * writer.endElement(HTML.ANCHOR_ELEM);
         * writer.endElement(HTML.DIV_ELEM);
         * 
         * writer.startElement(HTML.DIV_ELEM, component);
         * writer.writeAttribute(HTML.ONCLICK_ATTR, "alert('down')", null);
         * writer.writeAttribute(HTML.CLASS_ATTR, "nbSelectorButton", null);
         * writer.startElement(HTML.ANCHOR_ELEM, component);
         * writer.startElement(HTML.IMG_ELEM, component);
         * writer.writeAttribute(HTML.CLASS_ATTR, "nbSelectorImage", null);
         * writer.writeAttribute(HTML.SRC_ATTR,
         * "/inquiry/numberselector/images/down.gif", null);
         * writer.endElement(HTML.IMG_ELEM);
         * writer.endElement(HTML.ANCHOR_ELEM);
         * writer.endElement(HTML.DIV_ELEM);
         * 
         * writer.endElement(HTML.DIV_ELEM);
         */

        //encodeIncrementImage(facesContext, component);
        //encodeDecrementImage(facesContext, component);
    }

    /**
     * Renders the decrement image next to the input field
     * 
     * @param facesContext
     *            JSF context
     * @param component
     *            JSF UI component in which the increment button is rendered
     * @throws IOException
     *             if writing to the response writer fails.
     */
    protected void encodeDecrementImage(FacesContext facesContext, UIComponent component) throws IOException {
        if (!(component instanceof UINumberSelector)) {
            throw new IllegalArgumentException("Component must be a number selector component");
        }
        UINumberSelector nbSelector = (UINumberSelector) component;
        String className = (String) nbSelector.getAttributes().get(UINumberSelector.DECREMENT_LINK_CLASSNAME);
        encodeImage(facesContext, component, className, "alert('down');");
    }

    /**
     * Renders the increment image next to the input field
     * 
     * @param facesContext
     *            JSF context
     * @param component
     *            JSF UI component in which the increment button is rendered
     * @throws IOException
     *             if writing to the response writer fails.
     */
    protected void encodeIncrementImage(FacesContext facesContext, UIComponent component) throws IOException {
        if (!(component instanceof UINumberSelector)) {
            throw new IllegalArgumentException("Component must be a number selector component");
        }
        UINumberSelector nbSelector = (UINumberSelector) component;
        String className = (String) nbSelector.getAttributes().get(UINumberSelector.INCREMENT_LINK_CLASSNAME);
        encodeImage(facesContext, component, className, "alert('up');");
    }

    /**
     * Renders an image that can be clicked and calls a javascript handler
     * 
     * @param facesContext
     *            JSF context
     * @param component
     *            component in which the image is rendered
     * @param divClassName
     *            name of the div class name to use
     * @param onClickHandler
     *            JavaScript compliant expression to be called when the image
     *            gets clicked
     * @throws IOException
     *             if writing into the response writer fails.
     */
    protected void encodeImage(FacesContext facesContext, UIComponent component, String divClassName, String onClickHandler) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String inputId = component.getClientId(facesContext);
        writer.startElement(HTML.SPAN_ELEM, component);
        writer.writeAttribute(HTML.ID_ATTR, getButtonsDivName(facesContext, component), null);
        writer.writeAttribute(HTML.CLASS_ATTR, divClassName, null);
        writer.writeAttribute(HTML.ONCLICK_ATTR, onClickHandler, null);
        writer.endElement(HTML.SPAN_ELEM);
    }

    /**
     * Returns the name of the div element that includes contains the increment /
     * decrement buttons
     * 
     * @param facesContext
     *            JSF context
     * @param component
     *            JSF UI component on which this function tries to attach button
     * @return name of the DIV for the increment / decrement buttons
     */
    private String getButtonsDivName(FacesContext facesContext, UIComponent component) {
        if (component == null) {
            throw new NullPointerException("component cannot be null");
        }
        return (component.getClientId(facesContext) + BUTTON_DIV_ID);
    }
}