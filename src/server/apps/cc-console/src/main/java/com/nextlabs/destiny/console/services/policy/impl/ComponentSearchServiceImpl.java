/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 5, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy.impl;

import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.DELETED;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.withSorts;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.domain.destiny.common.IDSpec;
import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.dao.policy.PolicyDeploymentRecordDao;
import com.nextlabs.destiny.console.dao.policy.PolicyDevelopmentEntityDao;
import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.FacetTerm;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.IncludedComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.SubComponentLite;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.delegadmin.AccessibleTags;
import com.nextlabs.destiny.console.model.policy.ComponentExtDescription;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentRecord;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.policy.visitors.PredicateData;
import com.nextlabs.destiny.console.repositories.PolicyDevelopmentEntityRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.ComponentSearchRepository;
import com.nextlabs.destiny.console.search.repositories.PolicyModelSearchRepository;
import com.nextlabs.destiny.console.search.repositories.SavedSearchRepository;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.ComponentSearchService;
import com.nextlabs.destiny.console.services.policy.FolderService;
import com.nextlabs.destiny.console.services.policy.PolicyDeploymentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;
import com.nextlabs.destiny.console.utils.SearchCriteriaBuilder;
import com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder;

/**
 * Component Search Service implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class ComponentSearchServiceImpl implements ComponentSearchService {

    private static final Logger log = LoggerFactory
            .getLogger(ComponentSearchServiceImpl.class);

    @Autowired
    private PolicyDevelopmentEntityDao policyDevelopmentEntityDao;

    @Autowired
    private PolicyDeploymentRecordDao policyDeploymentRecordDao;

    @Autowired
    private PolicyDeploymentEntityMgmtService policyDeployementMgmtService;
    
    @Autowired
    private PolicyModelService policyModelService;

    @Autowired
    private FolderService folderService;

    @Resource
    private SavedSearchRepository savedSearchRepository;

    @Resource
    private ComponentSearchRepository componentSearchRepository;

    @Resource
    private PolicyModelSearchRepository policyModelSearchRepository;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;

    @Autowired
    private PolicyDevelopmentEntityRepository policyDevelopmentEntityRepository;

    @Autowired
    private ComponentMgmtService componentMgmtService;

    @Override
    public Page<ComponentLite> findByCriteria(SearchCriteria criteria, boolean skipDAFilter)
            throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);
            Pageable pageable = PageRequest.of(criteria.getPageNo(), criteria.getPageSize());
            return findByCriteria(criteria, pageable, skipDAFilter);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find components by given criteria",
                    e);
        }
    }

    private Page<ComponentLite> findByCriteria(SearchCriteria criteria,
                                               Pageable pageable, boolean skipDAFilter) throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);

            BoolQueryBuilder query = buildQuery(criteria.getFields());
            NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable);

            if (!skipDAFilter) {
                appendAccessControlTags(nativeQuery);
            }

            Query searchQuery = withSorts(nativeQuery.build(),
                    criteria.getSortFields());

            log.debug("Component search query :{},", query.toString());
            Page<ComponentLite> componentPage = componentSearchRepository
                    .search(searchQuery);

            log.info("Component list page :{}, No of elements: {}",
                    componentPage.getTotalPages(),
                    componentPage.getNumberOfElements());
            return componentPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find components by given criteria",
                    e);
        }
    }

    @Override
    public Page<ComponentLite> findComponentsByIds(List<Long> ids,
            List<SortField> sortFields, PageRequest pageable)
            throws ConsoleException {
        try {
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            query.must(QueryBuilders.matchAllQuery());
            QueryBuilder filter = QueryBuilders.termsQuery("id", ids.toArray());

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withFilter(filter).withPageable(pageable)
                    .build();

            searchQuery = withSorts(searchQuery, sortFields);

            log.debug("Component search by Ids query :{}, filter:{}",
                    query.toString(), filter.toString());

            Page<ComponentLite> componentPage = componentSearchRepository
                    .search(searchQuery);

            log.info("Component page by given ids :{}, No of elements: {}",
                    componentPage.getTotalPages(),
                    componentPage.getNumberOfElements());
            return componentPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find components for given ids", e);
        }
    }

    @Override
    public FacetResult findFacetByCriteria(SearchCriteria criteria)
            throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);

            String facetName = criteria.getFacetField();
            BoolQueryBuilder query = buildQuery(criteria.getFields());
            NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder().withQuery(query);
            appendAccessControlTags(nativeQuery);
            Query searchQuery = nativeQuery.addAggregation(
                    AggregationBuilders.terms(facetName)
                            .field(facetName)
                            .order(BucketOrder.key(true)))
                    .build();

            log.debug("Component facet search query :{},", query.toString());
            AggregatedPage<ComponentLite> aggregatedPage = (AggregatedPage<ComponentLite>) componentSearchRepository
                    .search(searchQuery);

            StringTerms terms = (StringTerms) aggregatedPage.getAggregation(facetName);
            FacetResult facetResult = new FacetResult(facetName);

            for (StringTerms.Bucket bucket : terms.getBuckets()) {
                facetResult.getTerms()
                        .add(FacetTerm.create(bucket.getKeyAsString(), Math.toIntExact(bucket.getDocCount())));
            }

            log.info(
                    "Component facet search query :[ Facet :{}, No of terms :{}]",
                    facetName, facetResult.getTerms().size());
            return facetResult;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find components by given criteria",
                    e);
        }
    }

    @Override
    public Page<ComponentLite> findComponentsByGroupAndType(String groupType,
                                                            String modelType, Pageable pageable, boolean skipDAFilter)
            throws ConsoleException {
        try {
            log.debug("Find component [ Group type: {}, Model type :{}]",
                    groupType, modelType);
            Page<ComponentLite> componentPage = null;
            SearchCriteriaBuilder criteriaBuilder = SearchCriteriaBuilder
                    .create();
            if (StringUtils.isEmpty(modelType)) {
                criteriaBuilder.addSingleExactMatchField(ComponentLite.GROUP_FIELD,
                        groupType);
                componentPage = this.findByCriteria(criteriaBuilder.getCriteria(), pageable, skipDAFilter);
            } else {
                criteriaBuilder.addSingleExactMatchField(ComponentLite.GROUP_FIELD,
                        groupType);
                criteriaBuilder.addSingleField(ComponentLite.MODEL_TYPE_FIELD,
                        modelType);
                componentPage = this.findByCriteria(criteriaBuilder.getCriteria(), pageable, skipDAFilter);
            }

            log.info(
                    "Component by [ Group type: {}, Model type :{}, No of items :{}]",
                    groupType, modelType, componentPage.getContent().size());
            return componentPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find components by group", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reIndexAllComponents() throws ConsoleException {
        try {
			componentSearchRepository.deleteAll();

            List<PolicyDevelopmentEntity> devEntities = policyDevelopmentEntityDao
                    .findActiveRecordsByType(DevEntityType.COMPONENT.getKey());

			Map<Long, PolicyDevelopmentEntity> componentIdMap = getComponentIdMap(devEntities);

            Map<Long, ComponentLite> componentMap = new HashMap<>();

            for (PolicyDevelopmentEntity devEntity : devEntities) {
                String title = devEntity.getTitle();
                String[] splits = title.split("/", -1);
                String componentGroup = splits[0];

                String extendedDesc = devEntity.getExtendedDescription();
                Long modelTypeId = 0L;
                String modelType = "";
                boolean preCreated = false;

                if (StringUtils.isNotEmpty(extendedDesc)) {
                    ObjectMapper objectMapper = new ObjectMapper();
					ComponentExtDescription componentDesc = objectMapper.readValue(extendedDesc,
                                    ComponentExtDescription.class);

                    PolicyModel policyModel =
                            policyModelSearchRepository.findById(componentDesc.getPolicyModelId()).orElse(null);
					preCreated = componentDesc.isPreCreated();
                    if (policyModel != null) {
                        modelTypeId = policyModel.getId();
                        modelType = policyModel.getName();
                    }
                    else{
                    	 Long compModelId = componentDesc.getPolicyModelId();
                    	 if (compModelId != null){
                    		 PolicyModel model = policyModelService.findActivePolicyModelById(compModelId);
                    		 if (model != null){
                    			 modelTypeId = compModelId;
                    			 modelType = model.getName();
                    		 }                  		 
                    	 }
                    }
                }
                
                String pql = devEntity.getPql();
                PredicateData predicateData = new PredicateData();
                if (StringUtils.isNotEmpty(pql)) {

					DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);
                    IDSpec spec = domBuilder.processSpec();
					predicateData = ComponentPQLHelper.create().getPredicates(spec, pql, componentGroup);

                }
				ComponentLite componentLite = ComponentLite.getLite(devEntity, componentGroup, modelTypeId, modelType,
						componentIdMap, predicateData, appUserSearchRepository);
                PolicyDeploymentEntity depEntity = policyDeployementMgmtService
						.findLastActiveRecordWithRevisionCount(devEntity.getId(), true);
                if (depEntity != null) {
                    componentLite.setActionType(depEntity.getActionType());
					componentLite.setRevisionCount(depEntity.getRevisionCount());
                }
                PolicyDeploymentEntity pendingDeploymentEntity = policyDeployementMgmtService
                        .findLastActiveRecordWithRevisionCount(devEntity.getId(), false);
                if (pendingDeploymentEntity != null) {
                    PolicyDeploymentRecord deploymentRecord = policyDeploymentRecordDao
                            .findById(pendingDeploymentEntity.getDepRecordId());
                    if (deploymentRecord != null && deploymentRecord.getAsOf() != null) {
                        componentLite.setDeploymentTime(deploymentRecord.getAsOf());
                    }
                }
                if (preCreated){
                	componentLite.setPreCreated(true);
                }

                componentMap.put(componentLite.getId(), componentLite);
            }

            Map<Long, Set<ComponentLite>> includedInMap = new HashMap<>();

            for (ComponentLite component : componentMap.values()) {
                for (SubComponentLite sub : component.getSubComponents()) {
                    // Populate sub component details
                    populateSubComponentDetails(componentMap, sub);

					Set<ComponentLite> includes = includedInMap.get(sub.getId());
                    if (includes == null) {
                        includes = new TreeSet<>();
                        includedInMap.put(sub.getId(), includes);
                    }
                    includes.add(component);
                }
            }

            for (Long componentId : includedInMap.keySet()) {
                ComponentLite component = componentMap.get(componentId);
                if (component == null) {
                    continue;
                }
				Set<ComponentLite> includedInComponents = includedInMap.get(componentId);

                for (ComponentLite comp : includedInComponents) {
                    component.setHasIncludedIn(true);
                    // Populate included in component details
					IncludedComponentLite includedComp = new IncludedComponentLite(comp.getId(), comp.getFullName());
					includedComp.setDeployed(comp.isDeployed());
					includedComp.setStatus(comp.getStatus());
                    includedComp.setPolicyModel(comp.getModelType());
                    includedComp.setLastUpdatedDate(comp.getLastUpdatedDate());
                    includedComp.setHasIncludedIn(comp.isHasIncludedIn());
                    includedComp.setHasSubComponents(comp.isHasSubComponents());
                    component.getIncludedInComponents().add(includedComp);
                }
            }

            for (ComponentLite component : componentMap.values()) {
                componentSearchRepository.save(component);
            }

			log.info("Component re-indexing successfull, No of re-indexes :{}", componentMap.values().size());
        } catch (Exception e) {
			throw new ConsoleException("Error encountered in re-indexing components", e);
        }
    }

    private void populateSubComponentDetails(
            Map<Long, ComponentLite> componentMap, SubComponentLite sub) {
        ComponentLite comp = componentMap.get(sub.getId());
        if (comp != null) {
            sub.setDeployed(comp.isDeployed());
            sub.setStatus(comp.getStatus());
            sub.setPolicyModel(comp.getModelType());
            sub.setLastUpdatedDate(comp.getLastUpdatedDate());
            sub.setHasIncludedIn(comp.isHasIncludedIn());
            sub.setHasSubComponents(comp.isHasSubComponents());
        }
    }

    private Map<Long, PolicyDevelopmentEntity> getComponentIdMap(
            List<PolicyDevelopmentEntity> devEntities) {
        Map<Long, PolicyDevelopmentEntity> policyIdMap = new HashMap<>();
        for (PolicyDevelopmentEntity devEntity : devEntities) {
            policyIdMap.put(devEntity.getId(), devEntity);
        }
        return policyIdMap;
    }

    private void appendAccessControlTags(NativeSearchQueryBuilder nativeQuery) {
        PrincipalUser principal = getCurrentUser();
        if (principal.isSuperUser()) {
            log.debug(
                    "Access control filters do not applicable for super user");
            return;
        }

        ApplicationUser user = appUserSearchRepository.findById(principal.getUserId()).orElse(null);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        QueryBuilder tagsFilter = SearchCriteriaQueryBuilder.buildTagFilterQuery(user.getComponentAccessibleTags());
        if (tagsFilter == null) {
            log.info("Components access control tags not found or not applicable for user, [ user : {}]", user.getUsername());
            return;
        }
        log.debug("Access control filter :{}", tagsFilter.toString());

        AccessibleTags accessibleTags = user.getComponentAccessibleTags();
        folderService.addIncludedSubFolderTags(accessibleTags);
        QueryBuilder foldersFilter = SearchCriteriaQueryBuilder.buildFolderFilterQuery(accessibleTags);
        if (foldersFilter == null) {
            log.info("Component access control folders not found or not applicable for user, [ user : {}]", user.getUsername());
            return;
        }
        log.debug("Access control folder filter :{}", foldersFilter);

        nativeQuery.withFilter(QueryBuilders.boolQuery().must(tagsFilter).must(foldersFilter));
    }

    /* (non-Javadoc)
     * @see com.nextlabs.destiny.console.services.policy.ComponentSearchService#reIndexComponents(com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO[])
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reIndexComponents(ComponentDTO... componentDTOs) throws ConsoleException {
        List<ComponentLite> componentLiteList = new ArrayList<>();

        try {
            for (ComponentDTO componentDTO : componentDTOs) {
                PolicyDevelopmentEntity devEntity = policyDevelopmentEntityDao.findById(componentDTO.getId());
                String devEntityType = devEntity.getType();
                
                if (!DevEntityType.COMPONENT.getKey().equalsIgnoreCase(devEntityType)){
                	//skip
                	continue;
                }

                String title = devEntity.getTitle();
                String[] splits = title.split("/", -1);
                String componentGroup = splits[0];

                String extendedDesc = devEntity.getExtendedDescription();
                Long modelTypeId = 0L;
                String modelType = "";
                boolean preCreated = false;

                if (StringUtils.isNotEmpty(extendedDesc)) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ComponentExtDescription componentDesc = objectMapper.readValue(extendedDesc, ComponentExtDescription.class);

                    PolicyModel policyModel =
                            policyModelSearchRepository.findById(componentDesc.getPolicyModelId()).orElse(null);
                    preCreated = componentDesc.isPreCreated();
                    if (policyModel != null) {
                        modelTypeId = policyModel.getId();
                        modelType = policyModel.getName();
                    } else {
                        Long compModelId = componentDesc.getPolicyModelId();
                        if (compModelId != null) {
                            PolicyModel model = policyModelService.findActivePolicyModelById(compModelId);
                            if (model != null) {
                                modelTypeId = compModelId;
                                modelType = model.getName();
                            }
                        }
                    }
                }

                String pql = devEntity.getPql();
                PredicateData predicateData = new PredicateData();
                if (StringUtils.isNotEmpty(pql)) {

                    DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);
                    IDSpec spec = domBuilder.processSpec();
                    predicateData = ComponentPQLHelper.create().getPredicates(spec, pql, componentGroup);

                }

                Map<Long, PolicyDevelopmentEntity> componentIdMap = new HashMap<>();
                for (Long refId : predicateData.getReferenceIds()) {
                    if (componentIdMap.containsKey(refId)) {
                        continue;
                    }
                    PolicyDevelopmentEntity policyDevelopmentEntity = policyDevelopmentEntityDao.findById(refId);
                    if (policyDevelopmentEntity != null &&
                            (policyDevelopmentEntity.getHidden() == null || !policyDevelopmentEntity.getHidden())
                            && !DELETED.getKey().equals(policyDevelopmentEntity.getStatus())) {
                        componentIdMap.put(policyDevelopmentEntity.getId(), policyDevelopmentEntity);
                    }
                }

                ComponentLite componentLite = ComponentLite.getLite(devEntity, componentGroup, modelTypeId, modelType, componentIdMap, predicateData,
                        appUserSearchRepository);
                PolicyDeploymentEntity depEntity =
                        policyDeployementMgmtService.findLastActiveRecordWithRevisionCount(devEntity.getId(), true);
                if (depEntity != null) {
                    componentLite.setActionType(depEntity.getActionType());
                    componentLite.setRevisionCount(depEntity.getRevisionCount());
                }
                PolicyDeploymentEntity pendingDeploymentEntity = policyDeployementMgmtService
                        .findLastActiveRecordWithRevisionCount(devEntity.getId(), false);
                if (pendingDeploymentEntity != null) {
                    PolicyDeploymentRecord deploymentRecord = policyDeploymentRecordDao
                            .findById(pendingDeploymentEntity.getDepRecordId());
                    if (deploymentRecord != null && deploymentRecord.getAsOf() != null) {
                        componentLite.setDeploymentTime(deploymentRecord.getAsOf());
                    }
                }

                if (preCreated) {
                    componentLite.setPreCreated(true);
                }
                componentLiteList.add(componentLite);
                // Link and update parent components of my change
                for(ComponentLite parentComponentLite : componentSearchRepository
                                .findBySubComponentsId(componentLite.getId(),
                                                PageRequest.of(0, 50)).getContent()) {
                    IncludedComponentLite includedComp = new IncludedComponentLite(parentComponentLite.getId(),
                                    parentComponentLite.getFullName());
                    includedComp.setDeployed(parentComponentLite.isDeployed());
                    includedComp.setStatus(parentComponentLite.getStatus());
                    includedComp.setPolicyModel(parentComponentLite.getModelType());
                    includedComp.setLastUpdatedDate(parentComponentLite.getLastUpdatedDate());
                    includedComp.setHasIncludedIn(parentComponentLite.isHasIncludedIn());
                    includedComp.setHasSubComponents(parentComponentLite.isHasSubComponents());
                    componentLite.getIncludedInComponents().add(includedComp);
                    componentLite.setHasIncludedIn(true);

                    for(SubComponentLite subComponentLite : parentComponentLite.getSubComponents()) {
                        if(subComponentLite.getId().equals(componentLite.getId())) {
                            subComponentLite.setName(componentLite.getName());
                            subComponentLite.setComponentFullName(componentLite.getFullName());
                            subComponentLite.setDeployed(componentLite.isDeployed());
                            subComponentLite.setStatus(componentLite.getStatus());
                            subComponentLite.setAuthorities(componentLite.getAuthorities());
                            subComponentLite.setHasIncludedIn(componentLite.isHasIncludedIn());
                            subComponentLite.setHasSubComponents(componentLite.isHasSubComponents());
                            break;
                        }
                    }

                    componentLiteList.add(parentComponentLite);
                }

                Set<Long> processedChildId = new HashSet<>();
                // Unlink or update child components of my change
                for(ComponentLite childComponentLite : componentSearchRepository
                                .findByIncludedInComponentsId(componentLite.getId(),
                                                PageRequest.of(0, 50)).getContent()) {
                    boolean existInMySubComponentList = false;
                    processedChildId.add(childComponentLite.getId());
                    for(SubComponentLite subComponentLite : componentLite.getSubComponents()) {
                        if(subComponentLite.getId().equals(childComponentLite.getId())) {
                            existInMySubComponentList = true;
                            break;
                        }
                    }

                    IncludedComponentLite includedComponentLite = null;
                    for(int i = 0; i< childComponentLite.getIncludedInComponents().size(); i++) {
                        includedComponentLite = childComponentLite.getIncludedInComponents().get(i);
                        if(includedComponentLite.getId().equals(componentLite.getId())) {
                            break;
                        }
                    }

                    if (existInMySubComponentList) {
                        includedComponentLite.setComponentFullName(componentLite.getFullName());
                        includedComponentLite.setName(componentLite.getName());
                        includedComponentLite.setDeployed(componentLite.isDeployed());
                        includedComponentLite.setHasIncludedIn(componentLite.isHasIncludedIn());
                        includedComponentLite.setHasSubComponents(componentLite.isHasSubComponents());
                        includedComponentLite.setStatus(componentLite.getStatus());
                        includedComponentLite.setAuthorities(componentLite.getAuthorities());
                    } else {
                        childComponentLite.getIncludedInComponents().remove(includedComponentLite);
                        childComponentLite.setHasIncludedIn(!childComponentLite.getIncludedInComponents().isEmpty());
                    }
                    componentLiteList.add(childComponentLite);
                }

                // Link newly added child components of my change
                for(SubComponentLite subComponentLite : componentLite.getSubComponents()) {
                    if(!processedChildId.contains(subComponentLite.getId())) {
                        Optional<ComponentLite> childComponentList = componentSearchRepository.findById(subComponentLite.getId());

                        if(childComponentList.isPresent()) {
                            IncludedComponentLite includedInComp = new IncludedComponentLite(componentLite.getId(),
                                            componentLite.getFullName());
                            includedInComp.setDeployed(componentLite.isDeployed());
                            includedInComp.setStatus(componentLite.getStatus());
                            includedInComp.setPolicyModel(componentLite.getModelType());
                            includedInComp.setLastUpdatedDate(componentLite.getLastUpdatedDate());
                            includedInComp.setHasIncludedIn(componentLite.isHasIncludedIn());
                            includedInComp.setHasSubComponents(componentLite.isHasSubComponents());

                            ComponentLite childComponent = childComponentList.get();
                            childComponent.getIncludedInComponents().add(includedInComp);
                            childComponent.setHasIncludedIn(true);

                            componentLiteList.add(childComponent);
                        }

                        processedChildId.add(subComponentLite.getId());
                    }
                }

                componentSearchRepository.saveAll(componentLiteList);
            }
        } catch (Exception e) {
            throw new ConsoleException("Error encountered in re-indexing components", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reIndexComponentsByFolder(Long folderId) throws ConsoleException {
        List<ComponentDTO> componentDTOS = new ArrayList<>();
        for(PolicyDevelopmentEntity policyDevelopmentEntity : policyDevelopmentEntityRepository.findByFolderIdAndType(folderId, DevEntityType.POLICY.getKey()))
        {
            componentDTOS.add(componentMgmtService.findById(policyDevelopmentEntity.getId()));
        }
        reIndexComponents(componentDTOS.toArray(new ComponentDTO[0]));
    }

}
