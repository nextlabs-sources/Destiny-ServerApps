package com.nextlabs.destiny.configservice.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.configservice.services.LoggerConfigService;
import com.nextlabs.destiny.configservice.services.MessageService;

/**
 * REST controller for logger configuration management.
 *
 * @author Sachindra Dasun
 */
@RestController
@RequestMapping("logger-config")
public class LoggerConfigController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private LoggerConfigService loggerConfigService;

    @GetMapping(value = "get")
    public ResponseEntity<List<String>> get() {
        List<String> loggerConfigs = loggerConfigService.get();
        return new ResponseEntity<>(loggerConfigs, HttpStatus.OK);
    }

    @GetMapping(value = "refresh")
    public ResponseEntity<String> refresh(@RequestParam(name = "applications", required = false) Set<String> applications) {
        messageService.sendLoggerRefresh(applications);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
