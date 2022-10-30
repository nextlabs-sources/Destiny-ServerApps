/*
 * Created on May 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report;

import com.bluejungle.framework.exceptions.BlueJungleException;

/**
 * ReportViewException is thrown when an error occurs within the report display model layer
 * 
 * @author sgoldstein
 */

public class ReportViewException extends BlueJungleException {
    private String message;
    
    /**
     * Create an instance of RolesExecption
     * 
     * @param cause
     */
    public ReportViewException(String message, Throwable cause) {
        super(cause);
        // FIX ME - Localize Message
        this.message = message;
    }

    /**
     * @see com.bluejungle.framework.exceptions.BlueJungleException#getMessage()
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @see com.bluejungle.framework.exceptions.BlueJungleException#getLocalizedMessage()
     */
    public String getLocalizedMessage() {
        return this.message;
    }
}