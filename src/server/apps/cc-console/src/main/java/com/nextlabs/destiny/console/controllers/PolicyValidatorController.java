package com.nextlabs.destiny.console.controllers;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.config.properties.PolicyValidatorProperties;

/**
 * REST controller for redirecting to policy validator.
 *
 * @author Sachindra Dasun
 */
@RestController
@RequestMapping("policy-validator")
public class PolicyValidatorController {

    @Autowired
    private PolicyValidatorProperties policyValidatorProperties;

    @GetMapping
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect(policyValidatorProperties.getUrl());
    }

}
