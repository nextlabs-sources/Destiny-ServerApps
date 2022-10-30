/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import com.bluejungle.destiny.webui.controls.UITab;
import com.bluejungle.destiny.webui.controls.UITabbedPane;
import com.bluejungle.destiny.webui.renderers.helpers.link.ILinkBuilder;
import com.bluejungle.destiny.webui.renderers.helpers.link.SimpleLinkBuilder;

import org.apache.myfaces.renderkit.RendererUtils;
import org.apache.myfaces.renderkit.html.HTML;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Renderer for a Tabbed Pane
 * 
 * @author sgoldstein
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/TabbedPaneRenderer.java#1 $
 */

public class TabbedPaneRenderer extends Renderer {

    private static final String FLAP_CONTENT_FACET_NAME = "tab content";
    private static final String BODY_FACET_NAME = "body";
    private static final String FOOTER_FACET_NAME = "footer";

    private static final String DEFAULT_START_DIV_CLASS = "details";
    private static final String DEFAULT_FLAP_START_DIV_CLASS = "tabheader";
    private static final String SELECTED_TAB_FLAP_LI_CLASS = "current";
    private static final String DISABLED_TAB_FLAP_LI_CLASS = "disabled";
    private static final String DEFAULT_TAB_BODY_DIV_CLASS = "sectioncontent tabcontent";
    private static final String DEFAULT_TAB_FOOTER_DIV_CLASS = "actionbuttons";

    private static final String TAB_TO_SELECT_PARAM_NAME = "tabToSelect";
    private static final String SUBMITTED_TAB_PARAM_NAME = "submittedTab";

    /**
     * Create an instance of TabbedPaneRenderer
     * 
     */
    public TabbedPaneRenderer() {
        super();
    }

    /**
     * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void decode(FacesContext facesContext, UIComponent component) {
        if (!(component instanceof UITabbedPane)) {
            throw new IllegalStateException("Invalid component: " + component.getClass().getName());
        }

        UITabbedPane tabbedPaneComponent = (UITabbedPane) component;
        String tabToSelectParamName = buildTabToSelectParamName(facesContext, tabbedPaneComponent);
        Map requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();
        String tabToSelect = (String) requestParameterMap.get(tabToSelectParamName);
        if ((tabToSelect != null) && (!tabToSelect.equals("")) && (!tabToSelect.equals("null"))) { // The
            // last
            // check
            // is
            // due
            // to a
            // MyFaces
            // behavior
            tabbedPaneComponent.setSelectedTab(tabToSelect);
        }

        String tabSubmittedParamName = buildTabSubmittedParamName(facesContext, tabbedPaneComponent);
        String tabSubmitted = (String) requestParameterMap.get(tabSubmittedParamName);
        if ((tabSubmitted != null) && (!tabSubmitted.equals("")) && (!tabSubmitted.equals("null"))) {
            tabbedPaneComponent.setSubmittedTab(tabSubmitted);
        }
    }

    /**
     * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        if (!(component instanceof UITabbedPane)) {
            throw new IllegalArgumentException("Invalid component: " + component.getClass().getName());
        }

        ResponseWriter writer = context.getResponseWriter();
        writeDivStart(component, DEFAULT_START_DIV_CLASS, writer);
    }

    /**
     * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        if (!(component instanceof UITabbedPane)) {
            throw new IllegalArgumentException("Invalid component: " + component.getClass().getName());
        }

        UITabbedPane tabbedPaneComponent = (UITabbedPane) component;
        TabData tabData = buildTabData(tabbedPaneComponent);
        encodeTabData(context, tabbedPaneComponent, tabData);

    }

    /**
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (!(component instanceof UITabbedPane)) {
            throw new IllegalArgumentException("Invalid component: " + component.getClass().getName());
        }

        ResponseWriter writer = context.getResponseWriter();
        writeDivEnd(writer);
    }

    /**
     * @see javax.faces.render.Renderer#getRendersChildren()
     */
    public boolean getRendersChildren() {
        return true;
    }

    /**
     * @param component
     */
    private TabData buildTabData(UITabbedPane component) {
        TabData tabDataToReturn = new TabData();

        List children = component.getChildren();
        Iterator childIterator = children.iterator();
        while (childIterator.hasNext()) {
            UIComponent nextChildComponent = (UIComponent) childIterator.next();

            if (!(nextChildComponent instanceof UITab)) {
                throw new IllegalStateException("The TabbedPaneRenderer does not currently support children which are not UITab components");
            }

            UITab nextChildTab = (UITab) nextChildComponent;
            UIComponent nextTabContentComponent = nextChildTab.getFacet(FLAP_CONTENT_FACET_NAME);
            UIComponent nextTabBodyComponent = nextChildTab.getFacet(BODY_FACET_NAME);
            UIComponent nextTabFooterComponent = nextChildTab.getFacet(FOOTER_FACET_NAME);
            String tabName = nextChildTab.getName();
            boolean isDisabled = nextChildTab.isDisabled();
            Collection uiParameters = nextChildTab.getUIParameters();

            Tab nextTab = new Tab(nextTabContentComponent, nextTabBodyComponent, nextTabFooterComponent, tabName, isDisabled, uiParameters);
            tabDataToReturn.addTab(nextTab);

            if (component.isTabSelected(tabName)) {
                tabDataToReturn.setSelectedTab(nextTab);
            }
        }

        return tabDataToReturn;
    }

