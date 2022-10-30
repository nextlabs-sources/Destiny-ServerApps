/*
 * Created on Apr 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import javax.faces.component.ActionSource;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;


/**
 * This basic action listener provides utility function to custom action
 * listeners used in the application.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/ActionListenerBase.java#2 $
 */

public abstract class ActionListenerBase extends FacesListenerBase implements ActionListener {

    /**
     * Set the action which should taken following the execution of this
     * listener. This is used to forward or redirect to a particular page in the
     * application, similar to returning a String from a Faces Action Method
     * Binding. The action provided, if not null, must be an outcome specified
     * within a navigation case in the faces configuration. Providing a null
     * action indicates that the user should be returned to the same page from
     * which the action listener event was fired
     * 
     * @param action
     *            the action to take following the invoke application phase
     * @param event
     *            the event which triggered invocation of this ActionListener
     */
    protected void setResponseAction(String action, ActionEvent event) {
        MethodBinding actionMethodBinding = null;
        if (action != null) {
            actionMethodBinding = new ConstantMethodBinding(action);
        }
        ActionSource actionSource = (ActionSource) event.getComponent();
        actionSource.setAction(actionMethodBinding);
    }
}