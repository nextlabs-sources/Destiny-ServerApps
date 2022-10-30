package com.nextlabs.destiny.cc.installer.config.properties;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.nextlabs.destiny.cc.installer.annotations.CcServicesStopped;
import com.nextlabs.destiny.cc.installer.annotations.ValidDbConnection;
import com.nextlabs.destiny.cc.installer.annotations.ValidDiskSpace;
import com.nextlabs.destiny.cc.installer.annotations.ValidEmptyLogQueue;
import com.nextlabs.destiny.cc.installer.annotations.ValidHigherUpgradeVersion;
import com.nextlabs.destiny.cc.installer.annotations.ValidInstallationPath;
import com.nextlabs.destiny.cc.installer.annotations.ValidInstallationPathAccess;
import com.nextlabs.destiny.cc.installer.annotations.ValidLicense;
import com.nextlabs.destiny.cc.installer.annotations.ValidLinuxSetcap;
import com.nextlabs.destiny.cc.installer.annotations.ValidManagementServer;
import com.nextlabs.destiny.cc.installer.annotations.ValidPassword;
import com.nextlabs.destiny.cc.installer.config.properties.validationgroups.DbValidation;
import com.nextlabs.destiny.cc.installer.enums.Component;
import com.nextlabs.destiny.cc.installer.enums.Environment;
import com.nextlabs.destiny.cc.installer.enums.OperatingSystem;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;
import com.nextlabs.destiny.cc.installer.validators.LicenseValidator;

/**
 * Properties available for control center configuration.
 *
 * @author Sachindra Dasun
 */
@ConfigurationProperties("nextlabs.cc")
@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.NONE
)
@ValidDbConnection(message = "{db.validDbConnection}", groups = DbValidation.class)
@ValidLinuxSetcap(message = "{linux.setcap.valid}")
@ValidDiskSpace(message = "{disk.freeSpace}")
@ValidEmptyLogQueue(message = "{logqueue.empty}")
@ValidHigherUpgradeVersion(message = "{upgrade.higherVersion}")
@ValidManagementServer(message = "{managementServer.valid}")
@ValidLicense(message = "{license.validLicense}")
@CcServicesStopped(message = "{ccServices.stopped}")
public class CcProperties {

    public static final Path CACERTS_FILE = Paths.get("cacerts.jks");
    public static final Path FPE_KEYSTORE_FILE = Paths.get("fpe-keystore.jks");
    public static final String CC_TYPE_COMPLETE = "COMPLETE";
    public static final String CC_TYPE_ICENET = "ICENET";
    public static final String CC_TYPE_MANAGEMENT_SERVER = "MANAGEMENT_SERVER";
    public static final String CONFIG_KEY_HOME = "Home";
    public static final String NEXTLABS_FOLDER_NAME = "Nextlabs";
    public static final String REGISTRY_KEY_CONTROL_CENTER = "SOFTWARE\\WOW6432Node\\NextLabs,Inc.\\ControlCenter";
    public static final String REGISTRY_VALUE_NAME_HOME = "Home";
    public static final String REGISTRY_VALUE_NAME_DATE = "Date";
    public static final String REGISTRY_VALUE_NAME_DMS_LOCATION = "DMSLocation";
    public static final String REGISTRY_VALUE_NAME_INSTALL_DIR = "InstallDir";
    public static final String REGISTRY_VALUE_NAME_VERSION = "Version";
    public static final Path SERVER_CONF_FILE_PATH = Paths.get("/etc", "CompliantEnterpriseServer", "server.conf");
    public static final String SERVER_CONF_KEY_INSTALL_HOME = "INSTALL_HOME";
    private String accessKey;
    @ValidPassword(message = "{password.valid}")
    private String adminPassword;
    private String activeMqBindAddress;
    private String build;
    @NestedConfigurationProperty
    @NotNull
    @Valid
    private CommProfileProperties commProfile = new CommProfileProperties();
    private String componentPrefix;
    @NestedConfigurationProperty
    @NotNull(message = "{db.notNull}")
    @Valid
    private DbProperties db = new DbProperties();
    private String dnsName;
    @NotNull(message = "{environment.notNull}")
    private Environment environment = Environment.STANDALONE;
    @NotEmpty(message = "{home.notEmpty}")
    private String home;
    @NotEmpty(message = "{hostname.notEmpty}")
    private String hostname;
    @ValidInstallationPath(message = "{installationPath.valid}")
    @ValidInstallationPathAccess(message = "{installationPath.validAccess}")
    private String installationPath;
    @NestedConfigurationProperty
    @NotNull
    @Valid
    private JavaProperties java = new JavaProperties();
    private String license;
    private String licenseFilePath;
    @NestedConfigurationProperty
    @NotNull
    private ManagementServerProperties managementServer = new ManagementServerProperties();
    private OperatingSystem operatingSystem;
    @NestedConfigurationProperty
    @NotNull(message = "{port.notNull}")
    @Valid
    private PortProperties port = new PortProperties();
    private String previousHome;
    private Version previousVersion;
    @NotNull(message = "{runningMode.notNull}")
    private RunningMode runningMode = RunningMode.COMMAND;
    private String serviceName;
    @NestedConfigurationProperty
    @NotNull(message = "{ssl.notNull}")
    @Valid
    private SslProperties ssl = new SslProperties();
    @NestedConfigurationProperty
    @NotNull
    @Valid
    private HealthCheckServiceProperties healthCheckService = new HealthCheckServiceProperties();
    @NotEmpty(message = "{type.notEmpty}")
    private String type;
    private boolean upgradeExisting;
    @NotNull
    @Valid
    private UserProperties user = new UserProperties();
    @NestedConfigurationProperty
    @NotNull(message = "{version.notNull}")
    @Valid
    private Version version = new Version("0.0.0.0");
    @NestedConfigurationProperty
    @NotNull(message = "waitFor.notNull")
    @Valid
    private WaitForProperties waitFor = new WaitForProperties();
    private boolean webInstaller;
    @NestedConfigurationProperty
    @NotNull(message = "oidc.notNull")
    @Valid
    private OidcProperties oidc = new OidcProperties();

