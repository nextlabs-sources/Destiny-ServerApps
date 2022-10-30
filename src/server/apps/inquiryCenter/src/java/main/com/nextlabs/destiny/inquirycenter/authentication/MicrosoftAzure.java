package com.nextlabs.destiny.inquirycenter.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;

import com.bluejungle.framework.crypt.IDecryptor;
import com.bluejungle.framework.crypt.ReversibleEncryptor;

public class MicrosoftAzure
        implements Authority {

    private static final Log logger = LogFactory.getLog(MicrosoftAzure.class);
    private static final String UNIQUE_ID = "${id}";
    private static final String API_VERSION = "/v1.0";
    private static final String API_USERS = "/users/";
    private static final String API_MEMBER_OF = "/memberOf";

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

    public Map<String, String> getUserAttributes(String userId, Map<String, String> userAttributeMapping) {
    	MicrosoftGraphHelper graphHelper;
    	Map<String, String> userAttributes = new HashMap<>();
    	
    	try {
    		graphHelper = buildHelper();
	    	IAuthenticationResult authResult = graphHelper.authenticate();
	    	JSONObject attributes = new JSONObject(graphHelper.callAPI(
	    			configurations.get(ATTRIBUTE_URI).replace(UNIQUE_ID, URLEncoder.encode(userId, StandardCharsets.UTF_8.toString())),
	    			authResult.accessToken()));

	    	Iterator<String> attributeKey = attributes.keys();

	    	while(attributeKey.hasNext()) {
	    		String key = attributeKey.next();
	    		if(userAttributeMapping.containsValue(key)) {
	    			String nextlabsKey = key;
	    			
    				for(Entry<String, String> mapping : userAttributeMapping.entrySet()) {
    					if(key.equals(mapping.getValue())) {
    						nextlabsKey = mapping.getKey();
    						break;
    					}
    				}
	    			
	    			Object value = attributes.get(key);
	    			
	    			if(value != null) {
	    				if(value instanceof JSONArray) {
	    					continue;
	    				}
	    				
	    				userAttributes.put(nextlabsKey, value.toString());
	    			}
	    		}
	    	}
    		
        } catch(Exception err) {
            logger.error(err.getMessage(), err);
        }
    	
    	return userAttributes;
    }

    public Set<String> getUserGroups(String userId) {
        MicrosoftGraphHelper graphHelper;
        Set<String> groups = new HashSet<>();

        try {
            graphHelper = buildHelper();
            StringBuilder userGroupsURL = new StringBuilder();
            userGroupsURL.append(configurations.get(MicrosoftAzure.API_URI));
            userGroupsURL.append(API_VERSION).append(API_USERS).append(userId).append(API_MEMBER_OF);
            userGroupsURL.append("?$select=displayName");

            IAuthenticationResult authResult = graphHelper.authenticate();
            JSONObject attributes = new JSONObject(graphHelper.callAPI(
                            userGroupsURL.toString(), authResult.accessToken()));

            Iterator<String> attributeKey = attributes.keys();

            while(attributeKey.hasNext()) {
                String key = attributeKey.next();

                if(key.equals("value")) {
                    Object value = attributes.get(key);

                    if(value != null) {
                        if(value instanceof JSONArray) {
                            JSONArray userGroups = (JSONArray)value;

                            for(int i = 0; i < userGroups.length(); i++) {
                                groups.add(userGroups.getJSONObject(i).getString("displayName"));
                            }
                        }
                    }
                }
            }
        } catch(Exception err) {
            logger.error(err.getMessage(), err);
        }

        return groups;
    }

	private MicrosoftGraphHelper buildHelper() {
		return new MicrosoftGraphHelper(configurations.get(MicrosoftAzure.AUTHORITY_URI),
				configurations.get(MicrosoftAzure.TENANT_ID), 
				configurations.get(MicrosoftAzure.AUTHORIZE_SERVICE),
				configurations.get(MicrosoftAzure.API_URI),
				configurations.get(MicrosoftAzure.APPLICATION_ID), 
				configurations.get(MicrosoftAzure.APPLICATION_KEY));
	}
}

class MicrosoftGraphHelper {

	private static final Log logger = LogFactory.getLog(MicrosoftGraphHelper.class);
	
    private static final String CONTENT_TYPE = "application/json";

    private String apiUri;
    private String applicationId;
    private String applicationKey;
    private String authorizationURL;

    public MicrosoftGraphHelper(String authority, String tenant, String authorizeService, String apiUri, String applicationId, String applicationKey) {
        this.apiUri = apiUri;
        this.applicationId = applicationId;
        this.applicationKey = applicationKey;
        this.authorizationURL = (authority.endsWith("/") ? authority : authority + "/") + tenant + authorizeService;
    }

    public IAuthenticationResult authenticate()
    		throws Exception {
        IAuthenticationResult result;

        try {
            result = getAccessTokenByClientCredentialGrant(getClientApplication());
            if (result == null) {
                throw new AuthenticationException("Authentication result for " + apiUri + " from " + authorizationURL + " was null");
            }
        } catch (TimeoutException e) {
            throw new AuthenticationException("Timed out acquiring authentication token from " + authorizationURL);
        } catch (MalformedURLException e) {
            throw new AuthenticationException("Unable to connection to URL " + authorizationURL, e);
        } catch (ExecutionException e) {
            throw new AuthenticationException(e.getMessage());
        }

        return result;
    }

    public String callAPI(String apiURL, String accessToken)
    		throws Exception {
    	return getResponse(buildAPIRequestConnection(apiURL, accessToken));
    }

    private ConfidentialClientApplication getClientApplication()
                    throws MalformedURLException {
        return ConfidentialClientApplication
                        .builder(applicationId,
                                        ClientCredentialFactory.createFromSecret(applicationKey))
                        .authority(authorizationURL)
                        .build();
    }

    private IAuthenticationResult getAccessTokenByClientCredentialGrant(ConfidentialClientApplication clientApplication)
                    throws ExecutionException, InterruptedException, TimeoutException {
        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                        Collections.singleton("https://graph.microsoft.com/.default"))
                        .build();

        CompletableFuture<IAuthenticationResult> future = clientApplication.acquireToken(clientCredentialParam);
        return future.get(30, TimeUnit.SECONDS);
    }

    private HttpURLConnection buildAPIRequestConnection(String requestURL, String accessToken)
    		throws Exception {
    	try {
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod(HttpMethod.GET.name());
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Accept", CONTENT_TYPE);
            
            return connection;
    	} catch(Exception e) {
    		logger.error(e.getMessage(), e);
    		throw e;
    	}
    }

    private String getResponse(HttpURLConnection httpConnection)
    		throws IOException {
    	StringBuilder response = new StringBuilder();
    	try(BufferedReader reader = (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) ?
    			new BufferedReader(new InputStreamReader(httpConnection.getInputStream())) :
    				new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()))) {
        	String line;
    		while((line = reader.readLine()) != null) {
    			response.append(line);
    		}
    	} catch(IOException e) {
    		logger.error(e.getMessage(), e);
    		throw e;
    	}
    	
    	return response.toString();
    }
}
