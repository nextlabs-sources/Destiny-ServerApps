package com.nextlabs.serverapps.common.properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Container to store OpenID Connect Properties.
 *
 * @author Mohammed Sainal Shah
 */

public class CasOidcProperties implements Serializable {

    @NestedConfigurationProperty
    private List<CCOIDCService> services = new ArrayList<>();

    @NestedConfigurationProperty
    private List<CCOIDCService> customServices = new ArrayList<>();

    // Encryption jwks can be accessed here. Signing jwks is set with cas.authn.oidc.jwksFile, and accessed by CAS directly
    private String encryptionJwksProperty;
    private String signingJwksProperty;

    private String encryptionJwks;
    private String signingJwks;
    private String encryptionAlgorithm;
    private String encryptionEncoding;
    private String oidcIssuer;
    private String signingAlgorithm;

    public List<CCOIDCService> getCustomServices() {
        return customServices;
    }

    public void setCustomServices(List<CCOIDCService> customServices) {
        this.customServices = customServices;
    }

    public List<CCOIDCService> getServices() {
        return Stream.concat(services.stream(), customServices.stream()).collect(Collectors.toList());
    }

    public void setServices(List<CCOIDCService> services) {
        this.services = services;
    }

    public String getEncryptionJwksProperty() {
        return encryptionJwksProperty;
    }

    public void setEncryptionJwksProperty(String encryptionJwksProperty) {
        this.encryptionJwksProperty = encryptionJwksProperty;
    }

    public String getEncryptionJwks() {
        return encryptionJwks;
    }

    public void setEncryptionJwks(String encryptionJwks) {
        this.encryptionJwks = encryptionJwks;
    }

    public String getSigningJwks() {
        return signingJwks;
    }

    public void setSigningJwks(String signingJwks) {
        this.signingJwks = signingJwks;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getEncryptionEncoding() {
        return encryptionEncoding;
    }

    public void setEncryptionEncoding(String encryptionEncoding) {
        this.encryptionEncoding = encryptionEncoding;
    }

    public String getSigningJwksProperty() {
        return signingJwksProperty;
    }

    public void setSigningJwksProperty(String signingJwksProperty) {
        this.signingJwksProperty = signingJwksProperty;
    }

    public String getOidcIssuer() {
        return oidcIssuer;
    }

    public void setOidcIssuer(String oidcIssuer) {
        this.oidcIssuer = oidcIssuer;
    }

    public String getSigningAlgorithm() {
        return signingAlgorithm;
    }

    public void setSigningAlgorithm(String signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm;
    }



    public CCOIDCService getOidcService(String clientId){
        if(StringUtils.isBlank(clientId)){
            return null;
        }
        Optional<CCOIDCService> oidcService = getServices()
                .stream()
                .filter(service -> service.getClientId().equals(clientId))
                .findFirst();
        return oidcService.orElse(null);
    }
}
