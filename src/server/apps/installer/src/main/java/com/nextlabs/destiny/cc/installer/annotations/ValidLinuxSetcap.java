package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.LinuxSetcapValidator;

/**
 * Annotation to validate if setcap program is found which is used during the Linux installation.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {LinuxSetcapValidator.class})
@Documented
public @interface ValidLinuxSetcap {

    String message() default "The program \"setcap\" is required at \"/sbin/setcap\" or \"/usr/sbin/setcap\" to perform the installation";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
