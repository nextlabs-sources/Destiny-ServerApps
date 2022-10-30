/*
 * Created on Apr 4, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

import java.io.IOException;
import java.util.Locale;

/**
 * A custom view handler which allows request parameters to be appended to the
 * view id when serving a jsf page.  
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/RequestParameterAllowingViewHandler.java#1 $
 */

public class RequestParameterAllowingViewHandler extends ViewHandler {

    private static final String QUERY_STRING_INDICATOR = "?";

    private ViewHandler wrappedViewHandler;

    /**
     * Create an instance of RequestParameterAllowingViewHandler
     * 
     * @param wrappedViewHandler
     */
    public RequestParameterAllowingViewHandler(ViewHandler wrappedViewHandler) {
        this.wrappedViewHandler = wrappedViewHandler;
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
     * @see javax.faces.application.ViewHandler#createView(javax.faces.context.FacesContext,
     *      java.lang.String)
     */
    @Override
    public UIViewRoot createView(FacesContext context, String viewId) {
        return this.wrappedViewHandler.createView(context, viewId);
    }

    /**
     * @see javax.faces.application.ViewHandler#getActionURL(javax.faces.context.FacesContext,
     *      java.lang.String)
     */
    @Override
    public String getActionURL(FacesContext context, String viewId) {
        String viewIdImpl = applyValueBinding(context, viewId);
        int indexOfQueryString = viewIdImpl.indexOf(QUERY_STRING_INDICATOR);
        String queryString = "";
        if (indexOfQueryString > -1) {
            queryString = viewIdImpl.substring(indexOfQueryString);
        }

        String actionUrl = this.wrappedViewHandler.getActionURL(context, viewIdImpl);

        return actionUrl + queryString;
    }

    /**
     * @see javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext,
     *      java.lang.String)
     */
    @Override
    public String getResourceURL(FacesContext context, String path) {
        String pathImpl = applyValueBinding(context, path);

        return this.wrappedViewHandler.getResourceURL(context, pathImpl);
    }

    /**
     * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext,
     *      javax.faces.component.UIViewRoot)
     */
    @Override
    public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
        try {
    		this.wrappedViewHandler.renderView(context, viewToRender);
        } catch(IllegalStateException illegal) {
        	// Silent this exception where session may no longer exist
        }
    }

    /**
     * @see javax.faces.application.ViewHandler#restoreView(javax.faces.context.FacesContext,
     *      java.lang.String)
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

    /**
     * Replace the value binding with the value in the specific string
     * 
     * @param context
     *            the current FacesContext
     * @param stringToModify
     *            the string to tranform
     * @return the transformed string with the value bindings replaced
     */
    private String applyValueBinding(FacesContext context, String stringToModify) {
        if (UIComponentTag.isValueReference(stringToModify)) {
            Application application = context.getApplication();
            ValueBinding vb = application.createValueBinding(stringToModify);

            stringToModify = (String) vb.getValue(context);
        }

        return stringToModify;
    }
}
