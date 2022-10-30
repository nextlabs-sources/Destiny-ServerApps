/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.services.policyworkflow;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.WorkflowRequestCommentLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestComment;
import org.springframework.data.domain.Page;

/**
 *
 * Workflow request comment Search Service interface
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
public interface WorkflowRequestCommentSearchService {

    /**
     * Find remote environments
     *
     * @throws ConsoleException
     */
    Page<WorkflowRequestCommentLite> findWorkflowRequestCommentsByCriteria(SearchCriteria criteria) throws ConsoleException;
    /**
     * Re-Index all the policies
     *
     * @throws ConsoleException
     */
    void reIndexAllWorkflowRequestComments() throws ConsoleException;

    /**
     * @param workflowRequestComment
     * @throws ConsoleException 
     */
    void reIndexWorkflowRequestComment(WorkflowRequestComment workflowRequestComment) throws ConsoleException;

}
