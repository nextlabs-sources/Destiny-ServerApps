/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.services.tool.impl;

import java.util.List;
import java.util.Optional;

import com.nextlabs.destiny.console.services.tool.EnrollmentValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.console.enums.EnrollmentType;

@Component
public class EnrollmentValidationServiceFactory {

    List<EnrollmentValidationService> validationServices;

    public Optional<EnrollmentValidationService> getValidationService(EnrollmentType enrollmentType) {
        return validationServices.stream().filter(service -> service.isEnrollmentType(enrollmentType))
                .findFirst();
    }

    @Autowired
    public void setValidationServices(List<EnrollmentValidationService> validationServices) {
        this.validationServices = validationServices;
    }
}
