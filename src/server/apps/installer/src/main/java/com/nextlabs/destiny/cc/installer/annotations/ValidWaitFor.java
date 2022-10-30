package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.WaitForValidator;

/**
 * Annotation for valid wait for URL.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {WaitForValidator.class})
@Documented
public @interface ValidWaitFor {

    String message() default "Wait for URL is not accessible";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
