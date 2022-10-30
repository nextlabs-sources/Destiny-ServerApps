package com.nextlabs.destiny.console.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Password enforcement properties.
 *
 * @author Sachindra Dasun
 */
@Configuration
@ConfigurationProperties(prefix = "enforce.password")
public class PasswordEnforcementProperties {

    private int history;

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

}
