package com.nextlabs.destiny.inquirycenter.authentication;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthorityFactory {
    
    public static Authority getAuthority(JSONObject configuration) 
            throws JSONException {
        if(Authority.AZURE_AD.equals(configuration.getString(Authority.CODE))) {
            return new MicrosoftAzure(configuration);
        }
        
        return null;
    }
}
