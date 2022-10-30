package com.nextlabs.authentication.services;

/**
 * Password management service interface.
 *
 * @author Sachindra Dasun
 */
public interface PasswordManagementService {

    String findEmail(String username);

    String findUsername(String email);

    boolean updatePassword(String username, String password);

    boolean reHashPassword(String username, String password);

}
