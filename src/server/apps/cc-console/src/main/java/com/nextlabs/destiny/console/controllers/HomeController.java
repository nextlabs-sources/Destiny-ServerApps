/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Oct 30, 2015
 *
 */
package com.nextlabs.destiny.console.controllers;

import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;

/**
 *
 * Console Project Home Controller
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@RequestMapping("/home")
public class HomeController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(HomeController.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ConsoleResponseEntity<ResponseDTO> homepage() {

        log.info("Current user : {}", getCurrentUser().getUsername());

        return ConsoleResponseEntity.get(
                ResponseDTO.create("OOO1",
                        String.format(
                                "Hello %s, Welcome to Control Center Console API",
                                getCurrentUser().getDisplayName())),
                HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }

}
