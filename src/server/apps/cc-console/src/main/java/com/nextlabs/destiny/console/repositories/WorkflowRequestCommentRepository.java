package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Repository
public interface WorkflowRequestCommentRepository extends JpaRepository<WorkflowRequestComment, Long> {

    @Query("SELECT C FROM WorkflowRequestComment C " +
            "INNER JOIN EntityWorkflowRequest R ON C.workflowRequestId = R.id " +
            "INNER JOIN PolicyDevelopmentEntity P ON R.developmentId = P.id " +
            "WHERE P.status <> 'DE'")
    Optional<List<WorkflowRequestComment>> findAllByActivePolicies();
}
