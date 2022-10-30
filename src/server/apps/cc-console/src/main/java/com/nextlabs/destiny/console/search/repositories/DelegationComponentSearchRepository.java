/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 25, 2016
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.dto.delegadmin.DelegationComponentLite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Delegate component search repository
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface DelegationComponentSearchRepository
        extends ElasticsearchRepository<DelegationComponentLite, Long> {

    /**
     * Find delegation components by group type
     * 
     * @param group
     * @param pageable
     * @return
     */
    Page<DelegationComponentLite> findByGroup(String group, Pageable pageable);

    /**
     * Find delegation components by name
     * 
     * @param name
     * @param pageable
     * @return
     */
    Page<DelegationComponentLite> findByName(String name, Pageable pageable);

    /**
     * Find delegation components by group type and model type
     * 
     * @param group
     * @param modelType
     * @param pageable
     * @return
     */
    Page<DelegationComponentLite> findByGroupAndModelType(String group,
            String modelType, Pageable pageable);
}
