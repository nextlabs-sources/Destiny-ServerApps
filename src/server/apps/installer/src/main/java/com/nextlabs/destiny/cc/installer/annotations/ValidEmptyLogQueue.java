package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.EmptyLogQueueValidator;

/**
 * Annotation to validate if disk has enough space to perform installation.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EmptyLogQueueValidator.class})
@Documented
public @interface ValidEmptyLogQueue {

    String message() default "Existing \"PolicyServer/logqueue\" folder must be empty before upgrade";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
