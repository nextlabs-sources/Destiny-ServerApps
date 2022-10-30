/*
 * Created on Feb 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.tags;

import javax.faces.context.FacesContext;

import com.bluejungle.destiny.webui.jsfmock.MockApplication;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.destiny.webui.jsfmock.MockViewHandler;

import junit.framework.TestCase;

/**
 * This is the base test class for JSF related tests. It sets up a dummy JSF
 * context to allows UI components and tags to be tested properly.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/tags/BaseJSFTest.java#1 $
 */

public abstract class BaseJSFTest extends TestCase {

    protected MockFacesContext facesContext;
    protected MockApplication application;

    /**
     * Constructor
     */
    public BaseJSFTest() {
        super();
    }

    /**
     * Constructor
     * 
     * @param testName
     *            name of the test
     */
    public BaseJSFTest(String testName) {
        super(testName);
    }

    /**
     * Sets up a dummy JSF context. Only dummy objects that are needed are
     * instantiated here.
     * @throws Exception
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        this.facesContext = new MockFacesContext();
        this.application = new MockApplication();
        this.facesContext.setApplication(this.application);
        this.application.setViewHandler(new MockViewHandler ());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws NullPointerException {
        MockFacesContext context = (MockFacesContext) FacesContext.getCurrentInstance();
        context.setApplication(null);
    }
}