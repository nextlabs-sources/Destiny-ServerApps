/*
 * Created on Mar 22, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.jsfmock;

import java.io.OutputStream;
import java.io.Writer;

import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;

import org.apache.myfaces.renderkit.html.ext.HtmlLinkRenderer;
import org.apache.myfaces.renderkit.html.ext.HtmlTextRenderer;

import com.bluejungle.destiny.webui.renderers.HtmlListScrollerRenderer;
import com.bluejungle.destiny.webui.renderers.HtmlTableRenderer;

/**
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/jsfmock/MockRenderKit.java#1 $
 */

public class MockRenderKit extends RenderKit {

    /**
     * @see javax.faces.render.RenderKit#addRenderer(java.lang.String,
     *      java.lang.String, javax.faces.render.Renderer)
     */
    public void addRenderer(String arg0, String arg1, Renderer arg2) {
    }

    /**
     * Returns some actual renderers. If new tests require new types, they have
     * to be added here.
     * 
     * @see javax.faces.render.RenderKit#getRenderer(java.lang.String,
     *      java.lang.String)
     */
    public Renderer getRenderer(String compFamily, String compType) {
        Renderer result = null;
        if ("javax.faces.Command".equals(compFamily) && "javax.faces.Link".equals(compType)) {
            result = new HtmlLinkRenderer();
        } else if ("javax.faces.Data".equals(compFamily) && "org.apache.myfaces.Table".equals(compType)) {
            result = new HtmlTableRenderer();
        } else if ("javax.faces.Panel".equals(compFamily) && "org.apache.myfaces.DataScroller".equals(compType)) {
            result = new HtmlListScrollerRenderer();
        } else if ("javax.faces.Output".equals(compFamily) && "javax.faces.Text".equals(compType)) {
            result = new HtmlTextRenderer();
        }
        return result;
    }

    /**
     * @see javax.faces.render.RenderKit#getResponseStateManager()
     */
    public ResponseStateManager getResponseStateManager() {
        return null;
    }

    /**
     * @see javax.faces.render.RenderKit#createResponseWriter(java.io.Writer,
     *      java.lang.String, java.lang.String)
     */
    public ResponseWriter createResponseWriter(Writer arg0, String arg1, String arg2) {
        return null;
    }

    /**
     * @see javax.faces.render.RenderKit#createResponseStream(java.io.OutputStream)
     */
    public ResponseStream createResponseStream(OutputStream arg0) {
        return null;
    }

}