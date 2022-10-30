/*
 * Created on Mar 16, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

import org.apache.myfaces.renderkit.html.HTML;

import com.bluejungle.destiny.webui.controls.UIMenu;
import com.bluejungle.destiny.webui.controls.UIMenuItem;

/**
 * The MenuRenderer is a JSF renderer for encoding a Destiny menu.
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/MenuRenderer.java#1 $
 */
public class MenuRenderer extends Renderer {

	private static final String STYLE_CLASS_ATTRIBUTE_NAME = "styleClass";
	private static final String SELECTED_ITEM_STYLE_CLASS_ATTRIBUTE_NAME = "selectedItemStyleClass";
	private static final String VIEW_ID_PATTERN_ATTR_NAME = "viewIdPattern";
	private static final String LIST_ITEM_ID_SUFFIX = "li";

	private HtmlLinkRenderer linkRenderer;

	/**
	 * Create a MenuRenderer
	 * 
	 */
	public MenuRenderer() {
		super();
		linkRenderer = new HtmlLinkRenderer();
	}

	/**
	 * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
	 *      javax.faces.component.UIComponent)
	 */
	public void encodeBegin(FacesContext facesContext, UIComponent component)
			throws IOException {
		if (component instanceof UIMenu) {
			encodeUnordereListStart(facesContext, component);
		} else {
			throw new IllegalStateException("Unknown component: "
					+ component.getClass().getName());
		}
	}

	/**
	 * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext,
	 *      javax.faces.component.UIComponent)
	 */
	public void encodeChildren(FacesContext facesContext, UIComponent component)
			throws IOException {

		List childComponents = component.getChildren();
		Iterator childComponentIterator = childComponents.iterator();
		int index = 0;
		while (childComponentIterator.hasNext()) {
			UIComponent childComponent = (UIComponent) childComponentIterator
					.next();
			if (childComponent instanceof UIMenuItem) {
				encodeListItemBegin(facesContext, childComponent, index);
				linkRenderer.encodeBegin(facesContext, childComponent);
				if (linkRenderer.getRendersChildren()) {
					linkRenderer.encodeChildren(facesContext, childComponent);
				}
				linkRenderer.encodeEnd(facesContext, childComponent);
				encodeListItemEnd(facesContext, childComponent);
			} else {
				throw new IllegalStateException("Unknown child component: "
						+ childComponent.getClass().getName());
			}
			index++;
		}
	}

	/**
	 * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
	 *      javax.faces.component.UIComponent)
	 */
	public void encodeEnd(FacesContext facesContext, UIComponent component)
			throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		encodeUnordereListEnd(writer, component);
	}

	/**
	 * @see javax.faces.render.Renderer#getRendersChildren()
	 */
	public boolean getRendersChildren() {
		return true;
	}

	/**
	 * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext,
	 *      javax.faces.component.UIComponent)
	 */
	public void decode(FacesContext facesContext, UIComponent component) {
		List childComponents = component.getChildren();
		Iterator childComponentIterator = childComponents.iterator();
		while (childComponentIterator.hasNext()) {
			UIComponent childComponent = (UIComponent) childComponentIterator
					.next();
			if (childComponent instanceof UIMenuItem) {
				linkRenderer.decode(facesContext, childComponent);
			} else {
				throw new IllegalStateException("Unknown child component: "
						+ childComponent.getClass().getName());
			}
		}
	}

	/**
	 * Render the beginning of the Destiny menu using an unordered list item.
	 * 
	 * @throws IOException
	 */
	private void encodeUnordereListStart(FacesContext facesContext,
			UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

		writer.startElement(HTML.UL_ELEM, component);
		writer.writeAttribute(HTML.ID_ATTR,
				component.getClientId(facesContext), null);

		String styleClass = (String) component.getAttributes().get(
				STYLE_CLASS_ATTRIBUTE_NAME);
		if (styleClass != null) {
			writer.writeAttribute(HTML.CLASS_ATTR, styleClass, null);
		}
		writer.write("\n");
	}

	/**
	 * Render the end of the Destiny menu using an unordered list item.
	 * 
	 * @throws IOException
	 */
	private void encodeUnordereListEnd(ResponseWriter writer,
			UIComponent component) throws IOException {
		writer.endElement(HTML.UL_ELEM);
		writer.write("\n");
	}

	/**
	 * Render the beginning of a Destiny menu item using a list item.
	 * 
	 * @throws IOException
	 */
	private void encodeListItemBegin(FacesContext facesContext,
			UIComponent component, int index) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		writer.startElement(HTML.LI_ELEM, component);
		writer.writeAttribute(HTML.ID_ATTR, component.getClientId(facesContext)
				+ NamingContainer.SEPARATOR_CHAR + LIST_ITEM_ID_SUFFIX, null);

		UIMenu parentComponent = (UIMenu) component.getParent();
		String styleClass = (String) parentComponent.getAttributes().get(
				SELECTED_ITEM_STYLE_CLASS_ATTRIBUTE_NAME);
		if ((styleClass != null) && (!styleClass.equals(""))) {
			UIMenuItem menuItemComponent = (UIMenuItem) component;
			if (isViewIdPatternMatch(menuItemComponent, facesContext)) {
				writer
						.writeAttribute(HTML.CLASS_ATTR, styleClass + index,
								null);
			}
		}

		writer.startElement(HTML.DIV_ELEM, component);
		writer.write("\n");
	}

	/**
	 * Render the end of a Destiny menu item using a list item.
	 * 
	 * @throws IOException
	 */
	private void encodeListItemEnd(FacesContext facesContext,
			UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		writer.endElement(HTML.DIV_ELEM);
		writer.endElement(HTML.LI_ELEM);
		writer.write("\n");
	}

	/**
	 * Determine if the view id pattern set for the provided menu item component
	 * matches the current view id.
	 * 
	 * @param menuItemComponent
	 *            the menu item with the view id to test
	 * @param facesContext
	 *            a FacesContext with the current view id
	 * @return true if the view id pattern matches the current view id; false
	 *         otherwise
	 */
	private boolean isViewIdPatternMatch(UIMenuItem menuItemComponent,
			FacesContext facesContext) {
		boolean viewIdPatternMatch = false;

		HttpServletRequest servletRequest = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		String viewId = servletRequest.getRequestURI();
		String queryString = servletRequest.getQueryString();
		if (queryString != null) {
			viewId += queryString;
		}
		String viewIdPattern = (String) menuItemComponent.getAttributes().get(
				VIEW_ID_PATTERN_ATTR_NAME);

		if ((viewIdPattern != null) && (viewId.indexOf(viewIdPattern) > -1)) {
			viewIdPatternMatch = true;
		}

		return viewIdPatternMatch;
	}
}