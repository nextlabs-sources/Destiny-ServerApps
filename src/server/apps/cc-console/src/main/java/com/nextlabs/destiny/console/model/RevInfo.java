package com.nextlabs.destiny.console.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import com.nextlabs.destiny.console.config.listeners.RevInfoListener;

/**
 * Entity for revision information.
 *
 * @author Sachindra Dasun
 */
@Entity
@RevisionEntity(RevInfoListener.class)
@Table(name = "REV_INFO")
public class RevInfo extends DefaultRevisionEntity {

    @Column(name = "USER_ID")
    private long userId;

    @Column(name = "SUPER_USER")
    private boolean superUser;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isSuperUser() {
        return superUser;
    }

    public void setSuperUser(boolean superUser) {
        this.superUser = superUser;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
