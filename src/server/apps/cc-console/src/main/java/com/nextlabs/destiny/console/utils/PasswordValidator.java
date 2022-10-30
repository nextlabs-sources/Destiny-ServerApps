/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 28, 2016
 *
 */
package com.nextlabs.destiny.console.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates the user entered password
 *
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class PasswordValidator {

    private Pattern pattern;
    private Matcher matcher;

    private static final String PASSWORD_PATTERN = "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})[A-Za-z0-9!~`<>,;:_=?*+#.'\\\\\"&%()\\|\\[\\]\\{\\}\\-\\$\\^\\@\\/]{10,128}";

    public PasswordValidator() {
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    public boolean validatePassword(String password) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
