package com.nextlabs.destiny.configservice.services;

import java.util.Set;

/**
 * Message service to send JMS messages.
 *
 * @author Sachindra Dasun
 */
public interface MessageService {

    void sendConfigRefresh(Set<String> applications);

    void sendLoggerRefresh(Set<String> applications);

    void sendSecureStoreRefresh(Set<String> applications);
}
