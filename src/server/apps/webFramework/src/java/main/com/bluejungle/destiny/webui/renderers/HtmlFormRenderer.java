/*
 * Created on Mar 22, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.renderkit.RendererUtils;
import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.renderkit.html.HtmlFormRendererBase;
import org.apache.myfaces.renderkit.html.HtmlRendererUtils;

/**
 * This is the Html form renderer class. It extends the basic form renderer from
 * myFaces and allows using forms either with a GET or a POST.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/HtmlFormRenderer.java#3 $
 */

public class HtmlFormRenderer extends HtmlFormRendererBase {

    protected static final String POST_METHOD = "POST";
    private static final String INPUT_TO_FOCUS_ATTR_NAME = "inputToFocus";

    /**
     * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        RendererUtils.checkParamValidity(facesContext, component, UIForm.class);

        UIForm htmlForm = (UIForm) component;
        ResponseWriter writer = facesContext.getResponseWriter();
        ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
        String viewId = facesContext.getViewRoot().getViewId();
        String clientId = htmlForm.getClientId(facesContext);
        String actionURL = viewHandler.getActionURL(facesContext, viewId);

        writer.startElement(HTML.FORM_ELEM, htmlForm);
        writer.writeAttribute(HTML.ID_ATTR, clientId, null);
        writer.writeAttribute(HTML.NAME_ATTR, clientId, null);
        String method = (String) component.getAttributes().get(HTML.METHOD_ATTR);
        if (method == null) {
            method = POST_METHOD;
        }
        writer.writeAttribute(HTML.METHOD_ATTR, method, null);
        writer.writeURIAttribute(HTML.ACTION_ATTR, facesContext.getExternalContext().encodeActionURL(actionURL), null);

        String shouldSubmitJSFunctionName = getShouldSubmitJSFunction(facesContext, component);
        component.getAttributes().put(HTML.ONSUMBIT_ATTR, "return " + shouldSubmitJSFunctionName + "();");

        HtmlRendererUtils.renderHTMLAttributes(writer, htmlForm, HTML.FORM_PASSTHROUGH_ATTRIBUTES);
    }

    /**
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        /*
         * Write standard form end
         */
        super.encodeEnd(facesContext, component);

        ResponseWriter writer = facesContext.getResponseWriter();

        /*
         * Write onload javascript, if necessary
         */
        String inputToFocus = (String) component.getAttributes().get(INPUT_TO_FOCUS_ATTR_NAME);
        if (inputToFocus != null) {
            UIComponent componentToFocus = component.findComponent(inputToFocus);
            if (componentToFocus != null) {
                writer.startElement(HTML.SCRIPT_ELEM, component);
                writer.writeAttribute(HTML.SCRIPT_LANGUAGE_ATTR, HTML.SCRIPT_LANGUAGE_JAVASCRIPT, null);
                HtmlRendererUtils.writePrettyLineSeparator(facesContext);
                writer.write("addLoadEvent(function () {");
                HtmlRendererUtils.writePrettyLineSeparator(facesContext);
                HtmlRendererUtils.writePrettyIndent(facesContext);
                writer.write("this.document.getElementById(\'");
                writer.write(componentToFocus.getClientId(facesContext));
                writer.write("\').focus();");
                HtmlRendererUtils.writePrettyLineSeparator(facesContext);
                writer.write("});");
                HtmlRendererUtils.writePrettyLineSeparator(facesContext);
                writer.endElement(HTML.SCRIPT_ELEM);
            }
        }

        /*
         * Write onsubmit javascript
         */
        String jsNamespaceToken = getJSNamespaceToken(facesContext, component);
        String submittedCountVariableName = "submitted_" + jsNamespaceToken;
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        writer.startElement(HTML.SCRIPT_ELEM, component);
        writer.writeAttribute(HTML.SCRIPT_LANGUAGE_ATTR, HTML.SCRIPT_LANGUAGE_JAVASCRIPT, null);
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        writer.write("var ");
        writer.write(submittedCountVariableName);
        writer.write(" = 0;");
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        writer.write("function ");
        writer.write(getShouldSubmitJSFunction(facesContext, component));
        writer.write("() {");
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        HtmlRendererUtils.writePrettyIndent(facesContext);
        writer.write("var shouldSubmit = false;");
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        HtmlRendererUtils.writePrettyIndent(facesContext);
        writer.write("if (");
        writer.write(submittedCountVariableName);
        writer.write(" == 0) {");
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        HtmlRendererUtils.writePrettyIndent(facesContext);
        HtmlRendererUtils.writePrettyIndent(facesContext);
        writer.write("shouldSubmit = true;");
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        HtmlRendererUtils.writePrettyIndent(facesContext);
        HtmlRendererUtils.writePrettyIndent(facesContext);
        writer.write(submittedCountVariableName);
        writer.write(" = 1;");
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        HtmlRendererUtils.writePrettyIndent(facesContext);
        writer.write("}");
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        HtmlRendererUtils.writePrettyIndent(facesContext);
        writer.write("return shouldSubmit;");
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        writer.write("}");
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        writer.endElement(HTML.SCRIPT_ELEM);
    }

    public static void addHiddenInputParameter(UIForm form, String paramName) {
        HtmlFormRendererBase.addHiddenCommandParameter(form, paramName);
    }

    /**
     * @param facesContext
     * @param component TODO
     * @return
     */
    private String getShouldSubmitJSFunction(FacesContext facesContext, UIComponent component) {
        String jsNamespaceToken = getJSNamespaceToken(facesContext, component);
        return "shouldSubmit_" + jsNamespaceToken;
    }
    
    /**
     * Retrieve the Javascript namespace token
     * 
     * @param facesContext
     * @param component
     * @return a javascript namespace token
     */
    private String getJSNamespaceToken(FacesContext facesContext, UIComponent component) {
        String formClientId = component.getClientId(facesContext);
        return formClientId.replace(':', '_');
    }
}