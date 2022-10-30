package com.nextlabs.destiny.cc.installer.services.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;
import com.nextlabs.destiny.cc.installer.helpers.RegistryHelper;
import com.nextlabs.destiny.cc.installer.services.InstanceConfigurationService;
import com.nextlabs.destiny.cc.installer.services.ProgressService;
import com.nextlabs.destiny.cc.installer.services.ServiceManagementService;
import com.nextlabs.destiny.cc.installer.services.UninstallService;
import com.nextlabs.destiny.cc.installer.validators.CcServicesStoppedValidator;

/**
 * Service implementation for Control Center uninstall.
 *
 * @author Sachindra Dasun
 */
@Service
public class UninstallServiceImpl implements UninstallService {

    private static final Marker INSTALLER_CONSOLE_MARKER = MarkerFactory.getMarker("INSTALLER_CONSOLE");
    private static final Logger logger = LoggerFactory.getLogger(UninstallServiceImpl.class);

    @Autowired
    private CcProperties ccProperties;
    @Autowired
    private InstanceConfigurationService instanceConfigurationService;
    @Autowired
    private ProgressService progressService;
    @Autowired
    private ServiceManagementService serviceManagementService;

    /**
     * Perform Control center uninstall task.
     *
     * @throws InterruptedException if an error occurred
     * @throws IOException          if an error occurred
     */
    @Override
    public void uninstall() throws InterruptedException, IOException, InvocationTargetException, IllegalAccessException {
        try {
            // Skip uninstall task if existing Control Center installation is not found.
            if (!RunningMode.UPGRADE.equals(ccProperties.getRunningMode())) {
                logger.info(INSTALLER_CONSOLE_MARKER, "Existing Control Center installation not found.");
                return;
            }
            if (!new CcServicesStoppedValidator().isValid(null, null)) {
                logger.info(INSTALLER_CONSOLE_MARKER, "All running Control Center services must be stopped.");
                return;
            }
            logger.info(INSTALLER_CONSOLE_MARKER, "Uninstalling Control Center.");
            progressService.start();
            deleteShortcuts();
            deleteServices();
            deleteConfigurations();
            Files.write(Paths.get(ParameterHelper.INIT_CC_HOME, "action-cc-delete.txt"),
                    ccProperties.getPreviousHome().getBytes());
            logger.info("Control Center delete file created");
            progressService.setCurrentTask(Task.COMPLETE);
        } finally {
            progressService.stop();
            Thread.sleep(600);
        }
        complete();
    }

    private void deleteShortcuts() throws IOException {
        if (SystemUtils.IS_OS_WINDOWS) {
            String programDataFolder = System.getenv("PROGRAMDATA");
            if (StringUtils.isNotEmpty(programDataFolder)) {
                Path programsFolderPath = Paths.get(programDataFolder, "Microsoft", "Windows", "Start Menu",
                        "Programs");
                if (programsFolderPath.toFile().exists()) {
                    FileUtils.deleteDirectory(programsFolderPath.resolve("Control Center").toFile());
                    FileUtils.deleteDirectory(programsFolderPath.resolve("NextLabs Control Center").toFile());
                }
            }
        }
    }

    private void deleteServices() throws IOException {
        progressService.setCurrentTask(Task.REMOVE_SERVICES);
        serviceManagementService.deleteCCServices();
    }

    private void deleteConfigurations() throws InvocationTargetException, IllegalAccessException, IOException {
        progressService.setCurrentTask(Task.REMOVE_CONFIGURATIONS);
        if (SystemUtils.IS_OS_WINDOWS) {
            if (StringUtils.isNotEmpty(RegistryHelper.readString(RegistryHelper.HKEY_LOCAL_MACHINE,
                    CcProperties.REGISTRY_KEY_CONTROL_CENTER, CcProperties.REGISTRY_VALUE_NAME_HOME)) ||
                    StringUtils.isNotEmpty(RegistryHelper.readString(RegistryHelper.HKEY_LOCAL_MACHINE,
                            CcProperties.REGISTRY_KEY_CONTROL_CENTER, CcProperties.REGISTRY_VALUE_NAME_VERSION))) {
                RegistryHelper.deleteKey(RegistryHelper.HKEY_LOCAL_MACHINE, CcProperties.REGISTRY_KEY_CONTROL_CENTER);
            }
        } else if (SystemUtils.IS_OS_LINUX) {
            FileUtils.deleteDirectory(CcProperties.SERVER_CONF_FILE_PATH.getParent().toFile());
            Files.deleteIfExists(Paths.get("/etc", "ld.so.conf.d",
                    InstanceConfigurationServiceImpl.CC_JAVA_PERMISSION_FILE_NAME));
            instanceConfigurationService.runCommand("ldconfig");
        }
    }

    private void complete() {
        logger.info(INSTALLER_CONSOLE_MARKER, "Control Center uninstalled.");
    }

}
