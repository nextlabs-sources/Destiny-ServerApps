package com.nextlabs.authentication.handlers.gauth;

import java.time.LocalDateTime;

import org.apereo.cas.authentication.OneTimeToken;
import org.apereo.cas.otp.repository.token.BaseOneTimeTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.authentication.models.gauth.GoogleAuthToken;
import com.nextlabs.authentication.services.GoogleAuthService;

/**
 * Mange Google Authenticator tokens.
 *
 * @author Sachindra Dasun
 */
@Transactional
public class GoogleAuthOneTimeTokenRepository extends BaseOneTimeTokenRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAuthOneTimeTokenRepository.class);

    private final long expireTokensInSeconds;

    @Autowired
    private GoogleAuthService googleAuthService;

    public GoogleAuthOneTimeTokenRepository(long expireTokensInSeconds) {
        this.expireTokensInSeconds = expireTokensInSeconds;
    }

    @Override
    public void cleanInternal() {
        int count = googleAuthService.deleteExpiredTokens(LocalDateTime
                .now()
                .minusSeconds(this.expireTokensInSeconds));
        LOGGER.debug("Deleted [{}] expired previously used token record(s)", count);
    }

    @Override
    public void store(OneTimeToken token) {
        googleAuthService.save(new GoogleAuthToken(token));
    }

    @Override
    public OneTimeToken get(String uid, Integer otp) {
        return googleAuthService.findToken(uid, otp)
                .map(GoogleAuthToken::getOneTimeToken)
                .orElse(null);
    }

    @Override
    public void remove(String uid, Integer otp) {
        googleAuthService.deleteToken(uid, otp);
    }

    @Override
    public void remove(String uid) {
        googleAuthService.deleteToken(uid);
    }

    @Override
    public void remove(Integer otp) {
        googleAuthService.deleteToken(otp);
    }

    @Override
    public void removeAll() {
        googleAuthService.deleteAllTokens();
    }

    @Override
    public long count(String uid) {
        return googleAuthService.countTokens(uid);
    }

    @Override
    public long count() {
        return googleAuthService.countTokens();
    }

}