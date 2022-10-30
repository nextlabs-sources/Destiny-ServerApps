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
 * Creates seed data for Delegated Policy module
 * 
 * @author Moushumi Seal
 *
 */
@Service
public class PolicySeedDataService implements DelegationSeedDataService {

	@Autowired
	protected MessageBundleService msgBundle;

	/**
	 * Initializes ActionConfig for Policy Delegation Model
	 * 
	 * @param DelegateModel
	 *            entity
	 * @return DelegateModel entity
	 */
	@Override
	public DelegateModel addActionConfig(DelegateModel delegateModel) {
		ActionConfig view = getActionConfig(msgBundle.getText("action.view.name", DASeedDataService.POLICY),
				DelegationModelActions.VIEW_POLICY);
		delegateModel.getActions().add(view);

		ActionConfig edit = getActionConfig(msgBundle.getText("action.edit.name", DASeedDataService.POLICY),
				DelegationModelActions.EDIT_POLICY);
		delegateModel.getActions().add(edit);

		ActionConfig deploy = getActionConfig(msgBundle.getText("action.deploy.name", DASeedDataService.POLICY),
				DelegationModelActions.DEPLOY_POLICY);
		delegateModel.getActions().add(deploy);

		ActionConfig delete = getActionConfig(msgBundle.getText("action.delete.name", DASeedDataService.POLICY),
				DelegationModelActions.DELETE_POLICY);
		delegateModel.getActions().add(delete);

		ActionConfig create = getActionConfig(msgBundle.getText("action.create.name", DASeedDataService.POLICY),
				DelegationModelActions.CREATE_POLICY);
		delegateModel.getActions().add(create);

		ActionConfig move = getActionConfig(msgBundle.getText("action.move.name", DASeedDataService.POLICY),
				DelegationModelActions.MOVE_POLICY);
		delegateModel.getActions().add(move);

		ActionConfig migrate = getActionConfig(msgBundle.getText("action.migrate.name", DASeedDataService.POLICY),
				DelegationModelActions.MIGRATE_POLICY);
		delegateModel.getActions().add(migrate);

		return delegateModel;
	}

	/**
	 * Returns the map containing allowed actions for Policy Delegation Model
	 * 
	 */
	@Override
	public Map<String, String> getActions() {
		Map<String, String> actionsMap = new HashMap<>();

		actionsMap.put(DelegationModelActions.VIEW_POLICY,
				msgBundle.getText("action.view.name", DASeedDataService.POLICY));
		actionsMap.put(DelegationModelActions.EDIT_POLICY,
				msgBundle.getText("action.edit.name", DASeedDataService.POLICY));
		actionsMap.put(DelegationModelActions.DELETE_POLICY,
				msgBundle.getText("action.delete.name", DASeedDataService.POLICY));
		actionsMap.put(DelegationModelActions.DEPLOY_POLICY,
				msgBundle.getText("action.deploy.name", DASeedDataService.POLICY));
		actionsMap.put(DelegationModelActions.CREATE_POLICY,
				msgBundle.getText("action.create.name", DASeedDataService.POLICY));
		actionsMap.put(DelegationModelActions.MOVE_POLICY,
				msgBundle.getText("action.move.name", DASeedDataService.POLICY));
		actionsMap.put(DelegationModelActions.MIGRATE_POLICY,
				msgBundle.getText("action.migrate.name", DASeedDataService.POLICY));

		return actionsMap;
	}

}
