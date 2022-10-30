package com.nextlabs.destiny.inquirycenter.authentication;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public interface Authority {

	public static final String AZURE_AD = "AzureAD";
	public static final String URL_ENCODING = "UTF-8";
    public static final String STATES = "states";
    public static final String STATE = "state";
    public static final String NONCE = "nonce";
    public static final Integer STATE_TTL = 3600;

    public static final String CODE = "code";
    public static final String ENABLED = "enabled";
    public static final String DISPLAY_ORDER = "displayOrder";
    public static final String AUTHORITY_URI = "authorityUri";
    public static final String ATTRIBUTE_URI = "attributeUri";
    public static final String TENANT_ID = "tenantId";
    public static final String AUTHORIZE_SERVICE = "authorizeService";
    public static final String TOKEN_CLAIM_SERVICE = "tokenClaimService";
    public static final String APPLICATION_ID = "applicationId";
    public static final String APPLICATION_KEY = "applicationKey";
    public static final String API_URI = "apiUri";

    public static final String PRINCIPAL_SESSION_NAME = "principal";
    public static final String ERROR = "error";
    public static final String ERROR_DESCRIPTION = "error_description";
    public static final String ERROR_URI = "error_uri";
    public static final String ID_TOKEN = "id_token";

    Map<String, String> getUserAttributes(String userId, Map<String, String> userAttributeMapping);
    Set<String> getUserGroups(String userId);
}
