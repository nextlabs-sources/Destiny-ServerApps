package com.nextlabs.authentication.services;

/**
 * CSRF protection service create and return CSRF token.
 *
 * @author Sachindra Dasun
 */
public interface CsrfProtectionService {

    String getCsrfToken();

}
