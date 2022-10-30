package com.nextlabs.authentication.config;

import javax.sql.DataSource;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.authentication.principal.provision.DelegatedClientUserProfileProvisioner;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.pac4j.Pac4jDelegatedAuthenticationProperties;
import org.apereo.cas.services.ServicesManager;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.SessionStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nextlabs.authentication.handlers.authentication.CCAuthenticationHandler;
import com.nextlabs.authentication.handlers.authentication.InternalUserAuthenticationHandler;
import com.nextlabs.authentication.handlers.authentication.DelegatedAuthenticationHandler;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authentication handler configuration.
 *
 * @author Sachindra Dasun
 */

@Configuration
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class AuthenticationHandlerConfiguration
                implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    @Qualifier("authenticationPrincipalFactory")
    private PrincipalFactory principalFactory;

    @Autowired
    @Qualifier("clientPrincipalFactory")
    private PrincipalFactory clientPrincipalFactory;

    @Autowired
    @Qualifier("servicesManager")
    private ObjectProvider<ServicesManager> servicesManagerObjectProvider;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("ccAuthenticationHandler")
    private AuthenticationHandler ccAuthenticationHandler;

    @Autowired
    private Clients clients;

    @Autowired
    private DelegatedClientUserProfileProvisioner clientUserProfileProvisioner;

    @Autowired
    private SessionStore<JEEContext> delegatedClientDistributedSessionStore;

    @Bean(name = "ccAuthenticationHandler")
    public AuthenticationHandler ccAuthenticationHandler(PasswordEncoder delegatingPasswordEncoder, InternalUserAuthenticationHandler internalUserAuthenticationHandler) {
        final CCAuthenticationHandler handler = new CCAuthenticationHandler("CCAuthenticationHandler",
                servicesManagerObjectProvider.getIfAvailable(), principalFactory, 1);
        internalUserAuthenticationHandler.setPasswordEncoder(delegatingPasswordEncoder);
        handler.setPrimaryHandler(internalUserAuthenticationHandler);

        return handler;
    }

    @RefreshScope
    @Bean(name = "clientAuthenticationHandler")
    public AuthenticationHandler clientAuthenticationHandler() {
        final Pac4jDelegatedAuthenticationProperties pac4j = casProperties.getAuthn().getPac4j();
        final DelegatedAuthenticationHandler handler = new DelegatedAuthenticationHandler(pac4j.getName(), pac4j.getOrder(),
                        servicesManager, clientPrincipalFactory, clients, clientUserProfileProvisioner, delegatedClientDistributedSessionStore);
        handler.setTypedIdUsed(pac4j.isTypedIdUsed());
        handler.setPrincipalAttributeId(pac4j.getPrincipalAttributeId());
        return handler;
    }

    @Bean
    public InternalUserAuthenticationHandler internalUserAuthenticationHandler(DataSource dataSource){
        return new InternalUserAuthenticationHandler(
                "InternalUserAuthenticationHandler", servicesManagerObjectProvider.getIfAvailable(),
                principalFactory, null, dataSource);
    }

    @ConditionalOnMissingBean(name = "authenticationPrincipalFactory")
    @Bean
    @RefreshScope
    public PrincipalFactory authenticationPrincipalFactory() {
        return PrincipalFactoryUtils.newPrincipalFactory();
    }

    @Override
    public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(ccAuthenticationHandler);
    }
}
