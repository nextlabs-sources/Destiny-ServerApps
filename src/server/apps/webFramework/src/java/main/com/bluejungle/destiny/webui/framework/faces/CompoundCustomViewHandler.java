/*
 * Created on Apr 4, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import java.io.IOException;
import java.util.Locale;


/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/CompoundCustomViewHandler.java#1 $
 */

public class CompoundCustomViewHandler extends ViewHandler {
    private ViewHandler wrappedViewHandler;
        
    /**
     * Create an instance of CompoundCustomViewHandler
     * @param wrappedViewHandler
     */
    public CompoundCustomViewHandler(ViewHandler wrappedViewHandler) {
        this.wrappedViewHandler = new PrerenderActionViewHandler(new RequestParameterAllowingViewHandler(wrappedViewHandler));
    }

    /**
     * @see javax.faces.application.ViewHandler#calculateLocale(javax.faces.context.FacesContext)
     */
    @Override
    public Locale calculateLocale(FacesContext context) {
        return this.wrappedViewHandler.calculateLocale(context);
    }

    /**
     * @see javax.faces.application.ViewHandler#calculateRenderKitId(javax.faces.context.FacesContext)
     */
    @Override
    public String calculateRenderKitId(FacesContext context) {
        return this.wrappedViewHandler.calculateRenderKitId(context);
    }

    /**
     * @see javax.faces.application.ViewHandler#createView(javax.faces.context.FacesContext, java.lang.String)
     */
    @Override
    public UIViewRoot createView(FacesContext context, String viewId) {
        return this.wrappedViewHandler.createView(context, viewId);
    }

    /**
     * @see javax.faces.application.ViewHandler#getActionURL(javax.faces.context.FacesContext, java.lang.String)
     */
    @Override
    public String getActionURL(FacesContext context, String viewId) {
        return this.wrappedViewHandler.getActionURL(context, viewId);
    }


    /**
     * @see javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext, java.lang.String)
     */
    @Override
    public String getResourceURL(FacesContext context, String path) {
        return this.wrappedViewHandler.getResourceURL(context, path);
    }

    /**
     * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
     */
    @Override
    public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
        this.wrappedViewHandler.renderView(context, viewToRender);
    }

    /**
     * @see javax.faces.application.ViewHandler#restoreView(javax.faces.context.FacesContext, java.lang.String)
     */
    @Override
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        return this.wrappedViewHandler.restoreView(context, viewId);
    }

    /**
     * @see javax.faces.application.ViewHandler#writeState(javax.faces.context.FacesContext)
     */
    @Override
    public void writeState(FacesContext context) throws IOException {
        this.wrappedViewHandler.writeState(context);
    }
}
