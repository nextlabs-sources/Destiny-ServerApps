package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyModelRepository
        extends JpaRepository<PolicyModel, Long> {

    List<PolicyModel> findByShortNameAndTypeAndStatus(String shortName, PolicyModelType type, Status status);

    List<PolicyModel> findByTypeAndStatusAndIdNotIn(PolicyModelType type, Status status, List<Long> ids);

}
