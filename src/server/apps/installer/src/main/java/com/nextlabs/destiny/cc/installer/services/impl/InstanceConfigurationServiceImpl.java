package com.nextlabs.destiny.cc.installer.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.CommandOption;
import com.nextlabs.destiny.cc.installer.enums.Component;
import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.helpers.CommandLineOptionsHelper;
import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;
import com.nextlabs.destiny.cc.installer.helpers.RegistryHelper;
import com.nextlabs.destiny.cc.installer.services.InstanceConfigurationService;
import com.nextlabs.destiny.cc.installer.services.ProgressService;

/**
 * Service implementation for server management.
 *
 * @author Sachindra Dasun
 */
@Service
public class InstanceConfigurationServiceImpl implements InstanceConfigurationService {

    public static final String CC_INSTALLER_JAVA_PERMISSION_FILE_NAME = "nextlabs-cc-installer-java-libjli.conf";
    public static final String CC_JAVA_PERMISSION_FILE_NAME = "nextlabs-cc-java-libjli.conf";
    private static final String WINDOWS_SHORTCUT_FILE_CONTENT_FORMAT = "[InternetShortcut]%s%sURL=%s%sIconFile=%s";
    private static final Logger logger = LoggerFactory.getLogger(InstanceConfigurationServiceImpl.class);
    @Autowired
    private CcProperties ccProperties;
    @Autowired
    private ProgressService progressService;
    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    /**
     * Configure Control Center instance.
     *
     * @throws IOException               if an error occurred
     * @throws InvocationTargetException if an error occurred
     * @throws IllegalAccessException    if an error occurred
     */
    @Override
    public void configure() throws IOException, InvocationTargetException, IllegalAccessException, JSONException {
        progressService.setCurrentTask(Task.CONFIGURE_CONTROL_CENTER_SERVER);
        Set<Component> components = getComponents(ccProperties.getType());
        createServerXmlFile("server.xml", components);
        configureSetEnvFile();
        if (ccProperties.isManagementServerInstance()) {
            configurePolicyValidator();
        }
        if (ccProperties.getHealthCheckService().isEnabled()) {
            createHealthCheckService();
        }
        if (ccProperties.isWebInstaller() || CommandLineOptionsHelper.has(CommandOption.START)) {
            Path hostnameFolderPath = Paths.get(ccProperties.getHome(), "server", "logs",
                    ccProperties.getHostname());
            boolean hostnameFolderCreated = hostnameFolderPath.toFile().mkdirs();
            if (hostnameFolderCreated) {
                logger.info("Folder created: {}", hostnameFolderPath);
            }
            if (SystemUtils.IS_OS_WINDOWS) {
                createRegistryKeys();
                createShortcuts();
            } else if (SystemUtils.IS_OS_LINUX) {
                createServerConfFile();
                createUserGroup();
                createUser();
                changeFolderOwnership(ccProperties.getHome());
                changeFolderPermission(ccProperties.getHome());
                if (ccProperties.getPort().getAppServicePort() < 1024) {
                    grantRestrictedPortAccessPermission(CC_JAVA_PERMISSION_FILE_NAME);
                }
            }
        }
        configureServer(components);
    }

