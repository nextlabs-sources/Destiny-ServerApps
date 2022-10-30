/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.services.tool;

import com.nextlabs.destiny.console.dto.enrollment.EnrollmentDTO;
import com.nextlabs.destiny.console.enums.EnrollmentType;

import javax.validation.ConstraintValidatorContext;

public interface EnrollmentValidationService {

    boolean isEnrollmentType(EnrollmentType enrollmentType);

    boolean isValid(EnrollmentDTO enrollmentDTO, ConstraintValidatorContext context);

}
