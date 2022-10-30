package com.nextlabs.authentication.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import org.apache.http.auth.AuthenticationException;
import org.springframework.http.HttpMethod;

public class MicrosoftGraphHelper {
    private static final String CONTENT_TYPE = "application/json";
    private static final String RESPONSE_ARRAY_KEY = "value";
    private static final String API_VERSION = "/v1.0";
    private static final String API_USERS = "/users";
    private static final String API_MEMBER_OF = "/memberOf";

    private final String apiUri;
    private final String applicationId;
    private final String applicationKey;
    private final String authorizationURL;

    public MicrosoftGraphHelper(String authority, String tenant, String authorizeService, String apiUri, String applicationId, String applicationKey) {
        this.apiUri = apiUri;
        this.applicationId = applicationId;
        this.applicationKey = applicationKey;
        this.authorizationURL = (authority.endsWith("/") ? authority : authority + "/") + tenant + authorizeService;
    }

    public IAuthenticationResult authenticate()
            throws InterruptedException, AuthenticationException {
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
            throws IOException {
        return getResponse(buildAPIRequestConnection(apiURL, accessToken));
    }

    public Set<String> getMemberOf(String accessToken, String upn, String fieldName)
            throws IOException {
        String memberOfRequestURL = String.format("%s%s%s/%s%s?$select=%s", apiUri, API_VERSION, API_USERS, upn,
                API_MEMBER_OF, fieldName);
        String response = callAPI(memberOfRequestURL, accessToken);
        JsonObject jsonRoot = JsonParser.parseString(response).getAsJsonObject();
        Set<String> groups = new HashSet<>();

        if(jsonRoot.get(RESPONSE_ARRAY_KEY) != null) {
            JsonArray groupEntries = jsonRoot.get(RESPONSE_ARRAY_KEY).getAsJsonArray();

            for (int i = 0; i < groupEntries.size(); i++) {
                JsonObject groupObject = groupEntries.get(i).getAsJsonObject();
                groups.add(getJsonObjectStringValue(groupObject, fieldName));
            }
        }
        return groups;
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
            throws IOException {
        URL url = new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(HttpMethod.GET.name());
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Accept", CONTENT_TYPE);
        return connection;
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
        }
        return response.toString();
    }

    private String getJsonObjectStringValue(JsonObject jsonObject, String memberName) {
        if(jsonObject.has(memberName) && !(jsonObject.get(memberName) instanceof JsonNull)) {
            return jsonObject.get(memberName).getAsString();
        }
        return "null";
    }
}
