/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 22, 2016
 *
 */
package com.nextlabs.destiny.console.config.root;

import java.util.Collection;

import com.nextlabs.destiny.console.enums.UserCategory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 *
 * Current logged in user's detail
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class PrincipalUser extends User {

    private static final long serialVersionUID = -6958343969074578724L;

    private Long userId;
    private String firstName;
    private String lastName;
    private String displayName;
    private String type;
    private String category;
    private boolean hideSplash = false;
    private boolean superUser = false;

    /**
     * Calls the more complex constructor with all boolean arguments set to
     * {@code true}.
     */
    public PrincipalUser(Long userId, String firstName, String lastName, String displayName,
            String username, String password, String type, String category, boolean superUser,
            boolean hideSplash,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        if(StringUtils.isBlank(this.displayName))
            this.displayName = String.format("%s %s", firstName, lastName);
        if(StringUtils.isBlank(this.displayName))
            this.displayName = username;
        this.superUser = superUser;
        this.hideSplash = hideSplash;
        this.type = type;
        this.category = category;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSuperUser() {
        return superUser || UserCategory.ADMINISTRATOR.getCode().equals(this.category);
    }

    public void setSuperUser(boolean superUser) {
        this.superUser = superUser;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isHideSplash() {
        return hideSplash;
    }

    public void setHideSplash(boolean hideSplash) {
        this.hideSplash = hideSplash;
    }

    @Override
    public String toString() {
        return String.format("PrincipalUser [userId=%s, displayName=%s]",
                userId, displayName);
    }

}
