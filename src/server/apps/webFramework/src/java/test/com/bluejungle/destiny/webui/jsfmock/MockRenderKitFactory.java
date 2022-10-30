/*
 * Created on Mar 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.jsfmock;

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

/**
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/jsfmock/MockRenderKitFactory.java#1 $
 */

public class MockRenderKitFactory extends RenderKitFactory {

    /**
     * @see javax.faces.render.RenderKitFactory#addRenderKit(java.lang.String,
     *      javax.faces.render.RenderKit)
     */
    public void addRenderKit(String arg0, RenderKit arg1) {
    }

    /**
     * @see javax.faces.render.RenderKitFactory#getRenderKit(javax.faces.context.FacesContext,
     *      java.lang.String)
     */
    public RenderKit getRenderKit(FacesContext facesContext, String rKitName) {
        return new MockRenderKit();
    }

    /**
     * @see javax.faces.render.RenderKitFactory#getRenderKitIds()
     */
    public Iterator getRenderKitIds() {
        return null;
    }

}