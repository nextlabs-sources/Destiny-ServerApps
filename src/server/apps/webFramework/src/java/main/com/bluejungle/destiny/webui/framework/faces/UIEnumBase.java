/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import com.bluejungle.framework.patterns.EnumBase;

/**
 * This is the base class for a UI enumeration. A UI enumeration converts back
 * and forth between a UI value (an small int) and an internal enumeration
 * object. Also, it supports localized display value through a resource bundle.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/UIEnumBase.java#1 $
 */

public abstract class UIEnumBase extends EnumBase {

    private String bundleKeyName;

    /**
     * Constructor
     * 
     * @param enunName
     *            name associated with the enumeration
     * @param bundleKeyName
     *            name of the resource bundle key that should be used when
     *            figuring out the display value
     * @param type
     *            value used when referencing the enum
     */
    protected UIEnumBase(String enunName, String bundleKeyName, int type) {
        super(enunName, type);
        this.bundleKeyName = bundleKeyName;
    }

    /**
     * Returns the bundle key name associated with the enumeration
     * 
     * @return the bundle key name associated with the enumeration
     */
    public String getBundleKeyName() {
        return this.bundleKeyName;
    }

    /**
     * Returns the localized display value for the enum
     * 
     * @param enum
     *            enum object to use
     * @param bundleName
     *            name of the resource bundle
     * @return the display value of the enum based on the specified resource
     *         bundle
     */
    protected static String getDisplayValue(UIEnumBase enumeration, String bundleName) {
    	Locale currentLocale = null;
    	try { 
    		currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();	
    	} catch (Exception e) {
    		currentLocale = new Locale("en", "US");  // if any issue load the default locale en_US
    	}
        
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, currentLocale);
        return bundle.getString(enumeration.getBundleKeyName());
    }
}