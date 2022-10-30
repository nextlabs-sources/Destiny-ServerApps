/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

/**
 * DisplayMessagesTag is a JSF tag used to render components in the family
 * com.bluejungle.destiny.Messages with a render type
 * com.bluejungle.destiny.MessagesRenderer
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/DisplayMessagesTag.java#1 $
 */
public class DisplayMessagesTag extends UIComponentTag {

    private static final String COMPONENT_TYPE = "com.bluejungle.destiny.Messages";
    private static final String RENDERER_TYPE = "com.bluejungle.destiny.MessagesRenderer";

    private static final String FOR_ATT_NAME = "forClientId";
    private static final String FATAL_CLASS_ATT_NAME = "fatalStyleClass";
    private static final String ERROR_CLASS_ATT_NAME = "errorStyleClass";
    private static final String WARN_CLASS_ATT_NAME = "warnStyleClass";
    private static final String INFO_CLASS_ATT_NAME = "infoStyleClass";

    private String _for;
    private String fatalClass;
    private String errorClass;
    private String warnClass;
    private String infoClass;

    /**
     * Create an instance of DisplayMessagesTag
     *  
     */
    public DisplayMessagesTag() {
        super();
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#getComponentType()
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#getRendererType()
     */
    public String getRendererType() {
        return RENDERER_TYPE;
    }

    /**
     * Set the _for
     * 
     * @param _for
     *            The _for to set.
     */
    public void set_for(String _for) {
        this._for = _for;
    }

    /**
     * Set the fatalClass
     * 
     * @param fatalClass
     *            The fatalClass to set.
     */
    public void setFatalClass(String fatalClass) {
        this.fatalClass = fatalClass;
    }

    /**
     * Set the errorClass
     * 
     * @param errorClass
     *            The errorClass to set.
     */
    public void setErrorClass(String errorClass) {
        this.errorClass = errorClass;
    }

    /**
     * Set the warnClass
     * 
     * @param warnClass
     *            The warnClass to set.
     */
    public void setWarnClass(String warnClass) {
        this.warnClass = warnClass;
    }

    /**
     * Set the infoClass
     * 
     * @param infoClass
     *            The infoClass to set.
     */
    public void setInfoClass(String infoClass) {
        this.infoClass = infoClass;
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        super.release();

        this._for = null;
        this.fatalClass = null;
        this.errorClass = null;
        this.warnClass = null;
        this.infoClass = null;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        TagUtil.setString(component, FOR_ATT_NAME, get_for());
        TagUtil.setString(component, FATAL_CLASS_ATT_NAME, getFatalClass());
        TagUtil.setString(component, ERROR_CLASS_ATT_NAME, getErrorClass());
        TagUtil.setString(component, WARN_CLASS_ATT_NAME, getWarnClass());
        TagUtil.setString(component, INFO_CLASS_ATT_NAME, getInfoClass());
    }

    /**
     * Retrieve the _for.
     * 
     * @return the _for.
     */
    private String get_for() {
        return this._for;
    }

    /**
     * Retrieve the fatalClass.
     * 
     * @return the fatalClass.
     */
    private String getFatalClass() {
        return this.fatalClass;
    }

    /**
     * Retrieve the errorClass.
     * 
     * @return the errorClass.
     */
    private String getErrorClass() {
        return this.errorClass;
    }

    /**
     * Retrieve the warnClass.
     * 
     * @return the warnClass.
     */
    private String getWarnClass() {
        return this.warnClass;
    }

    /**
     * Retrieve the infoClass.
     * 
     * @return the infoClass.
     */
    private String getInfoClass() {
        return this.infoClass;
    }
}