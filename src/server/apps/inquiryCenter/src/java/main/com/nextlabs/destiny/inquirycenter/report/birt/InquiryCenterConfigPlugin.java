/** Created on May 18, 2009
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 * 
 * This is invoked by JavaScript scripts in BIRT reports to read action list from configuration.xml.
 * This has a Singleton pattern to maintain only one copy of action list.
 */

package com.nextlabs.destiny.inquirycenter.report.birt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers.DBUtil;
import com.bluejungle.destiny.server.shared.configuration.IActionConfigDO;
import com.bluejungle.destiny.server.shared.configuration.IActionListConfigDO;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyConfigurationStoreImpl;
import com.bluejungle.framework.configuration.IDestinyConfigurationStore;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import com.nextlabs.destiny.inquirycenter.report.birt.datagen.InquiryCenterAction;


/**
 * @author Nao
 */

public class InquiryCenterConfigPlugin {
	
	  private static final Log log =
		        LogFactory.getLog(InquiryCenterConfigPlugin.class);
	
	// singleton 
	private static final InquiryCenterConfigPlugin INSTANCE = new InquiryCenterConfigPlugin();
	private static boolean hasReadConfig;
	
	private static ConcurrentHashMap<String, InquiryCenterAction> actionsMap;
	private static ConcurrentHashMap<Long, String> policyModelMap;
	
	private static final ScheduledExecutorService execService = Executors.newScheduledThreadPool(1);

	// Private constructor prevents instantiation from other classes
	private InquiryCenterConfigPlugin() {
		hasReadConfig = false;
	}

	// get the instance.  There's only one instance.
	// This method is synchronized so that it reads actions from configuration only once.	
	public static synchronized InquiryCenterConfigPlugin getInstance() {
		if (INSTANCE.hasReadConfig == false) {
			readConfig();
		}
		
		refreshActionsList();
		
		return INSTANCE;
	}

	public static Map<String, InquiryCenterAction> getActions() {
		return actionsMap;
	}
	
	public static Map<Long, String> getPolicyModels() {
		return policyModelMap;
	}

