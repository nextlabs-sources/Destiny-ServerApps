package com.nextlabs.destiny.cc.installer.services.impl;

import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.xml.sax.SAXException;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.config.properties.validationgroups.InstallValidationSequence;
import com.nextlabs.destiny.cc.installer.enums.CommandOption;
import com.nextlabs.destiny.cc.installer.enums.Environment;
import com.nextlabs.destiny.cc.installer.enums.OperatingSystem;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.helpers.CommandLineOptionsHelper;
import com.nextlabs.destiny.cc.installer.helpers.DbHelper;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;
import com.nextlabs.destiny.cc.installer.helpers.RegistryHelper;
import com.nextlabs.destiny.cc.installer.services.CertificateManagementService;
import com.nextlabs.destiny.cc.installer.services.ConfigurationManagementService;
import com.nextlabs.destiny.cc.installer.services.DataMigrationService;
import com.nextlabs.destiny.cc.installer.services.DbInitializationService;
import com.nextlabs.destiny.cc.installer.services.InstallService;
import com.nextlabs.destiny.cc.installer.services.InstanceConfigurationService;
import com.nextlabs.destiny.cc.installer.services.ProgressService;
import com.nextlabs.destiny.cc.installer.services.ServiceManagementService;

/**
 * Perform Control Center installation.
 *
 * @author Sachindra Dasun
 */
@Service
@Validated(InstallValidationSequence.class)
public class InstallServiceImpl implements InstallService {

    private static final Marker INSTALLER_CONSOLE_MARKER = MarkerFactory.getMarker("INSTALLER_CONSOLE");
    private static final Logger logger = LoggerFactory.getLogger(InstallServiceImpl.class);

    private CcProperties ccProperties;
    @Autowired
    private CertificateManagementService certificateManagementService;
    @Autowired
    private ConfigurationManagementService configurationManagementService;
    @Autowired
    private DataMigrationService dataMigrationService;
    @Autowired
    private DbInitializationService dbInitializationService;
    @Autowired
    private InstanceConfigurationService instanceConfigurationService;
    @Autowired
    private ProgressService progressService;
    @Autowired
    private ServiceManagementService serviceManagementService;

    public InstallServiceImpl(CcProperties ccProperties) {
        this.ccProperties = ccProperties;
    }

    /**
     * Crate marker files used to start Control Center. Created marker files are processed by the startup script.
     *
     * @throws IOException if an error occurred
     */
    public void createCcStartFiles() throws IOException {
        Path ccRunFilePath = Paths.get(ParameterHelper.INIT_CC_HOME, "action-cc-run.txt");
        Path ccStartFilePath = Paths.get(ParameterHelper.INIT_CC_HOME, "action-cc-start.txt");
        Path ccEsStartFilePath = Paths.get(ParameterHelper.INIT_CC_HOME, "action-cc-es-run.txt");
        Files.deleteIfExists(ccRunFilePath);
        Files.deleteIfExists(ccStartFilePath);
        Files.deleteIfExists(ccEsStartFilePath);
        if (CommandLineOptionsHelper.has(CommandOption.RUN)) {
            boolean runCcFileCreated = ccRunFilePath.toFile().createNewFile();
            if (runCcFileCreated) {
                logger.info("Control Center run file created");
            }
            if (!CommandLineOptionsHelper.has(CommandOption.NO_ES) && ccProperties.isManagementServerInstance()) {
                boolean runEsFileCreated = ccEsStartFilePath.toFile().createNewFile();
                if (runEsFileCreated) {
                    logger.info("Control Center Elasticsearch run file created");
                }
            }
        } else if (ccProperties.isWebInstaller() || CommandLineOptionsHelper.has(CommandOption.START)) {
            boolean startCcFileCreated = ccStartFilePath.toFile().createNewFile();
            if (startCcFileCreated) {
                logger.info("Control Center start file created");
            }
        }
    }

    /**
     * Perform Control Center installation.
     *
     * @param ccProperties installation properties
     * @return installation Future
     * @throws ParserConfigurationException if an error occurred
     * @throws SAXException                 if an error occurred
     * @throws IOException                  if an error occurred
     * @throws InvocationTargetException    if an error occurred
     * @throws IllegalAccessException       if an error occurred
     * @throws InterruptedException         if an error occurred
     */
    @Override
    @Async
    public Future<Boolean> install(@Valid CcProperties ccProperties) throws Exception {
        boolean installationCopied = false;
        try {
            progressService.setError(false);
            progressService.setCurrentTask(Task.VALIDATE);
            progressService.start();
            initInstallation();
            installationCopied = copyInstallation();
            grantPermissions();
            certificateManagementService.createCertificates();
            dbInitializationService.initialize();
            configurationManagementService.perform();
            certificateManagementService.uploadKeyStores();
            dataMigrationService.migrateData();
            if (CommandLineOptionsHelper.has(CommandOption.START) || ccProperties.isWebInstaller()) {
                serviceManagementService.createCCServices();
            }
            instanceConfigurationService.configure();
            progressService.setCurrentTask(Task.COMPLETE);
        } catch (Exception e) {
            logger.error("Installer Error: ", e);
            progressService.setError(true);
            progressService.sendProgress();
            rollback(installationCopied);
            throw e;
        } finally {
            progressService.stop();
            Files.deleteIfExists(Paths.get(ccProperties.getHome(), "access-key.properties"));
            Thread.sleep(600);
        }
        complete();
        return new AsyncResult<>(true);
    }

