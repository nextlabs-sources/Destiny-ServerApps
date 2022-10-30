/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.controls;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;

import java.util.Iterator;
import java.util.List;

/**
 * Java Server Faces component for creating a Tabbed Pane. Work in conjunction
 * with the
 * 
 * @see UITab component
 * 
 * @author sgoldstein
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/controls/UITabbedPane.java#1 $
 */
public class UITabbedPane extends UIPanel {

    private String preselectedTabName = null;
    private UITab selectedTab = null;
    private String submittedTabName = null;

    /**
     * The component family of the UITabbedPane component
     */
    public static final String COMPONENT_FAMILY = "com.bluejungle.destiny.TabbedPane";

    /**
     * The renderer type used to render a UITabbedPane by default
     */
    public static final String DEFAULT_RENDERER_TYPE = "com.bluejungle.destiny.TabbedPaneRenderer";

    /**
     * Create an instance of UITabbedPane
     * 
     */
    public UITabbedPane() {
        super();
        this.setRendererType(DEFAULT_RENDERER_TYPE);
    }

    /**
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * Determine if the tab with specified name is selected
     * 
     * @param tabName
     * @return true if the tab is selected, false otherwise
     */
    public boolean isTabSelected(String tabName) {
        if (tabName == null) {
            throw new NullPointerException("tabName cannot be null.");
        }

        boolean valueToReturn = false;

        UITab selectedTab = getSelectedTab();
        if (selectedTab != null) {
            valueToReturn = tabName.equals(selectedTab.getName());
        }

        return valueToReturn;
    }

    /**
     * Determine if the specified tab is selected
     * 
     * @param tab
     *            the tab to qery
     * @return true if it's selected; false otherwise
     */
    public boolean isTabSelected(UITab tab) {
        if (tab == null) {
            throw new NullPointerException("tab cannot be null.");
        }

        boolean valueToReturn = false;

        UITab selectedTab = getSelectedTab();
        if (selectedTab != null) {
            valueToReturn = tab.equals(selectedTab);
        }

        return valueToReturn;
    }

    /**
     * Retrieve the selected tab. If no tab has been selected, the first tab is
     * automatically selected. If no tabs have been added to this tabbed pan,
     * null is returned
     * 
     * @return the selected tab or null if no tabs have been added to this
     *         tabbed pane
     */
    public UITab getSelectedTab() {
        if (this.selectedTab == null) {
            List children = this.getChildren();
            if (!children.isEmpty()) {
                // First, check if a tab has been preselected
                if (this.preselectedTabName != null) {
                    UITab tabToSelect = findTab(this.preselectedTabName);
                    setSelectedTab(tabToSelect);
                    this.preselectedTabName = null;
                } else {
                    // Find the first time in the list
                    Object firstChild = this.getChildren().get(0);
                    if (!(firstChild instanceof UITab)) {
                        StringBuffer errorMessage = new StringBuffer("UITabbedPane can only be the parent of a UITab instance.  Found: ");
                        errorMessage.append(firstChild.getClass().getName());

                        throw new IllegalStateException(errorMessage.toString());
                    }

                    UITab tabToSelect = (UITab) firstChild;
                    setSelectedTabImpl(tabToSelect);
                }
            }
        }

        return this.selectedTab;
    }

    /**
     * Set the tab with the specified name to be the selected tab
     * 
     * @param tabName
     *            the name of the tab to select
     */
    public void setSelectedTab(String tabName) {
        if (tabName == null) {
            throw new NullPointerException("tabName cannot be null.");
        }

        if (!isTabSelected(tabName)) {
            if (this.getChildCount() == 0) {
                /*
                 * Assume that the children have not yet been added
                 */
                this.preselectedTabName = tabName;
            } else {
                UITab tabToSelect = findTab(tabName);
                this.setSelectedTab(tabToSelect);
            }
        }
    }

    /**
     * Determine if the tab with the specified name was submitted (the selected
     * tab may not be the submitted tab in the case of browser refresh and tab
     * switching)
     * 
     * @param tabName
     *            the tab to query
     * @return true if the tab was submitted, false otherwise
     */
    public boolean wasTabSubmitted(String tabName) {
        if (submittedTabName != null) {
            return submittedTabName.equals(tabName);
        } else {
            // I don't think tabName is null but be safe.
            return tabName == null;
        }
    }

