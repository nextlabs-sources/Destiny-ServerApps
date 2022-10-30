/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 18, 2015
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.WorkflowRequestCommentLite;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Workflow request comments Search criteria repository
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
public interface WorkflowRequestCommentSearchRepository
        extends ElasticsearchRepository<WorkflowRequestCommentLite, Long> {

}
