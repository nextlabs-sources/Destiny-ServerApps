package com.nextlabs.destiny.configservice.init;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.cc.common.enums.DbType;

/**
 * Initialize configurations
 *
 * @author Sachindra Dasun
 */
public class ConfigurationInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationInitializer.class);

    private static final String SIGNING_ALGORITHM_KEY = "cc-oidc-config.signingAlgorithm";
    private static final String ENCRYPTION_ALGORITHM_KEY = "cc-oidc-config.encryptionAlgorithm";
    private static final Map<String, Supplier<String>> CONFIG_VALUE_GENERATORS = new HashMap<>();
    private static final String SQL_UPDATE_CONFIG_VALUE = "UPDATE SYS_CONFIG SET VALUE = ?, DEFAULT_VALUE = ? WHERE (VALUE IS NULL OR VALUE = '') AND APPLICATION = ? AND CONFIG_KEY = ?";
    private static final String SQL_OVERRIDE_CONFIG_VALUE = "UPDATE SYS_CONFIG SET VALUE = ? WHERE APPLICATION = ? AND CONFIG_KEY = ?";
    private static final String SQL_GET_SYS_CONFIG_VALUE = "SELECT VALUE FROM SYS_CONFIG WHERE APPLICATION = ? AND CONFIG_KEY = ?";

    static {
        CONFIG_VALUE_GENERATORS.put("cas.cas.authn.mfa.gauth.crypto.encryption.key", () -> KeyGenerator.generateEncryptedJsonWebKey(256));
        CONFIG_VALUE_GENERATORS.put("cas.cas.authn.mfa.gauth.crypto.signing.key", () -> KeyGenerator.generateEncryptedJsonWebKey(512));
        CONFIG_VALUE_GENERATORS.put("cas.cas.authn.oauth.accessToken.crypto.encryption.key", () -> KeyGenerator.generateEncryptedJsonWebKey(256));
        CONFIG_VALUE_GENERATORS.put("cas.cas.authn.oauth.accessToken.crypto.signing.key", () -> KeyGenerator.generateEncryptedJsonWebKey(512));
        CONFIG_VALUE_GENERATORS.put("cas.cas.authn.oauth.crypto.encryption.key", () -> KeyGenerator.generateEncryptedJsonWebKey(512));
        CONFIG_VALUE_GENERATORS.put("cas.cas.authn.oauth.crypto.signing.key", () -> KeyGenerator.generateEncryptedJsonWebKey(512));
        CONFIG_VALUE_GENERATORS.put("cas.cas.authn.pac4j.cookie.crypto.encryption.key", () -> KeyGenerator.generateEncryptedJsonWebKey(256));
        CONFIG_VALUE_GENERATORS.put("cas.cas.authn.pac4j.cookie.crypto.signing.key", () -> KeyGenerator.generateEncryptedJsonWebKey(512));
        CONFIG_VALUE_GENERATORS.put("cas.cas.authn.pm.reset.crypto.encryption.key", () -> KeyGenerator.generateEncryptedJsonWebKey(256));
        CONFIG_VALUE_GENERATORS.put("cas.cas.authn.pm.reset.crypto.signing.key", () -> KeyGenerator.generateEncryptedJsonWebKey(512));
        CONFIG_VALUE_GENERATORS.put("cas.cas.tgc.crypto.encryption.key", () -> KeyGenerator.generateEncryptedJsonWebKey(256));
        CONFIG_VALUE_GENERATORS.put("cas.cas.tgc.crypto.signing.key", () -> KeyGenerator.generateEncryptedJsonWebKey(512));
        CONFIG_VALUE_GENERATORS.put("cas.cas.webflow.crypto.encryption.key", () -> KeyGenerator.generateEncryptedBase64RandomString(16));
        CONFIG_VALUE_GENERATORS.put("cas.cas.webflow.crypto.signing.key", () -> KeyGenerator.generateEncryptedJsonWebKey(512));
        CONFIG_VALUE_GENERATORS.put("application.pbkdf2.encoding.secret.key", () -> KeyGenerator.generateEncryptedJsonWebKey(512));
        CONFIG_VALUE_GENERATORS.put("application.enrollmentservice.clientSecret", () -> KeyGenerator.generateEncryptedRandomString(32));
    }

    private ConfigurationInitializer() {
    }

    /**
     * Initialize configurations.
     */
    public static void init(Properties configServiceProperties, Connection connection) throws SQLException {
        addAdditionalConfig(configServiceProperties, connection);
        generateConfigurationValues(connection);
        updateOverriddenConfigurations(connection);
    }

    private static void addAdditionalConfig(Properties configServiceProperties, Connection connection) {
        CONFIG_VALUE_GENERATORS.put("application.cc-oidc-config.signingJwks", () ->
                KeyGenerator.generateEncryptedRSAJsonWebKey(2048, "oidc.signing.jwks",
                        getSysConfigValue(connection, "application", SIGNING_ALGORITHM_KEY)));
        CONFIG_VALUE_GENERATORS.put("application.cc-oidc-config.encryptionJwks", () ->
                KeyGenerator.generateEncryptedRSAJsonWebKey(2048, "oidc.encryption.jwks",
                        getSysConfigValue(connection, "application", ENCRYPTION_ALGORITHM_KEY)));
        CONFIG_VALUE_GENERATORS.put("application.db.driver", () ->
                DbType.fromJdbcUrl(configServiceProperties.getProperty("db.url")).getDriver());
    }

    private static void generateConfigurationValues(Connection connection) throws SQLException {
        try (PreparedStatement configValueUpdatePreparedStatement = connection.prepareStatement(SQL_UPDATE_CONFIG_VALUE)) {
            for (Map.Entry<String, Supplier<String>> entry : CONFIG_VALUE_GENERATORS.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().get();
                configValueUpdatePreparedStatement.setString(1, value);
                configValueUpdatePreparedStatement.setString(2, value);
                configValueUpdatePreparedStatement.setString(3, key.substring(0, key.indexOf('.')));
                configValueUpdatePreparedStatement.setString(4, key.substring(key.indexOf('.') + 1));
                configValueUpdatePreparedStatement.addBatch();
            }
            int[] updateCounts = configValueUpdatePreparedStatement.executeBatch();
            LOGGER.info("Configuration values generated for {} configurations", updateCounts.length);
        }
    }

    private static void updateOverriddenConfigurations(Connection connection) {
        try {
            Properties overriddenConfigurations = getOverriddenConfigurations();
            if (overriddenConfigurations != null && !overriddenConfigurations.isEmpty()) {
                try (PreparedStatement configOverridePreparedStatement = connection.prepareStatement(SQL_OVERRIDE_CONFIG_VALUE)) {
                    for (String key : overriddenConfigurations.stringPropertyNames()) {
                        configOverridePreparedStatement.setString(1, overriddenConfigurations.getProperty(key));
                        configOverridePreparedStatement.setString(2, key.substring(0, key.indexOf('.')));
                        configOverridePreparedStatement.setString(3, key.substring(key.indexOf('.') + 1));
                        configOverridePreparedStatement.addBatch();
                    }
                    int[] updateCounts = configOverridePreparedStatement.executeBatch();
                    LOGGER.info("Configuration values overridden for {} configurations", updateCounts.length);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in updating overridden configurations", e);
        }
    }

    private static String getSysConfigValue(Connection connection, String application, String key) {
        try {
            try (PreparedStatement configOverridePreparedStatement = connection.prepareStatement(SQL_GET_SYS_CONFIG_VALUE)) {
                configOverridePreparedStatement.setString(1, application);
                configOverridePreparedStatement.setString(2, key);
                try (ResultSet resultSet = configOverridePreparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(1);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in updating overridden configurations", e);
        }
        return null;
    }

    private static Properties getOverriddenConfigurations() {
        Path propertyFilePath = Paths.get(System.getProperty("cc.home"), "server", "configuration",
                "config-overrides.properties");
        Properties overriddenConfigurations = null;
        try {
            if (propertyFilePath.toFile().exists()) {
                overriddenConfigurations = new Properties();
                try (FileInputStream fileInputStream = new FileInputStream(propertyFilePath.toFile())) {
                    overriddenConfigurations.load(fileInputStream);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error in reading overridden configurations", e);
        }
        return overriddenConfigurations;
    }
}
