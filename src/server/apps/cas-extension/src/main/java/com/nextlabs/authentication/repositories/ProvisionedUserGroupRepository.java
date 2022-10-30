package com.nextlabs.authentication.repositories;

import com.nextlabs.authentication.models.ProvisionedUserGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for provisioned user group.
 *
 * @author Chok Shah Neng
 */
@Repository
public interface ProvisionedUserGroupRepository extends CrudRepository<ProvisionedUserGroup, Long> {

    Optional<ProvisionedUserGroup> findByGroupIdIgnoreCase(String groupId);

}
