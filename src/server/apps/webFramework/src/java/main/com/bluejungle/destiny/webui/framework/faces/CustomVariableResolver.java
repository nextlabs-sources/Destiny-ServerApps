/*
 * Created on Apr 19, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import com.bluejungle.destiny.webui.framework.context.AppContext;

/**
 * A custom variable resolver. Currently, it only add the implicit variable,
 * "customAppContext", which references the current (@see
 * com.bluejungle.destiny.webui.framework.context.AppContext) instance
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/CustomVariableResolver.java#1 $
 */
public class CustomVariableResolver extends VariableResolver {

    public static final String APP_CONTEXT_VARIABLE_NAME = "customAppContext";

    private VariableResolver wrappedResolver;

    /**
     * Create an instance of CustomVariableResolver.  
     * 
     * @param wrappedResolver
     *            the original resolver
     */
    public CustomVariableResolver(VariableResolver wrappedResolver) {
        super();
        this.wrappedResolver = wrappedResolver;
    }

    /**
     * @see javax.faces.el.VariableResolver#resolveVariable(javax.faces.context.FacesContext,
     *      java.lang.String)
     */
    public Object resolveVariable(FacesContext context, String name) throws EvaluationException {
        Object valueToReturn = null;

        if ((name != null) && (name.equals(APP_CONTEXT_VARIABLE_NAME))) {
            valueToReturn = AppContext.getContext();
        } else {
            valueToReturn = wrappedResolver.resolveVariable(context, name);
        }

        return valueToReturn;
    }
}