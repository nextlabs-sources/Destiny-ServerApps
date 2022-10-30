/*
 * Created on May 12, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.bluejungle.destiny.types.actions.v1.ActionType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.inquirycenter.enumeration.ReportGroupByType;
import com.bluejungle.destiny.inquirycenter.enumeration.ReportGroupingUIType;
import com.bluejungle.destiny.inquirycenter.enumeration.ReportLoggingLevelUIType;
import com.bluejungle.destiny.inquirycenter.enumeration.ReportTargetUIType;
import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.ExpressionCutter;
import com.bluejungle.destiny.server.shared.configuration.IActionConfigDO;
import com.bluejungle.destiny.server.shared.configuration.IActionListConfigDO;
import com.bluejungle.destiny.types.actions.v1.ActionList;
import com.bluejungle.destiny.types.basic.v1.StringList;
import com.bluejungle.destiny.types.effects.v1.EffectList;
import com.bluejungle.destiny.types.effects.v1.EffectType;
import com.bluejungle.destiny.types.report.v1.Report;
import com.bluejungle.destiny.types.report.v1.ReportSummaryType;
import com.bluejungle.destiny.types.report.v1.ReportTargetType;
import com.bluejungle.destiny.webui.framework.faces.UIActionType;
import com.bluejungle.destiny.webui.framework.faces.UIEnforcementType;
import com.bluejungle.domain.action.ActionEnumType;
import com.bluejungle.domain.policydecision.PolicyDecisionEnumType;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyConfigurationStoreImpl;
import com.bluejungle.framework.configuration.IDestinyConfigurationStore;

/**
 * @author ihanen
 * @version $Id:
 *          //depot/branch/Destiny_D_Platform_Extensibility/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportImpl.java#1 $
 */

public class ReportImpl implements IReport {

	/**
	 * Name of the inquiry center bundle
	 */
	private static final String INQUIRY_CENTER_BUNDLE_NAME = "InquiryCenterMessages";

	/**
	 * Map between target data and available action choices
	 */
	private static final Map TARGET_DATA_2_ACTION_CHOICES = new HashMap();

	/**
	 * Name of the resource bundle key to use if there are no actions to display
	 */
	private static final String ANY_ACTION_BUNDLE_KEY = "reports_form_any_action_value";

	/**
	 * Name of the resource bundle key to use if there are no enforcements to
	 * display
	 */
	private static final String ANY_ENFORCEMENT_BUNDLE_KEY = "reports_form_any_enforcement_value";

	/**
	 * Name of the resource bundle key to use if there are no policies to
	 * display
	 */
	private static final String ANY_POLICY_BUNDLE_KEY = "reports_form_any_policy_value";

	/**
	 * Name of the resource bundle key to use if there are no resources to
	 * display
	 */
	private static final String ANY_RESOURCE_BUNDLE_KEY = "reports_form_any_resource_value";

	/**
	 * Name of the resource bundle key to use if there are no users to display
	 */
	private static final String ANY_USER_BUNDLE_KEY = "reports_form_any_user_value";

	/**
	 * The name of a non-saved report
	 */
	private static final String QUICK_REPORT_NAME;
	private static final String QUICK_REPORT_NAME_BUNDLE_KEY = "my_reports_quick_report_title";

	/**
	 * Choices definitions
	 */
	private static final List ENFORCEMENT_CHOICES = new ArrayList();
	private static final List POLICY_GROUP_BY_CHOICES = new ArrayList();
	private static final List TARGET_DATA_CHOICES = new ArrayList();
	private static final List TRACKING_GROUP_BY_CHOICES = new ArrayList();
	private static final List LOGGING_LEVEL_CHOICES = new ArrayList();

	/**
	 * Hack: This is used for the Selected Report Details panel, for text
	 * wrapping purposes
	 */


	/**
	 * Log object
	 */
	private static final Log LOG = LogFactory.getLog(ReportImpl.class);

