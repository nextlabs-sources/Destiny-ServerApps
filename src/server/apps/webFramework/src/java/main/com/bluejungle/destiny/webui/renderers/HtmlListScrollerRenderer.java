/*
 * Created on Mar 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionListener;

import org.apache.myfaces.custom.datascroller.HtmlDataScroller;
import org.apache.myfaces.custom.datascroller.HtmlDataScrollerRenderer;
import org.apache.myfaces.renderkit.RendererUtils;
import org.apache.myfaces.renderkit.html.HTML;

import com.bluejungle.destiny.webui.framework.faces.NewSelectedItemEvent;

/**
 * This is the data scroller renderer for data list. It displays the scrolling
 * controls, and the children components in the middle. This renderer does not
 * render the paginator control in the middle, and replaces it with whatever
 * child control (if any) that has been placed in the JSF page.
 * 
 * This renderers renders the facets related to "previous" first in encodeBegin.
 * Then, encodeChildren will render the children components (if any), and
 * finally encodeEnd will deal with the "after" controls.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/HtmlListScrollerRenderer.java#2 $
 */

public class HtmlListScrollerRenderer extends HtmlDataScrollerRenderer {

    /**
     * Decodes an incoming request.
     * 
     * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void decode(FacesContext context, UIComponent component) {
        RendererUtils.checkParamValidity(context, component, HtmlDataScroller.class);

        HtmlDataScroller scroller = (HtmlDataScroller) component;

        UIData uiData = findUIData(scroller, component);
        if (uiData == null) {
            return;
        }

        Map parameter = context.getExternalContext().getRequestParameterMap();
        String param = (String) parameter.get(component.getClientId(context));
        if (param != null) {
            if (param.equals(FACET_FIRST)) {
                uiData.setFirst(0);
                UIComponent facetFirst = (UIComponent) component.getFacets().get(param);
                fireNewSelectedItemEvent(facetFirst, uiData);
            } else if (param.equals(FACET_PREVOIUS)) {
                int previous = uiData.getFirst() - uiData.getRows();
                if (previous >= 0) {
                    uiData.setFirst(previous);
                    UIComponent facetPrevious = (UIComponent) component.getFacets().get(param);
                    fireNewSelectedItemEvent(facetPrevious, uiData);
                }
            } else if (param.equals(FACET_NEXT)) {
                int next = uiData.getFirst() + uiData.getRows();
                if (next < uiData.getRowCount()) {
                    uiData.setFirst(next);
                    UIComponent facetNext = (UIComponent) component.getFacets().get(param);
                    fireNewSelectedItemEvent(facetNext, uiData);
                }
            } else if (param.equals(FACET_FAST_FORWARD)) {
                int fastStep = scroller.getFastStep();
                if (fastStep <= 0) {
                    fastStep = 1;
                }
                int next = uiData.getFirst() + uiData.getRows() * fastStep;
                int rowcount = uiData.getRowCount();
                if (next > rowcount) {
                    next = (rowcount - 1) - ((rowcount - 1) % uiData.getRows());
                }
                uiData.setFirst(next);
                UIComponent facetForward = (UIComponent) component.getFacets().get(param);
                fireNewSelectedItemEvent(facetForward, uiData);
            } else if (param.equals(FACET_FAST_REWIND)) {
                int fastStep = scroller.getFastStep();
                if (fastStep <= 0) {
                    fastStep = 1;
                }
                int previous = uiData.getFirst() - uiData.getRows() * fastStep;
                if (previous < 0) {
                    previous = 0;
                }
                uiData.setFirst(previous);
                UIComponent facetFastRewind = (UIComponent) component.getFacets().get(param);
                fireNewSelectedItemEvent(facetFastRewind, uiData);
            } else if (param.equals(FACET_LAST)) {
                int rowcount = uiData.getRowCount();
                int rows = uiData.getRows();
                int delta = rowcount % rows;
                int first = delta > 0 && delta < rows ? rowcount - delta : rowcount - rows;
                if (first >= 0) {
                    uiData.setFirst(first);
                } else {
                    uiData.setFirst(0);
                }
                UIComponent facetLast = (UIComponent) component.getFacets().get(param);
                fireNewSelectedItemEvent(facetLast, uiData);
            } else if (param.startsWith(PAGE_NAVIGATION)) {
                int index = Integer.parseInt(param.substring(PAGE_NAVIGATION.length(), param.length()));
                int pageCount = getPageCount(uiData);
                if (index > pageCount) {
                    index = pageCount;
                } else if (index <= 0) {
                    index = 1;
                }
                uiData.setFirst(uiData.getRows() * (index - 1));
            }
        }
    }

    /**
     * Returns whether the scroller can scroll more to reach the first record
     * 
     * @return true if the scroller is not on the first record already, false
     *         otherwise
     */
    protected boolean isGoFirstEnabled(FacesContext facesContext, UIComponent uiComponent) {
        boolean result = false;
        RendererUtils.checkParamValidity(facesContext, uiComponent, HtmlDataScroller.class);
        HtmlDataScroller scroller = (HtmlDataScroller) uiComponent;

        UIData uiData = findUIData(scroller, uiComponent);
        if (uiData != null) {
            int firstDisplayedRowIndex = uiData.getFirst();
            result = firstDisplayedRowIndex - uiData.getRows() >= 0 ? true : false;
        }
        return result;
    }

