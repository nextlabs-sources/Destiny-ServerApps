/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 4 Aug 2016
 *
 */
package com.nextlabs.authentication.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for Authentication Providers configured in cc_datbase
 *
 * @author aishwarya
 * @since 8.0
 */
public class AuthTypeDetails {

    private Long id;
    private String accountId;
    private String type;
    private Map<String, String> configData;
    private Map<String, String> userAttrMap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the configData
     */
    public Map<String, String> getConfigData() {
        if(configData == null) {
            configData = new HashMap<>();
        }
        return configData;
    }

    /**
     * @param configData the configData to set
     */
    public void setConfigData(Map<String, String> configData) {
        this.configData = configData;
    }

    public Map<String, String> getUserAttrMap() {
        if(userAttrMap == null) {
            userAttrMap = new HashMap<>();
        }
        return userAttrMap;
    }

    public void setUserAttrMap(Map<String, String> userAttrMap) {
        this.userAttrMap = userAttrMap;
    }
}
