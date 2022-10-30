/*
 * Created on Mar 16, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import com.bluejungle.destiny.appframework.i18n.IOptionItemResource;
import com.bluejungle.destiny.appframework.i18n.OptionItemResourceListFactory;

import javax.faces.model.SelectItem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * SelectItemResourceListFactory is a wrapper around
 * {@see com.bluejungle.destiny.appframework.i18n.OptionItemResourceListFactory}
 * which translation
 * {@see com.bluejungle.destiny.appframework.i18n.IOptionItemResource} lists to
 * {@see javax.faces.model.SelectItem} lists which can be used with the JSF
 * selectItems tag
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/SelectItemResourceListFactory.java#1 $
 */

public class SelectItemResourceListFactory {

    private static final Map CACHED_SELECT_ITEM_LISTS = new HashMap();

    private OptionItemResourceListFactory wrappedOptionItemResourceListFactory;

    /**
     * Create an instance of SelectItemResourceListFactory. Currently default
     * access because it's only created form common constants.
     * 
     * @param max_ui_element_list_size_options
     */
    SelectItemResourceListFactory(OptionItemResourceListFactory wrappedOptionListResourceItemFactory) {
        this.wrappedOptionItemResourceListFactory = wrappedOptionListResourceItemFactory;
    }

    // FIX ME - After 1.5 add template
    /**
     * Retrieve a list of SelectItem instances by locale
     */
    public List getSelectItemResources(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("locale cannot be null.");
        }

        List optionItemResourcesToReturn = null;
        if (CACHED_SELECT_ITEM_LISTS.containsKey(locale)) {
            optionItemResourcesToReturn = (List) CACHED_SELECT_ITEM_LISTS.get(locale);
        } else {
            optionItemResourcesToReturn = buildSelectItemResourceList(locale);
            CACHED_SELECT_ITEM_LISTS.put(locale, optionItemResourcesToReturn);
        }

        return optionItemResourcesToReturn;
    }

    private List buildSelectItemResourceList(Locale resourceBundleLocale) {
        if (resourceBundleLocale == null) {
            throw new NullPointerException("resourceBundleLocale cannot be null.");
        }

        List selectItemResourceListToReturn = new LinkedList();

        List optionItemResourceList = this.wrappedOptionItemResourceListFactory.getOptionItemResources(resourceBundleLocale);
        Iterator optionItemResourceIterator = optionItemResourceList.iterator();
        while (optionItemResourceIterator.hasNext()) {
            IOptionItemResource nextOptionItemResource = (IOptionItemResource) optionItemResourceIterator.next();
            SelectItem nextSelectItem = new SelectItem(nextOptionItemResource.getOptionValue(), nextOptionItemResource.getResource());
            selectItemResourceListToReturn.add(nextSelectItem);
        }

        return selectItemResourceListToReturn;
    }
}
