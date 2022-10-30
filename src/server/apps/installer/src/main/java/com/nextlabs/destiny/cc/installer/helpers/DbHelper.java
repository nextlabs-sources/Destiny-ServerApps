package com.nextlabs.destiny.cc.installer.helpers;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.nextlabs.cc.common.enums.DbType;
import com.nextlabs.destiny.cc.installer.config.properties.DbProperties;
import oracle.jdbc.OracleDriver;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.net.URI;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Utility for connecting to the Control Center database.
 *
 * @author Sachindra Dasun
 */
public class DbHelper {

    private static final Logger logger = LoggerFactory.getLogger(DbHelper.class);
    private static JdbcTemplate jdbcTemplate;

    private DbHelper() {
    }

    public static synchronized void initJdbcTemplate(DbProperties dbProperties) {
        jdbcTemplate = getJdbcTemplate(dbProperties);
    }

    public static synchronized JdbcTemplate getJdbcTemplate(DbProperties dbProperties) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbProperties.getDbType().getDriver());
        dataSource.setUrl(dbProperties.getUrl());
        dataSource.setUsername(dbProperties.getUsername());
        dataSource.setPassword(dbProperties.getPassword());
        return new JdbcTemplate(dataSource);
    }

    public static synchronized JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public static String getJdbcUrl(DbProperties dbProperties) {
        if (dbProperties.getDbType() == DbType.ORACLE) {
            if (dbProperties.getPort() < 0) {
                dbProperties.setPort(1521);
            }
            String protocol = dbProperties.isSsl() ? "TCPS" : "TCP";
            String validateServer = dbProperties.isValidateServerCertificate() ? String.format("(SECURITY=(SSL_SERVER_CERT_DN=%s))",
                    dbProperties.getHostNameInCertificate()) : ")";
            return String.format("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=%s)(HOST=%s)(PORT=%d))(CONNECT_DATA=(SID=%s))%s)",
                    protocol, dbProperties.getHost(), dbProperties.getPort(), dbProperties.getDatabaseName(),
                    validateServer);
        } else if (dbProperties.getDbType() == DbType.POSTGRESQL) {
            if (dbProperties.getPort() < 0) {
                dbProperties.setPort(5432);
            }
            String validateServer = dbProperties.isValidateServerCertificate() ? "&sslmode=verify-full" :
                    "&sslmode=verify-ca";
            String sslConfiguration = dbProperties.isSsl() ? String.format("?ssl=true%s", validateServer) : "";
            return String.format("jdbc:postgresql://%s:%d/%s%s", dbProperties.getHost(), dbProperties.getPort(),
                    dbProperties.getDatabaseName(), sslConfiguration);
        } else if (dbProperties.getDbType() == DbType.SQL_SERVER) {
            String serverName = StringUtils.isEmpty(dbProperties.getInstanceName()) ?
                    dbProperties.getHost() : String.format("%s\\%s", dbProperties.getHost(), dbProperties.getInstanceName());
            String port = dbProperties.getPort() > -1 ? String.format(":%d", dbProperties.getPort()) : "";
            String trustServerCertificate = String.format("trustServerCertificate=%s",
                    dbProperties.isValidateServerCertificate() ? String.format("false;hostNameInCertificate=%s",
                            dbProperties.getHostNameInCertificate()) : "true");
            String sslConfiguration = dbProperties.isSsl() ? String.format(";encrypt=true;%s", trustServerCertificate) : ";encrypt=false";
            String osAuthentication = dbProperties.isOsAuthentication() ? ";integratedSecurity=true" : "";
            return String.format("jdbc:sqlserver://%s%s;databaseName=%s%s%s",
                    serverName, port, dbProperties.getDatabaseName(), sslConfiguration, osAuthentication);
        } else if (dbProperties.getDbType() == DbType.DB2) {
            if (dbProperties.getPort() < 0) {
                dbProperties.setPort(50000);
            }
            String validateServer = dbProperties.isValidateServerCertificate() ?
                            String.format("validateServerCertificate=true;hostNameInCertificate=%s;", dbProperties.getHostNameInCertificate())
                            : "validateServerCertificate=false;";
            String sslConfiguration = dbProperties.isSsl() ? String.format("sslConnection=true;%s;", validateServer) : "";
            return String.format("jdbc:db2://%s:%d/%s:currentSchema=%s;allowNextOnExhaustedResultSet=true;%s", dbProperties.getHost(), dbProperties.getPort(),
                            dbProperties.getDatabaseName(), dbProperties.getSchemaName(), sslConfiguration);
        }
        throw new IllegalArgumentException("Unsupported database type");
    }

    public static void parseJdbcUrl(DbProperties dbProperties) {
        try {
            dbProperties.setDbType(DbType.fromJdbcUrl(dbProperties.getUrl()));
            if (dbProperties.getDbType() == DbType.ORACLE) {
                DriverPropertyInfo[] driverPropertyInfoList = new OracleDriver()
                        .getPropertyInfo(dbProperties.getUrl(), null);
                Arrays.stream(driverPropertyInfoList).forEach(driverPropertyInfo -> {
                    if ("database".equalsIgnoreCase(driverPropertyInfo.name)) {
                        if (driverPropertyInfo.value.contains("(")) {
                            Arrays.stream(driverPropertyInfo.value.replace(")", "\n")
                                    .replace("(", "\n").split("\\n"))
                                    .filter(line -> StringUtils.isNotEmpty(line) && line.contains("="))
                                    .forEach(line -> {
                                        String[] keyValue = line.split("=");
                                        String key = keyValue[0].toUpperCase();
                                        switch (key) {
                                            case "PROTOCOL": {
                                                dbProperties.setSsl("TCPS".equalsIgnoreCase(keyValue[1]));
                                                break;
                                            }
                                            case "HOST": {
                                                dbProperties.setHost(keyValue[1]);
                                                break;
                                            }
                                            case "PORT": {
                                                dbProperties.setPort(Integer.parseInt(keyValue[1]));
                                                break;
                                            }
                                            case "SID": {
                                                dbProperties.setDatabaseName(keyValue[1]);
                                                break;
                                            }
                                            case "SSL_SERVER_CERT_DN": {
                                                dbProperties.setHostNameInCertificate(keyValue[1].replace("\"", ""));
                                                dbProperties.setValidateServerCertificate(true);
                                                break;
                                            }
                                            default: {
                                                break;
                                            }
                                        }
                                    });
                        } else {
                            URI dbUri = URI.create(String.format("tcp://%s", driverPropertyInfo.value));
                            dbProperties.setHost(dbUri.getHost());
                            dbProperties.setPort(dbUri.getPort());
                            dbProperties.setDatabaseName(dbUri.getPath().replace("/", ""));
                        }
                    }
                });
            } else if (dbProperties.getDbType() == DbType.POSTGRESQL) {
                DriverPropertyInfo[] driverPropertyInfoList = new Driver()
                        .getPropertyInfo(dbProperties.getUrl(), null);
                Arrays.stream(driverPropertyInfoList).forEach(driverPropertyInfo -> {
                    if ("PGHOST".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setHost(driverPropertyInfo.value);
                    } else if ("PGPORT".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setPort(Integer.parseInt(driverPropertyInfo.value));
                    } else if ("PGDBNAME".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setDatabaseName(driverPropertyInfo.value);
                    } else if ("ssl".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setSsl(Boolean.parseBoolean(driverPropertyInfo.value));
                    } else if ("sslmode".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setValidateServerCertificate("verify-full".equalsIgnoreCase(driverPropertyInfo.value));
                    }
                });
            } else if (dbProperties.getDbType() == DbType.SQL_SERVER) {
                DriverPropertyInfo[] driverPropertyInfoList = new SQLServerDriver()
                        .getPropertyInfo(dbProperties.getUrl(), null);
                Arrays.stream(driverPropertyInfoList).forEach(driverPropertyInfo -> {
                    if ("databaseName".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setDatabaseName(driverPropertyInfo.value);
                    } else if ("encrypt".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setSsl(Boolean.parseBoolean(driverPropertyInfo.value));
                    } else if ("hostNameInCertificate".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setHostNameInCertificate(driverPropertyInfo.value);
                    } else if ("integratedSecurity".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setOsAuthentication(Boolean.parseBoolean(driverPropertyInfo.value));
                    } else if ("portNumber".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setPort(Integer.parseInt(driverPropertyInfo.value));
                    } else if ("serverName".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setHost(driverPropertyInfo.value);
                    } else if ("instanceName".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setInstanceName(driverPropertyInfo.value);
                    } else if ("trustServerCertificate".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setValidateServerCertificate(!Boolean.parseBoolean(driverPropertyInfo.value));
                    }
                });
            } else if (dbProperties.getDbType() == DbType.DB2) {
                DriverPropertyInfo[] driverPropertyInfoList = new DB2Util()
                                .getPropertyInfo(dbProperties.getUrl(), null);
                Arrays.stream(driverPropertyInfoList).forEach(driverPropertyInfo -> {
                    if ("serverName".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setHost(driverPropertyInfo.value);
                    } else if ("portNumber".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setPort(Integer.parseInt(driverPropertyInfo.value));
                    } else if ("databaseName".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setDatabaseName(driverPropertyInfo.value);
                    } else if ("currentSchema".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setSchemaName(driverPropertyInfo.value);
                    } else if ("sslConnection".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setSsl(Boolean.parseBoolean(driverPropertyInfo.value));
                    } else if ("validateServerCertificate".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setValidateServerCertificate(Boolean.parseBoolean(driverPropertyInfo.value));
                    } else if ("hostNameInCertificate".equalsIgnoreCase(driverPropertyInfo.name)) {
                        dbProperties.setHostNameInCertificate(driverPropertyInfo.value);
                    }
                });
            }
        } catch (Exception e) {
            logger.error("Error in parsing database connection URL", e);
        }
    }
}

