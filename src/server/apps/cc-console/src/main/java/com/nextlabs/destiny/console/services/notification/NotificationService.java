package com.nextlabs.destiny.console.services.notification;

import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.notification.Notification;

import java.util.List;

/**
 *
 * Service to manage user notification
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
public interface NotificationService {

    /**
     * Sends notification to given list of users.
     *
     * @param recipients{@link
     *            List<ApplicationUser>}
     * @param notification{@link Notification}
     */
    void saveAndNotifyUser(List<ApplicationUser> recipients, Notification notification) throws ConsoleException;

    /**
     * Marks a notification as ready for the current logged in user.
     *
     * @param notificationId
     */
    void markAsRead(Long notificationId) throws ConsoleException;
}
