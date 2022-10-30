package com.bluejungle.destiny.mgmtconsole.usersandroles.roles;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import com.bluejungle.destiny.mgmtconsole.CommonConstants;

public class ApplicationResourceBean {

	private static final String APP_ADMINISTRATOR_LABEL = "users_and_roles_roles_can_access_administrator_checkbox_label";
	private static final String APP_POLICY_AUTHOR_LABEL = "users_and_roles_roles_can_access_policy_author_checkbox_label";
	// private static final String APP_REPORTER_LABEL = "users_and_roles_roles_can_access_reporter_checkbox_label";
	private static final String APP_REPORTER_ADMIN_LABEL = "users_and_roles_roles_can_access_reporter_admin_checkbox_label";
	private static final String APP_REPORTER_ANALYST_LABEL = "users_and_roles_roles_can_access_reporter_analyst_checkbox_label";

	private static final String APP_ADMINISTRATOR_NAME = "Management Console";
	private static final String APP_POLICY_AUTHOR_NAME = "Policy Author";
	private static final String APP_REPORTER_ADMIN_NAME = "Inquiry Center Admin";
	private static final String APP_REPORTER_USER_NAME = "Inquiry Center User";

	private static final Map<String, String> INTERNAL_TO_EXTERNAL_NAME_MAP;
	static {
		INTERNAL_TO_EXTERNAL_NAME_MAP = new HashMap<String, String>();
		INTERNAL_TO_EXTERNAL_NAME_MAP.put(APP_ADMINISTRATOR_NAME,
				APP_ADMINISTRATOR_LABEL);
		INTERNAL_TO_EXTERNAL_NAME_MAP.put(APP_POLICY_AUTHOR_NAME,
				APP_POLICY_AUTHOR_LABEL);
		INTERNAL_TO_EXTERNAL_NAME_MAP.put(APP_REPORTER_ADMIN_NAME,
				APP_REPORTER_ADMIN_LABEL);
		INTERNAL_TO_EXTERNAL_NAME_MAP.put(APP_REPORTER_USER_NAME,
				APP_REPORTER_ANALYST_LABEL);
	}

	private final String name;
	private final String internalName;
	private final String id;
	private boolean isAccessible;

	public ApplicationResourceBean(String internalName, boolean isAccessible) {
		String label = INTERNAL_TO_EXTERNAL_NAME_MAP.get(internalName);
		this.name = label != null ? getLocalizedMessageBundleValue(label)
				: internalName;
		this.internalName = internalName;
		this.isAccessible = isAccessible;
		int nameHashCode = (name.hashCode() == Integer.MIN_VALUE) ? 0 : name
				.hashCode();
		id = "canAccess" + Math.abs(nameHashCode) + "Checkbox";
	}

	/**
	 * the display name, Such as "Administrator"
	 * 
	 * @return
	 */
	public String getDisplayName() {
		return name;
	}

	/**
	 * the internal name, such as Management Console
	 * 
	 * @return
	 */
	public String getInternalName() {
		return internalName;
	}

	/**
	 * Must begin with a letter A-Z or a-z Can be followed by: letters (A-Za-z),
	 * digits (0-9), hyphens ("-"), underscores ("_"), colons (":"), and periods
	 * (".") Values are case-sensitive
	 *
	 * this is html friendly id
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public boolean isAccessible() {
		return isAccessible;
	}

	public void setAccessible(boolean isAccessible) {
		this.isAccessible = isAccessible;
	}

	private String getLocalizedMessageBundleValue(String key) {
		Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(
				CommonConstants.MGMT_CONSOLE_BUNDLE_NAME, currentLocale);
		return bundle.getString(key);
	}

}
