/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker;

import java.util.List;

/**
 * IItemListDisplaySpec describes how a list of selected items is displayed
 * within the browsable data picker view. FIX ME
 * 
 * @author sgoldstein
 */
public interface IItemListDisplaySpec {

    /**
     * Retrieve the list of column spec's describing the content of each column
     * in the list
     * 
     * @return a list of column spec instances describing the content of each
     *         column in the item list
     */
    public List getColumnsSpec();
}

