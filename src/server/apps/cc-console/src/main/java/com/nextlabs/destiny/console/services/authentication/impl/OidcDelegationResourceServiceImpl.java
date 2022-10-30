package com.nextlabs.destiny.console.services.authentication.impl;

import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.services.authentication.DelegationResourceService;
import org.springframework.stereotype.Service;

@Service("OidcDelegationResourceService")
public class OidcDelegationResourceServiceImpl
        implements DelegationResourceService {

    /**
     * OIDC client does not require custom configuration
     *
     * @param handlerDetail Authentication configuration enter by user
     */
    @Override
    public void configure(AuthHandlerDetail handlerDetail) {
        // Custom configuration not require
    }

    /**
     * OIDC client does not require any clean up
     *
     */
    @Override
    public void cleanUp() {
        // Custom clean up not require
    }
}