    /**
     * Set the tab that was submitted
     * 
     * @param submittedTabName
     *            the name of the tab that was submitted
     */
    public void setSubmittedTab(String submittedTabName) {
        this.submittedTabName = submittedTabName;
    }

    /**
     * Add a listener to listner for {@see SelectedTabChangeEvent}'s
     * 
     */
    public void addSelectedTabChangeEventListner(SelectedTabChangeEventListener listnerToAdd) {
        if (listnerToAdd == null) {
            throw new NullPointerException("listnerToAdd cannot be null.");
        }

        addFacesListener(listnerToAdd);
    }

    /**
     * <p>
     * Override {@link UIComponent#processDecodes}to ensure that the tabbed
     * pane is decoded <strong>before </strong> its children. This is necessary
     * to allow the <code>submittedTab</code> property to be correctly set.
     * </p>
     * 
     * @exception NullPointerException
     *                {@inheritDoc}
     */
    public void processDecodes(FacesContext context) {

        if (context == null) {
            throw new NullPointerException();
        }

        // Skip processing if our rendered flag is false
        if (!isRendered()) {
            return;
        }

        // Process this component itself
        decode(context);

        // Process the submitted tab
        if (this.submittedTabName != null) {
            UITab tabSubmitted = findTab(this.submittedTabName);
            tabSubmitted.processDecodes(context);
        }
    }

    /**
     * @see javax.faces.component.UIComponentBase#processUpdates(javax.faces.context.FacesContext)
     */
    public void processUpdates(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }

        // Skip processing if our rendered flag is false
        if (!isRendered()) {
            return;
        }

        // Process the submitted tab
        if (this.submittedTabName != null) {
            UITab tabSubmitted = findTab(this.submittedTabName);
            tabSubmitted.processUpdates(context);
        }
    }

    /**
     * @see javax.faces.component.UIComponentBase#processValidators(javax.faces.context.FacesContext)
     */
    public void processValidators(FacesContext context) {

        if (context == null) {
            throw new NullPointerException();
        }

        // Skip processing if our rendered flag is false
        if (!isRendered()) {
            return;
        }

        // Process the submitted tab
        // Process the submitted tab
        if (this.submittedTabName != null) {
            UITab tabSubmitted = findTab(this.submittedTabName);
            tabSubmitted.processValidators(context);
        }
    }

    /**
     * Set the specified tab to be the selected one. Currently private because
     * it explicitly does not check that the specified tab is a child of this
     * tabbed pane
     * 
     * @param tabToSelect
     *            the tab to select
     */
    private void setSelectedTab(UITab tabToSelect) {
        if (tabToSelect == null) {
            throw new NullPointerException("tabToSelect cannot be null.");
        }

		this.setSelectedTabImpl(tabToSelect);
		
        SelectedTabChangeEvent selectedTabChangedEvent = new SelectedTabChangeEvent(this);
        queueEvent(selectedTabChangedEvent);
    }

    /**
     * Set the specified tab to be the selected one. This is similar
     * to setSelectTab(), but does not fire the SelectedTabChangedEvent
     * 
     * @param tabToSelect
     *            the tab to select
     */
	private void setSelectedTabImpl(UITab tabToSelect) {
		if (tabToSelect == null) {
            throw new NullPointerException("tabToSelect cannot be null.");
        }
				
        this.selectedTab = tabToSelect;        
	}
	
    /**
     * Find the tab with the specified name
     */
    private UITab findTab(String tabName) {
        if (tabName == null) {
            throw new NullPointerException("tabName cannot be null.");
        }

        UITab tabToReturn = null;

        Iterator childrenIterator = getChildren().iterator();
        while ((childrenIterator.hasNext()) && (tabToReturn == null)) {
            UITab nextChildTab = (UITab) childrenIterator.next();
            String nextChildTabName = nextChildTab.getName();
            if (nextChildTabName.equals(tabName)) {
                tabToReturn = nextChildTab;
            }
        }

        if (tabToReturn == null) {
            StringBuffer errorMessage = new StringBuffer("Tab with tab name, ");
            errorMessage.append(tabName);
            errorMessage.append(", could not be found.");

            throw new IllegalArgumentException(errorMessage.toString());
        }

        return tabToReturn;
    }
}
