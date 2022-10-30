/*
 * Created on Mar 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.data;

import java.util.List;

/**
 * This is the interface implemented by all the data objects. It allows the
 * components or event listeners to interact with them in a uniform way.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/data/IDataList.java#1 $
 */

public interface IDataList {

    /**
     * Returns the data contained in the data list
     * 
     * @return a list of records
     */
    public List getData();
}