package com.nextlabs.destiny.console.services.delegation.seed;

import java.util.Map;

import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;

/**
 * Contains common operations to Create seed data for Delegated Administration
 * module
 * 
 * @author Moushumi Seal
 * 
 *
 */
public interface DelegationSeedDataService {
	
	/**
	 * Initializes ActionConfig for Delegation Model
	 * 
	 * @param DelegateModel
	 *            entity
	 * @return DelegateModel entity
	 */
	public DelegateModel addActionConfig(DelegateModel delegateModel);

	/**
	 * Returns the map containing allowed actions for a Delegation Model
	 * 
	 * @return A Map of actions 
	 */
	public Map<String, String> getActions();

	/**
	 * Returns a {@link ActionConfig} object
	 * 
	 * @param name
	 * @param shortName
	 * @return {@link ActionConfig} object
	 */
	public default ActionConfig getActionConfig(String name, String shortName) {

		ActionConfig action = new ActionConfig();
		action.setName(name);
		action.setShortName(shortName);

		return action;
	}
}
