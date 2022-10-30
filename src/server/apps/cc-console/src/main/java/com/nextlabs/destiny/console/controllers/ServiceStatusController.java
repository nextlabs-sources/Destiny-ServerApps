package com.nextlabs.destiny.console.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to get service status.
 *
 * @author Sachindra Dasun
 */
@RestController
@RequestMapping("status")
public class ServiceStatusController {

    @GetMapping
    public ResponseEntity<String> status() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
