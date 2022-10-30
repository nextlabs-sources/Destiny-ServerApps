/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers.helpers.link;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/helpers/link/ILinkBuilder.java#1 $
 */

public interface ILinkBuilder {
    public void setRequestParameters(Map parameters);
    public void setDisabled(boolean disabled);
    public void encodeLinkStart(FacesContext context, UIComponent component, boolean renderForm) throws IOException;
    public void encodeLinkEnd(FacesContext context, UIComponent component) throws IOException;
}
