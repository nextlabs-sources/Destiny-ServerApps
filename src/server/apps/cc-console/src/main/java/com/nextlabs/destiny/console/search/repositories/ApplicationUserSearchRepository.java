/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 5, 2016
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.model.ApplicationUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Application user search repository
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface ApplicationUserSearchRepository
        extends ElasticsearchRepository<ApplicationUser, Long> {

    Page<ApplicationUser> findByAuthHandlerId(Long authHandlerId, Pageable pageable);

}
