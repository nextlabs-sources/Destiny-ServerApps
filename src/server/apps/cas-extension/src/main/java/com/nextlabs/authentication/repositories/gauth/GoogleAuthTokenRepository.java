package com.nextlabs.authentication.repositories.gauth;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nextlabs.authentication.models.gauth.GoogleAuthToken;

/**
 * Repository interface for Google Authenticator token.
 *
 * @author Sachindra Dasun
 */
@Repository
public interface GoogleAuthTokenRepository extends CrudRepository<GoogleAuthToken, Long> {

    Optional<GoogleAuthToken> findByUsernameIgnoreCaseAndToken(String username, int token);

    @Transactional
    @Modifying
    @Query("delete from GoogleAuthToken t where t.issuedDateTime>= :expired")
    int deleteExpiredTokens(@Param("expired") LocalDateTime expired);

    void deleteByUsernameIgnoreCaseAndToken(String username, int token);

    void deleteByUsernameIgnoreCase(String username);

    void deleteByToken(int token);

    long countByUsernameIgnoreCase(String username);

}