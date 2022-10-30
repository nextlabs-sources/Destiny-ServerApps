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
 * This is the CSS inclusion renderer class. This class generates HTML in order
 * to include a CSS stylesheet in the layout of the page.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/CSSIncludeRenderer.java#1 $
 */

public class CSSIncludeRenderer extends HtmlTextRendererBase {

    protected static final String HTML_LINK = "LINK";
    protected static final String HTML_REL_ATTR = "REL";
    protected static final String HTML_REL_VALUE = "StyleSheet";
    protected static final String HTML_TEXT_CSS_TYPE = "text/css";

    /**
     * Render a CSS inclusion tag in HTML.
     * 
     * For example : <LINK REL=StyleSheet HREF="destiny.css" TYPE="text/css">
     * 
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        RendererUtils.checkParamValidity(facesContext, component, null);
        String cssLocation = RendererUtils.getStringValue(facesContext, component);
        if (cssLocation != null) {
            ResponseWriter writer = facesContext.getResponseWriter();
            writer.startElement(HTML_LINK, component);
            writer.writeAttribute(HTML_REL_ATTR, HTML_REL_VALUE, null);
            writer.writeAttribute(HTML.HREF_ATTR, ContextUtil.getFullContextLocation(facesContext, cssLocation), null);
            writer.writeAttribute(HTML.TYPE_ATTR, HTML_TEXT_CSS_TYPE, null);
            writer.endElement(HTML_LINK);
        }
    }
}