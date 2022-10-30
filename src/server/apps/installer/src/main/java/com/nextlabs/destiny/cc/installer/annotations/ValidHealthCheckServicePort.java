package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.HealthCheckServicePortValidator;

/**
 * Annotation for valid Control Center health check service port.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {HealthCheckServicePortValidator.class})
@Documented
public @interface ValidHealthCheckServicePort {

    String message() default "Health check service port must be open and all running Control Center services must be stopped";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
