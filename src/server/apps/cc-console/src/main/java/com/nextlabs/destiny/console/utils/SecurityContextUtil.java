/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 23, 2016
 *
 */
package com.nextlabs.destiny.console.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.enums.UserCategory;
import com.nextlabs.destiny.console.model.ApplicationUser;

/**
 *
 * Access to application security context
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public final class SecurityContextUtil {

    private static final String USER_INFO_TEMPLATE = "[username=%s, displayName=%s, category=%s]";
    /**
     * Get current logged in user
     * 
     * @return {@link PrincipalUser}
     */
    public static PrincipalUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        PrincipalUser user = null;
        if (auth != null) {
            user = (PrincipalUser) auth.getPrincipal();
        } else {
            // Dummy user for initial data user
            List<GrantedAuthority> authorities = new ArrayList<>();
            user = new PrincipalUser(0L, "Administrator", "", "Administrator","Administrator", "******",
                    ApplicationUser.USER_TYPE_INTERNAL, UserCategory.ADMINISTRATOR.getCode(), false, true, authorities);
        }
        return user;
    }

    /**
     * Get current logged in user id
     * 
     * @return user id
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth != null) {
            PrincipalUser user = (PrincipalUser) auth.getPrincipal();
            return user.getUserId();
        }
        return -1L;
    }

    /**
     * Get current logged in user id
     * 
     * @return user id
     */
    public static Authentication getCurrentAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getUserInfo() {
        String userInfo = "";
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null) {
                Object user = authentication.getPrincipal();
                if (user != null) {
                    userInfo = user.toString();
                    if (user instanceof PrincipalUser) {
                        PrincipalUser principalUser = (PrincipalUser) user;
                        userInfo = String.format(USER_INFO_TEMPLATE,
                                principalUser.getUsername(),
                                principalUser.getDisplayName(),
                                principalUser.getCategory()
                        );
                    }
                }
            }
        }
        return userInfo;
    }

    public static String getUserInfo(ApplicationUser appUser) {
        if(appUser != null) {
            return String.format(USER_INFO_TEMPLATE,
                    appUser.getUsername(),
                    appUser.getDisplayName(),
                    appUser.getUserCategory());
        }

        return "";
    }

    public static String getUserInfo(Collection<ApplicationUser> userList) {
        if(userList != null && !userList.isEmpty()) {
            List<String> list = new ArrayList<>();

            for(ApplicationUser user : userList) {
                list.add(getUserInfo(user));
            }

            return String.join(", ", list);
        }

        return "";
    }
}
