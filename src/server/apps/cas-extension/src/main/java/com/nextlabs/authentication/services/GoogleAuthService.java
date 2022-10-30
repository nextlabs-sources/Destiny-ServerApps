package com.nextlabs.authentication.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.nextlabs.authentication.models.gauth.GoogleAuthAccount;
import com.nextlabs.authentication.models.gauth.GoogleAuthToken;

/**
 * Google authenticator service interface.
 *
 * @author Sachindra Dasun
 */
public interface GoogleAuthService {

    Optional<GoogleAuthAccount> findAccount(String username);

    Optional<GoogleAuthAccount> findById(long id);

    Optional<GoogleAuthAccount> findByIdAndUsername(long id, String username);

    void save(GoogleAuthAccount googleAuthAccount);

    void deleteAllAccounts();

    Optional<GoogleAuthToken> findToken(String username, int token);

    void deleteToken(String username, int token);

    void deleteToken(String username);

    void deleteToken(int token);

    void deleteAllTokens();

    long countTokens(String username);

    long countTokens();

    int deleteExpiredTokens(LocalDateTime expireTime);

    void save(GoogleAuthToken googleAuthToken);

    List<GoogleAuthAccount> findAll();

    void deleteAccountByUsername(String username);

    long countAccounts();

    long countAccounts(String username);

}
