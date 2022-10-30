/*
 * Created on Mar 12, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.customapps;

import com.bluejungle.destiny.container.dcc.DCCResourceLocators;
import com.bluejungle.destiny.container.dcc.INamedResourceLocator;
import com.bluejungle.destiny.container.dcc.ServerRelativeFolders;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.IManagerEnabled;
import com.bluejungle.framework.comp.IStartable;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.utils.CollectionUtils;
import com.bluejungle.framework.utils.Formatter;
import com.nextlabs.destiny.container.shared.customapps.CustomAppDataManager;
import com.nextlabs.destiny.container.shared.customapps.InvalidCustomAppException;
import com.nextlabs.destiny.container.shared.customapps.hibernateimpl.CustomAppDO;
import com.nextlabs.destiny.container.shared.customapps.hibernateimpl.CustomApplicationDtoJConverter;
import com.nextlabs.destiny.container.shared.customapps.mapping.CustomReportJO;
import com.nextlabs.destiny.container.shared.customapps.mapping.JoHelper;
import com.nextlabs.destiny.container.shared.customapps.mapping.PolicyApplicationJO;
import com.nextlabs.destiny.container.shared.inquirymgr.customapps.hibernateimpl.CustomReportDataDO;
import com.nextlabs.destiny.container.shared.inquirymgr.customapps.hibernateimpl.CustomReportFileDO;
import com.nextlabs.destiny.container.shared.inquirymgr.customapps.hibernateimpl.CustomReportUIDO;
import com.nextlabs.destiny.inquirycenter.customapps.mapping.CustomReportUIJO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/customapps/ExternalReportAppManager.java#1 $
 */

