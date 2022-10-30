/*
 * Created on Jun 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

/**
 * A method binding implementation which returns a constant value
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/ConstantMethodBinding.java#1 $
 */
public class ConstantMethodBinding extends MethodBinding implements StateHolder {

    private String outcome = null;

    /**
     * Create an instance of ConstantMethodBinding
     * @param constantOutcome
     */
    public ConstantMethodBinding(String constantOutcome) {
        outcome = constantOutcome;
    }

    /**
     * @see javax.faces.el.MethodBinding#invoke(javax.faces.context.FacesContext, java.lang.Object[])
     */
    public Object invoke(FacesContext context, Object params[]) {
        return outcome;
    }

    /**
     * @see javax.faces.el.MethodBinding#getType(javax.faces.context.FacesContext)
     */
    public Class getType(FacesContext context) {
        return String.class;
    }

    public Object saveState(FacesContext context) {
        return outcome;
    }

    public void restoreState(FacesContext context, Object state) {
        outcome = (String) state;
    }

    private boolean transientFlag = false;

    public boolean isTransient() {
        return (this.transientFlag);
    }

    public void setTransient(boolean transientFlag) {
        this.transientFlag = transientFlag;
    }
}