    /**
     * Encode a Tabbed Pane
     * 
     * @param context
     * @param tabData
     * @throws IOException
     */
    private void encodeTabData(FacesContext context, UITabbedPane component, TabData tabData) throws IOException {
        encodeFlaps(context, component, tabData);
        encodeTabBody(context, component, tabData);
        encodeTabFooter(context, component, tabData);
    }

    /**
     * Encode the flaps of the tabbed pane
     * 
     * @param context
     * @param tabData
     * @throws IOException
     */
    private void encodeFlaps(FacesContext context, UITabbedPane component, TabData tabData) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writeDivStart(component, DEFAULT_FLAP_START_DIV_CLASS, writer);
        writer.startElement(HTML.UL_ELEM, component);

        Tab[] tabs = tabData.getTabs();
        for (int i = 0; i < tabs.length; i++) {
            Tab nextTab = tabs[i];

            writer.startElement(HTML.LI_ELEM, component);
            if (tabData.isSelected(nextTab)) {
                writer.writeAttribute(HTML.CLASS_ATTR, SELECTED_TAB_FLAP_LI_CLASS, null);
            } else if (nextTab.isDisabled()) {
                writer.writeAttribute(HTML.CLASS_ATTR, DISABLED_TAB_FLAP_LI_CLASS, null);
            }

            writer.startElement(HTML.DIV_ELEM, component);

            Map parameterMap = new HashMap(1);
            String tabToSelectParamName = buildTabToSelectParamName(context, component);
            parameterMap.put(tabToSelectParamName, new String[] { nextTab.getName() });
            parameterMap.putAll(nextTab.getParameters());
            ILinkBuilder linkBuilder = getLinkBuilder();
            linkBuilder.setDisabled(nextTab.isDisabled());
            linkBuilder.setRequestParameters(parameterMap);
            linkBuilder.encodeLinkStart(context, component, true);
            UIComponent flapContent = nextTab.getFlapContent();
            RendererUtils.renderChild(context, flapContent);
            linkBuilder.encodeLinkEnd(context, component);

            writer.endElement(HTML.DIV_ELEM);
            writer.endElement(HTML.LI_ELEM);
        }

