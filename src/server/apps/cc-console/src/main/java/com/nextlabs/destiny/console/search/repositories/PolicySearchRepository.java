/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 18, 2015
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Policy Search criteria repository
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface PolicySearchRepository
        extends ElasticsearchRepository<PolicyLite, Long> {

}
