package com.nextlabs.destiny.console.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "policy.workflow")
public class PolicyWorkflowProperties {

    private boolean enable;

    public boolean isWorkflowEnabled() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
