/*
 * Created on Jul 25, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.datapickers;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;

import com.sun.faces.el.ValueBindingImpl;

/**
 * Mock application object for value binding evaluation
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/destiny/inquirycenter/report/defaultimpl/datapickers/MockApplication.java#1 $
 */

public class MockApplication extends com.bluejungle.destiny.webui.jsfmock.MockApplication {

    private ValueBinding vb = new MockValueBinding();

    /**
     * @see javax.faces.application.Application#createValueBinding(java.lang.String)
     */
    public ValueBinding createValueBinding(String arg0) throws ReferenceSyntaxException {
        return vb;
    }

    /**
     * @author ihanen
     */
    private class MockValueBinding extends ValueBindingImpl {

        private Object report = new MockReportImpl();

        /**
         * @see javax.faces.el.ValueBinding#getValue(javax.faces.context.FacesContext)
         */
        public Object getValue(FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
            return report;
        }

        /**
         * @see javax.faces.el.ValueBinding#setValue(javax.faces.context.FacesContext,
         *      java.lang.Object)
         */
        public void setValue(FacesContext arg0, Object arg1) throws EvaluationException, PropertyNotFoundException {
        }

        /**
         * @see javax.faces.el.ValueBinding#isReadOnly(javax.faces.context.FacesContext)
         */
        public boolean isReadOnly(FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
            return false;
        }

        /**
         * @see javax.faces.el.ValueBinding#getType(javax.faces.context.FacesContext)
         */
        public Class getType(FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
            return null;
        }

    }
}