package com.nextlabs.destiny.console.services;

import java.util.List;

/**
 *
 * Service to send emails
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
public interface EmailService {

    /**
     * Sends email to given list of users.
     *
     * @param recipients{@link
     *            List<String>}
     * @param subject{@link String}
     * @param body{@link String}
     */
    void sendEmail(List<String> recipients, String subject, String body);
}
