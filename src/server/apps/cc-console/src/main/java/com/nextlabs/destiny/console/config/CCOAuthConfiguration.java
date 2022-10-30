package com.nextlabs.destiny.console.config;

import com.nextlabs.serverapps.common.framework.services.JwtValidationService;
import com.nextlabs.serverapps.common.framework.services.impl.JwtValidationServiceImpl;
import com.nextlabs.serverapps.common.properties.CasOidcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CC OAuthConfiguration
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
@Configuration
public class CCOAuthConfiguration {

    @RefreshScope
    @Bean
    @ConfigurationProperties(prefix = "cc-oidc-config")
    public CasOidcProperties casOidcProperties(){
        return new CasOidcProperties();
    }

    @RefreshScope
    @Bean
    public JwtValidationService jwtValidationService(){
        return new JwtValidationServiceImpl(casOidcProperties());
    }
}
