/*
 * Created on May 10, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import com.bluejungle.framework.exceptions.SingleErrorBlueJungleException;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/SelectableItemSourceException.java#1 $
 */

public class SelectableItemSourceException extends SingleErrorBlueJungleException {

    /**
     * Create an instance of SelectableItemSourceException
     * 
     */
    public SelectableItemSourceException() {
        super();
    }

    /**
     * Create an instance of SelectableItemSourceException
     * @param string
     */
    public SelectableItemSourceException(String errorMessage) {
        // FIX ME - error message is currently lost
        super();
    }
    
    /**
     * Create an instance of SelectableItemSourceException
     * @param cause
     */
    public SelectableItemSourceException(Throwable cause) {
        super(cause);
    }
}
