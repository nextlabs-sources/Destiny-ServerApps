package com.nextlabs.destiny.console.model.authentication;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluejungle.framework.crypt.IDecryptor;
import com.bluejungle.framework.crypt.ReversibleEncryptor;

public class MicrosoftAzure
        implements Authority {

    private static final Logger logger = LoggerFactory.getLogger(MicrosoftAzure.class);
    
    protected static final IDecryptor decryptor = new ReversibleEncryptor();
    protected Map<String, String> configurations = new HashMap<>();

    public MicrosoftAzure(JSONObject configuration) {
        super();
        setConfigurations(configuration);
    }

    private void setConfigurations(JSONObject configuration) {
        try {
            configurations.put(AUTHORITY_URI, configuration.getString(AUTHORITY_URI));
            configurations.put(ATTRIBUTE_URI, configuration.getString(ATTRIBUTE_URI));
            configurations.put(TENANT_ID, configuration.getString(TENANT_ID));
            configurations.put(AUTHORIZE_SERVICE, configuration.getString(AUTHORIZE_SERVICE));
            configurations.put(TOKEN_CLAIM_SERVICE, configuration.getString(TOKEN_CLAIM_SERVICE));
            configurations.put(APPLICATION_ID, configuration.getString(APPLICATION_ID));
            configurations.put(APPLICATION_KEY, decryptor.decrypt(configuration.getString(APPLICATION_KEY)));
            configurations.put(API_URI, configuration.getString(API_URI));
        } catch(Exception err) {
            logger.error(err.getMessage(), err);
        }
    }
}
