/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 11, 2015
 *
 */
package com.nextlabs.destiny.console.services;

/**
 *
 * Message bundle resolver Service interface
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface MessageBundleService {

    /**
     * Get message bundle value for the given key
     * 
     * @param msgKey
     * @return string message
     */
    String getText(String msgKey);

    /**
     * Get message bundle value for the given key + ".code"
     *
     * @param msgKey
     * @return string message
     */
    String getCode(String msgKey);

    /**
     * Get message bundle value with replaced place-holders according to given
     * values.
     * 
     * @param msgKey
     * @param values
     * @return string message
     */
    String getText(String msgKey, String... values);

}
