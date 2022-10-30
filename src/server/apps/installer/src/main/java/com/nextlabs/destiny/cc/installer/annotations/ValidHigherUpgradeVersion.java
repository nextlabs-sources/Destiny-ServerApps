package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.HigherUpgradeVersionValidator;

/**
 * Annotation to validate if the upgrade version is higher than the existing version.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {HigherUpgradeVersionValidator.class})
@Documented
public @interface ValidHigherUpgradeVersion {

    String message() default "Control Center upgrade version must be higher than the existing version";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
