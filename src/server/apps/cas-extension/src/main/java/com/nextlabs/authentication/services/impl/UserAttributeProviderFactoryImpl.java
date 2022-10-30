package com.nextlabs.authentication.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.authentication.enums.AuthType;
import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.models.ApplicationUser;
import com.nextlabs.authentication.models.AuthHandlerRegistry;
import com.nextlabs.authentication.repositories.ApplicationUserRepository;
import com.nextlabs.authentication.repositories.AuthHandlerRegistryRepository;
import com.nextlabs.authentication.services.UserAttributeProviderFactory;
import com.nextlabs.authentication.services.UserAttributeProviderService;

/**
 * Factory class to obtain the user attribute provider based on the authentication method.
 *
 * @author Sachindra Dasun
 */
@Service
public class UserAttributeProviderFactoryImpl implements UserAttributeProviderFactory {

    private Map<String, UserAttributeProviderService> userAttributeProviderServices;

    @Autowired
    private AuthHandlerRegistryRepository handlerRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Override
    public UserAttributeProviderService getUserAttributeProvider(String username) {
        Optional<ApplicationUser> applicationUserOptional = applicationUserRepository
                .findByUsernameIgnoreCaseAndStatus(username, UserStatus.ACTIVE);
        if (applicationUserOptional.isPresent()) {
            ApplicationUser applicationUser = applicationUserOptional.get();
            Optional<AuthHandlerRegistry> authHandlerRegistryOptional = handlerRepository
                    .findById(applicationUser.getAuthHandlerId());
            if (authHandlerRegistryOptional.isPresent()) {
                AuthHandlerRegistry authHandlerRegistry = authHandlerRegistryOptional.get();
                if (AuthType.LDAP.name().equals(authHandlerRegistry.getType())) {
                    return userAttributeProviderServices.get(AuthType.LDAP.name());
                } else if (AuthType.OIDC.name().equals(authHandlerRegistry.getType())) {
                    return userAttributeProviderServices.get(AuthType.OIDC.name());
                }
            }
        }
        return userAttributeProviderServices.get(AuthType.INTERNAL.name());
    }

    @Autowired
    private void setUserAttributeProviders(List<UserAttributeProviderService> userAttributeProviderServices) {
        this.userAttributeProviderServices = userAttributeProviderServices.stream()
                .collect(Collectors.toMap(UserAttributeProviderService::getAuthenticationMethod, Function.identity()));
    }

}
