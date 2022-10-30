package com.nextlabs.destiny.console.dto.authentication;

import com.nextlabs.destiny.console.enums.AuthHandlerType;

public class IdProviderFactory {

    public static final String ID_PROVIDER = "idprovider";

    private IdProviderFactory() {

    }

    public static IdProvider getProvider(String protocol, String accountId) {
        if(AuthHandlerType.OIDC.toString().equals(protocol)) {
            if(Pac4jOidcType.AZURE.toString().equals(accountId)) {
                return new Azure();
            }
        } else if(AuthHandlerType.SAML2.toString().equals(protocol)) {
            return new Saml2();
        }

        return null;
    }
}
