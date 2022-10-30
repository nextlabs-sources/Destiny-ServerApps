/*
 * Created on Oct 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * Temporary fix for the UIInput rest problem. Here's the scenario: 1. View a
 * page with has a navigation menu to the left 2. Attempt to enter a value for a
 * selected item which is invalid 3. Submit the form. Should result in error 4.
 * Click on another item
 * 
 * result - The other item has the first item's values. expected - All values
 * are reset to that of the newly selected item. See code below and UIInput
 * source to see why this doesn't happen on its own
 * 
 * After posting to newsgroups, a better solution should be found
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/UIInputUtils.java#1 $
 */

public class UIInputUtils {

    public static void resetUIInput(FacesContext context) {
        UIViewRoot viewRoot = context.getViewRoot();
        processComponent(viewRoot);
    }

    private static void processComponent(UIComponent componentToProcess) {
        if (componentToProcess instanceof UIInput) {
            UIInput input = (UIInput) componentToProcess;
            input.setSubmittedValue(null);
            input.setValue(null);
            input.setLocalValueSet(false);
        }

        Iterator facetsAndChildren = componentToProcess.getFacetsAndChildren();
        while (facetsAndChildren.hasNext()) {
            processComponent((UIComponent) facetsAndChildren.next());
        }
    }
}