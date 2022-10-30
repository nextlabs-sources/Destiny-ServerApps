/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.services.notification;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.notification.NotificationLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.notification.AppUserNotification;
import com.nextlabs.destiny.console.model.notification.SuperAppUserNotification;
import org.springframework.data.domain.Page;

/**
 *
 * Notification Search Service interface
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
public interface NotificationSearchService {

    /**
     * Find notifications
     *
     * @throws ConsoleException
     */
    Page<NotificationLite> findNotificationsByCriteria(SearchCriteria criteria) throws ConsoleException;
    /**
     * Re-Index all the notifications
     *
     * @throws ConsoleException
     */
    void reIndexAllNotifications() throws ConsoleException;

    /**
     * @param notification
     * @throws ConsoleException 
     */
    void reIndexNotification(AppUserNotification notification) throws ConsoleException;

    /**
     * @param notification
     * @throws ConsoleException
     */
    void reIndexNotification(SuperAppUserNotification notification) throws ConsoleException;
}
