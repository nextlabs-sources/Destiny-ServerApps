/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 11, 2015
 *
 */
package com.nextlabs.destiny.console.config.root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * <p>
 * Message Properties bundle specific configurations - load message bundles,
 * cookie or header based local resolver
 * 
 * </p>
 * 
 * @author Amila Silva
 * @since 8.0
 */
@Configuration
public class MessageBundleResolveConfig {

    private static final Logger log = LoggerFactory
            .getLogger(MessageBundleResolveConfig.class);

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
         messageSource.setBasenames("classpath:i18n/systemcodes", "classpath:i18n/policymgmt");

        // If true, the key of the message will be displayed if the key is not
        // found, instead of throwing an exception
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setFallbackToSystemLocale(true);
        messageSource.setAlwaysUseMessageFormat(true);
        messageSource.setDefaultEncoding("UTF-8");

        // The value 0 means always reload the messages to be developer friendly
        messageSource.setCacheSeconds(5);

        log.info("Message bundles loaded successfully");
        return messageSource;
    }

}
