package com.nextlabs.destiny.console.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.console.validators.EnrollmentValidator;

/**
 * Annotation for valid enrollment.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnrollmentValidator.class})
@Documented
public @interface ValidEnrollment {

    String message() default "Invalid enrollment";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

