package com.nextlabs.destiny.cc.installer.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.cc.common.enums.DbType;
import com.nextlabs.destiny.cc.installer.helpers.DbHelper;
import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;
import com.nextlabs.destiny.cc.installer.services.impl.ConfigurationManagementServiceImpl;

/**
 * Properties available for database configuration.
 *
 * @author Sachindra Dasun
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DbProperties {

    private static final Logger logger = LoggerFactory.getLogger(DbProperties.class);
    private String databaseName;
    private String schemaName;
    private DbType dbType;
    private String host;
    private String hostNameInCertificate;
    private String instanceName;
    private boolean osAuthentication;
    private String password;
    private int port = -1;
    private int retryAttempts;
    private int retryBackOffPeriod;
    private boolean ssl;
    private String url;
    private String username;
    private boolean validateServerCertificate;

    @JsonIgnore
    public Version getCcVersionFromDb() {
        try {
            String version = DbHelper.getJdbcTemplate().queryForObject(ConfigurationManagementServiceImpl.SQL_GET_VALUE,
                    new String[]{"application", "application.version"}, String.class);
            if (StringUtils.isNotEmpty(version)) {
                return new Version(version);
            }
        } catch (Exception e) {
            logger.info("Control Center version not found in database");
        }
        return null;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostNameInCertificate() {
        return hostNameInCertificate;
    }

    public void setHostNameInCertificate(String hostNameInCertificate) {
        this.hostNameInCertificate = hostNameInCertificate;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public boolean isOsAuthentication() {
        return osAuthentication;
    }

    public void setOsAuthentication(boolean osAuthentication) {
        this.osAuthentication = osAuthentication;
    }

    public String getPassword() {
        return StringUtils.isEmpty(password) ? null : EncryptionHelper.decryptIfEncrypted(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public int getRetryBackOffPeriod() {
        return retryBackOffPeriod;
    }

    public void setRetryBackOffPeriod(int retryBackOffPeriod) {
        this.retryBackOffPeriod = retryBackOffPeriod;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public boolean isValidateServerCertificate() {
        return validateServerCertificate;
    }

    public void setValidateServerCertificate(boolean validateServerCertificate) {
        this.validateServerCertificate = validateServerCertificate;
    }

    public String getUrl() {
        if (StringUtils.isEmpty(url) && StringUtils.isNotEmpty(host)) {
            url = DbHelper.getJdbcUrl(this);
        }

        if(DbType.SQL_SERVER.equals(this.dbType)) {
            if(!url.contains("encrypt=")) {
                url = url.concat(";encrypt=false");
            }
        } else if(DbType.DB2.equals(this.dbType)) {
            if(!url.contains("allowNextOnExhaustedResultSet=")) {
                url = url.concat("allowNextOnExhaustedResultSet=true;");
            } else if(url.contains("allowNextOnExhaustedResultSet=false")) {
                url = url.replace("allowNextOnExhaustedResultSet=false", "allowNextOnExhaustedResultSet=true");
            }
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        if (StringUtils.isNotEmpty(this.url)) {
            this.url = this.url.replaceAll("\\s*=\\s*", "=").replaceAll("\\s*;\\s*", ";");
            DbHelper.parseJdbcUrl(this);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
