package com.nextlabs.destiny.console.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties of Control Center Policy Validator.
 *
 * @author Sachindra Dasun
 */
@Component
@ConfigurationProperties(prefix = "policy-validator")
public class PolicyValidatorProperties {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
