package com.nextlabs.destiny.configservice.config.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties required to initialize broker service. Properties can be configured using the prefix
 * activemq.brokerService.*
 *
 * @author Sachindra Dasun
 */
@Component
@ConfigurationProperties(prefix = "activemq")
public class BrokerServiceProperties {

    private Map<String, String> brokerService = new HashMap<>();

    public Map<String, String> getBrokerService() {
        return brokerService;
    }

    public void setBrokerService(Map<String, String> brokerService) {
        this.brokerService = brokerService;
    }

}
