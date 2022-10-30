/*
\ * Created on Apr 18, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;
import javax.faces.event.FacesListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/FacesListenerBase.java#1 $
 */

public class FacesListenerBase implements FacesListener {

    private static final String EL_EXPRESSION_BEGIN = "#{";
    private static final String EL_EXPRESSION_END = "}";

    /**
     * Retrieve a request parameter by name
     * 
     * @return the request parameter value with the specified name or the
     *         specified default is the parameter does not exists
     */
    protected String getRequestParameter(String name, String defaultValue) {
        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }
    
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map requestParameters = ctx.getExternalContext().getRequestParameterMap();
        String valueToReturn = (String) requestParameters.get(name);
        if (valueToReturn == null) {
            valueToReturn = defaultValue;
        }
    
        return valueToReturn;
    }

    /**
     * Retrieve a Map of the request parameters with names that start with the
     * specified prefix (e.g. itemToDelete*). This is useful, for instance, in
     * the case of a view having checkboxes next to each data item in order to
     * perform actions in bulk. The request parameter names would the have a
     * particular prefix, ending with the ID of the assoicated data item
     * 
     * @param prefix
     *            the prefix of the request parameters
     * @return a Map of the request parameters with names that start with the
     *         specified prefix (name, value)
     */
    protected Map getRequestParametersWithPrefix(String prefix) {
        if (prefix == null) {
            throw new NullPointerException("prefix cannot be null.");
        }
    
        Map matchingParametersToReturn = new HashMap();
    
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map requestParameters = ctx.getExternalContext().getRequestParameterMap();
        Iterator requestParameterIterator = requestParameters.entrySet().iterator();
        while (requestParameterIterator.hasNext()) {
            Map.Entry nextRequestParameter = (Map.Entry) requestParameterIterator.next();
            String nextKey = (String) nextRequestParameter.getKey();
            if (nextKey.startsWith(prefix)) {
                matchingParametersToReturn.put(nextKey, nextRequestParameter.getValue());
            }
        }
    
        return matchingParametersToReturn;
    }

    /**
     * Retrieve initialization parameter value
     * 
     * @param name Name of parameter to retrieve
     * @param defaultValue Value to return if not provided in the initialization configuration
     * @return Value of parameter
     */
    protected String getInitParameter(String name, String defaultValue) {
        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }
    
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map initParameters = ctx.getExternalContext().getInitParameterMap();
        String valueToReturn = (String) initParameters.get(name);
        if (valueToReturn == null) {
            valueToReturn = defaultValue;
        }
    
        return valueToReturn;
    }

    /**
     * Retrieve a Managed Bean by name.
     * 
     * @param beanName
     *            the name of the bean to retrieve
     * @return the bean with the associated name
     * @throws EvaluationException
     *             if an exception is thrown while obtaining the bean
     * @throws PropertyNotFoundException
     *             if the name specified does not correspond to a managed bean
     */
    protected Object getManagedBeanByName(String beanName) throws EvaluationException, PropertyNotFoundException {
        if (beanName == null) {
            throw new NullPointerException("beanNeam cannot be null.");
        }
    
        ValueBinding beanValueBinding = this.createValueBindingFromString(beanName);
        FacesContext ctx = FacesContext.getCurrentInstance();
    
        return beanValueBinding.getValue(ctx);
    }

    /**
     * Creates a value binding from a string expression. If the expression does
     * not contain the proper EL prefix and suffix, they get added to the
     * expression.
     * 
     * @param stringExpression
     *            expression to use to create the binding
     * @return a value binding object based on this expression.
     */
    private ValueBinding createValueBindingFromString(final String stringExpression) {
        String vbExpression = stringExpression;
        if (!stringExpression.startsWith(EL_EXPRESSION_BEGIN)) {
            vbExpression = EL_EXPRESSION_BEGIN + stringExpression;
        }
        if (!stringExpression.endsWith(EL_EXPRESSION_END)) {
            vbExpression += EL_EXPRESSION_END;
        }
        return createValueBinding(vbExpression);
    }
    
    /**
     * Returns a value binding object based on the expression
     * 
     * @param vbExpression
     *            value binding expression
     * @return a value binding object based on the expression
     */
    private ValueBinding createValueBinding(final String vbExpression) {
        return getApplication().createValueBinding(vbExpression);
    }
    
    /**
     * Returns the current application object
     * 
     * @return the current JSF application object
     */
    protected Application getApplication() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        return ctx.getApplication();
    }    
}