        writeDivEnd(writer);
    }

    /**
     * Encode the body of the tabbed pane
     * 
     * @param context
     * @param tabData
     * @throws IOException
     */
    private void encodeTabBody(FacesContext context, UITabbedPane component, TabData tabData) throws IOException {
        Tab selectedTab = tabData.getSelectedTab();
        ResponseWriter writer = context.getResponseWriter();
        writeDivStart(component, DEFAULT_TAB_BODY_DIV_CLASS, writer);

        // Write hidden tab name which indicates which tab was submitted
        writer.startElement(HTML.INPUT_ELEM, component);
        writer.writeAttribute(HTML.TYPE_ATTR, "hidden", null);
        writer.writeAttribute(HTML.VALUE_ATTR, selectedTab.getName(), null);

        String submittedTabInputName = buildTabSubmittedParamName(context, component);
        writer.writeAttribute(HTML.NAME_ATTR, submittedTabInputName, null);
        writer.endElement(HTML.INPUT_ELEM);

        RendererUtils.renderChild(context, selectedTab.getBody());
        writeDivEnd(writer);
    }

    /**
     * Encode the footer of the tabbed pane
     * 
     * @param context
     * @param tabData
     * @throws IOException
     */
    private void encodeTabFooter(FacesContext context, UITabbedPane component, TabData tabData) throws IOException {
        Tab selectedTab = tabData.getSelectedTab();
        UIComponent footer = selectedTab.getFooter();
        if (footer != null) {
            ResponseWriter writer = context.getResponseWriter();
            writeDivStart(component, DEFAULT_TAB_FOOTER_DIV_CLASS, writer);
            RendererUtils.renderChild(context, footer);
            writeDivEnd(writer);
        }
    }

    /**
     * Create an request parameter name for a given tab within the tabbed pane
     * to denote its selection
     * 
     * @param context
     * @param component
     * @return
     */
    private String buildTabSubmittedParamName(FacesContext context, UITabbedPane component) {
        String clientID = component.getClientId(context);
        StringBuffer submittedTabParamName = new StringBuffer(clientID);
        submittedTabParamName.append(NamingContainer.SEPARATOR_CHAR);
        submittedTabParamName.append(SUBMITTED_TAB_PARAM_NAME);

        return submittedTabParamName.toString();
    }

    /**
     * Create an request parameter name for a given tab within the tabbed pane
     * to denote its selection
     * 
     * @param context
     * @param component
     * @return
     */
    private String buildTabToSelectParamName(FacesContext context, UITabbedPane component) {
        String clientID = component.getClientId(context);
        StringBuffer tabToSelectParamName = new StringBuffer(clientID);
        tabToSelectParamName.append(NamingContainer.SEPARATOR_CHAR);
        tabToSelectParamName.append(TAB_TO_SELECT_PARAM_NAME);

        return tabToSelectParamName.toString();
    }

    /**
     * Retrieve the a link builder
     * 
     * @return a link builder to utilize for building html links
     */
    private ILinkBuilder getLinkBuilder() {
        return new SimpleLinkBuilder();
    }

    /**
     * Write an html
     * 
     * <pre>
     *    
     *     
     *      &lt;div&gt;
     *      
     *     
     * </pre>
     * 
     * tag start
     * 
     * @param component
     * @param writer
     * @throws IOException
     */
    private void writeDivStart(UIComponent component, String divClass, ResponseWriter writer) throws IOException {
        writer.startElement(HTML.DIV_ELEM, component);
        writer.writeAttribute(HTML.CLASS_ATTR, divClass, null);

    }

    /**
     * Write an html
     * 
     * <pre>
     *    
     *     
     *      &lt;div&gt;
     *      
     *     
     * </pre>
     * 
     * tag end
     * 
     * @param writer
     * @throws IOException
     */
    private void writeDivEnd(ResponseWriter writer) throws IOException {
        writer.endElement(HTML.DIV_ELEM);
    }

    private class Tab {

        private final UIComponent flapContent;
        private final UIComponent body;
        private final UIComponent footer;
        private final String name;
        private final boolean disabled;
        private final Map parameters;

        /**
         * Create an instance of Tab
         * 
         * @param flapContent
         * @param body
         * @param footer
         * @param uiParameters 
         */
        public Tab(UIComponent flapContent, UIComponent body, UIComponent footer, String tabName, boolean disabled, Collection uiParameters) {
            if (flapContent == null) {
                throw new NullPointerException("flapContent cannot be null.");
            }

            if (body == null) {
                throw new NullPointerException("body cannot be null.");
            }

            if (tabName == null) {
                throw new NullPointerException("tabName cannot be null.");
            }
            
            if (uiParameters == null) {
                throw new NullPointerException("uiParameters cannot be null.");
            }
            
            this.flapContent = flapContent;
            this.body = body;
            this.footer = footer;
            this.name = tabName;
            this.disabled = disabled;
            
            this.parameters = new HashMap();
            Iterator uiParametersIterator = uiParameters.iterator();
            while (uiParametersIterator.hasNext()) {
                UIParameter nextParameter = (UIParameter) uiParametersIterator.next();
                String nextParameterName = nextParameter.getName();
                String[] nextParameterValue = new String[]{(String)nextParameter.getValue()};
                this.parameters.put(nextParameterName, nextParameterValue);
            }            
        }

        /**
         * Retrieve the body.
         * 
         * @return the body.
         */
        private UIComponent getBody() {
            return this.body;
        }

        /**
         * Retrieve the footer.
         * 
         * @return the footer.
         */
        private UIComponent getFooter() {
            return this.footer;
        }

        /**
         * Retrieve the flapContent.
         * 
         * @return the flapContent.
         */
        private UIComponent getFlapContent() {
            return this.flapContent;
        }

        /**
         * Retrieve the disabled.
         * 
         * @return the disabled.
         */
        public boolean isDisabled() {
            return this.disabled;
        }

        /**
         * Retrieve the tab name.
         * 
         * @return the tab name.
         */
        private String getName() {
            return this.name;
        }

        /**
         * Retrieve the parameters.
         * 
         * @return the parameters.
         */
        public Map getParameters() {
            return this.parameters;
        }
    }

    private class TabData {

        private Tab selectedTab;
        private ArrayList tabList = new ArrayList();

        /**
         * Retrieve the selectedTab.
         * 
         * @return the selectedTab.
         */
        private Tab getSelectedTab() {
            return this.selectedTab;
        }

        /**
         * @param nextTab
         * @return
         */
        public boolean isSelected(Tab nextTab) {
            return (nextTab == this.selectedTab);
        }

        /**
         * Set the selectedTab
         * 
         * @param selectedTab
         *            The selectedTab to set.
         */
        private void setSelectedTab(Tab selectedTab) {
            this.selectedTab = selectedTab;
        }

        /**
         * Retrieve the tabList.
         * 
         * @return the tabList.
         */
        private Tab[] getTabs() {
            return (Tab[]) this.tabList.toArray(new Tab[tabList.size()]);
        }

        /**
         * Add a tab to the tab data
         * 
         * @param tabToAdd
         */
        private void addTab(Tab tabToAdd) {
            tabList.add(tabToAdd);
        }
    }
}