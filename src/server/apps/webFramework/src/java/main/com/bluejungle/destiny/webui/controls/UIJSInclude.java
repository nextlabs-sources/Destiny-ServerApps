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
 * This is a UI component to include one javascript file inside the web page
 * layout. This component simply stores the name of the file to include as its
 * value.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UIJSInclude.java#1 $
 */

public class UIJSInclude extends UIOutput {

    protected static final String RENDERER_TYPE = "com.bluejungle.destiny.webui.renderers.JSIncludeRenderer";

    /**
     * Constructor. Sets the renderer type.
     */
    public UIJSInclude() {
        super();
        this.setRendererType(RENDERER_TYPE);
    }
}