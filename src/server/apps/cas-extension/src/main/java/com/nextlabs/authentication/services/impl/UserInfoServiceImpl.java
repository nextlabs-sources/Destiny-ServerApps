package com.nextlabs.authentication.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.models.AppUserProperties;
import com.nextlabs.authentication.repositories.AppUserPropertiesRepository;
import com.nextlabs.authentication.repositories.ApplicationUserRepository;
import com.nextlabs.authentication.services.UserInfoService;

/**
 * User service implementation.
 *
 * @author Sachindra Dasun
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private AppUserPropertiesRepository appUserPropertiesRepository;

    @Override
    public String getUserAttributeValue(String username, String propKey) {
        return applicationUserRepository.findByUsernameIgnoreCaseAndStatus(username, UserStatus.ACTIVE)
                .map(applicationUser ->
                        appUserPropertiesRepository.findByUserIdAndPropKey(applicationUser.getId(), propKey)
                                .map(AppUserProperties::getPropValue)
                                .orElse(null)).orElse(null);
    }

}
