package com.nextlabs.authentication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author kyu & sduan
 * @since 8.0
 */
public class OAuth2Token {

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("access_token")
    private String accessToken;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("token_type")
    private String tokenType;

    @JsonInclude(Include.NON_DEFAULT)
    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonInclude(Include.NON_NULL)
    private String error;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
