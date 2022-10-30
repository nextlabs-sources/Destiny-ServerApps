package com.nextlabs.authentication.handlers.authentication;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.models.ApplicationUser;
import com.nextlabs.authentication.models.SuperApplicationUser;
import com.nextlabs.authentication.repositories.ApplicationUserRepository;
import com.nextlabs.authentication.repositories.SuperApplicationUserRepository;
import com.nextlabs.authentication.services.PasswordManagementService;
import com.nextlabs.serverapps.common.config.SecurePasswordEncoderConfig;

/**
 * Perform authentication using database.
 *
 * @author Sachindra Dasun
 */

public final class InternalUserAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private SuperApplicationUserRepository superApplicationUserRepository;

    @Autowired
    private PasswordManagementService passwordManagementServiceImpl;

    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory
            .getLogger(InternalUserAuthenticationHandler.class);

    public InternalUserAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
                                             Integer order, DataSource dataSource) {
        super(name, servicesManager, principalFactory, order, dataSource);
    }

    @Override
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        super.setPasswordEncoder(passwordEncoder);
        this.passwordEncoder = passwordEncoder;
    }

	@SneakyThrows(GeneralSecurityException.class)
    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) {

        final UsernamePasswordCredential originalUserPass = (UsernamePasswordCredential) credential;
        final UsernamePasswordCredential userPass = new UsernamePasswordCredential(originalUserPass.getUsername(), originalUserPass.getPassword());
        if (StringUtils.isBlank(userPass.getUsername())) {
            throw new AccountNotFoundException("Username is null.");
        }
        if (StringUtils.isBlank(userPass.getPassword())) {
            throw new FailedLoginException("Password is null.");
        }
        validateUser(userPass);
        if (!SecurePasswordEncoderConfig.isPasswordPbkdf2Encoded(userPass.getPassword())) {
            passwordManagementServiceImpl.reHashPassword(originalUserPass.getUsername(), originalUserPass.getPassword());
        }
        // continue with rest of the flow
        return authenticateUsernamePasswordInternal(userPass, originalUserPass.getPassword());
    }

    protected final AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential,
                                                                                        String originalPassword) {
        return createHandlerResult(credential, this.principalFactory.createPrincipal(credential.getUsername()),
                new ArrayList<>());
    }

    private SuperApplicationUser fetchSuperUser(String username) throws AccountNotFoundException {
        return superApplicationUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new AccountNotFoundException(String.format("%s not found", username)));
    }

    private ApplicationUser fetchApplicationUser(String username) throws AccountNotFoundException {
        return applicationUserRepository.findByUsernameIgnoreCaseAndStatus(username, UserStatus.ACTIVE)
                .orElseThrow(() -> new AccountNotFoundException(String.format("%s not found", username)));
    }

    private String getDecodedPassword(byte[] password) {
        String decodedPassword = new String(password);
        if (SecurePasswordEncoderConfig.isPasswordPbkdf2Encoded(decodedPassword)) {
            return decodedPassword;
        }
        return DatatypeConverter.printHexBinary(password);
    }

    public void validateUser(UsernamePasswordCredential userPass)
            throws AccountNotFoundException, FailedLoginException, AccountLockedException, AccountPasswordMustChangeException {

        String dbPassword;
        boolean locked;
        String initLoginDone;
        try {
            SuperApplicationUser superUser = fetchSuperUser(userPass.getUsername());
            dbPassword = getDecodedPassword(superUser.getPassword());
            locked = superUser.getLocked();
            initLoginDone = superUser.getInitLoginDone();
        } catch (AccountNotFoundException anf){
            // throws AccountNotFoundException if username not found in both SuperApplicationUser and ApplicationUser
            ApplicationUser applicationUser = fetchApplicationUser(userPass.getUsername());
            dbPassword = getDecodedPassword(applicationUser.getPassword());
            locked = applicationUser.getLocked();
            initLoginDone = applicationUser.getInitLoginDone();
        }
        if (locked) {
            throw new AccountLockedException("Account has been locked. Please contact system administrator.");
        }

        if (!this.passwordEncoder.matches(userPass.getPassword(), dbPassword)){
            throw new FailedLoginException("Password does not match value on record.");
        }
        userPass.setPassword(dbPassword);

        if (!"Y".equals(initLoginDone)) {
            throw new AccountPasswordMustChangeException("User is trying to login with preset password");
        }
    }

}
