package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.InstallationPathValidator;

/**
 * Annotation for valid Control Center installation path.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {InstallationPathValidator.class})
@Documented
public @interface ValidInstallationPath {

    String message() default "Valid existing installation path is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
