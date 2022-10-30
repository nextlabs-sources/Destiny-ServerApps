/*
 * Created on Apr 1, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.jsfmock;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/jsfmock/MockViewHandler.java#1 $
 */

public class MockViewHandler extends ViewHandler {

    /**
     * Constructor
     * 
     */
    public MockViewHandler() {
        super();
    }

    /**
     * @see javax.faces.application.ViewHandler#calculateLocale(javax.faces.context.FacesContext)
     */
    public Locale calculateLocale(FacesContext context) {
        return Locale.ENGLISH;
    }

    /**
     * @see javax.faces.application.ViewHandler#calculateRenderKitId(javax.faces.context.FacesContext)
     */
    public String calculateRenderKitId(FacesContext context) {
        return null;
    }

    /**
     * @see javax.faces.application.ViewHandler#createView(javax.faces.context.FacesContext, java.lang.String)
     */
    public UIViewRoot createView(FacesContext context, String viewId) {
        return null;
    }

    /**
     * @see javax.faces.application.ViewHandler#getActionURL(javax.faces.context.FacesContext, java.lang.String)
     */
    public String getActionURL(FacesContext context, String viewId) {
        return null;
    }

    /**
     * @see javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext, java.lang.String)
     */
    public String getResourceURL(FacesContext context, String path) {
        return null;
    }

    /**
     * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
     */
    public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
    }

    /**
     * @see javax.faces.application.ViewHandler#restoreView(javax.faces.context.FacesContext, java.lang.String)
     */
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        return null;
    }

    /**
     * @see javax.faces.application.ViewHandler#writeState(javax.faces.context.FacesContext)
     */
    public void writeState(FacesContext context) throws IOException {
    }

}
