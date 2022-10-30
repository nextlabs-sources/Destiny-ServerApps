/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.jsfmock;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;

/**
 * This is a mock value binding class for the testing of JSF components.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/jsfmock/MockValueBinding.java#1 $
 */

public class MockValueBinding extends ValueBinding {

    private Application application;
    private String expression;
    private String rawExpression;
    private Object value;

    /**
     * Constructor
     *  
     */
    public MockValueBinding() {
        this(null, null);
    }

    /**
     * Constructor
     * 
     * @param app
     *            JSF application
     * @param expression
     *            binding expression
     */
    public MockValueBinding(Application app, String expression) {
        this.application = app;
        this.rawExpression = expression;

        if (expression != null) {
            if (expression.startsWith("#{") && expression.endsWith("}")) {
                expression = expression.substring(2, expression.length() - 1);
            }
        }
        this.expression = expression;
    }

    /**
     * For now, returns directly the raw expression + some other constant.
     * 
     * @see javax.faces.el.ValueBinding#getValue(javax.faces.context.FacesContext)
     */
    public Object getValue(FacesContext context) throws EvaluationException, PropertyNotFoundException {
        return this.rawExpression + "_Value";
    }

    /**
     * @see javax.faces.el.ValueBinding#setValue(javax.faces.context.FacesContext,
     *      java.lang.Object)
     */
    public void setValue(FacesContext context, Object value) throws EvaluationException, PropertyNotFoundException {
        this.value = value;
    }

    /**
     * @see javax.faces.el.ValueBinding#isReadOnly(javax.faces.context.FacesContext)
     */
    public boolean isReadOnly(FacesContext context) throws EvaluationException, PropertyNotFoundException {
        return false;
    }

    /**
     * @see javax.faces.el.ValueBinding#getType(javax.faces.context.FacesContext)
     */
    public Class getType(FacesContext context) throws EvaluationException, PropertyNotFoundException {
        return null;
    }

    /**
     * @see javax.faces.el.ValueBinding#getExpressionString()
     */
    public String getExpressionString() {
        return this.expression;
    }

}