/*
 * Created on May 13, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt;

import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.report.presentation.aggregation.IFragment;
import org.eclipse.birt.report.resource.BirtResources;
import org.eclipse.birt.report.servlet.ViewerServlet;
import org.eclipse.birt.report.utility.ParameterAccessor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author rlin
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java
 *          /main/com/nextlabs/destiny/inquirycenter/report/birt/
 *          ConnectionPoolViewerServlet.java#2 $
 */

public class ConnectionPoolViewerServlet extends ViewerServlet {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(ConnectionPoolViewerServlet.class);

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
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		try {
			IComponentManager componentManager = ComponentManagerFactory
					.getComponentManager();
			IHibernateRepository activityDataSrc = (IHibernateRepository) componentManager
					.getComponent(DestinyRepository.ACTIVITY_REPOSITORY
							.getName());
			if (activityDataSrc == null) {
				throw new RuntimeException("Data source "
						+ DestinyRepository.ACTIVITY_REPOSITORY
						+ " is not correctly setup for the DABS component.");
			}
			Session session;
			session = activityDataSrc.getSession();
			Connection testConn = session.connection();
			arg0
					.setAttribute("AppContextKey",
							"com.nextlabs.destiny.inquirycenter.report.birt.ReportOdaJdbcDriver");
			arg0.setAttribute("AppContextValue", testConn);
			if (arg1 instanceof HttpServletResponse) {
				DestinyHttpServletResponse destinyHttpServletResponse = new DestinyHttpServletResponse(
						(HttpServletResponse) arg1);
				super.service(arg0, destinyHttpServletResponse);
				destinyHttpServletResponse.flushBuffer();
			} else {
				super.service(arg0, arg1);
			}

			testConn.close();
			session.close();
		} catch (HibernateException e) {
			log.error(e.getMessage(), e);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}
}
