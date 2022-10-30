/*
 * Created on Feb 28, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.webui.tags;

import javax.faces.component.UIComponent;

import org.apache.myfaces.taglib.html.ext.HtmlDataTableTag;

import com.bluejungle.destiny.webui.tags.TagUtil;


/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/nextlabs/destiny/webui/tags/PopupDataTableTag.java#1 $
 */

public class PopupDataTableTag extends HtmlDataTableTag {

    protected static final String COMPONENT_TYPE = "com.nextlabs.destiny.popupDataTable";
    protected static final String RENDERER_TYPE = "com.nextlabs.destiny.popupDataTableRenderer";
    protected static final String STRIPE_ATTR_NAME = "stripeRows";
//    protected static final String ROW_ON_MOUSE_OVER_ATTR_NAME = "rowOnMouseOver";
//    protected static final String ROW_ON_MOUSE_OUT_ATTR_NAME = "rowOnMouseOut";
//    protected static final String ROW_ON_CLICK_ATTR_NAME = "rowOnClick";
//    protected static final String ROW_ID_VARIABLE_ATTR_NAME = "rowIdVariable";
    private static final String EMPTY_TABLE_MESSAGE_ATTR_NAME = "emptyTableMessage";
    
    private boolean stripeRows = false;
//    private String rowOnMouseOver = null;
//    private String rowOnMouseOut = null;
//    private String rowOnClick = null;
//    private String rowIdVariable = null;
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
    
//    /**
//     * Returns the javascript for onmouseover
//     * 
//     * @return the javascript for onmouseover
//     */
//    protected String getRowOnMouseOver() {
//        return this.rowOnMouseOver;
//    }
//    
//    /**
//     * Returns the javascript for onmouseout
//     * 
//     * @return the javascript for onmouseout
//     */
//    protected String getRowOnMouseOut() {
//        return this.rowOnMouseOut;
//    }
//    
//    /**
//     * Returns the javascript for onmouseout
//     * 
//     * @return the javascript for onmouseout
//     */
//    protected String getRowOnClick() {
//        return this.rowOnClick;
//    }

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
//        component.getAttributes().put(ROW_ON_MOUSE_OVER_ATTR_NAME, getRowOnMouseOver());
//        component.getAttributes().put(ROW_ON_MOUSE_OUT_ATTR_NAME, getRowOnMouseOut());
//        component.getAttributes().put(ROW_ON_CLICK_ATTR_NAME, getRowOnClick());
//        component.getAttributes().put(ROW_ID_VARIABLE_ATTR_NAME, getRowIdVariable());
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
   
//    /**
//     * Sets the rowOnMouseOut
//     * @param rowOnMouseOut The rowOnMouseOut to set.
//     */
//    public void setRowOnMouseOut(String rowOnMouseOut) {
//        this.rowOnMouseOut = rowOnMouseOut;
//    }
//
//    /**
//     * Sets the rowOnMouseOver
//     * @param rowOnMouseOver The rowOnMouseOver to set.
//     */
//    public void setRowOnMouseOver(String rowOnMouseOver) {
//        this.rowOnMouseOver = rowOnMouseOver;
//    }
//    
//    /**
//     * Sets the rowOnMouseOver
//     * @param rowOnMouseOver The rowOnMouseOver to set.
//     */
//    public void setRowOnClick(String rowOnClick) {
//        this.rowOnClick = rowOnClick;
//    }

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
//        this.rowOnMouseOver = null;
//        this.rowOnMouseOut = null;
//        this.rowOnClick = null;
//        this.rowIdVariable = null;
    }
    
    /**
     * Retrieve the emptyMessage.
     * 
     * @return the emptyMessage.
     */
    private String getEmptyTableMessage() {
        return this.emptyTableMessage;
    }
//
//    /**
//     * Returns the rowId.
//     * @return the rowId.
//     */
//    public String getRowIdVariable() {
//        return this.rowIdVariable;
//    }
//    
//    /**
//     * Sets the rowId
//     * @param rowId The rowId to set.
//     */
//    public void setRowId(String rowIdVariable) {
//        this.rowIdVariable = rowIdVariable;
//    }
}
