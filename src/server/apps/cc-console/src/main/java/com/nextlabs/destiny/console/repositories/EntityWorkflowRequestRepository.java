package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.model.policyworkflow.EntityWorkflowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Repository
public interface EntityWorkflowRequestRepository extends JpaRepository<EntityWorkflowRequest, Long> {

    @Query("FROM EntityWorkflowRequest WHERE status in (com.nextlabs.destiny.console.enums.EntityWorkflowRequestStatus.PENDING, " +
            "com.nextlabs.destiny.console.enums.EntityWorkflowRequestStatus.APPROVED) and developmentId = :developmentId")
    EntityWorkflowRequest findActiveEntityWorkflowRequest(Long developmentId);
}
