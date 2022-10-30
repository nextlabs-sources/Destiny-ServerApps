package com.nextlabs.destiny.inquirycenter;

import static javax.faces.FactoryFinder.FACES_CONTEXT_FACTORY;
import static javax.faces.FactoryFinder.LIFECYCLE_FACTORY;
import static javax.faces.lifecycle.LifecycleFactory.DEFAULT_LIFECYCLE;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.nextlabs.destiny.inquirycenter.framework.PaginatedResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.bluejungle.destiny.types.policies.v1.Policy;
import com.bluejungle.destiny.types.users.v1.User;
import com.bluejungle.framework.utils.StringUtils;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.AttributeColumnMappingDO;
import com.nextlabs.destiny.inquirycenter.report.ResourceLookupService;

/**
 * Utility methods shared across all reporter classes.
 *
 * @author ssen
 *
 */
public class SharedUtils {

	private static final Log log = LogFactory.getLog(SharedUtils.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String OFFSET_KEY = "start";
	private static final String PAGE_LIMIT_KEY = "length";
	private static final String DRAW_COUNT_KEY = "draw";
	private static final String ORDER_KEY = "order[0][dir]";
	private static final String SEARCH_PARAM_KEY = "search[value]";

    public static String limitStringWithEllipsis(String originalStr, int len) {
        String modifiedStr = null;
        if (originalStr != null && originalStr.length() > 3) {
            modifiedStr = StringUtils.limitLength(originalStr, len);
        }
        return modifiedStr;
    }

	private static ResourceLookupService resourceLookupService;

	/**
	 * Initialize faces context. Need to call this when using plain HttpServlets so that
	 * the SecureSession stored in faces context is available for web service authentication
	 * in {@link AuthenticationHandler}
	 * @param request
	 * @param response
	 * @return
	 */
	public static FacesContext getFacesContext(HttpServletRequest request,
			HttpServletResponse response) {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (facesContext == null) {

			FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FACES_CONTEXT_FACTORY);
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(LIFECYCLE_FACTORY);
			Lifecycle lifecycle = lifecycleFactory.getLifecycle(DEFAULT_LIFECYCLE);

			facesContext = contextFactory.getFacesContext(request.getSession()
					.getServletContext(), request, response, lifecycle);

			UIViewRoot view = facesContext.getApplication().getViewHandler()
					.createView(facesContext, "");
			facesContext.setViewRoot(view);
		}
		return facesContext;
	}

	public static void policyLookupData(HttpServletRequest request,
			HttpServletResponse response, String payload) {

		log.info("Request came to policy lookup data");

		JSONObject resObj = new JSONObject();
		try {
			JSONObject reqObj = new JSONObject(payload);

			String dacLocation = request.getSession().getServletContext().getInitParameter("DACLocation");
			getResourceLookupService().setDataLocation(dacLocation);
			getResourceLookupService().setRequest(request);
            List<Policy> policies = getResourceLookupService().getPoliciesSearch("", -1); // search all data

            resObj.put("dataSize", policies.size());

            JSONArray jArr = new JSONArray();

			for (Policy policy : policies) {
				jArr.put(getPolicyJSON(policy));
			}
			resObj.put("policies", jArr);

    		log.info("Request came to policy lookup data, [ DATA SIZE : " + policies.size() +  "]");
			writeJSONResponse(request, response, resObj);
		} catch (Exception e) {
			log.error("Error encountered in policy lookup data,", e);
		}
	}

	public static void policyLookupDataPaginated(HttpServletRequest request,
										HttpServletResponse response, String payload) {
		int offset = 0;
		if (org.apache.commons.lang3.StringUtils.isNotBlank(request.getParameter(OFFSET_KEY))) {
			offset = Integer.parseInt(request.getParameter(OFFSET_KEY));
		}
		String searchParameter = request.getParameter(SEARCH_PARAM_KEY);
		int pageLimit = Integer.parseInt(request.getParameter(PAGE_LIMIT_KEY));
		int drawCount = Integer.parseInt(request.getParameter(DRAW_COUNT_KEY));
		String order = request.getParameter(ORDER_KEY);
		try {
			List policy = getResourceLookupService().getPoliciesPaginated(offset, pageLimit, searchParameter, order);
			if(policy == null){
				policy = Collections.emptyList();
			}
			int totalCount = getResourceLookupService().getPolicyRecordCount("");
			int filteredCount = getResourceLookupService().getPolicyRecordCount(searchParameter);
			log.info("Request came for policy lookup data, [ DATA SIZE : {}" + filteredCount + "]");

			PaginatedResult paginatedResult = generatePaginatedResult(policy, drawCount, totalCount, filteredCount);
			writeJSONResponse(request, response, new Gson().toJson(paginatedResult));
		} catch (IOException jse) {
			log.error("Error while doing user lookup\n{}", jse);
		}
	}

