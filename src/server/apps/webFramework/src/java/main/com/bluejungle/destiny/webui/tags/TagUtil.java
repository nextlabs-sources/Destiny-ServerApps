/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.application.Application;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.webapp.UIComponentTag;

import org.apache.myfaces.el.SimpleActionMethodBinding;

/**
 * This class contains various utility functions that UI component tags can use.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/TagUtil.java#1 $
 */
public class TagUtil {

    private static final Class[] ACTION_LISTENER_ARGS = { ActionEvent.class };

    /**
     * Returns whether an expression is a value reference (that can be used for
     * a value binding)
     * 
     * @param expression
     *            expression to evaluate
     * @return true if the expression is a value binding, false otherwise.
     */
    private static boolean isValueReference(String expression) {
        return UIComponentTag.isValueReference(expression);
    }

    /**
     * Sets an integer attribute on a UI component
     * 
     * @param component
     *            component object
     * @param attrName
     *            attribute name
     * @param attrValue
     *            attribute value
     */
    public static void setInteger(UIComponent component, String attrName, String attrValue) {
        //Check the required parameters
        if (component == null) {
            throw new NullPointerException("Component cannot be null");
        }
        if (attrName == null) {
            throw new NullPointerException("Attribute name cannot be null");
        }

        if (attrValue != null) {
            if (isValueReference(attrValue)) {
                setValueBinding(component, attrName, attrValue);
            } else {
                component.getAttributes().put(attrName, new Integer(attrValue));
            }
        }
    }

    /**
     * Sets a long attribute on a UI component
     * 
     * @param component
     *            component object
     * @param attrName
     *            attribute name
     * @param attrValue
     *            attribute value
     */
    public static void setLong(UIComponent component, String attrName, String attrValue) {
        //Check the required parameters
        if (component == null) {
            throw new NullPointerException("Component cannot be null");
        }
        if (attrName == null) {
            throw new NullPointerException("Attribute name cannot be null");
        }

        if (attrValue != null) {
            if (isValueReference(attrValue)) {
                setValueBinding(component, attrName, attrValue);
            } else {
                component.getAttributes().put(attrName, new Long(attrValue));
            }
        }
    }

    /**
     * Sets an string attribute on a UI component
     * 
     * @param component
     *            component object
     * @param attrName
     *            attribute name
     * @param attrValue
     *            attribute value
     */
    public static void setString(UIComponent component, String attrName, String attrValue) {
        //Check the required parameters
        if (component == null) {
            throw new NullPointerException("Component cannot be null");
        }
        if (attrName == null) {
            throw new NullPointerException("Attribute name cannot be null");
        }

        if (attrValue != null) {
            if (isValueReference(attrValue)) {
                setValueBinding(component, attrName, attrValue);
            } else {
                component.getAttributes().put(attrName, attrValue);
            }
        }
    }

    /**
     * Sets an boolean attribute on a UI component
     * 
     * @param component
     *            component object
     * @param attrName
     *            attribute name
     * @param attrValue
     *            attribute value
     */
    public static void setBoolean(UIComponent component, String attrName, String attrValue) {
        //Check the required parameters
        if (component == null) {
            throw new NullPointerException("Component cannot be null");
        }
        if (attrName == null) {
            throw new NullPointerException("Attribute name cannot be null");
        }

        if (attrValue != null) {
            if (isValueReference(attrValue)) {
                setValueBinding(component, attrName, attrValue);
            } else {
                component.getAttributes().put(attrName, new Boolean(attrValue));
            }
        }
    }

    /**
     * Sets an action attribute on a ActionSource component
     * 
     * @param component
     *            component object
     * @param attrName
     *            attribute name
     * @param attrValue
     *            attribute value
     */
    public static void setAction(ActionSource component, String attrValue) {
        //Check the required parameters
        if (component == null) {
            throw new NullPointerException("Component cannot be null");
        }

        if (attrValue != null) {
            MethodBinding methodBinding = getActionMethodBinding(attrValue);
            component.setAction(methodBinding);
        }
    }

    /**
     * Sets an action listener attribute on a ActionSource component
     * 
     * @param component
     *            component object
     * @param attrName
     *            attribute name
     * @param attrValue
     *            attribute value
     */
    public static void setActionListener(ActionSource component, String actionListener) {
        if (component == null) {
            throw new NullPointerException("component cannot be null.");
        }

        if (actionListener != null) {
            if (isValueReference(actionListener)) {
                FacesContext context = FacesContext.getCurrentInstance();
                MethodBinding methodBinding = context.getApplication().createMethodBinding(actionListener, ACTION_LISTENER_ARGS);
                component.setActionListener(methodBinding);
            } else {
                throw new IllegalArgumentException("Invalid expression: " + actionListener);
            }
        }
    }

    /**
     * Creates a value binding for a given component
     * 
     * @param component
     *            UI component
     * @param attrName
     *            name of the component attribute
     * @param attrExpr
     *            value binding expression
     */
    private static void setValueBinding(UIComponent component, String attrName, String attrExpr) {
        FacesContext context = FacesContext.getCurrentInstance();
        Application application = context.getApplication();
        ValueBinding vb = application.createValueBinding(attrExpr);
        component.setValueBinding(attrName, vb);
    }

    /**
     * Creates a method binding for a given component
     * 
     * @param component
     *            UI component
     * @param attrName
     *            name of the component attribute
     * @param attrExpr
     *            method binding expression
     */
    private static MethodBinding getActionMethodBinding(String attrExpr) {
        MethodBinding bindingToReturn = null;

        if (isValueReference(attrExpr)) {
            FacesContext context = FacesContext.getCurrentInstance();
            Application application = context.getApplication();
            bindingToReturn = application.createMethodBinding(attrExpr, null);
        } else {
            bindingToReturn = new SimpleActionMethodBinding(attrExpr);
        }

        return bindingToReturn;
    }
}