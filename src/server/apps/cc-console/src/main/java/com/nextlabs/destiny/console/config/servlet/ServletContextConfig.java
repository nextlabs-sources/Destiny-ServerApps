/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Oct 30, 2015
 *
 */
package com.nextlabs.destiny.console.config.servlet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.nextlabs.destiny.console.utils.SmartLocaleResolver;

/**
 * <p>
 * Spring MVC config for the servlet context in the application.
 *
 * The beans of this context are only visible inside the servlet context.
 * </p>
 * 
 * @author Amila Silva
 * @since 8.0
 *
 */
@Configuration
public class ServletContextConfig {

    /**
     * <p>
     * URL based view resolver.
     * </p>
     *
     * @return {@link UrlBasedViewResolver}
     *
     */
    @Bean
    public UrlBasedViewResolver setupViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/");
        resolver.setSuffix(".html");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new SmartLocaleResolver();
    }
}
