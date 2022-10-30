package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.model.notification.SuperAppUserNotification;
import com.nextlabs.destiny.console.model.notification.UserNotificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Repository
public interface SuperAppUserNotificationRepository extends JpaRepository<SuperAppUserNotification, UserNotificationId> {
    Optional<List<SuperAppUserNotification>> findByActiveIsTrue();
}
