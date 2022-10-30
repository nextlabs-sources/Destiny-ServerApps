package com.nextlabs.destiny.inquirycenter.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONObject;

import com.bluejungle.destiny.services.management.types.UserDTO;
import com.bluejungle.destiny.services.management.types.UserDTOList;
import com.nextlabs.destiny.container.shared.inquirymgr.SharedLib;
import com.nextlabs.destiny.inquirycenter.SharedUtils;
import com.nextlabs.destiny.inquirycenter.audit.IAuditLogDataGenerator;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.DataGeneratorFactory;
import com.nextlabs.destiny.inquirycenter.user.service.AppUserMgmtService;
import com.nextlabs.report.datagen.ResultData;

public class AuditReportServlet 
		extends HttpServlet {
	
	private static final long serialVersionUID = 2922666707750742595L;
	private static final Log log = LogFactory.getLog(AuditReportServlet.class);
	
	private AppUserMgmtService applicationUserService;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			super.init(config);
			applicationUserService = new AppUserMgmtService();
		} catch (Exception e) {
			log.error("Error in initialzing ReportServlet, ", e);
			throw new ServletException(e);
		}
	}

	/**
	 * All the get requests serves here.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * All the post requests serves here.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}
	
	private void doProcess(HttpServletRequest request,
			HttpServletResponse response) {
		FacesContext currentContext = null;
		try {
			currentContext = SharedUtils.getFacesContext(request, response);
			String action = request.getParameter("action");
			String payload = request.getParameter("data");

			if("VALIDATE_USER_SESSION".equals(action)) {
				validateUserSession(request, response);
			} else if("LIST_APPLICATION_USERS".equals(action)) {
				getAllUsers(request, response);
			} else if("EXCUTE_QUERY".equals(action)) {
				executeQuery(request, response, payload);
			}
		} catch (Exception e) {
			log.error("Error encountered in processing the request,", e);
		} finally {
			if(currentContext != null)
				currentContext.release();
		}
	}
	
	private void validateUserSession(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			SharedUtils.writeJSONResponse(request, response, new JSONObject().accumulate("valid_session", true));
		} catch (Exception ex) {
			log.error("Error occurred in validateUserSession,", ex);
		}
	}

	private void getAllUsers(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JSONObject resObj = new JSONObject();
		try {
			JSONArray resArray = new JSONArray();
			UserDTOList usersList = applicationUserService.getAllUsers();
			
			if(usersList != null
					&& usersList.getUsers() != null) {
				for(UserDTO user : usersList.getUsers()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("first_name", user.getFirstName());
                    jsonObject.put("last_name", user.getLastName());
                    jsonObject.put("username", user.getUniqueName());
                    resArray.put(jsonObject);
				}
			}
			
			resObj.put("app_users", resArray);
			
			SharedUtils.writeJSONResponse(request, response, resObj);
		} catch(Exception ex) {
			log.error("Error occured in getAllUsers,", ex);
		}
	}
	
	private void executeQuery(HttpServletRequest request, 
			HttpServletResponse response, String payload) throws Exception {
		IAuditLogDataGenerator dataGenerator = null;
		try {
			dataGenerator = DataGeneratorFactory.getInstance()
					.getAuditLogDataGenerator(SharedLib.getActivityDataSource().getSession());
			
			SharedUtils.writeJSONResponse(request, response, wrapResponse(dataGenerator.executeQuery(getQueryModel(payload))));
		} catch (Exception ex) {
			log.error("Error occurred in executeQuery,", ex);
		} finally {
			if(dataGenerator != null) {
				dataGenerator.cleanup();
			}
		}
	}
	
	private AuditQueryModel getQueryModel(String payload)
			throws Exception {
		JSONObject jsonData = new JSONObject(payload);
		AuditQueryModel queryCriteria = new AuditQueryModel();
		
		queryCriteria.setBeginDate(SharedUtils.parseDate(jsonData.getString("beginDate")));
		queryCriteria.setEndDate(SharedUtils.parseDate(jsonData.getString("endDate")));
		queryCriteria.setAction(jsonData.getString("action"));
		queryCriteria.setEntityType(jsonData.getString("entityType"));
		String users = jsonData.optString("users", null);
		if(users != null
				&& users.length() > 0) {
			queryCriteria.setUsers(users.trim().split("\\s*,\\s*"));
		}
		String entityIds = jsonData.optString("entityIds", null);
		if(entityIds != null
				&& entityIds.length() > 0) {
			String[] entityIdArray = entityIds.trim().split("\\s*,\\s*");
			List<Long> entityIdList = new ArrayList<Long>();
			
			for(String entityId : entityIdArray) {
				try {
					entityIdList.add(Long.parseLong(entityId));
				} catch(NumberFormatException nonLongException) {
					// Ignore
				}
			}
			queryCriteria.setEntityIds(entityIdList.toArray(new Long[entityIdList.size()]));
		}
		
		JSONObject orderBy = jsonData.getJSONObject("orderBy");
		OrderByModel orderByModel = new OrderByModel(orderBy.getString("fieldName"), orderBy.getString("sortOrder"));
		queryCriteria.setOrderBy(orderByModel);
		
		queryCriteria.setPageSize(jsonData.optInt("pageSize", 100));
		queryCriteria.setOffset(jsonData.optInt("offset", 0));
		
		return queryCriteria;
	}
	
	private JSONObject wrapResponse(ResultData resultData)
			throws Exception {
		JSONObject response = new JSONObject();
		JSONArray header = new JSONArray();
		JSONArray data = new JSONArray();

		if(resultData != null) {
			// Yeah yeah, it's weird, but this method is providing the number of columns
			int columnCount = resultData.getRowCount();
			int recordCount = resultData.size();

			for(int i = 0; i < recordCount; i++) {
				JSONObject record = new JSONObject();
				
				for(int j = 0; j < columnCount; j++) {
					String columnName = resultData.getName(j) == null ? "" : resultData.getName(j).toLowerCase();
					Object cellValue = resultData.get(i, j);
					
					if(i == 0) {
						JSONObject column = new JSONObject();
						column.put("sTitle", columnName);
						column.put("mData", columnName);
						
						header.put(column);
					}
					
					record.put(columnName, cellValue == null ? "null" : cellValue);
				}

				data.put(record);
			}
		}
		
		response.put("header", header);
		response.put("data", data);
		
		return response;
	}
}