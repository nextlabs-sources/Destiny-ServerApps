/*
 * Created on Mar 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import javax.faces.context.FacesContext;

/**
 * This is a utility class to retrieve the current application context.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/ContextUtil.java#1 $
 */

public class ContextUtil {

    /**
     * Returns the path of the current application context. For example, if URL
     * to application is http://myHost/MyApp1, this function returns /MyApp1.
     * 
     * @param context
     *            current JSF context
     * @return the path of the current application context
     */
    public static String getContextPath(final FacesContext context) {
        return (context.getExternalContext().getRequestContextPath());
    }

    /**
     * This function returns the URL of a resource located inside a certain
     * application context. For example, in an application located at
     * http://myHost/MyApp1, the resource /image/img1.gif becomes
     * /MyApp1/image/img1.gif
     * 
     * @param context
     * @param relativeURL
     * @return
     */
    public static String getFullContextLocation(final FacesContext context, final String relativeURL) {
        String path = getContextPath(context);
        String realRelativeURL = relativeURL;
        if (!realRelativeURL.startsWith("/")) {
            realRelativeURL = "/" + realRelativeURL;
        }
        return path + realRelativeURL;
    }
}