    private void initInstallation() {
        if (ccProperties.isManagementServerInstance()) {
            DbHelper.initJdbcTemplate(ccProperties.getDb());
        }
    }

    public boolean copyInstallation() throws IOException {
        progressService.setCurrentTask(Task.COPY_INSTALLATION);
        boolean createNextLabsFolder = true;
        if (ccProperties.getRunningMode() == RunningMode.UPGRADE && ccProperties.isUpgradeExisting()
                && ccProperties.getPreviousVersion().before(ccProperties.getVersion())) {
            Path previousInstallationPath = Paths.get(ccProperties.getPreviousHome());
            Path renamedPreviousInstallationPath = Files.move(previousInstallationPath,
                    previousInstallationPath.getParent()
                            .resolve(String.format("%s-%s", previousInstallationPath.getFileName(),
                                    ccProperties.getPreviousVersion().getCcVersion())));
            logger.info("Previous installation renamed to {}", renamedPreviousInstallationPath);
            ccProperties.setInstallationPath(renamedPreviousInstallationPath.getParent().toFile()
                    .getCanonicalPath());
            createNextLabsFolder = false;
            ccProperties.setPreviousHome(renamedPreviousInstallationPath.toString());
        }
        if (StringUtils.isNotBlank(ccProperties.getInstallationPath())) {
            Path installationPath = createInstallationPath(ccProperties.getInstallationPath(), createNextLabsFolder);
            FileUtils.copyDirectory(Paths.get(ccProperties.getHome()).toFile(), installationPath.toFile());
            Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "license", "license.dat"));
            ccProperties.setHome(installationPath.toString());
            FileUtils.cleanDirectory(Paths.get(ccProperties.getHome(), "server", "logs").toFile());
            Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "configuration", "server-installer.xml"));
            Files.deleteIfExists(Paths.get(ccProperties.getHome(), "installation.properties"));
            if (SystemUtils.IS_OS_WINDOWS) {
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "install.bat"));
            } else {
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "install.sh"));
            }
            FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "server", "tomcat", "installerapp").toFile());
            return true;
        }
        return false;
    }

    private void grantPermissions() throws IOException {
        if (SystemUtils.IS_OS_LINUX) {
            grantExecutePermission(Paths.get(ccProperties.getHome(), "java", "jre", "bin", "java"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "java", "jre", "bin", "keytool"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "server", "data", "search-index", "bin", "elasticsearch"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "server", "data", "search-index", "modules", "x-pack-ml", "platform", "linux-x86_64", "bin", "controller"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "server", "tomcat", "bin", "startup.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "server", "tomcat", "bin", "catalina.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "service-stop.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "service-start.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "uninstall.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "crypt", "mkpassword.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "datasync", "datasync.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "enrollment", "clientInfoMgr.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "enrollment", "enrollmgr.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "enrollment", "propertymgr.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "enrollmentPreview", "enrollmentPreview.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "importexport", "createSeedData.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "importexport", "export.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "importexport", "import.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "jbosscrypt", "mkpassword.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "keymanagement", "keymanagement.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "genappldif.sh"));
            grantExecutePermission(Paths.get(ccProperties.getHome(), "tools", "importLocations.sh"));
        }
    }

    private void rollback(boolean installationCopied) {
        try {
            try (Stream<Path> pathStream = Files.list(Paths.get(ParameterHelper.INIT_CC_HOME, "server", "certificates"))) {
                pathStream.filter(path -> !"cacerts".equals(path.getFileName().toString())
                        && Arrays.stream(CertificateManagementServiceImpl.DEFAULT_CERTIFICATES)
                        .noneMatch(fileName -> fileName.equals(path.getFileName().toString())))
                        .forEach(path -> FileUtils.deleteQuietly(path.toFile()));
            }
            Path certificateDirectoryPath = Paths.get(ParameterHelper.INIT_CC_HOME, "server", "certificates");
            FileUtils.deleteDirectory(certificateDirectoryPath.resolve("override").toFile());
            FileUtils.cleanDirectory(certificateDirectoryPath.resolve("cacerts").toFile());
            Files.deleteIfExists(Paths.get(ParameterHelper.INIT_CC_HOME, "server", "configuration", "server.xml"));
            Files.deleteIfExists(Paths.get(ParameterHelper.INIT_CC_HOME, "server", "configuration", "application.properties"));
            Files.deleteIfExists(Paths.get(ParameterHelper.INIT_CC_HOME, "server", "configuration", "bootstrap.properties"));
            if (ccProperties.getRunningMode() == RunningMode.INSTALLATION) {
                if (SystemUtils.IS_OS_WINDOWS) {
                    if (StringUtils.isNotEmpty(RegistryHelper.readString(RegistryHelper.HKEY_LOCAL_MACHINE,
                            CcProperties.REGISTRY_KEY_CONTROL_CENTER, CcProperties.CONFIG_KEY_HOME))) {
                        RegistryHelper.deleteKey(RegistryHelper.HKEY_LOCAL_MACHINE, CcProperties.REGISTRY_KEY_CONTROL_CENTER);
                    }
                } else if (SystemUtils.IS_OS_LINUX) {
                    File serverConfDirectory = CcProperties.SERVER_CONF_FILE_PATH.getParent().toFile();
                    if (serverConfDirectory.exists()) {
                        FileUtils.deleteQuietly(serverConfDirectory);
                    }
                }
                serviceManagementService.deleteCCServices();
            }
            if (installationCopied && !ParameterHelper.INIT_CC_HOME.equals(ccProperties.getHome())) {
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome()).toFile());
                String initPreviousHome = System.getProperty("nextlabs.cc.previous-home");
                if (ccProperties.getRunningMode() == RunningMode.UPGRADE
                        && StringUtils.isNotEmpty(ccProperties.getPreviousHome())
                        && StringUtils.isNotEmpty(initPreviousHome)
                        && !initPreviousHome.equals(ccProperties.getPreviousHome())) {
                    FileUtils.moveDirectory(Paths.get(ccProperties.getPreviousHome()).toFile(),
                            Paths.get(initPreviousHome).toFile());
                }
            }
            logger.info("Control Center installation rollback completed");
        } catch (Exception e) {
            logger.error("Error during Control Center installation rollback", e);
        }
    }

    public void complete() {
        if (ccProperties.isManagementServerInstance()) {
            String controlCenterUrl = String.format("https://%s%s", ccProperties.getDnsName(),
                    ccProperties.getPort().getExternalPort() == 443 ? "" :
                            String.format(":%s", ccProperties.getPort().getExternalPort()));
            if (ccProperties.getDb().isOsAuthentication()) {
                logger.info(INSTALLER_CONSOLE_MARKER, "Before starting Control Center, configure the Log On account for " +
                        "the Control Center service to enable OS authentication for Microsoft SQL Server. " +
                        "For more information, refer to the Control Center Installation and Upgrade Guide.");
            }
            String startInstruction = String.format("After Control Center has started, the Control Center is available at %s",
                    controlCenterUrl);
            logger.info(INSTALLER_CONSOLE_MARKER, startInstruction);
        } else if (ccProperties.getOperatingSystem() == OperatingSystem.WINDOWS) {
            logger.info(INSTALLER_CONSOLE_MARKER, "If you are using OS authentication for Microsoft SQL Server, " +
                    "before starting Control Center, configure the Log On account for " +
                    "the Control Center service to enable OS authentication for Microsoft SQL Server. " +
                    "For more information, refer to the Control Center Installation and Upgrade Guide.\n");
        }
        logger.info("Control Center installation completed");
    }

    private Path createInstallationPath(String path, boolean createNextLabsFolder) throws IOException {
        String ccFolderName = Paths.get(ccProperties.getHome()).toFile().getCanonicalFile().getName();
        Path installationPath = Paths.get(path);
        if (createNextLabsFolder && (installationPath.getFileName() == null ||
                !StringUtils.equalsIgnoreCase(installationPath.getFileName().toString(), CcProperties.NEXTLABS_FOLDER_NAME))) {
            installationPath = installationPath.resolve(CcProperties.NEXTLABS_FOLDER_NAME);
            if (installationPath.toFile().mkdirs()) {
                logger.info("Installation path {} directory created", CcProperties.NEXTLABS_FOLDER_NAME);
                if (SystemUtils.IS_OS_LINUX) {
                    instanceConfigurationService.changeFolderOwnership(installationPath.toString());
                }
            }
        }
        installationPath = installationPath.resolve(ccFolderName);
        if (installationPath.toFile().mkdirs()) {
            logger.info("Installation path {} directory created", ccFolderName);
        }
        return installationPath;
    }

    /**
     * Grant Linux execute permission to the given file.
     *
     * @param filePath to grant the permission
     * @throws IOException if an error occurred
     */
    public void grantExecutePermission(Path filePath) throws IOException {
        // Execute permission is already granted to files when running as a container.
        if (ccProperties.getEnvironment() == Environment.CONTAINER) {
            return;
        }
        if (filePath.toFile().exists()) {
            List<String> arguments = new ArrayList<>();
            arguments.add("+x");
            arguments.add(filePath.toString());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CommandLine commandLine = new CommandLine("chmod");
            arguments.forEach(argument -> commandLine.addArgument(argument, false));
            logger.trace("Running command: {}", commandLine);
            DefaultExecutor defaultExecutor = new DefaultExecutor();
            defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
            try {
                defaultExecutor.execute(commandLine);
            } finally {
                String output = byteArrayOutputStream.toString();
                if (StringUtils.isNotEmpty(output)) {
                    logger.info(output);
                }
            }
            logger.info("Granted execute permission to {}", filePath);
        }
    }

}
