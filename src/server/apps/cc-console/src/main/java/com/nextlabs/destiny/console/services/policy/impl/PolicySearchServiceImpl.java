/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.services.policy.impl;

import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildFolderFilterQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildTagFilterQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.withIdSort;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.withSorts;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.dao.policy.PolicyDeploymentRecordDao;
import com.nextlabs.destiny.console.dao.policy.PolicyDevelopmentEntityDao;
import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.FacetTerm;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.SubPolicyLite;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.delegadmin.AccessibleTags;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyDeploymentRecord;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.repositories.PolicyDevelopmentEntityRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.PolicySearchRepository;
import com.nextlabs.destiny.console.search.repositories.SavedSearchRepository;
import com.nextlabs.destiny.console.search.repositories.TagLabelSearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.policy.FolderService;
import com.nextlabs.destiny.console.services.policy.PolicyDeploymentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicySearchService;

/**
 *
 * Policy Search Criteria Service implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class PolicySearchServiceImpl implements PolicySearchService {

    private static final Logger log = LoggerFactory
            .getLogger(PolicySearchServiceImpl.class);

    @Autowired
    private PolicyDevelopmentEntityDao policyDevelopmentEntityDao;

    @Autowired
    private PolicyDeploymentRecordDao deploymentRecordDao;

    @Autowired
    private PolicyDeploymentEntityMgmtService policyDeployementMgmtService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private MessageBundleService msgBundle;

    @Autowired
    private RestHighLevelClient esRestHighLevelClient;

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @Autowired
    private FolderService folderService;

    @Autowired
    private PolicyMgmtService policyMgmtService;

    @Resource
    private SavedSearchRepository savedSearchRepository;

    @Resource
    private PolicySearchRepository policySearchRepository;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;

    @Resource
    private TagLabelSearchRepository tagLabelSearchRepository;

    @Autowired
    private PolicyDevelopmentEntityRepository policyDevelopmentEntityRepository;

    @Override
    public Page<PolicyLite> findPolicyByCriteria(SearchCriteria criteria)
            throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);
            Pageable pageable = PageRequest.of(criteria.getPageNo(), criteria.getPageSize());
            return findByCriteria(criteria, pageable);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policies by given criteria", e);
        }
    }

    @Override
    public Page<PolicyLite> findPolicyByIds(List<Long> ids,
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

            log.debug("Policy search by Ids query :{}, filter:{}",
                    query.toString(), filter.toString());

            Page<PolicyLite> policyListPage = policySearchRepository
                    .search(searchQuery);

            log.info("Policy page by given ids :{},  No of elements : {}",
                    policyListPage.getTotalPages(),
                    policyListPage.getNumberOfElements());
            return policyListPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policies for given ids", e);
        }
    }

    @Override
    public PolicyLite findPolicyTree(Long id) throws ConsoleException {
        PolicyLite policyLite = policySearchRepository.findById(id).orElse(null);
        if (policyLite == null) {
            return null;
        }
        PolicyLite policyLiteRoot = findPolicyTree(policyLite.getRootFolder());
        policyLiteRoot.setTags(policyLite.getTags());
        policyLiteRoot.setFolderId(policyLite.getFolderId());
        return policyLiteRoot;
    }

    @Override
    public PolicyLite findPolicyTree(String rootFolder)
            throws ConsoleException {
        try {

            long startTime = System.currentTimeMillis();
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            query.must(QueryBuilders.matchAllQuery());

            QueryBuilder filters = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("rootFolder", rootFolder.toLowerCase()))
                    .must(QueryBuilders.termQuery("hasParent", FALSE));

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withFilter(filters)
                    .withPageable(PageRequest.of(0, 10000)).build();
            searchQuery = withIdSort(searchQuery,
                    new SortField("id", SortField.ASC));

            log.debug(
                    "Search immediate parents by root folder query :{}, filter:{}",
                    query.toString(), filters.toString());

            Page<PolicyLite> parentPolicyListPage = policySearchRepository
                    .search(searchQuery);

            log.debug(
                    "Immediate parent policies for root folder :{},  No of elements : {}",
                    parentPolicyListPage,
                    parentPolicyListPage.getNumberOfElements());

            PolicyLite root = new PolicyLite();
            root.setName(rootFolder);
            for (PolicyLite rootLevelParent : parentPolicyListPage
                    .getContent()) {
                root.getChildNodes().add(rootLevelParent);
                rootLevelParent = accessControlService
                        .enforceTBAConPolicy(rootLevelParent);
                childNodesWalk(rootLevelParent);
            }
            long totalTimeTaken = System.currentTimeMillis() - startTime;
            log.info(
                    "Policy full tree population successfull, [ Root folder : {},"
                            + " No of Root Level Parents: {}, Time taken for processing with TBAC : {}ms],",
                    rootFolder, parentPolicyListPage.getNumberOfElements(),
                    totalTimeTaken);
            return root;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policy hierarchy for root", e);
        }
    }

    @Override
    public List<PolicyLite> findSubPolicy(Long parentPolicyId)
            throws ConsoleException {
        try {

            long startTime = System.currentTimeMillis();
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            query.must(QueryBuilders.matchAllQuery());

            QueryBuilder filters = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("parentPolicy.id", parentPolicyId))
                    .must(QueryBuilders.termQuery("hasParent", TRUE));

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withFilter(filters)
                    .withPageable(PageRequest.of(0, 10000)).build();
            searchQuery = withIdSort(searchQuery,
                    new SortField("id", SortField.ASC));

            log.debug("Search immediate sub policies query :{}, filter:{}", query, filters);

            Page<PolicyLite> subPolicyListPage = policySearchRepository
                    .search(searchQuery);

            log.debug(
                    "Immediate sub policies for policy :{},  No of elements : {}",
                    parentPolicyId,
                    subPolicyListPage.getNumberOfElements());
            long totalTimeTaken = System.currentTimeMillis() - startTime;
            log.info(
                    "Retrieved immediate sub policies, [ policy : {},"
                            + " No of sub policies: {}, Time taken for processing with TBAC : {}ms],",
                    parentPolicyId, subPolicyListPage.getNumberOfElements(),
                    totalTimeTaken);
            return subPolicyListPage.getContent();
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find sub policy", e);
        }
    }

    /**
     * Recursively walk through all the nodes in a given parent
     * 
     * @param immediateParent
     *            parent node
     * @return {@link PolicyLite}
     */
    private PolicyLite childNodesWalk(PolicyLite immediateParent) {
        for (SubPolicyLite subPolicy : immediateParent.getSubPolicies()) {
            policySearchRepository.findById(subPolicy.getId()).ifPresent(parent -> {
                accessControlService.enforceTBAConPolicy(parent);
                immediateParent.getChildNodes().add(parent);
                childNodesWalk(parent);
            });
        }
        return immediateParent;
    }

    @Override
    public FacetResult findFacetByCriteria(SearchCriteria criteria)
            throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);

            String facetName = criteria.getFacetField();
            BoolQueryBuilder query = buildQuery(criteria.getFields());
            NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder()
                    .withQuery(query);
            appendAccessControlTags(nativeQuery);

            Query searchQuery = nativeQuery.addAggregation(
                    AggregationBuilders
                            .terms(facetName)
                            .field(facetName)
                            .order(BucketOrder.key(true)))
                    .build();

            log.debug("Policy facet search query :{},", query.toString());
            AggregatedPage<PolicyLite> aggregatedPage = (AggregatedPage<PolicyLite>) policySearchRepository
                    .search(searchQuery);

            StringTerms terms = (StringTerms) aggregatedPage.getAggregation(facetName);
            FacetResult facetResult = new FacetResult(facetName);

            for (StringTerms.Bucket bucket : terms.getBuckets()) {
                facetResult.getTerms()
                        .add(FacetTerm.create(bucket.getKeyAsString(), Math.toIntExact(bucket.getDocCount())));
            }

            log.info("Policy facet search query :[ Facet :{}, No of terms :{}]",
                    facetName, facetResult.getTerms().size());
            return facetResult;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policy by given criteria", e);
        }
    }

    @Override
    public FacetResult aggregatedPoliciesByTags(int dataSize)
            throws ConsoleException {
        try {
            log.debug("Aggredted policies by tags request for top {} data",
                    dataSize);
            FacetResult facetResult = getPoliciesByTags(dataSize);

            if (facetResult == null) {
                facetResult = new FacetResult("tags_aggregator");
            }

            // load all policies with no tags
            int noOfPoliciesWithNoTags = getNoOfPoliciesWithNoTags();
            facetResult.getTerms().add(FacetTerm.create(
                    msgBundle.getText("tag.no.tags"), noOfPoliciesWithNoTags));
            log.info("Policy aggregation by tags search :[ No of terms :{}]",
                    facetResult.getTerms().size());
            return facetResult;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in aggregate policy by tags", e);
        }
    }

    private FacetResult getPoliciesByTags(int dataSize) {
        try {
            MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
            NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder()
                    .withQuery(matchAllQuery);
            appendAccessControlTags(nativeQuery);

            AbstractAggregationBuilder aggregation = AggregationBuilders
                    .nested("tags_aggs", "tags")
                    .subAggregation(AggregationBuilders.terms("group")
                            .field("tags.id").size(dataSize));
            nativeQuery.addAggregation(aggregation);


            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                    .query(nativeQuery.build().getQuery())
                    .aggregation(aggregation);
            SearchRequest searchRequest = new SearchRequest()
                    .indices("policies")
                    .source(searchSourceBuilder);
            SearchResponse searchResponse = esRestHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();

            ParsedNested parsedNested = aggregations.get("tags_aggs");
            ParsedLongTerms terms = parsedNested.getAggregations().get("group");
            FacetResult facetResult = new FacetResult("tags_aggregator");
            if (terms != null) {
                for (Terms.Bucket bucket : terms.getBuckets()) {
                    String key = String.valueOf(bucket.getKey());
                    TagLabel tagLabel = tagLabelSearchRepository.findById(Long.valueOf(key)).orElse(null);
                    if(tagLabel == null) {
                        return null;
                    }
                    facetResult.getTerms().add(FacetTerm.create(
                            tagLabel.getLabel(), (int) bucket.getDocCount()));
                }
            }
            return facetResult;
        } catch (Exception e) {
            log.warn(
                    "No policies were found with tags to aggregate by policy tags",
                    e.getMessage());
            return null;
        }
    }

    private int getNoOfPoliciesWithNoTags() throws IOException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("noOfTags", 0));
        NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery);
        appendAccessControlTags(nativeQuery);

        AbstractAggregationBuilder noTagsAggregation = AggregationBuilders
                .count("with_no_tags_count").field("noOfTags");
        nativeQuery.addAggregation(noTagsAggregation);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(nativeQuery.build().getQuery())
                .aggregation(noTagsAggregation);
        SearchRequest searchRequest = new SearchRequest()
                .indices("policies")
                .source(searchSourceBuilder);
        SearchResponse searchResponse = esRestHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = searchResponse.getAggregations();

        ParsedValueCount valueCount = aggregations.get("with_no_tags_count");
        if (valueCount != null) {
            return (int) valueCount.getValue();
        }
        return 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reIndexAllPolicies() throws ConsoleException {
        try {
			policySearchRepository.deleteAll();
			
            List<PolicyDevelopmentEntity> devEntities = policyDevelopmentEntityDao
                    .findActiveRecordsByType(DevEntityType.POLICY.getKey());

			Map<String, PolicyDevelopmentEntity> policyNameIdMap = getPolicyNameToIdMap(devEntities);

            for (PolicyDevelopmentEntity devEntity : devEntities) {
                String pql = devEntity.getPql();
                DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);

                try {
                    IDPolicy policy = domBuilder.processPolicy();
                    devEntity.setPolicy(policy);
                } catch (Exception e) {
					log.error("Skip policy indexing due to error in process policy in dom builder, Policy Id : {}",
                            devEntity.getId(), e);
                    continue;
                }

				PolicyLite policyLite = PolicyLite.getLite(devEntity, policyNameIdMap, appUserSearchRepository);
                PolicyDeploymentEntity depEntity = policyDeployementMgmtService
                        .findLastActiveRecordWithRevisionCount(devEntity.getId(), true);
                if (depEntity != null) {
                    policyLite.setActionType(depEntity.getActionType());
                    policyLite.setRevisionCount(depEntity.getRevisionCount());
                }
                PolicyDeploymentEntity pendingDeploymentEntity = policyDeployementMgmtService
                        .findLastActiveRecordWithRevisionCount(devEntity.getId(), false);
                if (pendingDeploymentEntity != null) {
                    PolicyDeploymentRecord deploymentRecord = deploymentRecordDao
                            .findById(pendingDeploymentEntity.getDepRecordId());
                    if (deploymentRecord != null && deploymentRecord.getAsOf() != null) {
                        policyLite.setDeploymentTime(deploymentRecord.getAsOf());
                    }
                }
                policySearchRepository.save(policyLite);
            }

			log.info("Policy re-indexing successfull, No of re-indexes :{}", devEntities.size());
        } catch (Exception e) {
			throw new ConsoleException("Error encountered in re-indexing policies", e);
        }

    }

    private Map<String, PolicyDevelopmentEntity> getPolicyNameToIdMap(
            List<PolicyDevelopmentEntity> devEntities) {
        Map<String, PolicyDevelopmentEntity> policyNameIdMap = new HashMap<>();
        for (PolicyDevelopmentEntity devEntity : devEntities) {
            policyNameIdMap.put(devEntity.getTitle(), devEntity);
        }
        return policyNameIdMap;
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
        QueryBuilder tagsFilter = buildTagFilterQuery(user.getPolicyAccessibleTags());
        if (tagsFilter == null) {
            log.info("Policy access control tags not found or not applicable for user, [ user : {}]", user.getUsername());
            return;
        }
        log.debug("Access control filter :{}", tagsFilter.toString());

        AccessibleTags accessibleTags = user.getPolicyAccessibleTags();
        folderService.addIncludedSubFolderTags(accessibleTags);
        QueryBuilder foldersFilter = buildFolderFilterQuery(accessibleTags);
        if (foldersFilter == null) {
            log.info("Policy access control folders not found or not applicable for user, [ user : {}]", user.getUsername());
            return;
        }
        log.debug("Access control folder filter :{}", foldersFilter);
        nativeQuery.withFilter(QueryBuilders.boolQuery().must(tagsFilter).must(foldersFilter));
    }

    private Page<PolicyLite> findByCriteria(SearchCriteria criteria,
            Pageable pageable) throws ConsoleException {
        try {

            log.debug("Search Criteria :[{}]", criteria);

            List<SearchField> searchFields = criteria.getFields();
            BoolQueryBuilder query = buildQuery(searchFields);
            NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable);

           appendAccessControlTags(nativeQuery);

            Query searchQuery = withSorts(nativeQuery.build(),
                    criteria.getSortFields());

            log.debug("Policy search query :{},", query.toString());
            Page<PolicyLite> policyListPage = policySearchRepository
                    .search(searchQuery);

            log.info("Policy list page :{}, No of elements :{}",
                    policyListPage.getTotalPages(),
                    policyListPage.getNumberOfElements());
            return policyListPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policies by given criteria", e);
        }
    }

    /* (non-Javadoc)
     * @see com.nextlabs.destiny.console.services.policy.PolicySearchService#reIndexPolicies(com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO[])
     */
    @Override
    public void reIndexPolicies(PolicyDTO... policyDTOs) throws ConsoleException {
        List<PolicyDevelopmentEntity> devEntities = policyDevelopmentEntityDao
                .findActiveRecordsByType(DevEntityType.POLICY.getKey());
        Map<String, PolicyDevelopmentEntity> policyNameIdMap = getPolicyNameToIdMap(devEntities);
        
        try {
            for (PolicyDTO policyDTO : policyDTOs) {
                PolicyDevelopmentEntity devEntity = policyDevelopmentEntityDao.findById(policyDTO.getId());
                String devEntityType = devEntity.getType();
                
                if (!DevEntityType.POLICY.getKey().equalsIgnoreCase(devEntityType)){
                	//skip
                	continue;
                }
                String pql = devEntity.getPql();
                DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);

                try {
                    IDPolicy policy = domBuilder.processPolicy();
                    devEntity.setPolicy(policy);
                } catch (Exception e) {
                    log.error("Skip policy indexing due to error in process policy in dom builder, Policy Id : {}", devEntity.getId(), e);
                    continue;
                }

                PolicyLite policyLite = PolicyLite.getLite(devEntity, policyNameIdMap, appUserSearchRepository);
                PolicyDeploymentEntity depEntity =
                        policyDeployementMgmtService.findLastActiveRecordWithRevisionCount(devEntity.getId(), true);
                if (depEntity != null) {
                    policyLite.setActionType(depEntity.getActionType());
                    policyLite.setRevisionCount(depEntity.getRevisionCount());
                }
                PolicyDeploymentEntity pendingDeploymentEntity = policyDeployementMgmtService
                        .findLastActiveRecordWithRevisionCount(devEntity.getId(), false);
                if (pendingDeploymentEntity != null) {
                    PolicyDeploymentRecord deploymentRecord = deploymentRecordDao
                            .findById(pendingDeploymentEntity.getDepRecordId());
                    if (deploymentRecord != null && deploymentRecord.getAsOf() != null) {
                        policyLite.setDeploymentTime(deploymentRecord.getAsOf());
                    }
                }
                policySearchRepository.save(policyLite);
            }
        } catch (Exception e) {
            throw new ConsoleException("Error encountered in re-indexing policies", e);
        }

    }

    public void reIndexPoliciesByFolder(Long folderId) throws ConsoleException {
        List<PolicyDTO> policyDTOS = new ArrayList<>();
        for (PolicyDevelopmentEntity policyDevelopmentEntity : policyDevelopmentEntityRepository.findByFolderIdAndType(folderId, DevEntityType.POLICY.getKey())) {
            policyDTOS.add(policyMgmtService.findById(policyDevelopmentEntity.getId()));
        }
        reIndexPolicies(policyDTOS.toArray(new PolicyDTO[0]));
    }

}