    /**
     * Returns whether the scroller can scroll more to reach the last record
     * 
     * @return true if the scroller is not on the last record already, false
     *         otherwise
     */
    protected boolean isGoLastEnabled(FacesContext facesContext, UIComponent uiComponent) {
        boolean result = false;
        RendererUtils.checkParamValidity(facesContext, uiComponent, HtmlDataScroller.class);
        HtmlDataScroller scroller = (HtmlDataScroller) uiComponent;

        UIData uiData = findUIData(scroller, uiComponent);
        if (uiData != null) {
            int firstDisplayedRowIndex = uiData.getFirst();
            result = firstDisplayedRowIndex + uiData.getRows() < uiData.getRowCount() ? true : false;
        }
        return result;
    }

    /**
     * Returns whether the scroller can scroll to the next page
     * 
     * @return true if the scroller can go to the next page, false otherwise
     */
    protected boolean isGoNextEnabled(FacesContext facesContext, UIComponent uiComponent) {
        return (isGoLastEnabled(facesContext, uiComponent));
    }

    /**
     * Returns whether the scroller can scroll to the previous page
     * 
     * @return true if the scroller can go to the previous page, false otherwise
     */
    protected boolean isGoPreviousEnabled(FacesContext facesContext, UIComponent uiComponent) {
        return (isGoFirstEnabled(facesContext, uiComponent));
    }

