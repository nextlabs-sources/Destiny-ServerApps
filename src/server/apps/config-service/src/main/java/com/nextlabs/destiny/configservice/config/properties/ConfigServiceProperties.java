package com.nextlabs.destiny.configservice.config.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.nextlabs.cc.common.enums.DbType;
import com.nextlabs.destiny.configservice.config.EnvironmentVariableEnvironmentRepository;
import com.nextlabs.destiny.configservice.config.ReversibleTextEncryptor;
import com.nextlabs.destiny.configservice.init.ConfigurationInitializer;

/**
 * Obtain properties required to initialize configuration service from database.
 *
 * @author Sachindra Dasun
 */
public class ConfigServiceProperties {

    public static final String APPLICATION_NAME = "config-service";
    private static final String LOAD_CONFIGURATION_QUERY = "SELECT CONFIG_KEY, VALUE FROM SYS_CONFIG WHERE " +
            "APPLICATION IN('" + APPLICATION_NAME + "', 'application')";

    private ConfigServiceProperties() {
    }

    public static Properties get() {
        Properties properties = new Properties();
        try {
            Properties configServiceProperties = getConfigServiceProperties();
            try (Connection connection = getDbConnection(configServiceProperties, properties)) {
                ConfigurationInitializer.init(configServiceProperties, connection);
                addPropertiesFromDatabase(connection, properties);
                addAdditionalProperties(properties);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    private static Properties getConfigServiceProperties() throws IOException {
        Path applicationPropertiesFilePath = Paths.get(System.getProperty("cc.home"), "server", "configuration",
                "application.properties");
        Properties applicationProperties = new Properties();
        if (applicationPropertiesFilePath.toFile().exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(applicationPropertiesFilePath.toFile())) {
                applicationProperties.load(fileInputStream);
            }
        }
        Map<String, String> environmentProperties = EnvironmentVariableEnvironmentRepository
                .getEnvironmentProperties(APPLICATION_NAME, "default");
        environmentProperties.forEach(System::setProperty);
        applicationProperties.putAll(environmentProperties);
        return applicationProperties;
    }

    private static void addPropertiesFromDatabase(Connection connection, Properties properties) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery(LOAD_CONFIGURATION_QUERY)) {
                while (rs.next()) {
                    properties.put(rs.getString("CONFIG_KEY"),
                                    rs.getString("VALUE") == null ? "" : rs.getString("VALUE"));
                }
            }
        }
    }

    private static void addAdditionalProperties(Properties properties) {
        properties.put("spring.application.name", APPLICATION_NAME);
        properties.put("spring.cloud.config.server.encrypt.enabled", "false");
    }

    private static Connection getDbConnection(Properties configServiceProperties, Properties properties) throws ClassNotFoundException, SQLException {
        String dbUrl = configServiceProperties.getProperty("db.url");
        DbType dbType = DbType.fromJdbcUrl(dbUrl);
        Class.forName(dbType.getDriver());
        properties.put("spring.jpa.properties.hibernate.dialect", dbType.getHibernateDialect());
        String dbPassword = configServiceProperties.getProperty("db.password");
        return DriverManager.getConnection(dbUrl,
                configServiceProperties.getProperty("db.username"),
                StringUtils.isEmpty(dbPassword) ? null : ReversibleTextEncryptor.decryptIfEncrypted(dbPassword));
    }

}
