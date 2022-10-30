/*
 * Created on Feb 9, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.loginmgr;

import java.util.Iterator;

import javax.faces.context.FacesContext;

import junit.framework.TestCase;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.destiny.webui.jsfmock.MockApplication;
import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.destiny.webui.jsfmock.MockViewHandler;
import com.bluejungle.destiny.webui.servletmock.MockHttpSession;
import com.mockobjects.servlet.MockHttpServletRequest;

/**
 * This is the web login manager test
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/test/com/bluejungle/destiny/webui/framework/loginmgr/WebLoginMgrTest.java#3 $
 */

public class WebLoginMgrTest extends TestCase {

    /**
     * Constructor
     */
    public WebLoginMgrTest() {
        super();
    }

    /**
     * Constructor
     * 
     * @param testName
     *            name of the test
     */
    public WebLoginMgrTest(String testName) {
        super(testName);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        MockViewHandler mockViewHandler = new MockViewHandler();
        MockApplication mockApplication = new MockApplication();
        mockApplication.setViewHandler(mockViewHandler);

        MockHttpSession mockSession = new MockHttpSession();
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setSession(mockSession);
        
        MockExternalContext mockExternalContext = new MockExternalContext("/foo");
        mockExternalContext.setRequest(mockRequest);
        
        MockFacesContext facesContext = new MockFacesContext();
        facesContext.setApplication(mockApplication);
        facesContext.setExternalContext(mockExternalContext);
        
    }

    /**
     * This test verifies that the class implements the right interfaces
     */
    public void testClass() {
        WebLoginMgrImpl wlm = new WebLoginMgrImpl();
        assertTrue("WebLoginMgrImpl implements the right interface", wlm instanceof IWebLoginMgr);
    }

    /**
     * This test verifies that the web login manager denies the login before the
     * real login manager is instantiated in the container.
     */
    public void testEarlyLogin() {
        Iterator it = FacesContext.getCurrentInstance().getMessages();
        assertFalse("Initially, no messages should be in the context", it.hasNext());
        WebLoginMgrImpl wlm = new WebLoginMgrImpl();
        final String outcome = wlm.performLogin();
        assertEquals("Early login should always fail", IWebLoginMgr.LOGIN_FAILURE, outcome);
        it = FacesContext.getCurrentInstance().getMessages();
        assertTrue("One error message should have been added upon login failure", it.hasNext());
    }

    /**
     * Test logout functionality
     */
    public void testPerformLogout() {
        AppContext appContext = AppContext.getContext();
        appContext.setRemoteUser(new ILoggedInUser() {

            /**
             * @see com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser#isPasswordModifiable()
             */
            public boolean isPasswordModifiable() {
                // TODO Auto-generated method stub
                return false;
            }

            /**
             * @see com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser#getPrincipalId()
             */
            public Long getPrincipalId() {
                // TODO Auto-generated method stub
                return null;
            }

            /**
             * @see com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser#getPrincipalName()
             */
            public String getPrincipalName() {
                // TODO Auto-generated method stub
                return null;
            }

            /**
             * @see com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser#getUsername()
             */
            public String getUsername() {
                // TODO Auto-generated method stub
                return null;
            }
            
        });
        
        assertTrue("Ensure user is logged on.", appContext.isLoggedIn());
        WebLoginMgrImpl loginManagerToTest = new WebLoginMgrImpl();
        loginManagerToTest.performLogout();
        
        assertTrue("Ensure user if logged out.", !appContext.isLoggedIn());
    }
}