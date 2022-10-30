package com.nextlabs.destiny.console.services.notification.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Console Notification Service
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Service
public class ConsoleNotificationServiceImpl implements ConsoleNotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ConsoleNotificationServiceImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void notifyUser(String username, Object data) {
        simpMessagingTemplate.convertAndSendToUser(username, "/secured/user/queue/notification", data);
    }
}
