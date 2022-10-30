package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.model.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
