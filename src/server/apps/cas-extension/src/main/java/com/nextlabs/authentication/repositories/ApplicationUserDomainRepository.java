package com.nextlabs.authentication.repositories;

import com.nextlabs.authentication.models.ApplicationUserDomain;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for application user domain.
 *
 * @author Chok Shah Neng
 */
@Repository
public interface ApplicationUserDomainRepository extends CrudRepository<ApplicationUserDomain, Long> {

    ApplicationUserDomain findByNameIgnoreCase(String name);

}
