/*
 * Created on Mar 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIInput;
import javax.faces.convert.IntegerConverter;

/**
 * This is the number selector UI component. The number selector controls allows
 * the user to enter a number inside an input field, and also allows the user to
 * use up and down arrows to increment / decrement the number. This control
 * comes
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UINumberSelector.java#1 $
 */

public class UINumberSelector extends UIInput {

    public static final String INCREMENT_LINK_CLASSNAME = "incr";
    public static final String DECREMENT_LINK_CLASSNAME = "decr";
    private static final String RENDERER_TYPE = "com.bluejungle.destiny.webui.renderers.NumberSelectorRenderer";

    /**
     * Constructor. Sets the basic component features.
     */
    public UINumberSelector() {
        super();
        setRendererType(RENDERER_TYPE);
        addControlConverter();
    }

    /**
     * This function adds an integer converter to the control. Child classes can
     * overwrite this method and use a different converter if required.
     */
    protected void addControlConverter() {
        this.setConverter(new IntegerConverter());
    }
}