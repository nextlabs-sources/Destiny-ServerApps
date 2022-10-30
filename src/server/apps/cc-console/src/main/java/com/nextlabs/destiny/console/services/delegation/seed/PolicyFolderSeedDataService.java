package com.nextlabs.destiny.console.services.delegation.seed;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.services.DASeedDataService;
import com.nextlabs.destiny.console.services.MessageBundleService;

/**
 * Creates seed data for Delegated Folder module
 * 
 * @author Moushumi Seal
 *
 */
@Service
public class PolicyFolderSeedDataService implements DelegationSeedDataService {

	@Autowired
	protected MessageBundleService msgBundle;

	/**
	 * Initializes ActionConfig for Folder Delegation Model
	 * 
	 * @param DelegateModel
	 *            entity
	 * @return DelegateModel entity
	 */
	@Override
	public DelegateModel addActionConfig(DelegateModel delegateModel) {
		// Actions for Policy Folder
		ActionConfig viewPolicy = getActionConfig(
				msgBundle.getText("action.view.name", DASeedDataService.POLICY_FOLDER),
				DelegationModelActions.VIEW_POLICY_FOLDER);
		delegateModel.getActions().add(viewPolicy);

		ActionConfig renamePolicy = getActionConfig(
				msgBundle.getText("action.rename.name", DASeedDataService.POLICY_FOLDER),
				DelegationModelActions.RENAME_POLICY_FOLDER);
		delegateModel.getActions().add(renamePolicy);

		ActionConfig deletePolicy = getActionConfig(
				msgBundle.getText("action.delete.name", DASeedDataService.POLICY_FOLDER),
				DelegationModelActions.DELETE_POLICY_FOLDER);
		delegateModel.getActions().add(deletePolicy);

		ActionConfig createPolicy = getActionConfig(
				msgBundle.getText("action.create.name", DASeedDataService.POLICY_FOLDER),
				DelegationModelActions.CREATE_POLICY_FOLDER);
		delegateModel.getActions().add(createPolicy);

		ActionConfig movePolicy = getActionConfig(
				msgBundle.getText("action.move.name", DASeedDataService.POLICY_FOLDER),
				DelegationModelActions.MOVE_POLICY_FOLDER);
		delegateModel.getActions().add(movePolicy);

		return delegateModel;
	}

	/**
	 * Returns the map containing allowed actions for Folder Delegation Model
	 * 
	 */
	@Override
	public Map<String, String> getActions() {
		Map<String, String> actionsMap = new HashMap<>();

		// Policy Folder Actions
		actionsMap.put(DelegationModelActions.VIEW_POLICY_FOLDER,
				msgBundle.getText("action.view.name", DASeedDataService.POLICY_FOLDER));
		actionsMap.put(DelegationModelActions.RENAME_POLICY_FOLDER,
				msgBundle.getText("action.rename.name", DASeedDataService.POLICY_FOLDER));
		actionsMap.put(DelegationModelActions.DELETE_POLICY_FOLDER,
				msgBundle.getText("action.delete.name", DASeedDataService.POLICY_FOLDER));
		actionsMap.put(DelegationModelActions.CREATE_POLICY_FOLDER,
				msgBundle.getText("action.create.name", DASeedDataService.POLICY_FOLDER));
		actionsMap.put(DelegationModelActions.MOVE_POLICY_FOLDER,
				msgBundle.getText("action.move.name", DASeedDataService.POLICY_FOLDER));

		return actionsMap;
	}

}
