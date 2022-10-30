/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.jsfmock;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;

/**
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/jsfmock/MockApplication.java#1 $
 */

public class MockApplication extends Application {
    private ViewHandler viewHandler;
    
    /**
     * Constructor
     */
    public MockApplication() {
        super();
        FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY, MockRenderKitFactory.class.getName());
    }

    /**
     * @see javax.faces.application.Application#getActionListener()
     */
    public ActionListener getActionListener() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#setActionListener(javax.faces.event.ActionListener)
     */
    public void setActionListener(ActionListener arg0) {
    }

    /**
     * @see javax.faces.application.Application#getDefaultLocale()
     */
    public Locale getDefaultLocale() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#setDefaultLocale(java.util.Locale)
     */
    public void setDefaultLocale(Locale arg0) {
    }

    /**
     * @see javax.faces.application.Application#getDefaultRenderKitId()
     */
    public String getDefaultRenderKitId() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#setDefaultRenderKitId(java.lang.String)
     */
    public void setDefaultRenderKitId(String arg0) {
    }

    /**
     * @see javax.faces.application.Application#getMessageBundle()
     */
    public String getMessageBundle() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#setMessageBundle(java.lang.String)
     */
    public void setMessageBundle(String arg0) {
    }

    /**
     * @see javax.faces.application.Application#getNavigationHandler()
     */
    public NavigationHandler getNavigationHandler() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#setNavigationHandler(javax.faces.application.NavigationHandler)
     */
    public void setNavigationHandler(NavigationHandler arg0) {
    }

    /**
     * @see javax.faces.application.Application#getPropertyResolver()
     */
    public PropertyResolver getPropertyResolver() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#setPropertyResolver(javax.faces.el.PropertyResolver)
     */
    public void setPropertyResolver(PropertyResolver arg0) {
    }

    /**
     * @see javax.faces.application.Application#getVariableResolver()
     */
    public VariableResolver getVariableResolver() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#setVariableResolver(javax.faces.el.VariableResolver)
     */
    public void setVariableResolver(VariableResolver arg0) {
    }

    /**
     * @see javax.faces.application.Application#getViewHandler()
     */
    public ViewHandler getViewHandler() {
        return this.viewHandler;
    }

    /**
     * @see javax.faces.application.Application#setViewHandler(javax.faces.application.ViewHandler)
     */
    public void setViewHandler(ViewHandler viewHandler) {
        this.viewHandler = viewHandler;
    }

    /**
     * @see javax.faces.application.Application#getStateManager()
     */
    public StateManager getStateManager() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#setStateManager(javax.faces.application.StateManager)
     */
    public void setStateManager(StateManager arg0) {
    }

    /**
     * @see javax.faces.application.Application#addComponent(java.lang.String,
     *      java.lang.String)
     */
    public void addComponent(String arg0, String arg1) {
    }

    /**
     * @see javax.faces.application.Application#createComponent(java.lang.String)
     */
    public UIComponent createComponent(String type) throws FacesException {
        UIComponent result = null;
        if ("javax.faces.HtmlCommandLink".equals(type)) {
            result = new HtmlCommandLink ();
        } else if ("javax.faces.Parameter".equals(type)) {
            result = new UIParameter ();
        }
        return result;
    }

    /**
     * @see javax.faces.application.Application#createComponent(javax.faces.el.ValueBinding,
     *      javax.faces.context.FacesContext, java.lang.String)
     */
    public UIComponent createComponent(ValueBinding arg0, FacesContext arg1, String arg2) throws FacesException {
        return null;
    }

    /**
     * @see javax.faces.application.Application#getComponentTypes()
     */
    public Iterator getComponentTypes() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#addConverter(java.lang.String,
     *      java.lang.String)
     */
    public void addConverter(String arg0, String arg1) {
    }

    /**
     * @see javax.faces.application.Application#addConverter(java.lang.Class,
     *      java.lang.String)
     */
    public void addConverter(Class arg0, String arg1) {
    }

    /**
     * @see javax.faces.application.Application#createConverter(java.lang.String)
     */
    public Converter createConverter(String arg0) {
        return null;
    }

    /**
     * @see javax.faces.application.Application#createConverter(java.lang.Class)
     */
    public Converter createConverter(Class arg0) {
        return null;
    }

    /**
     * @see javax.faces.application.Application#getConverterIds()
     */
    public Iterator getConverterIds() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#getConverterTypes()
     */
    public Iterator getConverterTypes() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#createMethodBinding(java.lang.String,
     *      java.lang.Class[])
     */
    public MethodBinding createMethodBinding(String ref, Class[] arg1) throws ReferenceSyntaxException {
        return new MockMethodBinding(ref);
    }

    /**
     * @see javax.faces.application.Application#getSupportedLocales()
     */
    public Iterator getSupportedLocales() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#setSupportedLocales(java.util.Collection)
     */
    public void setSupportedLocales(Collection arg0) {
    }

    /**
     * @see javax.faces.application.Application#addValidator(java.lang.String,
     *      java.lang.String)
     */
    public void addValidator(String arg0, String arg1) {
    }

    /**
     * @see javax.faces.application.Application#createValidator(java.lang.String)
     */
    public Validator createValidator(String arg0) throws FacesException {
        return null;
    }

    /**
     * @see javax.faces.application.Application#getValidatorIds()
     */
    public Iterator getValidatorIds() {
        return null;
    }

    /**
     * @see javax.faces.application.Application#createValueBinding(java.lang.String)
     */
    public ValueBinding createValueBinding(String expr) throws ReferenceSyntaxException {
        return (new MockValueBinding(this, expr));
    }

    /*
     * This mock method binding merely returns the provided binding expression from the invoke() and getExpressionString() methods.
     * @author sgoldstein
     */
    private class MockMethodBinding extends MethodBinding {

        private String bindingExpression;

        /**
         * Constructor
         * @param bindingExpression binding expression
         */
        public MockMethodBinding(String bindingExpression) {
            super();
            this.bindingExpression = bindingExpression;
        }

        /**
         * @see javax.faces.el.MethodBinding#getType(javax.faces.context.FacesContext)
         */
        public Class getType(FacesContext context) throws MethodNotFoundException {
            return null;
        }

        /**
         * @see javax.faces.el.MethodBinding#invoke(javax.faces.context.FacesContext,
         *      java.lang.Object[])
         */
        public Object invoke(FacesContext context, Object[] params) throws EvaluationException, MethodNotFoundException {
            return bindingExpression;
        }

        /**
         * @see javax.faces.el.MethodBinding#getExpressionString()
         */
        public String getExpressionString() {
            return bindingExpression;
        }
    }
}