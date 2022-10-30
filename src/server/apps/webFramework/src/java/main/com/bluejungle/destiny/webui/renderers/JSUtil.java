/*
 * Created on Mar 12, 2005
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
import org.apache.myfaces.renderkit.html.HtmlRendererUtils;

/**
 * This is a utility class that generates JS code for given components.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/JSUtil.java#1 $
 */

public class JSUtil {

    /**
     * Generates JavaScript in the body onload of the page to execute the
     * "stripe" function for a given component id.
     * 
     * @param context
     *            JSF context
     * @param writer
     *            writer object
     * @param component
     *            component that this fragment of JavaScript refers to
     * @param componentId
     *            id of the component on which the stripe should apply
     * @throws IOException
     *             if writing into the writer fails.
     */
    public static void generateStripeCode(FacesContext context, UIComponent component, ResponseWriter writer, String componentId) throws IOException {
        RendererUtils.checkParamValidity(context, component, null);
        
        writer.startElement(HTML.SCRIPT_ELEM, component);
        writer.writeAttribute(HTML.SCRIPT_LANGUAGE_ATTR, HTML.SCRIPT_LANGUAGE_JAVASCRIPT, null);
        HtmlRendererUtils.writePrettyLineSeparator(context);
        writer.write("addLoadEvent(function () {");
        HtmlRendererUtils.writePrettyLineSeparator(context);
        HtmlRendererUtils.writePrettyIndent(context);        
        writer.write("stripe ('" + componentId + "');");
        HtmlRendererUtils.writePrettyLineSeparator(context);
        writer.write("});");
        HtmlRendererUtils.writePrettyLineSeparator(context);
        writer.endElement(HTML.SCRIPT_ELEM);
    }
}