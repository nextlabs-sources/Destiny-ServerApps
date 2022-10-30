/*
 * Created on Mar 22, 2010
 *
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.customapps;

import com.nextlabs.destiny.container.shared.customapps.mapping.PolicyApplicationJO;
import com.nextlabs.destiny.inquirycenter.customapps.mapping.CustomReportUIJO;

/**
 * @author hchan
 * @version $Id:
 */

public class CustomAppJO {
    private final long customAppId;
    private final PolicyApplicationJO policyApplicationJO;
    private final CustomReportUIJO customReportUIJO;


    CustomAppJO(long customAppId, PolicyApplicationJO policyApplicationJO,
            CustomReportUIJO customReportUIJO) {
        super();
        if (policyApplicationJO == null || customReportUIJO == null) {
            throw new IllegalArgumentException();
        }
        this.customAppId = customAppId;
        this.policyApplicationJO = policyApplicationJO;
        this.customReportUIJO = customReportUIJO;
    }


    public long getCustomAppId() {
        return customAppId;
    }

    public PolicyApplicationJO getPolicyApplicationJO() {
        return policyApplicationJO;
    }

    public CustomReportUIJO getCustomReportUIJO() {
        return customReportUIJO;
    }

    public String toString() {
        return policyApplicationJO.getName();
    }
}
