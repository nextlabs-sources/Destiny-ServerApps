/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 14, 2016
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Policy model search repository
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface PolicyModelSearchRepository
        extends ElasticsearchRepository<PolicyModel, Long> {

    /**
     * Find Policy model by name and type
     * 
     * @param name
     * @param pageable
     * @return
     */
    Page<PolicyModel> findByNameAndType(String name, String type,
            Pageable pageable);

    /**
     * Find Policy models by model type
     * 
     * @param type
     * @param status
     * @param pageable
     * @return
     */
    Page<PolicyModel> findByTypeAndStatus(String type, Status status,
            Pageable pageable);
    
    /**
     * Find Policy models by model type and short code
     * @param type
     * @param shortName
     * @param status
     * @param pageable
     * @return
     */
    Page<PolicyModel> findByTypeAndShortName(String type, String shortName, 
    		Status status, Pageable pageable);
}
