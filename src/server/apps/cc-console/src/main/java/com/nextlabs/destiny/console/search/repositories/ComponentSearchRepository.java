/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 5, 2016
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Component search repository
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface ComponentSearchRepository
        extends ElasticsearchRepository<ComponentLite, Long> {

    /**
     * Find components by model id, which is the id of policy model
     * 
     * @param modelId
     * @param pageable
     * @return
     */
    Page<ComponentLite> findByModelId(Long modelId, Pageable pageable);

    @Query("{ \"nested\": { \"path\": \"subComponents\", \"query\": { \"bool\": { \"must\": [ { \"match\": { \"subComponents.id\": ?0 } } ] } }, \"score_mode\": \"avg\" } }")
    Page<ComponentLite> findBySubComponentsId(Long subComponentId, Pageable pageable);

    @Query("{ \"nested\": { \"path\": \"includedInComponents\", \"query\": { \"bool\": { \"must\": [ { \"match\": { \"includedInComponents.id\": ?0 } } ] } }, \"score_mode\": \"avg\" } }")
    Page<ComponentLite> findByIncludedInComponentsId(Long includedInComponentId, Pageable pageable);
}
