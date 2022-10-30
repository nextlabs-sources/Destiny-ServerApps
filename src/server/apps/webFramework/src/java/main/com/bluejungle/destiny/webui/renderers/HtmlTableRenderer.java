/*
 * Created on Mar 12, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.component.UIColumns;
import org.apache.myfaces.renderkit.JSFAttr;
import org.apache.myfaces.renderkit.RendererUtils;
import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.renderkit.html.HtmlRendererUtils;
import org.apache.myfaces.renderkit.html.HtmlTableRendererBase;

import com.bluejungle.destiny.webui.controls.UIRow;

/**
 * This is the table renderer class. This class renders HTML for the table so
 * that all the customization can easily be done through CSS. It generates HTML
 * that is simpler than the regular implementation.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/HtmlTableRenderer.java#2 $
 */

public class HtmlTableRenderer extends HtmlTableRendererBase {

    private static final Log LOG = LogFactory.getLog(HtmlTableRenderer.class.getName());

    protected static final String TABLE_TITLE_CLASS = "tabletitle";

    /**
     * Name of the attribute to look for to see if rows should be striped
     */
    protected static final String TABLE_STRIPE_ROW_ATTR = "stripeRows";

    /**
     * The message to display within the table if the data is empty
     */
    private static final String EMPTY_TABLE_MESSAGE_ATTR_NAME = "emptyTableMessage";
    private static final String DEFAULT_EMPTY_MESSAGE = "- No Records Found -";
    private static final Object DEFAULT_EMPTY_MESSAGE_CELL_STYLE_CLASS = "emptymessagetablerow";

    /**
     * @see org.apache.myfaces.renderkit.html.HtmlTableRendererBase#beforeTable(javax.faces.context.FacesContext,
     *      javax.faces.component.UIData)
     */
    protected void beforeTable(FacesContext facesContext, UIData uiData) throws IOException {
        Object needStripe = uiData.getAttributes().get(TABLE_STRIPE_ROW_ATTR);
        boolean showStripe = false;
        if (needStripe != null) {
            if (needStripe instanceof Boolean) {
                showStripe = ((Boolean) needStripe).booleanValue();
            } else {
                getLog().warn("The '" + TABLE_STRIPE_ROW_ATTR + "' table attribute is not a boolean type");
            }
        }
        if (showStripe) {
            ResponseWriter writer = facesContext.getResponseWriter();
            JSUtil.generateStripeCode(facesContext, uiData, writer, uiData.getClientId(facesContext));
        }
    }

    /**
     * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
        if (!(component instanceof UIData)) {
            throw new IllegalStateException("Unknown component, " + component.getClass().getName());
        }

        int rowCount = ((UIData) component).getRowCount();
        if (rowCount <= 0) {
            renderEmptyMessage(facesContext, component);
        } else {
            super.encodeChildren(facesContext, component);
        }
    }

    /**
     * Render the empty message if it exists
     * 
     * @param facesContext
     * @param component
     * @throws IOException
     */
    protected void renderEmptyMessage(FacesContext facesContext, UIComponent component) throws IOException {
        int colspan = 0;
        for (Iterator it = component.getChildren().iterator(); it.hasNext();) {
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
        
        String emptyMessage = (String) component.getAttributes().get(EMPTY_TABLE_MESSAGE_ATTR_NAME);
        if (emptyMessage == null) {
            emptyMessage = DEFAULT_EMPTY_MESSAGE;
        }

        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement(HTML.TR_ELEM, component);
        writer.writeAttribute(HTML.CLASS_ATTR, DEFAULT_EMPTY_MESSAGE_CELL_STYLE_CLASS, null);
        writer.startElement(HTML.TD_ELEM, component);
        writer.writeAttribute(HTML.COLSPAN_ATTR, new Integer(colspan), null);
        writer.write(emptyMessage);
        writer.endElement(HTML.TD_ELEM);
        writer.endElement(HTML.TR_ELEM);

    }

    /**
     * Returns the log object
     * 
     * @return the log object
     */
    protected Log getLog() {
        return LOG;
    }

    /**
     * @see org.apache.myfaces.renderkit.html.HtmlTableRendererBase#renderRowStart(javax.faces.context.FacesContext,
     *      javax.faces.context.ResponseWriter, javax.faces.component.UIData,
     *      java.lang.String)
     */
    protected void renderRowStart(FacesContext facesContext, ResponseWriter writer, UIData uiData, String rowStyleClass) throws IOException {

        //Figure out if there is a row tag mentioned in the table.
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
            super.renderRowStart(facesContext, writer, uiData, rowStyleClass);
        }
    }

