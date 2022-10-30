package com.nextlabs.destiny.console.dto.notification;

import com.nextlabs.destiny.console.model.notification.AppUserNotification;
import com.nextlabs.destiny.console.model.notification.Notification;
import com.nextlabs.destiny.console.model.notification.SuperAppUserNotification;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;
import java.util.Objects;


/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Document(indexName = "notification")
@Setting(settingPath = "/search_config/index-settings.json")
public class NotificationLite implements Serializable {

    private static final long serialVersionUID = 5318235110127133765L;

    @ApiModelProperty(value = "An ID that uniquely identifies the NotificationLite. The value is the hash of notificationId and addressedUserId fields.",
            example = "87", position = 10)
    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @ApiModelProperty(value = "The ID of the notification.", example = "87", position = 10)
    @Field(type = FieldType.Long, store = true)
    private Long notificationId;

    @ApiModelProperty("The ID of the user the notification is send to.")
    @Field
    private Long addressedUserId;

    @ApiModelProperty("The content field of the notification. Mainly used for email notification.")
    @Field
    private String content;

    @ApiModelProperty("The subject field of the notification. Mainly used for email notification.")
    @Field
    private String subject;

    @ApiModelProperty("Used to hold additional info regarding the notification. Mainly used for console notification.")
    @Field
    private String metadata;

    @ApiModelProperty("The type of the entity, for example policy.")
    @Field
    private String entityType;

    @ApiModelProperty("The ID of the entity.")
    @Field
    private long entityId;

    @ApiModelProperty("The ID of the entity, for example policy.")
    @Field
    private String notificationType;

    @ApiModelProperty("Indicates whether the notification was opened by user.")
    @Field
    private boolean notified;

    @ApiModelProperty(
            value = "Indicates the date at which this notification was last modified. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC(coordinated universal time). For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private long lastUpdatedDate;

    @ApiModelProperty(
            value = "Indicates the date at which this notification was created. The value is measured in milliseconds, between the current time and midnight, January 1, 1970 UTC(coordinated universal time). For example, November 15, 2019 10:48:49.296 AM will be written as 1573786129296.",
            example = "1573786129296")
    @MultiField(mainField = @Field(type = FieldType.Date, store = true), otherFields = {
            @InnerField(suffix = "untouched", type = FieldType.Date, store = true)})
    private long createdDate;

    @ApiModelProperty(value = "ID of the user who created the notification.", example = "0")
    @Field
    private long ownerId;

    @ApiModelProperty(value = "Display name of the user who created the notification.", example = "user1")
    @Field
    private String ownerDisplayName;

    @ApiModelProperty(value = "ID of the user who last modified the notification.", example = "0")
    private long modifiedById;

    @ApiModelProperty(value = "Display name of the user who last modified the notification.", example = "user1")
    private String modifiedBy;

    public Long getAddressedUserId() {
        return addressedUserId;
    }

    public void setAddressedUserId(Long addressedUserId) {
        this.addressedUserId = addressedUserId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public boolean getNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    public long getModifiedById() {
        return modifiedById;
    }

    public void setModifiedById(long modifiedById) {
        this.modifiedById = modifiedById;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public static NotificationLite getLite(AppUserNotification userNotification, ApplicationUserSearchRepository applicationUserSearchRepository) {
        Notification notification = userNotification.getNotification();
        NotificationLite notificationLite = new NotificationLite();
        notificationLite.setId((long) Objects.hash(userNotification.getUser().getId(), notification.getId()));
        notificationLite.setNotificationId(notification.getId());
        notificationLite.setContent(notification.getContent());
        notificationLite.setSubject(notification.getSubject());
        notificationLite.setMetadata(notification.getMetadata());
        notificationLite.setEntityType(notification.getDevEntityType().name());
        notificationLite.setEntityId(notification.getEntityId());
        notificationLite.setNotificationType(notification.getNotificationType().name());
        notificationLite.setNotified(userNotification.isNotified());
        notificationLite.setAddressedUserId(userNotification.getUser().getId());
        notificationLite.setOwnerId(notification.getOwnerId());
        notificationLite.setCreatedDate(notification.getCreatedDate().getTime());
        notificationLite.setModifiedById(notification.getLastUpdatedBy());
        notificationLite.setLastUpdatedDate(notification.getLastUpdatedDate().getTime());
        applicationUserSearchRepository.findById(notificationLite.getOwnerId())
                .ifPresent(owner -> notificationLite.setOwnerDisplayName(owner.getDisplayName()));
        applicationUserSearchRepository.findById(notificationLite.getModifiedById())
                .ifPresent(modifiedBy -> notificationLite.setModifiedBy(modifiedBy.getDisplayName()));
        return  notificationLite;
    }

    public static NotificationLite getLite(SuperAppUserNotification userNotification, ApplicationUserSearchRepository applicationUserSearchRepository) {
        Notification notification = userNotification.getNotification();
        NotificationLite notificationLite = new NotificationLite();
        notificationLite.setId((long) Objects.hash(userNotification.getUser().getId(), notification.getId()));
        notificationLite.setNotificationId(notification.getId());
        notificationLite.setContent(notification.getContent());
        notificationLite.setSubject(notification.getSubject());
        notificationLite.setMetadata(notification.getMetadata());
        notificationLite.setEntityType(notification.getDevEntityType().name());
        notificationLite.setEntityId(notification.getEntityId());
        notificationLite.setNotificationType(notification.getNotificationType().name());
        notificationLite.setNotified(userNotification.isNotified());
        notificationLite.setAddressedUserId(userNotification.getUser().getId());
        notificationLite.setOwnerId(notification.getOwnerId());
        notificationLite.setCreatedDate(notification.getCreatedDate().getTime());
        notificationLite.setModifiedById(notification.getLastUpdatedBy());
        notificationLite.setLastUpdatedDate(notification.getLastUpdatedDate().getTime());
        applicationUserSearchRepository.findById(notificationLite.getOwnerId())
                .ifPresent(owner -> notificationLite.setOwnerDisplayName(owner.getDisplayName()));
        applicationUserSearchRepository.findById(notificationLite.getModifiedById())
                .ifPresent(modifiedBy -> notificationLite.setModifiedBy(modifiedBy.getDisplayName()));
        return  notificationLite;
    }

}
