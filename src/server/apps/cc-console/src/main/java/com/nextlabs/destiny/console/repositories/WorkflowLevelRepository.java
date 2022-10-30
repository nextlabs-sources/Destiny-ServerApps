package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.model.policyworkflow.WorkflowLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Repository
public interface WorkflowLevelRepository extends JpaRepository<WorkflowLevel, Long> {

    List<WorkflowLevel> findAllByOrderByLevelOrderAsc();

}
