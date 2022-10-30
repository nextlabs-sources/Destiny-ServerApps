package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.model.ProvisionedUserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvisionedUserGroupRepository extends JpaRepository<ProvisionedUserGroup, Long> {

    List<ProvisionedUserGroup> findByAuthHandlerId(Long authHandlerId);

    ProvisionedUserGroup findByAuthHandlerIdAndGroupId(Long authHandlerId, String externalId);
}
