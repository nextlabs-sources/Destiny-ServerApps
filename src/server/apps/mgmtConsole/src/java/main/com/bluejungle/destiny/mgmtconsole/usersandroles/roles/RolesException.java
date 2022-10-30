/*
 * Created on May 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.roles;

import com.bluejungle.framework.exceptions.BlueJungleException;

/**
 * RolesException is thrown when an error occurs within the roles display model layer
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/roles/RolesException.java#1 $
 */

public class RolesException extends BlueJungleException {
    private String message;
    
    /**
     * Create an instance of RolesExecption
     * 
     * @param cause
     */
    public RolesException(String message, Throwable cause) {
        super(cause);
        // FIX ME - Localize Message
        this.message = message;
    }

    /**
     * @see com.bluejungle.framework.exceptions.BlueJungleException#getMessage()
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @see com.bluejungle.framework.exceptions.BlueJungleException#getLocalizedMessage()
     */
    public String getLocalizedMessage() {
        return this.message;
    }
}