/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

/**
 * This interface is implemented by bean that can be "pre-loaded" before being
 * used.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/ILoadable.java#1 $
 */

public interface ILoadable {

    /**
     * Returns whether is object is currently loaded
     * 
     * @return true if the object is loaded, false otherwise
     */
    public boolean isLoaded();

    /**
     * Loads the object internal data
     */
    public void load();
}