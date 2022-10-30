/*
 * Created on May 27, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.loginmgr.remote;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.remote.RemoteLoginManager;
import com.bluejungle.destiny.interfaces.secure_session.v1.SecureSessionServiceStub;
import org.apache.axis2.AxisFault;

/**
 * An extension of the RemoteLoginManager which utilizes the web app axis client
 * configuration to create the secure session service
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/loginmgr/remote/WebAppRemoteLoginMgr.java#1 $
 */

public class WebAppRemoteLoginMgr extends RemoteLoginManager {

    /**
     * @see com.bluejungle.destiny.appsecurity.loginmgr.remote.RemoteLoginManager#getSecureSessionService(null)
     */
    protected SecureSessionServiceStub getSecureSessionService(String serviceLocation) throws AxisFault {
        return new SecureSessionServiceStub(serviceLocation);
    }
}
