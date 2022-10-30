/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

/**
 * ISearchBucketSpec represents a set of parameters for performing a selectable
 * item search for a particular search bucket. 
 * 
 * @author sgoldstein
 */
public interface ISearchBucketSearchSpec extends ISelectableItemSearchSpec {

    /**
     * Retrieve the sequence of characters associated with the linked search
     * bucket
     * 
     * @return the sequence of characters associated with the linked search
     *         bucket
     */
    public Character[] getCharactersInBucket();
}
