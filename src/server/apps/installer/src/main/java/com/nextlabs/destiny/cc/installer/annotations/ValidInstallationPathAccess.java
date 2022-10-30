package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.InstallationPathAccessValidator;

/**
 * Annotation for valid Control Center installation path access.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {InstallationPathAccessValidator.class})
@Documented
public @interface ValidInstallationPathAccess {

    String message() default "The user must have read/write access to the specified installation directory";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
