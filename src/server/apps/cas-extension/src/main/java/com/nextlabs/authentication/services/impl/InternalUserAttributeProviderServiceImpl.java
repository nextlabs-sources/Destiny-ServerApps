package com.nextlabs.authentication.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.authentication.enums.AuthType;
import com.nextlabs.authentication.enums.UserAttributeKey;
import com.nextlabs.authentication.enums.UserCategory;
import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.repositories.ApplicationUserRepository;
import com.nextlabs.authentication.repositories.SuperApplicationUserRepository;
import com.nextlabs.authentication.services.UserAttributeProviderService;

/**
 * Provides attributes of an internal user.
 *
 * @author Sachindra Dasun
 */
@Service
public class InternalUserAttributeProviderServiceImpl implements UserAttributeProviderService {

    public static final String SUPER_USER_USERNAME = "Administrator";

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private SuperApplicationUserRepository superApplicationUserRepository;

    @Override
    public Map<String, Set<String>> getAttributes(String username) {
        return username.equalsIgnoreCase(SUPER_USER_USERNAME) ? getSuperUserAttributes() :
                getApplicationUserAttributes(username);
    }

    private Map<String, Set<String>> getSuperUserAttributes() {
        Map<String, Set<String>> attributes = new HashMap<>();
        attributes.put(UserAttributeKey.NEXTLABS_CC_USER_CATEGORY.getKey(), Set.of(UserCategory.ADMIN.name()));
        superApplicationUserRepository.findByUsernameIgnoreCase(SUPER_USER_USERNAME)
                .ifPresent(user -> {
                    attributes.put(UserAttributeKey.NEXTLABS_CC_USER_ID.getKey(), Set.of(String.valueOf(user.getId())));
                    attributes.put(UserAttributeKey.DISPLAY_NAME.getKey(), Set.of(user.getDisplayName()));
                    if (StringUtils.isNotEmpty(user.getEmail())) {
                        attributes.put(UserAttributeKey.EMAIL.getKey(), Set.of(user.getEmail()));
                    }
                    attributes.put(UserAttributeKey.FIRST_NAME.getKey(), Set.of(user.getFirstName()));
                    if (StringUtils.isNotEmpty(user.getLastName())) {
                        attributes.put(UserAttributeKey.LAST_NAME.getKey(), Set.of(user.getLastName()));
                    }
                    attributes.put(UserAttributeKey.USERNAME.getKey(), Set.of(user.getUsername()));
                });
        return attributes;
    }

    /**
     * Provide the attributes of the user with given username.
     * @param username username of the user
     * @return the map of attributes
     */
    private Map<String, Set<String>> getApplicationUserAttributes(String username) {
        Map<String, Set<String>> attributes = new HashMap<>();
        applicationUserRepository.findByUsernameIgnoreCaseAndStatus(username, UserStatus.ACTIVE)
                .ifPresent(applicationUser -> {
                            attributes.put(UserAttributeKey.NEXTLABS_CC_USER_CATEGORY.getKey(),
                                    Set.of(applicationUser.getUserCategory().name()));
                            attributes.put(UserAttributeKey.NEXTLABS_CC_USER_ID.getKey(), Set.of(String.valueOf(applicationUser.getId())));
                            attributes.put(UserAttributeKey.DISPLAY_NAME.getKey(), Set.of(applicationUser.getDisplayName()));
                            if (StringUtils.isNotEmpty(applicationUser.getEmail())) {
                                attributes.put(UserAttributeKey.EMAIL.getKey(), Set.of(applicationUser.getEmail()));
                            }
                            attributes.put(UserAttributeKey.FIRST_NAME.getKey(), Set.of(applicationUser.getFirstName()));
                            if (StringUtils.isNotEmpty(applicationUser.getLastName())) {
                                attributes.put(UserAttributeKey.LAST_NAME.getKey(), Set.of(applicationUser.getLastName()));
                            }
                            attributes.put(UserAttributeKey.USERNAME.getKey(), Set.of(applicationUser.getUsername()));
                            applicationUser.getProperties()
                                    .forEach(appUserProperties ->
                                            attributes.put(appUserProperties.getPropKey(),
                                                    Set.of(appUserProperties.getPropValue())));
                        }
                );
        return attributes;
    }

    @Override
    public String getAuthenticationMethod() {
        return AuthType.INTERNAL.name();
    }

}
