/*
 * Created on Apr 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import java.util.HashMap;
import java.util.Map;

/**
 * The PrerenderActionManagerBean maintains a Map from view id to actions which
 * are invoked by the
 * {@see com.bluejungle.destiny.webui.framework.faces.PrerenderActionViewHandler}
 * view handler at the start of the Faces render phase
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/PrerenderActionManagerBean.java#1 $
 */

public class PrerenderActionManagerBean {

    private Map prerenderActions = new HashMap();
    private String errorViewId;

    /**
     * Create an instance of PrerenderActionManagerBean
     *  
     */
    public PrerenderActionManagerBean() {
        super();
    }

    /**
     * Retrieve the prerender action associated with the specified view id
     * 
     * @param viewId
     * @returns the prerender action associated with the specified view id or
     *          null if one does not exist
     */
    public String getPrerenderAction(String viewId) {
        return (String) this.prerenderActions.get(viewId);
    }

    /**
     * Determing if a prerender action exists for the specified view id
     * 
     * @param viewId
     * @return true if one exists; false otherwise
     */
    public boolean hasPrerenderAction(String viewId) {
        return this.prerenderActions.containsKey(viewId);
    }

    /**
     * Retrieve a map of all configured prerender actions
     * 
     * @return a map of all configured prerender actions
     */
    public Map getPrerenderActions() {
        return this.prerenderActions;
    }

    /**
     * Sets the prerenderActions
     * @param prerenderActions The prerenderActions to set.
     */
    public void setPrerenderActions(Map prerenderActions) {
        this.prerenderActions = prerenderActions;
    }

    /**
     * Retrieve the errorViewId.
     * 
     * @return the errorViewId.
     */
    public String getErrorViewId() {
        return this.errorViewId;
    }

    /**
     * Set the errorViewId
     * 
     * @param errorViewId
     *            The errorViewId to set.
     */
    public void setErrorViewId(String errorViewId) {
        this.errorViewId = errorViewId;
    }
}