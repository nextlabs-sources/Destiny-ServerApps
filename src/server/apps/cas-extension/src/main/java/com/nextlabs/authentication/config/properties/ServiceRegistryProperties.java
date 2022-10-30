package com.nextlabs.authentication.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Container to store service registry properties.
 *
 * @author Sachindra Dasun
 */
@Configuration
@ConfigurationProperties(prefix = "allowed-services")
public class ServiceRegistryProperties {

    private String patterns;

    public String getPatterns() {
        return patterns;
    }

    public void setPatterns(String patterns) {
        this.patterns = patterns;
    }

}
