package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.DiskSpaceValidator;

/**
 * Annotation to validate if disk has enough space to perform installation.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DiskSpaceValidator.class})
@Documented
public @interface ValidDiskSpace {

    String message() default "Control Center requires at least 4GB of free disk space";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
