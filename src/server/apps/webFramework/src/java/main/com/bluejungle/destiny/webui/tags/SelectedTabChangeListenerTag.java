/*
 * Created on Apr 18, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import com.bluejungle.destiny.webui.controls.SelectedTabChangeEventListener;
import com.bluejungle.destiny.webui.controls.UITabbedPane;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * JSP tag to add a
 * {@see com.bluejungle.destiny.webui.controls.SelectedTabChangeEventListener}
 * instance to a UITabbedPane
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/SelectedTabChangeListenerTag.java#1 $
 */

public class SelectedTabChangeListenerTag extends TagSupport {

    private String type;

    /**
     * Set the type
     * 
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
		if (type == null) {
            throw new NullPointerException("type cannot be null.");
        }
        this.type = type;
    }

    /**
     * Retrieve the type.
     * 
     * @return the type.
     */
    private String getType() {
        return this.type;
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException {
        UIComponentTag parentTag = UIComponentTag.getParentUIComponentTag(pageContext);
        if (parentTag == null) {
            throw new JspException("Parent tag not found.");
        }

        if (parentTag.getCreated()) {
            try {
                SelectedTabChangeEventListener listenerToAdd = createListener();

                UIComponent parentComponent = parentTag.getComponentInstance();
                if (!(parentComponent instanceof UITabbedPane)) {
                    throw new JspException("Parent component not an instance of UITabbedPane as required: " + parentComponent.getClass().getName());
                }

                ((UITabbedPane) parentComponent).addSelectedTabChangeEventListner(listenerToAdd);
            } catch (ClassNotFoundException exception) {
                throw new JspException("Failed to find listener class, " + getType(), exception);
            } catch (IllegalAccessException exception) {
                throw new JspException("Failed to create listener class, " + getType() + ".  Please check access modifiers on class and default constructor.", exception);
            } catch (InstantiationException exception) {
                throw new JspException("Failed to instantiate listener class., " + getType() + ".  Exception thrown in constructor.", exception);
            }

        }

        return SKIP_BODY;
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport#release()
     */
    public void release() {
        super.release();

        this.type = null;
    }

    /**
     * Create the listener associated with this tag
     * 
     * @return the listener associated with this tag
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private SelectedTabChangeEventListener createListener() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class listenerClass = Class.forName(getType());
        return (SelectedTabChangeEventListener) listenerClass.newInstance();
    }
}
