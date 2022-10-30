/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.apache.myfaces.renderkit.html.HTML;

import com.bluejungle.destiny.webui.controls.UIMessages;
import com.bluejungle.destiny.webui.framework.message.MessageManager;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * MessagesRenderer is an HTML specified renderer for rendering a
 * com.bluejungle.destiny.webui.controls.UIMessages component.
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/MessagesRenderer.java#1 $
 */
public class MessagesRenderer extends Renderer {

    private static final String DIV_SUMMARY_CLASS = "name";
    private static final String DIV_DETAIL_CLASS = "reason";

    private static final String FATAL_CLASS_ATT_NAME = "fatalStyleClass";
    private static final String ERROR_CLASS_ATT_NAME = "errorStyleClass";
    private static final String WARN_CLASS_ATT_NAME = "warnStyleClass";
    private static final String INFO_CLASS_ATT_NAME = "infoStyleClass";

    private static final String FATAL_CLASS_DEFAULT = "fatalMsg";
    private static final String ERROR_CLASS_DEFAULT = "errorMsg";
    private static final String WARN_CLASS_DEFAULT = "warnMsg";
    private static final String INFO_CLASS_DEFAULT = "infoMsg";

    private static final Map SEVERITY_TO_STYLE_CLASS_ATTR_MAP = new HashMap();
    static {
        SEVERITY_TO_STYLE_CLASS_ATTR_MAP.put(FacesMessage.SEVERITY_FATAL, FATAL_CLASS_ATT_NAME);
        SEVERITY_TO_STYLE_CLASS_ATTR_MAP.put(FacesMessage.SEVERITY_ERROR, ERROR_CLASS_ATT_NAME);
        SEVERITY_TO_STYLE_CLASS_ATTR_MAP.put(FacesMessage.SEVERITY_WARN, WARN_CLASS_ATT_NAME);
        SEVERITY_TO_STYLE_CLASS_ATTR_MAP.put(FacesMessage.SEVERITY_INFO, INFO_CLASS_ATT_NAME);
    }

    private static final Map SEVERITY_TO_STYLE_CLASS_DEFAULTS_MAP = new HashMap();
    static {
        SEVERITY_TO_STYLE_CLASS_DEFAULTS_MAP.put(FacesMessage.SEVERITY_FATAL, FATAL_CLASS_DEFAULT);
        SEVERITY_TO_STYLE_CLASS_DEFAULTS_MAP.put(FacesMessage.SEVERITY_ERROR, ERROR_CLASS_DEFAULT);
        SEVERITY_TO_STYLE_CLASS_DEFAULTS_MAP.put(FacesMessage.SEVERITY_WARN, WARN_CLASS_DEFAULT);
        SEVERITY_TO_STYLE_CLASS_DEFAULTS_MAP.put(FacesMessage.SEVERITY_INFO, INFO_CLASS_DEFAULT);
    }

    /**
     * Create an instance of MessagesRenderer
     *  
     */
    public MessagesRenderer() {
        super();
    }

    /**
     * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);

        if (!(component instanceof UIMessages)) {
            throw new IllegalArgumentException("Invalid component: " + component.getClass().getName());
        }

        UIMessages messagesComponent = (UIMessages) component;
        Iterator messagesToRender = messagesComponent.getMessagesToRender(context);
        while (messagesToRender.hasNext()) {
            FacesMessage nextMessage = (FacesMessage) messagesToRender.next();
            encodeMessage(context, messagesComponent, nextMessage);
        }
        
        // Now encode the destiny managed messages
        Iterator managedMessagesIterator = MessageManager.getInstance().getMessages();
        while (managedMessagesIterator.hasNext()) {
            FacesMessage nextMessage = (FacesMessage) managedMessagesIterator.next();
            encodeMessage(context, messagesComponent, nextMessage);
        }
    }

    /**
     * Render a single message
     * 
     * @throws IOException
     * @author sgoldstein
     */
    private void encodeMessage(FacesContext facesContext, UIMessages component, FacesMessage messageToEncode) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement(HTML.DIV_ELEM, component);
        String messageStyleClass = getStyleClassForMessage(component, messageToEncode);
        writer.writeAttribute(HTML.CLASS_ATTR, messageStyleClass, null);

        writeMessageSummary(writer, component, messageToEncode);
        writeMessageDetail(writer, component, messageToEncode);

        writer.endElement(HTML.DIV_ELEM);
    }

    /**
     * Render the summary of a single message
     * 
     * @param writer
     * @param component
     * @param messageToEncode
     * @throws IOException
     */
    private void writeMessageSummary(ResponseWriter writer, UIMessages component, FacesMessage messageToEncode) throws IOException {
        String messageSummary = messageToEncode.getSummary();
        if (messageSummary != null) {
            writer.startElement(HTML.DIV_ELEM, component);
            writer.writeAttribute(HTML.CLASS_ATTR, DIV_SUMMARY_CLASS, null);
            writer.write(messageSummary);
            writer.endElement(HTML.DIV_ELEM);
        }
    }

    /**
     * Render the detail of a single message
     * 
     * @param writer
     * @param component
     * @param messageToEncode
     * @throws IOException
     */
    private void writeMessageDetail(ResponseWriter writer, UIMessages component, FacesMessage messageToEncode) throws IOException {
        String messageDetail = messageToEncode.getDetail();
        if (messageDetail != null) {
            writer.startElement(HTML.DIV_ELEM, component);
            writer.writeAttribute(HTML.CLASS_ATTR, DIV_DETAIL_CLASS, null);
			// XSS SAP-PQ Fix
			writer.write(StringEscapeUtils.escapeHtml3(messageDetail));
            //writer.write(messageDetail); 
            writer.endElement(HTML.DIV_ELEM);
        }
    }

    /**
     * Retrieve the configured style class for the provided component and
     * message. If a style class has not been specified for the severity of the
     * provided message, the following defaults will be used: <br />
     * <br />
     * FacesMessage.SEVERITY_FATAL - "fatalMsg"<br />
     * FacesMessage.SEVERITY_ERROR - "errorMsg"<br />
     * FacesMessage.SEVERITY_WARN - "warnMsg"<br />
     * FacesMessage.SEVERITY_INFO - "infoMsg"<br />
     * 
     * @param messageToEncode
     * @return the style class associated with the specified message or null if
     *         one was not specified
     */
    private String getStyleClassForMessage(UIMessages component, FacesMessage messageToEncode) {
        String classToReturn = null;

        FacesMessage.Severity messageSeverity = messageToEncode.getSeverity();
        String attributeKeyForSeverity = (String) SEVERITY_TO_STYLE_CLASS_ATTR_MAP.get(messageSeverity);
        if (attributeKeyForSeverity != null) {
            classToReturn = (String) component.getAttributes().get(attributeKeyForSeverity);
        }

        if (classToReturn == null) {
            classToReturn = (String) SEVERITY_TO_STYLE_CLASS_DEFAULTS_MAP.get(messageSeverity);
        }

        return classToReturn;
    }
}