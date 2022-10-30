package com.nextlabs.authentication.handlers.gauth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import org.apereo.cas.authentication.OneTimeTokenAccount;
import org.apereo.cas.gauth.credential.BaseGoogleAuthenticatorTokenCredentialRepository;
import org.apereo.cas.gauth.credential.GoogleAuthenticatorAccount;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.authentication.models.gauth.GoogleAuthAccount;
import com.nextlabs.authentication.services.GoogleAuthService;

/**
 * Mange Google Authenticator accounts.
 *
 * @author Sachindra Dasun
 */
@Transactional
public class GoogleAuthOneTimeTokenCredentialRepository extends BaseGoogleAuthenticatorTokenCredentialRepository {

    private final IGoogleAuthenticator authenticator;

    @Autowired
    private GoogleAuthService googleAuthService;

    public GoogleAuthOneTimeTokenCredentialRepository(
                    CipherExecutor<String, String> tokenCredentialCipher,
                                                      IGoogleAuthenticator authenticator) {
        super(tokenCredentialCipher, authenticator);
        this.authenticator = authenticator;
    }

    @Override
    public OneTimeTokenAccount get(long id) {
        GoogleAuthAccount account = googleAuthService.findById(id).orElse(null);
        return account == null ? null : decode(account.getOneTimeTokenAccount());
    }

    @Override
    public OneTimeTokenAccount get(String username, long id) {
        GoogleAuthAccount account = googleAuthService.findByIdAndUsername(id, username).orElse(null);
        return account == null ? null : decode(account.getOneTimeTokenAccount());
    }

    @Override
    public Collection<? extends OneTimeTokenAccount> get(String username) {
        GoogleAuthAccount account = googleAuthService.findAccount(username).orElse(null);
        List<GoogleAuthenticatorAccount> accountList = new ArrayList<>();

        if(account != null) {
            accountList.add(account.getGoogleAuthenticatorAccount());
        }

        return decode(accountList);
    }

    @Override
    public Collection<? extends OneTimeTokenAccount> load() {
        return decode(googleAuthService.findAll().stream()
                .map(account -> account.getGoogleAuthenticatorAccount())
                .collect(Collectors.toList()));
    }

    @Override
    public OneTimeTokenAccount save(OneTimeTokenAccount account) {
        return update(account);
    }

    @Override
    public OneTimeTokenAccount create(String username) {
        GoogleAuthenticatorKey key = this.authenticator.createCredentials();
        OneTimeTokenAccount account =  new OneTimeTokenAccount();
        account.setUsername(username);
        account.setSecretKey(key.getKey());
        account.setValidationCode(key.getVerificationCode());
        account.setName("no_name");
        account.setScratchCodes(key.getScratchCodes());

        return account;
    }

    @Override
    public OneTimeTokenAccount update(OneTimeTokenAccount account) {
        OneTimeTokenAccount encodedAccount = encode(account.clone());
        GoogleAuthAccount googleAuthAccount = googleAuthService.findAccount(encodedAccount.getUsername())
                .orElse(new GoogleAuthAccount(encodedAccount));
        googleAuthAccount.setValidationCode(encodedAccount.getValidationCode());
        googleAuthAccount.setAccountName(account.getName());
        googleAuthAccount.setScratchCodes(encodedAccount.getScratchCodes());
        googleAuthAccount.setSecretKey(encodedAccount.getSecretKey());
        googleAuthService.save(googleAuthAccount);
        return encodedAccount;
    }

    @Override
    public void deleteAll() {
        googleAuthService.deleteAllAccounts();
    }

    @Override
    public void delete(String username) {
        googleAuthService.deleteAccountByUsername(username);
    }

    @Override
    public long count() {
        return googleAuthService.countAccounts();
    }

    @Override public long count(String username) {
        return googleAuthService.countAccounts(username);
    }

}