public class ExternalReportAppManager implements IExternalReportApplication,
        IHasComponentInfo<IExternalReportApplication>, IConfigurable, IInitializable,
        IManagerEnabled, IStartable, ILogEnabled {
    private static final long DEFAULT_CLEANUP_FREQUENCY = 15 * 60 * 1000L;

    private static Log log = LogFactory.getLog(ExternalReportAppManager.class);

    private static final ComponentInfo<IExternalReportApplication> COMP_INFO =
            new ComponentInfo<IExternalReportApplication>(
                    ExternalReportAppManager.class,
                    LifestyleType.SINGLETON_TYPE);
    
    private class CustomAppEntry {
        final CustomAppJO customAppJO;
        final File folder;
        boolean isActive;
        ConcurrentMap<String, Long> activeSession;
        
        CustomAppEntry(CustomAppDO customAppDO) throws IOException, InvalidCustomAppException {
            isActive = true;
            
            //save the data to the local disk
            String folderName = customAppDO.getId() + "_" + customAppDO.getVersion().hashCode();

            folder = new File(ExternalReportAppManager.this.storageFolder, folderName);
            folder.mkdir();

            //don't save this to disk, this should be small enough to hold in memory
//            write(new File(folder, ExternalApplicationFileStructure.UI_CONFIG_XML_NAME), customAppDO.getReportUI().getFileContent());
//            customAppDO.getReportUI().setFileContent(null);

            Map<String, File> savedFiles = new HashMap<String, File>();
            
            for (CustomReportDataDO customReportDataDO : customAppDO.getCustomReports()) {
                for (CustomReportFileDO customReportFileDO : customReportDataDO.getReportDesignFiles()) {
                    File localFile = new File(folder, customReportFileDO.getName());
                    savedFiles.put(customReportFileDO.getName(), localFile);
                    write(localFile, customReportFileDO.getContent());
                    
                    //gc by itself
                    //customReportFileDO.setContent(null);
                }
            }
            
            // create jo
            PolicyApplicationJO policyApplicationJO = CustomApplicationDtoJConverter.convert(customAppDO); 

            String uiConfigContent = customAppDO.getReportUI().getFileContent();
            CustomReportUIJO customReportUIJO = new CustomReportUIJO();
            try {
                customReportUIJO = JoHelper.read(uiConfigContent, customReportUIJO);
            } catch (Exception e) {
                //this should already be tested
                throw new InvalidCustomAppException("Unable to parse ui-config.", e);
            }
            
            //replace all file with local path
            for (CustomReportJO customReportJO : policyApplicationJO.getCustomReports()) {
                List<String> designFileNames = customReportJO.getDesignFiles();
                int size = designFileNames.size();
                for (int i = 0; i < size; i++) {
                    String original = designFileNames.get(i);
                    File file = savedFiles.get(original);
                    if(file == null){
                        throw new InvalidCustomAppException("The design file, " + original + ", doesn't exist.");
                    }
                    designFileNames.set(i, file.getAbsolutePath());
                }
            }

            customAppJO = new CustomAppJO(
                    customAppDO.getId(), 
                    policyApplicationJO, 
                    customReportUIJO
            );
            

            activeSession = new ConcurrentHashMap<String, Long>();
        }

        void touch(String sessionId, long expiredTime) {
            activeSession.put(sessionId, expiredTime);
        }

        void touch(String sessionId, int ttl) {
            long expiredTime = ttl < 0 
                    ? Long.MAX_VALUE 
                    : System.currentTimeMillis() + ttl * 60 * 1000L;
            touch(sessionId, expiredTime);
        }

        void write(File file, String content) throws IOException {
            FileWriter fileWriter = new FileWriter(file);
            try {
                fileWriter.write(content);
            } finally {
                fileWriter.close();
            }
        }
        
        void release(String sessionId) {
            activeSession.remove(sessionId);
        }
        
        void delete(){
            getLog().info("CustomApp (" + customAppJO.getCustomAppId()
                    + ") will be deleted since it is not longer active.");
            ExternalReportAppManager.this.delete(folder);
        }
        
        boolean isActive(String sessionId){
            final long currentTime = System.currentTimeMillis();
            if(isActive){
                return true;
            }
            Long expiredTime = activeSession.get(sessionId);
            if(expiredTime != null){
                return expiredTime >= currentTime;
            }
            return false;
        }
    }
    
    private ConcurrentMap<Long, CustomAppEntry> currentApps;
    private File storageFolder;
    private Timer timer;
    
    private IConfiguration configuration;
    private IComponentManager componentManager;
    
    public void init() {
        currentApps = new ConcurrentHashMap<Long, CustomAppEntry>();
        if (configuration != null) {
            storageFolder = configuration.get(TEMP_STORAGE_FOLDER);
        }
        if(storageFolder == null){
            INamedResourceLocator serverResourceLocator = 
                (INamedResourceLocator)componentManager.getComponent(DCCResourceLocators.SERVER_HOME_RESOURCE_LOCATOR);
            String reportOutputDirFullPath = serverResourceLocator.getFullyQualifiedName(
                    ServerRelativeFolders.REPORTS_FOLDER.getPath());
            storageFolder = new File(reportOutputDirFullPath, "custom_apps_cache");
        }
        delete(storageFolder);
        storageFolder.mkdir();
        
        try {
            load();
        } catch (ExternalReportAppException e) {
            this.getLog().error("fail to load custom apps", e);
        }
    }
    
    public void load() throws ExternalReportAppException {
        CustomAppDataManager customAppDataManager = componentManager.getComponent(CustomAppDataManager.class);
        
        List<CustomAppDO> customAppDOs;
        try {
            customAppDOs = customAppDataManager.getAllCustomAppData();
        } catch (Exception e) {
            log.error("unable to get all custom app data", e);
            return;
        }
        assert customAppDOs != null;

        String message  = "The following app will be inactive: " + 
            CollectionUtils.asString(currentApps.values(), ", ", "", new Formatter<CustomAppEntry>() {
                public String toString(CustomAppEntry t) {
                    return Long.toString(t.customAppJO.getCustomAppId());
                }
            });
        log.info(message);
        
        for (CustomAppEntry e : currentApps.values()) {
            e.isActive = false;
        }

        if (customAppDOs.isEmpty()) {
            log.warn("no customApp is loaded.");
        } else {
            for (CustomAppDO customAppDO : customAppDOs) {
                try {
                    check(customAppDO);
                    currentApps.put(customAppDO.getId(), new CustomAppEntry(customAppDO));
                    log.info("customAppDO " + customAppDO.getName() + "," 
                            + customAppDO.getId() + " is loaded successfully.");
                } catch (InvalidCustomAppException e) {
                    log.error("The custom app (" + customAppDO.getName() + ", " + customAppDO.getId()
                            + ") will be skipped because the following reason.", e);
                    continue;
                } catch (IOException e) {
                    throw new ExternalReportAppException(e);
                }
            }
        }
    }
    
    protected void check(CustomAppDO customAppDO) throws InvalidCustomAppException{
        CustomReportUIDO customReportUIDO = customAppDO.getReportUI();
        if (customReportUIDO == null || customReportUIDO.getFileContent() == null) {
            throw new InvalidCustomAppException("The ui-config is not set.");
        }
        String xmlContent = customReportUIDO.getFileContent();
        readUiConfig(xmlContent);
        
    }
    
    protected CustomReportUIJO readUiConfig(String uiConfigContent){
        String schemaLocation = this.getClass().getResource(UI_CONFIG_XSD).toString();
        
        Collection<SAXParseException> errors;
        try {
            errors = JoHelper.validateXml(schemaLocation, NAMESPACE, uiConfigContent);
        } catch (Exception e) {
            throw new InvalidCustomAppException("Unable to validate ui-config.", e);
        }
        
        if (!errors.isEmpty()) {
            throw new InvalidCustomAppException("The ui-config doesn't match the schema. " 
                    + CollectionUtils.toString(errors));
        }
        
        try {
            return JoHelper.read(uiConfigContent, new CustomReportUIJO());
        } catch (Exception e) {
            throw new InvalidCustomAppException("Unable to parse ui-config.", e);
        }
    }
    
    protected void delete(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    delete(file);
                } else {
                    if(!file.delete()) {
                        log.warn("Unable to delete file " + file.getName() + ", this file will be overwritten.");
                    }
                }
            }
        }

        if(!folder.delete()) {
            log.warn("Unable to delete folder " + folder.getName());
        }
    }

    /**
     * @param sessionId
     * @param appId
     * @param sessionLife in minutes
     * @return
     */
    public Collection<CustomAppJO> getAll(String sessionId, int sessionLife)
            throws ExternalReportAppException {
    List<CustomAppJO> customAppJOs = new ArrayList<CustomAppJO>();
        
        for(Map.Entry<Long, CustomAppEntry> entry: currentApps.entrySet()){
            CustomAppEntry customAppEntry = entry.getValue();
            if (customAppEntry.isActive(sessionId)) {
                customAppEntry.touch(sessionId, sessionLife);
                customAppJOs.add(customAppEntry.customAppJO);
            }
        }
        
        return customAppJOs;
    }
    
    public void hold(String sessionId, int sessionLife, long... appIds) {
        if (appIds.length == 0) {
            for (CustomAppEntry e : currentApps.values()) {
                if (e.isActive(sessionId)) {
                    e.touch(sessionId, sessionLife);
                }
            }
        } else {
            for (long appId : appIds) {
                CustomAppEntry cae = currentApps.get(appId);
                if (cae != null && cae.isActive(sessionId)) {
                    cae.touch(sessionId, sessionLife);
                }
            }
        }
    }

    public void release(String sessionId, long... appIds) {
        if (appIds.length == 0) {
            for (CustomAppEntry e : currentApps.values()) {
                e.release(sessionId);
            }
        } else {
            for (long appId : appIds) {
                CustomAppEntry cae = currentApps.get(appId);
                if (cae != null) {
                    cae.release(sessionId);
                }
            }
        }
    }
    
