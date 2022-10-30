/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 18, 2015
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.model.SavedSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Saved Search criteria repository
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface SavedSearchRepository
        extends ElasticsearchRepository<SavedSearch, Long> {

}
