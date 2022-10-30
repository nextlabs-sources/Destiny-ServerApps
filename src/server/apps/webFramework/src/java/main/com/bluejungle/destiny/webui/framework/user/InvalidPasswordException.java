/*
 * Created on Sep 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.user;

/**
 * @author sasha
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/user/InvalidPasswordException.java#1 $:
 */

public class InvalidPasswordException extends Exception {

    /**
     * Constructor
     * 
     */
    public InvalidPasswordException() {
        super();
    }

    /**
     * Constructor
     * @param arg0
     */
    public InvalidPasswordException(String arg0) {
        super(arg0);
    }

    /**
     * Constructor
     * @param arg0
     */
    public InvalidPasswordException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Constructor
     * @param arg0
     * @param arg1
     */
    public InvalidPasswordException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
