/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker;

/**
 * ISearchBucket represents an individual search bucket in the browsable data
 * picker view
 * 
 * @author sgoldstein
 */
public interface ISearchBucket {

    /**
     * Retrieve the display value assoicated with this search bucket
     * 
     * @return the display value assoicated with this search bucket
     */
    public String getDisplayValue();
}

