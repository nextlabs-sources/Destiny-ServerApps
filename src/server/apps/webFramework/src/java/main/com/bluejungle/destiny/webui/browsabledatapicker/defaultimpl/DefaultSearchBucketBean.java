/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.appframework.CommonConstants;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.internal.SearchBucketImpl;

/**
 * Default implementation of the ISearchBucketBean interface. This
 * implementation reads the list of search buckets for a given locale from the
 * common resource bundle. For an example of how to declare the search buckets
 * in the bundle, please see the default bundle for an example
 * 
 * @author sgoldstein
 */
public class DefaultSearchBucketBean implements ISearchBucketBean {

    private static final Log LOG = LogFactory.getLog(DefaultSearchBucketBean.class.getName());

    private static final Map<Locale, ISearchBucketExtended[]> LOCALE_TO_SEARCH_BUCKETS_MAP =
			new HashMap<Locale, ISearchBucketExtended[]>();    
    private static final String BUNDLE_SEARCH_BUCKET_CHAR_KEY_PREFIX = "search_bucket";
    private static final String BUNDLE_SEARCH_BUCKET_CHAR_KEY_SPLIT_REGEXP = "\\.";

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISearchBucketBean#getSearchBuckets(Locale)
     */
    public ISearchBucketExtended[] getSearchBuckets(Locale searchBucketLocale) {
        if (searchBucketLocale == null) {
            throw new NullPointerException("searchBucketLocale cannot be null.");
        }

        if (!LOCALE_TO_SEARCH_BUCKETS_MAP.containsKey(searchBucketLocale)) {
            loadSearchBuckets(searchBucketLocale);
        }

        return LOCALE_TO_SEARCH_BUCKETS_MAP.get(searchBucketLocale);
    }

    /**
     * Load the search buckets for the provided locale
     * 
     * @param searchBucketLocale
     */
    private void loadSearchBuckets(Locale searchBucketLocale) {
		SortedMap<Integer, SortedMap<Integer, Character>> sortedSearchBucketData =
				loadSearchBucketBundleData(searchBucketLocale);
		ISearchBucketExtended[] searchBuckets = parseSearchBucketData(sortedSearchBucketData);
		LOCALE_TO_SEARCH_BUCKETS_MAP.put(searchBucketLocale, searchBuckets);
	}

    /**
     * Load and parse the search bucket definitions from the common resource
     * bundle
     * 
     * @param searchBucketLocale
     *            the locale for which to load the data
     * @return the parsed data in a SortedMap. The keys are the indexes of the
     *         search buckets. Each value is another SortedMap containing a
     *         mapping from characer index to character for each character in a
     *         particular search bucket.
     */
    private SortedMap<Integer, SortedMap<Integer, Character>> loadSearchBucketBundleData(
			Locale searchBucketLocale) {
		SortedMap<Integer, SortedMap<Integer, Character>> sortedSearchBucketData =
				new TreeMap<Integer, SortedMap<Integer, Character>>();

		ResourceBundle resourceBundle =
				ResourceBundle.getBundle(CommonConstants.COMMON_BUNDLE_NAME, searchBucketLocale);
		Enumeration<String> keys = resourceBundle.getKeys();
		final String prefix = BUNDLE_SEARCH_BUCKET_CHAR_KEY_PREFIX + ".";
		while (keys.hasMoreElements()) {
			String nextKey = keys.nextElement();
			if (nextKey.startsWith(prefix)) {
				String keyStr = resourceBundle.getString(nextKey).trim();
				char nextCharacter;
				if(keyStr.length() > 0){
					nextCharacter = keyStr.charAt(0);
				}else{
					nextCharacter = ISearchBucketBean.NO_VALUE;
				}

				addSearchBucketCharacter(sortedSearchBucketData, nextKey, nextCharacter);
			}
		}
		return sortedSearchBucketData;
	}

