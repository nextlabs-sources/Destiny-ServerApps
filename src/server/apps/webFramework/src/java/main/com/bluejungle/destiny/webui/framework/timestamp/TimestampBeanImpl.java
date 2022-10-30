/*
 * Created on Aug 3, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.timestamp;

/**
 * This is the timestamp bean class. This class provides the various timestamp
 * related functions.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/timestamp/TimestampBeanImpl.java#1 $
 */

public class TimestampBeanImpl {

    /**
     * Returns the current system time
     * 
     * @return the current system time
     */
    public long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}