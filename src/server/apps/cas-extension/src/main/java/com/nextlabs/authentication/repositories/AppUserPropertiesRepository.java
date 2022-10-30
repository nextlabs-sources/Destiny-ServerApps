package com.nextlabs.authentication.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.authentication.models.AppUserProperties;

import javax.transaction.Transactional;

/**
 * Repository interface for application user properties.
 *
 * @author Sachindra Dasun
 */
@Repository
public interface AppUserPropertiesRepository extends CrudRepository<AppUserProperties, Long> {

    Optional<AppUserProperties> findByUserIdAndPropKey(Long userId, String propKey);

    @Transactional
    List<AppUserProperties> removeByUserId(Long userId);
}
