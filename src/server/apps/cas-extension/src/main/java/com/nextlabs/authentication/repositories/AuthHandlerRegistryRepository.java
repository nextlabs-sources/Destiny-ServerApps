package com.nextlabs.authentication.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.authentication.models.AuthHandlerRegistry;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for authentication handler registry.
 *
 * @author Sachindra Dasun
 */
@Repository
public interface AuthHandlerRegistryRepository extends CrudRepository<AuthHandlerRegistry, Long> {

    List<AuthHandlerRegistry> findByTypeIgnoreCase(String type);

}
