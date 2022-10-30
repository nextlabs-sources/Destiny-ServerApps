/*
 * Created on Oct 31, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIOutput;

/**
 * This is the show/hide component implemenentation class.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UIShowHide.java#1 $
 */

public class UIShowHide extends UIOutput {

    protected static final String RENDERER_TYPE = "com.bluejungle.destiny.webui.renderers.ShowHideRenderer";

    /**
     * Constructor. Sets the renderer type.
     */
    public UIShowHide() {
        super();
        this.setRendererType(RENDERER_TYPE);
    }
}
