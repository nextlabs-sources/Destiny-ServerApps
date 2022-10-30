/*
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

import java.util.List;

import net.sf.hibernate.HibernateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.AttributeColumnMappingDAO;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.AttributeColumnMappingDAOImpl;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.AttributeColumnMappingDO;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.SavedReportDO;
import com.nextlabs.destiny.inquirycenter.SharedUtils;
import com.nextlabs.destiny.inquirycenter.report.OrderByModel;
import com.nextlabs.destiny.inquirycenter.report.ReportCriteriaJSONModel;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.ReportDataManager;
import com.nextlabs.report.datagen.ResultData;

/**
 * @author klee
 *
 */
public class ReportingService {

	private static AttributeColumnMappingDAO colMappingDAO = new AttributeColumnMappingDAOImpl();
	
	static final Log LOG = LogFactory.getLog(ReportingService.class.getName());

	private static final String COLUMN_ID = "id";
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 * @throws  
	 */
	public static JSONArray getAllColumnMappingJSON() throws JSONException
	{
		List<AttributeColumnMappingDO> colMappings = null;
		JSONArray arr = new JSONArray();
		
		try {
			colMappings = colMappingDAO.getAll();
		} catch (HibernateException e) {
			LOG.error("Operation failed", e);
			return arr;
		}
		
		for (AttributeColumnMappingDO colMap : colMappings)
		{
			arr.put(SharedUtils.getAttributeColumnMappingJSON(colMap));
		}
		
		return arr;
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 * @throws  
	 */
	public static String getColumnMappingJSON(String sType) throws JSONException
	{
		List<AttributeColumnMappingDO> colMappings;
		JSONArray arr = new JSONArray();
		
		try {
			colMappings = colMappingDAO.getAll(sType);
		} catch (HibernateException e) {
			LOG.error("Operation failed", e);
			return arr.toString();
		}
		colMappings.removeIf(attributeColumnMappingDO ->
				"ce::destinytype".equals(attributeColumnMappingDO.getAttributeName()));
		
		for (AttributeColumnMappingDO colMap : colMappings)
		{
			arr.put(SharedUtils.getAttributeColumnMappingJSON(colMap));
		}
		
		return arr.toString();
	}
	
	/**
	 * 
	 * @param savedReport
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getSavedReportResultsJSON(SavedReportDO savedReport) throws Exception
	{
		ResultData data = executeSavedReport(savedReport);
		
		JSONObject jsonResults = convertToJSON(savedReport, data);
		
		return jsonResults;
	}
	
	/**
	 * 
	 * @param savedReport
	 * @return
	 * @throws Exception
	 */
	private static ResultData executeSavedReport(SavedReportDO savedReport) throws Exception
	{
		ReportDataManager mgr = new ReportDataManager();		
		ResultData results = mgr.getPADetailsTableDataV2(savedReport.getCriteriaJSON());	
		return results;
	}
	
	/**
	 * convert search results in {@link ResultData} object to JSON format
	 * @param results
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject convertToJSON(SavedReportDO savedReport, ResultData results) throws JSONException
	{
		JSONObject jsonResults = new JSONObject();
		ReportCriteriaJSONModel jModel = new ReportCriteriaJSONModel(savedReport.getCriteriaJSON());
		
		String title = savedReport.getTitle();
		
		String fromDate = jModel.getGeneralValue("start_date");
		String toDate = jModel.getGeneralValue("end_date");
		int maxRows = jModel.getMaxRows();
		String groupingMode = jModel.getGroupingMode();
		String reportType = jModel.getSavedInfoDetails("report_type");
		
		String colName  = "";
		String sortOrder  = "";
		if(!jModel.getOrderByList().isEmpty()) {
			OrderByModel orderBy = jModel.getOrderByList().get(0);
			colName = orderBy.getColumnName();
			sortOrder = orderBy.getSortOrder();
		}
		
		jsonResults.put("report_title", title);
		jsonResults.put("report_type", reportType);
		jsonResults.put("from_date", fromDate);
		jsonResults.put("to_date", toDate);
		jsonResults.put("grouping_mode", groupingMode);
		jsonResults.put("col_name", colName);
		jsonResults.put("sort_order", sortOrder);
		jsonResults.put("max_rows", maxRows);
		
		if ( results == null)
		{
			jsonResults.put("header", new JSONArray());			
			jsonResults.put("data", new JSONArray());
			
			return jsonResults;
		}
		
		JSONArray header = new JSONArray();

		JSONArray data = new JSONArray();
		
		int numberOfCols = results.getRowCount();
		
		int numberOfRecords = results.size();
		
		/*
		 * find index of the id column
		 */
		
		int idColumnIndex = -1;
		
		for (int i = 0; i < numberOfCols ; i++)
		{
			String columnName = (results.getName(i) == null) ? "": results.getName(i).toLowerCase();
			if (columnName.equals(COLUMN_ID))
			{
				idColumnIndex = i;
				break;
			}
		}
		
		
		for (int i = 0; i < numberOfRecords; i++)
		{
			JSONObject record = new JSONObject();
			for (int j = 0; j < numberOfCols; j++)
			{
				String columnName = results.getName(j) == null ? "": results.getName(j).toLowerCase();
				
				/*
				 * do not send id column to client.
				 */
				
				if (j == idColumnIndex)
				{
					continue;
				}
				
				if (i == 0)
				{
					JSONObject col = new JSONObject();
					col.put("sTitle", columnName);
					col.put("mData", columnName);
					header.put(col);
				}
				Object cellValue = results.get(i, j);
				/*
				 * if record does not contain value for this property insert empty string
				 * so that properties in header and record match
				 */
				record.put(columnName, cellValue == null ? "" : cellValue);
			}
			data.put(record);
		}
		
		jsonResults.put("header", header);
		
		jsonResults.put("data", data);
		
		return jsonResults;
	}

		
}
