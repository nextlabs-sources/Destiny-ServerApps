/*
 * Created on Mar 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.faces.application.Application;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.component.html.ext.HtmlInputText;
import org.apache.myfaces.custom.calendar.HtmlCalendarRenderer;
import org.apache.myfaces.custom.calendar.HtmlInputCalendar;
import org.apache.myfaces.renderkit.RendererUtils;
import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.renderkit.html.HtmlRendererUtils;

/**
 * This is the HTML date picker renderer. For now, this renderer only supports
 * rendering a calendar input field with a popup to select the exact date.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/HTMLDatePickerRenderer.java#1 $
 */

public class HTMLDatePickerRenderer extends HtmlCalendarRenderer {

    protected static final String INPUT_SUFFIX = "_input";

    /**
     * Transfers the value of the real input control to the calendar component.
     * 
     * @param inputText
     *            input control where the user entered his value
     * @param calendarComponent
     *            complex calendar control (containing the inputText component)
     */
    private void assignValueToCalendarComponent(UIComponent inputText, UIComponent calendarComponent) {
        if (!(inputText instanceof EditableValueHolder)) {
            throw new IllegalArgumentException("input control should implement the EditableValueHolder interface");
        }

        if (!(calendarComponent instanceof EditableValueHolder)) {
            throw new IllegalArgumentException("calendar control should implement the EditableValueHolder interface");
        }
        Object latestValue = ((EditableValueHolder) inputText).getSubmittedValue();
        ((EditableValueHolder) calendarComponent).setSubmittedValue(latestValue);
    }

    /**
     * Decodes an incoming request parameters. Here, the calendar does not post
     * a new input directly, but its child component does. So, we call decode on
     * the child component directly. Then, the value (if any) needs to be passed
     * to the calendar component, as it is the control that hold the value.
     * 
     * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void decode(FacesContext facesContext, UIComponent component) {
        UIComponent inputComp = component.findComponent(getInputId(component));
        if (inputComp != null) {
            HtmlRendererUtils.decodeUIInput(facesContext, inputComp);
            assignValueToCalendarComponent(inputComp, component);
        }
    }

    /**
     * No children are supported in this renderer. Don't display anything about
     * them.
     * 
     * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    /**
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        RendererUtils.checkParamValidity(facesContext, component, HtmlInputCalendar.class);

        //Extracts the current value from the calendar component
        HtmlInputCalendar calendarComponent = (HtmlInputCalendar) component;
        Date value = null;
        try {
            value = RendererUtils.getDateValue(component);
        } catch (IllegalArgumentException e) {
            //Swallows the bad input
            value = null;
        }

        //Stores the value
        Locale currentLocale = facesContext.getViewRoot().getLocale();
        Calendar timeKeeper = Calendar.getInstance(currentLocale);
        timeKeeper.setTime(value != null ? value : new Date());

        //Renders the calendar control (and children)
        if (calendarComponent.isRenderAsPopup()) {
            String dateFormat = CalendarDateTimeConverter.createJSPopupFormat(facesContext, calendarComponent.getPopupDateFormat());
            Application application = facesContext.getApplication();

            HtmlInputText inputText = getInputComponent(calendarComponent);
            if (inputText == null) {
                inputText = (HtmlInputText) application.createComponent(HtmlInputText.COMPONENT_TYPE);
            }

            RendererUtils.copyHtmlInputTextAttributes(calendarComponent, inputText);
            inputText.setId(getInputId(calendarComponent)); //To prevent
            // duplicate id

            inputText.setEnabledOnUserRole(calendarComponent.getEnabledOnUserRole());
            inputText.setVisibleOnUserRole(calendarComponent.getVisibleOnUserRole());

            calendarComponent.getChildren().add(inputText);
            if (value != null) {
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                inputText.setValue(format.format(value));
            } else {
                inputText.setValue(null);
            }

            RendererUtils.renderChild(facesContext, inputText);

            DateFormatSymbols symbols = new DateFormatSymbols(currentLocale);
            String[] weekdays = DateUtil.mapWeekdays(symbols);
            String[] months = mapMonths(symbols);
            ResponseWriter writer = facesContext.getResponseWriter();

            UIForm form = getParentForm(facesContext, component);
            String formId = null;
            if (form != null) {
                formId = form.getClientId(facesContext);
            }
            getPopupImgText(facesContext, component, inputText.getClientId(facesContext), formId, dateFormat);

            writer.startElement(HTML.SCRIPT_ELEM, null);
            writer.writeAttribute(HTML.SCRIPT_TYPE_ATTR, HTML.SCRIPT_TYPE_TEXT_JAVASCRIPT, null);
            writer.write("jscalendarSetImageDirectory(\"" + ContextUtil.getFullContextLocation(facesContext, "/calendar/DB/") + "\");");
            writer.writeText(getLocalizedLanguageScript(symbols, months, timeKeeper.getFirstDayOfWeek(), calendarComponent), null);
            writer.endElement(HTML.SCRIPT_ELEM);
        }
    }

    /**
     * Returns the id of the input component
     * 
     * @param calendarComp
     *            calendar component tied to the input field
     * @return the id of the input field component
     */
    private String getInputId(UIComponent calendarComp) {
        return (calendarComp.getId() + INPUT_SUFFIX);
    }

