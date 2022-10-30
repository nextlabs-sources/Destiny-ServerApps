package com.nextlabs.authentication.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Entity for provisioned user group of different authentication handler.
 *
 * @author Chok Shah Neng
 */
@Entity
@Table(name = "PROVISIONED_USER_GROUP")
public class ProvisionedUserGroup implements Serializable {

    private static final long serialVersionUID = 7571152667505278566L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false)
    private long id;

    @Column(name = "AUTH_HANDLER_ID")
    private long authHandlerId;

    @Column(name = "GROUP_ID")
    private String groupId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuthHandlerId() {
        return authHandlerId;
    }

    public void setAuthHandlerId(long authHandlerId) {
        this.authHandlerId = authHandlerId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
