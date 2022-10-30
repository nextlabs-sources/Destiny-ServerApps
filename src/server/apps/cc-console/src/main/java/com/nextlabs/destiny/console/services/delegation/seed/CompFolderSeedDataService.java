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
public class CompFolderSeedDataService implements DelegationSeedDataService {

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
		// Actions for Component Folder
		ActionConfig viewComp = getActionConfig(
				msgBundle.getText("action.view.name", DASeedDataService.COMPONENT_FOLDER),
				DelegationModelActions.VIEW_COMPONENT_FOLDER);
		delegateModel.getActions().add(viewComp);

		ActionConfig renameComp = getActionConfig(
				msgBundle.getText("action.rename.name", DASeedDataService.COMPONENT_FOLDER),
				DelegationModelActions.RENAME_COMPONENT_FOLDER);
		delegateModel.getActions().add(renameComp);

		ActionConfig deleteComp = getActionConfig(
				msgBundle.getText("action.delete.name", DASeedDataService.COMPONENT_FOLDER),
				DelegationModelActions.DELETE_COMPONENT_FOLDER);
		delegateModel.getActions().add(deleteComp);

		ActionConfig createComp = getActionConfig(
				msgBundle.getText("action.create.name", DASeedDataService.COMPONENT_FOLDER),
				DelegationModelActions.CREATE_COMPONENT_FOLDER);
		delegateModel.getActions().add(createComp);

		ActionConfig moveComp = getActionConfig(
				msgBundle.getText("action.move.name", DASeedDataService.COMPONENT_FOLDER),
				DelegationModelActions.MOVE_COMPONENT_FOLDER);
		delegateModel.getActions().add(moveComp);

		return delegateModel;
	}

	/**
	 * Returns the map containing allowed actions for Folder Delegation Model
	 * 
	 */
	@Override
	public Map<String, String> getActions() {
		Map<String, String> actionsMap = new HashMap<>();
		
		// Component Folder Actions
		actionsMap.put(DelegationModelActions.VIEW_COMPONENT_FOLDER,
				msgBundle.getText("action.view.name", DASeedDataService.COMPONENT_FOLDER));
		actionsMap.put(DelegationModelActions.RENAME_COMPONENT_FOLDER,
				msgBundle.getText("action.rename.name", DASeedDataService.COMPONENT_FOLDER));
		actionsMap.put(DelegationModelActions.DELETE_COMPONENT_FOLDER,
				msgBundle.getText("action.delete.name", DASeedDataService.COMPONENT_FOLDER));
		actionsMap.put(DelegationModelActions.CREATE_COMPONENT_FOLDER,
				msgBundle.getText("action.create.name", DASeedDataService.COMPONENT_FOLDER));
		actionsMap.put(DelegationModelActions.MOVE_COMPONENT_FOLDER,
				msgBundle.getText("action.move.name", DASeedDataService.COMPONENT_FOLDER));

		return actionsMap;
	}

}
