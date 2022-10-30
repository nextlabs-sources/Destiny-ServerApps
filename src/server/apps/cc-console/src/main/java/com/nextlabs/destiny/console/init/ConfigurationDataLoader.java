/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 2, 2015
 *
 */
package com.nextlabs.destiny.console.init;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bluejungle.framework.crypt.IDecryptor;
import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.cc.common.enums.DbType;
import com.nextlabs.destiny.console.enums.EnvironmentConfig;

/**
 *
 * App initial data configurations.
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Component
public class ConfigurationDataLoader {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.hibernate.show.sql}")
    private String dbShowSQL;

    @Value("${db.hibernate.ddl.auto}")
    private String dbDDLupdate;

    @Value("${app.service.security}")
    private String appServiceSecurity;

    @Value("${app.service.home}")
    private String appServiceHome;

    @Value("${cas.service.login}")
    private String casServiceLogin;

    @Value("${cas.service.logout}")
    private String casServiceLogout;

    @Value("${cas.service.url}")
    private String casServiceUrl;

    @Value("${application.version}")
    private String applicationVersion;

    @Value("${application.build:}")
    private String applicationBuild;

    @Value("${help.content.dir.path}")
    private String helpContentDirPath;

    @Value("${policy.exports.file.location}")
    private String policyExportsFileLocation;

    @Value("${server.license.dir}")
    private String serverLicensePath;

    @Value("${server.log.queue.dir.path}")
    private String logQueueFolderPath;

    @Value("${mm.dd.yyyy}")
    private String mmDDyyyyFormat;
    
    @Value("${search.engine.local:false}")
    private String esIsLocal;
    
    @Value("${search.engine.cluster.name:elasticsearch}")
    private String esClusterName;
    
    @Value("${search.engine.host:127.0.0.1}")
    private String esHost;
    
    @Value("${search.engine.port:9300}")
    private String esPort;
    
    @Value("${search.engine.local.home:./data/cc-data-cluster}")
    private String esSearchDataLocalHome;
    
    @Value("${search.engine.client.transport.sniff:false}")
    private String esSniffOn;
    
    private IDecryptor decryptor;

    private EnvironmentConfig installMode;

    @PostConstruct
    public void init() {
        String installModeCode = System.getProperty(
                EnvironmentConfig.INSTALL_MODE_ENV_PARAM.getLabel());      
        installMode = EnvironmentConfig.get(installModeCode);     
        decryptor = new ReversibleEncryptor();
    }

    public Map<String, String> getDatabaseConfigs() {
        DbType dbType = DbType.fromJdbcUrl(dbUrl);
        String hibernateDialect = dbType.getHibernateDialect();
        if (dbType == DbType.POSTGRESQL) {
            hibernateDialect = "com.nextlabs.destiny.console.hibernate.dialect.PostgreSQL9DialectEx";
        } else if (dbType == DbType.SQL_SERVER) {
            hibernateDialect = "com.nextlabs.destiny.console.hibernate.dialect.SqlServerDialectEx";
        }
        Map<String, String> dbProperties = new HashMap<>();
        dbProperties.put("db.url", dbUrl);
        dbProperties.put("db.username", dbUsername);
        dbProperties.put("db.password", dbPassword);
        dbProperties.put("db.driver", dbType.getDriver());
        dbProperties.put("db.hibernate.dialect", hibernateDialect);
        dbProperties.put("db.hibernate.show.sql", dbShowSQL);
        dbProperties.put("db.hibernate.ddl.auto", dbDDLupdate);

        return dbProperties;
    }
    
    public Map<String, String> getESConfigs() {
        Map<String, String> esProps = new HashMap<>();
        esProps.put("search.engine.local", esIsLocal);
        esProps.put("search.engine.cluster.name", esClusterName);
        esProps.put("search.engine.host", esHost);
        esProps.put("search.engine.port", esPort);
        esProps.put("search.engine.client.transport.sniff", esSniffOn);
        esProps.put("search.engine.local.home", esSearchDataLocalHome);
        return esProps;
    }
    
    public Map<String, String> getCASConfigs() {
        Map<String, String> casProps = new HashMap<>();
        casProps.put("app.service.security", appServiceSecurity);
        casProps.put("app.service.home", appServiceHome);
        casProps.put("cas.service.login", casServiceLogin);
        casProps.put("cas.service.logout", casServiceLogout);
        casProps.put("cas.service.url", casServiceUrl);

        return casProps;
    }
    
    public String decrypt(String encryptedValue) {
        return decryptor.decrypt(encryptedValue);
    }

    public String getAppServiceSecurity() {
        return appServiceSecurity;
    }

    public String getAppServiceHome() {
        return appServiceHome;
    }

    public String getCasServiceLogin() {
        return casServiceLogin;
    }

    public String getCasServiceLogout() {
        return casServiceLogout;
    }

    public String getCasServiceUrl() {
        return casServiceUrl;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public String getApplicationBuild() {
        return applicationBuild;
    }

    public String getHelpContentDirPath() {
        return helpContentDirPath;
    }

    public String getPolicyExportsFileLocation() {
        return policyExportsFileLocation;
    }

    public String getServerLicensePath() {
        return serverLicensePath;
    }

    public String getLogQueueFolderPath() {
        return logQueueFolderPath;
    }

    public String getMmDDyyyyFormat() {
        return mmDDyyyyFormat;
    }

	public EnvironmentConfig getInstallMode() {
        return installMode;
    }

}
