/*
 * Created on Feb 28, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.webui.renderers;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.component.UIColumns;
import org.apache.myfaces.renderkit.JSFAttr;
import org.apache.myfaces.renderkit.RendererUtils;
import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.renderkit.html.HtmlRendererUtils;
import org.apache.myfaces.util.ArrayUtils;
import org.apache.myfaces.util.StringUtils;

import com.bluejungle.destiny.webui.controls.UIRow;
import com.bluejungle.destiny.webui.renderers.HtmlTableRenderer;


/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/nextlabs/destiny/webui/renderers/HtmlPopupTableRenderer.java#1 $
 */

public class HtmlPopupTableRenderer extends HtmlTableRenderer {

    private static final Log log = LogFactory.getLog(HtmlPopupTableRenderer.class.getName());
    
//    protected static final String ROW_ON_MOUSE_OVER_ATTR_NAME = "rowOnMouseOver";
//    protected static final String ROW_ON_MOUSE_OUT_ATTR_NAME = "rowOnMouseOut";
//    protected static final String ROW_ON_CLICK_ATTR_NAME = "rowOnClick";
//    protected static final String ROW_ID_VARIABLE_ATTR_NAME = "rowIdVariable";
    
    /**
     * @see com.bluejungle.destiny.webui.renderers.HtmlTableRenderer#renderRowStart(javax.faces.context.FacesContext, javax.faces.context.ResponseWriter, javax.faces.component.UIData, java.lang.String)
     */
    @Override
    protected void renderRowStart(FacesContext facesContext, ResponseWriter writer, UIData uiData, String rowStyleClass) throws IOException {
        //Figure out if there is a row tag mentioned in the table.
//        String rowOnMouseOver = (String)uiData.getAttributes().get(ROW_ON_MOUSE_OVER_ATTR_NAME);
//        String rowOnMouseOut = (String)uiData.getAttributes().get(ROW_ON_MOUSE_OUT_ATTR_NAME);
//        String rowOnClick = (String)uiData.getAttributes().get(ROW_ON_CLICK_ATTR_NAME);
//        String rowIdVariable = (String)uiData.getAttributes().get(ROW_ID_VARIABLE_ATTR_NAME);
        List children = uiData.getChildren();
        boolean rowFound = false;
        for (int j = 0, size = uiData.getChildCount(); j < size; j++) {
            UIComponent child = (UIComponent) children.get(j);
            if (child instanceof UIRow && ((UIRow) child).isRendered()) {
                writer.startElement(HTML.TR_ELEM, child);
                String styleClass = (String) child.getAttributes().get("styleClass");
                if (styleClass != null) {
                    writer.writeAttribute(HTML.CLASS_ATTR, styleClass, null);
                }
                rowFound = true;
                break;
            }
        }

        //If no row found, put the default row in there
        if (!rowFound) {            
            writer.startElement(HTML.TR_ELEM, uiData);
//            if (rowOnMouseOver != null){
//              writer.write(" onMouseOver=\"" + rowOnMouseOver + "\"");
//            }
//            if (rowOnMouseOut != null){
//              writer.write(" onMouseOut=\"" + rowOnMouseOut + "\"");
//            }
//            if (rowOnClick != null){
////              writer.write(" onMouseOut=\"" + rowOnMouseOut + "\"");
//                writer.writeAttribute(HTML.ONCLICK_ATTR, "window.open('" + rowOnClick + "?" + rowIdVariable + "=81', 'example', 'width=400,height=200,scrollbars=yes');", null);
//            }
            if (rowStyleClass != null)
            {
                writer.writeAttribute(HTML.CLASS_ATTR, rowStyleClass, null);
            }
        }
    }

    /**
     * @see com.bluejungle.destiny.webui.renderers.HtmlTableRenderer#afterRow(javax.faces.context.FacesContext, javax.faces.component.UIData)
     */
    @Override
    protected void afterRow(FacesContext facesContext, UIData dataTable) throws IOException {
        if (dataTable.getRowCount() != dataTable.getRowIndex() + 1) {
            int colspan = 0;
            for (Iterator it = dataTable.getChildren().iterator(); it.hasNext();) {
                UIComponent uiComponent = (UIComponent) it.next();
                if (uiComponent.isRendered()) {
                    if (uiComponent instanceof UIColumn) {
                        colspan++;
                    } else if (uiComponent instanceof UIColumns) {
                        UIColumns columns = (UIColumns) uiComponent;
                        colspan += columns.getRowCount();
                    }
                }
            }
        }
    }
    
//  -------------------------------------------------------------
    // Helper class Styles
    //-------------------------------------------------------------
    private static class Styles
    {
        //~ Instance fields
        // ------------------------------------------------------------------------

        private String[] _columnStyle;

        private String[] _rowStyle;

        //~ Constructors
        // ---------------------------------------------------------------------------
        Styles(String rowStyles, String columnStyles)
        {
            _rowStyle = (rowStyles == null) ? ArrayUtils.EMPTY_STRING_ARRAY : StringUtils.trim(StringUtils
                    .splitShortString(rowStyles, ','));
            _columnStyle = (columnStyles == null) ? ArrayUtils.EMPTY_STRING_ARRAY : StringUtils.trim(StringUtils
                    .splitShortString(columnStyles, ','));
        }

        public String getRowStyle(int idx)
        {
            if (!hasRowStyle())
            {
                return null;
            }
            return _rowStyle[idx % _rowStyle.length];
        }

        public String getColumnStyle(int idx)
        {
            if (!hasColumnStyle())
            {
                return null;
            }
            return _columnStyle[idx % _columnStyle.length];
        }

        public boolean hasRowStyle()
        {
            return _rowStyle.length > 0;
        }

        public boolean hasColumnStyle()
        {
            return _columnStyle.length > 0;
        }

    }
}
