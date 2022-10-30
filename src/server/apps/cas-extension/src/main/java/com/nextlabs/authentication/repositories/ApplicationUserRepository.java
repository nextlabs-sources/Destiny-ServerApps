package com.nextlabs.authentication.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.models.ApplicationUser;

/**
 * Repository interface for application user.
 *
 * @author Sachindra Dasun
 */
@Repository
public interface ApplicationUserRepository extends CrudRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> findByEmailAndStatus(String email, UserStatus status);

    Optional<ApplicationUser> findByUsernameIgnoreCaseAndStatus(String username, UserStatus status);

    Optional<ApplicationUser> findByUsernameIgnoreCaseAndUserTypeAndStatus(String username, String userType, UserStatus status);

}
