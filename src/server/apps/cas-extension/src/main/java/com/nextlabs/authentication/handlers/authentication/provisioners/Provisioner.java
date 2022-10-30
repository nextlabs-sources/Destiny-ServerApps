package com.nextlabs.authentication.handlers.authentication.provisioners;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.principal.ClientCredential;

import javax.security.auth.login.FailedLoginException;

public interface Provisioner {

    void provisionUser(final ClientCredential clientCredential, final AuthenticationHandlerExecutionResult result)
                    throws FailedLoginException;

}
