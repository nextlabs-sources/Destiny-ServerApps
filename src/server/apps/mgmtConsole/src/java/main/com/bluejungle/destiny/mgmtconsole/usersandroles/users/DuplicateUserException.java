/*
 * Created on Sep 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.usersandroles.users;

/**
 * @author sasha
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/usersandroles/users/DuplicateUserException.java#1 $:
 */

public class DuplicateUserException extends UsersException {

    
    /**
     * Constructor
     * @param message
     * @param cause
     */
    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
