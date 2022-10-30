/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 22, 2016
 *
 */
package com.nextlabs.destiny.console.services.delegadmin.impl;

import static com.nextlabs.destiny.console.enums.Status.ACTIVE;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.delegadmin.DelegateModelDao;
import com.nextlabs.destiny.console.dao.policy.ActionConfigDao;
import com.nextlabs.destiny.console.dao.policy.OperatorConfigDao;
import com.nextlabs.destiny.console.enums.DataType;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.search.repositories.DelegateModelSearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.delegadmin.DelegateModelService;
import com.nextlabs.destiny.console.utils.ActionShortCodeGenerator;

/**
 *
 * Delegate model service implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class DelegateModelServiceImpl implements DelegateModelService {

    private static final Logger log = LoggerFactory
            .getLogger(DelegateModelServiceImpl.class);

    @Autowired
    private DelegateModelDao delegateModelDao;

    @Autowired
    private OperatorConfigDao operatorConfigDao;
    
    @Autowired
    private ActionConfigDao actionConfigDao;

    @Autowired
    protected MessageBundleService msgBundle;

    @Resource
    private DelegateModelSearchRepository delegateModelSearchRepository;
    
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
    public DelegateModel save(DelegateModel model) throws ConsoleException {
        if (model.getId() == null) {
            checkShortNameIsUnique(model.getShortName());
            checkNameIsUnique(model.getName());
            synchronized (this) {
                if (!model.getActions().isEmpty()) {
                    updateModelActionList(model);
                }
                delegateModelDao.create(model);
            }
        } else {
            validateNameAndShortName(model);
            synchronized (this) {
                model.preUpdate();
                if (!model.getActions().isEmpty()) {
                    updateModelActionList(model);
                }
                delegateModelDao.update(model);
            }
        }

        delegateModelSearchRepository.save(model);
        log.debug("Delegation model saved successfully, [ Id: {}]",
                model.getId());
        return model;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public DelegateModel findById(Long id) throws ConsoleException {
        try {
            DelegateModel delegateModel = delegateModelDao.findById(id);

            if (delegateModel == null) {
                log.info("No Delegatemodel for given id: {} ", id);
                return null;
            } else {
                delegateModel.getTags().size();
                delegateModel.getAttributes().size();
                delegateModel.getActions().size();
                delegateModel.getObligations().size();
                return delegateModel;
            }
        } catch (Exception ex) {
            throw new ConsoleException(
                    "Error occured while finding a Delegation model by Id", ex);
        }
    }

    @Override
    public boolean remove(Long id) throws ConsoleException {
        DelegateModel model = delegateModelDao.findById(id);

        if (model == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.entity.found.delete.code"),
                    msgBundle.getText("no.entity.found.delete",
                            "Delegate Model"));
        } else {
            try {
                model.setStatus(Status.DELETED);
                delegateModelDao.update(model);
                delegateModelSearchRepository.deleteById(model.getId());
                return true;
            } catch (Exception ex) {
                throw new ConsoleException(
                        "Error occured while deleting an Delegation model ",
                        ex);
            }
        }
    }

    @Override
    public Page<DelegateModel> findByType(PolicyModelType type,
            PageRequest pageable) throws ConsoleException {
        try {
            log.debug("Find Delegate Model by type :[{}]", type);
            Page<DelegateModel> modelPages = delegateModelSearchRepository
                    .findByTypeAndStatus(type.name(), ACTIVE, pageable);

            log.info("Delegation models by Type :[ Type :{}, No of items :{}]",
                    type.getLabel(), modelPages.getNumberOfElements());
            return modelPages;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find Delegation models by type", e);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void reIndexAllModels() throws ConsoleException {
        try {

            List<DelegateModel> delegModels = delegateModelDao.findByTypes(
                    PolicyModelType.DA_RESOURCE, PolicyModelType.DA_SUBJECT);

            if (!delegModels.isEmpty()) {
                delegateModelSearchRepository.deleteAll();
            }

            int count = 0;
            for (DelegateModel dModel : delegModels) {
                if (dModel.getStatus().equals(ACTIVE)) {
                    DelegateModel model = findById(dModel.getId());
                    delegateModelSearchRepository.save(model);
                    count++;
                }
            }

            log.info(
                    "Delegation model re-indexing successfull, No of re-indexes :{}",
                    count);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in re-indexing Delegation models", e);
        }
    }

    /**
     * Set the Action short code before insert
     *
     * @param delegateModel
     * @throws ConsoleException
     */
	private void updateModelActionList(DelegateModel delegateModel)
			throws ConsoleException {
		Set<ActionConfig> actions = delegateModel.getActions();
		// get the current value of short code
		String currentVal = actionConfigDao.getLatestShortCode();
		for (ActionConfig action : actions) {
            // generate shortCode only if not exists
            if (StringUtils.isBlank(action.getShortCode())) {
                // generate the next value in the sequence
                String shortCode = getUniqueActionShortCode(currentVal);
                if (shortCode == null) {
                    log.info("Error occured while saving action config");
                    throw new ConsoleException(
                            "Error occured in saving actions of Policy Model");
                }
                action.setShortCode(shortCode);
                currentVal = shortCode;
            }
			if (action.getId() != null) {
                ActionConfig actionConfig = actionConfigDao.findById(action.getId());
                action.setVersion(actionConfig.getVersion());
                action.setCreatedDate(actionConfig.getCreatedDate());
                action.setShortCode(actionConfig.getShortCode());
			}
		}
		delegateModel.setActions(actions);
	}

    private void checkShortNameIsUnique(String shortName) {
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
        BoolQueryBuilder filter = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("shortName.na", shortName))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("type", PolicyModelType.DA_SUBJECT.name()))
                        .should(QueryBuilders.termQuery("type", PolicyModelType.DA_RESOURCE.name()))
                );

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query).withFilter(filter)
                .withPageable(PageRequest.of(0, 1)).build();

        Page<DelegateModel> modelPage = delegateModelSearchRepository
                .search(searchQuery);

        List<DelegateModel> models = modelPage.getContent();
        if (!models.isEmpty()) {
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
                .must(QueryBuilders.termQuery("name.na", name))
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("type", PolicyModelType.DA_SUBJECT.name()))
                        .should(QueryBuilders.termQuery("type", PolicyModelType.DA_RESOURCE.name()))
                );

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query).withFilter(filter)
                .withPageable(PageRequest.of(0, 1)).build();

        Page<DelegateModel> modelPage = delegateModelSearchRepository
                .search(searchQuery);

        List<DelegateModel> models = modelPage.getContent();
        if (!models.isEmpty()) {
            throw new NotUniqueException(
                    msgBundle.getText("server.error.not.unique.code"),
                    msgBundle.getText(
                            "server.error.policy.model.name.not.unique", "name",
                            name));
        }
    }

    private void validateNameAndShortName(DelegateModel model)
            throws ConsoleException {
        DelegateModel savedModel = findById(model.getId());
        if (savedModel.getShortName() != null
                || !StringUtils.isEmpty(savedModel.getShortName())) {
            checkShortNameIsUnique(model.getShortName());
        }
        if (savedModel.getName() != null
                && !savedModel.getName().equals(model.getName())) {
            checkNameIsUnique(model.getName());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateDAModelUserAttributes(
            Set<String> userProperties) throws ConsoleException {

        DelegateModel daUserModel = null;
        PageRequest pageReq = PageRequest.of(0, 10);
        Page<DelegateModel> daSubjectsPage = findByType(PolicyModelType.DA_SUBJECT, 
        		pageReq);
        List<DelegateModel> daSubjects = daSubjectsPage.getContent();
        if (!daSubjects.isEmpty()) {
            daUserModel = daSubjects.get(0);
            daUserModel = findById(daUserModel.getId());
        } else {
            log.warn("No DA_Subject policy model found");
            return;
        }

        // existing model attributes
        Set<String> modelAttributes = new HashSet<>();
        for (AttributeConfig attrConfig : daUserModel.getAttributes()) {
            modelAttributes.add(attrConfig.getName());
        }

        getExtraUserAttributes(modelAttributes, userProperties);

        if (!modelAttributes.isEmpty()) {
            Set<AttributeConfig> attributes = daUserModel.getAttributes();
            List<OperatorConfig> strOperConfig = operatorConfigDao
                    .findByDataType(DataType.STRING);

            // add the new attributes to DA_USER Policy Model AttributeConfig
            for (String attrName : modelAttributes) {
                AttributeConfig attrConfig = new AttributeConfig();
                attrConfig.setDataType(DataType.STRING);
                attrConfig.setName(attrName);
                attrConfig.setShortName(attrName);
                attrConfig.setOperatorConfigs(new TreeSet<>(strOperConfig));

                attributes.add(attrConfig);
            }

            daUserModel.setAttributes(attributes);
            save(daUserModel);
        }
    }

    private void getExtraUserAttributes(Set<String> modelAttributes,
            Set<String> userAttributes) {

        Set<String> originalModelAttrs = new HashSet<>();
        originalModelAttrs.addAll(modelAttributes);

        if (!(modelAttributes.containsAll(userAttributes))) {
            for (String userAttr : userAttributes) {
                if (modelAttributes.contains(userAttr)) {
                    continue;
                } else {
                    modelAttributes.add(userAttr);
                }
            }
        }

        modelAttributes.removeAll(originalModelAttrs);
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

}
