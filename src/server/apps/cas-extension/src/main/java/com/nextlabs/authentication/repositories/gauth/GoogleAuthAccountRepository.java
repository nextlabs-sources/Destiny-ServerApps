package com.nextlabs.authentication.repositories.gauth;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.authentication.models.gauth.GoogleAuthAccount;

/**
 * Repository interface for Google Authenticator account.
 *
 * @author Sachindra Dasun
 */
@Repository
public interface GoogleAuthAccountRepository extends CrudRepository<GoogleAuthAccount, Long> {

    Optional<GoogleAuthAccount> findByUsernameIgnoreCase(String username);

    Optional<GoogleAuthAccount> findByIdAndUsernameIgnoreCase(long id, String username);

    void deleteByUsername(String username);

}
