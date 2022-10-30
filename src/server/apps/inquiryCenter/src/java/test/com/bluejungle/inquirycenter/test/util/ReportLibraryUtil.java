/*
 * Created on Apr 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.inquirycenter.test.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.FileProvider;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoginInfo;
import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoginMgr;
import com.bluejungle.destiny.appframework.appsecurity.loginmgr.LoginException;
import com.bluejungle.destiny.appframework.appsecurity.loginmgr.remote.RemoteLoginManager;
import com.bluejungle.destiny.bindings.report.v1.ComponentLookupIFBindingStub;
import com.bluejungle.destiny.bindings.report.v1.ReportExecutionIFBindingStub;
import com.bluejungle.destiny.bindings.report.v1.ReportLibraryIFBindingStub;
import com.bluejungle.destiny.interfaces.report.v1.ComponentLookupIF;
import com.bluejungle.destiny.interfaces.report.v1.ReportExecutionIF;
import com.bluejungle.destiny.interfaces.report.v1.ReportLibraryIF;
import com.bluejungle.destiny.types.basic_faults.v1.AccessDeniedFault;
import com.bluejungle.destiny.types.basic_faults.v1.ServiceNotReadyFault;
import com.bluejungle.destiny.types.basic_faults.v1.UnknownEntryFault;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportSummaryType;
import com.bluejungle.destiny.types.report.v1.ReportTargetType;
import com.bluejungle.destiny.webui.jsfmock.MockExternalContext;
import com.bluejungle.destiny.webui.jsfmock.MockFacesContext;
import com.bluejungle.destiny.webui.servletmock.MockHttpSession;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.LifestyleType;
import com.mockobjects.servlet.MockHttpServletRequest;

/**
 * This is a utility class purely for debugging. This code is not going to
 * production, and should not even be compiled. The main use is to insert /
 * delete reports data from the database.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/test/com/bluejungle/inquirycenter/test/util/ReportLibraryUtil.java#2 $
 */

public class ReportLibraryUtil {

    private static final String CLIENT_CONFIG_FILE = "c:\\builds\\destiny\\server\\apps\\inquiryCenter\\WEB-INF\\client-config.wsdd";
    private static final String SERVICE_LOCATION = "http://localhost:8081/dac/services/SecureSessionService";

    /**
     * Returns a new instance of a component lookup client.
     * 
     * @return a component lookup client
     */
    protected ComponentLookupIF getComponentLookup() throws AxisFault {
        ComponentLookupIF lookup = null;
        try {
            URL location = new URL("http://localhost:8081/dac/services/ComponentLookup");
            lookup = new ComponentLookupIFBindingStub(location, getNewService());
        } catch (MalformedURLException exception) {
            // Shouldn't happen
            IllegalStateException exceptionToThrow = new IllegalStateException();
            exceptionToThrow.initCause(exception);
            throw exceptionToThrow;
        }
        return lookup;
    }

    /**
     * Returns a new service client object
     * 
     * @return a new service client object
     */
    private Service getNewService() {
        return new org.apache.axis.client.Service(new FileProvider(CLIENT_CONFIG_FILE));
    }

    /**
     * Returns a new instance of the report execution client
     * 
     * @return a report execution service client
     */
    protected ReportExecutionIF getReportExecution() throws AxisFault {
        ReportExecutionIFBindingStub execution = null;

        try {
            URL location = new URL("http://localhost:8081/dac/services/ReportExecution");
            execution = new ReportExecutionIFBindingStub(location, getNewService());
        } catch (MalformedURLException exception) {
            //  Shouldn't happen
            IllegalStateException exceptionToThrow = new IllegalStateException();
            exceptionToThrow.initCause(exception);
            throw exceptionToThrow;
        }

        return execution;
    }

    /**
     * Returns a new instance of the report library client
     * 
     * @return a report library service client
     */
    protected ReportLibraryIF getReportLibrary() throws AxisFault {
        ReportLibraryIF library = null;

        try {
            URL location = new URL("http://localhost:8081/dac/services/ReportLibrary");
            library = new ReportLibraryIFBindingStub(location, getNewService());
        } catch (MalformedURLException exception) {
            //  Shouldn't happen
            IllegalStateException exceptionToThrow = new IllegalStateException();
            exceptionToThrow.initCause(exception);
            throw exceptionToThrow;

        }

        return library;
    }

    /**
     * Logs in
     * 
     * @throws LoginException
     */
    protected void login() throws LoginException {

        MockExternalContext mockExternalContext = new MockExternalContext("/foo");
        MockHttpServletRequest request = new MockHttpServletRequest();
        mockExternalContext.setRequest(request);
        MockHttpSession mockSession = new com.bluejungle.destiny.webui.servletmock.MockHttpSession();
        request.setSession(mockSession);
        MockFacesContext mockFacesContext = new MockFacesContext(mockExternalContext);

        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();

        //Sets the remote login manager to test
        HashMapConfiguration componentConfig = new HashMapConfiguration();
        componentConfig.setProperty(RemoteLoginManager.SECURE_SESSION_SERVICE_ENDPOINT_PROP_NAME, SERVICE_LOCATION);
        ComponentInfo componentInfo = new ComponentInfo(ILoginMgr.COMP_NAME, RemoteLoginManager.class.getName(), ILoginMgr.class.getName(), LifestyleType.SINGLETON_TYPE, componentConfig);
        RemoteLoginManager loginMgr = (RemoteLoginManager) compMgr.getComponent(componentInfo);
        //Simulates a user login
        loginMgr.login(new MockLoginInfo("jimmy.carter", "jimmy.carter"));
    }

    /**
     * Inserts sample report data in the database
     * 
     * @param args
     */
    public static void main(String[] args) {
        ReportLibraryUtil util = new ReportLibraryUtil();
        try {
            util.login();
            ReportLibraryIF library = util.getReportLibrary();

            for (int i = 0; i < 30; i++) {
                Report report = new Report();
                report.setTarget(ReportTargetType.PolicyEvents);
                report.setSummaryType(ReportSummaryType.None);
                report.setDescription("Long Report Description " + i);
                report.setTitle("Long Report Title " + i);
                if (i < 20) {
                    report.setShared(false);
                } else {
                    report.setShared(true);
                }
                long start = System.currentTimeMillis();
                library.insertReport(report);
                long end = System.currentTimeMillis();
                System.out.println("Report insertion done in : " + (end - start) + " ms");
            }
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (UnknownEntryFault e) {
            e.printStackTrace();
        } catch (AccessDeniedFault e) {
            e.printStackTrace();
        } catch (ServiceNotReadyFault e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dummy login info class
     * 
     * @author ihanen
     */
    protected class MockLoginInfo implements ILoginInfo {

        private String userName;
        private String password;

        public MockLoginInfo(String name, String pass) {
            this.userName = name;
            this.password = pass;
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.loginmgr.ILoginInfo#getApplicationName()
         */
        public String getApplicationName() {
            return null;
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.loginmgr.ILoginInfo#getUserName()
         */
        public String getUserName() {
            return this.userName;
        }

        /**
         * @see com.bluejungle.destiny.webui.framework.loginmgr.ILoginInfo#getPassword()
         */
        public String getPassword() {
            return this.password;
        }
    }

}