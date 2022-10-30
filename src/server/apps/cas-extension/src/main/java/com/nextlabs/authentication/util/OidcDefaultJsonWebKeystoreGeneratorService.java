package com.nextlabs.authentication.util;

import org.apereo.cas.configuration.model.support.oidc.OidcProperties;
import org.apereo.cas.oidc.jwks.OidcJsonWebKeystoreGeneratorService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class OidcDefaultJsonWebKeystoreGeneratorService implements OidcJsonWebKeystoreGeneratorService {

    private final ResourceLoader resourceLoader;

    private final OidcProperties oidcProperties;

    public OidcDefaultJsonWebKeystoreGeneratorService(ResourceLoader resourceLoader, OidcProperties oidcProperties) {
        this.resourceLoader = resourceLoader;
        this.oidcProperties = oidcProperties;
    }

    @Override
    public Resource generate() {
        return resourceLoader.getResource(oidcProperties.getJwks().getJwksFile());
    }
}
