/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 18, 2016
 *
 */
package com.nextlabs.destiny.console.config.root;

import java.net.URI;

import com.nextlabs.destiny.console.dto.authentication.ActiveUserStore;
import com.nextlabs.destiny.console.services.HttpResponseService;
import com.nextlabs.destiny.console.web.filters.ApiRequestAuthenticationFilter;
import com.nextlabs.destiny.console.web.filters.CORSFilter;
import com.nextlabs.destiny.console.web.filters.CsrfTokenBindingFilter;
import com.nextlabs.destiny.console.web.filters.DynamicLogoutFilter;
import com.nextlabs.destiny.console.web.filters.HeaderTagAppendingFilter;
import com.nextlabs.destiny.console.web.filters.JwtValidationFilter;
import com.nextlabs.serverapps.common.framework.services.JwtValidationService;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.writers.frameoptions.StaticAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.destiny.console.config.CSRFRequestMatcher;
import com.nextlabs.destiny.console.config.properties.XFrameProperties;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;
import com.nextlabs.destiny.console.model.authorization.AccessControl;
import com.nextlabs.destiny.console.services.ApplicationUserService;
import com.nextlabs.destiny.console.services.EntityAuditLogService;
import com.nextlabs.destiny.console.services.authorization.AccessControlDataService;

/**
 *
 * Application security configuration
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Autowired
    private ConfigurationDataLoader configDataLoader;

    @Autowired
    private AccessControlDataService accessControlDataService;

    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private EntityAuditLogService entityAuditLogService;

    @Autowired
    private XFrameProperties xFrameProperties;

    private JwtValidationService jwtValidationService;

    private HttpResponseService httpResponseService;

    private static final String ALLOW_FROM = "ALLOW-FROM";
    private static final String DENY = "DENY";
    private static final String SAMEORIGIN = "SAMEORIGIN";

    @Autowired
    public void setJwtValidationService(JwtValidationService jwtValidationService) {
        this.jwtValidationService = jwtValidationService;
    }

    @Autowired
    public void setHttpResponseService(HttpResponseService httpResponseService) {
        this.httpResponseService = httpResponseService;
    }
    
    @Bean
    public SessionAuthenticationStrategy sessionStrategy() {
        return new SessionFixationProtectionStrategy();
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(configDataLoader.getAppServiceSecurity());
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(
                authenticationUserDetailsService());
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider
                .setTicketValidator(cas20ServiceTicketValidator());
        casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only");
        return casAuthenticationProvider;
    }

    @SuppressWarnings("unchecked")
    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService() {
        return new CasAuthenticationUserDetailsService();
    }

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(
                configDataLoader.getCasServiceUrl());
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter
                .setAuthenticationManager(authenticationManager());
        casAuthenticationFilter
                .setSessionAuthenticationStrategy(sessionStrategy());
        return casAuthenticationFilter;
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint
                .setLoginUrl(configDataLoader.getCasServiceLogin());
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    @Bean
    public ApiRequestAuthenticationFilter apiRequestAuthenticationFilter() {
        return new ApiRequestAuthenticationFilter();
    }

    @Bean
    public CsrfTokenBindingFilter csrfTokenBindingFilter() {
        return new CsrfTokenBindingFilter();
    }

    @Bean
    public JwtValidationFilter ccJwtValidationFilter() {
        return new JwtValidationFilter(jwtValidationService, applicationUserService, httpResponseService);
    }
    
    @Bean
    public CORSFilter corsFilter() {
        return new CORSFilter();
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    @Bean 
    public DynamicLogoutFilter dynamicLogoutFilter() {
    	String logoutSuccessUrl = configDataLoader.getCasServiceLogout() + "?service=" + configDataLoader.getAppServiceHome();
    	
    	return new DynamicLogoutFilter(logoutSuccessUrl, 
    			new SecurityContextLogoutHandler[] {new SecurityContextLogoutHandler()},
                        entityAuditLogService);
    }
    
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CCAccessDeniedHandler();
    }

    @Bean
    public ReversibleEncryptor reversibleEncryptor() {
        return new ReversibleEncryptor();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/ui").antMatchers("/ui/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	String xFrameOption = xFrameProperties.getOptions();
		String allowedOrigins = xFrameProperties.getAllowedOrigins();
		
        http.csrf().requireCsrfProtectionMatcher(new CSRFRequestMatcher());
        http.exceptionHandling()
                .authenticationEntryPoint(casAuthenticationEntryPoint()).and()
                .addFilter(casAuthenticationFilter())
                .addFilterBefore(corsFilter(),
                        CasAuthenticationFilter.class)
                .addFilterBefore(singleSignOutFilter(),
                        CasAuthenticationFilter.class)
                .addFilterBefore(dynamicLogoutFilter(),
                        LogoutFilter.class);
        http.addFilterAfter(ccJwtValidationFilter(), CsrfFilter.class);
        // CSRF tokens handling
        http.addFilterAfter(csrfTokenBindingFilter(), CsrfFilter.class);
        http.addFilterAfter(apiRequestAuthenticationFilter(), CsrfFilter.class);

        // meta tags and no cache parameters
        http.addFilterBefore(headerTagAppendingFilter(), CsrfFilter.class);
        
        for (AccessControl accessControl : accessControlDataService.getAccessControlList()) {
            http.authorizeRequests()
                    .antMatchers(accessControl.getRequestMethod(), accessControl.getUrlPattern())
                    .access(accessControl.getExpression());
        }
        http.authorizeRequests()
                .anyRequest().denyAll();
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
		
        switch(xFrameOption) {
			case DENY:
				http.headers().frameOptions().deny();
				break;
			case SAMEORIGIN:
				http.headers().frameOptions().sameOrigin();
				break;
			case ALLOW_FROM:
				if (!StringUtils.isEmpty(allowedOrigins)) {
					http.headers()
					.addHeaderWriter(new XFrameOptionsHeaderWriter(new StaticAllowFromStrategy(new URI(allowedOrigins))));
				} else {
					http.headers().frameOptions().deny();
				}
				break;
			default:
				http.headers().frameOptions().deny();
		}


        /**
         * <logout invalidate-session="true" delete-cookies="JSESSIONID" />
         */
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(casAuthenticationProvider());
    }

    @Bean
    public HeaderTagAppendingFilter headerTagAppendingFilter(){
        return new HeaderTagAppendingFilter();
    }

    @Bean
    public ActiveUserStore activeUserStore(){
        return new ActiveUserStore();
    }
}
