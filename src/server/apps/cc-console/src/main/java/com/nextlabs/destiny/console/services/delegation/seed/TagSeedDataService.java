package com.nextlabs.destiny.console.services.delegation.seed;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.services.MessageBundleService;

/**
 * Creates seed data for Tag Management module
 * 
 * @author Moushumi Seal
 *
 */
@Service
public class TagSeedDataService implements DelegationSeedDataService {

	@Autowired
	protected MessageBundleService msgBundle;
	
	private static final String POLICY_TAGS = "Policy Tags";
	private static final String POLICY_MODEL_TAGS = "Policy Model Tags";
	private static final String COMPONENT_TAGS = "Component Tags";

	/*
	 * (non-Javadoc)
	 * @see DelegationSeedDataService#addActionConfig(DelegateModel)
	 */
	@Override
	public DelegateModel addActionConfig(DelegateModel delegateModel) {
		ActionConfig createPolicyTags = getActionConfig(msgBundle.getText("action.create.name", POLICY_TAGS),
				DelegationModelActions.CREATE_POLICY_TAG);
		delegateModel.getActions().add(createPolicyTags);

		ActionConfig createComponentTags = getActionConfig(msgBundle.getText("action.create.name", COMPONENT_TAGS),
				DelegationModelActions.CREATE_COMPONENT_TAG);
		delegateModel.getActions().add(createComponentTags);

		ActionConfig createPMTags = getActionConfig(msgBundle.getText("action.create.name", POLICY_MODEL_TAGS),
				DelegationModelActions.CREATE_POLICY_MODEL_TAG);
		delegateModel.getActions().add(createPMTags);

		return delegateModel;
	}

	/*
	 * (non-Javadoc)
	 * @see DelegationSeedDataService#getActions()
	 */
	@Override
	public Map<String, String> getActions() {
		Map<String, String> actionsMap = new HashMap<>();

		actionsMap.put(DelegationModelActions.CREATE_POLICY_TAG,
				msgBundle.getText("action.create.name", POLICY_TAGS));

		actionsMap.put(DelegationModelActions.CREATE_COMPONENT_TAG,
				msgBundle.getText("action.create.name", COMPONENT_TAGS));

		actionsMap.put(DelegationModelActions.CREATE_POLICY_MODEL_TAG,
				msgBundle.getText("action.create.name", POLICY_MODEL_TAGS));

		return actionsMap;
	}

}
