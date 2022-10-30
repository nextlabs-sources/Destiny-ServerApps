package com.nextlabs.authentication.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.authentication.models.gauth.GoogleAuthAccount;
import com.nextlabs.authentication.models.gauth.GoogleAuthToken;
import com.nextlabs.authentication.repositories.gauth.GoogleAuthAccountRepository;
import com.nextlabs.authentication.repositories.gauth.GoogleAuthTokenRepository;
import com.nextlabs.authentication.services.GoogleAuthService;

/**
 * Google authenticator service implementation.
 *
 * @author Sachindra Dasun
 */
@Service
public class GoogleAuthServiceImpl implements GoogleAuthService {

    @Autowired
    private GoogleAuthAccountRepository googleAuthAccountRepository;

    @Autowired
    private GoogleAuthTokenRepository googleAuthTokenRepository;

    @Override
    public Optional<GoogleAuthAccount> findAccount(String username) {
        return googleAuthAccountRepository.findByUsernameIgnoreCase(username);
    }

    @Override
    public Optional<GoogleAuthAccount> findById(long id) {
        return googleAuthAccountRepository.findById(id);
    }

    @Override
    public
    Optional<GoogleAuthAccount> findByIdAndUsername(long id, String username) {
        return googleAuthAccountRepository.findByIdAndUsernameIgnoreCase(id, username);
    }

    @Override
    public void save(GoogleAuthAccount googleAuthAccount) {
        googleAuthAccountRepository.save(googleAuthAccount);
    }

    @Override
    public void deleteAllAccounts() {
        googleAuthAccountRepository.deleteAll();
    }

    @Override
    public Optional<GoogleAuthToken> findToken(String username, int token) {
        return googleAuthTokenRepository.findByUsernameIgnoreCaseAndToken(username, token);
    }

    @Override
    public void deleteToken(String username, int token) {
        googleAuthTokenRepository.deleteByUsernameIgnoreCaseAndToken(username, token);
    }

    @Override
    public void deleteToken(String username) {
        googleAuthTokenRepository.deleteByUsernameIgnoreCase(username);
    }

    @Override
    public void deleteToken(int token) {
        googleAuthTokenRepository.deleteByToken(token);
    }

    @Override
    public void deleteAllTokens() {
        googleAuthTokenRepository.deleteAll();
    }

    @Override
    public long countTokens(String username) {
        return googleAuthTokenRepository.countByUsernameIgnoreCase(username);
    }

    @Override
    public long countTokens() {
        return googleAuthTokenRepository.count();
    }

    @Override
    public int deleteExpiredTokens(LocalDateTime expireTime) {
        return googleAuthTokenRepository.deleteExpiredTokens(expireTime);
    }

    @Override
    public void save(GoogleAuthToken googleAuthToken) {
        googleAuthTokenRepository.save(googleAuthToken);
    }

    @Override
    public List<GoogleAuthAccount> findAll() {
        List<GoogleAuthAccount> googleAuthAccounts = new ArrayList<>();
        googleAuthAccountRepository.findAll().forEach(googleAuthAccounts::add);
        return googleAuthAccounts;
    }

    @Override
    public void deleteAccountByUsername(String username) {
        googleAuthAccountRepository.deleteByUsername(username);
    }

    @Override
    public long countAccounts() {
        return googleAuthAccountRepository.count();
    }

    @Override
    public long countAccounts(String username) {
        return googleAuthTokenRepository.countByUsernameIgnoreCase(username);
    }

}
