package com.nextlabs.authentication.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.nextlabs.destiny.logmanager.LogManagerClient;

/**
 * Start configuration refresher and reload logger settings.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class ConfigRefresherConfiguration {

    @Value("${spring.application.name}")
    private String application;

    @Value("${spring.cloud.config.uri}")
    private String configServiceUrl;

    @Value("${spring.cloud.config.username}")
    private String configServiceUsername;

    @Value("${spring.cloud.config.password}")
    private String configServicePassword;

    @Value("${logger.manager.enabled:true}")
    private boolean loggerManagerEnabled;

    @Value("${logger.update.refresher.enabled}")
    private boolean loggerUpdateRefresherEnabled;

    @Value("${config.update.refresher.enabled}")
    private boolean configUpdateRefresherEnabled;

    @Autowired
    private ConfigRefresher configRefresher;

    @Autowired
    private TextEncryptor textEncryptor;

    @PostConstruct
    public void initConfigRefreshers() {
        if (loggerManagerEnabled) {
            LogManagerClient.refresh(configServiceUrl, configServiceUsername,
                    textEncryptor.encrypt(configServicePassword));
        }
        loggerUpdateRefresherEnabled = loggerUpdateRefresherEnabled && loggerManagerEnabled;
        if (configUpdateRefresherEnabled || loggerUpdateRefresherEnabled) {
            new Thread(configRefresher).start();
        }
    }

}
