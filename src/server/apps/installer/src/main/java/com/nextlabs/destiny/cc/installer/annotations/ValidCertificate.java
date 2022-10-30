package com.nextlabs.destiny.cc.installer.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nextlabs.destiny.cc.installer.validators.CertificateValidator;

/**
 * Annotation for a valid SSL certificate.
 *
 * @author Sachindra Dasun
 */
@Target({ElementType.TYPE, ElementType.TYPE_USE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CertificateValidator.class})
@Documented
public @interface ValidCertificate {

    String message() default "SSL certificate must be a valid X.509 certificate";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
