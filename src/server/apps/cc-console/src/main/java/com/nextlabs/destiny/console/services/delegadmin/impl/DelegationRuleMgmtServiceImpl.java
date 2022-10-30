/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 26, 2016
 *
 */
package com.nextlabs.destiny.console.services.delegadmin.impl;

import static com.nextlabs.destiny.console.enums.AuditLogComponent.DA_RULE_MGMT;
import static com.nextlabs.destiny.console.enums.DevEntityType.DELEGATION_COMPONENT;
import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.DELETED;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.withSorts;

import javax.annotation.Resource;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.JSONArray;
import org.json.JSONObject;
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

import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.nextlabs.destiny.console.dao.policy.PolicyDevelopmentEntityDao;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.delegadmin.DelegateRuleLite;
import com.nextlabs.destiny.console.dto.delegadmin.DelegationRuleDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyComponent;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.DelegationComponentSearchRepository;
import com.nextlabs.destiny.console.search.repositories.DelegationRuleSearchRepository;
import com.nextlabs.destiny.console.services.ApplicationUserService;
import com.nextlabs.destiny.console.services.AuditLogService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.delegadmin.DelegationComponentSearchService;
import com.nextlabs.destiny.console.services.delegadmin.DelegationRuleMgmtService;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;

/**
 *
 * Delegation rule management service implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class DelegationRuleMgmtServiceImpl
        implements DelegationRuleMgmtService {

    private static final Logger log = LoggerFactory
            .getLogger(DelegationRuleMgmtServiceImpl.class);

    @Autowired
    private ComponentMgmtService componentMgmtService;

    @Autowired
    private PolicyMgmtService policyMgmtService;

    @Autowired
    private PolicyDevelopmentEntityDao policyDevelopmentEntityDao;

    @Autowired
    private DelegationComponentSearchService delegComponentSearchService;

    @Autowired
    private DelegationComponentSearchRepository delegationComponentSearchRepository;
    
    @Autowired
    private MessageBundleService msgBundle;

    @Autowired
    private AuditLogService auditService;

    @Resource
    private DelegationRuleSearchRepository delegationRuleSearchRepository;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;

    private ApplicationUserService applicationUserService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DelegationRuleDTO save(DelegationRuleDTO ruleDTO)
            throws ConsoleException, CircularReferenceException {
        log.info("save method of DelegationRuleDTO invoked, ruleID = {}",
                ruleDTO.getId());
        // save new subject component
        ComponentDTO subjectComp = ruleDTO.getSubjectComponent();
        subjectComp.setName("DELEG_SUBJECT_" + System.currentTimeMillis());
        subjectComp.setType("SUBJECT");
        subjectComp.setCategory(DELEGATION_COMPONENT);
        subjectComp.setStatus(PolicyDevelopmentStatus.DRAFT.name());
        subjectComp.setReIndexAllNow(false);
        subjectComp.setHidden(true);
        subjectComp = componentMgmtService.save(subjectComp);

        log.debug(
                "Delegation rule subject conditions saved as new subject component, [ Id:{}]",
                subjectComp.getId());

        // create policy dto to create rule
        ruleDTO.setStatus(PolicyDevelopmentStatus.DRAFT.name());
        PolicyDTO policyDTO = DelegationRuleDTO.getPolicyDTO(ruleDTO);
        
        // Checking for Duplicate name
        if(policyDTO.getId() == null) {
        	checkRuleNameIsUnique(policyDTO.getName());
        }
        policyDTO = policyMgmtService.save(policyDTO);

        log.info("Delegation rule saved as new policy, [ Id :{}]",
                policyDTO.getId());
        DelegationRuleDTO savedRule = new DelegationRuleDTO();
        savedRule.setId(policyDTO.getId());

        delegComponentSearchService.reIndexComponent(subjectComp.getId());
        applicationUserService.reIndexAllUsers();
        this.reIndexRule(policyDTO.getId());
        
        auditService.save(DA_RULE_MGMT.name(), "audit.new.da.rule",
                policyDTO.getName());

        return savedRule;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DelegationRuleDTO modify(DelegationRuleDTO ruleDTO)
            throws ConsoleException, CircularReferenceException {
        DelegationRuleDTO persistedRule = findById(ruleDTO.getId());
        
        //concurrent access check
        log.info("Modify DA rule starts, Rule id = {} and rule version = {} and dto version = {}", 
                persistedRule.getId(), persistedRule.getVersion(), ruleDTO.getVersion());
        
        ComponentDTO subjectComponent = persistedRule.getSubjectComponent();
        subjectComponent
                .setConditions(ruleDTO.getSubjectComponent().getConditions());
        subjectComponent.setReIndexAllNow(false);
        subjectComponent.setHidden(true);
        subjectComponent = componentMgmtService.modify(subjectComponent);

        PolicyDTO policyDTO = DelegationRuleDTO.getPolicyDTO(ruleDTO);
        // Checking for duplicates
        checkRuleNameIsUniqueOnUpdate(policyDTO.getName(), policyDTO.getId());
        
        policyDTO = policyMgmtService.modify(policyDTO);

        log.debug("Delegation rule modified successfully, [ Id :{}]",
                policyDTO.getId());

        DelegationRuleDTO savedRule = new DelegationRuleDTO();
        savedRule.setId(policyDTO.getId());

        delegComponentSearchService.reIndexComponent(subjectComponent.getId());
        applicationUserService.reIndexAllUsers();
        this.reIndexRule(ruleDTO.getId());
        return savedRule;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DelegationRuleDTO findById(Long id) throws ConsoleException {
        PolicyDTO policyDTO = policyMgmtService.findActiveById(id);

        if (policyDTO == null) {
            log.warn("Delegation rule not found, [ Id :{}]", id);
            return null;
        }

        DelegationRuleDTO ruleDTO = DelegationRuleDTO.getDTO(policyDTO);
        ruleDTO.getObligations().stream()
                .filter(obligationDTO -> DelegationModelShortName.POLICY_ACCESS_TAGS.name().equals(obligationDTO.getName())
                        || DelegationModelShortName.COMPONENT_ACCESS_TAGS.name().equals(obligationDTO.getName()))
                .forEach(obligationDTO -> obligationDTO.getParams().forEach((key, param) -> {
                    JSONObject tagFiltersObject = new JSONObject(param);
                    if (!hasFolderTags(tagFiltersObject)) {
                        JSONObject allFoldersTagFilter = new JSONObject().put("operator", "IN")
                                .put("tags", new JSONArray().put(new JSONObject()
                                        .put("id", JSONObject.NULL)
                                        .put("key", "all_folders")
                                        .put("label", "All Folders")
                                        .put("type", "FOLDER_TAG")
                                        .put("status", JSONObject.NULL)
                                ));
                        tagFiltersObject.getJSONArray("tagsFilters").put(allFoldersTagFilter);
                        obligationDTO.getParams().put(key, tagFiltersObject.toString());
                    }
                }));

        // load subject component details
        if (!policyDTO.getSubjectComponents().isEmpty()) {
            PolicyComponent policyComponent = policyDTO.getSubjectComponents()
                    .get(0);
            if (!policyComponent.getComponents().isEmpty()) {
                ComponentDTO subjectComponent = policyComponent.getComponents()
                        .get(0);
                subjectComponent = componentMgmtService
                        .findActiveById(subjectComponent.getId());
                ruleDTO.setSubjectComponent(subjectComponent);
            }
        }
        log.info("Delegation rule found, [ Id :{}]", ruleDTO.getId());

        return ruleDTO;
    }

    private boolean hasFolderTags(JSONObject tagFiltersObject) {
        for (Object jsonObject : tagFiltersObject.getJSONArray("tagsFilters")) {
            JSONObject tagFilter = (JSONObject) jsonObject;
            for (Object tagObject : tagFilter.getJSONArray("tags")) {
                JSONObject tag = (JSONObject) tagObject;
                if (TagType.FOLDER_TAG.name().equals(tag.get("type"))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void remove(Long id) throws ConsoleException {
        removeDelegRuleAndRefs(id);

        log.info("Delegation rule removed successfully, [ Id :{}]", id);
    }

    private void removeDelegRuleAndRefs(Long id) throws ConsoleException {
        DelegationRuleDTO persistedRule = findById(id);
        ComponentDTO subjectComponent = persistedRule.getSubjectComponent();
        componentMgmtService.updateStatus(subjectComponent.getId(), DELETED);

        persistedRule.setStatus(PolicyDevelopmentStatus.DELETED.name());
        PolicyDTO policyDTO = DelegationRuleDTO.getPolicyDTO(persistedRule);
        policyMgmtService.modify(policyDTO);
        
        delegationComponentSearchRepository.deleteById(subjectComponent.getId());
        delegationRuleSearchRepository.deleteById(id);
        applicationUserService.reIndexAllUsers();

        auditService.save(DA_RULE_MGMT.name(), "audit.delete.da.rule",
                policyDTO.getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void remove(List<Long> ids) throws ConsoleException {
        for (Long id : ids) {
            removeDelegRuleAndRefs(id);
        }
        log.info("Delegation rules removed successfully");
    }

    @Override
    public Page<DelegateRuleLite> findPolicyByCriteria(SearchCriteria criteria)
            throws ConsoleException {
        try {

            log.debug("Search Criteria :[{}]", criteria);
            PageRequest pageable = PageRequest.of(criteria.getPageNo(), criteria.getPageSize());

            List<SearchField> searchFields = criteria.getFields();
            BoolQueryBuilder query = buildQuery(searchFields);

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable).build();

            withSorts(searchQuery, criteria.getSortFields());

            log.debug("Delegation search query :{},", query.toString());
            Page<DelegateRuleLite> ruleListPage = delegationRuleSearchRepository
                    .search(searchQuery);

            log.info("Delegation list page :{}, No of elements :{}",
                    ruleListPage, ruleListPage.getNumberOfElements());
            return ruleListPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find delegation rules by given criteria",
                    e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reIndexAllRules() throws ConsoleException {
        try {
			delegationRuleSearchRepository.deleteAll();
			
            List<PolicyDevelopmentEntity> devEntities = policyDevelopmentEntityDao
                    .findActiveRecordsByType(
                            DevEntityType.DELEGATION_POLICY.getKey());

            for (PolicyDevelopmentEntity devEntity : devEntities) {
            	try {
            		reIndexRule(devEntity);
            	} catch(Exception err) {
            		log.error(err.getMessage(), err);
            	}
            }

            log.info(
                    "Delegation rule re-indexing successfull, No of re-indexes :{}",
                    devEntities.size());
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in re-indexing delegation rules", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reIndexRule(Long id) throws ConsoleException {
    	try {
    		reIndexRule(policyDevelopmentEntityDao.findById(id));
    	} catch(Exception err) {
    		throw new ConsoleException(err.getMessage(), err);
    	}
    }
    
    @Override
    public void reIndexRule(PolicyDevelopmentEntity entity)
    	throws ConsoleException {
    	if(entity != null) {
    		try {
                String pql = entity.getPql();
                DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);
                IDPolicy policy = domBuilder.processPolicy();
                entity.setPolicy(policy);

                DelegateRuleLite ruleLite = DelegateRuleLite.getLite(entity, appUserSearchRepository);
                delegationRuleSearchRepository.save(ruleLite);
    		} catch(Exception err) {
                throw new ConsoleException(
                        "Delegation rule indexing due to error in rule dom builder, Rule Id : " + entity.getId(),
                        err);
    		}
    	}
    }
    
    @Override
    public boolean isRuleExists(String ruleName) {
        try {
            checkRuleNameIsUnique(ruleName);
            return false;
        } catch (NotUniqueException e) {
            return true;
        }
    }

    private void checkRuleNameIsUnique(String ruleName) {

    	List<DelegateRuleLite> rules = getDelegationPoliciesByName(ruleName);
        if (!rules.isEmpty()) {
            throw new NotUniqueException(
                    msgBundle.getText("server.error.not.unique.code"),
                    msgBundle.getText("server.error.policy.name.not.unique",
                            ruleName));
        }

    }
    
    private void checkRuleNameIsUniqueOnUpdate(String ruleName, Long policyId) {
        List<DelegateRuleLite> rules = getDelegationPoliciesByName(ruleName);
        if (!rules.isEmpty()) {
        	for (DelegateRuleLite rule : rules){
        		if (rule.getId().compareTo(policyId) != 0) {
                    throw new NotUniqueException(
                            msgBundle.getText("server.error.not.unique.code"),
                            msgBundle.getText("server.error.policy.name.not.unique",
                            		ruleName));
                	}
        	}
        }
    }

    @Override
    public List<DelegateRuleLite> getDelegationPoliciesByName(String ruleName) {
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
        BoolQueryBuilder filter = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("name.untouched", ruleName.toLowerCase()));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query).withFilter(filter)
                .withPageable(PageRequest.of(0, 1)).build();

        Page<DelegateRuleLite> ruleLitePage = delegationRuleSearchRepository.search(searchQuery);
        return ruleLitePage.getContent();
    }

    @Autowired
    public void setApplicationUserService(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }
}
