/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.dao.policy;

import java.util.List;

import com.nextlabs.destiny.console.dao.GenericDao;
import com.nextlabs.destiny.console.enums.SavedSearchType;
import com.nextlabs.destiny.console.model.SavedSearch;

/**
 *
 * DAO interface for saved search criteria
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface SavedSearchDao extends GenericDao<SavedSearch, Long> {

    /**
     * Find the Saved Search Criterias by name or starts with characters
     * 
     * @param nameStartswith
     * @return List of {@link SavedSearch}
     */
    List<SavedSearch> findByStartwith(String nameStartswith,
            SavedSearchType type);

}
