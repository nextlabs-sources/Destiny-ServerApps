/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 20, 2016
 *
 */
package com.nextlabs.destiny.console.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation for API version, This annotation can be used to annotate the
 * appropriate api version for request mapping
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {

    int[]value() default { 1 };

}
