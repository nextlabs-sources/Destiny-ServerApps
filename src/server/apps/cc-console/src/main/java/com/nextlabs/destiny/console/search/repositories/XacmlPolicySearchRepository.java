/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 18, 2015
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.dto.policymgmt.XacmlPolicyLite;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Xacml Policy Search criteria repository
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
public interface XacmlPolicySearchRepository
        extends ElasticsearchRepository<XacmlPolicyLite, Long> {

}
