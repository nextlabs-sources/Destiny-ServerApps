/*
 * Created on Jun 4, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report;

import static com.nextlabs.destiny.inquirycenter.SharedUtils.getFormatedDate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.nextlabs.destiny.configclient.Config;
import com.nextlabs.destiny.configclient.ConfigClient;
import com.nextlabs.destiny.inquirycenter.DateRangeSelection;

/**
 * <p>
 * Model object to represent ReportCriteriaJSONModel and it extended
 * functonality.
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public class ReportCriteriaJSONModel {

	private JSONObject obj;
	private JSONObject filters;
	private JSONObject general;
	private JSONObject userCriteria;
	private JSONObject resourceCriteria;
	private JSONObject policyCriteria;
	private JSONObject otherCriteria;
	private JSONArray headers;
	private JSONArray orderByArr;
	private JSONArray aggregators;
	private JSONArray groupBy;
	private JSONObject saveInfo;
	
	public ReportCriteriaJSONModel(String jsonStr)
			throws JSONException {
		this.obj = new JSONObject(jsonStr);
		this.filters = obj.getJSONObject("filters");
		this.general = filters.getJSONObject("general");
		this.userCriteria = filters.getJSONObject("user_criteria");
		this.resourceCriteria = filters.getJSONObject("resource_criteria");
		this.policyCriteria = filters.getJSONObject("policy_criteria");
		this.otherCriteria = filters.getJSONObject("other_criteria");
		this.headers = obj.getJSONArray("header");
		this.orderByArr = obj.getJSONArray("order_by");
		this.aggregators = obj.getJSONArray("aggregators");
		this.groupBy = obj.getJSONArray("group_by");
		this.saveInfo = obj.getJSONObject("save_info");
		
		convertRelativeDatesToAbsolute(general, null);
		 
		if(!ConfigClient.get("use.past.data.for.monitoring").toBoolean() && obj.has("monitor_created_date")) {
			Timestamp startDate = Timestamp.valueOf(getGeneralValue("start_date"));
			Timestamp createdDate = Timestamp.valueOf(obj.getString("monitor_created_date"));
			
			if (startDate.getTime() < createdDate.getTime()) {
				String beginDate = getFormatedDate(new Date(createdDate.getTime()));
				general.put("start_date", beginDate);
			}
		}
	}

	
	/**
	 * converts relative dates into absolute
	 * @param general
	 * @throws JSONException
	 */
	public static void convertRelativeDatesToAbsolute(JSONObject general, Calendar relativeToDate) throws JSONException {
		
		String beginDate = null, endDate = null;

		int rangeOption = 0;
		int n = -1;
		
		if (general.getString("date_mode") != null && general.getString("date_mode").equalsIgnoreCase("fixed"))
		{
			return;
		}
		else if (general.getString("date_mode") != null && general.getString("date_mode").equalsIgnoreCase("relative"))
		{
			String reportRelativeRange = general.getString("window_mode");
			
			if (reportRelativeRange == null)
			{
				rangeOption = DateRangeSelection.CURRENT_WEEK;
			}
			else if (reportRelativeRange.equalsIgnoreCase("current_week"))
			{
				rangeOption = DateRangeSelection.CURRENT_WEEK;
			}
			else if (reportRelativeRange.equalsIgnoreCase("current_month"))
			{
				rangeOption = DateRangeSelection.CURRENT_MONTH;
			}
			else if (reportRelativeRange.equalsIgnoreCase("current_quarter"))
			{
				rangeOption = DateRangeSelection.CURRENT_QUARTER;
			}
			else if (reportRelativeRange.equalsIgnoreCase("current_year"))
			{
				rangeOption = DateRangeSelection.CURRENT_YEAR;
			}
			else if (reportRelativeRange.equalsIgnoreCase("last_week"))
			{
				rangeOption = DateRangeSelection.PRIOR_WEEK;
			}
			else if (reportRelativeRange.equalsIgnoreCase("last_month"))
			{
				rangeOption = DateRangeSelection.PRIOR_MONTH;
			}
			else if (reportRelativeRange.equalsIgnoreCase("last_quarter"))
			{
				rangeOption = DateRangeSelection.PRIOR_QUARTER;
			}
			else if (reportRelativeRange.equalsIgnoreCase("last_year"))
			{
				rangeOption = DateRangeSelection.PRIOR_YEAR;
			}
			else if (reportRelativeRange.equalsIgnoreCase("today"))
			{
				rangeOption = DateRangeSelection.TODAY;
			}
			else if (reportRelativeRange.equalsIgnoreCase("yesterday"))
			{
				rangeOption = DateRangeSelection.YESTERDAY;
			}
			else if (reportRelativeRange.equalsIgnoreCase("this_hour"))
			{
				rangeOption = DateRangeSelection.THIS_HOUR;
			}
			else if (reportRelativeRange.equalsIgnoreCase("last_hour"))
			{
				rangeOption = DateRangeSelection.PRIOR_HOUR;
			}
			else if (reportRelativeRange.toLowerCase().startsWith("last")) 
			{
				String[] temp = reportRelativeRange.split("_");
				
				if (temp.length == 3)
				{
					n = Integer.parseInt(temp[1]);
					rangeOption = temp[2].equalsIgnoreCase("days") ? DateRangeSelection.LAST_N_DAYS : DateRangeSelection.LAST_N_HOURS;
				}
			}
			
			DateRangeSelection range = null; 
			if ( n == -1)
			{
				if (relativeToDate == null)
				{
					range = new DateRangeSelection(rangeOption);
				}
				else
				{
					range = new DateRangeSelection(rangeOption, relativeToDate);
				}
			}
			else
			{
				if (relativeToDate == null)
				{
					range = new DateRangeSelection(rangeOption, n);
				}
				else
				{
					range = new DateRangeSelection(rangeOption, relativeToDate, n);
				}
			}
			
			beginDate = getFormatedDate(range.getBeginDate().getTime());
			endDate = getFormatedDate(range.getEndDate().getTime());
			
			general.put("start_date", beginDate);
			general.put("end_date", endDate);
		}
	}
	

	public String getGeneralValue(String key) throws JSONException {
		return general.getString(key);
	}

	public List<CriteriaFieldModel> getGeneralFields() throws JSONException {
		return getFields(general);
	}

	public CriteriaFieldModel getGeneralDesicionField() throws JSONException {
		CriteriaFieldModel field = getFieldByName("decision", general);
		return field;
	}

	public CriteriaFieldModel getGeneralActionField() throws JSONException {

		CriteriaFieldModel field = getFieldByName("action", general);
		if (field.getValues().isEmpty()) {
			field.setMultiValue(false);
			field.setValue("%");
			field.getValues().add("%");
		}
		return field;
	}

	public CriteriaFieldModel getGeneralLogLevelField() throws JSONException {
		CriteriaFieldModel field = getFieldByName("log_level", general);
		return field;
	}

	private CriteriaFieldModel getFieldByName(String fieldName,
			JSONObject jsonObject) throws JSONException {
		JSONArray jArray = jsonObject.getJSONArray("fields");
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject fld = jArray.getJSONObject(i);
			String name = fld.getString("name");

			if (fieldName.equals(name)) {
				CriteriaFieldModel fieldModel = createFieldModel(fld);
				return fieldModel;
			}
		}
		return null;
	}

	public CriteriaFieldModel getUserCriteriaLookUpField() throws JSONException {
		JSONObject userLookupField = userCriteria
				.getJSONObject("look_up_field");

		List<String> values = getValues(userLookupField.getJSONArray("value"));
		CriteriaFieldModel fieldModel = createFieldModel(userLookupField);
		handleSpecificDetails("Any User",values, fieldModel);
		return fieldModel;
	}


	private void handleSpecificDetails(String defaultValue ,List<String> values,
			CriteriaFieldModel fieldModel) {
		if (values.isEmpty() 
				|| defaultValue.equalsIgnoreCase(values.get(0))) {
			fieldModel.setMultiValue(false);
			fieldModel.setOperator("like");
			fieldModel.getValues().clear();
			fieldModel.getValues().add("%");
		} else if (values.size() == 1) {
			fieldModel.setMultiValue(false);
			fieldModel.setOperator("like");
		}
	}

	public List<CriteriaFieldModel> getUserCriteriaFields() throws JSONException {
		return getFields(userCriteria);
	}

	public CriteriaFieldModel getResourceCriteriaLookUpField() throws JSONException {
		JSONObject resourceLookupField = resourceCriteria
				.getJSONObject("look_up_field");

		List<String> values = getValues(resourceLookupField
				.getJSONArray("value"));
		CriteriaFieldModel fieldModel = createFieldModel(resourceLookupField);
		handleSpecificDetails("Any Resource", values, fieldModel);
		return fieldModel;
	}

	public List<CriteriaFieldModel> getResourceCriteriaFields()
			throws Exception {
		return getFields(resourceCriteria);
	}

	public CriteriaFieldModel getPolicyCriteriaLookUpField() throws JSONException {
		JSONObject policyLookupField = policyCriteria
				.getJSONObject("look_up_field");

		List<String> values = getValues(policyLookupField.getJSONArray("value"));
		CriteriaFieldModel fieldModel = createFieldModel(policyLookupField);
		handleSpecificDetails("Any Policy", values, fieldModel);
		return fieldModel;
	}

	public List<CriteriaFieldModel> getPolicyCriteriaFields() throws JSONException {
		return getFields(policyCriteria);
	}

	public List<CriteriaFieldModel> getOtherCriteriaFields() throws JSONException {
		return getFields(otherCriteria);
	}

	public List<String> getColumnHeaders() throws JSONException {
		List<String> headerArr = getValues(headers);
		return headerArr;
	}

	public int getMaxRows() throws JSONException {
		return obj.getInt("max_rows");
	}
	
	public int getOffset() throws JSONException {
	    return obj.getInt("offset");
	}
    
    public int getPagesize() throws JSONException {
        return obj.getInt("pagesize");
    }

	public String getGroupingMode() throws JSONException {
		return obj.getString("grouping_mode");
	}

	public List<String> getGroupByColumns() throws JSONException {
		return getValues(groupBy);
	}

	public List<CriteriaFieldModel> getAggregators() throws JSONException {

		List<CriteriaFieldModel> fields = new ArrayList<CriteriaFieldModel>();

		for (int i = 0; i < aggregators.length(); i++) {
			JSONObject fld = aggregators.getJSONObject(i);
			CriteriaFieldModel fieldModel = createFieldModel(fld);
			fields.add(fieldModel);
		}
		return fields;
	}

	public List<OrderByModel> getOrderByList() throws JSONException {
		List<OrderByModel> orderByModels = new ArrayList<OrderByModel>();

		for (int i = 0; i < orderByArr.length(); i++) {
			JSONObject fld = orderByArr.getJSONObject(i);

			String colName = fld.getString("col_name");
			String sortOrder = fld.getString("sort_order");

			OrderByModel model = new OrderByModel(colName, sortOrder);
			orderByModels.add(model);
		}
		return orderByModels;
	}

	public String getSavedInfoDetails(String key) throws JSONException {
		return saveInfo.getString(key);
	}

	public List<String> getHeaders() throws Exception {
		List<String> headerArr = getValues(headers);
		return headerArr;
	}

	private List<CriteriaFieldModel> getFields(JSONObject jSonObject)
			throws JSONException {
		List<CriteriaFieldModel> fields = new ArrayList<CriteriaFieldModel>();

		JSONArray jArray = jSonObject.getJSONArray("fields");
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject fld = jArray.getJSONObject(i);
			CriteriaFieldModel fieldModel = createFieldModel(fld);
			fields.add(fieldModel);
		}
		return fields;
	}

	private CriteriaFieldModel createFieldModel(JSONObject fieldObject)
			throws JSONException {
		CriteriaFieldModel fieldModel = null;
		String name = fieldObject.getString("name");
		String operator = fieldObject.getString("operator");
		String function = (fieldObject.has("function")) ? fieldObject.getString("function") : null;
		boolean isMultiValue = fieldObject.getBoolean("has_multi_value");

		if (function != null && !function.isEmpty()) {
			String value = fieldObject.getString("value");
			fieldModel = new CriteriaFieldModel(name, function, operator, value);
			return fieldModel;
		}

		if (isMultiValue) {
			JSONArray valueArr = fieldObject.getJSONArray("value");
			fieldModel = new CriteriaFieldModel(name, operator, isMultiValue,
					getValues(valueArr));
			return fieldModel;
		} else {
			String value = fieldObject.getString("value");
			fieldModel = new CriteriaFieldModel(name, operator, isMultiValue,
					value);
			return fieldModel;
		}
	}

	private List<String> getValues(JSONArray jArray) throws JSONException {
		List<String> values = new ArrayList<String>();

		for (int i = 0; i < jArray.length(); i++) {
			String value = jArray.getString(i);
			if (value != null && !value.trim().isEmpty()) {
				values.add(value);
			}
		}
		return values;
	}

}
