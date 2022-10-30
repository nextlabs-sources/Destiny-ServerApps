package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.ManagementServerValidator;

/**
 * Annotation for valid management server details.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ManagementServerValidator.class})
@Documented
public @interface ValidManagementServer {

    String message() default "The management server details cannot be empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
