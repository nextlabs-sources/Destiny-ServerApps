package com.nextlabs.authentication.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cc-oidc-config")
public class CcOidcProperties {

    private String encryptionJwks;
    private String signingJwks;

    public String getEncryptionJwks() {
        return encryptionJwks;
    }

    public void setEncryptionJwks(String encryptionJwks) {
        this.encryptionJwks = encryptionJwks;
    }

    public String getSigningJwks() {
        return signingJwks;
    }

    public void setSigningJwks(String signingJwks) {
        this.signingJwks = signingJwks;
    }

}