    public CcProperties() {
        this.operatingSystem = SystemUtils.IS_OS_WINDOWS ? OperatingSystem.WINDOWS : OperatingSystem.LINUX;
        Path licensePath;
        if (RunningMode.UPGRADE.name().equals(System.getProperty("nextlabs.cc.running-mode"))) {
            licensePath = Paths.get(System.getProperty("nextlabs.cc.previous-home"), "server", "license", "license.dat");
        } else {
            licensePath = Paths.get(ParameterHelper.INIT_CC_HOME, "server", "license", "license.dat");
        }
        if (licensePath.toFile().exists() && new LicenseValidator().isValid(licenseFilePath, null)) {
            this.licenseFilePath = licensePath.toString();
        }
    }

    public void updateFrom(CcProperties ccProperties) {
        adminPassword = ccProperties.getAdminPassword();
        db.setPassword(ccProperties.getDb().getPassword());
        db.setUrl(ccProperties.getDb().getUrl());
        db.setUsername(ccProperties.getDb().getUsername());
        dnsName = ccProperties.getDnsName();
        serviceName = ccProperties.getServiceName();
        hostname = ccProperties.getHostname();
        installationPath = ccProperties.getInstallationPath();
        licenseFilePath = ccProperties.getLicenseFilePath();
        managementServer.setConfigServicePort(ccProperties.getManagementServer().getConfigServicePort());
        managementServer.setHost(ccProperties.getManagementServer().getHost());
        managementServer.setPassword(ccProperties.getManagementServer().getPassword());
        managementServer.setUsername(ccProperties.getManagementServer().getUsername());
        managementServer.setWebServicePort(ccProperties.getManagementServer().getWebServicePort());
        port.setActiveMqPort(ccProperties.getPort().getActiveMqPort());
        port.setAppServicePort(ccProperties.getPort().getAppServicePort());
        port.setPolicyValidatorPort(ccProperties.getPort().getPolicyValidatorPort());
        port.setConfigServicePort(ccProperties.getPort().getConfigServicePort());
        port.setWebServicePort(ccProperties.getPort().getWebServicePort());
        port.setServerShutdownPort(ccProperties.getPort().getServerShutdownPort());
        ssl.getKeystore().setPassword(ccProperties.getSsl().getKeystore().getPassword());
        ssl.getTruststore().setPassword(ccProperties.getSsl().getTruststore().getPassword());
        type = ccProperties.getType();
        upgradeExisting = ccProperties.isUpgradeExisting();
    }

    @JsonProperty
    public String getAdminPassword() {
        return this.adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = EncryptionHelper.decryptIfEncrypted(adminPassword);
    }

