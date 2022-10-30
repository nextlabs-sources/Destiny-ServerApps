package com.nextlabs.destiny.console.model.notification;

import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.NotificationType;
import com.nextlabs.destiny.console.model.BaseModel;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Entity
@Table(name = "NOTIFICATION")
public class Notification extends BaseModel implements Serializable {
    private static final long serialVersionUID = -8462408604493271503L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "metadata")
    private String metadata;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content")
    private String content;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private DevEntityType devEntityType;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public DevEntityType getDevEntityType() {
        return devEntityType;
    }

    public void setDevEntityType(DevEntityType devEntityType) {
        this.devEntityType = devEntityType;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Notification)) return false;
        Notification that = (Notification) obj;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                        .append(id)
                        .toHashCode();
    }
}
