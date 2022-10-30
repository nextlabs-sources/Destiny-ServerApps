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
import org.apache.myfaces.renderkit.html.HtmlTextRendererBase;

/**
 * This is the renderer for JavaScript file inclusion.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/JSIncludeRenderer.java#1 $
 */

public class JSIncludeRenderer extends HtmlTextRendererBase {

    /**
     * Render a javascript inclusion tag in HTML.
     * 
     * For example : <script src="foo.js" type="text/javascript"> </script>
     * 
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        RendererUtils.checkParamValidity(facesContext, component, null);
        String jsLocation = RendererUtils.getStringValue(facesContext, component);
        if (jsLocation != null) {
            ResponseWriter writer = facesContext.getResponseWriter();
            writer.startElement(HTML.SCRIPT_ELEM, component);
            writer.writeAttribute(HTML.SCRIPT_LANGUAGE_ATTR, HTML.SCRIPT_LANGUAGE_JAVASCRIPT, null);
            writer.writeAttribute(HTML.SRC_ATTR, ContextUtil.getFullContextLocation(facesContext, jsLocation), null);
            writer.endElement(HTML.SCRIPT_ELEM);
        }
    }
}