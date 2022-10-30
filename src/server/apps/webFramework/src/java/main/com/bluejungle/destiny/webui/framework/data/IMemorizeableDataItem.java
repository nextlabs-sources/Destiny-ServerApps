/*
 * Created on Jul 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.data;

/**
 * IMemorizeableDataItem is implemented by data items which are able to be
 * remembered by the
 * {@see com.bluejungle.destiny.webui.framework.data.MemorizingDataModel}
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/data/IMemorizeableDataItem.java#1 $
 */
public interface IMemorizeableDataItem {

    /**
     * Retrieve a String which identifies the data item and can be used to later
     * retrieve it
     * 
     * @return a String which identifies the data item and can be used to later
     *         retrieve it
     */
    public String getId();
}