	public static void userLookupData(HttpServletRequest request,
			HttpServletResponse response, String payload) {

		log.info("Request came to user lookup data");

		JSONObject resObj = new JSONObject();
		try {
			JSONObject reqObj = new JSONObject(payload);

			String dacLocation = request.getSession().getServletContext().getInitParameter("DACLocation");
			getResourceLookupService().setDataLocation(dacLocation);
			getResourceLookupService().setRequest(request);
            List<User> users = getResourceLookupService().getUsersSearch("", -1); // search all data

            resObj.put("dataSize", users.size());
            JSONArray jArr = new JSONArray();

			for (User user : users) {
					jArr.put(getUserJSON(user));
			}
			resObj.put("users", jArr);

        	log.info("Request came to user lookup data, [ DATA SIZE : " + users.size() +  "]");
			writeJSONResponse(request, response, resObj);
		} catch (Exception e) {
			log.error("Error encountered in user lookup data,", e);
		}
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getUserJSON(User user) throws JSONException {
		if(user == null) {
			return null;
		}

		JSONObject jSONObject = new JSONObject();
		jSONObject.put("firstName", user.getFirstName());
		jSONObject.put("lastName", user.getLastName());
		jSONObject.put("displayName", user.getDisplayName());
		jSONObject.put("fullName",  user.getFirstName() + " " + user.getLastName());

		return jSONObject;
	}

	public static void userLookupDataPaginated(HttpServletRequest request,
										HttpServletResponse response, String payload) {
		int offset = 0;
		if (org.apache.commons.lang3.StringUtils.isNotBlank(request.getParameter(OFFSET_KEY))) {
			offset = Integer.parseInt(request.getParameter(OFFSET_KEY));
		}
		String searchParameter = request.getParameter(SEARCH_PARAM_KEY);
		int pageLimit = Integer.parseInt(request.getParameter(PAGE_LIMIT_KEY));
		int drawCount = Integer.parseInt(request.getParameter(DRAW_COUNT_KEY));
		String order = request.getParameter(ORDER_KEY);
		try {
			List users = getResourceLookupService().getUsersPaginated(offset, pageLimit, searchParameter, order);
			if(users == null){
				users = Collections.emptyList();
			}
			int totalCount = getResourceLookupService().getUserRecordCount("");
			int filteredCount = getResourceLookupService().getUserRecordCount(searchParameter);
			log.info("Request came to user lookup data, [ DATA SIZE : {}" + filteredCount + "]");

			PaginatedResult paginatedResult = generatePaginatedResult(users, drawCount, totalCount, filteredCount);
			writeJSONResponse(request, response, new Gson().toJson(paginatedResult));
		} catch (IOException jse) {
			log.error("Error while doing user lookup\n{}", jse);
		}
	}

	private static PaginatedResult generatePaginatedResult(List data, int drawCount, int totalCount,
														   int filteredCount) {
		PaginatedResult res = new PaginatedResult();
		res.setDraw(drawCount);
		res.setData(data);
		res.setRecordsTotal(totalCount);
		res.setRecordsFiltered(filteredCount);
		return res;
	}

	/**
	 *
	 * @param policy
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getPolicyJSON(Policy policy) throws JSONException {
		if(policy == null) {
			return null;
		}

		JSONObject jSONObject = new JSONObject();
		jSONObject.put("name", policy.getName());
		jSONObject.put("folderName", policy.getFolderName());

		return jSONObject;
	}

	private static ResourceLookupService getResourceLookupService()
	{
		if (resourceLookupService == null) {
			resourceLookupService = new ResourceLookupService();
		}
		return resourceLookupService;
	}

	/**
	 * <p>
	 *  Write the JSON response.
	 * </p>
	 *
	 * @param request
	 * @param response
	 * @param resObj
	 * @throws IOException
	 */
	public static void writeJSONResponse(HttpServletRequest request,
			HttpServletResponse response, JSONObject resObj) throws IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.print(resObj);
		pw.flush();
		pw.close();
	}

	private static void writeJSONResponse(HttpServletRequest request,
										  HttpServletResponse response, String res) throws IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.print(res);
		pw.flush();
		pw.close();
	}

	/**
	 * <p>
	 *  Write the JSON response.
	 * </p>
	 *
	 * @param request
	 * @param response
	 * @param resObj
	 * @throws IOException
	 */
	public static void writeJSONResponse(HttpServletRequest request,
			HttpServletResponse response, JSONArray resArray) throws IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.print(resArray);
		pw.flush();
		pw.close();
	}

	/**
	 *
	 * @param acm
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject getAttributeColumnMappingJSON(AttributeColumnMappingDO acm)
			throws JSONException
	{
		JSONObject obj = new JSONObject();

		obj.accumulate("id", acm.getId())
		.accumulate("attr_name", acm.getAttributeName())
		.accumulate("col_name", acm.getColumnName())
		.accumulate("data_type", acm.getDataType())
		.accumulate("attr_type", acm.getAttributeType())
		.accumulate("dynamic", acm.isDynamic());

		return obj;
	}


	/**
	 * <p>
	 * Format the given date to  "yyyy-MM-dd HH:mm:ss"
	 * </p>
	 *
	 * @return the formatted date
	 */
	public static String getFormatedDate(Date date) {
		return sdf.format(date);
	}

	public static Date parseDate(String formattedDate)
			throws ParseException {
		return sdf.parse(formattedDate);
	}
}
