package com.nextlabs.destiny.cc.installer.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.nextlabs.cc.common.util.HostnameUtil;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.config.properties.Version;
import com.nextlabs.destiny.cc.installer.enums.CommandOption;
import com.nextlabs.destiny.cc.installer.enums.Environment;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.exceptions.InstallerException;

/**
 * Utility for initializing installer system parameters.
 *
 * @author Sachindra Dasun
 */
public class ParameterHelper {

    public static final String INIT_CC_HOME = System.getenv("NEXTLABS_CC_HOME");
    private static final Marker INSTALLER_CONSOLE_MARKER = MarkerFactory.getMarker("INSTALLER_CONSOLE");

    private ParameterHelper() {
    }

    public static void setInitParameters() throws InvocationTargetException, IllegalAccessException,
            IOException {
        configureConsoleLoggingForContainer();
        setHostname();
        RunningMode runningMode = setInstallationMode();
        setLicensePath(runningMode);
    }

    private static void configureConsoleLoggingForContainer() throws IOException {
        if (Environment.CONTAINER.name().equals(System.getenv("NEXTLABS_CC_ENVIRONMENT"))) {
            Path log4jConfigurationFilePath = Paths.get(INIT_CC_HOME, "server", "configuration", "log4j2-installer.xml");
            String log4jConfiguration = new String(Files.readAllBytes(log4jConfigurationFilePath))
                    .replace("<MarkerFilter marker=\"INSTALLER_CONSOLE\" onMatch=\"ACCEPT\" onMismatch=\"DENY\"/>", "")
                    .replace("${logging.pattern.console}", "${logging.pattern}");
            Files.write(log4jConfigurationFilePath, log4jConfiguration.getBytes());
        }
    }

    private static void setHostname() throws IOException {
        String hostname = System.getenv("NEXTLABS_CC_HOSTNAME");
        if (StringUtils.isEmpty(hostname)) {
            hostname = HostnameUtil.getHostname();
            if (hostname != null) {
                hostname = hostname.toLowerCase();
            }
        }
        System.setProperty("nextlabs.cc.hostname", hostname);
        System.setProperty("server.hostname", hostname);
    }

    private static RunningMode setInstallationMode() throws InvocationTargetException, IllegalAccessException, IOException {
        RunningMode runningMode = RunningMode.COMMAND;
        boolean isUpgrade = isUpgrade();
        if (!CommandLineOptionsHelper.isCommand()) {
            runningMode = isUpgrade ? RunningMode.UPGRADE : RunningMode.INSTALLATION;
        }
        System.setProperty("nextlabs.cc.running-mode", runningMode.name());
        return runningMode;
    }

    private static void setLicensePath(RunningMode runningMode) {
        Path licenseFilePath;
        if (runningMode == RunningMode.UPGRADE) {
            licenseFilePath = Paths.get(System.getProperty("nextlabs.cc.previous-home"), "server", "license", "license.dat");
        } else {
            licenseFilePath = Paths.get(ParameterHelper.INIT_CC_HOME, "server", "license", "license.dat");
        }
        if (licenseFilePath.toFile().exists()) {
            System.setProperty("nextlabs.cc.license-path", licenseFilePath.toString());
        }
    }

    private static boolean isUpgrade() throws InvocationTargetException, IllegalAccessException, IOException {
        String previousHome = null;
        if (SystemUtils.IS_OS_WINDOWS) {
            previousHome = RegistryHelper.readString(RegistryHelper.HKEY_LOCAL_MACHINE,
                    CcProperties.REGISTRY_KEY_CONTROL_CENTER, CcProperties.CONFIG_KEY_HOME);
            if (StringUtils.isEmpty(previousHome)) {
                previousHome = RegistryHelper.readString(RegistryHelper.HKEY_LOCAL_MACHINE,
                        CcProperties.REGISTRY_KEY_CONTROL_CENTER, CcProperties.REGISTRY_VALUE_NAME_INSTALL_DIR);
            }
        } else if (SystemUtils.IS_OS_LINUX) {
            File serverConfFile = CcProperties.SERVER_CONF_FILE_PATH.toFile();
            if (serverConfFile.exists()) {
                try (InputStream inputStream = new FileInputStream(serverConfFile)) {
                    Properties serverConfProperties = new Properties();
                    serverConfProperties.load(inputStream);
                    previousHome = serverConfProperties.getProperty(CcProperties.SERVER_CONF_KEY_INSTALL_HOME,
                            serverConfProperties.getProperty(CcProperties.CONFIG_KEY_HOME.toLowerCase()));
                }
                if (StringUtils.isEmpty(previousHome)) {
                    for (String line : new String(Files.readAllBytes(CcProperties.SERVER_CONF_FILE_PATH),
                            StandardCharsets.UTF_8).split("\\R")) {
                        if (line.startsWith("# INSTALL_HOME=")) {
                            previousHome = line.split("=")[1].trim();
                            break;
                        }
                    }
                }
            }
        }
        if (StringUtils.isNotEmpty(previousHome)) {
            previousHome = previousHome.replace("\"", "");
            String previousVersion = getVersionFromFile(previousHome);
            if (StringUtils.isEmpty(previousVersion)) {
                throw new InstallerException(
                        String.format("Unable to determine installed Control Center version from version.txt file at " +
                                "%s", previousHome)
                );
            } else {
                if (CommandLineOptionsHelper.has(CommandOption.WITH_VERSION_CHECK) &&
                        !new Version(previousVersion).before(new Version(getVersionFromFile(
                                ParameterHelper.INIT_CC_HOME)))) {
                    LoggerFactory.getLogger(ParameterHelper.class)
                            .info(INSTALLER_CONSOLE_MARKER, "Existing Control Center installation found.");
                    throw new InstallerException("Existing Control Center installation found");
                }
                System.setProperty("nextlabs.cc.previous-home", Paths.get(previousHome).toString());
                System.setProperty("nextlabs.cc.previous-version", previousVersion);
                return true;
            }
        }
        return false;
    }

    private static String getVersionFromFile(String ccHome) throws IOException {
        String version = null;
        Path versionTextFilePath = Paths.get(ccHome, "version.txt");
        if (versionTextFilePath.toFile().exists()) {
            for (String line : Files.readAllLines(versionTextFilePath)) {
                if (line.toLowerCase().contains("version") && (line.contains(":") || line.contains("="))) {
                    version = line.split("[:=]")[1].trim();
                    break;
                }
            }
        }
        return version;
    }

}
