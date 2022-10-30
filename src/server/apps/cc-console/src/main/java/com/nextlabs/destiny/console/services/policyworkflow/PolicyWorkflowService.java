package com.nextlabs.destiny.console.services.policyworkflow;

import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.WorkflowRequestCommentDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Policy workflow service interface
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
public interface PolicyWorkflowService {

    @Transactional
    void submitWorkflowNested(Long policyId) throws ConsoleException, ServerException;

    @Transactional
    void approveWorkflowRequestLevel(Long policyId) throws ConsoleException, ServerException;

    @Transactional
    void returnWorkflowRequestLevel(Long policyId) throws ConsoleException, ServerException;

    /**
     * Saves {@link WorkflowRequestCommentDTO}
     *
     * @param workflowRequestCommentDTO
     *            {@link WorkflowRequestCommentDTO}
     * @throws ConsoleException
     *             thrown on any error
     *
     */
    void addComment(WorkflowRequestCommentDTO workflowRequestCommentDTO) throws ConsoleException, ServerException;

}
