package com.nextlabs.destiny.console.services.delegation.seed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.console.dao.delegadmin.DelegateModelDao;
import com.nextlabs.destiny.console.dao.policy.OperatorConfigDao;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.services.MessageBundleService;

/**
 * Creates seed data for Delegated Administration module
 * 
 * @author Moushumi Seal
 *
 */
@Service
public class DAUserSeedDataService implements DelegationSeedDataService {

	@Autowired
	private DelegateModelDao delegateModelDao;

	@Autowired
	protected MessageBundleService msgBundle;

	@Autowired
	private OperatorConfigDao operatorConfigDao;
	
	private static final String DELEGATION_POLICIES= "Delegation Policies";
	
	private static final String USERS= "Users";


	/**
	 * Returns an Attribute entity
	 * 
	 * @param name
	 * @param shortName
	 * @param dataType
	 * @param operatorsList
	 * @return User Attributes
	 */
	public AttributeConfig getUserAttribute(String name, String shortName, DataType dataType,
			List<OperatorConfig> operatorsList) {

		AttributeConfig attrConfig = new AttributeConfig();

		Set<OperatorConfig> operators = new TreeSet<>(operatorsList);

		attrConfig.setName(name);
		attrConfig.setShortName(shortName);
		attrConfig.setDataType(dataType);
		attrConfig.setOperatorConfigs(operators);

		return attrConfig;
	}

	/**
	 * Returns the allowed conditions set for cc_user
	 * 
	 * @return set of conditions
	 */
	public Set<AttributeConfig> getDAUserAttributes() {

		Set<AttributeConfig> attributes = new TreeSet<>();

		List<OperatorConfig> strOpers = operatorConfigDao.findByDataType(DataType.STRING);
		List<OperatorConfig> multiValOpers = operatorConfigDao.findByDataType(DataType.MULTIVAL);

		AttributeConfig firstName = getUserAttribute(msgBundle.getText("attr.firstName.key"),
				msgBundle.getText("attr.firstName.key"), DataType.STRING, strOpers);
		attributes.add(firstName);

		AttributeConfig lastName = getUserAttribute(msgBundle.getText("attr.lastName.key"),
				msgBundle.getText("attr.lastName.key"), DataType.STRING, strOpers);
		attributes.add(lastName);

		AttributeConfig username = getUserAttribute(msgBundle.getText("attr.username.key"),
				msgBundle.getText("attr.username.key"), DataType.STRING, strOpers);
		attributes.add(username);

		AttributeConfig email = getUserAttribute(msgBundle.getText("attr.email.key"),
				msgBundle.getText("attr.email.key"), DataType.STRING, strOpers);
		attributes.add(email);

		AttributeConfig dept = getUserAttribute(msgBundle.getText("attr.dept.label"),
				msgBundle.getText("attr.dept.key"), DataType.STRING, strOpers);
		attributes.add(dept);

		AttributeConfig country = getUserAttribute(msgBundle.getText("attr.country.key"),
				msgBundle.getText("attr.country.label"), DataType.STRING, strOpers);
		attributes.add(country);

		AttributeConfig groups = getUserAttribute(msgBundle.getText("attr.groups.key"),
						msgBundle.getText("attr.groups.label"), DataType.MULTIVAL, multiValOpers);
		attributes.add(groups);

		return attributes;
	}

	/**
	 * Initializes SUBJECT type Delegation Model
	 * 
	 * @return DelegateModel entity
	 * @throws ConsoleException
	 *             thrown at any error
	 */
	public DelegateModel getDAUser() {

		DelegateModel delegateModel = delegateModelDao.findByShortName(DelegationModelShortName.DA_USER.name());

		if (delegateModel == null) {
			delegateModel = new DelegateModel(null, msgBundle.getText("policy.model.da.user"),
					DelegationModelShortName.DA_USER.name(), msgBundle.getText("policy.model.delegation.subject"),
					PolicyModelType.DA_SUBJECT, Status.ACTIVE);

			delegateModel.setAttributes(getDAUserAttributes());

			return delegateModel;
		} else {
			boolean modified = false;

			for(AttributeConfig attributeConfig : getDAUserAttributes()) {
				boolean exist = false;
				for(AttributeConfig createdAttribute : delegateModel.getAttributes()) {
					if(createdAttribute.getShortName().equals(attributeConfig.getShortName())) {
						exist = true;
						break;
					}
				}

				if(!exist) {
					delegateModel.getAttributes().add(attributeConfig);
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
		ActionConfig manageDelegatedAdmin = getActionConfig(
				msgBundle.getText("action.manage.name", "Delegation Policies"),
				DelegationModelActions.MANAGE_DELEGATED_ADMIN);
		delegateModel.getActions().add(manageDelegatedAdmin);

		ActionConfig manageUsers = getActionConfig(msgBundle.getText("action.manage.name", "Users"),
				DelegationModelActions.MANAGE_USERS);
		delegateModel.getActions().add(manageUsers);

		return delegateModel;
	}

	/*
	 * (non-Javadoc)
	 * @see DelegationSeedDataService#getActions()
	 */
	@Override
	public Map<String, String> getActions() {
		return getDAModuleActions(DelegationModelActions.MANAGE_DELEGATED_ADMIN, DelegationModelActions.MANAGE_USERS);
	}

	/**
	 * Returns a map of actions the delegation administrator module can perform
	 * 
	 * @param manageDelgatePolicies
	 * @param manageUsers
	 * @return A map of actions under the Delegation Administration Module.
	 * @throws ConsoleException
	 */
	private Map<String, String> getDAModuleActions(String manageDelgatePolicies, String manageUsers) {
		Map<String, String> actionsMap = new HashMap<>();

		actionsMap.put(manageDelgatePolicies, msgBundle.getText("action.manage.name", DELEGATION_POLICIES));

		actionsMap.put(manageUsers, msgBundle.getText("action.manage.name", USERS));

		return actionsMap;
	}
}
