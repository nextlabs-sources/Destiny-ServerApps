package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.CaCertsFolderValidator;

/**
 * Annotation for valid SSL certificates in cacerts folder.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CaCertsFolderValidator.class})
@Documented
public @interface ValidCaCertsFolder {

    String message() default "All SSL certificates in the cacerts folder must be valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
