package com.nextlabs.authentication.handlers.authentication;

import com.nextlabs.authentication.enums.UserAttributeKey;
import com.nextlabs.authentication.services.UserAttributeProviderFactory;
import com.nextlabs.authentication.services.UserPermissionService;
import com.nextlabs.serverapps.common.exception.InvalidCredentialException;
import com.nextlabs.serverapps.common.properties.CCOIDCService;
import com.nextlabs.serverapps.common.properties.CasOidcProperties;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.*;
import org.apereo.cas.authentication.credential.BasicIdentifiableCredential;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.metadata.BasicCredentialMetaData;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.support.oauth.authenticator.OAuth20CasAuthenticationBuilder;
import org.apereo.cas.support.oauth.profile.OAuth20ProfileScopeToAttributesFilter;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.util.CollectionUtils;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CCOAuth20CasAuthenticationBuilder extends OAuth20CasAuthenticationBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CCOAuth20CasAuthenticationBuilder.class);

    private AuthenticationHandler ccAuthenticationHandler;
    private UserPermissionService userPermissionService;
    private CasOidcProperties casOidcProperties;
    private UserAttributeProviderFactory userAttributeProviderFactory;

    private static final String ACCESS_TOKEN_CONTEXT = "/oidc/accessToken";
    private static final String AUTHORIZE_CONTEXT = "/oidc/authorize";
    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";

    public CCOAuth20CasAuthenticationBuilder(PrincipalFactory principalFactory, ServiceFactory webApplicationServiceFactory,
                                             OAuth20ProfileScopeToAttributesFilter profileScopeToAttributesFilter,
                                             CasConfigurationProperties casProperties,
                                             AuthenticationHandler ccAuthenticationHandler, UserPermissionService userPermissionService,
                                             UserAttributeProviderFactory userAttributeProviderFactory,
                                             CasOidcProperties casOidcProperties) {

        super(principalFactory, webApplicationServiceFactory, profileScopeToAttributesFilter, casProperties);
        this.ccAuthenticationHandler = ccAuthenticationHandler;
        this.userPermissionService = userPermissionService;
        this.userAttributeProviderFactory = userAttributeProviderFactory;
        this.casOidcProperties = casOidcProperties;
    }

    @Override
    public Authentication build(CommonProfile profile, OAuthRegisteredService registeredService, JEEContext context, Service service) {
        var attrs = new HashMap<>(profile.getAttributes());
        attrs.putAll(profile.getAuthenticationAttributes());
        var profileAttributes = CoreAuthenticationUtils.convertAttributeValuesToMultiValuedObjects(attrs);

        Principal newPrincipal = null;
        final CredentialMetaData metadata = new BasicCredentialMetaData(new BasicIdentifiableCredential(profile.getId()));
        AuthenticationHandlerExecutionResult handlerResult = null;
        if (AUTHORIZE_CONTEXT.equals(context.getPath())){
            // for authorize grant, authentication is already done by login flow
            newPrincipal = this.principalFactory.createPrincipal(profile.getId(), profileAttributes);
            logger.debug("Created final principal [{}] after filtering attributes based on [{}]", newPrincipal, registeredService);
            final String authenticator = profile.getClass().getCanonicalName();
            handlerResult = new DefaultAuthenticationHandlerExecutionResult(authenticator, metadata, newPrincipal, new ArrayList<>());
        } else if (ACCESS_TOKEN_CONTEXT.equals(context.getPath())) {
            validateClient(context.getRequestParameter(OAuth20Constants.CLIENT_ID).orElse(null),
                    context.getRequestParameter(OAuth20Constants.CLIENT_SECRET).orElse(null));
            String username = context.getRequestParameter(USERNAME_PARAM).orElse(null);
            String password = context.getRequestParameter(PASSWORD_PARAM).orElse(null);
            newPrincipal = this.principalFactory.createPrincipal(username, profileAttributes);
            logger.debug("Created final principal [{}] after filtering attributes based on [{}]", username, registeredService);

            UsernamePasswordCredential credential = new UsernamePasswordCredential();
            credential.setUsername(username);
            credential.setPassword(password);
            try {
                handlerResult = ccAuthenticationHandler.authenticate(credential);
                profile.addAuthenticationAttribute("authenticationMethod", List.of(handlerResult.getHandlerName()));
            } catch (GeneralSecurityException | PreventedException e) {
                throw new InvalidCredentialException("Error while authenticating OAuth token request");
            }
        }

        final Set<Object> scopes = CollectionUtils.toCollection(context.getNativeRequest().getParameterValues(OAuth20Constants.SCOPE));

        final String state = context.getRequestParameter(OAuth20Constants.STATE).orElse(StringUtils.EMPTY);
        final String nonce = context.getRequestParameter(OAuth20Constants.NONCE).orElse(StringUtils.EMPTY);
        logger.debug("OAuth [{}] is [{}], and [{}] is [{}]", OAuth20Constants.STATE, state, OAuth20Constants.NONCE, nonce);

        Map<String, Set<String>> attributes = userAttributeProviderFactory
                .getUserAttributeProvider(profile.getId())
                .getAttributes(profile.getId());
        addProfileAttributes(newPrincipal, attributes);
        Set<String> permissions = userPermissionService.getPermissions(attributes);

        final AuthenticationBuilder bldr = DefaultAuthenticationBuilder.newInstance()
                .addAttribute("permissions", permissions)
                .addAttribute("scopes", scopes)
                .addAttribute(OAuth20Constants.STATE, state)
                .addAttribute(OAuth20Constants.NONCE, nonce)
                .addCredential(metadata)
                .setPrincipal(newPrincipal)
                .setAuthenticationDate(ZonedDateTime.now())
                .addSuccess(CCAuthenticationHandler.class.getCanonicalName(), handlerResult);

        return bldr.build();
    }

    private void addProfileAttributes(Principal newPrincipal, Map<String, Set<String>> attributes) {
        Set<String> firstNameAttributeValue = attributes.get(UserAttributeKey.FIRST_NAME.getKey());
        if (firstNameAttributeValue != null) {
            newPrincipal.getAttributes().put("given_name", new ArrayList<>(firstNameAttributeValue));
        }
        Set<String> lastNameAttributeValue = attributes.get(UserAttributeKey.LAST_NAME.getKey());
        if (lastNameAttributeValue != null) {
            newPrincipal.getAttributes().put("family_name", new ArrayList<>(lastNameAttributeValue));
        }
    }

    private void validateClient(String requestClientId, String requestClientSecret){
        CCOIDCService requesterService = null;
        for (CCOIDCService service : casOidcProperties.getServices()){
            if (StringUtils.equals(service.getClientId(), requestClientId)){
                requesterService = service;
            }
        }
        if (requesterService == null
                || !StringUtils.equals(requesterService.getClientSecret(), requestClientSecret)) {
            throw new CredentialsException("Invalid client");
        }
    }
}
