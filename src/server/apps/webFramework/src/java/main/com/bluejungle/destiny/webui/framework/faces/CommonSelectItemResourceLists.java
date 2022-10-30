/*
 * Created on Mar 16, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import com.bluejungle.destiny.appframework.i18n.CommonOptionItemResourceLists;

/**
 * Contains constants referencing common
 * {@see com.bluejungle.destiny.webui.framework.faces.SelectItemResourceListFactory}
 * instances
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/CommonSelectItemResourceLists.java#1 $
 */

public interface CommonSelectItemResourceLists {

    /**
     * List of select items representing the maximum UI element list drop down
     * seen throughout the Administrator and Reporter apps
     */
    public static final SelectItemResourceListFactory MAX_UI_ELEMENT_LIST_SIZE_SELECT_ITEMS = new SelectItemResourceListFactory(CommonOptionItemResourceLists.MAX_UI_ELEMENT_LIST_SIZE_OPTIONS);
}
