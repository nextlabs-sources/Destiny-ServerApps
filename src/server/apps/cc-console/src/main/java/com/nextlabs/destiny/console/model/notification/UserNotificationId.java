package com.nextlabs.destiny.console.model.notification;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Embeddable
public class UserNotificationId implements Serializable {
    private static final long serialVersionUID = 1435948964693051858L;

    private Long userId;

    private Long notificationId;

    public UserNotificationId() {
    }

    public UserNotificationId(Long userId, Long notificationId) {
        this.userId = userId;
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getNotificationId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserNotificationId)) return false;
        UserNotificationId that = (UserNotificationId) obj;
        return Objects.equals(getUserId(), that.getNotificationId()) &&
                Objects.equals(getNotificationId(), that.getNotificationId());
    }

    @Override
    public String toString() {
        return String.format("user id: %s, notificatin id: %s", userId, notificationId);
    }
}
