/*
 * Created on Jun 23, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report;

import com.nextlabs.destiny.inquirycenter.report.birt.InquiryCenterConfigPlugin;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.InquiryCenterAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>
 *  ReportActions
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class ReportActions {
	
	//default actions
	static Map<String, String> actionsMap;

	private static final Log log = LogFactory.getLog(ReportActions.class);

	public static Map<String, String> getActionsMap() {
		
		Map<String, InquiryCenterAction> inqActions = InquiryCenterConfigPlugin.getActions();
		if (inqActions == null || inqActions.isEmpty()){
			InquiryCenterConfigPlugin.getInstance();
			inqActions = InquiryCenterConfigPlugin.getActions();
		}
		
		Map<String,String> actions = new HashMap<String,String>();
		for (String action : inqActions.keySet()){
			actions.put(action, inqActions.get(action).getLongName());
		}
		actionsMap = sortByComparator(actions, true);
		
		return actionsMap;
	}

	
	public static Map<String, JSONArray> getActionsGroupByPolicyModelData() {
		
		Map<String, JSONArray> actions = new HashMap<String, JSONArray>();
		Map<String, InquiryCenterAction> inqActions = InquiryCenterConfigPlugin.getActions();
		Map<Long, String> pmModelNames = InquiryCenterConfigPlugin.getPolicyModels();
		
		try {
			if (inqActions == null || inqActions.isEmpty()) {
				InquiryCenterConfigPlugin.getInstance();
				inqActions = InquiryCenterConfigPlugin.getActions();
                pmModelNames = InquiryCenterConfigPlugin.getPolicyModels();
			}

			for (Map.Entry<String, InquiryCenterAction> entry : inqActions.entrySet()) {
				String actionShortName = entry.getKey();
				InquiryCenterAction action = entry.getValue();

				String policyModelName = (action.getPolicyModelId() <= 0) ? "DEFAULT_ACTION"
						: pmModelNames.get(action.getPolicyModelId());

                if (policyModelName == null) {
                    policyModelName = "UNKNOWN/DELETED";
                }
                
				JSONArray dataArr = actions.get(policyModelName);
				if (dataArr == null) {
					dataArr = new JSONArray();
					actions.put(policyModelName, dataArr);
				}

				JSONObject jsonObj = new JSONObject();
				jsonObj.accumulate("shortName", actionShortName);
				jsonObj.accumulate("label", action.getLongName());
				jsonObj.accumulate("policyModelId", action.getPolicyModelId());
				dataArr.put(jsonObj);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return actions;
	}

	private static Map<String, String> sortByComparator(
			Map<String, String> unsortMap, final boolean order) {

		List<Entry<String, String>> list = new LinkedList<Entry<String, String>>(
				unsortMap.entrySet());

		Collections.sort(list, new Comparator<Entry<String, String>>() {
			public int compare(Entry<String, String> o1,
					Entry<String, String> o2) {
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		Map<String, String> sortedMap = new LinkedHashMap<String, String>();
		for (Entry<String, String> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public static String getDisplayValue(String shortName) {
		return  getActionsMap().get(shortName);
	}
	
}
