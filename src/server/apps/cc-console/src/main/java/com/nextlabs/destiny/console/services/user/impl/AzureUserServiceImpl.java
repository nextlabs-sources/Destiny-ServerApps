package com.nextlabs.destiny.console.services.user.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.nextlabs.destiny.console.dao.ApplicationUserDao;
import com.nextlabs.destiny.console.dao.authentication.AuthHandlerTypeDetailDao;
import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.dto.authentication.activedirectory.AdSearchResponse;
import com.nextlabs.destiny.console.dto.common.ExternalGroupDTO;
import com.nextlabs.destiny.console.dto.common.ExternalUserDTO;
import com.nextlabs.destiny.console.exceptions.ConnectionFailedException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.ProvisionedUserGroup;
import com.nextlabs.destiny.console.model.authentication.Authority;
import com.nextlabs.destiny.console.model.authentication.MicrosoftAzure;
import com.nextlabs.destiny.console.repositories.ProvisionedUserGroupRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.user.ExternalUserService;
import com.nextlabs.destiny.console.utils.PrincipalUtil;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service("AzureUserService")
public class AzureUserServiceImpl 
	implements ExternalUserService {

	private static final Logger logger = LoggerFactory.getLogger(AzureUserServiceImpl.class);
	private static final String UNIQUE_ID = "${id}";
	private static final String ID = "id";
	private static final String DISPLAY_NAME = "displayName";

	@Autowired
	private MessageBundleService msgBundle;
	
	@Autowired
	private AuthHandlerTypeDetailDao authHandlerDao;
	
	@Autowired
	private ApplicationUserDao applicationUserDao;

	@Autowired
	private ProvisionedUserGroupRepository userGroupRepository;

	@Override
	public void checkConnection(AuthHandlerDetail handlerDTO) {
		try {
			MicrosoftGraphHelper graphHelper = buildHelper(handlerDTO);
			graphHelper.testConfiguration(graphHelper.authenticate(), handlerDTO);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
        	try {
        		if(e.getMessage().contains("{")
        				&& e.getMessage().contains("error_description")
        				&& e.getMessage().endsWith("}")) {
	        		JSONObject authError = new JSONObject(e.getMessage().substring(e.getMessage().indexOf('{')));
	        		throw new ConnectionFailedException(msgBundle.getText("auth.handler.auth.failed.code"), authError.getString("error_description"));
        		} else {
        			throw new ConnectionFailedException(msgBundle.getText("auth.handler.auth.failed.code"), e.getMessage());
        		}
        	} catch(JSONException jsonErr) {
        		throw new ConnectionFailedException(msgBundle.getText("auth.handler.auth.failed.code"), jsonErr.getMessage());
        	}
		}
	}

	@Override
	public AdSearchResponse getAllUsers(Long authHandlerId, String searchText, int pageSize) 
			throws ConsoleException {
		AdSearchResponse searchResult = new AdSearchResponse();
		try {
			AuthHandlerDetail handlerDTO = AuthHandlerDetail.getDTO(authHandlerDao.findById(authHandlerId));
			Set<String> existingUsers = getExistingUsers(authHandlerId);
			MicrosoftGraphHelper graphHelper = buildHelper(handlerDTO);
			List<ExternalUserDTO> users = graphHelper.getUsers(graphHelper.authenticate().accessToken(), handlerDTO.getUserAttributes(), searchText, pageSize + existingUsers.size());
			int returnCount = 0;
			
			for(ExternalUserDTO user : users) {
				user.setUsername(PrincipalUtil.extractUID(user.getUsername()));
				if(existingUsers.contains(user.getUsername().toLowerCase())) {
					continue;
				}
				
				if(returnCount == pageSize) {
					break;
				}
				
				user.setAuthHandlerId(authHandlerId);
				searchResult.getAdUsers().add(user);
				returnCount++;
			}
		} catch(Exception e) {
			throw new ConsoleException(e);
		}
		
		return searchResult;
	}

	@Override
	public AdSearchResponse getAllGroups(Long authHandlerId, String searchText, int pageSize)
				throws ConsoleException {
		AdSearchResponse searchResult = new AdSearchResponse();
		try {
			AuthHandlerDetail handlerDTO = AuthHandlerDetail.getDTO(authHandlerDao.findById(authHandlerId));
			Set<String> existingGroups = getProvisionedGroups(authHandlerId);
			MicrosoftGraphHelper graphHelper = buildHelper(handlerDTO);
			List<ExternalGroupDTO> groups = graphHelper.getGroups(graphHelper.authenticate().accessToken(), searchText, pageSize + existingGroups.size());

			for(ExternalGroupDTO group : groups) {
				if(existingGroups.contains(group.getExternalId())) {
					continue;
				}

				group.setAuthHandlerId(authHandlerId);
				searchResult.getAdGroups().add(group);

				if(searchResult.getAdGroups().size() == pageSize) {
					break;
				}
			}
		} catch(Exception e) {
			throw new ConsoleException(e);
		}

		return searchResult;
	}

	@Override
	public ExternalGroupDTO getGroup(Long authHandlerId, String groupId)
				throws ConsoleException {
		try {
			AuthHandlerDetail handlerDTO = AuthHandlerDetail.getDTO(authHandlerDao.findById(authHandlerId));
			MicrosoftGraphHelper graphHelper = buildHelper(handlerDTO);

			return graphHelper.getGroup(graphHelper.authenticate().accessToken(), groupId);
		} catch(Exception e) {
			throw new ConsoleException(e);
		}
	}

	@Override
	public AdSearchResponse getAllProvisionedGroups(Long authHandlerId, String searchText, int pageSize)
				throws ConsoleException {
		AdSearchResponse searchResult = new AdSearchResponse();
		try {
			AuthHandlerDetail handlerDTO = AuthHandlerDetail.getDTO(authHandlerDao.findById(authHandlerId));
			Set<String> existingGroups = getProvisionedGroups(authHandlerId);
			MicrosoftGraphHelper graphHelper = buildHelper(handlerDTO);

			if(StringUtils.isEmpty(searchText)) {
				for(String existingGroup : existingGroups) {
					ExternalGroupDTO externalGroupDTO = graphHelper.getGroup(graphHelper.authenticate().accessToken(), existingGroup);
					externalGroupDTO.setAuthHandlerName(handlerDTO.getName());
					ProvisionedUserGroup userGroup = userGroupRepository.findByAuthHandlerIdAndGroupId(authHandlerId, existingGroup);
					externalGroupDTO.setId(userGroup.getId());
					searchResult.getAdGroups().add(externalGroupDTO);

					if(searchResult.getAdGroups().size() == pageSize) {
						break;
					}
				}
			} else {
				List<ExternalGroupDTO> groups = graphHelper.getGroups(graphHelper.authenticate().accessToken(), searchText, 1000);

				for(ExternalGroupDTO group : groups) {
					if(!existingGroups.contains(group.getExternalId())) {
						continue;
					}

					ProvisionedUserGroup userGroup = userGroupRepository.findByAuthHandlerIdAndGroupId(authHandlerId, group.getExternalId());
					group.setId(userGroup.getId());
					group.setAuthHandlerId(authHandlerId);
					group.setAuthHandlerName(handlerDTO.getName());
					searchResult.getAdGroups().add(group);

					if(searchResult.getAdGroups().size() == pageSize) {
						break;
					}
				}
			}
		} catch(Exception e) {
			throw new ConsoleException(e);
		}

		return searchResult;
	}

	@Override
	public List<ApplicationUser> getUserWithoutProvisionedGroups(Long authHandlerId)
			throws ConsoleException {
		List<ApplicationUser> users = new ArrayList<>();

		try {
			List<ProvisionedUserGroup> provisionedGroups = userGroupRepository.findByAuthHandlerId(authHandlerId);
			List<String> usernameList = applicationUserDao.findAllGroupUsers(authHandlerId);

			// Shortcut, when there is no more user group, skip external API calls
			if(provisionedGroups.size() == 0) {
				for(String username : usernameList) {
					users.add(applicationUserDao.findByUsername(username));
				}
			} else {
				AuthHandlerDetail handlerDTO = AuthHandlerDetail.getDTO(authHandlerDao.findById(authHandlerId));
				Set<String> existingGroups = getProvisionedGroups(authHandlerId);
				MicrosoftGraphHelper graphHelper = buildHelper(handlerDTO);
				String accessToken = graphHelper.authenticate().accessToken();

				for(String username : usernameList) {
					boolean shouldRemove = true;
					Set<String> memberOfGroups = graphHelper.getMemberOf(accessToken, username, ID);

					for(String memberOfGroup : memberOfGroups) {
						if(existingGroups.contains(memberOfGroup)) {
							shouldRemove = false;
							break;
						}
					}

					if(shouldRemove) {
						users.add(applicationUserDao.findByUsername(username));
					}
				}
			}
		} catch(Exception e) {
			throw new ConsoleException(e);
		}

		return users;
	}

	@Override
	public Set<String> getUserGroups(AuthHandlerDetail handlerDTO, String username)
			throws ConsoleException {
		try {
			MicrosoftGraphHelper graphHelper = buildHelper(handlerDTO);
			return graphHelper.getMemberOf(graphHelper.authenticate().accessToken(), username, DISPLAY_NAME);
		} catch(Exception e) {
			throw new ConsoleException(e);
		}
	}

	/**
	 * @deprecated This method should not be invoked for Azure User
	 * @param authHandlerId
	 *            primary key of the Authentication Handler
	 * @return
	 */
	@Override
	@Deprecated
	public Set<String> getAllUserAttributes(Long authHandlerId) {
		return Collections.emptySet();
	}

	/**
	 * @deprecated This method should not be invoked for Azure User
	 * @param handlerDTO
	 * @return
	 */
	@Override
	@Deprecated
	public Set<String> getAllUserAttributes(AuthHandlerDetail handlerDTO) {
		return Collections.emptySet();
	}

	@Override
	public Map<String, String> getExternalUserAttributesByName(AuthHandlerDetail authHandler, String userId) {
		MicrosoftGraphHelper graphHelper;
		Map<String, String> attributeMapping = authHandler.getUserAttributes();
		Map<String, String> userAttributes = new LinkedHashMap<>();
		
		try {
			graphHelper = buildHelper(authHandler);
	    	IAuthenticationResult authResult = graphHelper.authenticate();
	    	JSONObject attributes = new JSONObject(graphHelper.callAPI(
	    			authHandler.getConfigData().get(Authority.ATTRIBUTE_URI).replace(UNIQUE_ID, userId), 
	    			authResult.accessToken()));

	    	Iterator<String> attributeKey = attributes.keys();

	    	while(attributeKey.hasNext()) {
	    		String key = attributeKey.next();
	    		if(attributeMapping.containsValue(key)) {
	    			String nextlabsKey = key;
	    			
    				for(Entry<String, String> mapping : attributeMapping.entrySet()) {
    					if(key.equals(mapping.getValue())) {
    						nextlabsKey = mapping.getKey();
    						break;
    					}
    				}
	    			
	    			Object value = attributes.get(key);
	    			
	    			if(value != null) {
	    				if(value instanceof JSONArray) {
	    					continue;
	    				}
	    				
	    				userAttributes.put(nextlabsKey, value.toString());
	    			}
	    		}
	    	}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		}
		
		return userAttributes;
	}

	private MicrosoftGraphHelper buildHelper(AuthHandlerDetail handlerDTO) {
		return new MicrosoftGraphHelper(handlerDTO.getConfigData().get(MicrosoftAzure.AUTHORITY_URI),
				handlerDTO.getConfigData().get(MicrosoftAzure.TENANT_ID), 
				handlerDTO.getConfigData().get(MicrosoftAzure.AUTHORIZE_SERVICE),
				handlerDTO.getConfigData().get(MicrosoftAzure.API_URI),
				handlerDTO.getConfigData().get(MicrosoftAzure.APPLICATION_ID), 
				handlerDTO.getConfigData().get(MicrosoftAzure.APPLICATION_KEY));
	}
	
	private Set<String> getExistingUsers(Long authHandlerId) {
		Set<String> existingUsers = new HashSet<>();
		List<ApplicationUser> appUsers = applicationUserDao.findAllActiveOrOtherHandler(authHandlerId);
		
		for (ApplicationUser appUser : appUsers) {
			existingUsers.add(appUser.getUsername().toLowerCase());
		}
		
		return existingUsers;
	}

	private Set<String> getProvisionedGroups(Long authHandlerId) {
		Set<String> provisionedGroups = new HashSet<>();
		List<ProvisionedUserGroup> userGroups;

		if(authHandlerId != null && authHandlerId > 0) {
			userGroups = userGroupRepository.findByAuthHandlerId(authHandlerId);
		} else {
			userGroups = userGroupRepository.findAll();
		}

		for(ProvisionedUserGroup userGroup : userGroups) {
			provisionedGroups.add(userGroup.getGroupId());
		}

		return provisionedGroups;
	}
}

