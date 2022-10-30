/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 25, 2016
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.dto.delegadmin.DelegateRuleLite;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Delegate rule search repository
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface DelegationRuleSearchRepository
        extends ElasticsearchRepository<DelegateRuleLite, Long> {

}
