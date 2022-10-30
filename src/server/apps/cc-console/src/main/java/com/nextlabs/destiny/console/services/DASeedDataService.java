/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 5, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.AuditLogDao;
import com.nextlabs.destiny.console.dao.delegadmin.DelegateModelDao;
import com.nextlabs.destiny.console.dao.policy.PolicyDevelopmentEntityDao;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentConditionDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.ObligationParameterDataType;
import com.nextlabs.destiny.console.enums.ObligationTagType;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.ObligationConfig;
import com.nextlabs.destiny.console.model.policy.ParameterConfig;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.services.delegadmin.DelegateModelService;
import com.nextlabs.destiny.console.services.delegadmin.DelegationComponentSearchService;
import com.nextlabs.destiny.console.services.delegation.seed.CompFolderSeedDataService;
import com.nextlabs.destiny.console.services.delegation.seed.ComponentSeedDataService;
import com.nextlabs.destiny.console.services.delegation.seed.DAUserSeedDataService;
import com.nextlabs.destiny.console.services.delegation.seed.PolicyFolderSeedDataService;
import com.nextlabs.destiny.console.services.delegation.seed.PolicySeedDataService;
import com.nextlabs.destiny.console.services.delegation.seed.TagSeedDataService;
import com.nextlabs.destiny.console.services.delegation.seed.PolicyWorkflowSeedDataService;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;

