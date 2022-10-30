package com.nextlabs.authentication.handlers;

import java.io.Serializable;

import com.nextlabs.authentication.enums.LogMarker;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.configuration.model.support.pm.PasswordManagementProperties;
import org.apereo.cas.pm.BasePasswordManagementService;
import org.apereo.cas.pm.PasswordChangeRequest;
import org.apereo.cas.pm.PasswordHistoryService;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextlabs.authentication.services.PasswordManagementService;

/**
 * Provide password management functions.
 *
 * @author Sachindra Dasun
 */
public class PasswordManagementHandler extends BasePasswordManagementService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordManagementHandler.class);

    @Autowired
    private PasswordManagementService passwordManagementService;

    public PasswordManagementHandler(PasswordManagementProperties properties,
                                     CipherExecutor<Serializable, String> cipherExecutor, String issuer,
                                     PasswordHistoryService passwordHistoryService) {
        super(properties, cipherExecutor, issuer, passwordHistoryService);
    }

    @Override
    public boolean changeInternal(Credential credential, PasswordChangeRequest bean) {
        boolean success = false;
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential userCredential = (UsernamePasswordCredential) credential;
            success = passwordManagementService.updatePassword(userCredential.getUsername(),
                    bean.getPassword());

            if(success) {
                logger.info(LogMarker.AUTHENTICATION, "User account password changed. [username={}]", userCredential.getUsername());
            } else {
                logger.error(LogMarker.AUTHENTICATION, "User change password failed. [username={}]", userCredential.getUsername());
            }
        }

        return success;
    }

    @Override
    public String findEmail(String username) {
        return passwordManagementService.findEmail(username);
    }

    @Override
    public String findUsername(String email) {
        return passwordManagementService.findUsername(email);
    }

}
