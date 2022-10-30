package com.nextlabs.serverapps.common.framework.services;

import org.jose4j.jwt.JwtClaims;

/**
 * CSRF protection service create and return CSRF token.
 *
 * @author Mohammed Sainal Shah
 */
public interface JwtValidationService {

    String ID_TOKEN_HEADER = "Authorization";
    String BEARER_PREFIX = "Bearer ";

    JwtClaims validateJwt(String idToken);

}
