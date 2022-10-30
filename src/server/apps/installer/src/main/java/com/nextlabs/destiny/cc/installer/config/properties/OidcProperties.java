package com.nextlabs.destiny.cc.installer.config.properties;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Properties available for OIDC configuration.
 *
 * @author Sachindra Dasun
 */
public class OidcProperties {

    private String clientSecret;

    public OidcProperties() {
        this.clientSecret = RandomStringUtils.random(32, true, true);
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

}
