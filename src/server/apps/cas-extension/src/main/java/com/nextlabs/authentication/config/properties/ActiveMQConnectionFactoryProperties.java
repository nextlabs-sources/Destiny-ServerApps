package com.nextlabs.authentication.config.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Container to store all ActiveMQ connection factory properties.
 *
 * @author Sachindra Dasun
 */
@Configuration
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
