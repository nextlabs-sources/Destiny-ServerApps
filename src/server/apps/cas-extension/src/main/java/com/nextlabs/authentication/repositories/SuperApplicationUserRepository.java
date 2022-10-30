package com.nextlabs.authentication.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.authentication.models.SuperApplicationUser;

/**
 * Repository interface for supper application user.
 *
 * @author Sachindra Dasun
 */
@Repository
public interface SuperApplicationUserRepository extends CrudRepository<SuperApplicationUser, Long> {

    Optional<SuperApplicationUser> findByEmail(String email);

    Optional<SuperApplicationUser> findByUsernameIgnoreCase(String username);

}
