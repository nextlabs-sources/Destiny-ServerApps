/*
 * Created on Apr 7, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.savedreport.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.bluejungle.destiny.inquirycenter.enumeration.AuditAction;
import com.bluejungle.destiny.inquirycenter.enumeration.AuditableEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.SavedReportDO;
import com.nextlabs.destiny.inquirycenter.JsonUtil;
import com.nextlabs.destiny.inquirycenter.exception.ReportingException;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.EntityAuditLogDAO;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.EntityAuditLogDAOImpl;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAO;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.SavedReportDAOImpl;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.UserServiceDao;
import com.nextlabs.destiny.inquirycenter.savedreport.dao.UserServiceDaoImpl;
import com.nextlabs.destiny.inquirycenter.savedreport.dto.EntityAuditLogDO;
import com.nextlabs.destiny.inquirycenter.savedreport.service.SavedReportService;

import net.sf.hibernate.HibernateException;

/**
 * <p>
 *  SavedReportServiceImpl
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class SavedReportServiceImpl implements SavedReportService {
	
	private static Log log = LogFactory.getLog(SavedReportServiceImpl.class);
	
	private SavedReportDAO savedReportDAO;
	
	private EntityAuditLogDAO entityAuditLogDAO;
	
	private UserServiceDao userServiceDAO;

	@Override
	public void create(SavedReportDO savedReport) throws ReportingException {
		
		try {
			getSavedReportDAO().create(savedReport);
			
			EntityAuditLogDO auditLog = new EntityAuditLogDO();
			auditLog.setAction(AuditAction.CREATE.name());
			auditLog.setActor(getActor(savedReport.getOwnerId()));
			auditLog.setActorId(savedReport.getOwnerId());
			auditLog.setEntityId(savedReport.getId());
			auditLog.setEntityType(AuditableEntity.REPORT.getCode());
			auditLog.setNewValue(getEntityAuditJson(savedReport));
			
			getEntityAuditLogDAO().create(auditLog);
		} catch (SQLException e) {
			throw new ReportingException(
					"Error occurred in creating a save report.", e);
		} catch (HibernateException e) {
			throw new ReportingException(
					"Error occurred in creating a save report.", e);
		} catch (JsonProcessingException e) {
			throw new ReportingException(
					"Error occurred in creating a save report.", e);
		} catch (JSONException e) {
			throw new ReportingException(
					"Error occurred in creating a save report.", e);
		}
	}

	@Override
	public void update(SavedReportDO savedReport) throws ReportingException {
		
		try {
			String snapshot = getEntityAuditJson(getSavedReportDAO().lookup(savedReport.getId()));
			getSavedReportDAO().update(savedReport);
			
			EntityAuditLogDO auditLog = new EntityAuditLogDO();
			auditLog.setAction(AuditAction.UPDATE.name());
			auditLog.setActor(getActor(savedReport.getOwnerId()));
			auditLog.setActorId(savedReport.getOwnerId());
			auditLog.setEntityId(savedReport.getId());
			auditLog.setEntityType(AuditableEntity.REPORT.getCode());
			auditLog.setOldValue(snapshot);
			auditLog.setNewValue(getEntityAuditJson(savedReport));
			
			getEntityAuditLogDAO().create(auditLog);
		} catch (SQLException e) {
			throw new ReportingException(
					"Error occurred in updating the saved report", e);
		} catch (HibernateException e) {
			throw new ReportingException(
					"Error occurred in updating the saved report", e);
		} catch (JsonProcessingException e) {
			throw new ReportingException(
					"Error occurred in updating the saved report", e);
		} catch (JSONException e) {
			throw new ReportingException(
					"Error occurred in updating the saved report", e);
		}
	}

	@Override
	public void delete(SavedReportDO savedReport) throws ReportingException {
		
		try {
			String snapshot = getEntityAuditJson(getSavedReportDAO().lookup(savedReport.getId()));
			getSavedReportDAO().delete(savedReport);
			
			EntityAuditLogDO auditLog = new EntityAuditLogDO();
			auditLog.setAction(AuditAction.DELETE.name());
			auditLog.setActor(getActor(savedReport.getOwnerId()));
			auditLog.setActorId(savedReport.getOwnerId());
			auditLog.setEntityId(savedReport.getId());
			auditLog.setEntityType(AuditableEntity.REPORT.getCode());
			auditLog.setOldValue(snapshot);

			getEntityAuditLogDAO().create(auditLog);
		} catch (SQLException e) {
		    throw new ReportingException("Error occurred in deleting saved report", e);
		} catch (HibernateException e) {
		    throw new ReportingException("Error occurred in deleting saved report", e);
		} catch (JsonProcessingException e) {
		    throw new ReportingException("Error occurred in deleting saved report", e);
		} catch (JSONException e) {
		    throw new ReportingException("Error occurred in deleting saved report", e);
		}
	}

	@Override
	public SavedReportDO lookup(Long savedReportId) throws ReportingException {
		try {
			return getSavedReportDAO().lookup(savedReportId);
		} catch (HibernateException e) {
			 throw new ReportingException("Error occurred in lookup saved report", e);
		}
	}
	
	@Override
	public List<SavedReportDO> lookupByReportName(String savedReportName)
			throws ReportingException {
		try {
			return getSavedReportDAO().lookupByReportName(savedReportName);
		} catch (HibernateException e) {
			 throw new ReportingException("Error occurred in lookup saved report", e);
		}
	}

	@Override
	public List<SavedReportDO> getAll() throws ReportingException {
	      try {
			return getSavedReportDAO().getAll();
		} catch (HibernateException e) {
			 throw new ReportingException("Error occurred in get all saved report", e);
		}
	}

	@Override
	public List<SavedReportDO> getSavedReportsForUser(Long userId, boolean needSharedReports)
			throws ReportingException {
		try {
			return getSavedReportDAO().getSavedReportsForUser(userId, needSharedReports);
		} catch (HibernateException e) {
			 throw new ReportingException("Error occurred in get saved reports for the user", e);
		}
	}

	public SavedReportDAO getSavedReportDAO() {
		if(savedReportDAO == null) {
			savedReportDAO = new SavedReportDAOImpl();
			log.info("Saved report DAO initialized.");
		}
		return savedReportDAO;
	}
	
	public synchronized EntityAuditLogDAO getEntityAuditLogDAO() {
		if(entityAuditLogDAO == null) {
			entityAuditLogDAO = new EntityAuditLogDAOImpl();
			log.info("Entity audit log DAO initialized.");
		}
		
		return entityAuditLogDAO;
	}
	
	public synchronized UserServiceDao getUserDAO() {
		if(userServiceDAO == null) {
			userServiceDAO = new UserServiceDaoImpl();
			log.info("User service DAO initialized.");
		}
		
		return userServiceDAO;
	}
	
	private String getActor(Long userId)
			throws SQLException, HibernateException {
		return getUserDAO().getUserDisplayName(userId);
	}
	
	/**
	 * Translate stored json criteria string.
	 * @param savedReportDO
	 * @return
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	private String getEntityAuditJson(SavedReportDO savedReportDO) 
			throws JsonProcessingException, JSONException {
		Map<String, Object> audit = new LinkedHashMap<String, Object>(); 
		if(savedReportDO != null) {
			audit.put("Name", savedReportDO.getTitle());
			audit.put("Description", savedReportDO.getDescription());
			
			JSONObject criteria = new JSONObject(savedReportDO.getCriteriaJSON());
			audit.put("Report Type", criteria.getJSONObject("save_info").getString("report_type"));
			audit.put("Share Mode", savedReportDO.getSharedMode());
			JSONArray sharedUsers = criteria.getJSONObject("save_info").getJSONArray("user_ids");
			if(sharedUsers.length() > 0) {
				String[] sharedToUsers = new String[sharedUsers.length()];
				for(int i = 0; i < sharedToUsers.length; i++) {
					sharedToUsers[i] = sharedUsers.getString(i);
				}
				audit.put("Share to Users", StringUtils.join(sharedToUsers, ", "));
			} else {
				audit.put("Share to Users", null);
			}
			JSONArray sharedGroups = criteria.getJSONObject("save_info").getJSONArray("group_ids"); 
			if(sharedGroups.length() > 0) {
				String[] sharedToGroups = new String[sharedGroups.length()];
				for(int i = 0; i < sharedToGroups.length; i++) {
					sharedToGroups[i] = Integer.toString(sharedGroups.getInt(i));
				}
				audit.put("Share to Groups (ID)", StringUtils.join(sharedToGroups, ", "));
			} else {
				audit.put("Share to Groups (ID)", null);
			}
			
			JSONArray displayColumns = criteria.getJSONArray("header");
			String[] columnNames = new String[displayColumns.length()];
			for(int i = 0; i < columnNames.length; i++) {
				columnNames[i] = removePrefix(displayColumns.getString(i));
			}
			audit.put("Display Columns", StringUtils.join(columnNames, ", "));
			audit.put("Grouping Mode", criteria.getString("grouping_mode"));
			audit.put("Maximum Rows", criteria.getInt("max_rows"));
			
			JSONObject general = criteria.getJSONObject("filters").getJSONObject("general");
			Map<String, String> dateRange = new LinkedHashMap<String, String>();
			dateRange.put("Date Mode", general.getString("date_mode"));
			dateRange.put("Start Date", general.getString("start_date"));
			dateRange.put("End Date", general.getString("end_date"));			
			audit.put("Date Range", dateRange);
			
			JSONArray generalFields = general.getJSONArray("fields");
			for(int i = 0; i < generalFields.length(); i++) {
				String fieldName = generalFields.getJSONObject(i).getString("name");
				
				if("log_level".equals(fieldName)) {
					audit.put("Event Level", generalFields.getJSONObject(i).getString("value"));
				} else if("decision".equals(fieldName)) {
					audit.put("Policy Decision", getDecision(generalFields.getJSONObject(i).getString("value")));
				} else if("action".equals(fieldName)) {
					if(generalFields.getJSONObject(i).getJSONArray("value").length() > 0) {
						String[] actions = new String[generalFields.getJSONObject(i).getJSONArray("value").length()];
						for(int j = 0; j < actions.length; j ++) {
							actions[j] = generalFields.getJSONObject(i).getJSONArray("value").getString(j);
						}
						audit.put("Actions", StringUtils.join(actions, ", "));
					} else {
						audit.put("Actions", null);
					}
				}
			}
			
			JSONObject userCriteria = criteria.getJSONObject("filters").getJSONObject("user_criteria");
			JSONArray userList = userCriteria.getJSONObject("look_up_field").getJSONArray("value"); 
			if(userList.length() > 0) {
				String[] users = new String[userList.length()];
				for(int i = 0; i < userList.length(); i++) {
					users[i] = userList.getString(i);
				}
				audit.put("User", StringUtils.join(users, ", "));
			} else {
				audit.put("User", null);
			}
			JSONArray userFields = userCriteria.getJSONArray("fields");
			if(userFields.length() > 0) {
				List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
				for(int i = 0; i < userFields.length(); i++) {
					JSONObject field = userFields.getJSONObject(i);
					Map<String, String> fieldDetails = new LinkedHashMap<String, String>();
					fieldDetails.put("Field Name", removePrefix(field.getString("name")));
					fieldDetails.put("Operator", field.getString("operator"));
					
					if(Boolean.valueOf(field.getString("has_multi_value"))) {
						String[] values = new String[field.getJSONArray("value").length()];
						for(int j = 0; j < values.length; j++) {
							values[j] = field.getJSONArray("value").getString(j);
						}
						fieldDetails.put("Values", StringUtils.join(values, ", "));
					} else {
						fieldDetails.put("Value", field.getString("value"));
					}
					fields.add(fieldDetails);
				}
				audit.put("User Criteria", fields);
			} else {
				audit.put("User Criteria", null);
			}
			
			JSONObject resourceCriteria = criteria.getJSONObject("filters").getJSONObject("resource_criteria");
			JSONArray resourceList = resourceCriteria.getJSONObject("look_up_field").getJSONArray("value"); 
			if(resourceList.length() > 0) {
				String[] resources = new String[resourceList.length()];
				for(int i = 0; i < resourceList.length(); i++) {
					resources[i] = resourceList.getString(i);
				}
				audit.put("Resource", StringUtils.join(resources, ", "));
			} else {
				audit.put("Resource", null);
			}
			JSONArray resourceFields = resourceCriteria.getJSONArray("fields");
			if(resourceFields.length() > 0) {
				List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
				for(int i = 0; i < resourceFields.length(); i++) {
					JSONObject field = resourceFields.getJSONObject(i);
					Map<String, String> fieldDetails = new LinkedHashMap<String, String>();
					fieldDetails.put("Field Name", removePrefix(field.getString("name")));
					fieldDetails.put("Operator", field.getString("operator"));
					
					if(Boolean.valueOf(field.getString("has_multi_value"))) {
						String[] values = new String[field.getJSONArray("value").length()];
						for(int j = 0; j < values.length; j++) {
							values[j] = field.getJSONArray("value").getString(j);
						}
						fieldDetails.put("Values", StringUtils.join(values, ", "));
					} else {
						fieldDetails.put("Value", field.getString("value"));
					}
					fields.add(fieldDetails);
				}
				audit.put("Resource Criteria", fields);
			} else {
				audit.put("Resource Criteria", null);
			}
			
			JSONObject policyCriteria = criteria.getJSONObject("filters").getJSONObject("policy_criteria");
			JSONArray policyList = policyCriteria.getJSONObject("look_up_field").getJSONArray("value"); 
			if(policyList.length() > 0) {
				String[] policies = new String[policyList.length()];
				for(int i = 0; i < policyList.length(); i++) {
					policies[i] = policyList.getString(i);
				}
				audit.put("Policy", StringUtils.join(policies, ", "));
			} else {
				audit.put("Policy", null);
			}
			JSONArray policyFields = policyCriteria.getJSONArray("fields");
			if(policyFields.length() > 0) {
				List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
				for(int i = 0; i < policyFields.length(); i++) {
					JSONObject field = policyFields.getJSONObject(i);
					Map<String, String> fieldDetails = new LinkedHashMap<String, String>();
					fieldDetails.put("Field Name", removePrefix(field.getString("name")));
					fieldDetails.put("Operator", field.getString("operator"));
					
					if(Boolean.valueOf(field.getString("has_multi_value"))) {
						String[] values = new String[field.getJSONArray("value").length()];
						for(int j = 0; j < values.length; j++) {
							values[j] = field.getJSONArray("value").getString(j);
						}
						fieldDetails.put("Values", StringUtils.join(values, ", "));
					} else {
						fieldDetails.put("Value", field.getString("value"));
					}
					fields.add(fieldDetails);
				}
				audit.put("Policy Criteria", fields);
			} else {
				audit.put("Policy Criteria", null);
			}
			
			JSONObject otherCriteria = criteria.getJSONObject("filters").getJSONObject("other_criteria");
			audit.put("Other", otherCriteria.getJSONObject("look_up_field").getString("value"));
			JSONArray otherFields = otherCriteria.getJSONArray("fields");
			if(otherFields.length() > 0) {
				List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
				for(int i = 0; i < otherFields.length(); i++) {
					JSONObject field = otherFields.getJSONObject(i);
					Map<String, String> fieldDetails = new LinkedHashMap<String, String>();
					fieldDetails.put("Field Name", removePrefix(field.getString("name")));
					fieldDetails.put("Operator", field.getString("operator"));
					
					if(Boolean.valueOf(field.getString("has_multi_value"))) {
						String[] values = new String[field.getJSONArray("value").length()];
						for(int j = 0; j < values.length; j++) {
							values[j] = field.getJSONArray("value").getString(j);
						}
						fieldDetails.put("Values", StringUtils.join(values, ", "));
					} else {
						fieldDetails.put("Value", field.getString("value"));
					}
					fields.add(fieldDetails);
				}
				audit.put("Other Criteria", fields);
			} else {
				audit.put("Other Criteria", null);
			}
			
			JSONArray orderBy = criteria.getJSONArray("order_by");
			if(orderBy.length() > 0) {
				Map<String, String> orders = new LinkedHashMap<String, String>();
				for(int i = 0; i < orderBy.length(); i++) {
					orders.put(orderBy.getJSONObject(i).getString("col_name"), orderBy.getJSONObject(i).getString("sort_order"));
				}
				audit.put("Order By", orders);
			} else {
				audit.put("Order By", null);
			}
		}
		
		return JsonUtil.toJsonString(audit);
	}
	
	private String removePrefix(String value) {
		if(value != null) {
			return value.substring(value.indexOf("_") + 1);
		}
		
		return value;
	}
	
	private String getDecision(String value) {
		if("B".equals(value)) {
			return "Both";
		} else if("A".equals(value)) {
			return "Allow";
		} else if("D".equals(value)) {
			return "Deny";
		}
		
		return value;
	}
}
