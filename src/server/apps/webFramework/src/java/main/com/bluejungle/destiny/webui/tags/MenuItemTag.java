/*
 * Created on Mar 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

/**
 * MenuItemTag is a JSF tag used to add a component of component type,
 * "com.bluejungle.destiny.MenuItem", to a page. This tag must be nested within
 * a tag of type, com.bluejungle.destiny.webui.tags.MenuTag
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/MenuItemTag.java#1 $
 */

public class MenuItemTag extends UIComponentTag {

    private static final String COMPONENT_TYPE = "com.bluejungle.destiny.MenuItem";
    private static final String IMMEDIATE_ATTR_NAME = "immediate";
    private static final String VALUE_ATTR_NAME = "value";
    private static final String VIEW_ID_PATTERN_ATTR_NAME = "viewIdPattern";

    private String action;
    private String actionListener;
    private String immediate;
    private String value;
    private String viewIdPattern;

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
        return null;
    }

    /**
     * Returns the action associated with the selected of the menu item
     * associated with this tag.
     * 
     * @return the action.
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Returns the actionListener invoked when this menu item is selected.
     * 
     * @return the actionListener.
     */
    public String getActionListener() {
        return this.actionListener;
    }

    /**
     * Returns the immediate flag, indicating the time at which action events
     * associated with this menu item are distributed. See JSF spec.
     * 
     * @return the value of the immediate flag.
     */
    public String getImmediate() {
        return this.immediate;
    }

    /**
     * Returns the value of the menu item component, used as the text of the
     * menu item.
     * 
     * @return the value of the menu item component.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the action
     * 
     * @param action
     *            The action to set.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Sets the actionListener
     * 
     * @param actionListener
     *            The actionListener to set.
     */
    public void setActionListener(String actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * Sets the immediate flag
     * 
     * @param immediate
     *            The immediate flag to set.
     */
    public void setImmediate(String immediate) {
        this.immediate = immediate;
    }

    /**
     * Sets the value of the menu item component
     * 
     * @param value
     *            The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the view id pattern associated with the menu item. The view id
     * pattern is used to indiciate whether or not the menu item is the
     * currently selected item. If the pattern appears within the view id of the
     * current JSF view, then this item is considered the currently selected
     * item. This information can be used to highlight the item using a provided
     * CSS style class.
     * 
     * @return the view id pattern associated with this menu item or null if
     *         none was specified
     */
    public String getViewIdPattern() {
        return this.viewIdPattern;
    }

    /**
     * Set the view id pattern associated with this UIMenuItem.
     * 
     * @see com.bluejungle.destiny.webui.tags.MenuItemTag#getViewIdPattern()
     * @param the viewIdPattern to set
     */
    public void setViewIdPattern(String viewIdPattern) {
        this.viewIdPattern = viewIdPattern;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        TagUtil.setBoolean(component, IMMEDIATE_ATTR_NAME, this.getImmediate());
        TagUtil.setString(component, VALUE_ATTR_NAME, this.getValue());
        TagUtil.setString(component, VIEW_ID_PATTERN_ATTR_NAME, this.getViewIdPattern());

        TagUtil.setAction((ActionSource) component, this.getAction());
        TagUtil.setActionListener((ActionSource) component, this.getActionListener());
    }
    
    
    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        super.release();
        
        this.action = null;
        this.actionListener = null;
        this.immediate = null;
        this.value = null;
        this.viewIdPattern = null;
    }
}