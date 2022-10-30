/*
 * Created on Apr 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.VariableResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PrerenderActionViewHandler is a custom View Handler which allows for logic to
 * be run at the beginning of the JSF Render View phase for a particular view
 * id. The logic to be run is registered through a Managed Bean of type
 * {@see PrerenderActionManagerBean}.<br />
 * <br />
 * The following is configuration example of the
 * {@see PrerenderActionManagerBean}Managed Bean: <br />
 * <br />
 * 
 * <pre>
 * 
 *   &lt;managed-bean&gt;
 *        &lt;description&gt;Managed Bean containing pre-render actions associated with particular views&lt;/description&gt;
 *        &lt;managed-bean-name&gt;PrerenderActionManagerBean&lt;/managed-bean-name&gt;
 *        &lt;managed-bean-class&gt;com.bluejungle.destiny.webui.framework.faces.PrerenderActionManagerBean&lt;/managed-bean-class&gt;
 *        &lt;managed-bean-scope&gt;application&lt;/managed-bean-scope&gt;
 *        &lt;managed-property&gt;
 *            &lt;description&gt;
 *                Map of viewid to pre-render actions
 *            &lt;/description&gt;
 *            &lt;property-name&gt;prerenderActions&lt;/property-name&gt;
 *            &lt;map-entries&gt;
 *                &lt;map-entry&gt;
 *                    &lt;key&gt;/agentconfig/agentConfigDesktop.jsp&lt;/key&gt;
 *                    &lt;value&gt;desktopAgentConfigurationBean.prerender&lt;/value&gt;
 *                &lt;/map-entry&gt;
 *            &lt;/map-entries&gt;
 *        &lt;/managed-property&gt;
 *        &lt;managed-property&gt;
 *            &lt;description&gt;
 *                Error page to display if a prerender action throws an exception
 *            &lt;/description&gt;
 *            &lt;property-name&gt;errorViewId&lt;/property-name&gt;
 *            &lt;value&gt;/core/jsp/error.jsp&lt;/value&gt;
 *        &lt;/managed-property&gt;
 *    &lt;/managed-bean&gt;
 *  
 * </pre>
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/PrerenderActionViewHandler.java#2 $
 */
public class PrerenderActionViewHandler extends ViewHandler {

    private static final Log LOG = LogFactory.getLog(PrerenderActionViewHandler.class.getName());

    public static final String ACTION_MANAGER_BEAN_NAME = "PrerenderActionManagerBean";

    private static final String JSF_FILE_EXTENSION = ".jsf";
    private static final String JSP_FILE_EXTENSION = ".jsp";

    private ViewHandler wrappedViewHandler;

    /**
     * Create an instance of PrerenderActionViewHandler
     * 
     * @param originalViewHandler
     */
    public PrerenderActionViewHandler(ViewHandler originalViewHandler) {
        this.wrappedViewHandler = originalViewHandler;
    }

    /**
     * @see javax.faces.application.ViewHandler#calculateLocale(javax.faces.context.FacesContext)
     */
    public Locale calculateLocale(FacesContext context) {
        return this.wrappedViewHandler.calculateLocale(context);
    }

    /**
     * @see javax.faces.application.ViewHandler#calculateRenderKitId(javax.faces.context.FacesContext)
     */
    public String calculateRenderKitId(FacesContext context) {
        return this.wrappedViewHandler.calculateRenderKitId(context);
    }

    /**
     * @see javax.faces.application.ViewHandler#createView(javax.faces.context.FacesContext,
     *      java.lang.String)
     */
    public UIViewRoot createView(FacesContext context, String viewId) {
        return this.wrappedViewHandler.createView(context, viewId);
    }

    /**
     * @see javax.faces.application.ViewHandler#getActionURL(javax.faces.context.FacesContext,
     *      java.lang.String)
     */
    public String getActionURL(FacesContext context, String viewId) {
        return this.wrappedViewHandler.getActionURL(context, viewId);
    }

    /**
     * @see javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext,
     *      java.lang.String)
     */
    public String getResourceURL(FacesContext context, String path) {
        return this.wrappedViewHandler.getResourceURL(context, path);
    }

