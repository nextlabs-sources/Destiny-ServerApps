/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 11, 2015
 *
 */
package com.nextlabs.destiny.console.exceptions;

/**
 * Exception for NTLMv2 auth errors
 *
 * @author Mohammed Sainal Shah
 * @since 2021.03
 *
 */
public class NTLMV2AuthException extends RuntimeException {

    private static final long serialVersionUID = 4297538764411321088L;

    /**
     * Constructor
     *
     * @param message
     */
    public NTLMV2AuthException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message
     * @param cause
     */
    public NTLMV2AuthException(String message, Throwable cause) {
        super(message, cause);
    }

}
