package com.nextlabs.destiny.console.model.notification;

import com.nextlabs.destiny.console.model.SuperApplicationUser;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Entity
@Table(name = "SUPER_APPLICATION_USER_NOTIFICATION")
public class SuperAppUserNotification implements Serializable {

    private static final long serialVersionUID = -5604544806841708944L;

    @EmbeddedId
    private UserNotificationId id;

    @Column(name = "is_active")
    private boolean active;

    @ManyToOne
    @MapsId("notificationId")
    private Notification notification;

    @ManyToOne
    @MapsId("userId")
    private SuperApplicationUser user;

    @Column(name = "notified")
    private boolean notified;

    public SuperAppUserNotification() {
    }

    public SuperAppUserNotification(SuperApplicationUser user, Notification notification) {
        this.user = user;
        this.notification = notification;
        this.id = new UserNotificationId(user.getId(), notification.getId());
    }

    public UserNotificationId getId() {
        return id;
    }

    public void setId(UserNotificationId id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public SuperApplicationUser getUser() {
        return user;
    }

    public void setUser(SuperApplicationUser user) {
        this.user = user;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }
}
