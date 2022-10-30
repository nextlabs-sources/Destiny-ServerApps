/*
 * Created on May 3, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import com.bluejungle.destiny.appframework.CommonConstants;
import com.bluejungle.domain.policydecision.PolicyDecisionEnumType;

/**
 * The UI enforcement type enumeration allows going from an enforcement type to
 * an enforcement name that can be displayed and localized for the end user.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/UIEnforcementType.java#1 $
 */

public class UIEnforcementType extends UIEnumBase {

    public static final UIEnforcementType ENFORCEMENT_ALLOW_BUNDLE_KEY = new UIEnforcementType(PolicyDecisionEnumType.POLICY_DECISION_ALLOW.getName(), "policy_decision_allow", 0);
    public static final UIEnforcementType ENFORCEMENT_DENY_BUNDLE_KEY = new UIEnforcementType(PolicyDecisionEnumType.POLICY_DECISION_DENY.getName(), "policy_decision_deny", 2);
    public static final UIEnforcementType ENFORCEMENT_BOTH_BUNDLE_KEY = new UIEnforcementType("Both", "policy_decision_both", 4);

    /**
     * Constructor
     * 
     * @param enunName
     *            enumeration name
     * @param bundleKeyName
     *            key to use in the resource bundle for display value
     * @param type
     *            enumeration type
     */
    protected UIEnforcementType(String enunName, String bundleKeyName, int type) {
        super(enunName, bundleKeyName, type);
    }

    /**
     * Returns the localized display value
     * 
     * @param enumeration
     *            enum to localize
     * @return the localized display value
     */
    public static String getDisplayValue(UIEnumBase enumeration) {
        return getDisplayValue(enumeration, CommonConstants.COMMON_BUNDLE_NAME);
    }

    /**
     * Retrieve an UIEnforcementType instance by name
     * 
     * @param name
     *            the name of the UIEnforcementType
     * @return the UIEnforcementType associated with the provided name
     * @throws IllegalArgumentException
     *             if no UIEnforcementType exists with the specified name
     */
    public static UIEnforcementType getUIEnforcementType(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }
        return getElement(name, UIEnforcementType.class);
    }
}