class MicrosoftGraphHelper {

	private static final String API_VERSION = "/v1.0";
    private static final String API_USERS = "/users";
    private static final String API_GROUPS = "/groups";
    private static final String API_MEMBER_OF = "/memberOf";
    private static final String CONTENT_TYPE = "application/json";
    private static final String UTF_8 = "UTF-8";
    private static final String RESPONSE_ARRAY_KEY = "value";
    private static final String USERNAME_KEY = "username";
    private static final String FIRSTNAME_KEY = "firstName";
    private static final String LASTNAME_KEY = "lastName";
    private static final String EMAIL_KEY = "email";

    private String apiUri;
    private String applicationId;
    private String applicationKey;
    private String authorizationURL;

    public MicrosoftGraphHelper(String authority, String tenant, String authorizeService, String apiUri, String applicationId, String applicationKey) {
        this.apiUri = apiUri;
        this.applicationId = applicationId;
        this.applicationKey = applicationKey;
		this.authorizationURL = (authority.endsWith("/") ? authority : authority + "/") + tenant + authorizeService;
    }

    public IAuthenticationResult authenticate()
			throws InterruptedException, AuthenticationException {
        IAuthenticationResult result;

        try {
			result = getAccessTokenByClientCredentialGrant(getClientApplication());
            if (result == null) {
                throw new AuthenticationException("Authentication result for " + apiUri + " from " + authorizationURL + " was null");
            }
        } catch (TimeoutException e) {
            throw new AuthenticationException("Timed out acquiring authentication token from " + authorizationURL);
        } catch (MalformedURLException e) {
            throw new AuthenticationException("Unable to connection to URL " + authorizationURL, e);
        } catch (ExecutionException e) {
            throw new AuthenticationException(e.getMessage());
		}

        return result;
    }

