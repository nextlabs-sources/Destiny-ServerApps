/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.nextlabs.destiny.console.enums.AuditAction;

/**
 *
 * Audit Log for entity's Create, Update, Delete operations
 *
 * @since 8.6
 *
 */
@Entity
@Table(name = "ENTITY_AUDIT_LOG")
public class EntityAuditLog {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "TIMESTAMP", nullable = false)
    private Long timestamp;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION", nullable = false, length = 10)
    private AuditAction action;
    
    @Column(name = "ACTOR_ID", nullable = false)
    private Long actorId;
    
    @Column(name = "ACTOR", nullable = false, length = 130)
    private String actor;
    
    @Column(name = "ENTITY_TYPE", nullable = false)
    private String entityType;
    
    @Column(name = "ENTITY_ID", nullable = false)
    private Long entityId;
    
    @Column(name = "OLD_VALUE")
    private String oldValue;
    
    @Column(name = "NEW_VALUE")
    private String newValue;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public AuditAction getAction() {
		return action;
	}

	public void setAction(AuditAction action) {
		this.action = action;
	}

	public Long getActorId() {
		return actorId;
	}

	public void setActorId(Long actorId) {
		this.actorId = actorId;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
