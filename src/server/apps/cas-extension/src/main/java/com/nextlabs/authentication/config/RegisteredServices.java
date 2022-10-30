package com.nextlabs.authentication.config;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.oidc.claims.OidcProfileScopeAttributeReleasePolicy;
import org.apereo.cas.services.DefaultRegisteredServiceMultifactorPolicy;
import org.apereo.cas.services.OidcRegisteredService;
import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.services.RegisteredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.nextlabs.authentication.config.properties.CcOidcProperties;
import com.nextlabs.authentication.config.properties.GoogleAuthenticatorProperties;
import com.nextlabs.authentication.config.properties.ServiceRegistryProperties;
import com.nextlabs.serverapps.common.properties.CCOIDCService;
import com.nextlabs.serverapps.common.properties.CasOidcProperties;

/**
 * Component to hold the registered services list.
 *
 * @author Sachindra Dasun
 */

@RefreshScope
@Component
public class RegisteredServices {

    @Autowired
    private CasOidcProperties casOidcProperties;

    @Autowired
    private CcOidcProperties ccOidcProperties;

    @Autowired
    private ServiceRegistryProperties serviceRegistryProperties;

    @Autowired
    private GoogleAuthenticatorProperties googleAuthenticatorProperties;

    private List<RegisteredService> services;

    @PostConstruct
    private void init() {
        if (serviceRegistryProperties != null && StringUtils.isNotEmpty(serviceRegistryProperties.getPatterns())) {
            String[] servicePatterns = serviceRegistryProperties.getPatterns().split(",");
            int id = 100;
            for (String servicePattern : servicePatterns) {
                RegexRegisteredService regexRegisteredService = new RegexRegisteredService();
                String serviceName = String.format("Control Center Service - %d", id);
                regexRegisteredService.setName(serviceName);
                regexRegisteredService.setDescription(serviceName);
                regexRegisteredService.setId(id++);
                regexRegisteredService.setServiceId(servicePattern.trim());
                regexRegisteredService.getAccessStrategy().getDelegatedAuthenticationPolicy().getAllowedProviders().add("azure");
                regexRegisteredService.getAccessStrategy().getDelegatedAuthenticationPolicy().getAllowedProviders().add("saml2");
                if(googleAuthenticatorProperties.isEnabled()) {
                    DefaultRegisteredServiceMultifactorPolicy defaultRegisteredServiceMultifactorPolicy = new DefaultRegisteredServiceMultifactorPolicy();
                    defaultRegisteredServiceMultifactorPolicy.setBypassEnabled(false);
                    defaultRegisteredServiceMultifactorPolicy.setMultifactorAuthenticationProviders(Collections.singleton("mfa-gauth"));
                    regexRegisteredService.setMultifactorPolicy(defaultRegisteredServiceMultifactorPolicy);
                }

                getServices().add(regexRegisteredService);
            }
            for (CCOIDCService service : casOidcProperties.getServices()){
                OidcRegisteredService oidcRegisteredService = new OidcRegisteredService();
                oidcRegisteredService.setBypassApprovalPrompt(service.isBypassApprovalPrompt());
                oidcRegisteredService.setName(service.getClientId());
                oidcRegisteredService.setDescription(service.getClientId());
                oidcRegisteredService.setId(id++);
                oidcRegisteredService.setServiceId(service.getServiceId());

                HashSet<String> grantTypes = new HashSet<>();
                grantTypes.add("password");
                grantTypes.add("refresh_token");
                grantTypes.add("authorization_code");
                oidcRegisteredService.setSupportedGrantTypes(grantTypes);

                // Signing jwks is configured as environment property in db
                oidcRegisteredService.setJwks(ccOidcProperties.getEncryptionJwks());

                oidcRegisteredService.setClientId(service.getClientId());
                oidcRegisteredService.setClientSecret(service.getClientSecret());
                oidcRegisteredService.setGenerateRefreshToken(true);
                oidcRegisteredService.setEncryptIdToken(service.isEncryptIdToken());
                oidcRegisteredService.setIdTokenEncryptionAlg(casOidcProperties.getEncryptionAlgorithm());
                oidcRegisteredService.setIdTokenEncryptionEncoding(casOidcProperties.getEncryptionEncoding());
                oidcRegisteredService.setJwtAccessToken(true);
                if(googleAuthenticatorProperties.isEnabled()) {
                    DefaultRegisteredServiceMultifactorPolicy defaultRegisteredServiceMultifactorPolicy = new DefaultRegisteredServiceMultifactorPolicy();
                    defaultRegisteredServiceMultifactorPolicy.setBypassEnabled(false);
                    defaultRegisteredServiceMultifactorPolicy.setMultifactorAuthenticationProviders(Collections.singleton("mfa-gauth"));
                    oidcRegisteredService.setMultifactorPolicy(defaultRegisteredServiceMultifactorPolicy);
                }
                oidcRegisteredService.setAttributeReleasePolicy(new OidcProfileScopeAttributeReleasePolicy());
                getServices().add(oidcRegisteredService);
            }
        }
    }

    public ServiceRegistryProperties getServiceRegistryProperties() {
        return serviceRegistryProperties;
    }

    public void setServiceRegistryProperties(ServiceRegistryProperties serviceRegistryProperties) {
        this.serviceRegistryProperties = serviceRegistryProperties;
    }

    public List<RegisteredService> getServices() {
        if (services == null) {
            services = new ArrayList<>();
        }
        return services;
    }

}
