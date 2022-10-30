package com.nextlabs.authentication.services;

import com.nextlabs.authentication.dto.OAuth2Token;

/**
 * OAuth token service interface.
 *
 * @author Sachindra Dasun
 */
public interface OAuth2TokenService {

    OAuth2Token getToken(String clientId, String clientSecret, long expiresIn);

}