class DB2Util {
    private static final String PREFIX = "jdbc:db2://";
    public DriverPropertyInfo[] getPropertyInfo(String jdbcUrl, Properties properties)
            throws SQLException {
        Map<Object, Object> jdbcProperties = new HashMap<>();
        if(properties != null) {
            for (Map.Entry<Object, Object> prop : properties.entrySet()) {
                jdbcProperties.put(prop.getKey(), prop.getValue());
            }
        }

        if(jdbcUrl == null
                        || !jdbcUrl.startsWith(PREFIX)) {
            throw new SQLException(String.format("JDBC URL must start with %s but was: %s", PREFIX, jdbcUrl));
        }

        if(!jdbcUrl.endsWith(";")) {
            throw new SQLException(String.format("JDBC URL must end with ';' but was: %s", jdbcUrl));
        }

        String tmpUrl = jdbcUrl.substring(PREFIX.length());
        StringBuilder result = new StringBuilder();
        String name = "";
        String value = "";

        final int inStart = 0;
        final int inServerName = 1;
        final int inPort = 2;
        final int inInstanceName = 3;
        final int inEscapedValueStart = 4;
        final int inEscapedValueEnd = 5;
        final int inValue = 6;
        final int inName = 7;

        int i = 0;
        char ch;
        int state = inStart;

        while(i < tmpUrl.length()) {
            ch = tmpUrl.charAt(i);

            switch(state) {
                case inStart: {
                    if (ch == ';') {
                        // done immediately
                        state = inName;
                    } else {
                        result.append(ch);
                        state = inServerName;
                    }
                    break;
                }
                case inServerName: {
                    if (ch == ';' || ch == ':' || ch == '\\') {
                        // non escaped trim the string
                        String property = result.toString().trim();
                        if (property.length() > 0) {
                            jdbcProperties.put("serverName", property);
                        }
                        result.setLength(0);

                        if (ch == ';')
                            state = inName;
                        else if (ch == ':')
                            state = inPort;
                        else
                            state = inInstanceName;
                    } else {
                        result.append(ch);
                        // same state
                    }
                    break;
                }
                case inPort: {
                    if (ch == '/') {
                        String property = result.toString().trim();
                        jdbcProperties.put("portNumber", property);
                        result.setLength(0);
                        state = inInstanceName;
                    } else {
                        result.append(ch);
                        // same state
                    }
                    break;
                }
                case inInstanceName: {
                    if (ch == ';' || ch == ':') {
                        // non escaped trim the string
                        String property = result.toString().trim();
                        jdbcProperties.put("databaseName", property);
                        result.setLength(0);

                        state = inName;
                    } else {
                        result.append(ch);
                        // same state
                    }
                    break;
                }
                case inName: {
                    if (ch == '=') {
                        // name is never escaped!
                        name = name.trim();
                        if (name.length() <= 0) {
                            throw new SQLException("Invalid connection string, property name is never escaped.");
                        }
                        state = inValue;
                    } else if (ch == ';') {
                        name = name.trim();
                        if (name.length() > 0) {
                            throw new SQLException("Invalid connection string, property name is empty.");
                        }
                        // same state
                    } else {
                        StringBuilder builder = new StringBuilder();
                        builder.append(name);
                        builder.append(ch);
                        name = builder.toString();
                        // same state
                    }
                    break;
                }
                case inValue: {
                    if (ch == ';') {
                        // simple value trim
                        value = value.trim();
                        jdbcProperties.put(name, value);
                        name = "";
                        value = "";
                        state = inName;
                    } else if (ch == '{') {
                        state = inEscapedValueStart;
                        value = value.trim();
                        if (value.length() > 0) {
                            throw new SQLException("Invalid connection string, property value is not empty.");
                        }
                    } else {
                        StringBuilder builder = new StringBuilder();
                        builder.append(value);
                        builder.append(ch);
                        value = builder.toString();
                        // same state
                    }
                    break;
                }
                case inEscapedValueStart: {
                    /*
                     * check for escaped }. when we see a }, first check to see if this is before the end of the string
                     * to avoid index out of range exception then check if the character immediately after is also a }.
                     * if it is, then we have a }}, which is not the closing of the escaped state.
                     */
                    if (ch == '}' && i + 1 < tmpUrl.length() && tmpUrl.charAt(i + 1) == '}') {
                        StringBuilder builder = new StringBuilder();
                        builder.append(value);
                        builder.append(ch);
                        value = builder.toString();
                        i++; // escaped }} into a }, so increment the counter once more
                        // same state
                    } else {
                        if (ch == '}') {
                            // no trimming use the value as it is.
                            jdbcProperties.put(name, value);

                            name = "";
                            value = "";
                            // to eat the spaces until the ; potentially we could do without the state but
                            // it would not be clean
                            state = inEscapedValueEnd;
                        } else {
                            StringBuilder builder = new StringBuilder();
                            builder.append(value);
                            builder.append(ch);
                            value = builder.toString();
                            // same state
                        }
                    }
                    break;
                }
                case inEscapedValueEnd: {
                    if (ch == ';') // eat space chars till ; anything else is an error
                    {
                        state = inName;
                    } else if (ch != ' ') {
                        // error if the chars are not space
                        throw new SQLException(String.format("Invalid connection string, space expected but character %s appear.", ch));
                    }
                    break;
                }

                default:
                    assert false : "parseURL: Invalid state " + state;
            }

            i++;
        }

        // Last round of tidy up after exit char looping
        switch (state) {
            case inServerName:
                String property = result.toString().trim();
                if (property.length() > 0) {
                    jdbcProperties.put("serverName", property);
                }
                break;
            case inPort:
                property = result.toString().trim();
                jdbcProperties.put("portNumber", property);
                break;
            case inInstanceName:
                property = result.toString().trim();
                jdbcProperties.put("databaseName", property);
                break;
            case inValue:
                // simple value trim
                value = value.trim();
                jdbcProperties.put(name, value);

                break;
            case inEscapedValueEnd:
            case inStart:
                // do nothing!
                break;
            case inName: {
                name = name.trim();
                if (name.length() > 0) {
                    throw new SQLException(String.format("Invalid connection string, property name %s without value.", name));
                }

                break;
            }
            default:
                throw new SQLException("Invalid connection string.");
        }

        List<DriverPropertyInfo> infoList = new ArrayList<>();

        for(Map.Entry<Object, Object> prop : jdbcProperties.entrySet()) {
            infoList.add(new DriverPropertyInfo(prop.getKey().toString(), prop.getValue().toString()));
        }

        return infoList.toArray(new DriverPropertyInfo[0]);
    }
}
