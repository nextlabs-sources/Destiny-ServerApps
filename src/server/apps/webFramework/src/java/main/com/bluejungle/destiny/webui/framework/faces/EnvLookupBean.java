/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This java bean looks up a value from the application init parameters. This
 * bean takes a key name as an argument. For performance, the bean computes its
 * value once and then stores the value in a member variable.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/EnvLookupBean.java#1 $
 */

public class EnvLookupBean {

    private static final Log LOG = LogFactory.getLog(EnvLookupBean.class.getName());
    private String keyName;
    private String cachedValue;

    /**
     * Calculates the bean value
     */
    protected void calculateValue() {
        this.cachedValue = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(getKeyName());
    }

    /**
     * Returns the name of the key
     * 
     * @return the name of the key
     */
    public String getKeyName() {
        return this.keyName;
    }

    /**
     * Sets the key name
     * 
     * @param keyName
     *            key name to set
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * Returns the value associated with the bean. This method is synchronized
     * in case the bean is placed in session or application scope, to allow
     * proper initialization.
     * 
     * @return the value associated with the bean
     */
    public synchronized String getValue() {
        if (this.cachedValue == null) {
            calculateValue();
        }
        return this.cachedValue;
    }
}