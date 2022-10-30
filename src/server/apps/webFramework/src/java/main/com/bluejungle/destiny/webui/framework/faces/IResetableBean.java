/*
 * Created on Jun 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

/**
 * Represents a session or application scope bean which can be "reset". See
 * {@see com.bluejungle.destiny.webui.framework.faces.ResetBeanActionListener}
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/IResetableBean.java#1 $
 */

public interface IResetableBean {

    /**
     * Reset the bean state
     * 
     * @throws ResetException
     *             if the reset fails - FIX ME - Add this when needed
     */
    public void reset();
}