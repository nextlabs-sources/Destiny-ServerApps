package com.nextlabs.destiny.console.model.authentication;

import org.json.JSONObject;

public class AuthorityFactory {

    private AuthorityFactory() {
        super();
    }

    public static Authority getAuthority(AuthHandlerTypeDetail authHandler) {
        JSONObject configuration = new JSONObject(authHandler.getConfigDataJson());
        
        if(MicrosoftAzure.AZURE_AD.equals(configuration.getString(Authority.CODE))) {
            return new MicrosoftAzure(configuration);
        }
        
        return null;
    }
}
