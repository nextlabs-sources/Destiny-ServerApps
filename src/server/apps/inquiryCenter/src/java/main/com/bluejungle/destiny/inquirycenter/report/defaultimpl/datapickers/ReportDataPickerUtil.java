/*
 * Created on Jul 24, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers;

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.webui.browsabledatapicker.ISelectedItem;
import com.bluejungle.destiny.webui.browsabledatapicker.defaultimpl.ISelectedItemList;

/**
 * This is a simple utility class for the report data pickers
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/ReportDataPickerUtil.java#1 $
 */

public class ReportDataPickerUtil {

    private static final String COMMA_SPACE = ", ";
    private static final String EL_EXPRESSION_BEGIN = "#{";
    private static final String EL_EXPRESSION_END = "}";

    /**
     * Returns the current report based on a String used to create a value
     * binding linking to the selected report.
     * 
     * @param currentReportBindingExpr
     *            expression representing the currently selected report. This
     *            expression is a String and does not have the JSF begin and end
     *            characters for value binding.
     * @return the object bound to the value binding
     */
    public static final IReport getCurrentReport(final String currentReportBindingExpr) {
        if (currentReportBindingExpr == null) {
            throw new NullPointerException("currentReportBindingExpr cannot be null");
        }
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding(EL_EXPRESSION_BEGIN + currentReportBindingExpr + EL_EXPRESSION_END);
        Object resultObject = vb.getValue(ctx);
        IReport result = null;
        if (!(resultObject instanceof IReport)) {
            throw new ClassCastException("The result should implement the IReport class");
        } else {
            result = (IReport) resultObject;
        }
        return result;
    }

    /**
     * This function creates an input field selection expression based on a list
     * of items that are already selected in the report, and a list of items
     * that should be added to the list.
     * 
     * @param selectedItems
     *            newly selected items
     * @param existingReportValue
     *            existing selection
     * @return a string expression that will be set on the user input field
     */
    public static final String createInputFieldSelection(ISelectedItemList selectedItems, final String existingReportValue) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(existingReportValue);
        Iterator iter = selectedItems.iterator();
        while (iter.hasNext()) {
            buffer.append(COMMA_SPACE);
            ISelectedItem selectedItem = (ISelectedItem) iter.next();
            String textToStoreInInputField = selectedItem.getId();
            buffer.append(textToStoreInInputField);
        }
        return buffer.toString();
    }
}