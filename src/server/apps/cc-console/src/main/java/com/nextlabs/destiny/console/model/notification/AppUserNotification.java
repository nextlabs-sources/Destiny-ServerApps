package com.nextlabs.destiny.console.model.notification;

import com.nextlabs.destiny.console.model.ApplicationUser;

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
@Table(name = "APPLICATION_USER_NOTIFICATION")
public class AppUserNotification implements Serializable {

    private static final long serialVersionUID = -94692582040459726L;

    @EmbeddedId
    private UserNotificationId id;

    @Column(name = "is_active")
    private boolean active;

    @ManyToOne
    @MapsId("notificationId")
    private Notification notification;

    @ManyToOne
    @MapsId("userId")
    private ApplicationUser user;

    @Column(name = "notified")
    private boolean notified;

    public AppUserNotification() {
    }

    public AppUserNotification(ApplicationUser user, Notification notification) {
        this.user = user;
        this.notification = notification;
        this.setId(new UserNotificationId(user.getId(), notification.getId()));
    }

    public UserNotificationId getId() {
        return id;
    }

    public void setId(UserNotificationId id) {
        this.id = id;
    }

    public boolean getActive() {
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

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser applicationUser) {
        this.user = applicationUser;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

}
