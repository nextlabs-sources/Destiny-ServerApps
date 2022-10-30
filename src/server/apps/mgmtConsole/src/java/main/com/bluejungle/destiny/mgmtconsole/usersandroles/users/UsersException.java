/*
 * Created on May 26, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

import com.bluejungle.framework.exceptions.BlueJungleException;

/**
 * UsersException is thrown when an error occurs within the users display model layer
 * 
 * @author pkeni
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/UsersException.java#1 $
 */

public class UsersException extends BlueJungleException {
    private String message;
    
    /**
     * Create an instance of UsersExecption
     * 
     * @param cause
     */
    public UsersException(String message, Throwable cause) {
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