    /**
     * Returns the input component within the calendar control. The input
     * component is supposed to be named with a given id and should be a child
     * of this control
     * 
     * @param calendarComponent
     *            parent calendar component
     * @return the input component (if exists), null otherwise
     */
    protected HtmlInputText getInputComponent(UIComponent calendarComponent) {
        HtmlInputText result = null;
        UIComponent inputText = calendarComponent.findComponent(getInputId(calendarComponent));
        if (inputText instanceof HtmlInputText) {
            result = (HtmlInputText) inputText;
        }
        return result;
    }

    /**
     * Retrieves the parent form of the component
     * 
     * @param context
     *            current JSF context
     * @param component
     *            child component from where the form search starts
     * @return the parent UI form component (if any), null otherwise.
     */
    private UIForm getParentForm(FacesContext context, UIComponent component) {
        UIComponent parent = component.getParent();
        while (parent != null && !(parent instanceof UIForm)) {
            parent = parent.getParent();
        }
        return (UIForm) parent;
    }

    /**
     * Renders the calendar icon for the control
     * 
     * @param component
     *            the component in which the rendering happens
     * @param writer
     *            response writer
     * @param clientId
     *            client id of the calendar component
     * @param formId
     *            client id of the form in which the calendar is located
     * @param dateFormat
     *            format to use for the date
     * @throws IOException
     *             if writer to the response writer fails
     */
    private void getPopupImgText(FacesContext facesContext, UIComponent component, String clientId, String formId, String dateFormat) throws IOException {
        String onClickExpr = "jscalendarPopUpCalendar(this, document.forms['" + formId + "'].elements['" + clientId + "'],'" + dateFormat + "')";
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement(HTML.ANCHOR_ELEM, component);
        writer.writeAttribute(HTML.ONCLICK_ATTR, onClickExpr, null);
        writer.startElement(HTML.IMG_ELEM, component);
        writer.writeAttribute(HTML.CLASS_ATTR, "calendarpopup", null);
        writer.writeAttribute(HTML.SRC_ATTR, ContextUtil.getFullContextLocation(facesContext, "/calendar/images/calendar-icon.gif"), null);
        writer.endElement(HTML.IMG_ELEM);
        writer.endElement(HTML.ANCHOR_ELEM);
    }

    /**
     * Yes, this control renders its own children, so the framework does not
     * have to walk through them and render them. Here, the calendar input
     * control renders takes care of rendering its own (children) input text,
     * button for popup, etc.
     * 
     * @see javax.faces.render.Renderer#getRendersChildren()
     */
    public boolean getRendersChildren() {
        return true;
    }
}