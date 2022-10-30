/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 22, 2016
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Delegate model search repository
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface DelegateModelSearchRepository
        extends ElasticsearchRepository<DelegateModel, Long> {

    /**
     * Find {@link DelegateModel} by name
     * 
     * @param name
     * @param pageable
     * @return
     */
    Page<DelegateModel> findByName(String name, Pageable pageable);

    /**
     * Find {@link DelegateModel} by model type
     * 
     * @param type
     * @param status
     * @param pageable
     * @return
     */
    Page<DelegateModel> findByTypeAndStatus(String type, Status status,
            Pageable pageable);

}
