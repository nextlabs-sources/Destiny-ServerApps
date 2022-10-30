package com.nextlabs.authentication.handlers.authentication;

import com.nextlabs.authentication.enums.AuthType;
import com.nextlabs.authentication.handlers.authentication.provisioners.Provisioner;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.principal.ClientCredential;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.provision.DelegatedClientUserProfileProvisioner;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.pac4j.authentication.handler.support.DelegatedClientAuthenticationHandler;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;

public class DelegatedAuthenticationHandler
                extends DelegatedClientAuthenticationHandler {
    private static final Logger logger = LoggerFactory.getLogger(DelegatedAuthenticationHandler.class);

    @Autowired
    @Qualifier("AzureProvisioner")
    private Provisioner azureProvisioner;

    @Autowired
    @Qualifier("SAML2Provisioner")
    private Provisioner saml2Provisioner;

    public DelegatedAuthenticationHandler(final String name, final Integer order,
                    final ServicesManager servicesManager, final PrincipalFactory principalFactory,
                    final Clients clients, final DelegatedClientUserProfileProvisioner profileProvisioner,
                    final SessionStore<JEEContext> sessionStore) {
        super(name, order, servicesManager, principalFactory, clients, profileProvisioner, sessionStore);
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(final Credential credential)
                    throws GeneralSecurityException {
        try {
            final AuthenticationHandlerExecutionResult result = super.doAuthentication(credential);
            ClientCredential clientCredential = (ClientCredential) credential;

            if(AuthType.SAML2.toString().equalsIgnoreCase(clientCredential.getClientName())) {
                saml2Provisioner.provisionUser(clientCredential, result);
            } else {
                azureProvisioner.provisionUser(clientCredential, result);
            }

            return result;
        } catch(FailedLoginException e) {
            throw e;
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }

        throw new FailedLoginException("Failed to authenticate user");
    }
}