    /**
     * In this implementation, the table header becomes a <DIV>that spans for
     * the whole width of the table. Hence, some parameters in this function are
     * simply ignored.
     * 
     * @see org.apache.myfaces.renderkit.html.HtmlTableRendererBase#renderTableHeaderRow(javax.faces.context.FacesContext,
     *      javax.faces.context.ResponseWriter,
     *      javax.faces.component.UIComponent,
     *      javax.faces.component.UIComponent, java.lang.String, int)
     */
    protected void renderTableHeaderRow(FacesContext facesContext, ResponseWriter writer, UIComponent component, UIComponent headerFacet, String headerStyleClass, int colspan) throws IOException {
        writer.startElement(HTML.TR_ELEM, component);
        writer.startElement(HTML.TH_ELEM, component);
        if (headerFacet != null) {
            RendererUtils.renderChild(facesContext, headerFacet);
        }
        writer.endElement(HTML.TH_ELEM);
        writer.endElement(HTML.TR_ELEM);
    }

    /**
     * Renders a simplified version of the table (no table body, and no special
     * header/footer block).
     * 
     * @see org.apache.myfaces.renderkit.html.HtmlTableRendererBase#renderFacet(javax.faces.context.FacesContext,
     *      javax.faces.context.ResponseWriter,
     *      javax.faces.component.UIComponent, boolean)
     */
    protected void renderFacet(FacesContext facesContext, ResponseWriter writer, UIComponent component, boolean header) throws IOException {
        int colspan = 0;
        boolean hasColumnFacet = false;
        for (Iterator it = component.getChildren().iterator(); it.hasNext();) {
            UIComponent uiComponent = (UIComponent) it.next();
            if (uiComponent.isRendered()) {
                if (uiComponent instanceof UIColumn) {
                    colspan++;
                    if (!hasColumnFacet) {
                        hasColumnFacet = header ? ((UIColumn) uiComponent).getHeader() != null : ((UIColumn) uiComponent).getFooter() != null;
                    }
                } else if (uiComponent instanceof UIColumns) {
                    UIColumns columns = (UIColumns) uiComponent;
                    colspan += columns.getRowCount();
                    if (!hasColumnFacet) {
                        hasColumnFacet = header ? columns.getHeader() != null : columns.getFooter() != null;
                    }
                }
            }
        }

        UIComponent facet = header ? (UIComponent) component.getFacets().get(HEADER_FACET_NAME) : (UIComponent) component.getFacets().get(FOOTER_FACET_NAME);
        if (facet != null || hasColumnFacet) {
            // Header or Footer present

            HtmlRendererUtils.writePrettyLineSeparator(facesContext);
            if (header) {
                String headerStyleClass = getHeaderClass(component);
                if (facet != null) {
                    renderTableHeaderRow(facesContext, writer, component, facet, headerStyleClass, colspan);
                }
                if (hasColumnFacet) {
                    renderColumnHeaderRow(facesContext, writer, component, headerStyleClass);
                }
            } else {
                String footerStyleClass = getFooterClass(component);
                if (hasColumnFacet) {
                    UIData dataComp = (UIData) component;
                    dataComp.setRowIndex(-1);
                    renderColumnFooterRow(facesContext, writer, component, footerStyleClass);
                }
                if (facet != null) {
                    renderTableFooterRow(facesContext, writer, component, facet, footerStyleClass, colspan);
                }
            }
        }
    }

    /**
     * @see org.apache.myfaces.renderkit.html.HtmlTableRendererBase#renderColumnFooterCell(javax.faces.context.FacesContext,
     *      javax.faces.context.ResponseWriter,
     *      javax.faces.component.UIComponent,
     *      javax.faces.component.UIComponent, java.lang.String, int)
     */
    protected void renderColumnFooterCell(FacesContext facesContext, ResponseWriter writer, UIComponent uiComponent, UIComponent facet, String footerStyleClass, int colspan) throws IOException {
        if (footerStyleClass == null) {
            footerStyleClass = (String) uiComponent.getAttributes().get(JSFAttr.FOOTER_CLASS_ATTR);
        }
        super.renderColumnFooterCell(facesContext, writer, uiComponent, facet, footerStyleClass, colspan);
    }

    /**
     * @see org.apache.myfaces.renderkit.html.HtmlTableRendererBase#afterRow(javax.faces.context.FacesContext,
     *      javax.faces.component.UIData)
     */
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

            ResponseWriter writer = facesContext.getResponseWriter();

            writer.startElement(HTML.TR_ELEM, dataTable);
            writer.writeAttribute(HTML.CLASS_ATTR, "datatableseparator", null);
            writer.startElement(HTML.TD_ELEM, dataTable);
            writer.writeAttribute(HTML.CLASS_ATTR, "datatableseparator", null);
            writer.writeAttribute(HTML.COLSPAN_ATTR, new Integer(colspan), null);
            writer.startElement(HTML.DIV_ELEM, dataTable);
            writer.writeAttribute(HTML.CLASS_ATTR, "datatableseparator", null);
            writer.endElement(HTML.DIV_ELEM);
            writer.endElement(HTML.TD_ELEM);
            writer.endElement(HTML.TR_ELEM);
        }
    }

}