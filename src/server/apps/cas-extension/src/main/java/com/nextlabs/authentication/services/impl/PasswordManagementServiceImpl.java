package com.nextlabs.authentication.services.impl;

import org.apereo.cas.pm.InvalidPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.models.ApplicationUser;
import com.nextlabs.authentication.models.SuperApplicationUser;
import com.nextlabs.authentication.repositories.ApplicationUserRepository;
import com.nextlabs.authentication.repositories.SuperApplicationUserRepository;
import com.nextlabs.authentication.services.PasswordManagementService;

/**
 * Password management service implementation.
 *
 * @author Sachindra Dasun
 */
@Service
public class PasswordManagementServiceImpl implements PasswordManagementService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordManagementServiceImpl.class);

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private SuperApplicationUserRepository superApplicationUserRepository;

    @Autowired
    PasswordEncoder delegatingPasswordEncoder;

    @Override
    public String findEmail(String username) {
        return superApplicationUserRepository.findByUsernameIgnoreCase(username)
                .map(SuperApplicationUser::getEmail)
                .orElse(superApplicationUserRepository.findByEmail(username)
                        .map(SuperApplicationUser::getEmail)
                        .orElse(applicationUserRepository.findByUsernameIgnoreCaseAndStatus(username, UserStatus.ACTIVE)
                                .map(ApplicationUser::getEmail)
                                .orElse(applicationUserRepository.findByEmailAndStatus(username, UserStatus.ACTIVE)
                                        .map(ApplicationUser::getEmail)
                                        .orElse(null))
                        )
                );
    }

    @Override
    public String findUsername(String email) {
        return superApplicationUserRepository.findByEmail(email)
                .map(SuperApplicationUser::getUsername)
                .orElse(applicationUserRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                        .map(ApplicationUser::getUsername)
                        .orElse(null));
    }

    @Override
    public boolean updatePassword(String username, String password) {
        SuperApplicationUser superApplicationUser = superApplicationUserRepository.findByUsernameIgnoreCase(username)
                .orElse(null);
        if (superApplicationUser == null) {
            ApplicationUser applicationUser = applicationUserRepository.findByUsernameIgnoreCaseAndStatus(username,
                    UserStatus.ACTIVE).orElse(null);
            if (applicationUser != null) {
                if (delegatingPasswordEncoder.matches(password, new String(applicationUser.getPassword()))) {
                    throw new InvalidPasswordException(".reusepassword",
                            "The new password can not be same as the current password", null);
                }
                byte[] passwordDigest = delegatingPasswordEncoder.encode(password).getBytes();
                applicationUser.setPassword(passwordDigest);
                applicationUser.setInitLoginDone("Y");
                applicationUserRepository.save(applicationUser);
                return true;
            }
        } else {
            if (delegatingPasswordEncoder.matches(password, new String(superApplicationUser.getPassword()))) {
                throw new InvalidPasswordException(".reusepassword",
                        "The new password can not be same as the current password", null);
            }
            byte[] passwordDigest = delegatingPasswordEncoder.encode(password).getBytes();
            superApplicationUser.setPassword(passwordDigest);
            superApplicationUser.setInitLoginDone("Y");
            superApplicationUserRepository.save(superApplicationUser);
            return true;
        }
        return false;
    }

    // update password hash by re-encoding the password with current default algorithm
    @Override
    public boolean reHashPassword(String username, String password) {
        SuperApplicationUser superApplicationUser = superApplicationUserRepository.findByUsernameIgnoreCase(username)
                .orElse(null);
        if (superApplicationUser == null) {
            ApplicationUser applicationUser = applicationUserRepository.findByUsernameIgnoreCaseAndStatus(username,
                    UserStatus.ACTIVE).orElse(null);
            if (applicationUser != null) {

                byte[] passwordDigest = delegatingPasswordEncoder.encode(password).getBytes();
                applicationUser.setPassword(passwordDigest);
                applicationUserRepository.save(applicationUser);
                return true;
            }
        } else {
            byte[] passwordDigest = delegatingPasswordEncoder.encode(password).getBytes();
            superApplicationUser.setPassword(passwordDigest);
            superApplicationUserRepository.save(superApplicationUser);
            return true;
        }
        logger.error("password rehash failed for user: {}", username);
        return false;
    }


}
