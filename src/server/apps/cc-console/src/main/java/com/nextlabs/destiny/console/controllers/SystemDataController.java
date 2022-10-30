/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 2, 2016
 *
 */
package com.nextlabs.destiny.console.controllers;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.enums.EnvironmentConfig;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;

/**
 *
 * Controller for system related data
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/system")
public class SystemDataController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(SystemDataController.class);

    @Autowired
    private ConfigurationDataLoader configDataLoader;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/version")
    public ConsoleResponseEntity<ResponseDTO> getVersionInfo() {

        String appVersion = configDataLoader.getApplicationVersion();
        String build = configDataLoader.getApplicationBuild();
        if (StringUtils.isNotEmpty(build)) {
            appVersion = String.format("%s-%s", appVersion, build);
        }
        log.debug("System version : {}", appVersion);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), appVersion);

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }
    
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/installMode")
    public ConsoleResponseEntity<ResponseDTO> getInstallMode() {

        EnvironmentConfig installMode = configDataLoader.getInstallMode();
        log.debug("Install mode : {}", installMode.name());

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), installMode.getLabel());

        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/csrfToken")
    public ConsoleResponseEntity<ResponseDTO> getCsrfToken() {
        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), "success");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @GetMapping(value = "status")
    public SimpleResponseDTO<String> status() {
        return SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.loaded.code"),
                msgBundle.getText("success.data.loaded"), "UP");
    }

    @Override
    protected Logger getLog() {
        return log;
    }

}