    /**
     * Renders the facets related to "previous" scrolling (fastrewind, first,
     * previous).
     * 
     * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        RendererUtils.checkParamValidity(facesContext, uiComponent, HtmlDataScroller.class);

        ResponseWriter writer = facesContext.getResponseWriter();
        HtmlDataScroller scroller = (HtmlDataScroller) uiComponent;
        UIData uiData = findUIData(scroller, uiComponent);
        if (uiData != null) {
            writer.startElement(HTML.DIV_ELEM, scroller);
            String styleClass = scroller.getStyleClass();
            if (styleClass != null) {
                writer.writeAttribute(HTML.CLASS_ATTR, styleClass, null);
            }

            UIComponent facetComp = scroller.getFirst();
            if (facetComp != null) {
                boolean enableLink = false;
                renderFacetWithLink(facesContext, scroller, facetComp, FACET_FIRST, isGoFirstEnabled(facesContext, scroller));
            }

            facetComp = scroller.getPrevious();
            if (facetComp != null) {
                boolean enableLink = false;
                renderFacetWithLink(facesContext, scroller, facetComp, FACET_PREVOIUS, isGoPreviousEnabled(facesContext, scroller));
            }
        }
    }

    /**
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        RendererUtils.checkParamValidity(facesContext, uiComponent, HtmlDataScroller.class);

        ResponseWriter writer = facesContext.getResponseWriter();
        HtmlDataScroller scroller = (HtmlDataScroller) uiComponent;
        UIData uiData = findUIData(scroller, uiComponent);
        if (uiData == null) {
            return;
        }

        UIComponent facetComp = scroller.getNext();
        if (facetComp != null) {
            renderFacetWithLink(facesContext, scroller, facetComp, FACET_NEXT, isGoNextEnabled(facesContext, scroller));
        }
        facetComp = scroller.getLast();
        if (facetComp != null) {
            renderFacetWithLink(facesContext, scroller, facetComp, FACET_LAST, isGoLastEnabled(facesContext, scroller));
        }
        writer.endElement(HTML.DIV_ELEM);
    }

    /**
     * Creates and fires an event mentionning that a new item has been selected
     * 
     * @param facetComp
     *            facet component that has been clicked
     * @param table
     *            data table component
     */
    protected void fireNewSelectedItemEvent(UIComponent facetComp, UIData table) {
        int currentRowIndex = table.getRowIndex();
        table.setRowIndex(table.getFirst());
        if (table.isRowAvailable()) {
            Object newSelectedItem = table.getRowData();
            table.setRowIndex(currentRowIndex);
            facetComp.queueEvent(new NewSelectedItemEvent(facetComp, newSelectedItem));
        }
    }

    /**
     * 
     * @param facesContext
     *            JSF context
     * @param scroller
     *            scroller UI component
     * @param facetComp
     *            facet component to render
     * @param facetName
     *            name of the facet
     * @param linkClass
     *            class of the link to use
     * @throws IOException
     *             if writing output to the writer fails.
     */
    protected void renderFacetWithLink(FacesContext facesContext, HtmlDataScroller scroller, UIComponent facetComp, String facetName, boolean enableLink) throws IOException {
        boolean facetHasLink = false;
        HtmlCommandLink link = getLink(facesContext, scroller, null, facetName);
        if (facetComp instanceof HtmlCommandLink) {
            facetHasLink = true;
            HtmlCommandLink facetLinkComp = (HtmlCommandLink) facetComp;
            ActionListener[] listeners = facetLinkComp.getActionListeners();
            int size = listeners.length;
            for (int i = 0; i < size; i++) {
                link.addActionListener(listeners[i]);
            }

            link.setImmediate(facetLinkComp.isImmediate());

            //Picks up any parameters of the command link
            List children = facetLinkComp.getChildren();
            Iterator it = children.iterator();
            List childrenToRemove = new ArrayList();
            while (it.hasNext()) {
                UIComponent child = (UIComponent) it.next();
                if (child instanceof UIParameter) {
                    childrenToRemove.add(child);
                }
            }
            it = childrenToRemove.iterator();
            while (it.hasNext()) {
                link.getChildren().add(it.next());
            }
            childrenToRemove.clear();
        }

        String className = enableLink ? scroller.getPaginatorActiveColumnClass() : scroller.getPaginatorColumnClass();
        if (className != null) {
            link.setStyleClass(className);
        }

        //Don't put an HREF to the link if it is disabled
        ResponseWriter writer = facesContext.getResponseWriter();
        if (enableLink) {
            link.encodeBegin(facesContext);
        } else {
            writer.startElement(HTML.ANCHOR_ELEM, link);
            if (link.getStyleClass() != null) {
                writer.writeAttribute(HTML.CLASS_ATTR, className, null);
            }
        }

        if (!facetHasLink) {
            facetComp.encodeBegin(facesContext);
        }

        if (facetComp.getRendersChildren()) {
            facetComp.encodeChildren(facesContext);
        }

        if (!facetHasLink) {
            facetComp.encodeEnd(facesContext);
        }

        if (enableLink) {
            link.encodeEnd(facesContext);
        } else {
            writer.endElement(HTML.ANCHOR_ELEM);
        }
    }
}