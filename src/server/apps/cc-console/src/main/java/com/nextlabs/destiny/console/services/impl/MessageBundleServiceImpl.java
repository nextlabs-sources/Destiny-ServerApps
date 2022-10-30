/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 11, 2015
 *
 */
package com.nextlabs.destiny.console.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.console.services.MessageBundleService;

/**
 *
 * Message bundle resolver Service interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class MessageBundleServiceImpl implements MessageBundleService {

    @Autowired
    private MessageSource messageSource;

    @Override
    public String getText(String msgKey) {
        return messageSource.getMessage(msgKey, null,
                LocaleContextHolder.getLocale());
    }

    /**
     * Return the value found for message key + ".code" in translation file.
     *
     * @param msgKey message key
     * @return the value of the message key code
     */
    @Override
    public String getCode(String msgKey) {
        return messageSource.getMessage(String.format("%s.code", msgKey), null,
                LocaleContextHolder.getLocale());
    }

    @Override
    public String getText(String msgKey, String... values) {
        return messageSource.getMessage(msgKey, values,
                LocaleContextHolder.getLocale());
    }

}
