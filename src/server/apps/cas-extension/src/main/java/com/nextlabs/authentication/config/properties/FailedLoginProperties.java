package com.nextlabs.authentication.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Failed login properties.
 *
 * @author Sachindra Dasun
 */
@Configuration
@ConfigurationProperties(prefix = "failed.login")
public class FailedLoginProperties {

    private int attempts;

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

}
