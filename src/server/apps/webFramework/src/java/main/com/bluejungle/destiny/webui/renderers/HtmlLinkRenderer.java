/*
 * Created on Nov 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import org.apache.myfaces.config.MyfacesConfig;
import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.renderkit.html.HtmlFormRendererBase;
import org.apache.myfaces.renderkit.html.HtmlRendererUtils;
import org.apache.myfaces.renderkit.html.util.DummyFormResponseWriter;
import org.apache.myfaces.renderkit.html.util.DummyFormUtils;
import org.apache.myfaces.renderkit.html.util.JavascriptUtils;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import java.io.IOException;
import java.util.Iterator;

/**
 * Temporary implementation of an Html Link Renderer. Used to workaround a bug
 * in MyFaces v1.0.9
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/HtmlLinkRenderer.java#1 $
 */

public class HtmlLinkRenderer extends org.apache.myfaces.renderkit.html.ext.HtmlLinkRenderer {

    /**
     * @see org.apache.myfaces.renderkit.html.HtmlLinkRendererBase#renderJavaScriptAnchorStart(javax.faces.context.FacesContext,
     *      javax.faces.context.ResponseWriter,
     *      javax.faces.component.UIComponent, java.lang.String)
     */
    protected void renderJavaScriptAnchorStart(FacesContext facesContext, ResponseWriter writer, UIComponent component, String clientId) throws IOException {
        // Find form
        UIComponent parent = component.getParent();
        while (parent != null && !(parent instanceof UIForm)) {
            parent = parent.getParent();
        }

        UIForm nestingForm = null;
        String formName;
        DummyFormResponseWriter dummyFormResponseWriter;
        if (parent != null) {
            // link is nested inside a form
            nestingForm = (UIForm) parent;
            formName = nestingForm.getClientId(facesContext);
            dummyFormResponseWriter = null;
        } else {
            // not nested in form, we must add a dummy form at the end of the
            // document
            formName = DummyFormUtils.DUMMY_FORM_NAME;
            dummyFormResponseWriter = DummyFormUtils.getDummyFormResponseWriter(facesContext);
            dummyFormResponseWriter.setWriteDummyForm(true);
        }

        StringBuffer onClick = new StringBuffer();

        String commandOnclick;
        if (component instanceof HtmlCommandLink) {
            commandOnclick = ((HtmlCommandLink) component).getOnclick();
        } else {
            commandOnclick = (String) component.getAttributes().get(HTML.ONCLICK_ATTR);
        }
        if (commandOnclick != null) {
            onClick.append(commandOnclick);
            onClick.append(';');
        }

        // call the clear_<formName> method
        onClick.append(HtmlRendererUtils.getClearHiddenCommandFormParamsFunctionName(formName)).append("();");

        String jsForm = "document.forms['" + formName + "']";

        if (MyfacesConfig.getCurrentInstance(facesContext.getExternalContext()).isAutoScroll()) {
            JavascriptUtils.appendAutoScrollAssignment(onClick, formName);
        }

        // add id parameter for decode
        String hiddenFieldName = HtmlRendererUtils.getHiddenCommandLinkFieldName(formName);
        onClick.append(jsForm);
        onClick.append(".elements['").append(hiddenFieldName).append("']");
        onClick.append(".value='").append(clientId).append("';");
        if (nestingForm != null) {
            HtmlFormRendererBase.addHiddenCommandParameter(nestingForm, hiddenFieldName);
        } else {
            dummyFormResponseWriter.addDummyFormParameter(hiddenFieldName);
        }

        // add child parameters
        for (Iterator it = component.getChildren().iterator(); it.hasNext();) {
            UIComponent child = (UIComponent) it.next();
            if (child instanceof UIParameter) {
                String name = ((UIParameter) child).getName();
                Object value = ((UIParameter) child).getValue();

                renderLinkParameter(dummyFormResponseWriter, name, value, onClick, jsForm, nestingForm);
            }
        }

        // target
        String target = ((HtmlCommandLink) component).getTarget();
        if (target != null && target.trim().length() > 0) {
            onClick.append(jsForm);
            onClick.append(".target='");
            onClick.append(target);
            onClick.append("';");
        }

        // onSubmit - Here's the bug we want to workaround. The original version
        // ignores the onsubmit return value
        // onClick.append("if("+jsForm+".onsubmit){"+jsForm+".onsubmit();}");
        onClick.append("if(").append(jsForm).append(".onsubmit){var result=").append(jsForm).append(".onsubmit();  if( (typeof result == 'undefined') || result ) {" + jsForm + ".submit();}}else{");

        // submit
        onClick.append(jsForm);
        onClick.append(".submit();}return false;"); // return false, so that
                                                    // browser does not handle
                                                    // the click

        writer.startElement(HTML.ANCHOR_ELEM, component);
        writer.writeURIAttribute(HTML.HREF_ATTR, "#", null);
        writer.writeAttribute(HTML.ONCLICK_ATTR, onClick.toString(), null);
    }

    private void renderLinkParameter(DummyFormResponseWriter dummyFormResponseWriter, String name, Object value, StringBuffer onClick, String jsForm, UIForm nestingForm) {
        if (name == null) {
            throw new IllegalArgumentException("Unnamed parameter value not allowed within command link.");
        }
        onClick.append(jsForm);
        onClick.append(".elements['").append(name).append("']");
        // UIParameter is no ValueHolder, so no conversion possible
        String strParamValue = value != null ? value.toString() : ""; // TODO:
                                                                        // Use
                                                                        // Converter?
        onClick.append(".value='").append(strParamValue).append("';");

        if (nestingForm != null) {
            // renderHiddenParam(writer, name);
            HtmlFormRendererBase.addHiddenCommandParameter(nestingForm, name);
        } else {
            dummyFormResponseWriter.addDummyFormParameter(name);
        }
    }
}
