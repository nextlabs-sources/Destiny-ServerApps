/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 15, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy.impl;

import static com.nextlabs.destiny.console.enums.Status.ACTIVE;
import static com.nextlabs.destiny.console.enums.Status.DELETED;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildTagFilterQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.withSorts;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.withStatuses;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.dao.TagLabelDao;
import com.nextlabs.destiny.console.dao.policy.ActionConfigDao;
import com.nextlabs.destiny.console.dao.policy.AttributeConfigDao;
import com.nextlabs.destiny.console.dao.policy.PolicyModelDao;
import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.FacetTerm;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.ObligationDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyComponent;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyModelDTO;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.PolicyStatus;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.DirtyUpdateException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.ObligationConfig;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.model.policy.ParameterConfig;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.policy.visitors.Attribute;
import com.nextlabs.destiny.console.policy.visitors.PredicateData;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.ComponentSearchRepository;
import com.nextlabs.destiny.console.search.repositories.PolicyModelSearchRepository;
import com.nextlabs.destiny.console.search.repositories.PolicySearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.TagLabelService;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.OperatorConfigService;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;
import com.nextlabs.destiny.console.utils.ActionShortCodeGenerator;
import com.nextlabs.destiny.console.utils.JavaBeanCopier;

/**
 * Policy model service implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class PolicyModelServiceImpl implements PolicyModelService {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyModelServiceImpl.class);
    
    public static final int MAX_ALLOWED_LENGTH = 247;
    
    public static final String COMPONENT_TYPE_ACTION = "ACTION";

    @Autowired
    private PolicyModelDao policyModelDao;

    @Autowired
    private TagLabelService tagLabelService;
    
    @Autowired
    private TagLabelDao tagLabelDao;

    @Autowired
    private OperatorConfigService operatorConfigService;

    @Autowired
    protected MessageBundleService msgBundle;

    @Autowired
    private AttributeConfigDao attributeConfigDao;

    @Autowired
    private ActionConfigDao actionConfigDao;
    
    @Autowired
    private PolicyMgmtService policyMgmtService;
    
    @Autowired
    private ComponentMgmtService componentMgmtService;

    @Autowired
    private EntityAuditLogDao entityAuditLogDao;

	@Autowired
	private AccessControlService accessControlService;
    
    @Resource
    private PolicyModelSearchRepository policyModelSearchRepository;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;
    
    @Resource
    private ComponentSearchRepository componentSearchRepository;
    
    @Resource
    private PolicySearchRepository policySearchRepository;
    
	private List<String> preDefinedActions;
	
	@PostConstruct
    public void getPreDefinedActionsList() {

		String[] preDefinedActionsArray = { "Cs", "Co", "Ca", "CP", "De", "Em", "Mo", "Pr", "Op", "Ed", "Rn", "SE",
				"SI", "Ex", "At", "Ru", "Av", "Me", "Ps", "Sh", "Re", "Qu", "Jo", "Vi", "Vo" };

		if (preDefinedActions == null) {
            preDefinedActions = new ArrayList<>();
		}

		for (String action : preDefinedActionsArray) {
			preDefinedActions.add(action.toLowerCase());
		}
	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyModel save(final PolicyModel policyModel, List<Long> tagIds, boolean createAction)
            throws ConsoleException, CircularReferenceException {

        policyModel.getTags().clear();
        for (Long tagId : tagIds) {
            TagLabel tag = tagLabelService.findById(tagId);
            if (tag != null){
            	 policyModel.getTags().add(tag);
            }          
        }
    
        return save(policyModel, createAction);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyModel save(PolicyModel policyModel, boolean createAction)
			throws ConsoleException, CircularReferenceException {
        validateNameAndShortName(policyModel);
        
        Map<String,String> originalActions = new HashMap<>();
        boolean updateModelType = false;

        if (policyModel.getId() == null) {
            synchronized (this) {
				accessControlService.authorizeByTags(ActionType.INSERT,
						DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS,
						policyModel,
						true);
                if (!policyModel.getActions().isEmpty()) {
                    updateModelActionList(policyModel);
                }
                policyModelDao.create(policyModel);
				policyModelSearchRepository.save(policyModel);
				entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE, AuditableEntity.COMPONENT_TYPE.getCode(),
                		policyModel.getId(), null, PolicyModelDTO.getDTO(policyModel).toAuditString());
            }
        } else {
        	PolicyModel savedModel = findById(policyModel.getId());

			accessControlService.authorizeByTags(ActionType.EDIT,
					DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS,
					savedModel,
					false);
			accessControlService.authorizeByTags(ActionType.EDIT,
					DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS,
					policyModel,
					true);

        	String snapshot = PolicyModelDTO.getDTO(savedModel).toAuditString();
        	//concurrent update check
        	if (policyModel.getVersion() != -1 && 
        			policyModel.getVersion() < savedModel.getVersion()){
        		//stale data, update not allowed
        		throw new DirtyUpdateException(
    					msgBundle.getText("server.error.dirty.update.code"),
    					msgBundle.getText("server.error.dirty.update"));
        	}
        	
        	updateModelType = !savedModel.getName().equals(policyModel.getName());
        	
        	for (ActionConfig actionConfig : savedModel.getActions()){
        		originalActions.put(actionConfig.getShortName(), actionConfig.getName());
        	}
            synchronized (this) {
				policyModel = update(policyModel, savedModel);
                entityAuditLogDao.addEntityAuditLog(AuditAction.UPDATE, AuditableEntity.COMPONENT_TYPE.getCode(),
                		policyModel.getId(), snapshot, PolicyModelDTO.getDTO(policyModel).toAuditString());
            }
        }

        log.debug("Policy model {} saved successfully, [ Id: {}]", policyModel.getName(),
                policyModel.getId());
		if (createAction) {
			createModifyDeleteActions(policyModel, originalActions);
		}

        if(updateModelType) {
        	updateComponentModelType(policyModel);
        }
        
        return policyModel;
    }

	private PolicyModel update(PolicyModel source, PolicyModel destination)
			throws ConsoleException {
		destination.setName(source.getName());
		destination.setDescription(source.getDescription());
		destination.setTags(source.getTags());

		updateAttributes(source, destination);
		updateActions(source, destination);
		updateObligations(source, destination);

		policyModelDao.update(destination);
		policyModelSearchRepository.save(destination);
		log.info("Policy model updated successfully, Updated Model : {}", destination.getId());
		return destination;
	}

	private void updateAttributes(PolicyModel source, PolicyModel destination)
			throws ConsoleException {
		Set<AttributeConfig> attributeConfigSet = new TreeSet<>();
		for (AttributeConfig attribute : source.getAttributes()) {
			Optional<AttributeConfig> config = destination.getAttributes().stream().filter(attr ->
					attr.getShortName().equals(attribute.getShortName())
			).findFirst();

			AttributeConfig attributeConfig;
			if(config.isPresent()) {
				attributeConfig = config.get();
				attributeConfig.getOperatorConfigs().clear();
			} else {
				attributeConfig = new AttributeConfig();
				attributeConfig.setShortName(attribute.getShortName());
			}
			attributeConfig.setName(attribute.getName());
			for (OperatorConfig operator : attribute.getOperatorConfigs()) {
				attributeConfig.getOperatorConfigs().add(operatorConfigService.
						findByKeyAndDataType(operator.getKey(),
								operator.getDataType()));
			}
			attributeConfig.setDataType(attribute.getDataType());
			attributeConfig.setRegExPattern(attribute.getRegExPattern());
			attributeConfig.setSortOrder(attribute.getSortOrder());
			attributeConfigSet.add(attributeConfig);
		}
		destination.getAttributes().clear();
		destination.getAttributes().addAll(attributeConfigSet);
	}

	private void updateActions(PolicyModel source, PolicyModel destination)
			throws ConsoleException {
		Set<ActionConfig> actionConfigSet = new TreeSet<>();
		for (ActionConfig action : source.getActions()) {
			Optional<ActionConfig> config = destination.getActions().stream().filter(act ->
					act.getShortName().equals(action.getShortName())
			).findFirst();

			ActionConfig actionConfig;
			if(config.isPresent()) {
				actionConfig = config.get();
			} else {
				actionConfig = new ActionConfig();
				actionConfig.setShortName(action.getShortName());
			}
			actionConfig.setName(action.getName());
			actionConfig.setSortOrder(action.getSortOrder());
			actionConfigSet.add(actionConfig);
		}
		destination.getActions().clear();
		destination.getActions().addAll(actionConfigSet);

		if (!destination.getActions().isEmpty()) {
			updateModelActionList(destination);
		}
	}

	private void updateObligations(PolicyModel source, PolicyModel destination) {
		Set<ObligationConfig> obligationConfigSet = new TreeSet<>();
		for (ObligationConfig obligation : source.getObligations()) {
			Optional<ObligationConfig> config = destination.getObligations().stream().filter(obl ->
					obl.getShortName().equals(obligation.getShortName())
			).findFirst();

			ObligationConfig obligationConfig;
			if(config.isPresent()) {
				obligationConfig = config.get();
			} else {
				obligationConfig = new ObligationConfig();
				obligationConfig.setShortName(obligation.getShortName());
			}
			obligationConfig.setName(obligation.getName());
			obligationConfig.setRunAt(obligation.getRunAt());

			Set<ParameterConfig> parameterConfigSet = new TreeSet<>();
			for (ParameterConfig parameter : obligation.getParameters()) {
				Optional<ParameterConfig> paramConfig = obligationConfig.getParameters().stream().filter(param ->
						param.getShortName().equals(parameter.getShortName())
				).findFirst();

				ParameterConfig parameterConfig;
				if(paramConfig.isPresent()) {
					parameterConfig = paramConfig.get();
				} else {
					parameterConfig = new ParameterConfig();
					parameterConfig.setShortName(parameter.getShortName());
				}
				parameterConfig.setType(parameter.getType());
				parameterConfig.setName(parameter.getName());
				parameterConfig.setMandatory(parameter.isMandatory());
				parameterConfig.setHidden(parameter.isHidden());
				parameterConfig.setEditable(parameter.isEditable());
				parameterConfig.setDefaultValue(parameter.getDefaultValue());
				parameterConfig.setListValues(parameter.getListValues());
				parameterConfig.setSortOrder(parameter.getSortOrder());
				parameterConfigSet.add(parameterConfig);
			}
			obligationConfig.getParameters().clear();
			obligationConfig.getParameters().addAll(parameterConfigSet);
			obligationConfig.setSortOrder(obligation.getSortOrder());
			obligationConfigSet.add(obligationConfig);
		}
		destination.getObligations().clear();
		destination.getObligations().addAll(obligationConfigSet);
	}

	/**
	 * Creates, modified or delete action components based on 
	 * the Policy Model action configuration
	 * 
	 * @param policyModel
	 * @throws ConsoleException
	 */
	private void createModifyDeleteActions(PolicyModel policyModel, Map<String, String> originalActions)
			throws ConsoleException, CircularReferenceException {

		Map<String, String> actions = new HashMap<>();
		Map<Long, String[]> modifiedActions = new HashMap<>();
		List<Long> deletedActions = new ArrayList<>();

		List<ComponentLite> referencesList = getPolicyModelReferences(policyModel);

		if (referencesList.isEmpty()) {
			Set<ActionConfig> actionsConfig = policyModel.getActions();
			for (ActionConfig actionConfig : actionsConfig) {
				String actionName = policyModel.getName() + "." + actionConfig.getName();
				boolean isExists = componentMgmtService.isComponentExists(actionName, COMPONENT_TYPE_ACTION);
				if (!isExists) {
					actions.put(actionConfig.getShortName(), actionConfig.getName());
				}
				log.info("No of actions to add = {}", actions.size());
			}
			createActionComponents(policyModel, actions);
		} else {
			getActionComponentChanges(policyModel, referencesList, actions, modifiedActions, originalActions,
					deletedActions);

			if (!actions.isEmpty()) {
				createActionComponents(policyModel, actions);
			}

			if (!modifiedActions.isEmpty()) {
				modifyActionComponents(policyModel, modifiedActions);
			}

			if (!deletedActions.isEmpty()) {
				deleteActionComponents(deletedActions);
			}
		}
	}
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createActionComponents(PolicyModel policyModel, Map<String, String> actions) throws ConsoleException, CircularReferenceException {

		log.debug("Create action components based on policyModel actionConfig");
		int savedActionsCount = 0;

		for (String action : actions.keySet()) {
			String name = getActionComponentName(policyModel.getName(), actions.get(action));

			// save the action component
			ComponentDTO componentDTO = getComponentDTOFromPolicyModel(null, name, action, policyModel, 0);

			componentMgmtService.save(componentDTO);
			savedActionsCount++;
			log.info("{} new actions saved successfully", savedActionsCount);
		}
	}
	
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void modifyActionComponents(PolicyModel policyModel, Map<Long, String[]> actions) throws ConsoleException, CircularReferenceException {

		log.debug("Modify action components based on policyModel actionConfig");
		int modifiedActionsCount = 0;

		for (Long actionId : actions.keySet()) {
			String[] actionDetails = actions.get(actionId);
			String name = getActionComponentName(policyModel.getName(), actionDetails[1]);

			// update the action component
			ComponentDTO componentDTO = getComponentDTOFromPolicyModel(actionId, name, actionDetails[0], policyModel, -1);

			componentMgmtService.modify(componentDTO);
			modifiedActionsCount++;
			log.info("{} new actions saved successfully", modifiedActionsCount);
		}
	}
    
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteActionComponents(List<Long> actionIds) throws ConsoleException {
		log.debug("Delete pre-created action components");

		componentMgmtService.remove(actionIds);
		log.info("{} action components deleted successfully", actionIds.size());
	}
    
	private ComponentDTO getComponentDTOFromPolicyModel(Long componentId, String componentName, String action,
			PolicyModel policyModel, int version) throws ConsoleException {

		ComponentDTO componentDTO = new ComponentDTO(componentId, componentName, componentName,
				PolicyStatus.DRAFT.name());
		componentDTO.setType(COMPONENT_TYPE_ACTION);
		componentDTO.getActions().add(action);
		componentDTO.setCategory(DevEntityType.COMPONENT);
		componentDTO.setDescription(msgBundle.getText("auto.generated.comp.desc"));
		componentDTO.setVersion(version);

		PolicyModelDTO policyModelDto = new PolicyModelDTO();
		policyModelDto.setId(policyModel.getId());
		policyModelDto.setName(policyModel.getName());
		componentDTO.setPolicyModel(policyModelDto);
		componentDTO.setPreCreated(true);

		// create all tags associated with the model
		Set<TagLabel> modelTags = policyModel.getTags();
        Set<TagDTO> compTags = new TreeSet<>();

		// create new components tags if not exist
		if (!modelTags.isEmpty()) {
			for (TagLabel modelTag : modelTags) {

				List<TagLabel> compTagLabels = tagLabelDao.findByKey(modelTag.getKey(), TagType.COMPONENT_TAG);

				if (compTagLabels.isEmpty()) {

					TagLabel tagLabel = new TagLabel(null, modelTag.getKey().toLowerCase(), modelTag.getLabel(),
							TagType.COMPONENT_TAG, Status.get(modelTag.getStatus().name()));
					TagLabel savedCompTag = tagLabelService.saveTag(tagLabel);
					TagDTO compTag = TagDTO.getDTO(savedCompTag);
					compTags.add(compTag);
				} else {
					TagLabel compTagLabel = compTagLabels.get(0);
					TagDTO compTag = TagDTO.getDTO(compTagLabel);
					compTags.add(compTag);
				}
			}
		}

		componentDTO.setTags(compTags);
		return componentDTO;
	}
    
	/**
	 * Generates the action component name based on policy model
	 *
	 * @param modelName
	 * @param actionName
	 * @return
	 * 
	 */
    private String getActionComponentName(String modelName, String actionName) {

		String name = actionName;

		if (name.length() >= MAX_ALLOWED_LENGTH) {
			name = name.substring(0, MAX_ALLOWED_LENGTH);
		}

		// not checking name uniqueness here
		return name;
	}
	
    /**
     * Get all components where a given policyModel is used
     * 
     * @param policyModel
     * @return 
     * 		list of componentIds that use the given model
     * @throws 
     * 		ConsoleException
     * 
     */
	private List<ComponentLite> getPolicyModelReferences(PolicyModel policyModel) throws ConsoleException {
		List<ComponentLite> refComponents = new ArrayList<>();
		
		try {
			Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
			Page<ComponentLite> resultPage;
			
			do {
				resultPage = componentSearchRepository.findByModelId(policyModel.getId(), pageable);
				refComponents.addAll(resultPage.getContent());
				
				pageable = resultPage.nextPageable();
			} while(resultPage.hasNext());
		} catch (Exception e) {
			throw new ConsoleException("Error encountered in checking policy model references", e);
		}
		
		return refComponents;
	}
    
    /**
     * Returns the actions to be created, modified or deleted based on 
     * Policy Model modifications
     * 
     * @param policyModel
     * 			Policy Model currently being saved or modified
	 * @param componentList
     * 			list of components where the given policy model is referenced
     * @param newActions
     * 			map containing new action components that need to be created
     * @param modifiedActions
     * 			map containing modified action components 
	 * @param originalActions
     * 			map containing modified action components names
	 * @param deletedActions
     * 			list of actionIds that need to be deleted
     * 
     */
	private void getActionComponentChanges(PolicyModel policyModel, List<ComponentLite> componentList,
			Map<String, String> newActions, Map<Long, String[]> modifiedActions, Map<String, String> originalActions,
            List<Long> deletedActions) {

		Map<String, String> renamedActions = new HashMap<>();
		Set<String> deletedActionShortNames = new HashSet<>();
		Set<String> latestActionShortNames = new HashSet<>();

		for (ActionConfig actionConfig : policyModel.getActions()) {
			String actionShortName = actionConfig.getShortName();
			latestActionShortNames.add(actionShortName);

			if (actionConfig.getId() == null) {
				// get new actions
				newActions.put(actionConfig.getShortName(), actionConfig.getName());
			} else if (!originalActions.get(actionShortName).equals(actionConfig.getName())) {
				// get modified actions
				renamedActions.put(actionConfig.getShortName(), actionConfig.getName());
			}
		}

		// get deleted actions
		for (Entry<String, String> entry : originalActions.entrySet()) {
			if (!latestActionShortNames.contains(entry.getKey())) {
				deletedActionShortNames.add(entry.getKey());
			}
		}

		for (ComponentLite component : componentList) {
			Long componentId = component.getId();
			if (!component.isPreCreated())
				continue;
			String actionShortName = component.getPredicateData().getActions().get(0);
			if (deletedActionShortNames.contains(actionShortName)) {
				deletedActions.add(componentId);
			} else if (renamedActions.containsKey(actionShortName)) {
				String[] actionDetails = new String[2];
				actionDetails[0] = actionShortName;
				actionDetails[1] = renamedActions.get(actionShortName);
				modifiedActions.put(componentId, actionDetails);
			}
		}
	}
	
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PolicyModel findById(Long id) throws ConsoleException {
        try {
            PolicyModel policyModel = policyModelDao.findById(id);

            if (policyModel == null) {
                log.info("No policy model for given id: {} ", id);
            } else {
                policyModel.getTags().size();
                policyModel.getAttributes().size();
                policyModel.getActions().size();
                policyModel.getObligations().size();
            }
            return policyModel;
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while finding a policy model by Id", ex);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PolicyModel findActivePolicyModelById(Long id)
            throws ConsoleException {

        PolicyModel policyModel = findById(id);
        
        if (policyModel == null
                || policyModel.getStatus().compareTo(DELETED) == 0) {
            return null;
        }
        
        //check for references
		long policyModelId = policyModel.getId();
		if (policyModelId != 0) {
			setModelAttributesInUse(policyModel);
			setModelActionsInUse(policyModel);
			setModelObligationsInUse(policyModel);
		}
        
        return policyModel;
    }

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public PolicyModel clone(Long id, boolean skipShortName, boolean checkUniqueName) 
			throws ConsoleException, CircularReferenceException {

		PolicyModel policyModel = findById(id);

		if (policyModel != null) {
			return clone(policyModel, skipShortName, checkUniqueName);
		} else
			return null;
	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public PolicyModel clone(PolicyModel policyModel, boolean skipShortName, 
			boolean checkUniqueName) throws ConsoleException, CircularReferenceException {
        PolicyModel cloneModel = new PolicyModel();
        String modelName = checkUniqueName ? getClonedName(policyModel.getName()) : policyModel.getName();
        cloneModel.setName(modelName);
        String shortName = (skipShortName) ? "" : policyModel.getShortName();
        cloneModel.setShortName(shortName);
        cloneModel.setDescription(policyModel.getDescription());
        cloneModel.setStatus(ACTIVE);
        cloneModel.setType(policyModel.getType());

        List<Long> tagIds = new ArrayList<>();
        for (TagLabel tag : policyModel.getTags()) {
            tagIds.add(tag.getId());
        }

        for (AttributeConfig attribute : policyModel.getAttributes()) {
            AttributeConfig attrib = new AttributeConfig();
            attrib.setName(attribute.getName());
            attrib.setShortName(attribute.getShortName());
            for (OperatorConfig operator : attribute.getOperatorConfigs()) {
                OperatorConfig operConfig = operatorConfigService.
                		findByKeyAndDataType(operator.getKey(), 
                				operator.getDataType());
              
                attrib.getOperatorConfigs().add(operConfig);
            }
            attrib.setDataType(attribute.getDataType());
            attrib.setRegExPattern(attribute.getRegExPattern());
            attrib.setSortOrder(attribute.getSortOrder());
            cloneModel.getAttributes().add(attrib);
        }

        for (ActionConfig action : policyModel.getActions()) {
            ActionConfig newAction = new ActionConfig();
            newAction.setName(action.getName());
            newAction.setShortName(action.getShortName());
            newAction.setShortCode(action.getShortCode());
            newAction.setSortOrder(action.getSortOrder());
            cloneModel.getActions().add(newAction);
        }

        for (ObligationConfig obligation : policyModel.getObligations()) {
            ObligationConfig newOblig = new ObligationConfig();
            newOblig.setName(obligation.getName());
            newOblig.setShortName(obligation.getShortName());
            newOblig.setRunAt(obligation.getRunAt());

            for (ParameterConfig param : obligation.getParameters()) {
                ParameterConfig newParam = new ParameterConfig();
                newParam.setType(param.getType());
                newParam.setName(param.getName());
                newParam.setShortName(param.getShortName());
                newParam.setMandatory(param.isMandatory());
                newParam.setHidden(param.isHidden());
                newParam.setEditable(param.isEditable());
                newParam.setDefaultValue(param.getDefaultValue());
                newParam.setListValues(param.getListValues());
                newParam.setSortOrder(param.getSortOrder());
                newOblig.getParameters().add(newParam);
            }
            newOblig.setSortOrder(obligation.getSortOrder());
            cloneModel.getObligations().add(newOblig);
        }

        save(cloneModel, tagIds, false);

        log.info("Policy model cloned successfully, New Model : {}",
                cloneModel.getId());
        return cloneModel;
    }

    @Override
    public Set<AttributeConfig> loadExtraSubjectAttributes(String type)
            throws ConsoleException {
        log.debug("Load extra subject attributes");

        Set<AttributeConfig> extraAttributes = attributeConfigDao
                .loadExternalSubjectAttributes(type.toUpperCase());

        log.info("Extra subject attributes loaded, size : {}",
                extraAttributes.size());
        return extraAttributes;
    }

    @Override
    public Page<PolicyModel> findByCriteria(SearchCriteria criteria,
            boolean skipDAFilter) throws ConsoleException {
        try {

            log.debug("Search Criteria :[{}]", criteria);
            return findByCriteria(criteria,
                    skipDAFilter, PageRequest.of(criteria.getPageNo(), criteria.getPageSize()));
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policy model by given criteria",
                    e);
        }
    }

    @Override
    public Page<PolicyModel> findByIds(List<Long> ids,
            List<SortField> sortFields, PageRequest pageable)
            throws ConsoleException {
        try {

			BoolQueryBuilder query = QueryBuilders.boolQuery()
					.must(QueryBuilders.matchAllQuery())
					.must(withStatuses(Status.ACTIVE));
			QueryBuilder filter = QueryBuilders.termsQuery("id", ids.toArray());
			Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withFilter(filter).withPageable(pageable)
                    .build();
            searchQuery = withSorts(searchQuery, sortFields);

            log.debug("Policy model search by Ids query :{}, filter:{}",
                    query.toString(), filter.toString());

            Page<PolicyModel> policyModelPage = policyModelSearchRepository
                    .search(searchQuery);

            log.info("Policy model page by given ids :{}, No of elements :{}",
                    policyModelPage.getTotalPages(),
                    policyModelPage.getNumberOfElements());
            return policyModelPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policy models for given ids", e);
        }
    }

    @Override
    public FacetResult findFacetByCriteria(SearchCriteria criteria)
            throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);

            String facetName = criteria.getFacetField();
            BoolQueryBuilder query = buildQuery(criteria.getFields());
            query.must(withStatuses(Status.ACTIVE));

			NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder()
					.withQuery(query);
			appendAccessControlTags(nativeQuery);

			Query searchQuery = nativeQuery
					.addAggregation(
							AggregationBuilders.terms(facetName)
									.field(facetName)
									.order(BucketOrder.key(true)))
					.build();

			log.debug("Policy model facet search query :{},", query.toString());
			AggregatedPage<PolicyModel> aggregatedPage = (AggregatedPage<PolicyModel>) policyModelSearchRepository
					.search(searchQuery);
			StringTerms terms = (StringTerms) aggregatedPage.getAggregation(facetName);
			FacetResult facetResult = new FacetResult(facetName);

			for (StringTerms.Bucket bucket : terms.getBuckets()) {
				facetResult.getTerms()
						.add(FacetTerm.create(bucket.getKeyAsString(), Math.toIntExact(bucket.getDocCount())));
			}
			log.info(
					"Policy model facet search query :[ Facet :{}, No of terms :{}]",
					facetName, facetResult.getTerms().size());
			return facetResult;
		} catch (Exception e) {
			throw new ConsoleException(
					"Error encountered in find policy by given criteria", e);
		}
    }

    @Override
    public Page<PolicyModel> findPolicyModelsByType(PolicyModelType type,
            PageRequest pageable) throws ConsoleException {
        try {
            log.debug("Find Policy Models by type :[{}]", type);
            Page<PolicyModel> policyModels = policyModelSearchRepository
                    .findByTypeAndStatus(type.name(), ACTIVE, pageable);

            log.info("Policy models by Type :[ Type :{}, No of items :{}]",
                    type.getLabel(), policyModels.getNumberOfElements());
            return policyModels;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policy by type", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean remove(Long id) throws ConsoleException {
        PolicyModel policyModel = policyModelDao.findById(id);
        boolean isRemoveAllowed = true;

        if (policyModel == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.entity.found.delete.code"), msgBundle
                            .getText("no.entity.found.delete", "Policy Model"));
        } else {

			accessControlService.authorizeByTags(ActionType.DELETE,
					DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS,
					policyModel,
					false);

            try {
                if (PolicyModelType.SUBJECT.equals(policyModel.getType())
					|| isPolicyModelInUse(id)) {
                    isRemoveAllowed = false;
                } else {
                	String snapshot = PolicyModelDTO.getDTO(policyModel).toAuditString();
                    policyModel.setStatus(Status.DELETED);
                    policyModelDao.update(policyModel);
                    entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE, AuditableEntity.COMPONENT_TYPE.getCode(),
                    		policyModel.getId(), snapshot, null);
					policyModelSearchRepository.deleteById(id);
                }
            } catch (Exception ex) {
                throw new ConsoleException(
                        "Error occured while deleting an policy model ", ex);
            }
        }
        return isRemoveAllowed;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<String> remove(List<Long> ids) throws ConsoleException {
		List<PolicyModel> authorizedPolicyModels = new ArrayList<>();

		// Policy model will be removed only if the user has permission to remove all requested policy models.
		for (Long id : ids) {
			PolicyModel policyModel = policyModelDao.findById(id);
			if (policyModel != null) {
				accessControlService.authorizeByTags(ActionType.DELETE,
						DelegationModelShortName.POLICY_MODEL_ACCESS_TAGS,
						policyModel,
						false);
				authorizedPolicyModels.add(policyModel);
			}
		}

        List<String> policyModelNames = new ArrayList<>();
        try {
            for (PolicyModel policyModel : authorizedPolicyModels) {
                if (PolicyModelType.SUBJECT.equals(policyModel.getType())
					|| isPolicyModelInUse(policyModel.getId())) {
                    policyModelNames.add(policyModel.getName());
                } else {
                	String snapshot = PolicyModelDTO.getDTO(policyModel).toAuditString();
                    policyModel.setStatus(Status.DELETED);
                    policyModelDao.update(policyModel);
                    entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE, AuditableEntity.COMPONENT_TYPE.getCode(),
                    		policyModel.getId(), snapshot, null);
					policyModelSearchRepository.deleteById(policyModel.getId());
                }
            }
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error occured in bulk delete operation on policy models ",
                    e);
        }
        return policyModelNames;
    }

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	public void reIndexAllModels() throws ConsoleException {
		try {
			policyModelSearchRepository.deleteAll();

			List<PolicyModel> policyModels = policyModelDao.findByTypes(PolicyModelType.SUBJECT,
					PolicyModelType.RESOURCE);

			int count = 0;
			for (PolicyModel policyModel : policyModels) {
				if (policyModel.getStatus().equals(ACTIVE)) {
					policyModel = findById(policyModel.getId());
					policyModel.setLowercase_name(policyModel.getName());
					policyModel.setLowercase_shortName(policyModel.getShortName());
					policyModelSearchRepository.save(policyModel);
					count++;
				}
			}

			log.info("Policy model re-indexing successfull, No of re-indexes :{}", count);
		} catch (Exception e) {
			throw new ConsoleException("Error encountered in re-indexing Policy models", e);
		}
	}

	public List<PolicyModel> findModelsByType(PolicyModelType type)
            throws ConsoleException {
        try {
            return policyModelDao.findByType(type);
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while finding a policy model by Type", ex);
        }
    }

    /**
     * 
     * Checks if a Policy Model is referenced by any component
     * 
     * @param policyModelId
     * @return
     * @throws ConsoleException
     */
	private boolean isPolicyModelInUse(Long policyModelId) 
			throws ConsoleException {
		try {
			// use page size 10, if one record is referring to this policy model, then it is in use
			Page<ComponentLite> compPage = componentSearchRepository.findByModelId(policyModelId, PageRequest.of(0, 10));
			List<ComponentLite> components = compPage.getContent();

			return !components.isEmpty();
		} catch (Exception e) {
			throw new ConsoleException("Error encountered in checking if policy model is in use",
					e);
		}
	}

    private void checkShortNameIsUnique(String shortName) {

        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
		BoolQueryBuilder filter = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("lowercase_shortName", shortName.toLowerCase()))
				.must(QueryBuilders.boolQuery()
						.should(QueryBuilders.termQuery("type", PolicyModelType.RESOURCE.name()))
						.should(QueryBuilders.termQuery("type", PolicyModelType.SUBJECT.name())));

		Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query).withFilter(filter)
				.withPageable(PageRequest.of(0, 1)).build();

        Page<PolicyModel> policyModelPage = policyModelSearchRepository
                .search(searchQuery);

        List<PolicyModel> policyModels = policyModelPage.getContent();
        if (!policyModels.isEmpty()) {
            throw new NotUniqueException(
                    msgBundle.getText("server.error.not.unique.code"),
                    msgBundle.getText(
                            "server.error.policy.model.name.not.unique",
                            "short name", shortName));
        }
    }

    private void checkNameIsUnique(String name) {

        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
		BoolQueryBuilder filter = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("lowercase_name", name.toLowerCase()))
				.must(QueryBuilders.boolQuery()
						.should(QueryBuilders.termQuery("type", PolicyModelType.RESOURCE.name()))
						.should(QueryBuilders.termQuery("type", PolicyModelType.SUBJECT.name()))
				);

		Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query).withFilter(filter)
				.withPageable(PageRequest.of(0, 1)).build();

        Page<PolicyModel> policyModelPage = policyModelSearchRepository
                .search(searchQuery);

        List<PolicyModel> policyModels = policyModelPage.getContent();
        if (!policyModels.isEmpty()) {
            throw new NotUniqueException(
                    msgBundle.getText("server.error.not.unique.code"),
                    msgBundle.getText(
                            "server.error.policy.model.name.not.unique", "name",
                            name));
        }
    }

    private String getClonedName(String name) {
 		if (name.length() >= MAX_ALLOWED_LENGTH) {
 			name = name.substring(0, MAX_ALLOWED_LENGTH);
 		}
        while (true) {
            try {
                name = JavaBeanCopier.clonedLabelSuffix(name);
                checkNameIsUnique(name);
                break;
            } catch (NotUniqueException ce) {
                continue;
            }
        }
        return name;
    }

    /**
     * Set the Action short code before insert
     * 
     * @param policyModel
     * @throws ConsoleException
     */
    private void updateModelActionList(PolicyModel policyModel)
            throws ConsoleException {
        Set<ActionConfig> actions = policyModel.getActions();
        // get the current value of short code
        String currentVal = actionConfigDao.getLatestShortCode();
        for (ActionConfig action : actions) {
            if (action.getId() == null) {
            	//generate shortCode only if not exists
            	if (StringUtils.isBlank(action.getShortCode())){
            		// generate the next value in the sequence
                    String shortCode = getUniqueActionShortCode(currentVal);
                    if (shortCode == null) {
                        log.info("Error occurred while saving action config");
                        throw new ConsoleException(
                                "Error occurred in saving actions of Policy Model");
                    }
                    action.setShortCode(shortCode);
                    currentVal = shortCode;
            	}
            } else {
                ActionConfig actionConfig = actionConfigDao
                        .findById(action.getId());
                action.setVersion(actionConfig.getVersion());
                action.setCreatedDate(actionConfig.getCreatedDate());
                action.setShortCode(actionConfig.getShortCode());
            }
        }
        policyModel.setActions(actions);
    }

    private void validateNameAndShortName(PolicyModel policyModel)
            throws ConsoleException {

        PolicyModel savedModel = null;
        if (policyModel.getId() != null) {
            savedModel = findById(policyModel.getId());
        }

        if (savedModel != null) { // update
            // short name
            if (!((policyModel.getShortName() == null
                    || isEmpty(policyModel.getShortName()))
                    || policyModel.getShortName()
                            .equals(savedModel.getShortName()))) {
                checkShortNameIsUnique(policyModel.getShortName());
            }
            // name
            if ((savedModel.getName() != null)
                    && !savedModel.getName().equals(policyModel.getName())) {
                checkNameIsUnique(policyModel.getName());
            }
        } else { // new or clone
            checkNameIsUnique(policyModel.getName());
            if (!(policyModel.getShortName() == null
                    || isEmpty(policyModel.getShortName()))) {
                checkShortNameIsUnique(policyModel.getShortName());
            }
        }
    }

    private void appendAccessControlTags(NativeSearchQueryBuilder nativeQuery) {
        PrincipalUser principal = getCurrentUser();
        if (principal.isSuperUser()) {
            log.debug(
                    "Access control filters do not applicable for super user");
            return;
        }

		ApplicationUser user = appUserSearchRepository.findById(principal.getUserId()).orElse(null);
        if(user == null) {
        	throw new AccessDeniedException("User not found");
		}
		QueryBuilder tagsFilter = buildTagFilterQuery(user.getPolicyModelAccessibleTags());
        if (tagsFilter == null) {
            log.info(
                    "Policy model access control tags not found or not applicable for user, [ user : {}]",
                    user.getUsername());
            return;
        }
        log.debug("Access control filter :{}", tagsFilter.toString());
        nativeQuery.withFilter(tagsFilter);
    }

    private Page<PolicyModel> findByCriteria(SearchCriteria criteria,
            boolean skipDAFilter, Pageable pageable) throws ConsoleException {
        try {

            log.debug("Search Criteria :[{}]", criteria);

            List<SearchField> searchFields = criteria.getFields();
            BoolQueryBuilder query = buildQuery(searchFields);
            query.must(withStatuses(Status.ACTIVE));
            NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable);
            if(!skipDAFilter) {
                appendAccessControlTags(nativeQuery);
            }

			Query searchQuery = withSorts(nativeQuery.build(), criteria.getSortFields());

            log.debug("Policy model search query :{},", query.toString());
            Page<PolicyModel> policyModelPage = policyModelSearchRepository.search(searchQuery);

            log.info("PolicyModel page :{}, No of elements :{}",
                    policyModelPage.getTotalPages(),
                    policyModelPage.getNumberOfElements());
            return policyModelPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policy model by given criteria",
                    e);
        }
    }
    
    /**
     * 
     * Check if model attributes are referenced in any component
     * @param {@link PolicyModel}
     * 
     */
    private void setModelAttributesInUse (PolicyModel model){
    	
    	//for each attribute, check against every component
    	Set<AttributeConfig> attributesConfig = model.getAttributes();
    	
    	if(!attributesConfig.isEmpty()) {
    		Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
    		Page<ComponentLite> resultPage;
    		List<ComponentLite> components = new LinkedList<>();
    		
    		do {
    			resultPage = componentSearchRepository.findByModelId(model.getId(), pageable);
    			components.addAll(resultPage.getContent());
    			
        		pageable = resultPage.nextPageable();
    		} while(resultPage.hasNext());
    		
    		for(AttributeConfig attrConfig : attributesConfig){
	    		checkAttributeReferences (model.getId(), attrConfig, attrConfig.getShortName(), components);
	    	}
    	}
    }

    /**
     * 
     * Check if model actions are referenced in any component
     * @param {@link PolicyModel}
     * 
     */
	private void setModelActionsInUse(PolicyModel model) throws ConsoleException {

		// for each action check against every component
		Set<ActionConfig> actionsConfig = model.getActions();
		
		if(!actionsConfig.isEmpty()) {
    		Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
    		Page<ComponentLite> resultPage;
    		List<ComponentLite> components = new LinkedList<>();
			
    		do {
    			resultPage = componentSearchRepository.findByModelId(model.getId(), pageable);
    			components.addAll(resultPage.getContent());
    			
    			pageable = resultPage.nextPageable();
    		} while(resultPage.hasNext());
			
			for(ActionConfig actionConfig : actionsConfig) {
				checkActionReferences(model.getId(), actionConfig, actionConfig.getShortName(), components);
			}
		}
	}
	
	/**
	 * Check if the model obligations are referenced by any policy
	 * @param {@link PolicyModel}
	 * @throws ConsoleException
	 * 
	 */
	private void setModelObligationsInUse(PolicyModel model) 
			throws ConsoleException {
		
		//for each obligation check if it is contained in the above map
		Set<ObligationConfig> obligations = model.getObligations();
		
		if(!obligations.isEmpty()) {
			
			Set<String> policyObligations = new HashSet<>();
			Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
			Page<PolicyLite> resultPage;
			
			do {
				resultPage = policySearchRepository.findAll(pageable);
				for (PolicyLite policyLite : resultPage.getContent()) {
					PolicyDTO policy = policyMgmtService.findById(policyLite.getId());
					if (policy != null) {
                        for (ObligationDTO allowObligation : policy.getAllowObligations()) {
                            if (allowObligation.getPolicyModelId().equals(model.getId()))
                                policyObligations.add(allowObligation.getName());
                        }

                        for (ObligationDTO denyObligation : policy.getDenyObligations()) {
                            if (denyObligation.getPolicyModelId().equals(model.getId()))
                                policyObligations.add(denyObligation.getName());
                        }
                    } else {
						log.debug("Policy not found in database. Cleaning up ElasticSearch. policy id: {}",
								policyLite.getId());
						policySearchRepository.deleteById(policyLite.getId());
					}
				}
				
				pageable = resultPage.nextPageable();
			} while(resultPage.hasNext());
			
			Iterator<ObligationConfig> oblIter = obligations.iterator();
			while (oblIter.hasNext()) {
				ObligationConfig obligation = oblIter.next();
				checkObligationReferences(obligation, obligation.getShortName(), policyObligations);
			}
		}
	}
	  
	private void checkObligationReferences(ObligationConfig obligation, String shortName,
            Set<String> policyObligations) {

		for (String obl : policyObligations) {
			if (obl.equals(shortName)) {
				obligation.setIsReferenced(true);
				return;
			} else {
				obligation.setIsReferenced(false);
			}
		}
	}
	
    private void checkAttributeReferences (Long policyModelId, AttributeConfig attrConfig,
    		 String shortName, List<ComponentLite> allComponents ){
    	
    	for (ComponentLite acomponent : allComponents){
    		Long modelId = acomponent.getModelId();
        	PredicateData predicateData = acomponent.getPredicateData();
        	List<Attribute> attributes = predicateData.getAttributes();
        	for (Attribute attr : attributes ){
        		String attrLhs = attr.getLhs();
				if (attrLhs.equals(shortName) && 
						(modelId.compareTo(policyModelId) == 0)) {
					attrConfig.setIsReferenced(true);
					return;
				} else {
					attrConfig.setIsReferenced(false);
				}
        	}
        }
    }
      
	private void checkActionReferences(Long policyModelId, ActionConfig actionConfig, String shortName,
			List<ComponentLite> allComponents) throws ConsoleException {

		for (ComponentLite acomponent : allComponents) {
			Long modelId = acomponent.getModelId();
			PredicateData predicateData = acomponent.getPredicateData();
			List<String> actions = predicateData.getActions();
			for (String action : actions) {
				if (action.equals(shortName) && (policyModelId.compareTo(modelId) == 0)) {
					if (!(acomponent.isPreCreated())) {
						actionConfig.setIsReferenced(true);
					} else {
						actionConfig.setIsReferenced(isComponentReferenced(acomponent));
					}
					return;
				} else {
					actionConfig.setIsReferenced(false);
				}
			}
		}
	}
    
	private boolean isComponentReferenced(ComponentLite componentLite) throws ConsoleException {
		Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
		Page<PolicyLite> resultPage;

		do {
			resultPage = policySearchRepository.findAll(pageable);
			List<PolicyLite> policies = resultPage.getContent();
			
			for (PolicyLite policy : policies) {
				PolicyDTO policyDto = policyMgmtService.findActiveById(policy.getId());
				if (policyDto != null) {
					for (PolicyComponent policyComp : policyDto.getActionComponents()) {
						for (ComponentDTO compDTO : policyComp.getComponents()) {
							if (compDTO.getId().compareTo(componentLite.getId()) == 0) {
								return true;
							}
						}
					}
				} else {
					log.debug("Active policy not found in database. Cleaning up ElasticSearch. policy id: {}",
							policy.getId());
					policySearchRepository.deleteById(policy.getId());
				}
			}
			
			pageable = resultPage.nextPageable();
		} while(resultPage.hasNext());
		
		return false;
	}
    
	private String getUniqueActionShortCode(String currentVal) {

		String nextVal = ActionShortCodeGenerator.getSeqNextVal(currentVal);
		boolean isUnique = isShortCodeUnique(nextVal);
		while (!isUnique) {
			currentVal = nextVal;
			nextVal = ActionShortCodeGenerator.getSeqNextVal(currentVal);
			isUnique = isShortCodeUnique(nextVal);
		}

		return nextVal;
	}

	private boolean isShortCodeUnique(String shortCodeVal) {

		boolean isUnique = true;
		if (preDefinedActions.contains(shortCodeVal)) {
			return false;
		}

		return isUnique;
	}
	
	/**
	 * Update component which using the 
	 * @param policyModel Policy model which get updated
	 * @throws ConsoleException
	 */
	private void updateComponentModelType(PolicyModel policyModel)
		throws ConsoleException {
		try {
			Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
			Page<ComponentLite> resultPage;
			
			do {
				resultPage = componentSearchRepository.findByModelId(policyModel.getId(), pageable);
				List<ComponentLite> components = resultPage.getContent();
				
				if(!components.isEmpty()) {
					for(ComponentLite component : components) {
						component.setModelType(policyModel.getName());
					}
					
					componentSearchRepository.saveAll(components);
				}
				
				pageable = resultPage.nextPageable();
			} while(resultPage.hasNext());
		} catch(Exception err) {
			throw new ConsoleException("Error encountered in updating policy model references", err);
		}
	}
}

