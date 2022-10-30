/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;

/**
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/CustomPropertyResolver.java#1 $
 */

public class CustomPropertyResolver extends PropertyResolver {

    private PropertyResolver realPropertyResolver;

    /**
     * Constructor
     * 
     * @param realPropResolver
     */
    public CustomPropertyResolver(PropertyResolver realPropResolver) {
        super();
        this.realPropertyResolver = realPropResolver;
    }

    /**
     * @see javax.faces.el.PropertyResolver#getValue(java.lang.Object,
     *      java.lang.Object)
     */
    public Object getValue(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
        return this.realPropertyResolver.getValue(base, property);
    }

    /**
     * @see javax.faces.el.PropertyResolver#getValue(java.lang.Object, int)
     */
    public Object getValue(Object base, int index) throws EvaluationException, PropertyNotFoundException {
        return this.realPropertyResolver.getValue(base, index);
    }

    /**
     * @see javax.faces.el.PropertyResolver#setValue(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public void setValue(Object base, Object property, Object value) throws EvaluationException, PropertyNotFoundException {
        this.realPropertyResolver.setValue(base, property, value);
    }

    /**
     * @see javax.faces.el.PropertyResolver#setValue(java.lang.Object, int,
     *      java.lang.Object)
     */
    public void setValue(Object base, int index, Object value) throws EvaluationException, PropertyNotFoundException {
        this.realPropertyResolver.setValue(base, index, value);
    }

    /**
     * @see javax.faces.el.PropertyResolver#isReadOnly(java.lang.Object,
     *      java.lang.Object)
     */
    public boolean isReadOnly(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
        return this.realPropertyResolver.isReadOnly(base, property);
    }

    /**
     * @see javax.faces.el.PropertyResolver#isReadOnly(java.lang.Object, int)
     */
    public boolean isReadOnly(Object base, int index) throws EvaluationException, PropertyNotFoundException {
        return this.realPropertyResolver.isReadOnly(base, index);
    }

    /**
     * @see javax.faces.el.PropertyResolver#getType(java.lang.Object,
     *      java.lang.Object)
     */
    public Class getType(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
        return this.realPropertyResolver.getType(base, property);
    }

    /**
     * @see javax.faces.el.PropertyResolver#getType(java.lang.Object, int)
     */
    public Class getType(Object base, int index) throws EvaluationException, PropertyNotFoundException {
        return this.realPropertyResolver.getType(base, index);
    }
}