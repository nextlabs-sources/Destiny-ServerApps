/*
 * Created on Jul 20, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.version;

/**
 * Java bean wrapper around the version framework
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/version/IVersionBean.java#1 $
 */

public interface IVersionBean {

    /**
     * Returns the version string
     * ("major.minor.maintenance")
     * 
     * @return the version string
     */
    public String getVersion();

    /**
     * Returns the build version
     * 
     * @return the build version
     */
    public int getBuildLabel();
}
