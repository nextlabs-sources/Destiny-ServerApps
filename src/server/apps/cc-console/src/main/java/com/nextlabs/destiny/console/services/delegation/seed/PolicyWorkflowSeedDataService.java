package com.nextlabs.destiny.console.services.delegation.seed;

import com.nextlabs.destiny.console.dao.delegadmin.DelegateModelDao;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowLevel;
import com.nextlabs.destiny.console.repositories.WorkflowLevelRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions.MANAGE_POLICY_WORKFLOW_LEVEL;

/**
 * Creates seed data for Delegated Administration module
 * 
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
@Service
public class PolicyWorkflowSeedDataService implements DelegationSeedDataService {

	private DelegateModelDao delegateModelDao;

	protected MessageBundleService msgBundle;

	private WorkflowLevelRepository workflowLevelRepository;

	private static final String POLICY_WORKFLOW = "Policy Workflow";


	/**
	 * Initializes RESOURCE type Delegation Model
	 * 
	 * @return DelegateModel entity
	 *             thrown at any error
	 */
	public DelegateModel getPolicyWorkflowDelegateModel() {

		DelegateModel delegateModel = delegateModelDao.findByShortName(DelegationModelShortName.POLICY_WORKFLOW.name());

		if (delegateModel == null) {
			delegateModel = new DelegateModel(null, msgBundle.getText("module.policy.workflow.name"),
					DelegationModelShortName.POLICY_WORKFLOW.name(), msgBundle.getText("module.ps.desc", POLICY_WORKFLOW),
					PolicyModelType.DA_RESOURCE, Status.ACTIVE);
			addActionConfig(delegateModel);
			return delegateModel;
		} else {
			boolean modified = false;
			List<WorkflowLevel> workflowLevels = workflowLevelRepository.findAllByOrderByLevelOrderAsc();
			for (ActionConfig actionConfig : delegateModel.getActions()) {
				if (workflowLevels.parallelStream()
						.noneMatch(workflowLevel ->
								String.format(MANAGE_POLICY_WORKFLOW_LEVEL, workflowLevel.getLevelOrder())
										.equals(actionConfig.getShortName()))) {
					delegateModel.getActions().remove(actionConfig);
					modified = true;
				}
			}
			for (WorkflowLevel workflowLevel : workflowLevels) {
				if (delegateModel.getActions()
						.parallelStream()
						.noneMatch(actionConfig ->
								String.format(MANAGE_POLICY_WORKFLOW_LEVEL, workflowLevel.getLevelOrder())
								.equals(actionConfig.getShortName()))) {
					delegateModel.getActions().add(getActionConfig(msgBundle.getText("action.review.name", workflowLevel.getName()),
							String.format(MANAGE_POLICY_WORKFLOW_LEVEL, workflowLevel.getLevelOrder())));
					modified = true;
				}
			}
			return modified ? delegateModel : null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see DelegationSeedDataService#addActionConfig(DelegateModel)
	 */
	@Override
	public DelegateModel addActionConfig(DelegateModel delegateModel) {
		List<WorkflowLevel> workflowLevels = workflowLevelRepository.findAllByOrderByLevelOrderAsc();
		for (int i = 0; i < workflowLevels.size(); i++) {
			ActionConfig manageWorkflowLevel = getActionConfig(msgBundle.getText("action.review.name", workflowLevels.get(i).getName()),
					String.format(MANAGE_POLICY_WORKFLOW_LEVEL, i + 1));
			delegateModel.getActions().add(manageWorkflowLevel);
		}
		return delegateModel;
	}

	/*
	 * (non-Javadoc)
	 * @see DelegationSeedDataService#getActions()
	 */
	@Override
	public Map<String, String> getActions() {
		Map<String, String> actionsMap = new HashMap<>();
		List<WorkflowLevel> workflowLevels = workflowLevelRepository.findAllByOrderByLevelOrderAsc();
		for (int i = 0; i < workflowLevels.size(); i++) {
			actionsMap.put(String.format(MANAGE_POLICY_WORKFLOW_LEVEL, i + 1),
					msgBundle.getText("action.review.name", workflowLevels.get(i).getName()));
		}
		return actionsMap;
	}

	@Autowired
	public void setDelegateModelDao(DelegateModelDao delegateModelDao) {
		this.delegateModelDao = delegateModelDao;
	}

	@Autowired
	public void setMsgBundle(MessageBundleService msgBundle) {
		this.msgBundle = msgBundle;
	}

	@Autowired
	public void setWorkflowLevelRepository(WorkflowLevelRepository workflowLevelRepository) {
		this.workflowLevelRepository = workflowLevelRepository;
	}
}
