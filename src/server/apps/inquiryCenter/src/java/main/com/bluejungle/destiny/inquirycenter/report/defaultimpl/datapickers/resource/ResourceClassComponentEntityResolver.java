/*
 * Created on May 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.resource;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.ExpressionCutter;
import com.bluejungle.destiny.types.basic.v1.StringList;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/resource/ResourceClassComponentEntityResolver.java#1 $
 */

public class ResourceClassComponentEntityResolver {

    /**
     * Extracts the list of resource class from the existing selection
     * 
     * @param existingSelections
     *            user existing resource class selection
     * @return an array of String with each resource class / name in an array
     *         element
     */
    public static String[] getResourceClassListFrom(String existingSelections) {
        StringList resourceClassList = ExpressionCutter.convertToStringList(existingSelections);
        String[] resourceClassArray = null;
        if (resourceClassList != null) {
            resourceClassArray = resourceClassList.getValues();
            if (resourceClassArray != null) {
                for (int i = 0; i < resourceClassArray.length; i++) {
                    resourceClassArray[i] = resourceClassArray[i].trim();
                }
            }
        }
        return resourceClassArray;
    }
}