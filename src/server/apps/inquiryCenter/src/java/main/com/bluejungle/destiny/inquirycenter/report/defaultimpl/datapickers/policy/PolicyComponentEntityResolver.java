/*
 * Created on May 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers.policy;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.ExpressionCutter;
import com.bluejungle.destiny.types.basic.v1.StringList;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/policy/PolicyComponentEntityResolver.java#1 $
 */

public class PolicyComponentEntityResolver {

    public static String[] getPolicyListFrom(String policyFieldExpression) {
        StringList policyList = ExpressionCutter.convertToStringList(policyFieldExpression);
        String[] policyArray = policyList.getValues();
        if (policyArray != null) {
            for (int i = 0; i < policyArray.length; i++) {
                policyArray[i] = policyArray[i].trim();
            }
        }
        return policyArray;
    }
}