package com.nextlabs.authentication.services.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.authentication.enums.AuthType;
import com.nextlabs.authentication.enums.UserAttributeKey;
import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.handlers.authentication.AuthHandlerDetail;
import com.nextlabs.authentication.models.ApplicationUser;
import com.nextlabs.authentication.repositories.ApplicationUserRepository;
import com.nextlabs.authentication.repositories.AuthHandlerRegistryRepository;
import com.nextlabs.authentication.services.UserAttributeProviderService;

/**
 * Provides attributes of an Azure AD user.
 *
 * @author Sachindra Dasun
 */
@Service
public class AzureAdUserAttributeProviderServiceImpl implements UserAttributeProviderService {

    private static final Logger logger = LoggerFactory.getLogger(AzureAdUserAttributeProviderServiceImpl.class);

    private static final String API_URI = "apiUri";
    private static final String APPLICATION_ID = "applicationId";
    private static final String APPLICATION_KEY = "applicationKey";
    private static final String ATTRIBUTE_URI = "attributeUri";
    private static final String AUTHORITY_URI = "authorityUri";
    private static final String AUTHORIZE_SERVICE = "authorizeService";
    private static final String TENANT_ID = "tenantId";
    private static final String UNIQUE_ID = "${id}";
    private static final String DISPLAY_NAME = "displayName";

    @Autowired
    private AuthHandlerRegistryRepository handlerRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Override
    public Map<String, Set<String>> getAttributes(String username) {
        Map<String, Set<String>> userAttributes = new LinkedHashMap<>();
        ApplicationUser applicationUser = applicationUserRepository.findByUsernameIgnoreCaseAndStatus(username,
                UserStatus.ACTIVE).orElseThrow();
        userAttributes.put(UserAttributeKey.NEXTLABS_CC_USER_CATEGORY.getKey(),
                Set.of(applicationUser.getUserCategory().name()));
        try {
            AuthHandlerDetail authHandlerDetail = new AuthHandlerDetail(
                    handlerRepository.findById(applicationUser.getAuthHandlerId()).orElseThrow());
            MicrosoftGraphHelper graphHelper = buildHelper(authHandlerDetail);
            IAuthenticationResult authResult = graphHelper.authenticate();
            JSONObject attributes = new JSONObject(graphHelper.callAPI(
                    authHandlerDetail.getConfigData().get(ATTRIBUTE_URI).replace(UNIQUE_ID, username),
                    authResult.accessToken()));

            Iterator<String> externalAttributeKeys = attributes.keys();

            Map<String, String> attributeMapping = authHandlerDetail.getUserAttributes();
            while (externalAttributeKeys.hasNext()) {
                String externalAttributeKey = externalAttributeKeys.next();
                if (attributeMapping.containsValue(externalAttributeKey)) {
                    Object attributeValue = attributes.get(externalAttributeKey);
                    if (attributeValue != null) {
                        if (attributeValue instanceof JSONArray) {
                            continue;
                        }
                        String internalAttributeKey = getInternalAttributeKey(attributeMapping, externalAttributeKey);
                        userAttributes.put(internalAttributeKey, Set.of(attributeValue.toString()));
                    }
                }
            }
            userAttributes.put("groups", graphHelper.getMemberOf(graphHelper.authenticate().accessToken(), username,
                    DISPLAY_NAME));
            userAttributes.put(UserAttributeKey.NEXTLABS_CC_USER_ID.getKey(), Set.of(String.valueOf(applicationUser.getId())));
        } catch (Exception e) {
            logger.error("Error in getting attributes", e);
        }
        return userAttributes;
    }

    @Override
    public String getAuthenticationMethod() {
        return AuthType.OIDC.name();
    }

    private MicrosoftGraphHelper buildHelper(AuthHandlerDetail handlerDTO) {
        return new MicrosoftGraphHelper(handlerDTO.getConfigData().get(AUTHORITY_URI),
                handlerDTO.getConfigData().get(TENANT_ID),
                handlerDTO.getConfigData().get(AUTHORIZE_SERVICE),
                handlerDTO.getConfigData().get(API_URI),
                handlerDTO.getConfigData().get(APPLICATION_ID),
                handlerDTO.getConfigData().get(APPLICATION_KEY));
    }

}
