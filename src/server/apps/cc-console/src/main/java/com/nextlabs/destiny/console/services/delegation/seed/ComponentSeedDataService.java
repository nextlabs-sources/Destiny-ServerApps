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
 * Creates seed data for Delegated Component module
 * 
 * @author Moushumi Seal
 *
 */
@Service
public class ComponentSeedDataService implements DelegationSeedDataService {

	@Autowired
	protected MessageBundleService msgBundle;

	/**
	 * Initializes ActionConfig for Component Delegation Model
	 * 
	 * @param DelegateModel
	 *            entity
	 * @return DelegateModel entity
	 */
	@Override
	public DelegateModel addActionConfig(DelegateModel delegateModel) {
		ActionConfig view = getActionConfig(msgBundle.getText("action.view.name", DASeedDataService.COMPONENT),
				DelegationModelActions.VIEW_COMPONENT);
		delegateModel.getActions().add(view);

		ActionConfig edit = getActionConfig(msgBundle.getText("action.edit.name", DASeedDataService.COMPONENT),
				DelegationModelActions.EDIT_COMPONENT);
		delegateModel.getActions().add(edit);

		ActionConfig deploy = getActionConfig(msgBundle.getText("action.deploy.name", DASeedDataService.COMPONENT),
				DelegationModelActions.DEPLOY_COMPONENT);
		delegateModel.getActions().add(deploy);

		ActionConfig delete = getActionConfig(msgBundle.getText("action.delete.name", DASeedDataService.COMPONENT),
				DelegationModelActions.DELETE_COMPONENT);
		delegateModel.getActions().add(delete);

		ActionConfig create = getActionConfig(msgBundle.getText("action.create.name", DASeedDataService.COMPONENT),
				DelegationModelActions.CREATE_COMPONENT);
		delegateModel.getActions().add(create);

		ActionConfig move = getActionConfig(msgBundle.getText("action.move.name", DASeedDataService.COMPONENT),
				DelegationModelActions.MOVE_COMPONENT);
		delegateModel.getActions().add(move);

		return delegateModel;
	}

	/**
	 * Returns the map containing allowed actions for Component Delegation Model
	 * 
	 */
	@Override
	public Map<String, String> getActions() {
		Map<String, String> actionsMap = new HashMap<>();

		actionsMap.put(DelegationModelActions.VIEW_COMPONENT,
				msgBundle.getText("action.view.name", DASeedDataService.COMPONENT));
		actionsMap.put(DelegationModelActions.EDIT_COMPONENT,
				msgBundle.getText("action.edit.name", DASeedDataService.COMPONENT));
		actionsMap.put(DelegationModelActions.DELETE_COMPONENT,
				msgBundle.getText("action.delete.name", DASeedDataService.COMPONENT));
		actionsMap.put(DelegationModelActions.DEPLOY_COMPONENT,
				msgBundle.getText("action.deploy.name", DASeedDataService.COMPONENT));
		actionsMap.put(DelegationModelActions.CREATE_COMPONENT,
				msgBundle.getText("action.create.name", DASeedDataService.COMPONENT));
		actionsMap.put(DelegationModelActions.MOVE_COMPONENT,
				msgBundle.getText("action.move.name", DASeedDataService.COMPONENT));

		return actionsMap;
	}

}