    /**
     * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext,
     *      javax.faces.component.UIViewRoot)
     */
    public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
        PrerenderActionManagerBean actionManagerBean = getActionManagerBean(context);
        String viewId = viewToRender.getViewId();
        viewId = getActualViewId(context, viewId);
        if ((actionManagerBean != null) && (actionManagerBean.hasPrerenderAction(viewId))) {
            String prerenderAction = actionManagerBean.getPrerenderAction(viewId);
            try {
                invokePrerenderAction(context, prerenderAction);
            } catch (Exception exception) {
                // Log it
                StringBuffer errorMessage = new StringBuffer("Prerender action failed: ");
                errorMessage.append(prerenderAction);
                errorMessage.append(".");
                LOG.error(errorMessage.toString(), exception);

                String errorViewId = actionManagerBean.getErrorViewId();
                if (errorViewId != null) {
                    // FIX ME - Populate Exception
                    viewToRender.setViewId(errorViewId);
                }
            }
        }

        this.wrappedViewHandler.renderView(context, viewToRender);
    }

    /**
     * @see javax.faces.application.ViewHandler#restoreView(javax.faces.context.FacesContext,
     *      java.lang.String)
     */
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        return this.wrappedViewHandler.restoreView(context, viewId);
    }

    /**
     * @see javax.faces.application.ViewHandler#writeState(javax.faces.context.FacesContext)
     */
    public void writeState(FacesContext context) throws IOException {
        this.wrappedViewHandler.writeState(context);
    }

    /**
     * Retrieve the PrerenderActionManangerBean configured for the current Faces
     * Application
     * 
     * @param facesContext
     * @return the PrerenderActionManangerBean configured for the current Faces
     *         Application
     */
    private PrerenderActionManagerBean getActionManagerBean(FacesContext facesContext) {
        VariableResolver variableResolver = facesContext.getApplication().getVariableResolver();
        return (PrerenderActionManagerBean) variableResolver.resolveVariable(facesContext, ACTION_MANAGER_BEAN_NAME);
    }

    /**
     * Invoke the specified prerender action
     * 
     * @param context
     * @param prerenderAction
     */
    private void invokePrerenderAction(FacesContext context, String prerenderAction) {
        StringBuffer actionMethodBindingExpression = new StringBuffer("#{");
        actionMethodBindingExpression.append(prerenderAction.trim());
        actionMethodBindingExpression.append("}");

        MethodBinding actionToInvokeBinding = context.getApplication().createMethodBinding(actionMethodBindingExpression.toString(), null);
        actionToInvokeBinding.invoke(context, null);
    }

    /**
     * Retrieve the actual viewId for the specified view id (i.e. change jsf to
     * jsp extension if necessary
     * 
     * @param context
     * @param viewId
     * @return the actual viewId for the specified view id
     */
    private String getActualViewId(FacesContext context, String viewId) {
        String viewIdToReturn = viewId;

        /**
         * Note that I'm taking a major shortcut here. This is definitely not
         * according to spec, but it should always work in our application
         */
        if ((viewId != null) && (viewId.endsWith(JSF_FILE_EXTENSION))) {
            viewIdToReturn = replaceExtension(viewId, JSF_FILE_EXTENSION, JSP_FILE_EXTENSION);
        }

        return viewIdToReturn;
    }

    /**
     * Replace the extension of the provided url string
     * 
     * @param viewId
     * @param extensionToReplace
     * @param extensionToSubstitute
     * @return the url string with the extension replaced
     */
    private String replaceExtension(String stringToModify, String extensionToReplace, String extensionToSubstitute) {
        StringBuffer stringToReturnBuffer = new StringBuffer(stringToModify);
        int indexOfExtensionToReplace = stringToModify.indexOf(extensionToReplace);
        stringToReturnBuffer.replace(indexOfExtensionToReplace, indexOfExtensionToReplace + extensionToReplace.length(), extensionToSubstitute);

        return stringToReturnBuffer.toString();
    }
}