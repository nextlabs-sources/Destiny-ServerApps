package com.nextlabs.authentication.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.authentication.enums.PolicyModelStatus;
import com.nextlabs.authentication.enums.PolicyModelType;
import com.nextlabs.authentication.models.ActionConfig;

/**
 * Action config repository.
 *
 * @author Sachindra Dasun
 */
@Repository
public interface ActionConfigRepository extends JpaRepository<ActionConfig, Long> {

    List<ActionConfig> findByPolicyModelStatusAndPolicyModelTypeIs(PolicyModelStatus status,
                                                                   PolicyModelType policyModelType);

}