/**
 *
 * Creates seed data for Delegated Administration module
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Service
public class DASeedDataService {

    @Autowired
    protected MessageBundleService msgBundle;

    @Autowired
    private DelegateModelDao delegateModelDao;

    @Autowired
    private PolicyDevelopmentEntityDao policyDevEntityDao;

    @Autowired
    private DelegateModelService delegateModelService;

    @Autowired
    private PolicyModelService policyModelService;

    @Autowired
    private ComponentMgmtService componentMgmtService;
    
    @Autowired
    private DelegationComponentSearchService delegationComponentService;

    @Autowired
    private AuditLogDao auditLogDao;
    
    @Autowired
    private PolicyFolderSeedDataService policyFolderSeedDataService;
    
    @Autowired
    private CompFolderSeedDataService compFolderSeedDataService;
   
    @Autowired
    private PolicySeedDataService policySeedDataService;
    
    @Autowired
    private ComponentSeedDataService componentSeedDataService;
    
    @Autowired
    private TagSeedDataService tagSeedDataService;
    
    @Autowired
    private DAUserSeedDataService daUserSeedDataService;

    @Autowired
    private PolicyWorkflowSeedDataService policyWorkflowSeedDataService;

	public static final String POLICY = "Policy";
	public static final String COMPONENT = "Component";
	public static final String POLICY_MODEL = "Policy Model";
	public static final String POLICY_FOLDER = "Policy Folder";
	public static final String COMPONENT_FOLDER = "Component Folder";
	public static final String ADMINISTRATOR = "Administrator";
	public static final String SYS_CONFIGURATION = "System Configuration";
	public static final String LOG_CONFIGURATION = "Server Log Configuration";
	public static final String REPORTER = "Reporter";
	public static final String USERS_ROLES = "Users and Roles";
	public static final String SECURE_STORE = "Secure Store";
	public static final String XACML_POLICY_UPLOAD = "Xacml Policy Upload";
	public static final String ENVIRONMENT_CONFIGURATION = "Environment Configuration";
	public static final String POLICY_WORKFLOW = "Policy Workflow";
	public static final String PDP_PLUGIN = "PDP Plugin";
    public static final String POLICY_VALIDATOR = "Policy Validator";
    public static final String POLICY_CONTROLLER = "Policy Controller";
    public static final String ICENET = "ICENet";

    /**
     * Create Delegation Models
     * 
     * @throws ConsoleException
     *             thrown on any error
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createDAModels() throws ConsoleException {
        
		// Policy Folder
		DelegateModel policyFolder = getDAModelToAdd(msgBundle.getText("module.ps.policy.folder.name"),
				DelegationModelShortName.POLICY_FOLDER.name(), msgBundle.getText("module.ps.desc", POLICY_FOLDER));
		if (policyFolder != null) {
			delegateModelService.save(policyFolder);
		}

		// Component Folder
		DelegateModel componentFolder = getDAModelToAdd(msgBundle.getText("module.ps.comp.folder.name"),
				DelegationModelShortName.COMPONENT_FOLDER.name(),
				msgBundle.getText("module.ps.desc", COMPONENT_FOLDER));
		if (componentFolder != null) {
			delegateModelService.save(componentFolder);
		}

    	// Policy Studio - Policy
        DelegateModel policy = getDAModelToAdd(
                msgBundle.getText("module.ps.name", POLICY),
                DelegationModelShortName.PS_POLICY.name(),
                msgBundle.getText("module.ps.desc", POLICY));
        if (policy != null) {
            delegateModelService.save(policy);
        }

        // Policy Studio - Component
        DelegateModel component = getDAModelToAdd(
                msgBundle.getText("module.ps.name", COMPONENT),
                DelegationModelShortName.PS_COMPONENT.name(),
                msgBundle.getText("module.ps.desc", COMPONENT));
        if (component != null) {
            delegateModelService.save(component);
        }

        // Policy Studio - Policy Model
        DelegateModel policyModel = getDAModelToAdd(
                msgBundle.getText("module.ps.name", POLICY_MODEL),
                DelegationModelShortName.PS_POLICY_MODEL.name(),
                msgBundle.getText("module.ps.desc", POLICY_MODEL));
        if (policyModel != null) {
            delegateModelService.save(policyModel);
        }

        // Administrator
        DelegateModel administrator = getDAModelToAdd(
                msgBundle.getText("module.administrator.name"),
                DelegationModelShortName.ADMINISTRATOR.name(), msgBundle
                        .getText("module.administrator.desc", ADMINISTRATOR));
        if (administrator != null) {
            delegateModelService.save(administrator);
        }

        // Reporter
        DelegateModel reporter = getDAModelToAdd(
                msgBundle.getText("module.reporter.name"),
                DelegationModelShortName.REPORTER.name(),
                msgBundle.getText("module.reporter.desc"));
        if (reporter != null) {
            delegateModelService.save(reporter);
        }

        // Delegated Administration
        DelegateModel daModule = getDAModelToAdd(
                msgBundle.getText("module.da.name"),
                DelegationModelShortName.DELEGATED_ADMIN.name(),
                msgBundle.getText("module.da.desc"));
        if (daModule != null) {
            delegateModelService.save(daModule);
        }
        
        // Enrollment Management
        DelegateModel emModule = getDAModelToAdd(
                msgBundle.getText("module.em.name"),
                DelegationModelShortName.ENROLLMENT_MANAGEMENT.name(),
                msgBundle.getText("module.em.desc"));
        if (emModule != null) {
            delegateModelService.save(emModule);
        }

        // DA_USER
        DelegateModel daUser = daUserSeedDataService.getDAUser();
        if (daUser != null) {
            delegateModelService.save(daUser);
        }

        // Tag Management
        DelegateModel tagMgmt = getDAModelToAdd(
                msgBundle.getText("module.tag.mgmt.name"),
                DelegationModelShortName.TAG_MANAGEMENT.name(),
                msgBundle.getText("module.tag.mgmt.desc"));
        if (tagMgmt != null) {
            delegateModelService.save(tagMgmt);
        }
        
        //Authentication Management
        DelegateModel authMgmt = getDAModelToAdd(
                msgBundle.getText("module.auth.mgmt.name"),
                DelegationModelShortName.AUTH_MANAGEMENT.name(),
                msgBundle.getText("module.auth.mgmt.desc"));
        if (authMgmt != null) {
            delegateModelService.save(authMgmt);
        }
        
        //System Configuration
        DelegateModel sysConfigModule = getDAModelToAdd(
                msgBundle.getText("module.config.sys.name"),
                DelegationModelShortName.SYS_CONFIG.name(),
                msgBundle.getText("module.ps.desc", SYS_CONFIGURATION));
        if (sysConfigModule != null) {
            delegateModelService.save(sysConfigModule);
        }
        
        //Logging Configuration
        DelegateModel logConfigModule = getDAModelToAdd(
                msgBundle.getText("module.config.log.name"),
                DelegationModelShortName.LOG_CONFIG.name(),
                msgBundle.getText("module.ps.desc", LOG_CONFIGURATION));
        if (logConfigModule != null) {
            delegateModelService.save(logConfigModule);
        }

        // Secure store Management
        DelegateModel secureStoreModule = getDAModelToAdd(
                        msgBundle.getText("module.secure.store.name"),
                        DelegationModelShortName.SECURE_STORE.name(),
                        msgBundle.getText("module.ps.desc", SECURE_STORE));
        if (secureStoreModule != null) {
            delegateModelService.save(secureStoreModule);
        }

        // Xacml Policy Uploader
        DelegateModel xacmlPolicyUploadModule = getDAModelToAdd(
                        msgBundle.getText("module.xacml.policy.upload.name"),
                        DelegationModelShortName.XACML_POLICY_UPLOADER.name(),
                        msgBundle.getText("module.ps.desc", XACML_POLICY_UPLOAD));
        if (xacmlPolicyUploadModule != null) {
            delegateModelService.save(xacmlPolicyUploadModule);
        }

        // Environment Configuration
        DelegateModel remoteEnvironmentManagement = getDAModelToAdd(
                        msgBundle.getText("module.remote.environment.mgmt.name"),
                        DelegationModelShortName.ENVIRONMENT_CONFIGURATION.name(),
                        msgBundle.getText("module.ps.desc", ENVIRONMENT_CONFIGURATION));
        if (remoteEnvironmentManagement != null) {
            delegateModelService.save(remoteEnvironmentManagement);
        }

        // Policy Workflow
        DelegateModel policyWorkflow = policyWorkflowSeedDataService.getPolicyWorkflowDelegateModel();
        if (policyWorkflow != null) {
            delegateModelService.save(policyWorkflow);
        }

        // PDP Plugin
        DelegateModel pdpPluginModule = getDAModelToAdd(
                        msgBundle.getText("module.pdp.plugin.name"),
                        DelegationModelShortName.PDP_PLUGIN.name(),
                        msgBundle.getText("module.ps.desc", PDP_PLUGIN));
        if (pdpPluginModule != null) {
            delegateModelService.save(pdpPluginModule);
        }

        // Policy Validator
        DelegateModel policyValidatorModule = getDAModelToAdd(
                msgBundle.getText("module.policy.validator.name"),
                DelegationModelShortName.POLICY_VALIDATOR.name(),
                msgBundle.getText("module.policy.validator.desc", POLICY_VALIDATOR));
        if (policyValidatorModule != null) {
            delegateModelService.save(policyValidatorModule);
        }

        // Policy Controller
        DelegateModel policyControllerModule = getDAModelToAdd(
                msgBundle.getText("module.policyController.name"),
                DelegationModelShortName.POLICY_CONTROLLER.name(),
                msgBundle.getText("module.policyController.desc", POLICY_CONTROLLER));
        if (policyControllerModule != null) {
            delegateModelService.save(policyControllerModule);
        }

        // ICENet
        DelegateModel icenetModule = getDAModelToAdd(
                msgBundle.getText("module.icenet.name"),
                DelegationModelShortName.ICENET.name(),
                msgBundle.getText("module.icenet.desc", ICENET));
        if (icenetModule != null) {
            delegateModelService.save(icenetModule);
        }
    }

    /**
     * Creates Delegation Resource and Action components
     * 
     * @throws ConsoleException
     *             thrown on any error
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createDAComponents() throws ConsoleException, CircularReferenceException {

    	addDAComponent(msgBundle.getText("module.ps.policy.folder.name"),
                DelegationModelShortName.POLICY_FOLDER.name());
    	
    	addDAComponent(msgBundle.getText("module.ps.comp.folder.name"),
                DelegationModelShortName.COMPONENT_FOLDER.name());
    	
    	addDAComponent(msgBundle.getText("module.ps.name", POLICY),
                DelegationModelShortName.PS_POLICY.name());

        addDAComponent(msgBundle.getText("module.ps.name", COMPONENT),
                DelegationModelShortName.PS_COMPONENT.name());

        addDAComponent(msgBundle.getText("module.ps.name", POLICY_MODEL),
                DelegationModelShortName.PS_POLICY_MODEL.name());

        addDAComponent(msgBundle.getText("module.administrator.name"),
                DelegationModelShortName.ADMINISTRATOR.name());

        addDAComponent(msgBundle.getText("module.reporter.name"),
                DelegationModelShortName.REPORTER.name());

        addDAComponent(msgBundle.getText("module.da.name"),
                DelegationModelShortName.DELEGATED_ADMIN.name());

        addDAComponent(msgBundle.getText("module.tag.mgmt.name"),
                DelegationModelShortName.TAG_MANAGEMENT.name());
        
        addDAComponent(msgBundle.getText("module.auth.mgmt.name"),
                DelegationModelShortName.AUTH_MANAGEMENT.name());
        
        addDAComponent(msgBundle.getText("module.em.name"),
                DelegationModelShortName.ENROLLMENT_MANAGEMENT.name());
        
        addDAComponent(msgBundle.getText("module.config.sys.name"),
                DelegationModelShortName.SYS_CONFIG.name());
        
        addDAComponent(msgBundle.getText("module.config.log.name"),
                DelegationModelShortName.LOG_CONFIG.name());

        addDAComponent(msgBundle.getText("module.secure.store.name"),
                        DelegationModelShortName.SECURE_STORE.name());

        addDAComponent(msgBundle.getText("module.xacml.policy.upload.name"),
                        DelegationModelShortName.XACML_POLICY_UPLOADER.name());

        addDAComponent(msgBundle.getText("module.remote.environment.mgmt.name"),
                        DelegationModelShortName.ENVIRONMENT_CONFIGURATION.name());

        addDAComponent(msgBundle.getText("module.policy.workflow.name"),
                        DelegationModelShortName.POLICY_WORKFLOW.name());

        addDAComponent(msgBundle.getText("module.pdp.plugin.name"),
                        DelegationModelShortName.PDP_PLUGIN.name());

        addDAComponent(msgBundle.getText("module.policy.validator.name"),
                DelegationModelShortName.POLICY_VALIDATOR.name());

        addDAComponent(msgBundle.getText("module.policyController.name"),
                DelegationModelShortName.POLICY_CONTROLLER.name());

        addDAComponent(msgBundle.getText("module.icenet.name"),
                DelegationModelShortName.ICENET.name());

        delegationComponentService.reIndexAllComponents();
    }

    /**
     * Checks if a Delegation Model already exists, given the short name
     * 
     * If not exists, initializes a new model
     * 
     * @param name
     * @param shortName
     * @param desc
     * @return DelegateModel entity
     * @throws ConsoleException
     *             throws on any error
     */
    private DelegateModel getDAModelToAdd(String name, String shortName,
            String desc) throws ConsoleException {

        DelegateModel delegateModel = delegateModelDao.findByShortName(shortName);
        boolean delegateModelUpdated = false;
        if (delegateModel == null) {
            delegateModel = new DelegateModel(null, name, shortName, desc,
                    PolicyModelType.DA_RESOURCE, Status.ACTIVE);

            switch (shortName) {
            	case "POLICY_FOLDER":
            		policyFolderSeedDataService.addActionConfig(delegateModel);
            		addModuleObligations(delegateModel, shortName);
            		break;
            	case "COMPONENT_FOLDER":
					compFolderSeedDataService.addActionConfig(delegateModel);
					addModuleObligations(delegateModel, shortName);
					break;
                case "PS_POLICY":
                	policySeedDataService.addActionConfig(delegateModel);
                    addModuleObligations(delegateModel, shortName);
                    break;
                case "PS_COMPONENT":
                	componentSeedDataService.addActionConfig(delegateModel);
                    addModuleObligations(delegateModel, shortName);
                    break;
                case "PS_POLICY_MODEL":
                	addModuleActionConfig(delegateModel,
                            DelegationModelActions.VIEW_POLICY_MODEL,
                            DelegationModelActions.EDIT_POLICY_MODEL,
                            DelegationModelActions.DELETE_POLICY_MODEL,
                            DelegationModelActions.CREATE_POLICY_MODEL);
                    addModuleObligations(delegateModel, shortName);
                    break;
                case "REPORTER":
                	addModuleActionConfig(delegateModel,
                            DelegationModelActions.VIEW_REPORTER,null,null,
                            DelegationModelActions.MANAGE_REPORTER);
                    break;
                case "ADMINISTRATOR":
                	addModuleActionConfig(delegateModel,
                            DelegationModelActions.VIEW_ADMINISTRATOR,
                            null, null, null);
                    break;
                case "DELEGATED_ADMIN":
                	daUserSeedDataService.addActionConfig(delegateModel);
                    break;
                case "TAG_MANAGEMENT":
                	tagSeedDataService.addActionConfig(delegateModel);
                    break;
                case "AUTH_MANAGEMENT":
                	addModuleActionConfig(delegateModel,
                            DelegationModelActions.VIEW_AUTH_MANAGEMENT,
                            DelegationModelActions.EDIT_AUTH_MANAGEMENT,
                            DelegationModelActions.DELETE_AUTH_MANAGEMENT,
                            DelegationModelActions.CREATE_AUTH_MANAGEMENT);
                    break;
                case "POLICY_VALIDATOR":
                	addModuleActionConfig(delegateModel,
                            DelegationModelActions.POLICY_VALIDATOR, null, null, null);
                    break;
                case "ENROLLMENT_MANAGEMENT":
                	addModuleActionConfig(delegateModel,
                            DelegationModelActions.ENROLLMENT_MANAGEMENT, null, null, null);
                    break;
                case "SYS_CONFIG":
                	addModuleActionConfig(delegateModel,
                            DelegationModelActions.MANAGE_SYSTEM_CONFIGURATION, null, null, null);
                    break;
                case "LOG_CONFIG":
                	addModuleActionConfig(delegateModel,
                            DelegationModelActions.MANAGE_LOGGING_CONFIGURATION, null, null, null);
                    break;
                case "SECURE_STORE":
                    addModuleActionConfig(delegateModel,
                            DelegationModelActions.MANAGE_SECURE_STORE, null, null, null);
                    break;
                case "XACML_POLICY_UPLOADER":
                    addModuleActionConfig(delegateModel,
                            DelegationModelActions.XACML_POLICY, null, null, null);
                    break;
                case "ENVIRONMENT_CONFIGURATION":
                    addModuleActionConfig(delegateModel,
                            DelegationModelActions.VIEW_ENVIRONMENT_CONFIGURATION, DelegationModelActions.EDIT_ENVIRONMENT_CONFIGURATION,
                            DelegationModelActions.DELETE_ENVIRONMENT_CONFIGURATION, DelegationModelActions.CREATE_ENVIRONMENT_CONFIGURATION);
                    break;
                case "PDP_PLUGIN":
                    addModuleActionConfig(delegateModel,
                                    DelegationModelActions.MANAGE_PDP_PLUGIN, null, null, null);
                    break;
                case "POLICY_CONTROLLER":
                    addModuleActionConfig(delegateModel,
                            DelegationModelActions.MANAGE_POLICY_CONTROLLER, null, null, null);
                    addModuleActionConfig(delegateModel,
                            DelegationModelActions.MANAGE_POLICY_CONTROLLER_PROFILE, null, null, null);
                    break;
                case "ICENET":
                    addModuleActionConfig(delegateModel,
                            DelegationModelActions.MANAGE_ICENET, null, null, null);
                    break;
                default:
                    break;
            }
            delegateModelUpdated = true;
        } else {
            switch (shortName) {
                case "PS_POLICY": {
                    if (delegateModel.getActions().stream().noneMatch(actionConfig ->
                            DelegationModelActions.MOVE_POLICY.equals(actionConfig.getShortName()))) {
                        ActionConfig move = getActionConfig(msgBundle.getText("action.move.name", DASeedDataService.POLICY),
                                DelegationModelActions.MOVE_POLICY);
                        delegateModel.getActions().add(move);
                        delegateModelUpdated = true;
                    }
                    if (delegateModel.getActions().stream().noneMatch(actionConfig ->
                            DelegationModelActions.MIGRATE_POLICY.equals(actionConfig.getShortName()))) {
                        ActionConfig migrate = getActionConfig(msgBundle.getText("action.migrate.name", DASeedDataService.POLICY),
                                DelegationModelActions.MIGRATE_POLICY);
                        delegateModel.getActions().add(migrate);
                        delegateModelUpdated = true;
                    }
                    for (ObligationConfig obligationConfig : delegateModel.getObligations()) {
                        if (DelegationModelShortName.POLICY_ACCESS_TAGS.name().equals(obligationConfig.getShortName())) {
                            if (obligationConfig.getParameters().stream()
                                    .noneMatch(parameterConfig -> ObligationTagType.MOVE_TAG_FILTERS.name().equals(parameterConfig.getShortName()))) {
                                ParameterConfig moveParam = getParameterConfig(msgBundle.getText("obligation.param.move.name"),
                                        ObligationTagType.MOVE_TAG_FILTERS.name());
                                obligationConfig.getParameters().add(moveParam);
                                delegateModelUpdated = true;
                            }
                            if (obligationConfig.getParameters().stream()
                                    .noneMatch(parameterConfig -> ObligationTagType.INSERT_TAG_FILTERS.name().equals(parameterConfig.getShortName()))) {
                                ParameterConfig insertParam = getParameterConfig(msgBundle.getText("obligation.param.insert.name"),
                                        ObligationTagType.INSERT_TAG_FILTERS.name());
                                obligationConfig.getParameters().add(insertParam);
                                delegateModelUpdated = true;
                            }
                        }
                    }
                    break;
                }
                case "PS_COMPONENT": {
                    if (delegateModel.getActions().stream().noneMatch(actionConfig ->
                            DelegationModelActions.MOVE_COMPONENT.equals(actionConfig.getShortName()))) {
                        ActionConfig move = getActionConfig(msgBundle.getText("action.move.name", DASeedDataService.COMPONENT),
                                DelegationModelActions.MOVE_COMPONENT);
                        delegateModel.getActions().add(move);
                        delegateModelUpdated = true;
                    }
                    for (ObligationConfig obligationConfig : delegateModel.getObligations()) {
                        if (DelegationModelShortName.COMPONENT_ACCESS_TAGS.name().equals(obligationConfig.getShortName())) {
                            if (obligationConfig.getParameters().stream()
                                    .noneMatch(parameterConfig -> ObligationTagType.MOVE_TAG_FILTERS.name().equals(parameterConfig.getShortName()))) {
                                ParameterConfig moveParam = getParameterConfig(msgBundle.getText("obligation.param.move.name"),
                                        ObligationTagType.MOVE_TAG_FILTERS.name());
                                obligationConfig.getParameters().add(moveParam);
                                delegateModelUpdated = true;
                            }
                            if (obligationConfig.getParameters().stream()
                                    .noneMatch(parameterConfig -> ObligationTagType.INSERT_TAG_FILTERS.name().equals(parameterConfig.getShortName()))) {
                                ParameterConfig insertParam = getParameterConfig(msgBundle.getText("obligation.param.insert.name"),
                                        ObligationTagType.INSERT_TAG_FILTERS.name());
                                obligationConfig.getParameters().add(insertParam);
                                delegateModelUpdated = true;
                            }
                        }
                    }
                    break;
                }
                case "XACML_POLICY_UPLOADER": {
                    if (delegateModel.getActions().stream().noneMatch(actionConfig ->
                            DelegationModelActions.XACML_POLICY.equals(actionConfig.getShortName()))) {
                        ActionConfig manage = getActionConfig(msgBundle.getText("action.view.name", ""),
                                DelegationModelActions.XACML_POLICY);
                        delegateModel.getActions().add(manage);
                        delegateModelUpdated = true;
                    }
                    break;
                }
                default:
                    break;
            }
        }
        return delegateModelUpdated ? delegateModel : null;
    }

    /**
     * Initializes ActionConfig for Delegation Models
     * 
     * @param delegateModel
     *            entity
     * @return DelegateModel entity
     * @throws ConsoleException
     *             thrown at any error
     */
    private DelegateModel addModuleActionConfig(DelegateModel delegateModel, String viewName,
                    String editName, String deleteName, String createName) {

        if (viewName != null) {
            ActionConfig view =
                            getActionConfig(msgBundle.getText("action.view.name", ""), viewName);
            delegateModel.getActions().add(view);
        }

        if (editName != null) {
            ActionConfig edit =
                            getActionConfig(msgBundle.getText("action.edit.name", ""), editName);
            delegateModel.getActions().add(edit);
        }

        if (deleteName != null) {
            ActionConfig delete = getActionConfig(msgBundle.getText("action.delete.name", ""),
                            deleteName);
            delegateModel.getActions().add(delete);
        }

        if (createName != null) {
            if (delegateModel.getShortName().equals(DelegationModelShortName.REPORTER.name())) {
                ActionConfig create = getActionConfig(msgBundle.getText("action.manage.name", ""),
                                createName);
                delegateModel.getActions().add(create);
            } else {
                ActionConfig create = getActionConfig(msgBundle.getText("action.create.name", ""),
                                createName);
                delegateModel.getActions().add(create);
            }
        }

        return delegateModel;
    }

    /**
     * Initializes ObligationConfig for Delegation Models
     * 
     * @param delegateModel
     *            entity
     * @return DelegateModel entity
     * @throws ConsoleException
     *             thrown at any error
     */
    private DelegateModel addModuleObligations(DelegateModel delegateModel, String module) {

        ObligationConfig oblConfig = null;

        switch (module) {
	        case "POLICY_FOLDER":
	        	oblConfig = getObligationConfig(
                        msgBundle.getText("obligation.name", POLICY_FOLDER),
                        msgBundle.getText("obligation.policy.folder.shortname"));
                break;
	    	case "COMPONENT_FOLDER":
	    		oblConfig = getObligationConfig(
                        msgBundle.getText("obligation.name", COMPONENT_FOLDER),
                        msgBundle.getText("obligation.component.folder.shortname"));
                break;
            case "PS_POLICY":
                oblConfig = getObligationConfig(
                        msgBundle.getText("obligation.name", POLICY),
                        msgBundle.getText("obligation.policy.shortname"));
                break;
            case "PS_COMPONENT":
                oblConfig = getObligationConfig(
                        msgBundle.getText("obligation.name", COMPONENT),
                        msgBundle.getText("obligation.comp.shortname"));
                break;
            case "PS_POLICY_MODEL":
                oblConfig = getObligationConfig(
                        msgBundle.getText("obligation.name", POLICY_MODEL),
                        msgBundle.getText("obligation.policymodel.shortname"));
                break;
            case "REPORTER":
                oblConfig = getObligationConfig(
                        msgBundle.getText("obligation.name", REPORTER),
                        msgBundle.getText("obligation.reporter.shortname"));
                break;
            case "ADMINISTRATOR":
                oblConfig = getObligationConfig(
                        msgBundle.getText("obligation.name", USERS_ROLES),
                        msgBundle.getText("obligation.usersroles.shortname"));
                break;
            case "POLICY_ENFORCER_CONFIG":
                oblConfig = getObligationConfig(
                        msgBundle.getText("obligation.name",
                                "Policy Enforcer Configuration"),
                        msgBundle.getText("obligation.enfconfig.shortname"));
                break;
            case "DELEGATED_ADMIN":
                oblConfig = getObligationConfig(
                        msgBundle.getText("obligation.name",
                                "Delegated Administration"),
                        msgBundle.getText("obligation.da.shortname"));
                break;
            default:
                break;
        }

        delegateModel.getObligations().add(oblConfig);

        return delegateModel;
    }

    /**
     * Saves the delegation actions
     * 
     * @param actions
     * @param damodel
     * @return true, if the action is saved successfully
     * @throws ConsoleException
     * @throws CircularReferenceException
     */
    private boolean saveActionDto(Map<String, String> actions, DelegateModel damodel) throws ConsoleException, CircularReferenceException {
    	boolean savedRecord = false;
    	for (Map.Entry<String, String> action : actions
                .entrySet()) {
            
            ComponentDTO actionDTO = getDAActionComponent(
                    action.getValue(), action.getValue(),
                    damodel.getId(), action.getKey());
            
            if (actionDTO != null) {
                componentMgmtService.save(actionDTO);
                savedRecord =  true;
            }
        }
    	return savedRecord;
    }

    /**
     * Creates a new delegation component, if not exists
     * 
     * @param name
     * @param shortName
     * @throws ConsoleException
     *             thrown at any error
     * @throws CircularReferenceException 
     */
    private void addDAComponent(String name, String shortName)
            throws ConsoleException, CircularReferenceException {

        DelegateModel damodel = delegateModelDao.findByShortName(shortName);
        if (damodel != null) {
            boolean savedRecord = false;

            // resources
            ComponentDTO dacomponent = getDAResourceComponent(name, name,
                    damodel.getId());
            if (dacomponent != null) {
                componentMgmtService.save(dacomponent);
                savedRecord = true;
            }

            // actions
            switch (shortName) {
            	case "POLICY_FOLDER":
	                Map<String, String> policyFolderActions = policyFolderSeedDataService.getActions();
	                savedRecord = saveActionDto(policyFolderActions, damodel);
	                break;
	                
            	case "COMPONENT_FOLDER":
	                Map<String, String> componentFolderActions = compFolderSeedDataService.getActions();
	                savedRecord = saveActionDto(componentFolderActions, damodel);
	                break;
	                
                case "PS_POLICY":
                    Map<String, String> policyActions = policySeedDataService.getActions();
                    savedRecord = saveActionDto(policyActions, damodel);
                    break;

                case "PS_COMPONENT":
                    Map<String, String> compActions = componentSeedDataService.getActions();
                    savedRecord = saveActionDto(compActions, damodel);
                    break;

                case "PS_POLICY_MODEL":
                    Map<String, String> modelActions = getDelegationComponentActions(
                            name, DelegationModelActions.VIEW_POLICY_MODEL,
                            DelegationModelActions.EDIT_POLICY_MODEL,
                            DelegationModelActions.DELETE_POLICY_MODEL,
                            DelegationModelActions.CREATE_POLICY_MODEL);
                    savedRecord = saveActionDto(modelActions, damodel);
                    break;

                case "ADMINISTRATOR":
                    Map<String, String> adminActions = getDelegationComponentActions(
                            name, DelegationModelActions.VIEW_ADMINISTRATOR,
                            null, null, null);
                    savedRecord = saveActionDto(adminActions, damodel);
                    break;

                case "REPORTER":
                    Map<String, String> reporterActions = getDelegationComponentActions(
                            name, DelegationModelActions.VIEW_REPORTER, null,
                            null, DelegationModelActions.MANAGE_REPORTER);
                    savedRecord = saveActionDto(reporterActions, damodel);
                    break;

                case "DELEGATED_ADMIN":
                    Map<String, String> daActions = daUserSeedDataService.getActions();
                    savedRecord = saveActionDto(daActions, damodel);
                    break;

                case "TAG_MANAGEMENT":
                    Map<String, String> tagMgmtActions = tagSeedDataService.getActions();
                    savedRecord = saveActionDto(tagMgmtActions, damodel);
                    break;
                    
                case "AUTH_MANAGEMENT":
                	Map<String, String> authMgmtActions = getDelegationComponentActions(
                            name, DelegationModelActions.VIEW_AUTH_MANAGEMENT,
                            DelegationModelActions.EDIT_AUTH_MANAGEMENT,
                            DelegationModelActions.DELETE_AUTH_MANAGEMENT,
                            DelegationModelActions.CREATE_AUTH_MANAGEMENT);
                	savedRecord = saveActionDto(authMgmtActions, damodel);
                    break;
               
                case "ENROLLMENT_MANAGEMENT":
                	Map<String, String> enrollmentActions = new HashMap<>();
                	enrollmentActions.put(DelegationModelActions.ENROLLMENT_MANAGEMENT,
                            msgBundle.getText("action.enroll.name"));
                    savedRecord = saveActionDto(enrollmentActions, damodel);
                    break;
               
                case "SYS_CONFIG":
                	Map<String, String> sysConfigActions = new HashMap<>();
                	sysConfigActions.put(DelegationModelActions.MANAGE_SYSTEM_CONFIGURATION,
                            msgBundle.getText("action.config.sys.name"));
                    savedRecord = saveActionDto(sysConfigActions, damodel);
                    break;
                
                case "LOG_CONFIG":
                	Map<String, String> logConfigActions = new HashMap<>();
                	logConfigActions.put(DelegationModelActions.MANAGE_LOGGING_CONFIGURATION,
                            msgBundle.getText("action.config.log.name"));
                    savedRecord = saveActionDto(logConfigActions, damodel);
                    break;
                case "SECURE_STORE":
                    Map<String, String> secureStoreActions = new HashMap<>();
                    secureStoreActions.put(DelegationModelActions.MANAGE_SECURE_STORE,
                                    msgBundle.getText("action.mgmt.secure.store"));
                    savedRecord = saveActionDto(secureStoreActions, damodel);
                    break;
                case "XACML_POLICY_UPLOADER":
                    Map<String, String> xacmlPolicyActions = new HashMap<>();
                    xacmlPolicyActions.put(DelegationModelActions.XACML_POLICY,
                            msgBundle.getText("action.xacml.policy"));
                    savedRecord = saveActionDto(xacmlPolicyActions, damodel);
                    break;
                case "ENVIRONMENT_CONFIGURATION":
                    Map<String, String> remoteEnvironmentActions = new HashMap<>();
                    remoteEnvironmentActions.put(DelegationModelActions.CREATE_ENVIRONMENT_CONFIGURATION,
                            msgBundle.getText("action.create.remote.environment"));
                    remoteEnvironmentActions.put(DelegationModelActions.EDIT_ENVIRONMENT_CONFIGURATION,
                            msgBundle.getText("action.edit.remote.environment"));
                    remoteEnvironmentActions.put(DelegationModelActions.DELETE_ENVIRONMENT_CONFIGURATION,
                            msgBundle.getText("action.delete.remote.environment"));
                    remoteEnvironmentActions.put(DelegationModelActions.VIEW_ENVIRONMENT_CONFIGURATION,
                            msgBundle.getText("action.view.remote.environment"));
                    savedRecord = saveActionDto(remoteEnvironmentActions, damodel);
                    break;
                case "POLICY_WORKFLOW":
                    Map<String, String> policyWorkflowActions = policyWorkflowSeedDataService.getActions();
                    savedRecord = saveActionDto(policyWorkflowActions, damodel);
                    break;
                case "PDP_PLUGIN":
                    Map<String, String> pdpPluginActions = new HashMap<>();
                    pdpPluginActions.put(DelegationModelActions.MANAGE_PDP_PLUGIN,
                                    msgBundle.getText("action.mgmt.pdp.plugin"));
                    savedRecord = saveActionDto(pdpPluginActions, damodel);
                    break;
                case "POLICY_VALIDATOR":
                    Map<String, String> policyValidatorActions = new HashMap<>();
                    policyValidatorActions.put(DelegationModelActions.POLICY_VALIDATOR,
                            msgBundle.getText("action.policy.validator.name"));
                    savedRecord = saveActionDto(policyValidatorActions, damodel);
                    break;
                case "POLICY_CONTROLLER":
                    Map<String, String> policyControllerActions = new HashMap<>();
                    policyControllerActions.put(DelegationModelActions.MANAGE_POLICY_CONTROLLER,
                            msgBundle.getText("action.policyController"));
                    policyControllerActions.put(DelegationModelActions.MANAGE_POLICY_CONTROLLER_PROFILE,
                            msgBundle.getText("action.policyControllerProfile"));
                    savedRecord = saveActionDto(policyControllerActions, damodel);
                    break;
                case "ICENET":
                    Map<String, String> icenetActions = new HashMap<>();
                    icenetActions.put(DelegationModelActions.MANAGE_ICENET,
                            msgBundle.getText("action.icenet"));
                    savedRecord = saveActionDto(icenetActions, damodel);
                    break;
                default:
                    break;
            }

            if (savedRecord) {
                auditLogDao.clearAll();
            }
        }
    }

    /**
     * Checks if a Delegation Resource Component already exists If not,
     * initializes the new component
     * 
     * @param name
     * @param desc
     * @param daModelId
     * @return componentDTO
     * @throws ConsoleException
     * 
     */
    private ComponentDTO getDAResourceComponent(String name, String desc,
            Long daModelId) throws ConsoleException {

        String title = "RESOURCE/" + name;
        PolicyDevelopmentEntity devEntity = policyDevEntityDao
                .findActiveByName(title);

        if (devEntity != null) {
            return null;
        }

        ComponentDTO daComponent = new ComponentDTO();

        PolicyModel policyModel = policyModelService.findById(daModelId);
        PolicyModelDTO policyModelDTO = PolicyModelDTO.getDTO(policyModel);

        List<ComponentConditionDTO> conditions = new ArrayList<>();

        ComponentConditionDTO condition = new ComponentConditionDTO(
                msgBundle.getText("attr.name.key"),
                msgBundle.getText("oper.equals.key"), name);
        conditions.add(condition);

        daComponent.setName(name);
        daComponent.setDescription(desc);
        daComponent.setType("RESOURCE");
        daComponent.setPolicyModel(policyModelDTO);
        daComponent.setConditions(conditions);
        daComponent.setCategory(DevEntityType.DELEGATION_COMPONENT);
        daComponent.setStatus(PolicyDevelopmentStatus.DRAFT.name());

        return daComponent;
    }

    /**
     * Checks if a Delegation Action Component already exists If not,
     * initializes the new component
     * 
     * @param name
     * @param desc
     * @param daModelId
     * @return componentDTO
     * @throws ConsoleException
     * 
     */
    private ComponentDTO getDAActionComponent(String name, String desc,
            Long daModelId, String action) throws ConsoleException {

        String title = "ACTION/" + name;
        PolicyDevelopmentEntity devEntity = policyDevEntityDao
                .findActiveByName(title);

        if (devEntity != null) {
            return null;
        }

        ComponentDTO daComponent = new ComponentDTO();

        PolicyModel policyModel = policyModelService.findById(daModelId);
        PolicyModelDTO policyModelDTO = PolicyModelDTO.getDTO(policyModel);

        List<String> actions = new ArrayList<>();
        actions.add(action);

        daComponent.setName(name);
        daComponent.setDescription(desc);
        daComponent.setType("ACTION");
        daComponent.setPolicyModel(policyModelDTO);
        daComponent.setActions(actions);
        daComponent.setCategory(DevEntityType.DELEGATION_COMPONENT);
        daComponent.setStatus(PolicyDevelopmentStatus.DRAFT.name());

        return daComponent;
    }


    /**
     * Returns the map containing allowed actions for a delegation model, given
     * the name.
     * 
     * @param moduleName
     * @param viewAction
     * @param editAction
     * @param deleteAction
     * @param createAction
     * @return  a Map of actions belonging to a particular module
     */
    private Map<String, String> getDelegationComponentActions(String moduleName,
            String viewAction, String editAction, String deleteAction,
            String createAction) {
        Map<String, String> actionsMap = new HashMap<>();

        if (moduleName.contains("Policy Studio")) {
            moduleName = "Policy Model";
        }

		if (viewAction != null) {
			actionsMap.put(viewAction, 
					msgBundle.getText("action.view.name", moduleName));
		}
		if (editAction != null) {
			 actionsMap.put(editAction,
		                msgBundle.getText("action.edit.name", moduleName));
		}
		if (deleteAction != null) {
			actionsMap.put(deleteAction,
	                msgBundle.getText("action.delete.name", moduleName));
		}
		if (createAction != null) {
			if (moduleName.contains("Reporter")) {
				actionsMap.put(createAction, msgBundle.getText(
						"action.manage.name", moduleName));
			} else {
				actionsMap.put(createAction, msgBundle.getText(
						"action.create.name", moduleName));
			}

		}

        return actionsMap;
    }


    /**
     * Returns a Policy action configuration entity for given name and shortname.
     * 
     * @param name
     * @param shortName
     * @return {@link ActionConfig} object
     */
    private ActionConfig getActionConfig(String name, String shortName) {

        ActionConfig action = new ActionConfig();
        action.setName(name);
        action.setShortName(shortName);

        return action;
    }

    /**
     * Returns a Policy obligation configuration entity for given name and shortname.
     * 
     * @param name
     * @param shortName
     * @return {@link ObligationConfig} object
     */
    private ObligationConfig getObligationConfig(String name,
            String shortName) {

        ObligationConfig oblConfig = new ObligationConfig();
        Set<ParameterConfig> parameters = new TreeSet<>();

        ParameterConfig insertParam = getParameterConfig(
                msgBundle.getText("obligation.param.insert.name"),
                ObligationTagType.INSERT_TAG_FILTERS.name());
        parameters.add(insertParam);
        
        ParameterConfig viewParam = getParameterConfig(
                msgBundle.getText("obligation.param.view.name"),
                ObligationTagType.VIEW_TAG_FILTERS.name());
        parameters.add(viewParam);

        ParameterConfig editParam = getParameterConfig(
                msgBundle.getText("obligation.param.edit.name"),
                ObligationTagType.EDIT_TAG_FILTERS.name());
        parameters.add(editParam);

        ParameterConfig deleteParam = getParameterConfig(
                msgBundle.getText("obligation.param.delete.name"),
                ObligationTagType.DELETE_TAG_FILTERS.name());
        parameters.add(deleteParam);


		ParameterConfig deployParam = getParameterConfig(msgBundle.getText("obligation.param.deploy.name"),
				ObligationTagType.DEPLOY_TAG_FILTERS.name());
		parameters.add(deployParam);
		
		if(!DelegationModelShortName.PS_POLICY_MODEL.name().equals(shortName)) {
			ParameterConfig moveParam = getParameterConfig(msgBundle.getText("obligation.param.move.name"),
					ObligationTagType.MOVE_TAG_FILTERS.name());
			parameters.add(moveParam);
		}
        

        oblConfig.setName(name);
        oblConfig.setShortName(shortName);
        oblConfig.setParameters(parameters);

        return oblConfig;
    }

    /**
     * Returns an Obligation parameter configuration entity for the given name and shortname.
     * 
     * @param name
     * @param shortName
     * @return {@link ParameterConfig} object
     */
    private ParameterConfig getParameterConfig(String name, String shortName) {

        ParameterConfig paramConfig = new ParameterConfig();
        paramConfig.setName(name);
        paramConfig.setShortName(shortName);
        paramConfig.setType(ObligationParameterDataType.TEXT_SINGLE_ROW);
        paramConfig.setEditable(true);

        return paramConfig;
    }

}
