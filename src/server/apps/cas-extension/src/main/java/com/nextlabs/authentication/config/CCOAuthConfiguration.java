package com.nextlabs.authentication.config;

import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.oidc.jwks.OidcJsonWebKeystoreGeneratorService;
import org.apereo.cas.support.oauth.profile.OAuth20ProfileScopeToAttributesFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;

import com.nextlabs.authentication.handlers.authentication.CCOAuth20CasAuthenticationBuilder;
import com.nextlabs.authentication.services.UserAttributeProviderFactory;
import com.nextlabs.authentication.services.UserPermissionService;
import com.nextlabs.authentication.util.OidcDefaultJsonWebKeystoreGeneratorService;
import com.nextlabs.serverapps.common.properties.CasOidcProperties;

/**
 * CC OAuthConfiguration
 * OIDC signing key param is set in CustomBeanPostProcessor.
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
@Configuration
public class CCOAuthConfiguration {

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private UserAttributeProviderFactory userAttributeProviderFactory;

    @Primary
    @Bean(name = "oauthCasAuthenticationBuilder")
    @RefreshScope
    public CCOAuth20CasAuthenticationBuilder oauthCasAuthenticationBuilder
            (PrincipalFactory oauthPrincipalFactory,
             ServiceFactory webApplicationServiceFactory,
             OAuth20ProfileScopeToAttributesFilter profileScopeToAttributesFilter,
             CasConfigurationProperties casProperties,
             AuthenticationHandler ccAuthenticationHandler,
             CasOidcProperties casOidcProperties) {
        return new CCOAuth20CasAuthenticationBuilder(oauthPrincipalFactory, webApplicationServiceFactory,
                profileScopeToAttributesFilter,
                casProperties, ccAuthenticationHandler, userPermissionService, userAttributeProviderFactory,
                casOidcProperties);
    }

    @Bean
    public OidcJsonWebKeystoreGeneratorService oidcJsonWebKeystoreGeneratorService(ResourceLoader resourceLoader,
                                                                                   CasConfigurationProperties casProperties) {
        return new OidcDefaultJsonWebKeystoreGeneratorService(resourceLoader, casProperties.getAuthn().getOidc());
    }

    @RefreshScope
    @Bean
    @ConfigurationProperties(prefix = "cc-oidc-config")
    public CasOidcProperties casOidcProperties(){
        return new CasOidcProperties();
    }
}
