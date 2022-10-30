package com.nextlabs.authentication.controllers;

import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.authentication.handlers.authentication.InternalUserAuthenticationHandler;
import com.nextlabs.authentication.services.PasswordManagementService;

/**
 * This controller allows to modify user account details.
 *
 * @author Sachindra Dasun
 */
@RestController
@RequestMapping("account")
public class UserAccountController {

    private static final Logger logger = LoggerFactory.getLogger(UserAccountController.class);

    private InternalUserAuthenticationHandler internalUserAuthenticationHandler;

    private PasswordManagementService passwordManagementService;

    @PostMapping("modifyPassword")
    public ResponseEntity<String> modifyPassword(@RequestParam String username,
                                                 @RequestParam String password,
                                                 @RequestParam String newPassword) {
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(newPassword)) {
            UsernamePasswordCredential credential = new UsernamePasswordCredential();
            credential.setUsername(username);
            credential.setPassword(password);
            try {
                internalUserAuthenticationHandler.validateUser(credential);
            } catch (AccountPasswordMustChangeException e) {
                logger.debug("Account password must be changed");
            } catch (GeneralSecurityException e) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            passwordManagementService.updatePassword(username, newPassword);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Autowired
    public void setInternalUserAuthenticationHandler(InternalUserAuthenticationHandler internalUserAuthenticationHandler) {
        this.internalUserAuthenticationHandler = internalUserAuthenticationHandler;
    }

    @Autowired
    public void setPasswordManagementService(PasswordManagementService passwordManagementService) {
        this.passwordManagementService = passwordManagementService;
    }
}
