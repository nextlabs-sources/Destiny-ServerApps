/*
 * Created on Mar 13, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.component.UserRoleUtils;
import org.apache.myfaces.component.html.ext.HtmlDataTable;
import org.apache.myfaces.custom.sortheader.HtmlCommandSortHeader;
import org.apache.myfaces.custom.sortheader.HtmlSortHeaderRenderer;

/**
 * This renderer class renders a sorted column header. If the column is sorted,
 * it renders the appropriate image next to the HTML link.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/HtmlSortColumnHeaderRenderer.java#2 $
 */

public class HtmlSortColumnHeaderRenderer extends HtmlSortHeaderRenderer {

    private static final Log LOG = LogFactory.getLog(HtmlSortColumnHeaderRenderer.class.getName());
    protected static final String SORT_DOWN_CLASS_NAME_ATTR = "sortDownClassName";
    protected static final String SORT_UP_CLASS_NAME_ATTR = "sortUpClassName";

    /**
     * @see org.apache.myfaces.renderkit.html.HtmlLinkRendererBase#getStyleClass(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    protected String getStyleClass(FacesContext facesContext, UIComponent component) {
        String result = null;
        if (UserRoleUtils.isEnabledOnUserRole(component)) {
            HtmlCommandSortHeader sortHeader = (HtmlCommandSortHeader) component;
            HtmlDataTable dataTable = sortHeader.findParentDataTable();

            if (dataTable != null && sortHeader.isArrow() && sortHeader.getColumnName().equals(dataTable.getSortColumn())) {
                ResponseWriter writer = facesContext.getResponseWriter();
                if (dataTable.isSortAscending()) {
                    result = (String) component.getAttributes().get(SORT_UP_CLASS_NAME_ATTR);
                } else {
                    result = (String) component.getAttributes().get(SORT_DOWN_CLASS_NAME_ATTR);
                }
            }
        }
        return result;
    }

    /**
     * Overrides the parent renderer method. After the link, nothing has to be
     * displayed in this implementation.
     * 
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
    }
}