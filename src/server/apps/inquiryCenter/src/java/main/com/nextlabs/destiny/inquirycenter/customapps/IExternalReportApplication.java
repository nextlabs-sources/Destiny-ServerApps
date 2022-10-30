/*
 * Created on Mar 10, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.customapps;

import java.io.File;
import java.util.Collection;

import com.bluejungle.framework.comp.PropertyKey;
import com.nextlabs.destiny.container.shared.customapps.IExternalApplication;

/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/customapps/IExternalReportApplication.java#1 $
 */

public interface IExternalReportApplication extends IExternalApplication {
    String UI_CONFIG_XSD = "/com/nextlabs/destiny/inquirycenter/customapps/ui-config.xsd";
    
    PropertyKey<File> TEMP_STORAGE_FOLDER = new PropertyKey<File>("temp storage folder");
    
    PropertyKey<Long> CLEAN_UP_FREQUENCY = new PropertyKey<Long>("inactive report clean up frequency");

    
    /**
     * load or reload all external report application
     * @throws ExternalReportAppException
     */
    void load() throws ExternalReportAppException;
    
    /**
     * get all custom app
     * @param sessionId
     * @param sessionLife
     * @return
     * @throws ExternalReportAppException
     */
    Collection<CustomAppJO> getAll(String sessionId, int sessionLife) throws ExternalReportAppException;
    
    /**
     * if the appId is not found, ignore and continue
     * @param sessionId
     * @param sessionLife, ttl in minutes.
     * @param appIds, if the appIds are empty, that means all apps
     */
    void hold(String sessionId, int sessionLife, long... appIds);
    
    /**
     * if the appId is not found, ignore and continue
     * @param sessionId
     * @param appIds, if the appIds are empty, that means all apps
     */
    void release(String sessionId, long... appIds);
}
