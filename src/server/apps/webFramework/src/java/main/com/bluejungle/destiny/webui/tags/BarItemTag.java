/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

import com.bluejungle.destiny.webui.controls.UIBarItem;

/**
 * The bar item tag allows storing information about a bargraph item in a
 * histogram.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/BarItemTag.java#1 $
 */

public class BarItemTag extends UIComponentTag {

    /**
     * Component type associated with the tag
     */
    protected static final String COMPONENT_TYPE = "com.bluejungle.destiny.BarItem";

    protected String barClassName;
    protected String containerClassName;
    protected String maxBarSize;
    protected String maxRange;
    protected String orientation = UIBarItem.ORIENTATION_HOR;
    protected String value;

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
     * Sets the bar class name
     * 
     * @param newBarClassName
     *            new bar class name to set
     */
    public void setBarClassName(String newBarClassName) {
        this.barClassName = newBarClassName;
    }

    /**
     * Sets the container class name
     * 
     * @param newContainerClassName
     *            new container class name to set
     */
    public void setContainerClassName(String newContainerClassName) {
        this.containerClassName = newContainerClassName;
    }

    /**
     * Sets the orientation property
     * 
     * @param newOrientation
     *            new orientation to set
     */
    public void setOrientation(String newOrientation) {
        if (UIBarItem.ORIENTATION_HOR.equals(newOrientation) || UIBarItem.ORIENTATION_VER.equals(newOrientation)) {
            this.orientation = newOrientation;
        } else {
            throw new IllegalArgumentException("Orientation can only be '" + UIBarItem.ORIENTATION_HOR + "' or '" + UIBarItem.ORIENTATION_VER + "'");
        }
    }

    /**
     * Sets the maximum size (in pixels) for the bar
     * 
     * @param newMaxBarSize
     *            new bar size to set
     */
    public void setMaxBarSize(String newMaxBarSize) {
        this.maxBarSize = newMaxBarSize;
    }

    /**
     * Sets the maximum range value
     * 
     * @param newMaxRange
     *            new maximum range to set
     */
    public void setMaxRange(String newMaxRange) {
        this.maxRange = newMaxRange;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent uiComp) {
        super.setProperties(uiComp);
        TagUtil.setString(uiComp, UIBarItem.BAR_CLASS_ATTR_NAME, this.barClassName);
        TagUtil.setString(uiComp, UIBarItem.CONTAINER_CLASS_ATTR_NAME, this.containerClassName);
        TagUtil.setInteger(uiComp, UIBarItem.MAX_BAR_SIZE, this.maxBarSize);
        TagUtil.setLong(uiComp, UIBarItem.MAX_RANGE_ATTR_NAME, this.maxRange);
        TagUtil.setString(uiComp, UIBarItem.ORIENTATION_ATTR_NAME, this.orientation);
        TagUtil.setString(uiComp, "value", this.value);
    }

    /**
     * Sets the value
     * 
     * @param newValue
     *            new value to set
     */
    public void setValue(String newValue) {
        this.value = newValue;
    }
}