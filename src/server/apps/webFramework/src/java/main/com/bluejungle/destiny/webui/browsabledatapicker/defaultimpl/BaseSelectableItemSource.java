/*
 * Created on May 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

import java.util.Locale;

/**
 * BaseSelectableItemSource is a base helper class for ISelectableItemSource
 * implementations
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/browsabledatapicker/defaultimpl/BaseSelectableItemSource.java#1 $
 */
public abstract class BaseSelectableItemSource implements ISelectableItemSource {

    public static final String LOCALE_LANGUAGE_SYSTEM_PROPERTY_SUFFIX = ".data.locale.language";
    public static final String LOCALE_COUNTRY_SYSTEM_PROPERTY_SUFFIX = ".data.locale.country";
    public static final String LOCALE_VARIANT_SYSTEM_PROPERTY_SUFFIX = ".data.locale.variant";

    public static final Locale DEFAULT_LOCALE = Locale.getDefault();

    private String returnAction;

    /**
     * Create an instance of BaseSelectableItemSource
     *  
     */
    protected BaseSelectableItemSource() {
        super();
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#getItemLocale()
     */
    public Locale getItemLocale() {
        Locale localeToReturn = null;

        String localeLanguage = System.getProperty(getItemName() + LOCALE_LANGUAGE_SYSTEM_PROPERTY_SUFFIX);
        if (localeLanguage != null) {
            String localeCountry = System.getProperty(getItemName() + LOCALE_LANGUAGE_SYSTEM_PROPERTY_SUFFIX);
            if (localeCountry == null) {
                localeCountry = "";
            }

            String localeVariant = System.getProperty(getItemName() + LOCALE_LANGUAGE_SYSTEM_PROPERTY_SUFFIX);
            if (localeVariant == null) {
                localeVariant = "";
            }

            localeToReturn = new Locale(localeLanguage, localeCountry, localeVariant);
        } else {
            localeToReturn = DEFAULT_LOCALE;
        }

        return localeToReturn;
    }

    /**
     * Default implementation of
     * {@see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#cancelDataSelection()}.
     * Merely returns the configured return action
     * 
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#cancelDataSelection()
     */
    public String cancelDataSelection() {
        return getReturnAction();
    }

    /**
     * Set the return navigation action to take once the data selection process
     * is complete
     * 
     * @param returnAction
     *            the return navigation action to take once the data selection
     *            process is complete
     */
    public void setReturnAction(String returnAction) {
        if (returnAction == null) {
            throw new NullPointerException("returnAction cannot be null.");
        }

        this.returnAction = returnAction;
    }

    /**
     * @see com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectableItemSource#reset()
     */
    public void reset() {
        //      At the moment, there's no caching so nothing to do. FIX ME
    }
    
    /**
     * Retrieve a descriptive name of the items to be selected (e.g. hosts).
     * Used to build a system property name which can configure the item locale
     * 
     * @return the name of the items to be selected
     */
    protected abstract String getItemName();

    /**
     * Retrieve the action to take once the available users browse page is
     * complete
     * 
     * @return the action to take once the available users browse page is
     *         complete
     */
    protected String getReturnAction() {
        return this.returnAction;
    }

}