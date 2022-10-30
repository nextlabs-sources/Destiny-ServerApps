/*
 * Created on Mar 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;

import org.apache.myfaces.taglib.html.ext.HtmlInputTextTag;

import com.bluejungle.destiny.webui.controls.UINumberSelector;

/**
 * The number selector tag accepts a number as an input. It is possible to
 * specify a minimum range and maximum range for the control.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/NumberSelectorTag.java#1 $
 */

public class NumberSelectorTag extends HtmlInputTextTag {

    protected static final String COMPONENT_TYPE = "com.bluejungle.destiny.numberSelector";

    private String decrementClassName;
    private String incrementClassName;
    private String minValue;
    private String maxValue;

    /**
     * Returns the component type associated with this tag.
     * 
     * @return the component type associated with this tag.
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * Returns the max value.
     * 
     * @return the max value.
     */
    public String getMaxValue() {
        return this.maxValue;
    }

    /**
     * Returns the min value.
     * 
     * @return the min value.
     */
    public String getMinValue() {
        return this.minValue;
    }

    /**
     * Returns the renderer type. The renderer settings will be decided at
     * runtime, so return null.
     * 
     * @return the renderer type
     */
    public String getRendererType() {
        return null;
    }

    /**
     * Sets the max value
     * 
     * @param maxValue
     *            The max value to set.
     */
    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Sets the min value
     * 
     * @param minValue
     *            The min value to set.
     */
    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        TagUtil.setInteger(component, "minValue", getMinValue());
        TagUtil.setInteger(component, "maxValue", getMaxValue());
        TagUtil.setString(component, UINumberSelector.DECREMENT_LINK_CLASSNAME, getDecrementClassName());
        TagUtil.setString(component, UINumberSelector.INCREMENT_LINK_CLASSNAME, getIncrementClassName());
    }

    /**
     * Returns the decrement class name.
     * 
     * @return the decrement class name.
     */
    public String getDecrementClassName() {
        return this.decrementClassName;
    }

    /**
     * Returns the increment class name.
     * 
     * @return the increment class name.
     */
    public String getIncrementClassName() {
        return this.incrementClassName;
    }

    /**
     * Sets the increment class name.
     * 
     * @param decrement
     *            class name The decrement class name to set.
     */
    public void setDecrementClassName(String decrementClassName) {
        this.decrementClassName = decrementClassName;
    }

    /**
     * Sets the increment class name
     * 
     * @param increment
     *            class name The increment class name to set.
     */
    public void setIncrementClassName(String incrementClassName) {
        this.incrementClassName = incrementClassName;
    }
}