	private static void readConfig() {
		
		if (actionsMap == null) {
			actionsMap = new ConcurrentHashMap<String, InquiryCenterAction>();
		}
		
		if (policyModelMap == null) {
			policyModelMap = new ConcurrentHashMap<Long, String>();
		}

		// hardcoded actions - this list has the same actions with
		// BirtReportTransform.java
		InquiryCenterAction changeAttribute = new InquiryCenterAction("Change Attribute");
		actionsMap.put("Ca", changeAttribute);

		InquiryCenterAction changeFilePerm = new InquiryCenterAction("Change File Permissions");
		actionsMap.put("Cs", changeFilePerm);

		InquiryCenterAction copyFile = new InquiryCenterAction("Copy / Embed File");
		actionsMap.put("Co", copyFile);

		InquiryCenterAction copyContent = new InquiryCenterAction("Copy Content");
		actionsMap.put("CP", copyContent);

		InquiryCenterAction delete = new InquiryCenterAction("Delete");
		actionsMap.put("De", delete);

		InquiryCenterAction embed = new InquiryCenterAction("Embed");
		actionsMap.put("Em", embed);

		InquiryCenterAction move = new InquiryCenterAction("Move");
		actionsMap.put("Mo", move);

		InquiryCenterAction print = new InquiryCenterAction("Print");
		actionsMap.put("Pr", print);

		InquiryCenterAction open = new InquiryCenterAction("Open");
		actionsMap.put("Op", open);

		InquiryCenterAction edit = new InquiryCenterAction("Create / Edit");
		actionsMap.put("Ed", edit);

		InquiryCenterAction rename = new InquiryCenterAction("Rename");
		actionsMap.put("Rn", rename);

		InquiryCenterAction email = new InquiryCenterAction("Email");
		actionsMap.put("SE", email);

		InquiryCenterAction instantMessage = new InquiryCenterAction("Instant Message");
		actionsMap.put("SI", instantMessage);

		InquiryCenterAction export = new InquiryCenterAction("Export");
		actionsMap.put("Ex", export);

		InquiryCenterAction attach = new InquiryCenterAction("Attach to Item");
		actionsMap.put("At", attach);

		InquiryCenterAction run = new InquiryCenterAction("Run");
		actionsMap.put("Ru", run);

		InquiryCenterAction voiceCall = new InquiryCenterAction("Voice Call / Video Call");
		actionsMap.put("Av", voiceCall);

		InquiryCenterAction meet = new InquiryCenterAction("Meet");
		actionsMap.put("Me", meet);

		InquiryCenterAction presence = new InquiryCenterAction("Presence");
		actionsMap.put("Ps", presence);

		// added 6 more that exists in Reporter GUI
		InquiryCenterAction share = new InquiryCenterAction("Share");
		actionsMap.put("Sh", share);

		InquiryCenterAction record = new InquiryCenterAction("Record");
		actionsMap.put("Re", record);

		InquiryCenterAction question = new InquiryCenterAction("Ask Question");
		actionsMap.put("Qu", question);

		InquiryCenterAction join = new InquiryCenterAction("Join");
		actionsMap.put("Jo", join);

		InquiryCenterAction video = new InquiryCenterAction("Video");
		actionsMap.put("Vi", video);

		InquiryCenterAction voice = new InquiryCenterAction("Voice");
		actionsMap.put("Vo", voice);

		// get the actions created using console ui
		try {
			Map<String, ActionData> dbActions = getActionsList();
			for (String key : dbActions.keySet()) {
				
				ActionData actionData = dbActions.get(key);
				InquiryCenterAction inqAction = new InquiryCenterAction(actionData.name,
						InquiryCenterAction.ORIGIN_DB, actionData.policyModelId);
				actionsMap.put(key, inqAction);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		// get configuration
		IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
		IDestinyConfigurationStore configMgr = (IDestinyConfigurationStore) compMgr
				.getComponent(DestinyConfigurationStoreImpl.COMP_INFO);
		// read ActionList
		IActionListConfigDO actionListConfig = configMgr.retrieveActionListConfig();
		IActionConfigDO[] actions = actionListConfig.getActions();
		for (int i = 0; i < actions.length; i++) {
			InquiryCenterAction inqAction = new InquiryCenterAction(actions[i].getDisplayName());
			actionsMap.put(actions[i].getShortName(), inqAction);
		}
		
		
		// Load policy model data
		loadPolicyModelNames();

		hasReadConfig = true;
	}
		 
	
	static class ActionData {
		String shortCode;
		String name;
		Long policyModelId;
		
		ActionData(String shortCode, String name, Long policyModelId) {
			this.shortCode = shortCode;
			this.name = name;
			this.policyModelId = policyModelId;
		}
		
		
		static ActionData create(String shortCode, String name, Long policyModelId) {
			return new ActionData(shortCode, name, policyModelId);
		}
	}
	
	private static Map<String, ActionData> getActionsList() throws HibernateException, SQLException {

		Map<String, ActionData> dbActions = new HashMap<String, ActionData>();

		Session session = null;
		ResultSet rs = null;
		PreparedStatement getActionsStmt = null;
		Connection connection = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

            String sqlQuery = "SELECT act.SHORT_CODE as shortCode, act.NAME as name, act.plcy_model_id FROM PM_ACTION_CONFIG act " +
                              "WHERE plcy_model_id NOT IN " +
                              "(SELECT ID FROM POLICY_MODEL WHERE DISCRIMINATOR = 'DELEGATION' OR STATUS = 'IN_ACTIVE')" +
                              "ORDER BY act.SHORT_CODE ";
                
			getActionsStmt = connection.prepareStatement(sqlQuery);

			rs = getActionsStmt.executeQuery();

			while (rs.next()) {
				String key = rs.getString(1);
				String actionName = rs.getString(2);
				Long policyModelId = rs.getLong(3);

                // Should always be true, but the consumers of this data rely on it, so let's check
                if (key != null && actionName != null) {
                    dbActions.put(key, ActionData.create(key, actionName, policyModelId));
                }
			}

		} finally {
			if (rs != null)
				rs.close();
			if (getActionsStmt != null)
				getActionsStmt.close();
			if (connection != null)
				connection.close();
			if (session != null)
				session.close();
		}
		return dbActions;
	}
	
	
	private static Map<Long, String> loadPolicyModelNames()  {
		if (policyModelMap == null) {
			policyModelMap = new ConcurrentHashMap<Long, String>();
		}

		Session session = null;
		ResultSet rs = null;
		PreparedStatement getActionsStmt = null;
		Connection connection = null;
		try {
			IHibernateRepository dataSource = DBUtil.getActivityDataSource();
			session = dataSource.getSession();
			connection = session.connection();

			String sqlQuery = "SELECT pm.id, pm.name FROM policy_model pm where pm.discriminator = 'POLICY'  AND STATUS = 'ACTIVE'";
			getActionsStmt = connection.prepareStatement(sqlQuery);

			rs = getActionsStmt.executeQuery();

			while (rs.next()) {
				Long key = rs.getLong(1);
				String modelName = rs.getString(2);
				policyModelMap.put(key, modelName);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (getActionsStmt != null)
					getActionsStmt.close();
				if (connection != null)
					connection.close();
				if (session != null)
					session.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return policyModelMap;
	}
	
	private static void retrieveConsoleActionList() {
		try {
			Map<String, ActionData> dbActions = getActionsList();

			// remove all existing dbActions from actionsMap
			Set<String> existingDbAction = getActionsByType(InquiryCenterAction.ORIGIN_DB);
			for (String existAction : existingDbAction) {
				actionsMap.remove(existAction);
			}

			// add latest database actions to the actionsMap
			for (String key : dbActions.keySet()) {
				ActionData actionData = dbActions.get(key);
				InquiryCenterAction inqAction = new InquiryCenterAction(actionData.name,
						InquiryCenterAction.ORIGIN_DB, actionData.policyModelId);
				actionsMap.put(key, inqAction);
			}

		} catch (Exception e) {
			log.error("Error occured while refreshing actions list ", e);
		}

	}
	
	private static Set<String> getActionsByType(String type) {
		Set<String> dbActionsSet = new HashSet<String>();

		for (String key : actionsMap.keySet()) {
			InquiryCenterAction iqAction = actionsMap.get(key);
			if (iqAction != null && iqAction.getOrigin().equals(type)) {
				dbActionsSet.add(key);
			}
		}

		return dbActionsSet;
	}
	
	/**
	 * Scheduler that updates database actions list periodically
	 */
	public static void refreshActionsList() {
		int delay = 500;
		int period = 30000;

		execService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				retrieveConsoleActionList();
				log.debug("Action list refreshed successsfully ");
				loadPolicyModelNames();
				log.debug("Policy model list refreshed successsfully ");
			}
		}, delay, period, TimeUnit.MILLISECONDS);
	}
}

