package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.CcServicesStoppedValidator;

/**
 * Annotation to test if Control Center services are stopped.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CcServicesStoppedValidator.class})
@Documented
public @interface CcServicesStopped {

    String message() default "All running Control Center services must be stopped";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
