/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 20, 2016
 *
 */
package com.nextlabs.destiny.console.config;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.nextlabs.destiny.console.annotations.ApiVersion;

/**
 * API Version based request mapping handler.
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ApiVersionRequestMappingHandlerMapping
        extends RequestMappingHandlerMapping {

    private final String prefix;

    public ApiVersionRequestMappingHandlerMapping(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method,
            Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method,
                handlerType);

        ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method,
                ApiVersion.class);

        if (methodAnnotation != null) {
            RequestCondition<?> methodCondition = getCustomMethodCondition(
                    method);
            // Concatenate our ApiVersion with the usual request mapping
            info = createApiVersionInfo(methodAnnotation, methodCondition)
                    .combine(info);
        } else {
            ApiVersion typeAnnotation = AnnotationUtils
                    .findAnnotation(handlerType, ApiVersion.class);

            if (typeAnnotation != null) {
                RequestCondition<?> typeCondition = getCustomTypeCondition(
                        handlerType);
                // Concatenate our ApiVersion with the usual request mapping
                if (info != null)
                    info = createApiVersionInfo(typeAnnotation, typeCondition)
                            .combine(info);
            }
        }

        return info;
    }

    private RequestMappingInfo createApiVersionInfo(ApiVersion annotation,
            RequestCondition<?> customCondition) {
        int[] values = annotation.value();
        String[] patterns = new String[values.length];

        if (values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                // Build the URL prefix
                patterns[i] = prefix + values[i];
            }

        } else {
            patterns = new String[] { prefix + '1' }; // default to v1
        }

        return new RequestMappingInfo(
                new PatternsRequestCondition(patterns, getUrlPathHelper(),
                        getPathMatcher(), useSuffixPatternMatch(),
                        useTrailingSlashMatch(), getFileExtensions()),
                new RequestMethodsRequestCondition(),
                new ParamsRequestCondition(), new HeadersRequestCondition(),
                new ConsumesRequestCondition(), new ProducesRequestCondition(),
                customCondition);
    }

}
