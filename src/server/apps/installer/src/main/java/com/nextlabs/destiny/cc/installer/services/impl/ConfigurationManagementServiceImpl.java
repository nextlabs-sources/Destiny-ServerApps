package com.nextlabs.destiny.cc.installer.services.impl;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.UnaryOperator;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.nextlabs.cc.common.enums.DbType;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.config.properties.Version;
import com.nextlabs.destiny.cc.installer.enums.Application;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.helpers.DbHelper;
import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;
import com.nextlabs.destiny.cc.installer.helpers.XmlFileHelper;
import com.nextlabs.destiny.cc.installer.services.ConfigurationManagementService;
import com.nextlabs.destiny.cc.installer.services.ProgressService;

/**
 * Service implementation for configuration management.
 *
 * @author Sachindra Dasun
 */
@Service
public class ConfigurationManagementServiceImpl implements ConfigurationManagementService {

    public static final String SQL_GET_VALUE = "SELECT VALUE FROM SYS_CONFIG WHERE APPLICATION = ? AND CONFIG_KEY = ?";
    private static final Map<String, CombiningMapping> COMBINING_KEY_MAPPINGS_CONFIGURATION_XML = new HashMap<>();
    private static final String SQL_CONFIG_KEYS = "SELECT APPLICATION, CONFIG_KEY, ENCRYPTED FROM SYS_CONFIG";
    private static final String SQL_IMPORTANT_CONFIGURATIONS = "SELECT APPLICATION, CONFIG_KEY, VALUE FROM SYS_CONFIG WHERE APPLICATION = ? AND CONFIG_KEY IN (?, ?)";
    private static final String SQL_SELECT_CONFIG = "SELECT VALUE, DEFAULT_VALUE, VALUE_FORMAT FROM SYS_CONFIG WHERE APPLICATION = ? AND CONFIG_KEY = ?";
    private static final String SQL_UPDATE = "UPDATE SYS_CONFIG SET VALUE = ?, DEFAULT_VALUE = ? WHERE APPLICATION = ? AND CONFIG_KEY = ?";
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManagementServiceImpl.class);
    @Autowired
    private CcProperties ccProperties;
    @Autowired
    private Environment environment;
    @Autowired
    private ProgressService progressService;
    private String passwordEncodeSecret;

    /**
     * Handle the Control Center system configuration update during the installation.
     *
     * @throws IOException                  if error occurred
     * @throws ParserConfigurationException if XML parsing error occurred
     * @throws SAXException                 if XML parsing error occurred
     */
    @Override
    public void perform() throws IOException, ParserConfigurationException, SAXException {
        progressService.setCurrentTask(Task.CONFIGURE_CONTROL_CENTER);
        if (ccProperties.isManagementServerInstance()) {
            Version versionFromDb = ccProperties.getDb().getCcVersionFromDb();
            obtainOidcClientSecretFromDb();
            if (versionFromDb == null || versionFromDb.before(ccProperties.getVersion())) {
                if (ccProperties.getRunningMode() == RunningMode.INSTALLATION) {
                    saveInstallationConfigurations();
                    saveConfigClientPassword();
                } else if (ccProperties.getRunningMode() == RunningMode.UPGRADE) {
                    if (ccProperties.getPreviousVersion().before(Version.V_9_0_0_0)) {
                        initCombiningKeyMappings();
                        saveToDb(Application.CONSOLE, getConfigsFromPropertiesFile(Application.CONSOLE));
                        saveToDb(Application.CAS, getConfigsFromPropertiesFile(Application.CAS));
                        saveToDb(Application.ADMINISTRATOR, getConfigsFromPropertiesFile(Application.ADMINISTRATOR));
                        saveToDb(Application.REPORTER, getConfigsFromPropertiesFile(Application.REPORTER));
                        saveToDb(Application.DMS, getConfigsFromConfigurationXmlFile());
                        saveToDb(Application.DMS, getCombiningConfigsFromXmlFile());
                        saveInstallationConfigurations();
                        saveConfigClientPassword();
                    }
                    if (ccProperties.getPreviousVersion().before(Version.V_2021_03)) {
                        updateExistingConfigurations();
                    }
                }
                setCCVersion();
            }
            updateInstallerProvidedConfigurations();
            createConfigOverridesSample();
        }
        createPropertyFiles();
        logger.info("Completed configuring control center");
    }

    private void obtainOidcClientSecretFromDb() {
        SqlRowSet sqlRowSet = DbHelper.getJdbcTemplate()
                .queryForRowSet(SQL_SELECT_CONFIG, Application.APPLICATION.getApplicationName(),
                        "cc-oidc-config.services[0].clientSecret");
        if (sqlRowSet.next()) {
            String value = sqlRowSet.getString("VALUE");
            if (StringUtils.isNotEmpty(value)) {
                ccProperties.getOidc().setClientSecret(EncryptionHelper.decryptIfEncrypted(value));
            }
        }
    }

    private void updateExistingConfigurations() {
        Application.APPLICATION.getExistingValueModifiers().forEach((configKey, modifier) -> {
            SqlRowSet sqlRowSet = DbHelper.getJdbcTemplate()
                    .queryForRowSet(SQL_SELECT_CONFIG, Application.APPLICATION.getApplicationName(), configKey);
            if (sqlRowSet.next()) {
                String existingValue = sqlRowSet.getString("VALUE");
                String existingDefaultValue = sqlRowSet.getString("DEFAULT_VALUE");
                String modifiedValue = modifier.apply(existingValue);
                String modifiedDefaultValue = modifier.apply(existingDefaultValue);
                if ((existingValue != null && !existingValue.equals(modifiedValue))
                        || (existingDefaultValue != null && !existingDefaultValue.equals(modifiedDefaultValue)))
                    DbHelper.getJdbcTemplate().update(SQL_UPDATE, modifiedValue,
                            modifiedDefaultValue, Application.APPLICATION.getApplicationName(), configKey);
            }
        });
    }

    private void updateInstallerProvidedConfigurations() {
        SqlRowSet sqlRowSet = DbHelper.getJdbcTemplate()
                .queryForRowSet(SQL_CONFIG_KEYS);
        List<Object[]> batch = new ArrayList<>();
        while (sqlRowSet.next()) {
            String application = sqlRowSet.getString("APPLICATION");
            String configKey = sqlRowSet.getString("CONFIG_KEY");
            boolean encrypt = sqlRowSet.getBoolean("ENCRYPTED");
            if(application == null || configKey == null) {
                continue;
            }
            String environmentVariableKey = String.format("NEXTLABS_CC_%s_%s",
                    application.toUpperCase().replace("-", ""),
                    configKey.toUpperCase().replaceAll("[.\\[\\]]", "_")
                            .replace("-", "")
                            .replace("__", "_"));
            String value = environment.getProperty(environmentVariableKey);
            if (value == null) {
                String propertyKey = String.format("nextlabs.cc.%s.%s",
                        application.replace("-", ""), configKey);
                value = environment.getProperty(propertyKey);
            }
            if (value != null) {
                String modifiedValue = modifyValue(application, null, configKey, encrypt, value);
                batch.add(new Object[]{modifiedValue, modifiedValue, application, configKey});
            }
        }
        DbHelper.getJdbcTemplate().batchUpdate(SQL_UPDATE, batch);
    }

    private void setCCVersion() {
        DbHelper.getJdbcTemplate().update(SQL_UPDATE, ccProperties.getVersion().toString(),
                ccProperties.getVersion().toString(), Application.APPLICATION.getApplicationName(),
                "application.version");
        if (StringUtils.isNotEmpty(ccProperties.getBuild())) {
            DbHelper.getJdbcTemplate().update(SQL_UPDATE, ccProperties.getBuild(),
                    ccProperties.getBuild(), Application.APPLICATION.getApplicationName(),
                    "application.build");
        }
    }

    private void createConfigOverridesSample() throws IOException {
        try (FileWriter fileWriter = new FileWriter(Paths.get(ccProperties.getHome(), "server", "configuration",
                "config-overrides-sample.properties").toFile())) {
            SqlRowSet sqlRowSet = DbHelper.getJdbcTemplate()
                    .queryForRowSet(SQL_IMPORTANT_CONFIGURATIONS, "application",
                            "server.name", "web.service.server.name");
            while (sqlRowSet.next()) {
                fileWriter.write(String.format("#%s.%s=%s", sqlRowSet.getString("APPLICATION"),
                        sqlRowSet.getString("CONFIG_KEY"), sqlRowSet.getString("VALUE")));
                fileWriter.write(System.lineSeparator());
            }
        }
    }

    private void saveInstallationConfigurations() {
        Map<String, String> applicationConfigurations = new HashMap<>();
        Map<String, String> configServiceConfigurations = new HashMap<>();
        Map<String, String> consoleConfigurations = new HashMap<>();

        applicationConfigurations.put("application.server.name", String.format("https://%s%s", ccProperties.getDnsName(),
                ccProperties.getPort().getExternalPort() == 443 ? "" :
                        String.format(":%s", ccProperties.getPort().getExternalPort())));
        applicationConfigurations.put("application.web.service.server.name", String.format("https://%s:%d", ccProperties.getServiceName(),
                ccProperties.getPort().getWebServicePort()));
        applicationConfigurations.put("application.config.activeMQConnectionFactory.brokerURL", String.format("failover:(ssl://%s:%d)",
                ccProperties.getServiceName(), ccProperties.getPort().getActiveMqPort()));
        applicationConfigurations.put("application.cc-oidc-config.services[0].clientSecret",
                EncryptionHelper.encrypt(ccProperties.getOidc().getClientSecret()));

        String currentValueOfKeyStorePassword = DbHelper.getJdbcTemplate().queryForObject(ConfigurationManagementServiceImpl.SQL_GET_VALUE,
                String.class, "application", "key.store.password");
        if (StringUtils.isEmpty(currentValueOfKeyStorePassword)) {
            applicationConfigurations.put("application.key.store.password", EncryptionHelper.encrypt(ccProperties.getSsl().getKeystore().getPassword()));
        }

        String currentValueOfTrustStorePassword = DbHelper.getJdbcTemplate().queryForObject(ConfigurationManagementServiceImpl.SQL_GET_VALUE,
                String.class, "application", "trust.store.password");
        if (StringUtils.isEmpty(currentValueOfTrustStorePassword)) {
            applicationConfigurations.put("application.trust.store.password", EncryptionHelper.encrypt(ccProperties.getSsl().getTruststore().getPassword()));
        }

        configServiceConfigurations.put("config-service.activemq.broker.connector.bindAddress",
                String.format("ssl://%s:%s", ccProperties.getActiveMqBindAddress(), ccProperties.getPort().getActiveMqPort()));
        String currentValueOfActiveMQPassword = DbHelper.getJdbcTemplate().queryForObject(ConfigurationManagementServiceImpl.SQL_GET_VALUE,
                String.class, "config-service", "activemq.broker.password");
        if (StringUtils.isEmpty(currentValueOfActiveMQPassword)) {
            String activeMQPassword = EncryptionHelper.encrypt(RandomStringUtils.random(32, true, true));
            applicationConfigurations.put("application.config.activeMQConnectionFactory.password", activeMQPassword);
            configServiceConfigurations.put("config-service.activemq.broker.password", activeMQPassword);
        }

        String currentValueOfPasswordEncodeSecret = DbHelper.getJdbcTemplate().queryForObject(ConfigurationManagementServiceImpl.SQL_GET_VALUE,
                String.class, "application", "pbkdf2.encoding.secret.key");
        if (StringUtils.isEmpty(currentValueOfPasswordEncodeSecret)) {
            configServiceConfigurations.put("application.pbkdf2.encoding.secret.key", this.passwordEncodeSecret);
        }

        consoleConfigurations.put("console.policyValidator.url", String.format("https://%s:%d/policy-validator", ccProperties.getDnsName(),
                ccProperties.getPort().getPolicyValidatorPort()));

        saveToDb(Application.APPLICATION, applicationConfigurations);
        saveToDb(Application.CONFIG_SERVICE, configServiceConfigurations);
        saveToDb(Application.CONSOLE, consoleConfigurations);
    }

    /**
     * This is for mappings that are multiple elements in older configuration.xml files, but are combined into single
     * elements in the config code
     */
    private void initCombiningKeyMappings() {
        COMBINING_KEY_MAPPINGS_CONFIGURATION_XML.put("dms.dabs.trusted.domains", CombiningMapping.build("/DestinyConfiguration/DABS/TrustedDomainsConfiguration/MutuallyTrusted", ";"));
        COMBINING_KEY_MAPPINGS_CONFIGURATION_XML.put("dms.dac.index.rebuild.days.of.week", CombiningMapping.build("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/IndexesRebuildOperation/DaysOfWeek/DayOfWeek", ","));
        COMBINING_KEY_MAPPINGS_CONFIGURATION_XML.put("dms.dac.index.rebuild.days.of.month", CombiningMapping.build("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/IndexesRebuildOperation/DaysOfMonth/DayOfMonth", ","));
        COMBINING_KEY_MAPPINGS_CONFIGURATION_XML.put("dms.dac.archive.rebuild.days.of.week", CombiningMapping.build("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/ArchiveOperation/DaysOfWeek/DayOfWeek", ","));
        COMBINING_KEY_MAPPINGS_CONFIGURATION_XML.put("dms.dac.archive.rebuild.days.of.month", CombiningMapping.build("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/ArchiveOperation/DaysOfMonth/DayOfMonth", ","));
    }

    private Map<String, String> getConfigsFromPropertiesFile(Application application) throws IOException {
        Path propertyFilePath = Paths.get(ccProperties.getPreviousHome(), "server", "configuration",
                application.getConfigFileName());
        Map<String, String> configurations = new HashMap<>();
        File propertyFile = propertyFilePath.toFile();
        if (propertyFile.exists()) {
            Properties properties = new Properties();
            try (FileInputStream fileInputStream = new FileInputStream(propertyFilePath.toFile())) {
                properties.load(fileInputStream);
            }
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                if (value != null) {
                    value = value.trim();
                }
                if (application.getKeyMappings().containsKey(key)) {
                    key = application.getKeyMappings().get(key);
                    if (key == null) {
                        continue;
                    }
                } else {
                    key = String.format("%s.%s", application.getApplicationName(), key);
                }
                configurations.put(key, value);
            }
            logger.debug("Found {} configurations in {}", configurations.size(), application.getConfigFileName());
        }
        return configurations;
    }

    private void saveToDb(Application application, Map<String, String> configurations) {
        if (configurations.isEmpty()) {
            return;
        }
        List<Object[]> batch = new ArrayList<>();
        configurations.forEach((key, value) -> {
            String applicationName = key.substring(0, key.indexOf('.'));
            key = key.substring(key.indexOf('.') + 1);
            String modifiedValue = modifyValue(applicationName, application.getValueModifiers(), key, false, value);
            batch.add(new Object[]{modifiedValue, modifiedValue, applicationName, key});
        });
        DbHelper.getJdbcTemplate().batchUpdate(SQL_UPDATE, batch);
        logger.debug("Saved {} configurations for {} to database table", configurations.size(), application);
    }

    /**
     * Format and apply value modifiers.
     *
     * @param applicationName application
     * @param valueModifiers  Map of value modifiers
     * @param key             configuration key
     * @param value           configuration value
     * @return the modified value
     */
    private String modifyValue(String applicationName, Map<String, UnaryOperator<String>> valueModifiers, String key,
                               boolean encrypt, String value) {
        if (value == null) {
            return null;
        }
        if (valueModifiers != null) {
            UnaryOperator<String> valueModifier = valueModifiers.get(key);
            if (valueModifier != null) {
                value = valueModifier.apply(value);
            }
        }
        String format = null;
        SqlRowSet sqlRowSet = DbHelper.getJdbcTemplate()
                .queryForRowSet(SQL_SELECT_CONFIG, applicationName, key);
        if (sqlRowSet.next()) {
            format = sqlRowSet.getString("VALUE_FORMAT");
        }
        if (encrypt && !value.startsWith(EncryptionHelper.CIPHER_VALUE_PREFIX)) {
            value = EncryptionHelper.encrypt(value);
        }
        if (StringUtils.isNotEmpty(format) && !value.startsWith(EncryptionHelper.CIPHER_VALUE_PREFIX)) {
            value = String.format(format, value);
        }
        return value;
    }

    private void saveConfigClientPassword() {
        String encryptedConfigClientPassword = EncryptionHelper.encrypt(ccProperties.getManagementServer().getPassword());
        DbHelper.getJdbcTemplate().update(SQL_UPDATE, encryptedConfigClientPassword, encryptedConfigClientPassword,
                Application.CONFIG_SERVICE.getApplicationName(), "config.client.password");
    }

    private Map<String, String> getConfigsFromConfigurationXmlFile()
            throws IOException, ParserConfigurationException, SAXException {
        return XmlFileHelper.readValues(
                Paths.get(ccProperties.getPreviousHome(), "server", "configuration", Application.DMS.getConfigFileName()),
                Application.DMS.getKeyMappings());
    }

    /*
     * This is for values in the configuration file that exist as
     * multiple elements, but have to be combined into a single
     * element for the new configuration.
     */
    private Map<String, String> getCombiningConfigsFromXmlFile() throws IOException, ParserConfigurationException, SAXException {
        Path xmlFilePath = Paths.get(ccProperties.getPreviousHome(), "server", "configuration",
                Application.DMS.getConfigFileName());
        Map<String, String> configurations = new HashMap<>();
        File xmlFile = xmlFilePath.toFile();
        if (xmlFile.exists()) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setExpandEntityReferences(false);
            Document document = documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlFile)));
            XPath xPath = XPathFactory.newInstance().newXPath();
            for (Map.Entry<String, CombiningMapping> entry : ConfigurationManagementServiceImpl.COMBINING_KEY_MAPPINGS_CONFIGURATION_XML.entrySet()) {
                ArrayList<String> values = new ArrayList<>();
                String expression = entry.getValue().getPath();
                try {
                    String value = xPath.evaluate("count(" + expression + ")", document);
                    int count = Integer.parseInt(value);
                    if (count > 0) {
                        for (int i = 1; i <= count; i++) {
                            values.add(xPath.evaluate(expression + "[" + i + "]/text()", document).trim());
                        }
                        configurations.put(entry.getKey(), String.join(entry.getValue().getSeparator(), values));
                    }
                } catch (XPathExpressionException e) {
                    logger.error("Error in XPath expression", e);
                }
            }
        }

        return configurations;
    }


    private void createPropertyFiles() throws IOException {
        if (ccProperties.isManagementServerInstance()) {
            try (OutputStream outputStream = new FileOutputStream(Paths.get(ccProperties.getHome(), "server",
                    "configuration", "application.properties").toFile())) {
                DbType dbType = ccProperties.getDb().getDbType();
                Properties properties = new Properties();
                properties.put("db.driver", dbType.getDriver());
                properties.put("db.hibernate.dialect", dbType.getHibernateDialect());
                properties.put("spring.jpa.properties.hibernate.dialect", dbType.getHibernateDialect());
                properties.put("db.url", ccProperties.getDb().getUrl());
                properties.put("db.username", ccProperties.getDb().isOsAuthentication() ? "" :
                        ccProperties.getDb().getUsername());
                properties.put("db.password", ccProperties.getDb().isOsAuthentication()
                        || StringUtils.isEmpty(ccProperties.getDb().getPassword()) ? "" :
                        EncryptionHelper.encrypt(ccProperties.getDb().getPassword()));
                properties.store(outputStream, null);
            }
        }
        try (OutputStream outputStream = new FileOutputStream(Paths.get(ccProperties.getHome(), "server",
                "configuration", "bootstrap.properties").toFile())) {
            Properties properties = new Properties();
            String configServiceName;
            if (ccProperties.isManagementServerInstance()) {
                configServiceName = ccProperties.getServiceName();
            } else {
                configServiceName = StringUtils.isEmpty(ccProperties.getManagementServer().getHost()) ?
                        ccProperties.getServiceName() : ccProperties.getManagementServer().getHost();
            }
            String springCloudConfigUri = environment.getProperty("spring.cloud.config.uri",
                    String.format("https://%s:%s/config-service",
                            configServiceName,
                            StringUtils.isEmpty(ccProperties.getManagementServer().getHost()) ?
                                    ccProperties.getPort().getConfigServicePort() :
                                    ccProperties.getManagementServer().getConfigServicePort()));
            String encryptedConfigClientPassword;
            if (ccProperties.isManagementServerInstance()) {
                encryptedConfigClientPassword = DbHelper.getJdbcTemplate()
                        .queryForObject(ConfigurationManagementServiceImpl.SQL_GET_VALUE,
                                String.class, Application.CONFIG_SERVICE.getApplicationName(),
                                "config.client.password");
            } else {
                encryptedConfigClientPassword = environment.getProperty("spring.cloud.config.password");
                if (StringUtils.isEmpty(encryptedConfigClientPassword)) {
                    encryptedConfigClientPassword = EncryptionHelper.encrypt(ccProperties.getManagementServer()
                            .getPassword());
                }
                if (!encryptedConfigClientPassword.startsWith(EncryptionHelper.CIPHER_VALUE_PREFIX)) {
                    encryptedConfigClientPassword = String.format(EncryptionHelper.CIPHER_VALUE_FORMAT,
                            encryptedConfigClientPassword);
                }
            }
            properties.put("spring.cloud.config.uri", springCloudConfigUri);
            properties.put("spring.cloud.config.username", "config-client");
            properties.put("spring.cloud.config.password", encryptedConfigClientPassword);
            properties.put("spring.cloud.config.fail-fast", "true");
            properties.store(outputStream, null);
        }
    }

    @Autowired
    public void setPasswordEncodeSecret(String passwordEncodeSecret) {
        this.passwordEncodeSecret = passwordEncodeSecret;
    }

    private static class CombiningMapping {

        private final String path;
        private final String separator;

        private CombiningMapping(String path, String separator) {
            this.path = path;
            this.separator = separator;
        }

        static CombiningMapping build(String path, String separator) {
            return new CombiningMapping(path, separator);
        }

        String getSeparator() {
            return separator;
        }

        String getPath() {
            return path;
        }

    }

}



