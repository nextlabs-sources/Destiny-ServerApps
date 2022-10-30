/*
 * Created on Apr 3, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.webui.renderers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import com.bluejungle.destiny.webui.renderers.HtmlLinkRenderer;
import com.bluejungle.destiny.webui.renderers.helpers.link.ILinkBuilder;
import com.nextlabs.destiny.webui.renderers.helpers.link.PopupLinkBuilder;
import com.sun.faces.util.Util;


/**
 * This is the renderer for the popup link component, it is mostly the same
 * as a command link renderer, except that it does not do form submission.
 * 
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/nextlabs/destiny/webui/renderers/HtmlPopupLinkRenderer.java#1 $
 */

public class HtmlPopupLinkRenderer extends HtmlLinkRenderer {

    private static final String LOG_DETAIL_PARAM_NAME = "logDetail";
    private static final String LOG_ID_PARAM = "logId";
    
    /**
     * @see com.sun.faces.renderkit.html_basic.CommandLinkRenderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null) {
            throw new NullPointerException(Util.getExceptionMessageString(
                Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }

        UICommand command = (UICommand) component;

        // If the component is disabled, do not change the value of the
        // component, since its state cannot be changed.
        if (Util.componentIsDisabledOnReadonly(component)) {
            return;
        } 
    
        String clientId = command.getClientId(context);
        Map requestParameterMap = context.getExternalContext().getRequestParameterMap();
        String value = (String) requestParameterMap.get(LOG_DETAIL_PARAM_NAME);
        if (value == null || value.equals("")) {
            return;
        }
        ActionEvent actionEvent = new ActionEvent(component);
        component.queueEvent(actionEvent);

        return;
    }


    /**
     * @see com.sun.faces.renderkit.html_basic.CommandLinkRenderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        UICommand command = (UICommand) component;
        Map parameterMap = new HashMap();
        List children = command.getChildren();
        for (int i = 0; i < children.size(); i++){
            UIComponent childComponent = (UIComponent)children.get(i); 
            if (childComponent instanceof UIParameter){
                UIParameter param = (UIParameter)childComponent;
                parameterMap.put(param.getName(), new String[]{param.getValue().toString()});
            }
        }
        
        parameterMap.put(LOG_DETAIL_PARAM_NAME, new String[]{"true"});
        ILinkBuilder linkBuilder = getLinkBuilder();
        linkBuilder.setRequestParameters(parameterMap);
        linkBuilder.encodeLinkStart(context, component, false); 
    }


    /**
     * @see com.sun.faces.renderkit.html_basic.CommandLinkRenderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        
        UICommand command = (UICommand) component;
        ResponseWriter writer = context.getResponseWriter();
        
        // write in the CSS style class
        String styleClass = (String)
        command.getAttributes().get("styleClass");
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }
        
        // write in the target attribute
        String target = ((HtmlCommandLink) component).getTarget();
        if (target != null && target.trim().length() > 0) {
            writer.writeAttribute("target", target, null);
        }
        
        ILinkBuilder linkBuilder = getLinkBuilder();
        linkBuilder.encodeLinkEnd(context, component); 
    }

    /**
     * Retrieve the a link builder
     * 
     * @return a link builder to utilize for building html links
     */
    private ILinkBuilder getLinkBuilder() {
        return new PopupLinkBuilder();
    }
}
