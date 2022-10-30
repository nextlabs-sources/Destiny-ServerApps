/*
 * Created on Jun 12, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.sharepoint.datagen;

import java.util.ArrayList;
import java.util.List;

import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.nextlabs.destiny.inquirycenter.report.CriteriaFieldModel;
import com.nextlabs.destiny.inquirycenter.report.OrderByModel;

/**
 * <p>
 * SharePointCriteriaJSONModel
 * </p>
 * 
 * <pre>
 * Sample Criteria :
 *  {
 *     "report_name":"",
 * 	   "start_date": "",
 * 	   "end_date":"",
 *     "report_type":"BAR| PIE",
 * 	   "fields": [
 * 				   { "name" : "obligations_name", "operator" : "eq", "value": "", "has_multi_value" : "false", function:""},
 * 				   { "name" : "obligations_attr_one", "operator" : "like", "value": "", "has_multi_value" : "false", function:""},
 * 				   { "name" : "obligations_attr_two", "operator" : "like", "value": "", "has_multi_value" : "false", function:""},
 * 				   { "name" : "obligations_attr_three", "operator" : "like", "value": "", "has_multi_value" : "false", function:""},
 * 				   { "name" : "log_level", "operator" : "eq", "value": "3", "has_multi_value" : "false", function:""}
 * 				],
 *     "select_cols" : [ "month_nb", "action"],
 *     "aggregator" : {"name":"quantity", "function":"count", "operator":"", "value":"*"},
 * 	   "group_by": [ "month_nb", "action"],
 * 	   "order_by": [
 * 						{ "col_name" : "res_ip-address", "sort_order" : "desc"}
 * 				]
 * 	}
 * 
 * </pre>
 * 
 * @author Amila Silva
 * 
 */
public class SharePointCriteriaJSONModel {

	private JSONObject obj;
	private JSONArray selectArr;
	private JSONArray fieldsArr;
	private JSONArray groupByArr;
	private JSONArray orderByArr;

	public SharePointCriteriaJSONModel(String jSonCriteria) throws Exception {
		obj = new JSONObject(jSonCriteria);
		fieldsArr = obj.getJSONArray("fields");
		selectArr = obj.getJSONArray("select_cols");
		groupByArr = obj.getJSONArray("group_by");
		orderByArr = obj.getJSONArray("order_by");
	}

	public String getReportName() throws Exception {
		return obj.getString("report_name");
	}
	
	public String getReportType() throws Exception {
		return obj.getString("report_type");
	}

	public String getStartDate() throws Exception {
		return obj.getString("start_date");
	}

	public String getEndDate() throws Exception {
		return obj.getString("end_date");
	}

	public List<CriteriaFieldModel> getFields() throws Exception {
		List<CriteriaFieldModel> fields = new ArrayList<CriteriaFieldModel>();

		for (int i = 0; i < fieldsArr.length(); i++) {
			JSONObject jsonField = fieldsArr.getJSONObject(i);
			CriteriaFieldModel fieldModel = createFieldModel(jsonField);
			fields.add(fieldModel);
		}
		return fields;
	}
	
	public List<String> getSelectColumns() throws Exception {
		List<String> selCols = new ArrayList<String>();

		for (int i = 0; i < selectArr.length(); i++) {
			String selectFld = selectArr.getString(i);
			selCols.add(selectFld);
		}
		return selCols;
	}

	public List<String> getGroupByFields() throws Exception {
		List<String> groupByCols = new ArrayList<String>();

		for (int i = 0; i < groupByArr.length(); i++) {
			String groupByFld = groupByArr.getString(i);
			groupByCols.add(groupByFld);
		}
		return groupByCols;
	}

	public List<OrderByModel> getOrderByFields() throws Exception {
		List<OrderByModel> orderBys = new ArrayList<OrderByModel>();

		for (int i = 0; i < orderByArr.length(); i++) {
			JSONObject orderByFld = orderByArr.getJSONObject(i);

			String colName = orderByFld.getString("col_name");
			String sortBy = orderByFld.getString("sort_order");

			orderBys.add(new OrderByModel(colName, sortBy));
		}
		return orderBys;
	}

	private CriteriaFieldModel createFieldModel(JSONObject fieldObject)
			throws JSONException {
		CriteriaFieldModel fieldModel = null;
		String name = fieldObject.getString("name");
		String operator = fieldObject.getString("operator");
		String function = (fieldObject.has("function")) ? fieldObject.getString("function") : null;
		boolean isMultiValue = (fieldObject.has("has_multi_value")) ? fieldObject.getBoolean("has_multi_value") :  false;
		
		if(name.isEmpty()) return null;

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

	public CriteriaFieldModel getAggregator() throws Exception {
		CriteriaFieldModel aggfield = null;
		if (obj.has("aggregator")) {
			JSONObject aggregator = obj.getJSONObject("aggregator");
			aggfield = createFieldModel(aggregator);
		}
		return aggfield;
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
