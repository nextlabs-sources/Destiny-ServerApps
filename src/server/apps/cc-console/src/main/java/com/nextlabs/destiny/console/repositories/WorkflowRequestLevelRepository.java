package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.enums.WorkflowRequestLevelStatus;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Repository
public interface WorkflowRequestLevelRepository extends JpaRepository<WorkflowRequestLevel, Long> {

    @Query("FROM WorkflowRequestLevel WHERE status in (:statuses) and entityWorkflowRequestId = :entityWorkflowRequestId")
    WorkflowRequestLevel findByEntityWorkflowRequestIdAndStatusIn(Long entityWorkflowRequestId, WorkflowRequestLevelStatus ... statuses);

    WorkflowRequestLevel findByEntityWorkflowRequestId(Long entityWorkflowRequestId);

}
