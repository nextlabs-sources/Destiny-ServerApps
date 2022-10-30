package com.nextlabs.destiny.configservice.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.configservice.dto.SysConfigValueDTO;
import com.nextlabs.destiny.configservice.services.ConfigService;
import com.nextlabs.destiny.configservice.services.MessageService;

/**
 * REST controller for configuration management.
 *
 * @author Sachindra Dasun
 */
@RestController
@RequestMapping("config")
public class ConfigController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConfigService configService;

    @GetMapping(value = "refresh")
    public ResponseEntity<String> refresh(@RequestParam("applications") Set<String> applications) {
        messageService.sendConfigRefresh(applications);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "update")
    public ResponseEntity<String> update(@RequestBody List<SysConfigValueDTO> sysConfigValueDTOS) {
        configService.update(sysConfigValueDTOS);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "reset")
    public ResponseEntity<String> reset(@RequestBody List<SysConfigValueDTO> sysConfigValueDTOS) {
        configService.reset(sysConfigValueDTOS);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
