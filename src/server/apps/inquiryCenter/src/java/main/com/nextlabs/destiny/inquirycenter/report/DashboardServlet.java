/*
 * Created on Apr 18, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report;

import com.bluejungle.destiny.container.dcc.DCCResourceLocators;
import com.bluejungle.destiny.container.dcc.INamedResourceLocator;
import com.bluejungle.destiny.container.dcc.ServerRelativeFolders;
import com.bluejungle.destiny.inquirycenter.environment.InquiryCenterResourceLocators;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.environment.webapp.WebAppResourceLocatorImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/DashboardServlet.java#1 $
 */

public class DashboardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(DashboardServlet.class);
	/**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
        INamedResourceLocator serverResourceLocator = (INamedResourceLocator) compMgr.getComponent(DCCResourceLocators.SERVER_HOME_RESOURCE_LOCATOR);
        String reportOutputDirRelativePath = ServerRelativeFolders.REPORTS_FOLDER.getPath();
        String fileParam = req.getParameter("file");
        
        if ((fileParam != null) && ((fileParam.indexOf("..\\") != -1) || (fileParam.indexOf("../") != -1)))
        	return;        		
        
        if (!fileParam.endsWith(".png")){
            InputStream dashboardInput = serverResourceLocator.getResourceAsStream(reportOutputDirRelativePath + "\\" + fileParam);
            if (dashboardInput == null){
                WebAppResourceLocatorImpl webAppResourceLocator = (WebAppResourceLocatorImpl)compMgr.getComponent(InquiryCenterResourceLocators.WEB_APP_RESOURCE_LOCATOR_COMP_NAME);
                dashboardInput = webAppResourceLocator.getResourceAsStream("/content/dashboard.html");
            }
            InputStreamReader reader = new InputStreamReader(dashboardInput);
            BufferedReader buff = new BufferedReader(reader);
            PrintWriter writer = res.getWriter();
            String content = "";
            while ((content = buff.readLine()) != null){
                writer.println(content);
            }
        } else {
            res.getOutputStream().write(readFile(serverResourceLocator.getResourceAsStream(reportOutputDirRelativePath + "\\" + fileParam)));
            res.setContentType("image/png");    
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }
    
    public byte[] readFile(InputStream input){
        byte[] bytes =null;
        if(input!=null){
            try{
                InputStream is = input;
                long length = input.available();

                bytes = new byte[(int)length];
                int offset = 0;
                int numRead = 0;
                while ( (offset < bytes.length) && ((numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) ) {
                    offset += numRead;
                }

                is.close();
            }catch(IOException e){
                log.error(e.getMessage(), e);
            }
        }
        return bytes;
    }
}
