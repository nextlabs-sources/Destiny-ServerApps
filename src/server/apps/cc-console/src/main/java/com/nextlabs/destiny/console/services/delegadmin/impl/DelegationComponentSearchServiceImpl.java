/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 25, 2016
 *
 */
package com.nextlabs.destiny.console.services.delegadmin.impl;

import static com.nextlabs.destiny.console.enums.DevEntityType.DELEGATION_COMPONENT;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.withSorts;

import javax.annotation.Resource;
import java.util.List;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.domain.destiny.common.IDSpec;
import com.nextlabs.destiny.console.dao.policy.PolicyDevelopmentEntityDao;
import com.nextlabs.destiny.console.dto.common.FacetResult;
import com.nextlabs.destiny.console.dto.common.FacetTerm;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.delegadmin.DelegationComponentLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.ComponentExtDescription;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.policy.visitors.PredicateData;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.DelegateModelSearchRepository;
import com.nextlabs.destiny.console.search.repositories.DelegationComponentSearchRepository;
import com.nextlabs.destiny.console.services.delegadmin.DelegationComponentSearchService;

/**
 *
 * Delegation component search service implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class DelegationComponentSearchServiceImpl
        implements DelegationComponentSearchService {

    private static final Logger log = LoggerFactory
            .getLogger(DelegateModelServiceImpl.class);

    @Resource
    private DelegationComponentSearchRepository delegComponentSearchRepository;

    @Resource
    private DelegateModelSearchRepository delegationModelSearchRepository;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;

    @Autowired
    private PolicyDevelopmentEntityDao policyDevelopmentEntityDao;

    @Override
    public Page<DelegationComponentLite> findByCriteria(SearchCriteria criteria)
            throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);
            PageRequest pageable = PageRequest.of(criteria.getPageNo(), criteria.getPageSize());

            BoolQueryBuilder query = buildQuery(criteria.getFields());

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable).build();

            searchQuery = withSorts(searchQuery, criteria.getSortFields());

            log.debug("Delegation Component search query :{},",
                    query.toString());
            Page<DelegationComponentLite> componentPage = delegComponentSearchRepository
                    .search(searchQuery);

            log.info("Delegation Component list page :{}, No of elements: {}",
                    componentPage, componentPage.getNumberOfElements());
            return componentPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find delegation components by given criteria",
                    e);
        }
    }

    @Override
    public Page<DelegationComponentLite> findComponentsByIds(List<Long> ids,
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

            log.debug("Delegation Component search by Ids query :{}, filter:{}",
                    query.toString(), filter.toString());

            Page<DelegationComponentLite> componentPage = delegComponentSearchRepository
                    .search(searchQuery);

            log.info(
                    "Delegation Component page by given ids :{}, No of elements: {}",
                    componentPage, componentPage.getNumberOfElements());
            return componentPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find delegation components for given ids",
                    e);
        }
    }

    @Override
    public FacetResult findFacetByCriteria(SearchCriteria criteria)
            throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);

            BoolQueryBuilder query = buildQuery(criteria.getFields());

            String facetName = criteria.getFacetField();

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query)
                    .addAggregation(
                            AggregationBuilders.terms(facetName)
                                    .field(facetName)
                                    .order(BucketOrder.key(true)))
                    .build();

            log.debug("Delegation Component facet search query :{},",
                    query.toString());
            AggregatedPage<DelegationComponentLite> aggregatedPage =
                    (AggregatedPage<DelegationComponentLite>) delegComponentSearchRepository
                            .search(searchQuery);

            StringTerms terms = (StringTerms) aggregatedPage.getAggregation(facetName);
            FacetResult facetResult = new FacetResult(facetName);

            for (StringTerms.Bucket bucket : terms.getBuckets()) {
                facetResult.getTerms()
                        .add(FacetTerm.create(bucket.getKeyAsString(), Math.toIntExact(bucket.getDocCount())));
            }

            log.info(
                    "Delegation Component facet search query :[ Facet :{}, No of terms :{}]",
                    facetName, facetResult.getTerms().size());
            return facetResult;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find delegation components by given criteria",
                    e);
        }
    }

    @Override
    public Page<DelegationComponentLite> findComponentsByGroupAndType(
            String groupType, String modelType, Pageable pageable)
            throws ConsoleException {
        try {
            log.debug(
                    "Find delegation component [ Group type: {}, Model type :{}]",
                    groupType, modelType);
            Page<DelegationComponentLite> componentPage = null;
            if (StringUtils.isEmpty(modelType)) {
                componentPage = delegComponentSearchRepository
                        .findByGroup(groupType, pageable);
            } else {
                componentPage = delegComponentSearchRepository
                        .findByGroupAndModelType(groupType, modelType,
                                pageable);
            }

            log.info(
                    "Delegation Component by [ Group type: {}, Model type :{}, No of items :{}]",
                    groupType, modelType, componentPage.getContent().size());
            return componentPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find delegation components by group",
                    e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reIndexAllComponents() throws ConsoleException {
        long id = 0l;
        try {
			 delegComponentSearchRepository.deleteAll();
			 
            long startTime = System.currentTimeMillis();
            List<PolicyDevelopmentEntity> devEntities = policyDevelopmentEntityDao
                    .findActiveRecordsByType(DELEGATION_COMPONENT.getKey());

            for (PolicyDevelopmentEntity devEntity : devEntities) {
            	id = devEntity.getId();
            	reIndexComponent(devEntity);
            }
            long endTime = System.currentTimeMillis();
            log.info(
                    "Delegation Component re-indexing successfull, No of re-indexes :{}, Time taken:{}ms",
                    devEntities.size(), (endTime - startTime));
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in re-indexing delegation components, [ Component Id :"
                            + id + "] ",
                    e);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reIndexComponent(Long id)
    	throws ConsoleException {
    	try {
    		reIndexComponent(policyDevelopmentEntityDao.findById(id));
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in re-indexing delegation components, [ Component Id :"
                            + id + "] ",
                    e);
        }
    }
    
    @Override
    public void reIndexComponent(PolicyDevelopmentEntity entity)
    	throws ConsoleException {
    	
    	if(entity != null) {
    		try {
                String title = entity.getTitle();
                String[] splits = title.split("/", -1);
                String componentGroup = splits[0];

                String extendedDesc = entity.getExtendedDescription();
                Long modelTypeId = 0L;
                String modelType = "";

                if (StringUtils.isNotEmpty(extendedDesc)) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ComponentExtDescription componentDesc = objectMapper
                            .readValue(extendedDesc,
                                    ComponentExtDescription.class);

                    DelegateModel model = delegationModelSearchRepository
                            .findById(componentDesc.getPolicyModelId()).orElse(null);
                    if (model != null) {
                        modelTypeId = model.getId();
                        modelType = model.getName();
                    }
                }

                String pql = entity.getPql();
                PredicateData predicateData = new PredicateData();
                if (StringUtils.isNotEmpty(pql)) {

                    DomainObjectBuilder domBuilder = new DomainObjectBuilder(
                            pql);
                    IDSpec spec = domBuilder.processSpec();
                    predicateData = ComponentPQLHelper.create()
                            .getPredicates(spec, pql, componentGroup);

                }
                DelegationComponentLite componentLite = DelegationComponentLite
                        .getLite(entity, componentGroup, modelTypeId,
                                modelType, predicateData,
                                appUserSearchRepository);

                delegComponentSearchRepository.save(componentLite);
    		} catch(Exception err) {
    			throw new ConsoleException(
                        "Error encountered in re-indexing delegation components, [ Component Id :"
                                + entity.getId() + "] ",
                        err);
    		}
    	}
    }
}
