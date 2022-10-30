package com.nextlabs.authentication.services.impl;

import java.security.GeneralSecurityException;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nextlabs.authentication.dto.OAuth2Token;
import com.nextlabs.authentication.services.OAuth2TokenService;
import com.nextlabs.authentication.services.UserInfoService;

/**
 * OAuth token service implementation.
 *
 * @author Sachindra Dasun
 */
@Service
public class OAuth2TokenServiceImpl implements OAuth2TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2TokenServiceImpl.class);
    private static final long MAX_EXPIRES_IN = 3600L * 24 * 365;
    private static final String ISSUER = "nextlabs";
    private static final String REALM = "Bearer";
    private static final String USER_TOKEN_ATTRIBUTE_KEY = "jwt_passphrase";

    @Autowired
    @Qualifier("ccAuthenticationHandler")
    private AuthenticationHandler authenticationHandler;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public OAuth2Token getToken(String clientId, String clientSecret, long expiresIn) {
        expiresIn = Math.min(expiresIn, MAX_EXPIRES_IN);
        OAuth2Token token = new OAuth2Token();
        try {
            AuthenticationHandlerExecutionResult handlerResult = authenticationHandler
                    .authenticate(new UsernamePasswordCredential(clientId, clientSecret));
            if (handlerResult != null) {
                // username is used as Subject field of JWT token
                // Get user's associated JWT secret
                String jwtSecret = userInfoService.getUserAttributeValue(clientId, USER_TOKEN_ATTRIBUTE_KEY);
                if (jwtSecret == null) {
                    LOGGER.debug("No JWT token secret associated with the user: {}", clientId);
                    token.setError("unauthorized_client");
                } else {
                    String jwttoken = JWT.create()
                            .withIssuer(ISSUER)
                            .withSubject(clientId)
                            .withExpiresAt(new Date(System.currentTimeMillis() + expiresIn * 1000))
                            .sign(Algorithm.HMAC256(jwtSecret));
                    token.setAccessToken(jwttoken);
                    token.setTokenType(REALM);
                    token.setExpiresIn(expiresIn);
                }
            } else {
                token.setError("invalid_client");
            }
        } catch (GeneralSecurityException | PreventedException e) {
            LOGGER.error("Exception while authenticating user", e);
            token.setError("invalid_client");
        } catch (JWTCreationException e) {
            LOGGER.error("Exception while creating jwt token", e);
            token.setError("invalid_request");
        }
        return token;
    }

}
