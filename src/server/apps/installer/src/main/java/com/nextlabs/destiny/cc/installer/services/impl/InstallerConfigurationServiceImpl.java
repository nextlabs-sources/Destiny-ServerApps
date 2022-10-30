package com.nextlabs.destiny.cc.installer.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.CommandOption;
import com.nextlabs.destiny.cc.installer.enums.Component;
import com.nextlabs.destiny.cc.installer.exceptions.InstallerException;
import com.nextlabs.destiny.cc.installer.helpers.CommandLineOptionsHelper;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;
import com.nextlabs.destiny.cc.installer.services.CertificateManagementService;
import com.nextlabs.destiny.cc.installer.services.InstallService;
import com.nextlabs.destiny.cc.installer.services.InstallerConfigurationService;
import com.nextlabs.destiny.cc.installer.services.InstanceConfigurationService;
import com.nextlabs.destiny.cc.installer.validators.CcServicesStoppedValidator;
import com.nextlabs.destiny.cc.installer.validators.PortValidator;

/**
 * Configure control center before running installer.
 *
 * @author Sachindra Dasun
 */
@Service
public class InstallerConfigurationServiceImpl implements InstallerConfigurationService {

    private static final Marker INSTALLER_CONSOLE_MARKER = MarkerFactory.getMarker("INSTALLER_CONSOLE");
    @Autowired
    private CcProperties ccProperties;
    @Autowired
    private CertificateManagementService certificateManagementService;
    @Autowired
    private InstallService installService;
    @Autowired
    private InstanceConfigurationService instanceConfigurationService;
    private Logger logger = LoggerFactory.getLogger(InstallerConfigurationServiceImpl.class);

    /**
     * Prepare Control Center installation environment.
     *
     * @throws IOException
     */
    public void configureInstaller() throws IOException {
        if (CommandLineOptionsHelper.has(CommandOption.UI)) {
            validateControlCenterServices();
            validateInstallerPort();
            if (SystemUtils.IS_OS_LINUX) {
                installService.grantExecutePermission(Paths.get(ccProperties.getHome(), "server", "tomcat",
                        "bin", "catalina.sh"));
                installService.grantExecutePermission(Paths.get(ccProperties.getHome(), "java", "jre", "bin",
                        "keytool"));
                if (CommandLineOptionsHelper.has(CommandOption.UI)
                        && ccProperties.getPort().getAppServicePort() < 1024) {
                    instanceConfigurationService.grantRestrictedPortAccessPermission(
                            InstanceConfigurationServiceImpl.CC_INSTALLER_JAVA_PERMISSION_FILE_NAME);
                }
            }
            certificateManagementService.createInstallerCertificate();
            instanceConfigurationService.createServerXmlFile("server-installer.xml",
                    Collections.singleton(Component.INSTALLER));
            instanceConfigurationService.configureSetEnvFile();
            generateInstallationPassword();
            Path cciInstallerRunFilePath = Paths.get(ParameterHelper.INIT_CC_HOME, "action-cc-installer-run.txt");
            boolean cciInstallerRunFileCreated = cciInstallerRunFilePath.toFile().createNewFile();
            if (cciInstallerRunFileCreated) {
                logger.info("Control Center installer run file created");
            }
        }
    }

    private void validateControlCenterServices() {
        boolean valid = new CcServicesStoppedValidator().isValid(null, null);
        if (!valid) {
            logger.info(INSTALLER_CONSOLE_MARKER, "All running Control Center services must be stopped.");
            throw new InstallerException("Running Control Center services found");
        }
    }

    private void validateInstallerPort() {
        boolean valid = new PortValidator().isValid(ccProperties.getPort().getAppServicePort(), null);
        if (!valid) {
            logger.info(INSTALLER_CONSOLE_MARKER, "The port {} must be open and all running Control Center services must be stopped.",
                    ccProperties.getPort().getAppServicePort());
            throw new InstallerException("Installer port is not open");
        }
    }

    private void generateInstallationPassword() throws IOException {
        String installationPassword = RandomStringUtils.random(12, true, true);
        Files.write(Paths.get(ccProperties.getHome(), "access-key.properties"),
                String.format("nextlabs.cc.access-key=%s", installationPassword).getBytes());
    }

}
