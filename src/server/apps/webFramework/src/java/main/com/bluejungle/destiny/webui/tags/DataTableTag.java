/*
 * Created on Apr 25, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.component.UIComponent;

import org.apache.myfaces.taglib.html.ext.HtmlDataTableTag;

/**
 * This is the data table tag class. It extends the regular myFaces table tag
 * class.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/tags/DataTableTag.java#1 $
 */

public class DataTableTag extends HtmlDataTableTag {

    protected static final String COMPONENT_TYPE = "com.bluejungle.destiny.dataTable";
    protected static final String RENDERER_TYPE = "com.bluejungle.destiny.dataTableRenderer";
    protected static final String STRIPE_ATTR_NAME = "stripeRows";
    private static final String EMPTY_TABLE_MESSAGE_ATTR_NAME = "emptyTableMessage";

    private boolean stripeRows = false;
    private String emptyTableMessage = null;

    /**
     * @see javax.faces.webapp.UIComponentTag#getComponentType()
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    
    /**
     * @see javax.faces.webapp.UIComponentTag#getDoStartValue()
     */
    /*protected int getDoStartValue() throws JspException {
        UIComponent component = getComponentInstance();
        if (!(component instanceof UIData)) {
            throw new IllegalStateException("Unknown component, " + component.getClass().getName());
        }
        
        return (((UIData)component).getRowCount() <= 0) ? SKIP_BODY : super.getDoStartValue();         
    }*/
    
    /**
     * Returns whether the rows should be striped
     * 
     * @return whether the rows should be striped
     */
    protected boolean getStripeRows() {
        return this.stripeRows;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#getRendererType()
     */
    public String getRendererType() {
        return RENDERER_TYPE;
    }

    /**
     * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        component.getAttributes().put(STRIPE_ATTR_NAME, new Boolean(getStripeRows()));
        TagUtil.setString(component, EMPTY_TABLE_MESSAGE_ATTR_NAME, getEmptyTableMessage());
    }

    /**
     * Sets whether stripes should be displayed on rows
     * 
     * @param showStripe
     *            true if stripes should be displayed, false otherwise
     */
    public void setStripeRows(boolean showStripe) {
        this.stripeRows = showStripe;
    }

    /**
     * Set the emptyMessage
     * 
     * @param emptyMessage
     *            The emptyMessage to set.
     */
    public void setEmptyTableMessage(String emptyMessage) {
        this.emptyTableMessage = emptyMessage;
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        super.release();
        
        this.emptyTableMessage = null;
        this.stripeRows = false;
    }
    
    /**
     * Retrieve the emptyMessage.
     * 
     * @return the emptyMessage.
     */
    private String getEmptyTableMessage() {
        return this.emptyTableMessage;
    }
    
    
}