    @JsonProperty
    public DbProperties getDb() {
        return db;
    }

    public void setDb(DbProperties db) {
        this.db = db;
    }

    @JsonProperty
    public String getDnsName() {
        return StringUtils.isEmpty(dnsName) ? getServiceName() : dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    @JsonProperty
    public String getServiceName() {
        return StringUtils.isEmpty(serviceName) ? hostname : serviceName;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @JsonProperty
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @JsonProperty
    public String getInstallationPath() {
        return installationPath;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    @JsonProperty
    public String getLicenseFilePath() {
        return licenseFilePath;
    }

    public void setLicenseFilePath(String licenseFilePath) {
        this.licenseFilePath = licenseFilePath;
    }

    @JsonProperty
    public ManagementServerProperties getManagementServer() {
        return managementServer;
    }

    @JsonProperty
    public PortProperties getPort() {
        return port;
    }

    public void setPort(PortProperties port) {
        this.port = port;
    }

    @JsonProperty
    public SslProperties getSsl() {
        return ssl;
    }

    @JsonProperty
    public HealthCheckServiceProperties getHealthCheckService() {
        return healthCheckService;
    }

    public void setHealthCheckService(HealthCheckServiceProperties healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @JsonProperty
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toUpperCase();
        System.setProperty("nextlabs.cc.is-management-server", String.valueOf(isManagementServerInstance()));
    }

    public boolean isManagementServerInstance() {
        return StringUtils.isNotEmpty(type) && (type.contains(CcProperties.CC_TYPE_COMPLETE) ||
                type.contains(CcProperties.CC_TYPE_MANAGEMENT_SERVER) ||
                type.contains(Component.CONFIG_SERVICE.name()));
    }

    public void setSsl(SslProperties ssl) {
        this.ssl = ssl;
    }

    public void setManagementServer(ManagementServerProperties managementServer) {
        this.managementServer = managementServer;
    }

    public void setInstallationPath(String installationPath) {
        this.installationPath = installationPath;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @JsonProperty
    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    @JsonProperty
    public String getActiveMqBindAddress() {
        return activeMqBindAddress;
    }

    public void setActiveMqBindAddress(String activeMqBindAddress) {
        this.activeMqBindAddress = activeMqBindAddress;
    }

    @JsonProperty
    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    @JsonProperty
    public String getComponentPrefix() {
        if (StringUtils.isEmpty(componentPrefix)) {
            return isManagementServerInstance() ? getDnsName() : getHostname();
        }
        return componentPrefix;
    }

    public void setComponentPrefix(String componentPrefix) {
        this.componentPrefix = componentPrefix;
    }

    @JsonProperty
    public CommProfileProperties getCommProfile() {
        return commProfile;
    }

    public void setCommProfile(CommProfileProperties commProfile) {
        this.commProfile = commProfile;
    }

    @JsonProperty
    public UserProperties getUser() {
        return user;
    }

    public void setUser(UserProperties user) {
        this.user = user;
    }

    @JsonProperty
    public JavaProperties getJava() {
        return java;
    }

    public void setJava(JavaProperties java) {
        this.java = java;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    @JsonProperty
    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    @JsonProperty
    public String getPreviousHome() {
        return previousHome;
    }

    public void setPreviousHome(String previousHome) {
        this.previousHome = previousHome;
    }

    @JsonProperty
    public Version getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(Version previousVersion) {
        this.previousVersion = previousVersion;
    }

    @JsonProperty
    public RunningMode getRunningMode() {
        return runningMode;
    }

    public void setRunningMode(RunningMode runningMode) {
        this.runningMode = runningMode;
    }

    public boolean isWebInstaller() {
        return webInstaller;
    }

    public void setWebInstaller(boolean webInstaller) {
        this.webInstaller = webInstaller;
    }

    @JsonProperty
    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @JsonProperty
    public boolean isUpgradeExisting() {
        return upgradeExisting;
    }

    public void setUpgradeExisting(boolean upgradeExisting) {
        this.upgradeExisting = upgradeExisting;
    }

    @JsonProperty
    public WaitForProperties getWaitFor() {
        return waitFor;
    }

    public void setWaitFor(WaitForProperties waitFor) {
        this.waitFor = waitFor;
    }

    public OidcProperties getOidc() {
        return oidc;
    }

    public void setOidc(OidcProperties oidc) {
        this.oidc = oidc;
    }

}
