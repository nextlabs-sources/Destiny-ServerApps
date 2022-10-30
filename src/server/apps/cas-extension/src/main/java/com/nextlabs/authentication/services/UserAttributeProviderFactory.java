package com.nextlabs.authentication.services;

import org.springframework.stereotype.Service;

/**
 * Service interface for the user attribute provider factory.
 *
 * @author Sachindra Dasun
 */
@Service
public interface UserAttributeProviderFactory {

    UserAttributeProviderService getUserAttributeProvider(String username);

}
