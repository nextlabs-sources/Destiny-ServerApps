/*
 * Created on Apr 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.mgmtconsole.status;

import javax.faces.model.DataModel;

/**
 * IComponentStatusBean is a java bean utilized by the display layer to retrieve
 * data about all of the registered server components
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/mgmtConsole/src/java/main/com/bluejungle/destiny/mgmtconsole/status/IComponentStatusBean.java#1 $
 */

public interface IComponentStatusBean {

    /**
     * Retrieve a DataModel containing a list of ComponentDataBean instances
     * which hold data for each registered server component
     * 
     * @return a DataModel containing a list of ComponentDataBean instances
     *         which hold data for each registered server component
     */
    public DataModel getComponentData();
}