    public void testConfiguration(IAuthenticationResult authResult, AuthHandlerDetail handlerDTO)
    		throws IOException {
    	HttpURLConnection connection = buildAPIRequestConnection(buildTestConfigurationRequestURL(handlerDTO.getUserAttributes()),
						authResult.accessToken());
    	if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
    		throw new AuthenticationException(getResponse(connection));
    	}
    }
    
    public List<ExternalUserDTO> getUsers(String accessToken, Map<String, String> userAttributes, String searchText, int pageSize)
			throws IOException {
    	String response = callAPI(buildListUsersRequestURL(userAttributes, searchText, pageSize), accessToken);
    	
    	JsonObject jsonRoot = JsonParser.parseString(response).getAsJsonObject();
    	JsonArray userEntries = jsonRoot.get(RESPONSE_ARRAY_KEY).getAsJsonArray();
    	List<ExternalUserDTO> users = new ArrayList<>();
    	
    	for(int i = 0; i < userEntries.size(); i++) {
    		JsonObject userObject = userEntries.get(i).getAsJsonObject();
    		ExternalUserDTO user = new ExternalUserDTO();
    		user.setExternalId(getJsonObjectStringValue(userObject, "id"));
    		user.setUserType(ApplicationUser.USER_TYPE_IMPORTED);
   			user.setUsername(getJsonObjectStringValue(userObject, userAttributes.get(USERNAME_KEY)));
   			user.setFirstName(getJsonObjectStringValue(userObject, userAttributes.get(FIRSTNAME_KEY)));
    		if(userAttributes.containsKey(LASTNAME_KEY)) {
    			user.setLastName(getJsonObjectStringValue(userObject, userAttributes.get(LASTNAME_KEY)));
    		}
    		if(userAttributes.containsKey(EMAIL_KEY)) {
    			user.setEmail(getJsonObjectStringValue(userObject, userAttributes.get(EMAIL_KEY)));
    		}
    		
    		for(Entry<String, String> attribute : userAttributes.entrySet()) {
    			if(USERNAME_KEY.equals(attribute.getKey())) {
    				continue;
    			}
    			
    			String attributeValue = getJsonObjectStringValue(userObject, attribute.getValue());
    			if(!"null".equals(attributeValue)) {
    				user.getAttributes().put(attribute.getValue(), attributeValue);
    			}
    		}
    		
    		users.add(user);
    	}
    	
    	return users;
    }

    public Set<String> getMemberOf(String accessToken, String upn, String fieldName)
			throws IOException {
		String response = callAPI(buildMemberOfRequestURL(upn, fieldName), accessToken);

		JsonObject jsonRoot = JsonParser.parseString(response).getAsJsonObject();
		Set<String> groups = new HashSet<>();

		if(jsonRoot.get(RESPONSE_ARRAY_KEY) != null) {
			JsonArray groupEntries = jsonRoot.get(RESPONSE_ARRAY_KEY).getAsJsonArray();

			for (int i = 0; i < groupEntries.size(); i++) {
				JsonObject groupObject = groupEntries.get(i).getAsJsonObject();
				groups.add(getJsonObjectStringValue(groupObject, fieldName));
			}
		}

    	return groups;
	}

    public List<ExternalGroupDTO> getGroups(String accessToken, String searchText, int pageSize)
			throws IOException {
		String response = callAPI(buildListGroupsRequestURL(searchText, pageSize), accessToken);
		JsonObject jsonRoot = JsonParser.parseString(response).getAsJsonObject();
		List<ExternalGroupDTO> groups = new ArrayList<>();

		if(jsonRoot.get(RESPONSE_ARRAY_KEY) != null) {
			JsonArray groupEntries = jsonRoot.get(RESPONSE_ARRAY_KEY).getAsJsonArray();

			for (int i = 0; i < groupEntries.size(); i++) {
				JsonObject groupObject = groupEntries.get(i).getAsJsonObject();
				ExternalGroupDTO groupDTO = new ExternalGroupDTO();
				groupDTO.setExternalId(getJsonObjectStringValue(groupObject, "id"));
				groupDTO.setName(getJsonObjectStringValue(groupObject, "displayName"));
				groupDTO.setDescription(getJsonObjectStringValue(groupObject, "description"));
				groupDTO.setEmail(getJsonObjectStringValue(groupObject, "mail"));

				groups.add(groupDTO);
			}
		}

		return groups;
	}

	public ExternalGroupDTO getGroup(String accessToken, String groupId)
			throws IOException {
		String response = callAPI(buildGetGroupRequestURL(groupId), accessToken);
		JsonObject jsonRoot = JsonParser.parseString(response).getAsJsonObject();
		ExternalGroupDTO groupDTO = new ExternalGroupDTO();
		groupDTO.setExternalId(getJsonObjectStringValue(jsonRoot, "id"));
    	groupDTO.setName(getJsonObjectStringValue(jsonRoot, "displayName"));
    	groupDTO.setDescription(getJsonObjectStringValue(jsonRoot, "description"));
    	groupDTO.setEmail(getJsonObjectStringValue(jsonRoot, "mail"));

    	return groupDTO;
	}

    public String callAPI(String apiURL, String accessToken)
    		throws IOException {
    	return getResponse(buildAPIRequestConnection(apiURL, accessToken));
    }

	private ConfidentialClientApplication getClientApplication()
					throws MalformedURLException {
		return ConfidentialClientApplication
						.builder(applicationId,
										ClientCredentialFactory.createFromSecret(applicationKey))
						.authority(authorizationURL)
						.build();
	}

	private IAuthenticationResult getAccessTokenByClientCredentialGrant(ConfidentialClientApplication clientApplication)
					throws ExecutionException, InterruptedException, TimeoutException {
		ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
						Collections.singleton("https://graph.microsoft.com/.default"))
						.build();

		CompletableFuture<IAuthenticationResult> future = clientApplication.acquireToken(clientCredentialParam);
		return future.get(30, TimeUnit.SECONDS);
	}

    private String buildTestConfigurationRequestURL(Map<String, String> userAttributes) {
    	StringBuilder requestURL = new StringBuilder(apiUri);
    	requestURL.append(API_VERSION).append(API_USERS);
    	requestURL.append("?$top=1");
    	requestURL.append("&$orderby=displayName");
    	
    	if(userAttributes != null && userAttributes.size() > 0) {
    		requestURL.append("&$select=id,").append(String.join(",", userAttributes.values()));
    	}
    	
    	return requestURL.toString();
    }
    
    private String buildListUsersRequestURL(Map<String, String> userAttributes, String searchText, int pageSize) 
    		throws UnsupportedEncodingException {
    	StringBuilder requestURL = new StringBuilder(apiUri);
    	requestURL.append(API_VERSION).append(API_USERS);
    	requestURL.append("?$top=" + (pageSize >= 1000 ? 999 : pageSize));	// Microsoft Azure support 1~999 only
    	
    	// $orderby cannot be use together with search string
    	if(!(searchText != null && searchText.length() > 0)) {
        	requestURL.append("&$orderby=displayName");
    	}
    	
    	if(userAttributes != null && userAttributes.size() > 0) {
    		requestURL.append("&$select=id,").append(String.join(",", userAttributes.values()));
    		
    		// Only search with firstname and username fields
    		if(searchText != null && searchText.length() > 0) {
    			List<String> filters = new ArrayList<>();
    			
    			if(userAttributes.containsKey(FIRSTNAME_KEY)) {
    				filters.add("startswith(" + userAttributes.get(FIRSTNAME_KEY) + ",'" + URLEncoder.encode(searchText, UTF_8) + "')");
    			}
    			
    			if(userAttributes.containsKey(USERNAME_KEY)) {
    				filters.add("startswith(" + userAttributes.get(USERNAME_KEY) + ",'" + URLEncoder.encode(searchText, UTF_8) + "')");
    			}
    			
    			requestURL.append("&$filter=").append(String.join(URLEncoder.encode(" or ", UTF_8), filters));
    		}
    	}
    	
    	return requestURL.toString();
    }

    private String buildMemberOfRequestURL(String upn, String fieldName) {
		StringBuilder requestURL = new StringBuilder(apiUri);
		requestURL.append(API_VERSION).append(API_USERS).append('/').append(upn).append(API_MEMBER_OF);
		requestURL.append("?$select=").append(fieldName);

		return requestURL.toString();
	}

    private String buildListGroupsRequestURL(String searchText, int pageSize)
			throws UnsupportedEncodingException {
    	StringBuilder requestURL = new StringBuilder(apiUri);
		requestURL.append(API_VERSION).append(API_GROUPS);
		requestURL.append("?$top=" + (pageSize >= 1000 ? 950 : pageSize));	// Microsoft Azure support 1~950 only
		requestURL.append("&$select=id,displayName,description,mail");

		// $orderby cannot be use together with search string
		if(!(searchText != null && searchText.length() > 0)) {
			requestURL.append("&$orderby=displayName");
		} else {
			requestURL.append("&$filter=startswith(displayName,'" + URLEncoder.encode(searchText, UTF_8) + "')");
		}

		return requestURL.toString();
	}

	private String buildGetGroupRequestURL(String groupId) {
		StringBuilder requestURL = new StringBuilder(apiUri);
		requestURL.append(API_VERSION).append(API_GROUPS);
		requestURL.append("/").append(groupId);
		requestURL.append("?$select=id,displayName,description,mail");

		return requestURL.toString();
	}

    private HttpURLConnection buildAPIRequestConnection(String requestURL, String accessToken)
    		throws IOException {
		URL url = new URL(requestURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod(HttpMethod.GET.name());
		connection.setRequestProperty("Authorization", "Bearer " + accessToken);
		connection.setRequestProperty("Accept", CONTENT_TYPE);

		return connection;
    }

    private String getResponse(HttpURLConnection httpConnection)
    		throws IOException {
    	StringBuilder response = new StringBuilder();
    	try(BufferedReader reader = (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) ?
    			new BufferedReader(new InputStreamReader(httpConnection.getInputStream())) :
    				new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()))) {
        	String line;
    		while((line = reader.readLine()) != null) {
    			response.append(line);
    		}
    	}
    	
    	return response.toString();
    }
    
    private String getJsonObjectStringValue(JsonObject jsonObject, String memberName) {
    	if(jsonObject.has(memberName)
    			&& !(jsonObject.get(memberName) instanceof JsonNull)) {
    		return jsonObject.get(memberName).getAsString();
    	}
    	
    	return "null";
    }
}
