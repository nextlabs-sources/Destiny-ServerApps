package com.nextlabs.serverapps.common.properties;

import java.io.Serializable;

/**
 * Container to store OpenID Connect Properties.
 *
 * @author Mohammed Sainal Shah
 */
public class CCOIDCService implements Serializable {

    private boolean bypassApprovalPrompt;
    private String clientId;
    private String clientSecret;
    private boolean encryptIdToken;
    private String serviceId;

    public boolean isBypassApprovalPrompt() {
        return bypassApprovalPrompt;
    }

    public void setBypassApprovalPrompt(boolean bypassApprovalPrompt) {
        this.bypassApprovalPrompt = bypassApprovalPrompt;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public boolean isEncryptIdToken() {
        return encryptIdToken;
    }

    public void setEncryptIdToken(boolean encryptIdToken) {
        this.encryptIdToken = encryptIdToken;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

}
