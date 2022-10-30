/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.model.dictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "DICT_ENUM_GROUP_MEMBERS")
public class EnumGroupMember {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Version
	private int version;

	@Column(name = "from_id")
	private long fromId;

	@Column(name = "to_id")
	private long toId;

	@Column(name = "enrollment_id")
	private Long enrollmentId;

	@Column(name = "is_direct")
	private char isDirect;

	@Column(name = "active_from")
	private long activeFrom;

	@Column(name = "active_to")
	private long activeTo;

	public EnumGroupMember(long fromId, long toId, Long enrollmentId, boolean isDirect, long activeFrom,
			long activeTo) {
		super();
		this.fromId = fromId;
		this.toId = toId;
		this.enrollmentId = enrollmentId;
		this.isDirect = isDirect? 'Y' : 'N';
		this.activeFrom = activeFrom;
		this.activeTo = activeTo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getFromId() {
		return fromId;
	}

	public void setFromId(long fromId) {
		this.fromId = fromId;
	}

	public long getToId() {
		return toId;
	}

	public void setToId(long toId) {
		this.toId = toId;
	}

	public Long getEnrollmentId() {
		return enrollmentId;
	}

	public void setEnrollmentId(Long enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	public char isDirect() {
		return isDirect;
	}

	public void setDirect(char isDirect) {
		this.isDirect = isDirect;
	}

	public long getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(long activeFrom) {
		this.activeFrom = activeFrom;
	}

	public long getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(long activeTo) {
		this.activeTo = activeTo;
	}

}
