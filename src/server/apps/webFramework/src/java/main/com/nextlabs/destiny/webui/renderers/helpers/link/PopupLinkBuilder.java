/*
 * Created on Apr 16, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.webui.renderers.helpers.link;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.renderkit.html.HTML;

import com.bluejungle.destiny.webui.renderers.HtmlFormRenderer;
import com.bluejungle.destiny.webui.renderers.helpers.link.ILinkBuilder;


/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/nextlabs/destiny/webui/renderers/helpers/link/PopupLinkBuilder.java#1 $
 */

public class PopupLinkBuilder implements ILinkBuilder {

    private Map requestParameters;
    private boolean disabled = false;

    /**
     * Create an instance of SimpleLinkRendererDelegate
     *  
     */
    public PopupLinkBuilder() {
        super();
    }

    /**
     * @see com.bluejungle.destiny.webui.renderers.helpers.link.ILinkBuilder#setRequestParameters(java.util.Map)
     */
    public void setRequestParameters(Map parameters) {
        this.requestParameters = parameters;
    }

    /**
     * @see com.bluejungle.destiny.webui.renderers.helpers.link.ILinkBuilder#setDisabled(boolean)
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @throws IOException
     * @see com.bluejungle.destiny.webui.renderers.helpers.link.ILinkBuilder#encodeLinkStart(javax.faces.context.FacesContext)
     */
    public void encodeLinkStart(FacesContext facesContext, UIComponent component, boolean renderForm) throws IOException {
        if (renderForm){
            if (this.disabled) {
                writeDisabledLinkStart(facesContext, component);
            } else {
                UIForm parentForm = getParentForm(component);
                if (parentForm != null) {
                    writeNestedInForLinkStart(facesContext, component, parentForm);
                } else {
                    writeSimpleLinkStart(facesContext, component);
                }
            }
        } else {
            writeSimpleLinkStart(facesContext, component);
        }
    }

    /**
     * @param facesContext
     * @param component
     * @throws IOException
     */
    private void writeNestedInForLinkStart(FacesContext facesContext, UIComponent component, UIForm form) throws IOException {
        StringBuffer formNameBuffer = new StringBuffer("document.forms['");
        formNameBuffer.append(form.getClientId(facesContext));
        formNameBuffer.append("']");
        
        String formName = formNameBuffer.toString();

        StringBuffer onClick = new StringBuffer();

        if (this.requestParameters != null) {
            Iterator parameterIterator = this.requestParameters.entrySet().iterator();
            while (parameterIterator.hasNext()) {
                Map.Entry nextParameter = (Map.Entry) parameterIterator.next();
                String parameterName = (String) nextParameter.getKey();

                /*
                 * Note that in forms, we can only support one value at the
                 * moment
                 */
                String[] parameterValues = (String[]) nextParameter.getValue();
                String firstValue = parameterValues[0];
                onClick.append(formName);
                onClick.append(".elements['");
                onClick.append(parameterName);
                onClick.append("'].value='");
                onClick.append(firstValue);
                onClick.append("';");

                HtmlFormRenderer.addHiddenInputParameter(form, parameterName);
            }
        }

        // Add form submission bit
        onClick.append("if (");
        onClick.append(formName);
        onClick.append(".onsubmit) { var result=");
        onClick.append(formName);
        onClick.append(".onsubmit();  if ((typeof result == 'undefined') || result) { ");
        onClick.append(formName);
        onClick.append(".submit(); } } else {");
        onClick.append(formName);
        onClick.append(".submit(); } return false;");

        writeCommonHrefLinkStart(facesContext, component, "#");

        ResponseWriter writer = facesContext.getResponseWriter();
        writer.writeAttribute(HTML.ONCLICK_ATTR, onClick.toString(), null);
    }

    /**
     * @param facesContext
     * @param component
     * @throws IOException
     */
    private void writeSimpleLinkStart(FacesContext facesContext, UIComponent component) throws IOException {
        ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
        String viewId = facesContext.getViewRoot().getViewId();
        String path = viewHandler.getActionURL(facesContext, viewId);

        StringBuffer onClickBuf = new StringBuffer(path);

        if (path.indexOf('?') == -1) {
            onClickBuf.append('?');
        } else {
            onClickBuf.append('&');
        }

        if (this.requestParameters != null) {
            Iterator parameterIterator = this.requestParameters.entrySet().iterator();
            while (parameterIterator.hasNext()) {
                Map.Entry nextParameter = (Map.Entry) parameterIterator.next();
                String parameterName = (String) nextParameter.getKey();
                String[] parameterValues = (String[]) nextParameter.getValue();
                for (int i = 0; i < parameterValues.length; i++) {
                    onClickBuf.append(parameterName);
                    onClickBuf.append("=");
                    onClickBuf.append(parameterValues[i]);
                    onClickBuf.append('&');
                }
            }
        }

        // RLIN FIXME - should make the parameters to the window.open call configurable through the tag
        String processedOnClick = "disablehref();window.open('" + 
                                  onClickBuf.toString() + 
                                  "', '_blank', 'menubar=1,scrollbars=1,resizable=1,width=650,height=500');";
        String onClick = facesContext.getExternalContext().encodeActionURL(processedOnClick);

        writeCommonHrefLinkStart(facesContext, component, "javascript:void(0);");

        ResponseWriter writer = facesContext.getResponseWriter();
        writer.writeAttribute(HTML.ONCLICK_ATTR, onClick, null);
    }
    
    /**
     * @param facesContext
     * @param component
     * @param href
     * @param writer
     * @throws IOException
     */
    private void writeCommonHrefLinkStart(FacesContext facesContext, UIComponent component, String href) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement(HTML.ANCHOR_ELEM, component);
        writer.writeURIAttribute(HTML.HREF_ATTR, facesContext.getExternalContext().encodeActionURL(href), null);
    }
    
    /**
     * 
     * @param facesContext
     * @param component
     * @throws IOException
     */
    private void writeDisabledLinkStart(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement(HTML.ANCHOR_ELEM, component);
    }

    /**
     * @param component
     * @return
     */
    private UIForm getParentForm(UIComponent component) {
        UIComponent parent = component.getParent();
        while (parent != null && !(parent instanceof UIForm)) {
            parent = parent.getParent();
        }

        return (UIForm) parent;
    }

    /**
     * @throws IOException
     * @see com.bluejungle.destiny.webui.renderers.helpers.link.ILinkBuilder#encodeLinkEnd(javax.faces.context.FacesContext)
     */
    public void encodeLinkEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.endElement(HTML.ANCHOR_ELEM);
    }
}
