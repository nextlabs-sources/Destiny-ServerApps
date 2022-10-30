/*
 * Created on May 6, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl;

/**
 * IFreeFormSearchSpec is a search specification describing a free form search
 * within the browsable data picker view
 * 
 * @author sgoldstein
 */
public interface IFreeFormSearchSpec extends ISelectableItemSearchSpec {

    /**
     * Retrieve the search string entered in the free form search box of the
     * browsable data picker view
     * 
     * @return the search string entered in the free form search box of the
     *         browsable data picker view
     */
    public String getFreeFormSeachString();
}

