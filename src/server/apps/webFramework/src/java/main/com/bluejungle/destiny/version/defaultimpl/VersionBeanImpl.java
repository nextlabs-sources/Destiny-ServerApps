/*
 * Created on Jul 20, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.version.defaultimpl;

import com.bluejungle.destiny.version.IVersionBean;
import com.bluejungle.version.IVersion;
import com.bluejungle.versionexception.InvalidVersionException;
import com.bluejungle.versionfactory.VersionFactory;

import java.io.IOException;

/**
 * Default implementation of IVersionBean
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/version/defaultimpl/VersionBeanImpl.java#1 $
 */

public class VersionBeanImpl implements IVersionBean {

    private IVersion wrappedVersion;
    private String version;
    
    public VersionBeanImpl() {
        try {
            this.wrappedVersion = new VersionFactory().getVersion();
            
            StringBuffer versionBuffer = new StringBuffer();
            versionBuffer.append(this.wrappedVersion.getMajor());
            versionBuffer.append(".");
            versionBuffer.append(this.wrappedVersion.getMinor());
            versionBuffer.append(".");
            versionBuffer.append(this.wrappedVersion.getMaintenance());
            if (wrappedVersion.getPatch() > 0) {
                versionBuffer.append(".");
                versionBuffer.append(wrappedVersion.getPatch());
            }
            this.version = versionBuffer.toString();
        } catch (InvalidVersionException exception) {
            throw new IllegalStateException("Invalid version found", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read version", exception);
        }
    }

    /**
     * @see com.bluejungle.destiny.version.IVersionBean#getBuildLabel()
     */
    public int getBuildLabel() {
        return this.wrappedVersion.getBuild();
    }

    /**
     * @see com.bluejungle.destiny.version.IVersionBean#getVersion()
     */
    public String getVersion() {
        return this.version;
    }    
}
