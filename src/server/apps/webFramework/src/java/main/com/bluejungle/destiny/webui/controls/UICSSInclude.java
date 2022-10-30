/*
 * Created on Mar 12, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIOutput;

/**
 * This is the CSS inclusion UI component class. This class simply holds the
 * value of the CSS stylesheet location.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UICSSInclude.java#1 $
 */

public class UICSSInclude extends UIOutput {

    protected static final String RENDERER_TYPE = "com.bluejungle.destiny.webui.renderers.CSSIncludeRenderer";

    /**
     * Constructor. Sets the renderer type.
     */
    public UICSSInclude() {
        super();
        this.setRendererType(RENDERER_TYPE);
    }
}