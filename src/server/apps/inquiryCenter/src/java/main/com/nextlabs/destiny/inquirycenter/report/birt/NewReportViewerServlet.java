/*
 * Created on Feb 12, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.report.presentation.aggregation.IFragment;
import org.eclipse.birt.report.resource.BirtResources;
import org.eclipse.birt.report.servlet.ViewerServlet;
import org.eclipse.birt.report.utility.ParameterAccessor;
import org.richfaces.json.JSONException;

import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.nextlabs.destiny.inquirycenter.monitor.service.ReportingService;

/**
 * @author klee
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java
 *          /main/com/nextlabs/destiny/inquirycenter/report/birt/
 *          NewReportViewerServlet.java#1 $
 */

public class NewReportViewerServlet extends ViewerServlet {
	private static final long serialVersionUID = 1L;
	
	public static final Log LOG = LogFactory.getLog(NewReportViewerServlet.class);

	/**
	 * Viewer fragment references.
	 */
	protected IFragment viewer = null;
	protected IFragment run = null;

	/**
	 * Check version.
	 * 
	 * @return
	 */
	public static boolean isOpenSource() {
		return openSource;
	}

	/**
	 * Servlet init.
	 * 
	 * @param config
	 * @exception ServletException
	 * @return
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ParameterAccessor.initParameters(config);
		BirtResources.setLocale(ParameterAccessor.getWebAppLocale());
		__init(config);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse)
	 */
	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		try {
				
				String sAction = request.getParameter("action");
						
				if (sAction!=null && sAction.equals("GET_MAPPING_DATA")){
					try {
						getMappingData(request, response);
						
						return;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						LOG.error(e.getMessage(), e);
					}
				}
				else {
											
						IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
						IHibernateRepository activityDataSrc = (IHibernateRepository) componentManager.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());
						
						if (activityDataSrc == null) {
							throw new RuntimeException("Data source "
									+ DestinyRepository.ACTIVITY_REPOSITORY
									+ " is not correctly setup for the DABS component.");
						}
						
						
						Session session;
						session = activityDataSrc.getSession();
						Connection testConn = session.connection();
						request.setAttribute("AppContextKey","com.nextlabs.destiny.inquirycenter.report.birt.ReportOdaJdbcDriver");
						request.setAttribute("AppContextValue", testConn);
						if (response instanceof HttpServletResponse) {
							DestinyHttpServletResponse destinyHttpServletResponse = new DestinyHttpServletResponse((HttpServletResponse) response);
							super.service(request, destinyHttpServletResponse);
							destinyHttpServletResponse.flushBuffer();
							
						} else {	
							super.service(request, response);
						}

						testConn.close();
						session.close();
						
				}
			
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
		} catch (SQLException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	
	private void getMappingData(ServletRequest request,
			ServletResponse response) throws IOException, JSONException {
		
		request.setCharacterEncoding("utf8");
        response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.print(ReportingService.getColumnMappingJSON(request.getParameter("TYPE")));
		pw.flush();
		pw.close();

	}
}