    /**
     * Add the specified character to the search bucket definition data
     * 
     * @param sortedSearchBucketData
     *            the data
     * @param nextKey
     *            the next resource bundle key which specified a search bucket
     *            charater
     * @param nextCharacter
     *            the associated search bucket character
     */
    private void addSearchBucketCharacter(
			SortedMap<Integer, SortedMap<Integer, Character>> sortedSearchBucketData,
			String nextKey, char nextCharacter) {
        String[] nextKeyParts = nextKey.split(BUNDLE_SEARCH_BUCKET_CHAR_KEY_SPLIT_REGEXP);

        if (nextKeyParts.length != 3) {
            logInvalidKey(nextKey);
        } else {
            Integer nextKeySearchBucketIndex = Integer.valueOf(nextKeyParts[1]);
            Integer nextKeyCharacterIndex = Integer.valueOf(nextKeyParts[2]);

            SortedMap<Integer, Character> sortedBucketCharacterMap =
					sortedSearchBucketData.get(nextKeySearchBucketIndex);
			if (sortedBucketCharacterMap == null) {
				sortedBucketCharacterMap = new TreeMap<Integer, Character>();
				sortedSearchBucketData.put(nextKeySearchBucketIndex, sortedBucketCharacterMap);
			}
			sortedBucketCharacterMap.put(nextKeyCharacterIndex, new Character(nextCharacter));
        }
    }

    /**
     * Log a message if an invalid key was found
     * 
     * @param nextKey
     *            the invalid key
     */
    private void logInvalidKey(String nextKey) {
        StringBuffer warningMessage = new StringBuffer("Invalid search bucket key found, ");
        warningMessage.append(nextKey);
        warningMessage.append(".  Key/Value must be in the format, ");
        warningMessage.append(BUNDLE_SEARCH_BUCKET_CHAR_KEY_PREFIX);
        warningMessage.append(BUNDLE_SEARCH_BUCKET_CHAR_KEY_SPLIT_REGEXP);
        warningMessage.append("{search bucket index}");
        warningMessage.append(BUNDLE_SEARCH_BUCKET_CHAR_KEY_SPLIT_REGEXP);
        warningMessage.append("{character index in search bucket} = {character}");

        getLog().warn(warningMessage.toString());
    }

    /**
     * Parse the search bucket data read from the resource bundle and build the
     * search bucket list
     * 
     * @param sortedSearchBucketData
     *            the search bucket data read from the resource bundle
     * @return the generated search bucket list
     */
    private ISearchBucketExtended[] parseSearchBucketData(
			SortedMap<Integer, SortedMap<Integer, Character>> sortedSearchBucketData) {
		ISearchBucketExtended[] searchBuckets =
				new ISearchBucketExtended[sortedSearchBucketData.size()];
		Iterator<SortedMap<Integer, Character>> searchBucketDataIterator =
				sortedSearchBucketData.values().iterator();
		for (int i = 0; searchBucketDataIterator.hasNext(); i++) {
			SortedMap<Integer, Character> nextSearchBucketDataItem =
					searchBucketDataIterator.next();
			searchBuckets[i] = buildSearchBucket(nextSearchBucketDataItem);
		}
		return searchBuckets;
	}

    /**
     * Build a search bucket with the specified search bucket data
     * 
     * @param nextSearchBucketDataItem
     *            the data for a search bucket
     * @return the generated search bucket
     */
    private ISearchBucketExtended buildSearchBucket(
			SortedMap<Integer, Character> nextSearchBucketDataItem) {
		Character[] nextSearchBucketCharacters = new Character[nextSearchBucketDataItem.size()];
		Iterator<Character> nextSearchBucketCharacterIterator =
				nextSearchBucketDataItem.values().iterator();
		for (int j = 0; nextSearchBucketCharacterIterator.hasNext(); j++) {
			Character nextCharacter = nextSearchBucketCharacterIterator.next();
			nextSearchBucketCharacters[j] = nextCharacter;
		}
		Locale currentLocale = Locale.getDefault();
		return new SearchBucketImpl(nextSearchBucketCharacters, currentLocale);
	}

    /**
     * Retrieve a reference to a logger
     * 
     * @return a reference to a logger
     */
    private Log getLog() {
        return LOG;
    }
}

