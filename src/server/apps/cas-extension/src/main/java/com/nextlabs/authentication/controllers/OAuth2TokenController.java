package com.nextlabs.authentication.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.authentication.dto.OAuth2Token;
import com.nextlabs.authentication.services.OAuth2TokenService;

/**
 * @author kyu & sduan
 * @since 8.0
 */
@RestController
@RequestMapping("/token")
public class OAuth2TokenController {

    @Autowired
    private OAuth2TokenService oAuth2TokenService;

    @PostMapping(params = {"grant_type=client_credentials"})
    public OAuth2Token getClientCredentialToken(
            @RequestParam(name = "client_id") String clientId,
            @RequestParam(name = "client_secret") String clientSecret,
            @RequestParam(name = "expires_in", defaultValue = "3600") long expiresIn) {
        return oAuth2TokenService.getToken(clientId, clientSecret, expiresIn);
    }

    @PostMapping(params = {"grant_type!=client_credentials"})
    public OAuth2Token getOtherToken() {
        OAuth2Token token = new OAuth2Token();
        token.setError("unsupported_grant_type");
        return token;
    }

}
