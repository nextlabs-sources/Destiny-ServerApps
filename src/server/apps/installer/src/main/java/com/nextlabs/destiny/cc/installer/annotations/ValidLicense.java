package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.LicenseValidator;

/**
 * Annotation for valid Control Center license.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {LicenseValidator.class})
@Documented
public @interface ValidLicense {

    String message() default "Valid license is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
