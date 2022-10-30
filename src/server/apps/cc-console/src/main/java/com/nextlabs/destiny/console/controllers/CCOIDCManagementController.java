/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 29, 2016
 *
 */
package com.nextlabs.destiny.console.controllers;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.configuration.SysConfig;
import com.nextlabs.destiny.console.services.SysConfigService;
import com.nextlabs.serverapps.common.properties.CasOidcProperties;
import com.nextlabs.serverapps.common.properties.CCOIDCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for OIDC service management
 *
 * @author Mohammed Sainal Shah
 */
@RestController
@ApiVersion(1)
@RequestMapping("/oidc/mgmt")
public class CCOIDCManagementController extends AbstractRestController {

    private static final Logger log = LoggerFactory.getLogger(CCOIDCManagementController.class);
    private CasOidcProperties casOidcProperties;

    private SysConfigService sysConfigService;

    private static final String OIDC_CONFIG_KEY_FORMAT = "cc-oidc-config.custom-services[%s].";

    private static final String CLIENT_ID_KEY = "clientId";
    private static final String CLIENT_SECRET_KEY = "clientSecret";
    private static final String SERVICE_ID_KEY = "serviceId";
    private static final String ENCRYPT_ID_TOKEN_KEY = "encryptIdToken";
    private static final String APPLICATION_TYPE = "application";
    private static final ReversibleEncryptor REVERSIBLE_ENCRYPTOR = new ReversibleEncryptor();

    @Autowired
    public CCOIDCManagementController(SysConfigService sysConfigService, CasOidcProperties casOidcProperties) {
        this.sysConfigService = sysConfigService;
        this.casOidcProperties = casOidcProperties;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/addService")
    public ConsoleResponseEntity<ResponseDTO> addService(
            @RequestParam(name = "client_id") String clientId,
            @RequestParam(name = "client_secret") String clientSecret,
            @RequestParam(name = "service_url") String serviceId,
            @RequestParam(defaultValue = "true") boolean encryptIdToken) throws ConsoleException {

        log.debug("Request came to add OIDC client");
        CCOIDCService ccOidcService = casOidcProperties.getOidcService(clientId);
        if(ccOidcService != null){
            throw new ConsoleException(String.format("Client with ID %s already exists", clientId));
        }
        sysConfigService.saveAll(getSysConfigs(clientId, clientSecret, serviceId, encryptIdToken));
        sysConfigService.sendConfigRefreshRequest(Collections.singleton(APPLICATION_TYPE));

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"));

        log.info("Added new OIDC client and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }

    private List<SysConfig> getSysConfigs(String clientId, String clientSecret, String serviceId, boolean encryptIdToken) {
        Map<String, String> serviceMap = new HashMap<>();
        String encryptedSecret = String.format("{cipher}%s", REVERSIBLE_ENCRYPTOR.encrypt(clientSecret));
        serviceMap.put(CLIENT_ID_KEY, clientId);
        serviceMap.put(CLIENT_SECRET_KEY, encryptedSecret);
        serviceMap.put(SERVICE_ID_KEY, serviceId);
        serviceMap.put(ENCRYPT_ID_TOKEN_KEY, String.valueOf(encryptIdToken));
        // First 1000 ids are reserved for NextLabs applications.
        int serviceIndex = casOidcProperties.getCustomServices().size();

        List<SysConfig> sysConfigList = new ArrayList<>(3);
        serviceMap.forEach((key,value) -> {
            SysConfig sysConfig = new SysConfig();
            sysConfig.setApplication(APPLICATION_TYPE);
            sysConfig.setConfigKey(String.format(OIDC_CONFIG_KEY_FORMAT, serviceIndex) + key);
            sysConfig.setValue(value);
            sysConfig.setDefaultValue(value);
            sysConfig.setMainGroup("security");
            sysConfig.setSubGroup("cas");
            sysConfig.setMainGroupOrder(40);
            sysConfig.setSubGroupOrder(5);
            sysConfig.setConfigOrder(0);
            sysConfig.setHidden(true);
            sysConfig.setReadOnly(true);
            sysConfig.setAdvanced(true);
            sysConfig.setUi(false);
            sysConfig.setRestartRequired(true);
            sysConfig.setEncrypted(key.equals(CLIENT_SECRET_KEY));
            sysConfig.setDataType("text");
            sysConfig.setFieldType("text");
            sysConfig.setRequired(true);
            sysConfig.setDescription(key);
            sysConfigList.add(sysConfig);
        });
        return sysConfigList;
    }
}