    private void createHealthCheckService() throws IOException {
        Path healthCheckServiceAppPath = Paths.get(ccProperties.getHome(), "server", "apps", "health-check-service.war");
        if (ccProperties.getHealthCheckService().isEnabled() && !healthCheckServiceAppPath.toFile().exists()) {
            try (FileSystem zipFileSystem = FileSystems.newFileSystem(
                    URI.create(String.format("jar:%s", healthCheckServiceAppPath.toUri())),
                    Collections.singletonMap("create", "true"))) {
                try (InputStream inputStream = getClass().getResourceAsStream("/health-check-service-content/status.html")) {
                    Files.copy(inputStream,
                            zipFileSystem.getPath(String.format("/%s", ccProperties.getHealthCheckService().getFileName())),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                try (InputStream inputStream = getClass().getResourceAsStream("/health-check-service-content/favicon.ico")) {
                    Files.copy(inputStream,
                            zipFileSystem.getPath("/favicon.ico"), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private Set<Component> getComponents(String type) {
        Set<Component> components = new HashSet<>();
        if (CommandLineOptionsHelper.has(CommandOption.CONFIGURE_INSTALLER)) {
            components.add(Component.INSTALLER);
            return components;
        }
        if (StringUtils.isNotEmpty(type)) {
            type = type.toUpperCase();
            if (type.contains(CcProperties.CC_TYPE_COMPLETE)) {
                components.add(Component.ADMINISTRATOR);
                components.add(Component.APP_HOME);
                components.add(Component.CAS);
                components.add(Component.CONFIG_SERVICE);
                components.add(Component.CONSOLE);
                components.add(Component.DABS);
                components.add(Component.DAC);
                components.add(Component.DCSF);
                components.add(Component.DEM);
                components.add(Component.DKMS);
                components.add(Component.DMS);
                components.add(Component.DPC);
                components.add(Component.DPS);
                components.add(Component.SERVICE_MANAGER);
                components.add(Component.POLICY_CONTROLLER_MANAGER);
                components.add(Component.REPORTER);
            } else if (type.contains(CcProperties.CC_TYPE_ICENET)) {
                components.add(Component.APP_HOME);
                components.add(Component.DABS);
                components.add(Component.DCSF);
                components.add(Component.DKMS);
                components.add(Component.DPC);
            } else if (type.contains(CcProperties.CC_TYPE_MANAGEMENT_SERVER)) {
                components.add(Component.ADMINISTRATOR);
                components.add(Component.APP_HOME);
                components.add(Component.CAS);
                components.add(Component.CONFIG_SERVICE);
                components.add(Component.CONSOLE);
                components.add(Component.DAC);
                components.add(Component.DCSF);
                components.add(Component.DEM);
                components.add(Component.DMS);
                components.add(Component.DPC);
                components.add(Component.DPS);
                components.add(Component.SERVICE_MANAGER);
                components.add(Component.POLICY_CONTROLLER_MANAGER);
                components.add(Component.REPORTER);
            } else {
                Arrays.stream(type.split(","))
                        .forEach(component -> {
                            try {
                                components.add(Component.valueOf(component.trim()));
                            } catch (IllegalArgumentException e) {
                                logger.error("Invalid component {}", component);
                            }
                        });
            }
        }
        return components;
    }

    private void configureServer(Set<Component> components) throws IOException {
        if (!CommandLineOptionsHelper.has(CommandOption.RUN)) {
            try (Stream<Path> pathStream = Files.walk(Paths.get(ccProperties.getHome(), "server", "apps"))) {
                List<Path> applicationFilesToDelete = pathStream.filter(path -> path.toFile().isFile() &&
                        !isRequiredApplication(path.getFileName().toString(), components))
                        .collect(Collectors.toList());
                for (Path applicationFilePath : applicationFilesToDelete) {
                    Files.deleteIfExists(applicationFilePath);
                }
            }
            if (!components.contains(Component.CONSOLE)) {
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "server", "data").toFile());
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "configuration", "dashboard.xml"));
            }
            if (!components.contains(Component.REPORTER)) {
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "configuration", "dashboard.xml"));
            }
            if (!components.contains(Component.CAS)) {
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "server", "license").toFile());
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "server", "license-validator").toFile());
            }
            if (!components.contains(Component.DMS)) {
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "configuration", "configuration.digester.rules.xml"));
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "configuration", "openaz-pep.properties"));
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "tools", "datasync").toFile());
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "tools", "dbInit").toFile());
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "tools", "enrollment").toFile());
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "tools", "genappldif").toFile());
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "tools", "importexport").toFile());
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "tools", "locationimporter").toFile());
                FileUtils.deleteDirectory(Paths.get(ccProperties.getHome(), "tools", "Seed_Data").toFile());
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "tools", "genappldif.bat"));
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "tools", "genappldif.sh"));
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "tools", "importLocations.sh"));
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "tools", "importLocations.bat"));
            }
            if (!components.contains(Component.CONFIG_SERVICE)) {
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "configuration", "configuration-template.xml"));
                Files.deleteIfExists(Paths.get(ccProperties.getHome(), "server", "configuration", "dbinit-logging.properties"));
            }
        }
    }

    private void createRegistryKeys() throws InvocationTargetException, IllegalAccessException {
        RegistryHelper.createKey(RegistryHelper.HKEY_LOCAL_MACHINE, CcProperties.REGISTRY_KEY_CONTROL_CENTER);
        RegistryHelper.writeStringValue(RegistryHelper.HKEY_LOCAL_MACHINE, CcProperties.REGISTRY_KEY_CONTROL_CENTER,
                CcProperties.REGISTRY_VALUE_NAME_HOME, ccProperties.getHome());
        if (StringUtils.isNotEmpty(RegistryHelper.readString(RegistryHelper.HKEY_LOCAL_MACHINE,
                CcProperties.REGISTRY_KEY_CONTROL_CENTER, CcProperties.REGISTRY_VALUE_NAME_VERSION))) {
            RegistryHelper.deleteValue(RegistryHelper.HKEY_LOCAL_MACHINE, CcProperties.REGISTRY_KEY_CONTROL_CENTER,
                    CcProperties.REGISTRY_VALUE_NAME_VERSION);
        }
        if (StringUtils.isNotEmpty(RegistryHelper.readString(RegistryHelper.HKEY_LOCAL_MACHINE,
                CcProperties.REGISTRY_KEY_CONTROL_CENTER, CcProperties.REGISTRY_VALUE_NAME_DATE))) {
            RegistryHelper.deleteValue(RegistryHelper.HKEY_LOCAL_MACHINE, CcProperties.REGISTRY_KEY_CONTROL_CENTER,
                    CcProperties.REGISTRY_VALUE_NAME_DATE);
        }
        if (StringUtils.isNotEmpty(RegistryHelper.readString(RegistryHelper.HKEY_LOCAL_MACHINE,
                CcProperties.REGISTRY_KEY_CONTROL_CENTER, CcProperties.REGISTRY_VALUE_NAME_DMS_LOCATION))) {
            RegistryHelper.deleteValue(RegistryHelper.HKEY_LOCAL_MACHINE, CcProperties.REGISTRY_KEY_CONTROL_CENTER,
                    CcProperties.REGISTRY_VALUE_NAME_DMS_LOCATION);
        }
        if (StringUtils.isNotEmpty(RegistryHelper.readString(RegistryHelper.HKEY_LOCAL_MACHINE,
                CcProperties.REGISTRY_KEY_CONTROL_CENTER, CcProperties.REGISTRY_VALUE_NAME_INSTALL_DIR))) {
            RegistryHelper.deleteValue(RegistryHelper.HKEY_LOCAL_MACHINE, CcProperties.REGISTRY_KEY_CONTROL_CENTER,
                    CcProperties.REGISTRY_VALUE_NAME_INSTALL_DIR);
        }
    }

    private void createShortcuts() throws IOException {
        String programDataFolder = System.getenv("PROGRAMDATA");
        if (StringUtils.isNotEmpty(programDataFolder)) {
            Path programsFolderPath = Paths.get(programDataFolder, "Microsoft", "Windows", "Start Menu",
                    "Programs");
            if (programsFolderPath.toFile().exists()) {
                FileUtils.deleteDirectory(programsFolderPath.resolve("Control Center").toFile());
                Path nextlabsControlCenterFolderPath = programsFolderPath.resolve("NextLabs Control Center");
                FileUtils.deleteDirectory(nextlabsControlCenterFolderPath.toFile());
                if (ccProperties.isManagementServerInstance()) {
                    Files.createDirectories(nextlabsControlCenterFolderPath);
                    createShortcut(nextlabsControlCenterFolderPath, "Console");
                    createShortcut(nextlabsControlCenterFolderPath, "Administrator");
                    createShortcut(nextlabsControlCenterFolderPath, "Reporter");
                }
            }
        }
    }

    private void createServerConfFile() throws IOException {
        File serverConfDirectory = CcProperties.SERVER_CONF_FILE_PATH.getParent().toFile();
        if (serverConfDirectory.exists()) {
            FileUtils.cleanDirectory(serverConfDirectory);
        } else {
            boolean pathCreated = serverConfDirectory.mkdirs();
            if (pathCreated) {
                logger.info("Directory path created {}", serverConfDirectory);
            }
        }
        Properties serverConfProperties = new Properties();
        serverConfProperties.put(CcProperties.CONFIG_KEY_HOME.toLowerCase(), ccProperties.getHome());
        try (OutputStream outputStream = new FileOutputStream(CcProperties.SERVER_CONF_FILE_PATH.toFile())) {
            serverConfProperties.store(outputStream, "NextLabs Control Center");
        }
    }

    private void createUserGroup() throws IOException {
        if (!isUserGroupExists()) {
            List<String> arguments = new ArrayList<>();
            arguments.add(ccProperties.getUser().getGroup());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CommandLine commandLine = new CommandLine("groupadd");
            arguments.forEach(commandLine::addArgument);
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
            logger.info("User group created {}", ccProperties.getUser().getGroup());
        }
    }

    private void createUser() throws IOException {
        if (!isUserExists()) {
            List<String> arguments = new ArrayList<>();
            arguments.add("-M");
            arguments.add("-c");
            arguments.add("NextLabs User");
            arguments.add("-d");
            arguments.add(ccProperties.getUser().getHome());
            arguments.add("-r");
            arguments.add("-g");
            arguments.add(ccProperties.getUser().getGroup());
            arguments.add(ccProperties.getUser().getName());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CommandLine commandLine = new CommandLine("useradd");
            arguments.forEach(commandLine::addArgument);
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
            logger.info("User created {} {}", ccProperties.getUser().getName(), ccProperties.getUser().getGroup());
        }
    }

    private String getServerXmlContent(boolean installer, Set<Component> components) {
        Context context = new Context();
        context.setVariables(getServerXmlProperties(installer, components));
        return springTemplateEngine.process("server", context);
    }

    private boolean isRequiredApplication(String applicationFileName, Set<Component> components) {
        if (ccProperties.getHealthCheckService().isEnabled() && "health-check-service.war".equals(applicationFileName)) {
            return true;
        }
        return components.stream().anyMatch(component -> component.getApplicationFileName().equals(applicationFileName))
                || Component.INSTALLER.getApplicationFileName().equals(applicationFileName);
    }

    private void createShortcut(Path shortcutFolderPath, String application) throws IOException {
        String url = String.format("https://%s%s/%s", ccProperties.getDnsName(),
                ccProperties.getPort().getExternalPort() == 443 ? "" :
                        String.format(":%s", ccProperties.getPort().getExternalPort()), application.toLowerCase());
        Files.write(shortcutFolderPath.resolve(String.format("%s.url", application)),
                String.format(WINDOWS_SHORTCUT_FILE_CONTENT_FORMAT, System.lineSeparator(),
                        System.lineSeparator(), url, System.lineSeparator(),
                        Paths.get(ccProperties.getHome(), "server", "images",
                                String.format("%s.ico", application.toLowerCase())).toString()).getBytes());
    }

    private boolean isUserGroupExists() {
        List<String> arguments = new ArrayList<>();
        arguments.add("group");
        arguments.add(ccProperties.getUser().getGroup());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine("getent");
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
        } catch (Exception e) {
            logger.info("User group {} not exists", ccProperties.getUser().getGroup());
            logger.debug("User group not exists", e);
        }
        String output = byteArrayOutputStream.toString();
        if (StringUtils.isNotEmpty(output) && output.startsWith(ccProperties.getUser().getGroup())) {
            logger.info("User group {} exists", ccProperties.getUser().getGroup());
            return true;
        } else {
            return false;
        }
    }

    private boolean isUserExists() {
        List<String> arguments = new ArrayList<>();
        arguments.add("passwd");
        arguments.add(ccProperties.getUser().getName());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine("getent");
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
        } catch (Exception e) {
            logger.info("User {} not exists", ccProperties.getUser().getName());
            logger.debug("User not exists", e);
        }
        String output = byteArrayOutputStream.toString();
        if (StringUtils.isNotEmpty(output) && output.startsWith(ccProperties.getUser().getName())) {
            logger.info("User {} exists", ccProperties.getUser().getName());
            return true;
        } else {
            return false;
        }
    }

    private void configureLibrary(String fileName) throws IOException {
        Path jliPath = Paths.get(ccProperties.getHome(), "java", "jre", "lib", "jli");
        Files.write(Paths.get("/etc", "ld.so.conf.d", fileName), jliPath.toString().getBytes());
        runCommand("ldconfig");
        logger.info("Configured libraries");
    }

    private Map<String, Object> getServerXmlProperties(boolean installer, Set<Component> components) {
        Map<String, Object> properties = new HashMap<>();
        int appServicePort = -1;
        if (!Collections.disjoint(components,
                Arrays.asList(Component.ADMINISTRATOR, Component.CAS, Component.CONSOLE, Component.REPORTER))) {
            appServicePort = ccProperties.getPort().getAppServicePort();
        }
        properties.put("serverShutdownPort", ccProperties.getPort().getServerShutdownPort());
        properties.put("appServicePort", appServicePort);
        properties.put("ccHome", ccProperties.getHome());
        properties.put("ccExternalName", ccProperties.getDnsName());
        properties.put("ccInternalName", ccProperties.getServiceName());
        properties.put("ccComponentNamePrefix", ccProperties.getComponentPrefix());
        int configServicePort = components.contains(Component.CONFIG_SERVICE) ?
                ccProperties.getPort().getConfigServicePort() : -1;
        properties.put("configServicePort", configServicePort);
        int installerServicePort = components.contains(Component.INSTALLER) ?
                ccProperties.getPort().getAppServicePort() : -1;
        properties.put("installerServicePort", installerServicePort);
        properties.put("keystorePass", EncryptionHelper.encrypt(installer ?
                ccProperties.getSsl().getInstallerKeystore().getPassword() :
                ccProperties.getSsl().getKeystore().getPassword()));
        properties.put("truststorePass", EncryptionHelper.encrypt(installer ?
                ccProperties.getSsl().getInstallerTruststore().getPassword() :
                ccProperties.getSsl().getTruststore().getPassword()));
        int webServicePort = -1;
        if (!Collections.disjoint(components,
                Arrays.asList(Component.DABS, Component.DAC, Component.DCSF, Component.DEM, Component.DKMS,
                        Component.DMS, Component.DPS))) {
            webServicePort = ccProperties.getPort().getWebServicePort();
        }
        properties.put("webServicePort", webServicePort);
        properties.put("managementServerHost", StringUtils.isEmpty(ccProperties.getManagementServer().getHost()) ?
                ccProperties.getServiceName() : ccProperties.getManagementServer().getHost());
        properties.put("managementServerWebServicePort", StringUtils.isEmpty(ccProperties.getManagementServer().getHost()) ?
                ccProperties.getPort().getWebServicePort() : ccProperties.getManagementServer().getWebServicePort());
        properties.put("components", components);
        properties.put("healthCheckServicePort", ccProperties.getHealthCheckService().isEnabled() ?
                ccProperties.getHealthCheckService().getPort() : -1);
        properties.put("healthCheckServiceProtocol", ccProperties.getHealthCheckService().getProtocol().name());
        properties.put("healthCheckServiceContextPath", ccProperties.getHealthCheckService().getContextPath());
        return properties;
    }

    /**
     * Create Apache Tomcat server.xml file required for the Control Center startup.
     *
     * @param fileName server.xml file name
     * @throws IOException if an error occurred
     */
    public void createServerXmlFile(String fileName, Set<Component> components) throws IOException {
        String serverXmlContent = getServerXmlContent(fileName.endsWith("-installer.xml"), components);
        Files.write(Paths.get(ccProperties.getHome(), "server", "configuration", fileName),
                serverXmlContent.getBytes());
        logger.info("Completed configuring Control Center server");
    }

    public void configureSetEnvFile() throws IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("CC_HOSTNAME", ccProperties.getHostname());
        StrSubstitutor strSubstitutor = new StrSubstitutor(parameters);
        Path setEnvFilePathWindows = Paths.get(ccProperties.getHome(), "server", "tomcat", "bin", "setenv.bat");
        Files.write(setEnvFilePathWindows, strSubstitutor.replace(new String(
                Files.readAllBytes(setEnvFilePathWindows), StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
        Path setEnvFilePathLinux = Paths.get(ccProperties.getHome(), "server", "tomcat", "bin", "setenv.sh");
        Files.write(setEnvFilePathLinux, strSubstitutor.replace(new String(
                Files.readAllBytes(setEnvFilePathLinux), StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Allow accessing restricted ports.
     *
     * @throws IOException if an error occurred
     */
    public void grantRestrictedPortAccessPermission(String fileName) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        List<String> arguments = new ArrayList<>();
        arguments.add("CAP_NET_BIND_SERVICE=+eip");
        arguments.add(Paths.get(ccProperties.getHome(), "java", "jre", "bin", "java").toString());
        CommandLine commandLine = new CommandLine(Paths.get( "/sbin", "setcap").toFile().exists() ?
                "/sbin/setcap" : "/usr/sbin/setcap");
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
        logger.info("Allowed access to restricted ports");
        configureLibrary(fileName);
    }

    public void changeFolderOwnership(String folder) throws IOException {
        List<String> arguments = new ArrayList<>();
        arguments.add("-R");
        arguments.add(String.format("%s:%s", ccProperties.getUser().getName(), ccProperties.getUser().getGroup()));
        arguments.add(folder);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine("chown");
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
        logger.info("Changed the ownership of {}", folder);
    }

    public void changeFolderPermission(String folder) throws IOException {
        List<String> arguments = new ArrayList<>();
        arguments.add("-R");
        arguments.add("o-rwx");
        arguments.add(folder);

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
        logger.info("Changed the directory permission of {}", folder);
    }

    /**
     * Run the provided command.
     *
     * @throws IOException if error occurred
     */
    public void runCommand(String command) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(command);
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
    }

    private void configurePolicyValidator() throws IOException, JSONException {
        Path policyValidatorPath = Paths.get(ccProperties.getHome(), "tools", "policy-validator");
        if(!policyValidatorPath.toFile().exists()) {
            return;
        }
        Path policyValidatorConfigFilePath = policyValidatorPath.resolve(Paths.get("data", "config", "config.json"));
        JSONObject configObject;
        try (Reader reader = Files.newBufferedReader(policyValidatorConfigFilePath)) {
            configObject = new JSONObject(IOUtils.toString(reader));
        }
        configObject.put("port", ccProperties.getPort().getPolicyValidatorPort());
        configObject.put("url", String.format("https://%s:%d/policy-validator",
                ccProperties.getDnsName().toLowerCase(), ccProperties.getPort().getPolicyValidatorPort()));
        configObject.put("ccUrl", String.format("https://%s%s", ccProperties.getDnsName().toLowerCase(),
                ccProperties.getPort().getExternalPort() == 443 ? "" :
                        String.format(":%s", ccProperties.getPort().getExternalPort())));
        JSONObject oidcProperties = (JSONObject) configObject.get("oidc");
        oidcProperties.put("clientSecret", ccProperties.getOidc().getClientSecret());

        JSONArray configurations = configObject.getJSONArray("configurations");
        for(int i = 0; i < configurations.length(); i++) {
            JSONObject configuration = configurations.getJSONObject(i);
            configuration.put("nextlabs.pdp.oauth2.ccip", ccProperties.getDnsName().toLowerCase());
            configuration.put("nextlabs.pdp.oauth2.ccport", ccProperties.getPort().getExternalPort());
        }

        try (Writer writer = Files.newBufferedWriter(policyValidatorConfigFilePath)) {
            IOUtils.write(configObject.toString(4), writer);
        }
        Path webCertificatePath = Paths.get(ccProperties.getHome(), "server", "certificates", "web.cer");
        if (Files.exists(webCertificatePath)) {
            FileUtils.copyFileToDirectory(webCertificatePath.toFile(), policyValidatorPath.resolve("certs").toFile());
        }
    }

}