//    public File getFile(CustomAppJO customAppJO, String name) throws ExternalReportAppException {
//        return getFile(customAppJO.getCustomAppId(), name);
//    }
//    
//    public File getFile(long appId, String name) throws ExternalReportAppException {
//        CustomAppEntry exitingApp = currentApps.get(appId);
//        if(exitingApp == null){
//            throw ExternalReportAppException.notFound(appId);
//        }
//        return new File(exitingApp.folder, name);
//    }
    
    /**
     * remove all expired inactive entry
     * @param timeout
     */
    void purge() {
        final long currentTime = System.currentTimeMillis();
        for (Map.Entry<Long, CustomAppEntry> cae : currentApps.entrySet()) {
            CustomAppEntry customAppEntry = cae.getValue();
            Map<String, Long> activeSession = customAppEntry.activeSession;
            for (Map.Entry<String, Long> ase : activeSession.entrySet()) {
                long lastAccessTime = ase.getValue();

                if (lastAccessTime < currentTime) {
                    activeSession.remove(ase.getKey());
                }
            }
            //only remove inactive expired entries.
            if (!customAppEntry.isActive && activeSession.isEmpty()) {
                currentApps.remove(cae.getKey());
                customAppEntry.delete();
            }
        }
    }
    
    public ComponentInfo<IExternalReportApplication> getComponentInfo() {
        return COMP_INFO;
    }

    public void start() {
        long frequency = configuration != null
                ? configuration.get(CLEAN_UP_FREQUENCY, DEFAULT_CLEANUP_FREQUENCY)
                : DEFAULT_CLEANUP_FREQUENCY;
        
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                purge();
            }
        }, frequency, frequency);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        log = log;
    }

    public IConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(IConfiguration config) {
        this.configuration = config;
    }

    public IComponentManager getManager() {
        return componentManager;
    }

    public void setManager(IComponentManager manager) {
        this.componentManager = manager;
    }
}
