/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 6, 2016
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.dto.common.HelpContent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Help content search repository
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface HelpContentSearchRepository
        extends ElasticsearchRepository<HelpContent, Long> {

}
