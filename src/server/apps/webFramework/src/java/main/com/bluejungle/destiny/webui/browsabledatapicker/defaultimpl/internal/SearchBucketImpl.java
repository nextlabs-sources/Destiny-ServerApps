/*
 * Created on May 7, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.bluejungle.destiny.appframework.CommonConstants;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketExtended;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec;

/**
 * Default implementation of the ISearchBucket interface
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/SearchBucketImpl.java#3 $
 */

public class SearchBucketImpl implements ISearchBucketExtended {
    private static final String SEARCH_BUCKET_NO_LAST_NAME_BUNDLE_KEY = "search_bucket_other";

	private static final String DISPLAY_FORMAT = "{0}-{1}"; //  Pull from
                                                            // Resource Bundle?
    private Character[] characterSequence;
    private String displayValue;
    
    public SearchBucketImpl(Character[] characterSequence, Locale searchBucketLocale) {
        if (characterSequence == null) {
            throw new NullPointerException("characterSequence cannot be null.");
        }

        this.characterSequence = characterSequence;
        
        if (characterSequence.length == 1 && Character.isWhitespace(characterSequence[0])) {
    		ResourceBundle resourceBundle =
					ResourceBundle.getBundle(CommonConstants.COMMON_BUNDLE_NAME,
							searchBucketLocale);
    		String noLastNameLabel = resourceBundle.getString(SEARCH_BUCKET_NO_LAST_NAME_BUNDLE_KEY);
			this.displayValue = noLastNameLabel;
		} else {
			Object[] messageArguments =
					{ characterSequence[0], characterSequence[characterSequence.length - 1] };
			this.displayValue = MessageFormat.format(DISPLAY_FORMAT, messageArguments);
		}
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISearchBucket#getSeachSpec()
     */
    public ISearchBucketSearchSpec getSeachSpec(int maximumResultsToDisplay) {
        return new SearchBucketSearchSpecImpl(maximumResultsToDisplay);
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.ISearchBucket#getDisplayValue()
     */
    public String getDisplayValue() {
        return this.displayValue;
    }

    private class SearchBucketSearchSpecImpl implements ISearchBucketSearchSpec {
        private int maximumResultsToDisplay = 0;
        
        /**
         * Create an instance of SearchBucketSearchSpecImpl
         * @param maximumResultsToDisplay
         */
        public SearchBucketSearchSpecImpl(int maximumResultsToDisplay) {
            this.maximumResultsToDisplay = maximumResultsToDisplay;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketSearchSpec#getCharactersInBucket()
         */
        public Character[] getCharactersInBucket() {
            return SearchBucketImpl.this.characterSequence;
        }

        /**
         * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSearchSpec#getMaximumResultsToReturn()
         */
        public int getMaximumResultsToReturn() {
            return this.maximumResultsToDisplay;
        }
        
    }
}