/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import java.util.Locale;

/**
 * The Search Bucket Bean is responsible for providing the list of search
 * buckets to use for the browsable data picker view.
 * 
 * @author sgoldstein
 */
public interface ISearchBucketBean {
	char NO_VALUE = ' ';

    /**
     * Retrieve an array of search buckets for the specified locale.
     * 
     * Note that an array is chosen here for quick indexed access by the
     * IBrowsableDataPickerBean in addition to the data set's static nature
     * 
     * @param searchBucketLocale
     *            the Locale associated with the Search Buckets to retrieve
     * @return an array of search buckets for the given locale
     */
    public ISearchBucketExtended[] getSearchBuckets(Locale searchBucketLocale);
}

