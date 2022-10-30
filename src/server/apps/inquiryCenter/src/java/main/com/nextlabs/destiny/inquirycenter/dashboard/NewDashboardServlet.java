/*
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.dashboard;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.SavedReportDO;
import com.nextlabs.destiny.inquirycenter.SharedUtils;
import com.nextlabs.destiny.inquirycenter.exception.ReportingException;
import com.nextlabs.destiny.inquirycenter.monitor.service.ReportingService;
import com.nextlabs.destiny.inquirycenter.savedreport.service.SavedReportService;
import com.nextlabs.destiny.inquirycenter.savedreport.service.impl.SavedReportServiceImpl;

/**
 * @author nnallagatla
 *
 */
public class NewDashboardServlet extends HttpServlet {

	public static final Log LOG = LogFactory.getLog(NewDashboardServlet.class);
	
	private SavedReportService savedReportService;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * just return the parameter content for now
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void doProcess(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		String action = request.getParameter("action");
		
		LOG.info("received request with action: " + action);
		
		if (action.equalsIgnoreCase("ECHO"))
		{
			echo(request, response);
		} 
		else if (action.equalsIgnoreCase("SAVED_REPORT"))
		{
			getSavedReportResults(request, response);
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void echo(HttpServletRequest request,
	HttpServletResponse response) throws IOException 
	{
		PrintWriter writer = response.getWriter();
		writer.write(request.getParameter("content"));
		writer.flush();			
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws  
	 */
	private void getSavedReportResults(HttpServletRequest request,
	HttpServletResponse response) throws IOException 
	{
		
		long reportId = -1;
		
		String id = request.getParameter("id");
		
		if (id != null && !id.isEmpty())
		{
			try
			{
				reportId = Long.parseLong(id.trim());
			}
			catch (NumberFormatException e)
			{
				LOG.error("reportId is not a number", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
		}
		
		SavedReportDO report = null;
		try
		{
			report = getSavedReportService().lookup(reportId);	
		}
		catch (ReportingException e)
		{
			LOG.error("Error looking up saved report", e);
		}
		
		if ( report == null)
		{
			LOG.error("Saved Report not found");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		try {
			
			LOG.debug("---------------------------------Executing saved report " + report.getId() + " for dashboard-----------------------------------");
			
			LOG.debug(report.getCriteriaJSON());
			
			JSONObject jsonResults = ReportingService.getSavedReportResultsJSON(report);
			
			SharedUtils.writeJSONResponse(request, response, jsonResults);
			LOG.debug("---------------------------------DONE Executing saved report " + report.getId() + " for dashboard-----------------------------------");
		} catch (JSONException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOG.error("JSON Error encountered ,", e);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOG.error("Error encountered ,", e);
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	private SavedReportService getSavedReportService()
	{
		/*
		 * Initialize saved report only once. double locked checking
		 */
		if (savedReportService == null)
		{
			synchronized (this)
			{
				if (savedReportService == null)
				{
					savedReportService = new SavedReportServiceImpl();
				}
			}
		}
		return savedReportService;
	}
}
