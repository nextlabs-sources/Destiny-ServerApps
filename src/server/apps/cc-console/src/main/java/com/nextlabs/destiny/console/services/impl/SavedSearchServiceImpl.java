/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 5, 2016
 *
 */
package com.nextlabs.destiny.console.services.impl;

import javax.annotation.Resource;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.policy.SavedSearchDao;
import com.nextlabs.destiny.console.enums.SavedSearchType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.model.SavedSearch;
import com.nextlabs.destiny.console.search.repositories.SavedSearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.SavedSearchService;

/**
 *
 * Saved Search service implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class SavedSearchServiceImpl implements SavedSearchService {

    private static final Logger log = LoggerFactory
            .getLogger(SavedSearchServiceImpl.class);

    @Autowired
    private SavedSearchDao savedSearchDao;

    @Autowired
    private MessageBundleService msgBundle;

    @Resource
    private SavedSearchRepository savedSearchRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SavedSearch saveCriteria(SavedSearch criteria) 
			throws ConsoleException {
    	
    	checkCriteriaNameIsUnique(criteria.getName(), criteria.getType());
    	
		if (criteria.getId() == null) {
			savedSearchDao.create(criteria);
		} else {
			savedSearchDao.update(criteria);
		}
		savedSearchRepository.save(criteria);
		log.debug("Saved search criteria saved successfully, [ Id: {}]",
				criteria.getId());

		return criteria;
	}

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public SavedSearch findById(Long id) throws ConsoleException {
        try {
            SavedSearch criteria = savedSearchDao.findById(id);

            if (criteria == null) {
                log.info("saved search criteria not found for id :{}", id);
            }

            return criteria;
        } catch (Exception e) {
            throw new ConsoleException(String.format(
                    "Error encountered while searching for saved search criteria by [ Id : %s ]",
                    id), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeCriteria(Long id) throws ConsoleException {

        SavedSearch criteria = savedSearchDao.findById(id);
        if (criteria != null) {
            try {
                savedSearchDao.delete(criteria);
                savedSearchRepository.deleteById(criteria.getId());
            } catch (Exception e) {
                throw new ConsoleException(String.format(
                        "Error encountered while removing a saved search criteria [ Id : %s ]",
                        id), e);
            }
        } else {
            throw new NoDataFoundException(
                    msgBundle.getText("no.entity.found.delete.code"),
                    msgBundle.getText("no.entity.found.delete",
                            "Saved Search Criteria"));
        }

        log.info("Saved search criteria deleted successfully. [ Id:{}]", id);
    }

    @Override
    public Page<SavedSearch> findByNameOrDescriptionAndType(String searchText,
            SavedSearchType type, Pageable pageable) throws ConsoleException {
        try {

            log.debug(
                    "Find By Name or Description and Type, [Search Text :{}, type :{}]",
                    searchText, type);

            BoolQueryBuilder query = QueryBuilders.boolQuery();

            if (StringUtils.isNotEmpty(searchText)) {
                query.must(QueryBuilders
                        .multiMatchQuery(searchText, "name", "desc")
                        .type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX));
            }
            query.must(QueryBuilders.matchQuery("type", type.name()));

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable).build();

            log.debug("Search query :{}", query);

            return savedSearchRepository.search(searchQuery);

        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a saved search by given text",
                    e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reIndexAllCriteria() throws ConsoleException {
        try {
            List<SavedSearch> savedSearches = savedSearchDao.findAll();

            if (!savedSearches.isEmpty()) {
                savedSearchRepository.deleteAll();

                for (SavedSearch savedSearch : savedSearches) {
                    savedSearchRepository.save(savedSearch);
                }

                log.info(
                        "Saved search re-indexing successfull, No of re-indexes :{}",
                        savedSearches.size());
            }

        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while re-indexing saved search ", e);
        }

    }
    
    private void checkCriteriaNameIsUnique(String criteriaName, SavedSearchType criteriaType) {
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
        BoolQueryBuilder filter = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("lowercase_name", criteriaName.toLowerCase()))
                .must(QueryBuilders.termQuery("type", criteriaType.name()));

        Query searchQuery = new NativeSearchQueryBuilder().withQuery(query).withFilter(filter)
                .withPageable(PageRequest.of(0, 1)).build();

        Page<SavedSearch> savedSearchPage = savedSearchRepository.search(searchQuery);

        List<SavedSearch> savedSearches = savedSearchPage.getContent();
        if (!savedSearches.isEmpty()) {
            throw new NotUniqueException(msgBundle.getText("server.error.not.unique.code"),
                    msgBundle.getText("server.error.search.criteria.name.not.unique", criteriaName));
        }

	}

}