	static {
		Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		ResourceBundle inquiryCenterBundle = ResourceBundle.getBundle(
				INQUIRY_CENTER_BUNDLE_NAME, currentLocale);
		QUICK_REPORT_NAME = inquiryCenterBundle
				.getString(QUICK_REPORT_NAME_BUNDLE_KEY);

		// Initializes the tracking activity actions
		Comparator comparator = new Comparator() {

			public int compare(Object L, Object R) {
				String lhs = ((SelectItem) L).getLabel();
				String rhs = ((SelectItem) R).getLabel();
				return lhs.compareTo(rhs);
			}
		};

		IComponentManager compMgr = ComponentManagerFactory
				.getComponentManager();
		final IDestinyConfigurationStore configMgr = (IDestinyConfigurationStore) compMgr
				.getComponent(DestinyConfigurationStoreImpl.COMP_INFO);
		IActionListConfigDO actionListConfig = configMgr
				.retrieveActionListConfig();
		
		IActionConfigDO[] actions =  new IActionConfigDO[0];
		if (actionListConfig != null) 
		    actions = actionListConfig.getActions();

		List trackingActivityActionChoices = new ArrayList();
		Set supportedActions = new HashSet(UIActionType.elements());
		supportedActions.remove(UIActionType.ACTION_UI_EMBED_BUNDLE_KEY);
		supportedActions.remove(UIActionType.ACTION_UI_CUT_PASTE_BUNDLE_KEY);
		Iterator iter = supportedActions.iterator();
		while (iter.hasNext()) {
			UIActionType actionType = (UIActionType) iter.next();
			try {
				String displayValue = UIActionType.getDisplayValue(actionType);
				trackingActivityActionChoices.add(new SelectItem(actionType
						.getName(), displayValue));
			} catch (MissingResourceException e) {
				LOG
						.error("Error occured getting the display name for action type - '"
								+ actionType.getName()
								+ "' with bundle key-name '"
								+ actionType.getBundleKeyName() + "'");
			}
		}
		for (int i = 0, n = actions.length; i < n; i++) {
			trackingActivityActionChoices.add(new SelectItem(actions[i]
					.getName(), actions[i].getDisplayName()));
		}
		Object[] selects = trackingActivityActionChoices.toArray();
		Arrays.sort(selects, comparator);
		trackingActivityActionChoices = Arrays.asList(selects);
		TARGET_DATA_2_ACTION_CHOICES.put(ReportTargetType.ActivityJournal,
				trackingActivityActionChoices);

		// Initializes the policy activity actions
		List policyActivityActionChoices = new ArrayList();
		Set supportedPolicyActivityActions = new HashSet(UIActionType
				.elements());
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_ABNORMAL_AGENT_SHUTDOWN);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_AGENT_READ_BINARIES);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_AGENT_READ_CONFIG);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_AGENT_READ_LOG);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_AGENT_START);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_AGENT_STOP);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_AGENT_USER_LOGIN);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_AGENT_USER_LOGOUT);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_BUNDLE_RECEIVED);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_INVALID_BUNDLE);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_AGENT_READ_BUNDLE);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_CUT_PASTE_BUNDLE_KEY);
		supportedPolicyActivityActions
				.remove(UIActionType.ACTION_UI_EMBED_BUNDLE_KEY);
		iter = supportedPolicyActivityActions.iterator();
		while (iter.hasNext()) {
			UIActionType actionType = (UIActionType) iter.next();
			try {
				String displayValue = UIActionType.getDisplayValue(actionType);
				policyActivityActionChoices.add(new SelectItem(actionType
						.getName(), displayValue));
			} catch (MissingResourceException e) {
				LOG
						.error("Error occured getting the display name for action type - '"
								+ actionType.getName()
								+ "' with bundle key-name '"
								+ actionType.getBundleKeyName() + "'");
			}
		}
		for (int i = 0, n = actions.length; i < n; i++) {
			policyActivityActionChoices.add(new SelectItem(
					actions[i].getName(), actions[i].getDisplayName()));
		}
		selects = policyActivityActionChoices.toArray();
		Arrays.sort(selects, comparator);
		policyActivityActionChoices = Arrays.asList(selects);
		TARGET_DATA_2_ACTION_CHOICES.put(ReportTargetType.PolicyEvents,
				policyActivityActionChoices);

		// Initializes the choices
		ENFORCEMENT_CHOICES
				.add(new SelectItem(
						UIEnforcementType.ENFORCEMENT_DENY_BUNDLE_KEY.getName(),
						UIEnforcementType
								.getDisplayValue(UIEnforcementType.ENFORCEMENT_DENY_BUNDLE_KEY)));
		ENFORCEMENT_CHOICES
				.add(new SelectItem(
						UIEnforcementType.ENFORCEMENT_ALLOW_BUNDLE_KEY
								.getName(),
						UIEnforcementType
								.getDisplayValue(UIEnforcementType.ENFORCEMENT_ALLOW_BUNDLE_KEY)));
		ENFORCEMENT_CHOICES
				.add(new SelectItem(
						UIEnforcementType.ENFORCEMENT_BOTH_BUNDLE_KEY.getName(),
						UIEnforcementType
								.getDisplayValue(UIEnforcementType.ENFORCEMENT_BOTH_BUNDLE_KEY)));

		POLICY_GROUP_BY_CHOICES
				.add(new SelectItem((new Integer(
						ReportGroupingUIType.UI_GROUPING_NONE.getType()))
						.toString(), ReportGroupingUIType
						.getDisplayValue(ReportGroupingUIType.UI_GROUPING_NONE)));
		POLICY_GROUP_BY_CHOICES
				.add(new SelectItem(
						(new Integer(ReportGroupingUIType.UI_GROUPING_POLICY
								.getType())).toString(),
						ReportGroupingUIType
								.getDisplayValue(ReportGroupingUIType.UI_GROUPING_POLICY)));
		POLICY_GROUP_BY_CHOICES.add(new SelectItem((new Integer(
				ReportGroupingUIType.UI_GROUPING_RESOURCE.getType()))
				.toString(), ReportGroupingUIType
				.getDisplayValue(ReportGroupingUIType.UI_GROUPING_RESOURCE)));
		POLICY_GROUP_BY_CHOICES
				.add(new SelectItem((new Integer(
						ReportGroupingUIType.UI_GROUPING_TIME.getType()))
						.toString(), ReportGroupingUIType
						.getDisplayValue(ReportGroupingUIType.UI_GROUPING_TIME)));
		POLICY_GROUP_BY_CHOICES
				.add(new SelectItem((new Integer(
						ReportGroupingUIType.UI_GROUPING_USER.getType()))
						.toString(), ReportGroupingUIType
						.getDisplayValue(ReportGroupingUIType.UI_GROUPING_USER)));

		TARGET_DATA_CHOICES.add(new SelectItem(ReportTargetUIType.POLICY_EVENTS
				.getName(), ReportTargetUIType
				.getDisplayValue(ReportTargetUIType.POLICY_EVENTS)));
		TARGET_DATA_CHOICES.add(new SelectItem(
				ReportTargetUIType.ACTIVITY_JOURNAL.getName(),
				ReportTargetUIType
						.getDisplayValue(ReportTargetUIType.ACTIVITY_JOURNAL)));

		TRACKING_GROUP_BY_CHOICES
				.add(new SelectItem((new Integer(
						ReportGroupingUIType.UI_GROUPING_NONE.getType()))
						.toString(), ReportGroupingUIType
						.getDisplayValue(ReportGroupingUIType.UI_GROUPING_NONE)));
		TRACKING_GROUP_BY_CHOICES.add(new SelectItem((new Integer(
				ReportGroupingUIType.UI_GROUPING_RESOURCE.getType()))
				.toString(), ReportGroupingUIType
				.getDisplayValue(ReportGroupingUIType.UI_GROUPING_RESOURCE)));
		TRACKING_GROUP_BY_CHOICES
				.add(new SelectItem((new Integer(
						ReportGroupingUIType.UI_GROUPING_TIME.getType()))
						.toString(), ReportGroupingUIType
						.getDisplayValue(ReportGroupingUIType.UI_GROUPING_TIME)));
		TRACKING_GROUP_BY_CHOICES
				.add(new SelectItem((new Integer(
						ReportGroupingUIType.UI_GROUPING_USER.getType()))
						.toString(), ReportGroupingUIType
						.getDisplayValue(ReportGroupingUIType.UI_GROUPING_USER)));

		LOGGING_LEVEL_CHOICES
				.add(new SelectItem(
						(new Integer(
								ReportLoggingLevelUIType.LOGGING_LEVEL_USER
										.getType())),
						ReportLoggingLevelUIType
								.getDisplayValue(ReportLoggingLevelUIType.LOGGING_LEVEL_USER)));
		LOGGING_LEVEL_CHOICES
				.add(new SelectItem(
						(new Integer(
								ReportLoggingLevelUIType.LOGGING_LEVEL_APPLICATION
										.getType())),
						ReportLoggingLevelUIType
								.getDisplayValue(ReportLoggingLevelUIType.LOGGING_LEVEL_APPLICATION)));
		LOGGING_LEVEL_CHOICES
				.add(new SelectItem(
						(new Integer(ReportLoggingLevelUIType.LOGGING_LEVEL_ALL
								.getType())),
						ReportLoggingLevelUIType
								.getDisplayValue(ReportLoggingLevelUIType.LOGGING_LEVEL_ALL)));
	}

	private Report report;
	private Date runTime;
	private String enforcement;

	/**
	 * Constructor - In theory should not be used. Note - Currently protected
	 * for unit tests and didn't have time to fix it. Should be made private if
	 * possible
	 */
	protected ReportImpl() {
		this(new Report());
		this.report.setSummaryType(ReportSummaryType.None);
		this.report.setTarget(ReportTargetType.PolicyEvents);
	}

	/**
	 * Constructor
	 * 
	 * @param wsReport
	 *            corresponding saved report coming from the web service.
	 */
	public ReportImpl(Report wsReport) {
		if (wsReport == null) {
			throw new NullPointerException("wsSavedReport cannot be null");
		}
		this.report = wsReport;
	}

	public Report getReport() {
		return this.report;
	}

	/**
	 * Converts a user input into a String list that should be saved on the back
	 * end. This function eliminates the "Any *" that may be there if the user
	 * did not specify anything besides the default.
	 * 
	 * @param userInput
	 *            raw UI input from the user
	 * @param defaultBundleKeyName
	 *            name of the key that leads to the default input (e.g. Any
	 *            Policy, Any Resource, etc..)
	 * @return a proper string list ready to be saved on the back end
	 */
	protected StringList convertInputToStringList(final String userInput,
			final String defaultBundleKeyName) {
		if (defaultBundleKeyName == null) {
			throw new NullPointerException(
					"default bundle key name cannot be null");
		}
		StringList result = null;
		if (userInput != null) {
			result = ExpressionCutter.convertToStringList(userInput);
			String[] valueArray = result.getValues();
			if (valueArray != null) {
				ResourceBundle bundle = ResourceBundle
						.getBundle(INQUIRY_CENTER_BUNDLE_NAME, FacesContext
								.getCurrentInstance().getViewRoot().getLocale());
				String anyInputString = bundle.getString(defaultBundleKeyName);
				List resultList = new ArrayList();
				for (int i = 0; i < valueArray.length; i++) {
					String currentValue = valueArray[i];
					if (!currentValue.equalsIgnoreCase(anyInputString)) {
						resultList.add(valueArray[i]);
					}
				}
				valueArray = new String[resultList.size()];
				resultList.toArray(valueArray);
				result.setValues(valueArray);
			}
		}
		return result;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getDescription()
	 */
	public String getDescription() {
		return this.report.getDescription();
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getId()
	 */
	public Long getId() {
		if (this.report.getId() != null) {
			return this.report.getId().getId();
		} else {
			return null;
		}
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getOwned()
	 */
	public boolean getOwned() {
		return this.report.getOwned();
	}

	public ReportSummaryType getSummaryType() {
		return this.report.getSummaryType();
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#isShared()
	 */
	public boolean isShared() {
		return this.report.getShared();
	}

	public boolean getShared() {
		return this.report.getShared();
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#setDescription(java.lang.String)
	 */
	public void setDescription(String newDescription) {
		this.report.setDescription(newDescription);
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#setShared(boolean)
	 */
	public void setShared(boolean shared) {
		this.report.setShared(shared);
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#setTitle(java.lang.String)
	 */
	public void setTitle(String newTitle) {
		this.report.setTitle(newTitle);
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getActions()
	 */
	public String getActions() {
		String result = null;
		ActionList actionList = this.report.getActions();
		if (actionList != null) {
			ActionType[] actions = actionList.getActions();
			if (actions != null) {
				int size = actions.length;
				List tempList = new ArrayList();
				for (int i = 0; i < size; i++) {
					try {
						ActionEnumType currentActionType = ActionEnumType
								.getActionEnum(actions[i].getActionType());
						UIActionType uiType = UIActionType
								.getUIActionType(currentActionType.getName());
						String displayValue = UIActionType
								.getDisplayValue(uiType);
						tempList.add(displayValue);
					} catch (IllegalArgumentException e) {
						IComponentManager compMgr = ComponentManagerFactory
								.getComponentManager();
						final IDestinyConfigurationStore configMgr = (IDestinyConfigurationStore) compMgr
								.getComponent(DestinyConfigurationStoreImpl.COMP_INFO);
						IActionListConfigDO actionListConfig = configMgr
								.retrieveActionListConfig();
						IActionConfigDO[] actionDos = actionListConfig
								.getActions();
						for (IActionConfigDO item : actionDos) {
							if (item.getName().equals(actions[i])) {
								tempList.add(item.getDisplayName());
								break;
							}
						}
					}
				}
				String[] stringArray = new String[tempList.size()];
				tempList.toArray(stringArray);
				StringList stringList = new StringList();
				stringList.setValues(stringArray);
				result = ExpressionCutter.convertFromStringList(stringList);
			}
		}
		if (result == null) {
			Locale currentLocale = FacesContext.getCurrentInstance()
					.getViewRoot().getLocale();
			ResourceBundle inquiryCenterBundle = ResourceBundle.getBundle(
					INQUIRY_CENTER_BUNDLE_NAME, currentLocale);
			result = inquiryCenterBundle.getString(ANY_ACTION_BUNDLE_KEY);
		}
		return result;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getActionsAsList()
	 */
	public List getActionsAsList() {
		List selectedActions = new ArrayList();
		ActionList actionList = this.report.getActions();
		if (actionList != null) {
			ActionType[] actions = actionList.getActions();
			if (actions != null) {
				int size = actions.length;
				for (int i = 0; i < size; i++) {
					try {
						UIActionType uiType = UIActionType
								.getUIActionType(actions[i].getActionType());
						selectedActions.add(uiType.getName());
					} catch (Exception ex) {
						selectedActions.add(actions[i]);
					}
				}
			}
		}
		return selectedActions;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getBeginDate()
	 */
	public Date getBeginDate() {
		Calendar calendar = this.report.getBeginDate();
		if (calendar == null) {
		    Date endDate = getEndDate();
		    calendar = GregorianCalendar.getInstance();
		    calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, -6);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
		}
		return (calendar.getTime());
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getEndDate()
	 */
	public Date getEndDate() {
		Calendar calendar = this.report.getEndDate();
		if (calendar == null) {
    		calendar = GregorianCalendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
		}
		return (calendar.getTime());
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getEnforcements()
	 */
	public String getEnforcements() {
		String result = null;
		EffectList effectList = this.report.getEffects();
		Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		if (effectList != null) {
			EffectType[] effects = effectList.getValues();
			if (effects != null) {
				int size = effects.length;
				String[] stringArray = new String[size];
				for (int i = 0; i < size; i++) {
					PolicyDecisionEnumType policyDecision = PolicyDecisionEnumType
							.getPolicyDecisionEnum(effects[i].getValue());
					UIEnforcementType uiType = UIEnforcementType
							.getUIEnforcementType(policyDecision.getName());
					stringArray[i] = UIEnforcementType.getDisplayValue(uiType);
				}
				StringList stringList = new StringList();
				stringList.setValues(stringArray);
				result = ExpressionCutter.convertFromStringList(stringList);
			}
		}
		if (result == null) {
			ResourceBundle inquiryCenterBundle = ResourceBundle.getBundle(
					INQUIRY_CENTER_BUNDLE_NAME, currentLocale);
			result = inquiryCenterBundle.getString(ANY_ENFORCEMENT_BUNDLE_KEY);
		}
		return result;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getEnforcementsAsList()
	 */
	public List getEnforcementsAsList() {
		List currentEffects = new ArrayList();
		EffectList effectList = this.report.getEffects();
		if (effectList != null) {
			EffectType[] effects = effectList.getValues();
			if (effects != null) {
				int size = effects.length;
				for (int i = 0; i < size; i++) {
					UIEnforcementType uiType = UIEnforcementType
							.getUIEnforcementType(effects[i].getValue());
					currentEffects.add(uiType.getName());
				}
			}
		}
		if (currentEffects.size() == 0) {
			UIEnforcementType uiType = UIEnforcementType
					.getUIEnforcementType(UIEnforcementType.ENFORCEMENT_ALLOW_BUNDLE_KEY
							.getName());
			currentEffects.add(uiType.getName());
		}
		return currentEffects;
	}

	public String getEnforcement() {
		List currentEnforcement = this.getEnforcementsAsList();
		String result = null;
		if (currentEnforcement.size() == 1) {
			if (currentEnforcement
					.contains(UIEnforcementType.ENFORCEMENT_ALLOW_BUNDLE_KEY
							.getName())) {
				result = UIEnforcementType.ENFORCEMENT_ALLOW_BUNDLE_KEY
						.getName();
				this.enforcement = result;
			} else {
				result = UIEnforcementType.ENFORCEMENT_DENY_BUNDLE_KEY
						.getName();
				this.enforcement = result;
			}
		} else if (currentEnforcement.size() == 2) {
			result = UIEnforcementType.ENFORCEMENT_BOTH_BUNDLE_KEY.getName();
			this.enforcement = result;
		} else {
			result = UIEnforcementType.ENFORCEMENT_ALLOW_BUNDLE_KEY.getName();
			this.enforcement = result;
		}
		return result;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getEnforcementChoices()
	 */
	public List getEnforcementChoices() {
		return ENFORCEMENT_CHOICES;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getGroupBy()
	 */
	public String getGroupBy() {
		ReportGroupByType groupByType = getGroupByType();
		int groupByTypeEnum = groupByType.getType();
		Integer groupByTypeEnumVal = new Integer(groupByTypeEnum);
		String result = groupByTypeEnumVal.toString();
		return result;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getGroupByPolicyChoices()
	 */
	public List getGroupByPolicyChoices() {
		return POLICY_GROUP_BY_CHOICES;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getGroupByTrackingChoices()
	 */
	public List getGroupByTrackingChoices() {
		return TRACKING_GROUP_BY_CHOICES;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getLoggingLevelChoices()
	 */
	public List getLoggingLevelChoices() {
		return LOGGING_LEVEL_CHOICES;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getGroupByType()
	 */
	public ReportGroupByType getGroupByType() {
		ReportGroupByType result = null;
		ReportSummaryType wsGroupType = this.report.getSummaryType();
		// Need manual conversion here, since at the UI, there is only group by
		// time, and not group by days or months.
		if (ReportSummaryType.TimeDays.equals(wsGroupType)
				|| ReportSummaryType.TimeMonths.equals(wsGroupType)) {
			result = ReportGroupByType.TIME;
		} else {
			result = ReportGroupByType.getReportGroupByEnum(wsGroupType
					.getValue());
		}
		return result;
	}

	/**
	 * Returns the log object
	 * 
	 * @return the log object
	 */
	protected Log getLog() {
		return LOG;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getPolicyActionChoices()
	 */
	public List getPolicyActionChoices() {
		return (List) TARGET_DATA_2_ACTION_CHOICES
				.get(ReportTargetType.PolicyEvents);
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getPolicies()
	 */
	public String getPolicies() {
		String policies = ExpressionCutter.convertFromStringList(this.report
				.getPolicies());
		if (policies == null || policies.length() == 0) {
			ResourceBundle bundle = ResourceBundle.getBundle(
					INQUIRY_CENTER_BUNDLE_NAME, FacesContext
							.getCurrentInstance().getViewRoot().getLocale());
			policies = bundle.getString(ANY_POLICY_BUNDLE_KEY);
		}
		return policies;
	}

	public String getSelectedPolicies() {
		String policies = ExpressionCutter.convertFromStringList(this.report
				.getPolicies());
		if (policies == null || policies.length() == 0) {
			ResourceBundle bundle = ResourceBundle.getBundle(
					INQUIRY_CENTER_BUNDLE_NAME, FacesContext
							.getCurrentInstance().getViewRoot().getLocale());
			policies = bundle.getString(ANY_POLICY_BUNDLE_KEY);
		} 
		return policies;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getResources()
	 */
	public String getResources() {
		String result = null;
		StringList resources = this.report.getResourceNames();
		if (resources != null) 
			result = StringUtils.join(resources.getValues());

		if (result == null || result.length() == 0) {
			ResourceBundle bundle = ResourceBundle.getBundle(
					INQUIRY_CENTER_BUNDLE_NAME, FacesContext
							.getCurrentInstance().getViewRoot().getLocale());
			result = bundle.getString(ANY_RESOURCE_BUNDLE_KEY);
		}
		return result;
	}

	public String getSelectedResources() {
		String resources = ExpressionCutter.convertFromStringList(this.report
				.getResourceNames());
		if (resources == null || resources.length() == 0) {
			ResourceBundle bundle = ResourceBundle.getBundle(
					INQUIRY_CENTER_BUNDLE_NAME, FacesContext
							.getCurrentInstance().getViewRoot().getLocale());
			resources = bundle.getString(ANY_RESOURCE_BUNDLE_KEY);
		} 
		return resources;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getTargetDataChoices()
	 */
	public List getTargetDataChoices() {
		return TARGET_DATA_CHOICES;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getTargetDisplayName()
	 */
	public String getTargetDisplayName() {
		String targetName = getTargetData();
		ReportTargetUIType targetType = ReportTargetUIType
				.getByName(targetName);
		String displayName = ReportTargetUIType.getDisplayValue(targetType);
		return displayName;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getTargetData()
	 */
	public String getTargetData() {
		ReportTargetType currentTarget = this.report.getTarget();
		if (currentTarget == null) {
			// Set a default target:
			currentTarget = ReportTargetType.PolicyEvents;
		}
		String targetName = ReportTargetUIType.getByName(
				currentTarget.getValue()).getName();
		return targetName;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getTitle()
	 */
	public String getTitle() {
		if (this.report.getTitle() != null) {
			return this.report.getTitle();
		} else {
			return QUICK_REPORT_NAME;
		}
	}

	public String getProcessedTitle() {
		if (this.getOwned()) {
			return this.report.getTitle();
		} else {
			return this.report.getTitle() + " (S)";
		}
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getTrackingActionChoices()
	 */
	public List getTrackingActionChoices() {
		return (List) TARGET_DATA_2_ACTION_CHOICES
				.get(ReportTargetType.ActivityJournal);
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#getUsers()
	 */
	public String getUsers() {
		String users = ExpressionCutter.convertFromStringList(this.report
				.getUsers());
		if (users == null || users.length() == 0) {
			ResourceBundle bundle = ResourceBundle.getBundle(
					INQUIRY_CENTER_BUNDLE_NAME, FacesContext
							.getCurrentInstance().getViewRoot().getLocale());
			users = bundle.getString(ANY_USER_BUNDLE_KEY);
		}
		return users;
	}

	public String getSelectedUsers() {
		String users = ExpressionCutter.convertFromStringList(this.report
				.getUsers());
		if (users == null || users.length() == 0) {
			ResourceBundle bundle = ResourceBundle.getBundle(
					INQUIRY_CENTER_BUNDLE_NAME, FacesContext
							.getCurrentInstance().getViewRoot().getLocale());
			users = bundle.getString(ANY_USER_BUNDLE_KEY);
		} 
		return users;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getLoggingLevel()
	 */
	public int getLoggingLevel() {
		return this.report.getLoggingLevel();
	}

	/**
	 * Retrieve the UI display string for the logging level
	 * 
	 * @return the UI display string for the logging level
	 */
	public String getLoggingLevelUIDisplay() {
		String loggingLevelString;
		if (this.report.getLoggingLevel() == 1) {
			loggingLevelString = ReportLoggingLevelUIType
					.getDisplayValue(ReportLoggingLevelUIType.LOGGING_LEVEL_ALL);
		} else if (this.report.getLoggingLevel() == 2) {
			loggingLevelString = ReportLoggingLevelUIType
					.getDisplayValue(ReportLoggingLevelUIType.LOGGING_LEVEL_APPLICATION);
		} else {
			loggingLevelString = ReportLoggingLevelUIType
					.getDisplayValue(ReportLoggingLevelUIType.LOGGING_LEVEL_USER);
		}
		return loggingLevelString;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#getRunTime()
	 */
	public Date getRunTime() {
		return this.runTime;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#isPolicyActivitySelected()
	 */
	public boolean isPolicyActivitySelected() {
		return ReportTargetType.PolicyEvents.equals(this.report.getTarget());
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#isTrackingActivitySelected()
	 */
	public boolean isTrackingActivitySelected() {
		return !isPolicyActivitySelected();
	}

	/**
	 * Retrieve the wrapped report instance
	 * 
	 * @return the wrapped report instance
	 */
	Report getWrappedReport() {
		return this.report;
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#setActionsAsList(java.util.List)
	 */
	public void setActionsAsList(List userSelectedActions) {
		ActionList actionList = new ActionList();
		Iterator iter = userSelectedActions.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			ActionType actionType = new ActionType();
			actionType.setActionType((String) iter.next());
			actionList.addActions(actionType);
		}

		this.report.setActions(actionList);
	}

	/**
	 * In this implementation, the begin date always starts at 0:00:00:000 and
	 * finishes at 23:59:59:999
	 * 
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#setBeginDate(java.util.Date)
	 */
	public void setBeginDate(Date newStartDate) {
		if (newStartDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(newStartDate.getTime());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			this.report.setBeginDate(cal);
		} else {
			this.report.setBeginDate(null);
		}
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#setEndDate(java.util.Date)
	 */
	public void setEndDate(Date newEndDate) {
		if (newEndDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(newEndDate.getTime());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			this.report.setEndDate(cal);
		} else {
			this.report.setEndDate(null);
		}
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#setEnforcements(java.util.List)
	 */
	public void setEnforcementsAsList(List enforcements) {
		// Need to separate effects from obligations
		// In release 1.0, this is only about effects, so no separation required
		EffectType[] effectTypeArray = new EffectType[enforcements.size()];
		Iterator iter = enforcements.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			EffectType type = EffectType.Factory.fromValue((String) iter.next());
			effectTypeArray[i] = type;
		}

		EffectList effectList = new EffectList();
		effectList.setValues(effectTypeArray);
		this.report.setEffects(effectList);
	}

	public void setEnforcement(String enforcement) {
		List<String> enforcementList = new ArrayList<String>();
		if (enforcement != null) {
			if (enforcement
					.equals(UIEnforcementType.ENFORCEMENT_ALLOW_BUNDLE_KEY
							.getName())) {
				UIEnforcementType uiType = UIEnforcementType
						.getUIEnforcementType(UIEnforcementType.ENFORCEMENT_ALLOW_BUNDLE_KEY
								.getName());
				enforcementList.add(uiType.getName());
			} else if (enforcement
					.equals(UIEnforcementType.ENFORCEMENT_DENY_BUNDLE_KEY
							.getName())) {
				UIEnforcementType uiType = UIEnforcementType
						.getUIEnforcementType(UIEnforcementType.ENFORCEMENT_DENY_BUNDLE_KEY
								.getName());
				enforcementList.add(uiType.getName());
			} else { // both
				UIEnforcementType uiAllowType = UIEnforcementType
						.getUIEnforcementType(UIEnforcementType.ENFORCEMENT_ALLOW_BUNDLE_KEY
								.getName());
				enforcementList.add(uiAllowType.getName());
				UIEnforcementType uiDenyType = UIEnforcementType
						.getUIEnforcementType(UIEnforcementType.ENFORCEMENT_DENY_BUNDLE_KEY
								.getName());
				enforcementList.add(uiDenyType.getName());
			}
		} else {
			UIEnforcementType uiType = UIEnforcementType
					.getUIEnforcementType(UIEnforcementType.ENFORCEMENT_ALLOW_BUNDLE_KEY
							.getName());
			enforcementList.add(uiType.getName());
		}
		setEnforcementsAsList(enforcementList);
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#setGroupBy(java.lang.String)
	 */
	public void setGroupBy(String newGroupBy) {
		int iNewGroupBy = ReportGroupingUIType.UI_GROUPING_NONE.getType();
		try {
			iNewGroupBy = (new Integer(newGroupBy)).intValue();
		} catch (NumberFormatException exception) {
			// In response to bug #3454. Not sure in what scenario this happens,
			// but adding this check to be robust
			LOG
					.warn(
							"Group by is invalid number.  Resulting report format may not be as expected",
							exception);
		}
		ReportGroupingUIType uiType = ReportGroupingUIType
				.getReportGroupingUIType(iNewGroupBy);
		ReportGroupByType groupByType = ReportGroupByType
				.getReportGroupByEnum(uiType.getName());
		if (ReportGroupByType.TIME.equals(groupByType)) {
			this.report.setSummaryType(ReportSummaryType.TimeDays);
		} else {
			// Converts automatically
			this.report.setSummaryType(ReportSummaryType.Factory.fromValue(groupByType
					.getName()));
		}
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#setPolicies(java.lang.String)
	 */
	public void setPolicies(String policyExpr) {
		this.report.setPolicies(convertInputToStringList(policyExpr.replace(
				"<wbr>", ""), ANY_POLICY_BUNDLE_KEY));
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#setResources(java.lang.String)
	 */
	public void setResources(String resourceExpr) {
		StringList result = new StringList();
		String[] values = new String[1];
		if ((resourceExpr != null) && (!resourceExpr.isEmpty()))
			values[0] = resourceExpr.replace("<wbr>", "");
		else {
			ResourceBundle bundle = ResourceBundle
					.getBundle(INQUIRY_CENTER_BUNDLE_NAME, FacesContext
							.getCurrentInstance().getViewRoot().getLocale());
			values[0] = bundle.getString(ANY_RESOURCE_BUNDLE_KEY);
		}
			
		result.setValues(values);
		this.report.setResourceNames(result);
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#setTargetData(java.lang.String)
	 */
	public void setTargetData(String targetData) {
		ReportTargetUIType selectedTarget = ReportTargetUIType
				.getByName(targetData);
		ReportTargetType selectedWSTarget = ReportTargetType.Factory
				.fromValue(selectedTarget.getName());
		this.report.setTarget(selectedWSTarget);
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.ISavedReport#setUsers(java.lang.String)
	 */
	public void setUsers(String usersExpr) {
		this.report.setUsers(convertInputToStringList(usersExpr.replace(
				"<wbr>", ""), ANY_USER_BUNDLE_KEY));
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#setLoggingLevel(int)
	 */
	public void setLoggingLevel(int level) {
		this.report.setLoggingLevel(level);
	}

	/**
	 * @see com.bluejungle.destiny.inquirycenter.report.IReport#setRunTime(java.util.Date)
	 */
	public void setRunTime(Date runTime) {
		this.runTime = runTime;
	}

	/**
	 * Replace characters having special meaning <em>inside</em> HTML tags
	 * with their escaped equivalents, using character entities such as
	 * <tt>'&amp;'</tt>.
	 * 
	 * <P>
	 * The escaped characters are :
	 * <ul>
	 * <li> <
	 * <li> >
	 * <li> "
	 * <li> '
	 * <li> \
	 * <li> &
	 * </ul>
	 * 
	 * <P>
	 * This method ensures that arbitrary text appearing inside a tag does not
	 * "confuse" the tag. For example, <tt>HREF='Blah.do?Page=1&Sort=ASC'</tt>
	 * does not comply with strict HTML because of the ampersand, and should be
	 * changed to <tt>HREF='Blah.do?Page=1&amp;Sort=ASC'</tt>. This is
	 * commonly seen in building query strings. (In JSTL, the c:url tag performs
	 * this task automatically.)
	 */
	public static String forHTMLTag(String aTagFragment) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				aTagFragment);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\'') {
				result.append("&#039;");
			} else if (character == '\\') {
				result.append("&#092;");
			} else if (character == '&') {
				result.append("&amp;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	public static String forUnescapedHTML(String escapedString) {
		escapedString = escapedString.replace("&lt;", "<");
		escapedString = escapedString.replace("&gt;", ">");
		escapedString = escapedString.replace("&quot;", "\"");
		escapedString = escapedString.replace("&#039;", "\'");
		escapedString = escapedString.replace("&#092;", "\\");
		escapedString = escapedString.replace("&amp;", "&");

		return escapedString;
	}
}