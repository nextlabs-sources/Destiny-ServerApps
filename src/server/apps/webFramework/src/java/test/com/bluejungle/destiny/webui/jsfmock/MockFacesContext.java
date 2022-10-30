/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.jsfmock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

/**
 * This is a dummy JSF context class. It is used for the testing of JSF related
 * classes.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/jsfmock/MockFacesContext.java#1 $
 */

public class MockFacesContext extends FacesContext {

    private Application application;
    private ExternalContext externalContext;
    private Map messages = new HashMap();
    private boolean renderResponse = false;
    private boolean responseComplete = false;
    private ResponseStream responseStream = null;
    private ResponseWriter responseWriter = null;
    private UIViewRoot viewRoot = new UIViewRoot();

    /**
     * Constructor
     */
    public MockFacesContext() {
        super();
        setCurrentInstance(this);
        viewRoot.setRenderKitId("BASIC_HTML");
    }

    /**
     * Constructor
     * 
     * @param externalContext
     *            dummy external context
     */
    public MockFacesContext(ExternalContext externalContext) {
        setExternalContext(externalContext);
        setCurrentInstance(this);
    }

    /**
     * Constructor
     * 
     * @param externalContext
     *            dummy external context
     * @param lifecycle
     *            dummy lifecyle
     */
    public MockFacesContext(ExternalContext externalContext, Lifecycle lifecycle) {
        this(externalContext);
    }

    /**
     * @see javax.faces.context.FacesContext#getApplication()
     */
    public Application getApplication() {
        return (this.application);
    }

    /**
     * Sets the application object
     * 
     * @param application
     *            new application object
     */
    public void setApplication(Application application) {
        this.application = application;
    }

    /**
     * @see javax.faces.context.FacesContext#getClientIdsWithMessages()
     */
    public Iterator getClientIdsWithMessages() {
        return (this.messages.keySet().iterator());
    }

    /**
     * @see javax.faces.context.FacesContext#getExternalContext()
     */
    public ExternalContext getExternalContext() {
        return (this.externalContext);
    }

    /**
     * Sets the dummy external context
     * 
     * @param externalContext
     *            new external context
     */
    public void setExternalContext(ExternalContext externalContext) {
        this.externalContext = externalContext;
    }

    /**
     * @see javax.faces.context.FacesContext#getMaximumSeverity()
     */
    public Severity getMaximumSeverity() {
        return null;
    }

    /**
     * @see javax.faces.context.FacesContext#getMessages()
     */
    public Iterator getMessages() {
        List results = new ArrayList();
        Iterator clientIds = this.messages.keySet().iterator();
        while (clientIds.hasNext()) {
            String clientId = (String) clientIds.next();
            results.addAll((List) this.messages.get(clientId));
        }
        return (results.iterator());
    }

    /**
     * @see javax.faces.context.FacesContext#getMessages(java.lang.String)
     */
    public Iterator getMessages(String clientId) {
        List result = (List) this.messages.get(clientId);
        if (result == null) {
            result = new ArrayList();
        }
        return (result.iterator());
    }

    /**
     * @see javax.faces.context.FacesContext#getRenderKit()
     */
    public RenderKit getRenderKit() {
        UIViewRoot viewRoot = getViewRoot();
        if (viewRoot == null) {
            return (null);
        }

        String renderKitId = viewRoot.getRenderKitId();
        if (renderKitId == null) {
            return (null);
        }

        RenderKitFactory rkFactory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        return (rkFactory.getRenderKit(this, renderKitId));
    }

    /**
     * @see javax.faces.context.FacesContext#getRenderResponse()
     */
    public boolean getRenderResponse() {
        return (this.renderResponse);
    }

    /**
     * @see javax.faces.context.FacesContext#getResponseComplete()
     */
    public boolean getResponseComplete() {
        return (this.responseComplete);
    }

    /**
     * @see javax.faces.context.FacesContext#getResponseStream()
     */
    public ResponseStream getResponseStream() {
        return (this.responseStream);
    }

    /**
     * @see javax.faces.context.FacesContext#setResponseStream(javax.faces.context.ResponseStream)
     */
    public void setResponseStream(ResponseStream responseStream) {
        this.responseStream = responseStream;
    }

    /**
     * @see javax.faces.context.FacesContext#getResponseWriter()
     */
    public ResponseWriter getResponseWriter() {
        return (this.responseWriter);
    }

    /**
     * @see javax.faces.context.FacesContext#setResponseWriter(javax.faces.context.ResponseWriter)
     */
    public void setResponseWriter(ResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    /**
     * @see javax.faces.context.FacesContext#getViewRoot()
     */
    public UIViewRoot getViewRoot() {
        return (this.viewRoot);
    }

    /**
     * @see javax.faces.context.FacesContext#setViewRoot(javax.faces.component.UIViewRoot)
     */
    public void setViewRoot(UIViewRoot root) {
        this.viewRoot = root;
    }

    /**
     * 
     * @see javax.faces.context.FacesContext#addMessage(java.lang.String,
     *      javax.faces.application.FacesMessage)
     */
    public void addMessage(String clientId, FacesMessage message) {
        if (message == null) {
            throw new NullPointerException("Message cannot be null");
        }
        List list = (List) this.messages.get(clientId);
        if (list == null) {
            list = new ArrayList();
            this.messages.put(clientId, list);
        }
        list.add(message);
    }

    /**
     * @see javax.faces.context.FacesContext#release()
     */
    public void release() {
        this.application = null;
        this.externalContext = null;
        this.messages.clear();
        this.renderResponse = false;
        this.responseComplete = false;
        this.responseStream = null;
        this.responseWriter = null;
        this.viewRoot = null;
    }

    /**
     * @see javax.faces.context.FacesContext#renderResponse()
     */
    public void renderResponse() {
        this.renderResponse = true;
    }

    /**
     * @see javax.faces.context.FacesContext#responseComplete()
     */
    public void responseComplete() {
        this.responseComplete = true;
    }
}