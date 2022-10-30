/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 11, 2015
 *
 */
package com.nextlabs.destiny.console.utils;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/**
 *
 * Smart Locale Resolver for message bundle loading according to cookie or
 * header locale settings
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class SmartLocaleResolver extends CookieLocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = request.getLocale();
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage == null || acceptLanguage.trim().isEmpty()) {
            locale = super.determineDefaultLocale(request);
        }
        LocaleContextHolder.setLocale(locale);
        return locale;
    }
}
