/*
 * Created on Oct 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.agentstatus;

import com.bluejungle.framework.exceptions.SingleErrorBlueJungleException;

/**
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/agentstatus/StatusByAgentViewException.java#1 $
 */

public class StatusByAgentViewException extends SingleErrorBlueJungleException {

    /**
     * Create an instance of StatusByAgentViewException
     * @param string
     * @param exception
     */
    public StatusByAgentViewException(String message, Throwable cause) {
        super(cause);
    }           
}
