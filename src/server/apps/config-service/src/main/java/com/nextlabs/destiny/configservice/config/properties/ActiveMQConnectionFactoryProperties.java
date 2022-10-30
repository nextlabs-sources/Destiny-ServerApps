package com.nextlabs.destiny.configservice.config.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties required to initialize ActiveMQConnectionFactory. Properties can be configured using the prefix
 * config.activeMQConnectionFactory.*
 *
 * @author Sachindra Dasun
 */
@Component
@ConfigurationProperties(prefix = "config")
public class ActiveMQConnectionFactoryProperties {

    private Map<String, String> activeMQConnectionFactory = new HashMap<>();

    public Map<String, String> getActiveMQConnectionFactory() {
        return activeMQConnectionFactory;
    }

    public void setActiveMQConnectionFactory(Map<String, String> activeMQConnectionFactory) {
        this.activeMQConnectionFactory = activeMQConnectionFactory;
    }

}
