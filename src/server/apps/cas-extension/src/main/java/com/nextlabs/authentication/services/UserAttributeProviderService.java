package com.nextlabs.authentication.services;

import java.util.Map;
import java.util.Set;

/**
 * Service interface for the user attribute provider.
 *
 * @author Sachindra Dasun
 */
public interface UserAttributeProviderService {

    Map<String, Set<String>> getAttributes(String username);

    String getAuthenticationMethod();

    default String getInternalAttributeKey(Map<String, String> attributeMapping, String externalAttributeKey) {
        for (Map.Entry<String, String> entry: attributeMapping.entrySet()) {
            if (externalAttributeKey.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
